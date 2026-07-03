package mensajes;
// A
class Request {
    String fecha; // Fecha del viaje provista por el cliente
    Channel cRespuesta; // Canal donde el cliente espera el booleano final
}

global Channel cVuelo = new Channel();
global Channel cHotel = new Channel();
global Channel cAuto = new Channel();
global Channel cAgencia = new Channel();

process Agencia {
    while (true) {
        // Recibe la tupla del cliente de forma indirecta en el canal global
        Request req = cAgencia.receive(); // Se bloquea hasta que caiga un pedido

        // PATRÓN B: Despacha un thread de inmediato para maximizar la concurrencia
        thread (req) { 
            // Canal local intermedio para recolectar los 3 resultados parciales
            Channel cPack = new Channel(); 

            thread (req, cPack) {
                Channel cVueloRta = new Channel(); // Canal de retorno exclusivo para el vuelo
                cVuelo.send(req.fecha, cVueloRta); // Le pregunta de forma asincrónica al proveedor
                cPack.send(cVueloRta.receive());   // Espera la respuesta y la mete en el cPack
            }

            thread (req, cPack) {
                Channel cHotelRta = new Channel();
                cHotel.send(req.fecha, cHotelRta);
                cPack.send(cHotelRta.receive()); 
            }

            thread (req, cPack) {
                Channel cAutoRta = new Channel(); 
                cAuto.send(req.fecha, cAutoRta); 
                cPack.send(cAutoRta.receive());
            }

            bool hayPack = true; // BARRERA DE SINCRONIZACIÓN: Junta los 3 resultados parciales
            repeat (3) {
                hayPack = hayPack && cPack.receive(); // Bloqueante por cada respuesta que va llegando
            }

            req.cRespuesta.send(hayPack); 
        }
    }
}

// B
process Agencia {
    while (true) {
        Request req = cAgencia.receive(); 

        // ALTA CONCURRENCIA: Lanzamos un hilo independiente POR CADA CLIENTE.
        // Esto permite que 100 clientes hagan consultas al mismo tiempo sin encolarse.
        thread (req) { 
            Channel cPack = new Channel(); // Canal de recolección local para ESTE cliente

            thread (req, cPack) {
                Channel cVueloRta = new Channel();
                cVuelo.send(req.fecha, cVueloRta);
                cPack.send(cVueloRta.receive());
            }

            thread (req, cPack) {
                Channel cHotelRta = new Channel();
                cHotel.send(req.fecha, cHotelRta);
                cPack.send(cHotelRta.receive());
            }

            thread (req, cPack) {
                Channel cAutoRta = new Channel();
                cAuto.send(req.fecha, cAutoRta);
                cPack.send(cAutoRta.receive());
            }

            bool hayPack = true;
            repeat (3) {
                hayPack = hayPack && cPack.receive();
            }

            // Le responde de forma privada al cliente correspondiente
            req.cRespuesta.send(hayPack); 
        } 
    }
}

// C
global Channel[] cVuelo;
global Channel[] cHotel;
global Channel[] cAuto;
global Channel cAgencia = new Channel();

process Agencia {
    while (true) {
        Request req = cAgencia.receive(); 

        thread (req, cVuelo, cHotel, cAuto) { 
            Channel cPack = new Channel();

            thread (req, cPack, cVuelo) {
                Channel cVueloRta = new Channel();
                
                // Envía la consulta a TODO el arreglo de proveedores de vuelos
                for (int i = 0; i < cVuelo.length; i++) {
                    cVuelo[i].send(req.fecha, cVueloRta);
                }

                bool hayVuelo = false;
                // Recolecta exactamente la cantidad de respuestas esperadas
                repeat (cVuelo.length) {
                    // Operador OR: con que un proveedor tenga lugar, ya nos sirve
                    hayVuelo = hayVuelo || cVueloRta.receive();
                }
                cPack.send(hayVuelo);
            }

            thread (req, cPack, cHotel) {
                Channel cHotelRta = new Channel();
                
                for (int i = 0; i < cHotel.length; i++) {
                    cHotel[i].send(req.fecha, cHotelRta);
                }

                bool hayHotel = false;
                repeat (cHotel.length) {
                    hayHotel = hayHotel || cHotelRta.receive();
                }
                cPack.send(hayHotel);
            }

            thread (req, cPack, cAuto) {
                Channel cAutoRta = new Channel();
                
                for (int i = 0; i < cAuto.length; i++) {
                    cAuto[i].send(req.fecha, cAutoRta);
                }

                bool hayAuto = false;
                repeat (cAuto.length) {
                    hayAuto = hayAuto || cAutoRta.receive();
                }
                cPack.send(hayAuto);
            }

            bool hayPack = true;
            repeat (3) {
                hayPack = hayPack && cPack.receive();
            }

            req.cRespuesta.send(hayPack);
        }
    }
}
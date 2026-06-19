// e2.a
global Channel cVuelo = new Channel(); // Canal del proveedor de vuelos
global Channel cHotel = new Channel(); // Canal del proveedor de hoteles
global Channel cAuto = new Channel(); // Canal del proveedor de autos
global Channel cAgencia = new Channel(); // Canal donde la agencia recibe pedidos

process Agencia : {
    while (true) { // La agencia atiende clientes de forma secuencial: procesa un pedido completo antes de atender el siguiente
        Request req = cAgencia.receive();  // Esperamos bloqueados a que llegue un cliente. Cada cliente es atendido por un hilo independiente, lo que permite atender múltiples clientes en paralelo
        
        Channel cPack = new Channel(); // Canal para recolectar las 3 respuestas de los rubros

        thread (req, cPack) : {  // Hilo exclusivo para consultar vuelos en paralelo a los demás rubros
            Channel cVueloRta = new Channel(); // Canal para que el proveedor nos responda a nosotros
            cVuelo.send(req.fecha, cVueloRta); // Enviamos fecha y nuestro canal de respuesta
            bool hayVuelo = cVueloRta.receive(); // Esperamos bloqueados la respuesta de este rubro
            cPack.send(hayVuelo); // Enviamos el resultado en el canal unificador del paquete
        }

        thread (req, cPack) : {
            Channel cHotelRta = new Channel();
            cHotel.send(req.fecha, cHotelRta);
            bool hayHotel = cHotelRta.receive();
            cPack.send(hayHotel);
        }

        thread (req, cPack) : {
            Channel cAutoRta = new Channel();
            cAuto.send(req.fecha, cAutoRta);
            bool hayAuto = cAutoRta.receive();
            cPack.send(hayAuto);
        }

        bool hayPack = true; // Iniciamos en true para acumular con AND: si alguno da false, el paquete completo falla
        repeat (3) {
            // Nos bloqueamos a recibir las 3 respuestas
            hayPack = hayPack && cPack.receive();
        }

        req.cRespuesta.send(hayPack); // Le respondemos al cliente usando el canal exclusivo que él nos pasó en la tupla
    }
}

// e2.b
global Channel cVuelo = new Channel();
global Channel cHotel = new Channel();
global Channel cAuto = new Channel();
global Channel cAgencia = new Channel();

process Agencia : {
    while (true) { 
        Request req = cAgencia.receive();  // Esperamos bloqueados a que llegue un cliente. Cada cliente es atendido por un hilo independiente, lo que permite atender múltiples clientes en paralelo

        thread (req) {  // [MODIFICACIÓN]: Lanzamos un hilo principal por cada cliente para atenderlos en paralelo, en lugar de procesarlos secuencialmente. Esto permite que mientras un cliente espera respuestas de los proveedores, otros clientes puedan ser atendidos.
            Channel cPack = new Channel();

            thread (req, cPack) : {
                Channel cVueloRta = new Channel();
                cVuelo.send(req.fecha, cVueloRta);
                cPack.send(cVueloRta.receive());
            }

            thread (req, cPack) : {
                Channel cHotelRta = new Channel();
                cHotel.send(req.fecha, cHotelRta);
                cPack.send(cHotelRta.receive());
            }

            thread (req, cPack) : {
                Channel cAutoRta = new Channel();
                cAuto.send(req.fecha, cAutoRta);
                cPack.send(cAutoRta.receive());
            }

            bool hayPack = true;
            repeat (3) {
                hayPack = hayPack && cPack.receive();
            }

            req.cRespuesta.send(hayPack); // Cada hilo le responde a su propio cliente de forma independiente, sin bloquear a otros clientes que puedan estar siendo atendidos en paralelo.
        }
    }
}

// e2.c
global List<Channel> cVuelo = new List<Channel>(); // [MODIFICACIÓN]: Arreglo de proveedores de vuelos
global List<Channel> cHotel = new List<Channel>(); // [MODIFICACIÓN]: Arreglo de proveedores de hoteles
global List<Channel> cAuto = new List<Channel>();  // [MODIFICACIÓN]: Arreglo de proveedores de autos
global Channel cAgencia = new Channel();

process Agencia : {
    while (true) {
        Request req = cAgencia.receive(); 

        thread (req) {
            Channel cPack = new Channel();

            thread (req, cPack) : { // [MODIFICACIÓN]: Sub-hilo adaptado para recorrer el arreglo de vuelos
                Channel cVueloRta = new Channel();
                for (Channel v : cVuelo) { // For asincrónico: le tiramos la consulta a todos los proveedores del listado
                    v.send(req.fecha, cVueloRta);
                }

                bool hayVuelo = false; // Flag para validar si al menos uno responde que sí
                
                repeat (cVuelo.size()) { // Limpiamos el canal recibiendo exactamente la misma cantidad de respuestas que mensajes enviados
                    hayVuelo = hayVuelo || cVueloRta.receive(); // Si un proveedor da true, se clava en true
                }
                cPack.send(hayVuelo);
            }

            thread (req, cPack) : {
                Channel cHotelRta = new Channel();
                for (Channel h : cHotel) {
                    h.send(req.fecha, cHotelRta);
                }
                bool hayHotel = false;
                repeat (cHotel.size()) {
                    hayHotel = hayHotel || cHotelRta.receive();
                }
                cPack.send(hayHotel);
            }

            thread (req, cPack) : {
                Channel cAutoRta = new Channel();
                for (Channel a : cAuto) {
                    a.send(req.fecha, cAutoRta);
                }
                bool hayAuto = false;
                repeat (cAuto.size()) {
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
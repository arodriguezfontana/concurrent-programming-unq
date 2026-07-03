package mensajes;
// A
class Request {
    Channel cRespuesta; // Canal donde la persona espera la crónica
}

Channel cServer = new Channel(); // Canal global e indirecto del servidor 

process ServidorNovedades { 
    String cronica = ""; // Estado interno protegido 

    while (true) {
        case (cServer.receive()) { // Bloqueante: espera novedades o pedidos
            ("Novedad", String novedad) -> { 
                cronica = cronica + " " + novedad; // Modificación local segura (secuencial)
            };
            ("Pedido", Request req) -> { 
                req.cRespuesta.send(cronica); // Atiende de a uno: envía la crónica actual
            };  
        }
    }
}

// B
class Request {
    Channel cRespuesta; // Canal de contacto inicial
}

Channel cServer = new Channel(); // Canal público del servidor

process ServidorNovedades {
    String cronica = ""; // Estado interno principal

    while (true) {
        case (cServer.receive()) { 
            ("Novedad", String novedad) -> { 
                cronica = cronica + " " + novedad; // Secuencial: evita que hilos pisen el string
            };
            ("Pedido", Request req) -> { 
                thread (cronica, req) { // Concurrente: copia las variables al nuevo hilo
                    Channel cChatBot = new Channel(); // Canal privado y efímero para ESTE chat 
                    req.cRespuesta.send(cChatBot); // Le da el canal privado al cliente 

                    boolean seguir = true; 
                    while (seguir) {
                        String pregunta = cChatBot.receive(); // Espera pregunta del cliente en el canal privado 

                        if (pregunta.equals(" ")) { 
                            seguir = false; // Cadena vacía corta el chat 
                        } else {
                            String respuesta = generarRespuesta(cronica, pregunta); // Genera respuesta con su copia de crónica 
                            cChatBot.send(respuesta); // Envía respuesta al cliente 
                        }
                    }
                } 
            };
        }
    }
}

process Periodista {
    while (true) {
        String novedad = redactarNovedad(); 
        cServer.send(("Novedad", novedad)); // Envía reporte de forma asrincrónica 
    }
}

process Persona {
    Channel cRespuesta = new Channel(); // Canal para recibir la conexión del bot 
    
    Request req = new Request(); 
    req.cRespuesta = cRespuesta; // Asigna canal de respuesta 
    
    cServer.send(("Pedido", req)); // Pide iniciar un chat 
    Channel cMiChat = cRespuesta.receive(); // Se bloquea hasta que el bot le de su canal privado 

    boolean seguirChateando = true;
    while (seguirChateando) {
        String miPregunta = escribirPreguntaUsuario(); 
        cMiChat.send(miPregunta); // Habla directo con su bot asignado 
        
        if (miPregunta.equals(" ")) {
            seguirChateando = false; // Corta su bucle local 
        } else {
            String respuestaBot = cMiChat.receive(); // Espera la respuesta del bot 
            print(respuestaBot);
        }
    }
}

// C
class Request {
    Channel cRespuesta; 
}

Channel cServer = new Channel(); 

process ServidorNovedades {
    String cronica = "";
    List<Channel> suscriptores = new List<Channel>(); // Lista de casillas de correo de suscriptores 

    while (true) {
        case (cServer.receive()) { 
            ("Novedad", String novedad) -> { 
                cronica = cronica + " " + novedad; // Registra el gol en la crónica local
                
                thread (suscriptores, novedad) { // Copia serializable de la lista: no se bloquea
                    for (Channel s : suscriptores) {
                        s.send(novedad); // Reparte el gol en paralelo de fondo 
                    }
                }
            };
            ("Pedido", Request req) -> { 
                thread (cronica, req) { // Se mantiene la lógica de chatbots del punto B 
                    Channel cChatBot = new Channel();
                    req.cRespuesta.send(cChatBot); 

                    boolean seguir = true; 
                    while (seguir) {
                        String pregunta = cChatBot.receive(); 

                        if (pregunta.equals(" ")) { 
                            seguir = false;
                        } else {
                            String respuesta = generarRespuesta(cronica, pregunta);
                            cChatBot.send(respuesta); 
                        }
                    }
                } 
            };
            ("Suscripcion", Channel cSuscriptor) -> { 
                suscriptores.add(cSuscriptor); // Agrega el canal del cliente a la lista de forma segura
            };
        }
    }
}

process Periodista {
    while (true) {
        String novedad = redactarNovedad(); 
        cServer.send(("Novedad", novedad)); 
    }
}

process PersonaSuscripta { 
    Channel miCanalAlertas = new Channel(); // Canal exclusivo para recibir notificaciones en vivo 
    
    cServer.send(("Suscripcion", miCanalAlertas)); // Se suscribe enviando su canal
    
    while (true) { 
        String alerta = miCanalAlertas.receive(); // Se destraba cada vez que el thread reparte un gol
        print(alerta);
    }
}
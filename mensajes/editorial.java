package mensajes;
process Servidor {
    while (true) {
        // Escucha en el canal global la intención de un cliente para iniciar una compra
        Request req = chEditorial.receive(); 

        // Concurrencia Total: Delegamos la sesión interactiva completa en un hilo independiente
        thread (req, chDepo) { 
            // 1. Canal efímero y privado para los comandos de ESTA sesión de chat/compra
            Channel cSesionPrivada = new Channel(); 
            
            // 2. Le envía el canal privado al cliente por la vía de callback declarada en el Request
            req.cRespuesta.send(cSesionPrivada); 

            // 3. Inicializa el carrito local aislado para este cliente en particular
            List<String> carrito = new List<String>(); 

            boolean sesionActiva = true;
            while (sesionActiva) {
                // Se bloquea esperando el siguiente comando del cliente en la línea privada
                case (cSesionPrivada.receive()) {

                    // COMANDO 1: Agregar un libro al carrito
                    ("Agregar", String titulo) -> {
                        carrito.add(titulo); // Se añade de forma segura en la lista local
                        cSesionPrivada.send("Libro '" + titulo + "' agregado exitosamente."); // Confirma con un String
                    };

                    // COMANDO 2: Confirmar y concretar la compra del carrito actual
                    ("Comprar") -> {
                        Channel cDepoRta = new Channel(); // Canal efímero exclusivo para la respuesta del depósito
                        
                        // Envía el carrito actual al depósito junto con la vía de retorno privada
                        chDepo.send((carrito, cDepoRta)); 
                        
                        // Espera bloqueado el veredicto booleano del depósito
                        bool exito = cDepoRta.receive(); 

                        if (exito) {
                            carrito.clear(); // Si la compra fue exitosa, el carrito se vacía automáticamente
                            cSesionPrivada.send("Compra concretada con éxito. Su carrito fue vaciado.");
                        } else {
                            cSesionPrivada.send("Error: No se pudo efectuar la compra por falta de stock.");
                        }
                    };

                    // COMANDO 3: Terminar la sesión de compra actual
                    ("Terminar") -> {
                        cSesionPrivada.send("Gracias por su visita. ¡Hasta la próxima!"); // Mensaje de despedida
                        sesionActiva = false; // Rompe el bucle de la sesión para dar fin al thread
                    };
                }
            }
        } // Fin del thread de sesión concurrente
    }
}

// ---------------------------------------- b
process Deposito(Map<String, Integer> stock) { // Recibe el diccionario de stock inicial
    while (true) {
        // Se queda esperando que caiga una tupla con un carrito y su vía de retorno privada
        case (chDepo.receive()) {
            (List<String> carrito, Channel cDepoRta) -> {

                // CONCURRENCIA TOTAL: Delegamos el procesamiento en un thread independiente.
                // Pasamos el 'stock' (se copia para trabajar de fondo), el 'carrito' y 'cDepoRta'.
                thread (stock, carrito, cDepoRta) {
                    boolean hayStockDeTodo = true;

                    // PASO 1: Validar si TODOS los libros del carrito tienen stock disponible
                    for (String titulo : carrito) {
                        // Si el libro no existe en el diccionario o su cantidad es 0
                        if (!stock.containsKey(titulo) || stock.get(titulo) < 1) {
                            hayStockDeTodo = false; // Marcamos que el pedido no se puede cumplir
                        }
                    }

                    // PASO 2: Si hay stock de todo, procedemos a descontar las unidades correspondientes
                    if (hayStockDeTodo) {
                        for (String titulo : carrito) {
                            int cantidadActual = stock.get(titulo);
                            stock.put(titulo, cantidadActual - 1); // Disminuye el stock por 1
                        }
                        cDepoRta.send(true); // Le responde de forma privada a la editorial con un TRUE
                    } else {
                        cDepoRta.send(false); // Le responde de forma privada a la editorial con un FALSE
                    }
                } // Fin del thread delegado de control de stock

            };
        }
    }
}
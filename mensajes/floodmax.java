// e2.a
Process Nodo(List<Channel> vecinos, Channel myChannel, Channel timer) {
    Channel myChannelT = new Channel(); // Canal exclusivo creado por este nodo para comunicarse con el Timer

    timer.send(myChannelT); // Le mandamos al Timer nuestro canal personal para que nos registre en su listado
    
    while (true) { // Bucle infinito para mantener vivo el proceso del nodo
        int uid = random(); 
        int max_uid = uid;
        
        while (true) { // Bucle interno para procesar las rondas consecutivas controladas por el Timer
            case (myChannelT.receive()) { // Evaluamos qué instrucción nos llega desde el canal de control del Timer
                
                "Proceda" -> {
                    for (Channel v : vecinos) {
                        v.send(max_uid); // Enviamos el máximo acumulado, no el id estático inicial
                    }
                    
                    repeat (vecinos.size()) { // Nos quedamos bloqueados recolectando los id de nuestros vecinos inmediatos
                        int uidVecino = myChannel.receive();
                        max_uid = max(max_uid, uidVecino); // Conservamos el mayor valor entre el nuestro y el recibido
                    }
                };
                "Fin" -> {
                    break; 
                }
            }
        }
        
        if (max_uid == uid) {
            print("Soy el lider de la red con ID: " + uid);
        } else {
            print("No soy lider, mi ID es: " + uid + " y el lider es: " + max_uid);
        }
    }
}

// e2.b
Process Timer(int frecuencia, int diametro, Channel myChannel) {
    Channel flood_max_channel = new Channel(); // Canal interno e independiente para pasarle el listado de nodos al algoritmo sin congelar el registro

    thread Registro() { // HILO 1: Encargado exclusivamente de registrar nuevos nodos a la red en cualquier momento
        List<Channel> nodos = new List<Channel>(); 
        
        while (true) {
            Channel nuevoNodo = myChannel.receive(); // Se bloquea a esperar que se conecte un nuevo nodo y nos dé su canal personal
            nodos.add(nuevoNodo); 
            
            if (nodos.size() % 10 == 0) {
                flood_max_channel.send(new List<Channel>(nodos)); // Hacemos una copia profunda de la lista actual de hilos y se la enviamos al orquestador
            }
        }
    }

    thread OrquestadorFloodMax() { // HILO 2: Encargado exclusivamente de orquestar las rondas del algoritmo FloodMax
        while (true) {
            List<Channel> nodos_a_procesar = flood_max_channel.receive(); // Espera a que el hilo de registro le mande una lista de nodos autorizada para competir
            
            repeat (diametro) {
                for (Channel n : nodos_a_procesar) { // Le damos la orden de inicio de ronda a todos los nodos participantes
                    n.send("Proceda"); 
                }
                sleep(frecuencia); 
            }
            
            for (Channel n : nodos_a_procesar) { // Al terminar las 'r' rondas, les mandamos la señal de cierre para que calculen si ganaron
                n.send("Fin"); 
            }
        }
    }
}

// e3.c
// Adentro del case (myChannelT.receive()) del Proceso Nodo:
("A Laburar", Channel temp) -> {
    if (max_uid == uid) { // Comprobamos si portamos la corona de líder en base a la última elección
        Object resultado = computo_costoso(); 
        temp.send(resultado); // Le devolvemos el cómputo final al canal de retorno privado que nos dio el Timer
    }
}

thread ClientManager(Channel myChannelC, List<Channel> nodos) { // Se añade este nuevo hilo al Timer global, y la lista 'nodos' ahora debe ser compartida o accesible por este hilo.
    while (true) {
        Request reqCliente = myChannelC.receive(); // Recibe una tupla del cliente con sus parámetros y su canal de respuesta dinámico
        
        thread (reqCliente, nodos) { // Lanzamos un sub-hilo por cliente para que las consultas corran de manera concurrente
            Channel temp = new Channel(); // Canal de retorno único para que el líder de la red nos devuelva el cálculo
            
            for (Channel n : nodos) {  // Le mandamos la orden a todos. Solo el que tenga seteado 'soyLider = true' va a procesarlo
                n.send(("A Laburar", temp)); 
            }
            
            Object resultado = temp.receive(); // Nos bloqueamos a esperar la respuesta proveniente del nodo líder de la red
            reqCliente.cRtaCliente.send(resultado); 
        }
    }
}
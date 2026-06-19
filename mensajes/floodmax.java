// A
Process Nodo(List<Channel> vecinos, Channel myChannel, Channel timer) {
    Channel myChannelT = new Channel();

    timer.send(myChannelT);
    
    while (true) {
        int uid = random();
        int max_uid = uid;
        
        while (true) {
            case (myChannelT.receive()) {
                "Proceda" -> {
                    for (Channel v : vecinos) {
                        v.send(max_uid);
                    }
                    
                    repeat (vecinos.size()) {
                        int uidVecino = myChannel.receive();
                        max_uid = max(max_uid, uidVecino);
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

// B
Process Timer(int frecuencia, int diametro, Channel myChannel) {
    Channel flood_max_channel = new Channel();

    thread Registro() {
        List<Channel> nodos = new List<Channel>();
        
        while (true) {
            Channel nuevoNodo = myChannel.receive();
            nodos.add(nuevoNodo);
            
            if (nodos.size() % 10 == 0) {
                flood_max_channel.send(new List<Channel>(nodos));
            }
        }
    }

    thread OrquestadorFloodMax() {
        while (true) {
            List<Channel> nodos_a_procesar = flood_max_channel.receive();
            
            repeat (diametro) {
                for (Channel n : nodos_a_procesar) {
                    n.send("Proceda");
                }
                sleep(frecuencia);
            }
            
            for (Channel n : nodos_a_procesar) {
                n.send("Fin");
            }
        }
    }
}

// C
("A Laburar", Channel temp) -> {
    if (max_uid == uid) {
        Object resultado = computo_costoso();
        temp.send(resultado);
    }
}

thread ClientManager(Channel myChannelC, List<Channel> nodos) {
    while (true) {
        Request reqCliente = myChannelC.receive();
        
        thread (reqCliente, nodos) {
            Channel temp = new Channel();
            
            for (Channel n : nodos) {
                n.send(("A Laburar", temp));
            }
            
            Object resultado = temp.receive();
            reqCliente.cRtaCliente.send(resultado);
        }
    }
}
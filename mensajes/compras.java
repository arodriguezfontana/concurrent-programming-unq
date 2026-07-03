package mensajes;

//=========================================== B ==============================================
process Servidor(myChannel){
    int umbral = M;
    Channel cantidadDeClientes = new Channel();
    cantidadDeClientes.send(0);
    while (true) {
        Channel client = myChannel.receive();
        int cantidadActual = 1 + cantidadDeClientes.receive();
        if (cantidadActual > umbral) {
            print("Advertencia!");
        }
        cantidadDeClientes.send(cantidadActual);
        Thread client_manager = new Thread(client) {
            Channel myChannelT = new Channel();
            List[] carrito = [];
            client.send("Mucho gusto, yo lo atenderé", myChannelT);
            while (true) {
                case (myChannelT.receive()) {
                    ("Agregar producto", p) ->
                        if (hayStock(p)){
                            carrito.add(p);
                            client.send("Producto aniadido");
                        }
                        else {
                            client.send("No hay Stock");
                        }
                    "Confirmar compra" ->
                        client.send(validar_compra(carrito));
                        carrito = [];
                    "Terminar comunicación" ->
                        print("Fina de la comunicacion con el cliente");
                        break;
                }
            };
            cantidadDeClientes.send(cantidadDeClientes.receive() - 1);
        };
        client_manager.run();
    }
}

Thread Cliente(server, productos) {
    Channel myChannel = new Channel();
    server.send(myChannel);
    (_, manager) = myChannel.receive();
    for p in productos {
        manager.send("Agregar producto", p);
        respuesta = manager.receive();
        print(respuesta);
    }
    manager.send("Confirmar compra");
    respuesta = manager.receive();
    print(respuesta);
    manager.send("Terminar comunicacion");
}

//===========================================A==============================================
process Servidor(myChannel){
    while (true) {
        Channel client = myChannel.receive();
        Thread client_manager = new Thread(client) {
            Channel myChannelT = new Channel();
            List[] carrito = [];
            client.send("Mucho gusto, yo lo atenderé", myChannelT);
            while (true) {
                case (myChannelT.receive()) {
                    ("Agregar producto", p) ->
                        if (hayStock(p)){
                            carrito.add(p);
                            client.send("Producto aniadido");
                        }
                        else {
                            client.send("No hay Stock");
                        }
                    "Confirmar compra" ->
                        client.send(validar_compra(carrito));
                        carrito = [];
                    "Terminar comunicación" ->
                        print("Fina de la comunicacion con el cliente");
                        break;
                }
            }
        };
        client_manager.run();
    }
}

Thread Cliente(server, productos) {
    Channel myChannel = new Channel();
    server.send(myChannel);
    (_, manager) = myChannel.receive();
    for p in productos {
        manager.send("Agregar producto", p);
        respuesta = manager.receive();
        print(respuesta);
    }
    manager.send("Confirmar compra");
    respuesta = manager.receive();
    print(respuesta);
    manager.send("Terminar comunicacion");
}
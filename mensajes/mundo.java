package mensajes;
// ================ A ================
Channel chServer = new Channel();

process Jugador(Datos datos) {
    String nickname = input();
    Channel chJugador = new Channel();
    chServer.send(nickname, datos, chJugador);
    (String response, Channel chMundo) = chJugador.receive();
    if (response != "Registrado con éxito") throw new RuntimeException("Falló");
    String accion = "Entrar al mundo";
    while(accion != "Salir"){
        Estado mundo = chJugador.receive();
        prtin(mundo);
        String accion = input();
        chMundo.send(accion, nickname);
    }
}

// ================ B ================
process Servidor() {
    HashMap<String, Channel> jugadores = new HashMap();
    Channel chJugadores = new Channel();
    Channel chMundo = new Channel();
    chJugadores.send(jugadores);
    Estado mundo = inicializarMundo();
    
    Thread registrarJugadores = new Thread(...) {
        while(true) {
            (nick, datos, chJugador) = chServer.receive();
            Channel[] js = chJugadores.receive();
            js.put(nickname, chJugador);
            chJugador.send("Registrado con éxito", chMundo);
            chJugadores.send(js);
        }
    };
    
    Thread actualizarMundo = new Thread(...) {
        Channel[] js = chJugadores.receive();
        for j in js {
            j.send(mundo);
        }
        int n = js.size();
        chJugadores.send(js);
        String[] acciones = []
        repeat(n) {
            (accion, nickname) = chMundo.receive();
            if (accion == "Salir") {
                js = chJugadores.receive();
                js.pop(nickname);
                chJugadores.send(js);
            }
            acciones.add((accion, nickname));
        }
        nuevoEstado(mundo, acciones);

    };
}
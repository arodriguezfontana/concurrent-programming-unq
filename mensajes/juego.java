package mensajes;
// bool valdar(Credenciales log);
// World generarMundo(int seed);
// World microUpdate(State s, Ref world);

process ServidorJuego(myChannel) {
    while(true) {
        Credenciales log = myChannel.receive();
        if (validar(log)) {
            Thread clientHandler(userCh = log.userCh()) {
                Channel myChannelH = new Channel();
                LoginResponse response = new LoginResponse(
                    responseMsg = "Credenciales correctas",
                    channel = myChannelH
                );
                userCh.send(response);
                World w = generarMundo(randomSeed());
                while(true) {
                    case myChannelH.receive() {
                        MicroUpdateRequest microU -> {
                            World microWorld = microUpdate(microU.state, w.getRef());
                            userCh.send(microWorld);
                        }
                        MacroUpdateRequest macroU -> {
                            Thread bufferHandler = new Thread(buffer = macroU.buffer) {
                                for update in buffer {
                                    World microWorld = microUpdate(update.state, w.getRef());
                                    userCh.send(microWorld);
                                }
                            }
                            bufferHandler.run();
                        }
                        LogoutRequest logout -> {
                            userCh.send("Adiooo, sesión cerrada");
                            break;
                        }
                    }
                }
            }
            clientHandler.run();
        } else {
            log.userCh.send("Credenciales incorrectas");
        }
    }
}

Request Credenciales() {
    String usuario;
    String contraseña;
    Channel userCh;
}

Request LoginResponse() {
    String responseMsg;
    Channel channel;
}

Request MicroUpdateRequest() {
     State state;
}

Request MacroUpdateRequest() {
    Buffer buffer;
}

Request LogoutRequest() {}

// ======================== B =====================

process ServidorJuego(myChannel) {
    Channel[] usuarios = [];
    Channel usuariosDelMultiplayer = new Channel();
    usuariosDelMultiplayer.send(usuarios);
    World globalWorld = generarMundo(randomSeed());
    while(true) {
        Credenciales log = myChannel.receive();
        if (validar(log)) {
            Thread clientHandler(userCh = log.userCh()) {
                Channel myChannelH = new Channel();
                LoginResponse response = new LoginResponse(
                    responseMsg = "Credenciales correctas",
                    channel = myChannelH
                );
                userCh.send(response);
                TipoDeJuego tipo = userCh.receive();
                case tipo {
                    SinglePlayer -> { ... } // Hago lo mismo que el item anteior
                    Multiplayer -> {
                        Channel[] users = usuariosDelMultiplayer.receive();
                        users.add(userCh);
                        usuariosDelMultiplayer.send(users);
                        while(true) {
                            case myChannelH.receive() {
                                MicroUpdateRequest microU -> {
                                    World microGlobalWorld = microUpdate(microU.state, globalWorld.getRef());
                                    Channel[] users = usuariosDelMultiplayer.receive();
                                    for user in users {
                                        user.send(microGlobalWorld);
                                    }
                                    usuariosDelMultiplayer.send(users);
                                }
                                MacroUpdateRequest macroU -> {
                                    Thread bufferHandler = new Thread(buffer = macroU.buffer) {
                                        for update in buffer {
                                            World microGlobalWorld = microUpdate(update.state, w.getRef());
                                            Channel[] users = usuariosDelMultiplayer.receive();
                                            for user in users {
                                                user.send(microGlobalWorld);
                                            }
                                            usuariosDelMultiplayer.send(users);
                                        }
                                    }
                                    bufferHandler.run();
                                }
                                LogoutRequest logout -> {
                                    Channel[] users = usuariosDelMultiplayer.receive();
                                    users.remove(userCh);
                                    usuariosDelMultiplayer.send(users);
                                    userCh.send("Adiooo, sesión cerrada");
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            clientHandler.run();
        } else {
            log.userCh.send("Credenciales incorrectas");
        }
    }
}

Enum TipoDeJuego {
    SinglePlayer
    Multiplayer 
}
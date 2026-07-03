package mensajes;
// A
global Channel AtoP = new Channel();
global Channel PtoS = new Channel();

process Agente (int id) {
    while (true) {
        AtoP.send(id);
        sleep(60);
    }
}

process Proxy {
    while (true) {
        int idAgente = AtoP.receive();
        PtoS.send(idAgente);
    }
}

process Servidor {
    while (true) {
        int idReportado = PtoS.receive();
        print("Log: El agente " + idReportado + " esta funcionando.");
    }
}

// B
global Channel AtoP = new Channel();
global Channel PtoS = new Channel();

process Agente (int id) {
    int miId = id;
    while (true) {
        Channel cRtaA = new Channel();
        AtoP.send(miId, cRtaA);
        int random = cRtaA.receive();
        miId = miId + random;
        sleep(60);
    }
}

process Proxy {
    while (true) {
        Request reqA = AtoP.receive();
        thread (reqA) {
            Channel cRtaP = new Channel();
            PtoS.send(reqA.id, cRtaP);
            int random = cRtaP.receive();
            reqA.cRtaA.send(random);
        }
    }
}

process Servidor {
    while (true) {
        Request reqP = PtoS.receive();
        print("Log: El agente " + reqP.id + " esta funcionando.");
        reqP.cRtaP.send(random());
    }
}

// C
global Channel AtoP = new Channel();
global Channel PtoS = new Channel();

process Servidor {
    global Channel cToken = new Channel();
    cToken.send(false);

    thread Notificacion() {
        while (true) {
            sleep(120);
            bool huboActividad = cToken.receive();
            if (!huboActividad) {
                print("ALERTA: No se recibio comunicacion de ningun Agente en los ultimos 2 minutos.");
            }
            cToken.send(false);
        }
    }

    while (true) {
        Request reqP = PtoS.receive();
        cToken.receive();
        cToken.send(true);
        print("Log: El agente " + reqP.id + " esta funcionando.");
        reqP.cRtaP.send(random());
    }
}
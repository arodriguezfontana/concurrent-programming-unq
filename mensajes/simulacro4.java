// e4.a
global Channel AtoP = new Channel(); // Canal donde el Proxy escucha los reportes de todos los agentes
global Channel PtoS = new Channel(); // Canal donde el Servidor escucha los reenvíos del Proxy

process Agente (int id) {
    while (true) { // Cada agente reporta su estado cada 60 segundos
        AtoP.send(id);  // Enviamos simplemente el ID único del agente por el canal global hacia el Proxy
        sleep(60); 
    }
}

process Proxy {
    while (true) { // El Proxy espera continuamente por los reportes
        int idAgente = AtoP.receive(); // Espera bloqueante hasta recibir un reporte de cualquier agente
        PtoS.send(idAgente); // Reenvía el ID recibido de inmediato al Servidor central
    }
}

process Servidor {
    while (true) { // El Servidor central espera continuamente por los reportes
        int idReportado = PtoS.receive(); // Espera bloqueante hasta recibir el reporte reenviado por el Proxy
        print("Log: El agente " + idReportado + " esta funcionando."); // El enunciado pide almacenar un log "imprimiendo por pantalla a medida que los va recibiendo"
    }
}

// e4.b
global Channel AtoP = new Channel(); 
global Channel PtoS = new Channel(); 

process Agente (int id) {
    int miId = id; // Copia local para poder modificar el ID con el número aleatorio recibido
    
    while (true) {
        Channel cRtaA = new Channel(); // Canal exclusivo donde este agente espera su respuesta
        
        AtoP.send(miId, cRtaA); // Enviamos nuestro ID y el canal donde el Proxy nos debe responder
        int random = cRtaA.receive(); // Espera bloqueante a que el Proxy le reenvíe el número aleatorio generado
        
        miId = miId + random;  
        sleep(60); 
    }
}

process Proxy {
    while (true) {
        // Recibe la tupla del Agente (contiene el ID del agente y su canal personal cRtaA)
        Request reqA = AtoP.receive(); 
        // Para no congelar el Proxy, lanzamos un hilo por reporte que maneje el viaje con el Servidor
        thread (reqA) { // Cada hilo maneja un reporte específico del agente que lo originó
            Channel cRtaP = new Channel(); // Canal dinámico exclusivo del Proxy para que el Servidor le responda a él
            PtoS.send(reqA.id, cRtaP); // Le manda al Servidor el ID del agente y el canal personal del Proxy 
            int random = cRtaP.receive(); // Espera bloqueante a que el Servidor le devuelva el número aleatorio al Proxy
            reqA.cRtaA.send(random); // El Proxy toma ese número aleatorio y se lo reenvía al Agente usando el canal
        }
    }
}

process Servidor {
    while (true) {
        Request reqP = PtoS.receive();  // Recibe el reporte enviado por el Proxy id y canal personal del Proxy
        print("Log: El agente " + reqP.id + " esta funcionando."); 
        reqP.cRtaP.send(random()); // Le devuelve el número aleatorio generado al Proxy usando su canal exclusivo
    }
}

// e4.c
global Channel AtoP = new Channel(); 
global Channel PtoS = new Channel(); 

process Servidor { // Los procesos Agente y Proxy se mantienen iguales
    global Channel cToken = new Channel(); // Canal de control interno para enviarnos un token (booleano) entre el hilo principal y el de notificación
    cToken.send(false); // Inicializamos el canal mandando un 'false'

    thread Notificacion() { // Hilo encargado exclusivamente de controlar el reloj de inactividad cada 2 minutos
        while (true) { // Bucle infinito para que el hilo de notificación siempre esté activo
            sleep(120); 
            bool huboActividad = cToken.receive(); // Sacamos el estado actual del canal de control

            if (!huboActividad) { // Si el flag es false, significa que el bucle del Servidor no puso un 'true' en estos 2 minutos
                print("ALERTA: No se recibio comunicacion de ningun Agente en los ultimos 2 minutos."); 
            }
            
            cToken.send(false); // Reseteamos el canal inyectando 'false' para el próximo ciclo de 2 minutos
        }
    }

    while (true) { // Bucle principal del Servidor
        Request reqP = PtoS.receive(); 
        cToken.receive();  // Sacamos el valor que esté flotando en el canal de control (ya sea true o false) para vaciarlo
        cToken.send(true); // Inyectamos inmediatamente un 'true'. Le avisamos al hilo Notificacion que la pista está activa
        print("Log: El agente " + reqP.id + " esta funcionando."); 
        reqP.cRtaP.send(random()); 
    }
}
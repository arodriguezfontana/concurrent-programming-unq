thread Auto { // acquire por cada estacion porque paro en cada una
    estacion[0].acquire();
    mutexA.acquire();
    mirobot = id_robot;
    mutexR.release();
    esperarAuto[mirobot].release();
    esperarSubirRobot[mirobot].acquire();
    estacion[1].acquire();

    for (i rango[0,5]) {
        llegada[i].release()
        termina[i].acquire()
        estacion[i+1].acquire()
        // Manejo prox. estacion
        estacion[i].release()
    }
    esperaBajar[mirobot].release();
    estacion[5].release()
}

thread Maquina (id) {
    while (true) {
        llegada[id].acquire();
        // limpia
        termina[id].release();
    }
}

global int id_robot = null;

thread Robot (id) { // se consume el id de la estacion o el robot que sea
    while (true) {
        estacion[0].release();
        mutexR.acquire();
        id_robot = id;
        mutexA.release();
        esperarAuto[id].acquire();
        esperarSubirRobot[id].release();
        // limpia
        esperaBajar[id].acquire();
    }
}

// c semaforo robot y estacion fuertes


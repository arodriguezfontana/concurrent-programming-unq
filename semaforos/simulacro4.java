thread Auto {
    estacion[0].acquire();
    mutexA.acquire();
    mirobot = id_robot;
    mutexR.release();
    esperarAuto[mirobot].release();
    esperarSubirRobot[mirobot].acquire();
    estacion[1].acquire();

    for (i rango[0,5]) {
        llegada[i].release();
        termina[i].acquire();
        estacion[i+1].acquire();
        estacion[i].release();
    }
    esperaBajar[mirobot].release();
    estacion[5].release();
}

thread Maquina (id) {
    while (true) {
        llegada[id].acquire();
        termina[id].release();
    }
}

global int id_robot = null;

thread Robot (id) {
    while (true) {
        estacion[0].release();
        mutexR.acquire();
        id_robot = id;
        mutexA.release();
        esperarAuto[id].acquire();
        esperarSubirRobot[id].release();
        esperaBajar[id].acquire();
    }
}
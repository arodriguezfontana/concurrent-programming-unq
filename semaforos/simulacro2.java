global Semaphore mutexL = new Semaphore(1);
global Semaphore mutexMesaApostador = new Semaphore(1);
global Semaphore mutexMesaCrupier = new Semaphore(1);
global Semaphore permisoL = new Semaphore(1);
global Semaphore permisoVerResultado = new Semaphore(0);
global Semaphore permisoAbrirRonda = new Semaphore(0);

global int resultado;
global int apostadores = 0;
global int cantApostadores = 0;

thread Apostador(int capital) {
    while (capital > 0) {
        permisoL.acquire();
        mutexL.acquire();
        apostadores++;
        if (apostadores == 1) {
            mutexMesaCrupier.acquire();
        }
        mutexL.release();
        permisoL.release();

        int apuesta = elegirNumero();
        capital--;

        mutexMesaApostador.acquire();
        cantApostadores++;
        mutexMesaApostador.release();

        mutexL.acquire();
        apostadores--;
        if (apostadores == 0) {
            mutexMesaCrupier.release();
        }
        mutexL.release();

        permisoVerResultado.acquire();
        if (apuesta == resultado) {
            print("Gane!");
            capital += 36;
        } else {
            print("Perdi!");
        }
        permisoAbrirRonda.release();
    }
    print("No, mi casa!");
}

thread Crupier {
    while (true) {
        print("No va mas");
        
        permisoL.acquire();
        mutexMesaCrupier.acquire();
        
        resultado = girarRuleta();
        
        mutexMesaApostador.acquire();
        int totalApostadoresRonda = cantApostadores;
        cantApostadores = 0;
        mutexMesaApostador.release();
        
        permisoVerResultado.release(totalApostadoresRonda);
        permisoAbrirRonda.acquire(totalApostadoresRonda);
        
        mutexMesaCrupier.release();
        permisoL.release();
    }
}
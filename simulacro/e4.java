global Semaphore mutexL = new Semaphore(1); // Protege la variable 'apostadores'.
global Semaphore mutexMesaApostador = new Semaphore(1); // Protege la variable 'cantApostadores'.
global Semaphore mutexMesaCrupier = new Semaphore(1); // Exclusión mutua total sobre el paño de la mesa.
global Semaphore permisoL = new Semaphore(1); // Torniquete que usa el Crupier para trabar la fila de ingreso.
global Semaphore permisoVerResultado = new Semaphore(0); // Bloquea a los apostadores hasta que la bolilla se detenga.
global Semaphore permisoAbrirRonda = new Semaphore(0); // Bloquea al Crupier hasta que los apostadores terminen de cobrar.

global int resultado; // Buffer de un espacio para comunicar el número ganador.
global int apostadores = 0; // Cuenta cuántos están adentro de la mesa apostando en este instante.
global int cantApostadores = 0; // Cuenta cuántas apuestas efectivas se registraron en la ronda actual.

thread Apostador(int capital) { // Prioridad lectores
    while (capital > 0) {
        // INGRESO A LA MESA
        permisoL.acquire(); // Pasa por el filtro del Crupier. Si el Crupier cerró, acá se frena.
        mutexL.acquire(); // Toma el candado para modificar el contador de personas en la mesa.
        apostadores++;
        if (apostadores == 1) {
            mutexMesaCrupier.acquire(); // El primer apostador le bloquea la mesa al Crupier.
        }
        mutexL.release();
        permisoL.release(); // Libera el filtro inmediatamente para el siguiente de la fila.

        // APUESTA
        int apuesta = elegirNumero(); 
        capital--;

        mutexMesaApostador.acquire();
        cantApostadores++; // Registra de forma segura una ficha más en la mesa para esta ronda.
        mutexMesaApostador.release();

        // EGRESO DE LA MESA
        mutexL.acquire();
        apostadores--;
        if (apostadores == 0) {
            mutexMesaCrupier.release(); // El último apostador en retirarse le devuelve el control de la mesa al Crupier.
        }
        mutexL.release();

        // RESULTADO Y COBRO
        permisoVerResultado.acquire(); // Se queda esperando que el Crupier cante el número ganador.
        if (apuesta == resultado) {
            print("Gane!")
            capital += 36
        } else {
            print("Perdi!")
        }
        permisoAbrirRonda.release(); // Le avisa al Crupier que ya procesó su jugada.
    }
    print("No, mi casa!")
}

thread Crupier {
    while (true) {
        // Espero el tiempo que quiero
        print("No va mas");
        
        // TRABA DE PRIORIDAD
        permisoL.acquire(); // Cierra el paso a nuevos apostadores (Prioridad Escritor)
        mutexMesaCrupier.acquire(); // Pide la mesa, espera hasta que el ultimo apueste
        
        // Gira la ruleta
        resultado = girarRuleta(); // El resultado se guarda en la variable global
        
        mutexMesaApostador.acquire(); // Captura una copia segura de cuántos jugaron en esta ronda.
        int totalApostadoresRonda = cantApostadores;
        cantApostadores = 0; // Resetea el contador de fichas para la próxima ronda.
        mutexMesaApostador.release();
        
        // CONTROL DE PREMIOS
        permisoVerResultado.release(totalApostadoresRonda); // Despierta a la cantidad exacta de jugadores que pusieron ficha.
        permisoAbrirRonda.acquire(totalApostadoresRonda); // Espera a que esa misma cantidad exacta cobre y grite el resultado.
        
        // REABRIR LA MESA
        mutexMesaCrupier.release(); // Libera la mesa y permiten volver a entrar
        permisoL.release(); // Abre el paso para la fila de la siguiente ronda
    }
}
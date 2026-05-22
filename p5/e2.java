// a) Productor-Consumidor
// Empiezan en 0 para forzar la espera
Semaphore puedeAbordar = new Semaphore(0); // El bote avisa que esta en la orilla y ya pueden subir
Semaphore puedeZarpar = new Semaphore(0); // La persona avisa que ya se acomodo o bajo
Semaphore puedeDescender = new Semaphore(0); // El bote avisa que llego al otro lado y ya pueden bajar

thread Bote {
    while(true) { // No se termina el ciclo
        puedeAbordar.release(); // LLega a la orilla y avisa que la persona puede subir
        puedeZarpar.acquire(); // Espero hasta que la persona suba y se acomode
        puedeDescender.release(); // LLega el la otra orilla y avisa que la persona puede bajar
        puedeZarpar.acquire(); // Espero hasta que la persona baje antes de pegar la vuelta
    }
}

thread Persona {
    puedeAbordar.acquire(); // Espero a que el bote llegue y me avise que puedo abordar
    puedeZarpar.release(); // Le aviso al bote que ya estoy listo para que zarpe
    puedeDescender.acquire(); // Espero hasta que el bote llegue a la otra orilla
    puedeZarpar.release(); // Le aviso al bote que ya baje, por ende, que ya puede pegar la vuelta
}

// b) Todas las personas bajan y despues suben las nuevas
int n = 5;
Semaphore[] puedeAbordar = new Semaphore[2]; // 0 porque nadie pasa sin el permiso del bote
puedeAbordar[0] = new Semaphore(0); // Oeste
puedeAbordar[1] = new Semaphore(0); // Este

Semaphore[] puedeDescender = new Semaphore[2];
puedeDescender[0] = new Semaphore(0); // Oeste
puedeDescender[1] = new Semaphore(0); // Este

Semaphore puedeZarpar = new Semaphore(0);    
Semaphore puedeSentarse = new Semaphore(n); // Porque hay n lugares libres al principio

thread Bote {
    int costa = 0;
    while(true) {
        repeat(n) puedeAbordar[costa].release(); // Llega y habilita n asientos
        repeat(n) puedeZarpar.acquire(); // Espera a que los n pasajeros avisen que estan sentados
        costa = (costa + 1) % 2; // Cambia a la otra costa
        repeat(n) puedeDescender[costa].release(); // Avisa a los n pasajeros que pueden bajar
        repeat(n) puedeSentarse.acquire(); // Espera a que los n asientos se liberen antes de volver a cargar
    }
}

thread Persona (int costa) {
    puedeAbordar[costa].acquire(); // Espera que el bote llegue a SU costa
    puedeZarpar.release(); // Avisa al bote que ya ocupo su lugar
    puedeDescender[(costa + 1) % 2].acquire(); // Espera el permiso para bajar en la costa de destino
    puedeSentarse.release(); // Avisa al bote que libero el asiento y hay un espacio disponible
}

// Las personas suben y bajan concurrentemente
thread Bote {
    int costa = 0;
    while(true) {
        repeat(n) puedeAbordar[costa].release();
        repeat(n) puedeZarpar.acquire();
        costa = (costa + 1) % 2; 
        repeat(n) puedeDescender[costa].release();
    }
}

thread Persona (int costa) {
    puedeAbordar[costa].acquire();
    puedeSentarse.acquire(); // Espera a que haya un asiento desocupado
    puedeZarpar.release(); // Le avisa al bote que ya se sento y pueden zarpar
    puedeDescender[(costa + 1) % 2].acquire();}
    puedeSentarse.release(); // Libera el asiento al llegar permitiendo que otra persona suba en el proximo viaje
}
int x = 0;
Semaphore mutex = new Semaphore(1); // Se inicializa en 1 para que el primero que llegue pueda entrar, si empieza en 0, nadie podria entrar

thread T1 : { // Hay GE porque no hay una secuencia rigida, cualquiera puede ejecutar el acquire y ningun thread depende del otro para avanzar.
    mutex.acquire(); // Pide pasar con el permiso y, si esta disponible, pasa
    x = x + 1;
    mutex.release(); // Devuelve el permiso para que pueda pasar el proximo
}

thread T2 : {
    mutex.acquire();
    x = x + 2;
    mutex.release();
}

thread T3 : {
    mutex.acquire();
    x = x + 3;
    mutex.release();
}
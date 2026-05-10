Semaphore sA = new Semaphore(1); // Permite que cualquiera de los dos se ejecute primero
Semaphore sB = new Semaphore(1);

thread T1 // La cantidad de A y B difiere como maximo en 1
    while (true)
        sA.acquire(); // Espero a que B me habilite
        print ( "A" );
        sB.release(); // Le doy el premiso a B

thread T2
    while (true)
        sB.acquire(); // Espero a que A me habilite
        print ( "B" );
        sA.release(); // Le doy el premiso a A

Semaphore sA = new Semaphore(1); // Permite que primero se ejecute la A
Semaphore sB = new Semaphore(0);

thread T1 // ABABAB
    while (true)
        sA.acquire();
        print ( "A" );
        sB.release();

thread T2
    while (true)
        sB.acquire();
        print ( "B" );
        sA.release();

Semaphore sA = new Semaphore(1); // Permite que primero se ejecute la A
Semaphore sB = new Semaphore(0);

thread T1 // ABBABBABB
    while (true)
        sA.acquire();
        print ( "A" );
        sB.release();
        sB.release();

thread T2
    while (true)
        repeat(2) // Toma el permiso e imprime B inmediatamente dos veces
            sB.acquire();
            print ( "B" );
        sA.release();
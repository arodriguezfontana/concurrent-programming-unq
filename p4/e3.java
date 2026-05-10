Semaphore sI = new Semaphore(0); 
Semaphore sO = new Semaphore(0); 
Semaphore sOK = new Semaphore(0);

thread T1 {
    print ( "R" );
    sI.release();
    sOK.acquire(); // Espera que termine la secuencia R I O
    print ( "OK" );
}

thread T2 {
    sI.acquire();
    print ( "I" );
    sO.release();
    sOK.acquire(); // Espera que termine la secuencia R I O
    print ( "OK" );
}

thread T3 {
    sO.acquire();
    print ( "O" )
    sOK.release(); // Habilita los 2 OK (sus permisos) 
    sOK.release();
    print ( "OK" );
}
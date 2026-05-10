Semaphore sC = new Semaphore(0);
Semaphore sR = new Semaphore(0);
Semaphore sO = new Semaphore(0);

thread T1 {
    sC.acquire(); // Espera que T2 imprima A
    print ( "C" );
    sR.release(); // Habilita la R porque ya se imprimio C
    print ( "E" );
    sO.release(); // Habilita la O porque ya se imprimio E
}

thread T2 {
    print ( "A" );
    sC.release(); // Habilita la C
    sR.acquire(); // Espera que T1 imprima C
    print ( "R" );
    sO.acquire(); // Espera que T1 imprima E
    print ( "O" );
}
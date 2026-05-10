Semaphore sF = new Semaphore(0);
Semaphore sC = new Semaphore(0);

thread T1 {
    print ( "A" );
    sF.release();
    print ( "B" );
    sC.acquire();
    print ( "C" );
}

thread T2 {
    print ( "E" );
    sF.acquire();
    print ( "F" );
    sC.release();
    print ( "G" );
}
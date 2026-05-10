Semaphore sF = new Semaphore(0);
Semaphore sH = new Semaphore(0);
Semaphore sC = new Semaphore(0);

thread
    while (true) {
    print ( "A" );
    sF.release();
    print ( "B" );
    sC.acquire();
    print ( "C" );
    print ( "D" );
}

thread
    while (true) {
    print ( "E" );
    sH.release();
    sF.acquire();
    print ( "F" );
    print ( "G" );
    sC.release();

}

thread
    while (true) {
    sH.acquire();
    print ( "H" );
    print ( "I" );
}
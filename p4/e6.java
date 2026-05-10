int y = 0 , z = 0;
Semaphore s = new Semaphore(0);
Semaphore s2 = new Semaphore(0);

// Los valores finales posibles de x son 0, 1, 2 y 3

thread T1 { // Para 0
    int x ;
    x = y + z ;
    s.release();
}

thread T2 {
    s.acquire();
    y = 1;
    z = 2;
}

thread T1 { // Para 1
    s.acquire();
    int x ;
    x = y + z ;
    s2.release();
}

thread T2 {
    y = 1;
    s.release();
    s2.acquire();
    z = 2;
}

thread T1 { // Para 3
    s.acquire();
    int x ;
    x = y + z ;
}

thread T2 {
    y = 1;
    z = 2;
    s.release();
}
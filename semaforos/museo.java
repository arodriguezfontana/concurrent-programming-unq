// A
global Semaphore permisoLaburar = new Semaphore(1);
global Semaphore mutexVisitante = new Semaphore(1);
global Semaphore permisoShow = new Semaphore(0);
global int visitantes = 0;

Thread Visitante(int cantidadBoletos) {
    mutexVisitante.acquire();
    visitantes++;
    if(visitantes==1){
        permisoLaburar.acquire();
    }
    mutexVisitante.release();
    
    permisoShow.release(cantidadBoletos);

    mutexVisitante.acquire();
    visitantes--;
    if(visitantes==0){
        permisoLaburar.release();
    }
    mutexVisitante.release();
}

Thread EquipoRenovacion {
    while(true){
        permisoLaburar.acquire();
        permisoLaburar.release();
    }
}

Thread Personajes {
    while(true){
        permisoShow.acquire();
    }
}

// B
global Semaphore permisoLaburar = new Semaphore(1);
global Semaphore mutexVisitante = new Semaphore(1);
global Semaphore permisoShow = new Semaphore(0);
global Semaphore permisoVisitante = new Semaphore(1);
global Semaphore mutexP = new Semaphore(1);
global Semaphore pausarShow = new Semaphore(1);
global int visitantes = 0;

Thread Visitante(int cantidadBoletos) {
    mutexP.acquire();
    permisoVisitante.acquire();
    mutexVisitante.acquire();
    visitantes++;
    if(visitantes==1){
        permisoLaburar.acquire();
    }
    mutexVisitante.release();
    permisoVisitante.release();
    mutexP.release();
    
    permisoShow.release(cantidadBoletos);

    mutexVisitante.acquire();
    visitantes--;
    if(visitantes==0){
        permisoLaburar.release();
    }
    mutexVisitante.release();
}

Thread EquipoRenovacion {
    while(true){
        permisoVisitante.acquire();
        
        permisoLaburar.acquire();
        pausarShow.acquire();
        
        pausarShow.release();
        permisoLaburar.release();
        permisoVisitante.release();
    }
}

Thread Personajes {
    while(true){
        permisoShow.acquire();
        pausarShow.acquire();
        pausarShow.release();
    }
}
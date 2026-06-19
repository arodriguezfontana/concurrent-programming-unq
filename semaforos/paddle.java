global Semaphore[] permisoAbrirPuertasParaEntrar = [0,...,0]; 
global Semaphore[] permisoEntrarALaCancha = [0,...,0]; 
global Semaphore[] permisoCerrarPuertas = [0,...,0]; 
global Semaphore[] permisoJugar = [0,...,0]; 
global Semaphore[] permisoAbrirPuertasParaSalir = [0,...,0]; 
global Semaphore[] permisoSalirDeLaCancha = [0,...,0]; 

global int[] letrero = [null, null, null, null]; 
global int inicio = 0; 
global int fin = 0; 
global Semaphore vacio = new Semaphore(4); 
global Semaphore lleno = new Semaphore(0); 
global Semaphore mutexCancha = new Semaphore(1); 
global Semaphore mutexPersona = new Semaphore(1); 

global Semaphore mutexP = new Semaphore(1, True); 
global Semaphore permisoUsarVestuario = new Semaphore(1); 
global Semaphore mutexPersonaEnVestuario = new Semaphore(1); 
global Semaphore permisoEntrarVestuario = new Semaphore(1); 
global Semaphore permisoLimpiar = new Semaphore(0); 
global int personas = 0;

Thread Persona {
    lleno.acquire();
    mutexPersona.acquire();
    int cancha = letrero[fin];
    fin = (fin+1) % 4;
    mutexPersona.release();
    vacio.release();
    
    permisoAbrirPuertasParaEntrar[cancha].release();
    permisoEntrarALaCancha[cancha].acquire();
    permisoCerrarPuertas[cancha].release();
    permisoJugar[cancha].acquire();
    permisoAbrirPuertasParaSalir[cancha].release();
    permisoSalirDeLaCancha[cancha].acquire();
    permisoCerrarPuertas[cancha].release();
    
    mutexP.acquire();
    permisoUsarVestuario.acquire();
    mutexPersonaEnVestuario.acquire();
    personas++;
    if (personas==1){
        permisoEntrarVestuario.acquire();
    }
    mutexPersonaEnVestuario.release();
    permisoUsarVestuario.release();
    mutexP.release();
    
    mutexPersonaEnVestuario.acquire();
    personas--;
    if (personas==0){
        permisoEntrarVestuario.release();
    }
    mutexPersonaEnVestuario.release();
}

Thread Cancha(int id) {
    while(true){
        repeat(4){
            vacio.acquire();
            mutexCancha.acquire();
            letrero[inicio] = id;
            inicio = (inicio+1) % 4;
            mutexCancha.release();
            lleno.release();
        }
        permisoAbrirPuertasParaEntrar[id].acquire(4);
        permisoEntrarALaCancha[id].release(4);

        permisoCerrarPuertas[id].acquire(4);
        permisoJugar[id].release(4);

        permisoAbrirPuertasParaSalir[id].acquire(4);
        permisoSalirDeLaCancha[id].release(4);
        permisoCerrarPuertas[id].acquire(4);
        permisoLimpiar.release();
    }
}

Thread Dron {
    while(true){
        permisoLimpiar.acquire(30);
        permisoUsarVestuario.acquire();
        permisoEntrarVestuario.acquire();
        
        permisoEntrarVestuario.release();
        permisoUsarVestuario.release();
    }
}
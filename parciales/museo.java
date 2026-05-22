// ============== Ejercicio A =============
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
    
    // El visitante pasea por el museo
    permisoShow.release(cantidadBoletos);
    // El visitante pasea por el museo

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
        // Renovar exhibiciones
        permisoLaburar.release();
    }
}

Thread Personajes {
    while(true){
        permisoShow.acquire();
        // Iniciar show
    }
}



// ============== Ejercicio B =============
global Semaphore permisoLaburar = new Semaphore(1); // Controla si el museo está libre de personas para mantenimiento
global Semaphore mutexVisitante = new Semaphore(1); // Protege el contador global de visitantes
global Semaphore permisoShow = new Semaphore(0); // Acumula los billetes/fichas depositados por los visitantes
global Semaphore permisoVisitante = new Semaphore(1); // Candado que el Equipo usa para CERRAR la puerta del museo
global Semaphore mutexP = new Semaphore(1); // Torniquete para asegurar el orden de prioridad del Equipo
global Semaphore pausarShow = new Semaphore(1); // Exclusión mutua entre el show musical y el mantenimiento.
global int visitantes = 0; // Contador global de personas dentro del edificio.

Thread Visitante(int cantidadBoletos) { // LECTOR
    // FASE 1: Control de Ingreso
    mutexP.acquire(); // Toma el torniquete. Si el Equipo está esperando, se traba acá.
    permisoVisitante.acquire(); // Verifica si la puerta del museo está abierta.
    mutexVisitante.acquire(); // Abre el candado para modificar el contador de visitantes.
    visitantes++; // Registra que entró una persona
    if(visitantes==1){
        permisoLaburar.acquire(); // La primer persona en entrar le roba el permiso al Equipo de Renovación.
    }
    mutexVisitante.release(); // Libera el contador.
    permisoVisitante.release(); // Deja la puerta libre para el que viene atrás
    mutexP.release(); // Suelta el torniquete.
    
    // FASE 2: Iniciar Espectáculo (Productor de billetes)
    // El visitante pasea por el museo
    permisoShow.release(cantidadBoletos); // Deposita sus billetes
    // El visitante pasea por el museo

    // FASE 3: Egreso del Museo
    mutexVisitante.acquire(); // Pide el candado para restar su salida.
    visitantes--; // Registra que se retira del edificio.
    if(visitantes==0){
        permisoLaburar.release(); // La ultima persona en salir le devuelve el permiso al Equipo de Renovación.
    }
    mutexVisitante.release(); // Libera el contador.
}

Thread EquipoRenovacion { // Escritor con prioridad
    while(true){
        // FASE 1: Bloquear Nuevos Ingresos
        permisoVisitante.acquire(); // "Cierra con llave la boletería". Nadie más puede ingresar al museo.
        
        // FASE 2: Esperar el Vaciado y Pausar el Show
        permisoLaburar.acquire(); // Se traba acá hasta que el ÚLTIMO visitante que ya estaba adentro se retire.
        pausarShow.acquire(); // Apaga el sistema de los personajes (espera si hay una canción sonando).
        // Renovar exhibiciones

        // FASE 3: Reapertura del Museo
        pausarShow.release(); // Reactiva el sistema de los personajes animatrónicos.
        permisoLaburar.release(); // Devuelve el permiso del edificio.
        permisoVisitante.release(); // "Abre la boletería". La gente puede volver a entrar en la próxima ronda.
    }
}

Thread Personajes { // Consumidor de billetes
    while(true){
        permisoShow.acquire(); // Se queda bloqueado hasta que algún visitante deposite AL MENOS un billete.
        pausarShow.acquire(); // Verifica que el Equipo de Renovación no esté trabajando en el edificio.
        // Iniciar show consumiendo un billete
        pausarShow.release(); // Libera el control para que el Equipo de Renovación pueda interrumpir si quiere.
    }
}

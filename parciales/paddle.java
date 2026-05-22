// Se usa un arreglo de tamaño K para que cada cancha tenga su propia comunicación privada
global Semaphore[] permisoAbrirPuertasParaEntrar = [0,...,0]; 
global Semaphore[] permisoEntrarALaCancha = [0,...,0]; // Todos fuertes para respetar el orden de llegada
global Semaphore[] permisoCerrarPuertas = [0,...,0]; 
global Semaphore[] permisoJugar = [0,...,0]; 
global Semaphore[] permisoAbrirPuertasParaSalir = [0,...,0]; 
global Semaphore[] permisoSalirDeLaCancha = [0,...,0]; 

// Esquema P-C. La cancha anota su ID para atraer jugadores.
global int[] letrero = [null, null, null, null]; // Porque una cancha de paddle necesita exactamente 4 personas para iniciar un partido. El arreglo funciona como una "sala de espera" temporal de 4 casilleros donde la cancha deposita sus 4 invitaciones.
global int inicio = 0; // Para que la cancha escriba.
global int fin = 0; // Para que la persona lea.
global Semaphore vacio = new Semaphore(4); // Inicia en 4 porque hay 4 espacios libres para anotar IDs
global Semaphore lleno = new Semaphore(0); // Inicia en 0 porque no hay IDs anotados aún
global Semaphore mutexCancha = new Semaphore(1); // Protege el 'inicio'.
global Semaphore mutexPersona = new Semaphore(1); // Protege el 'fin'.

// L-E con Prioridad al Dron unico
global Semaphore mutexP = new Semaphore(1,True); // Dar prioridad al Dron
global Semaphore permisoUsarVestuario = new Semaphore(1); // Candado principal de entrada al vestuario.
global Semaphore mutexPersonaEnVestuario = new Semaphore(1); // Protege el contador de personas.
global Semaphore permisoEntrarVestuario = new Semaphore(1); // Controla la exclusión entre personas y el Dron.
global Semaphore permisoLimpiar = new Semaphore(0); // Acumula partidos para avisar al Dron.

Thread Persona {
    // FASE 1: Buscar Cancha (Consumidor).
    lleno.acquire(); // Espera a que haya un ID en el letrero
    mutexPersona.acquire(); // Candado para ser la ÚNICA persona leyendo
    int cancha = letrero[fin]; // Toma el ID de la cancha asignada.
    fin = (fin+1) % 4; // Avanza el puntero de lectura de forma circular
    mutexPersona.release(); // Suelta el candado de lectura
    vacio.release(); // Avisa que se liberó un renglón en el letrero
    
    // FASE 2: Sincronización con SU cancha específica.
    // LLega a la cancha
    permisoAbrirPuertasParaEntrar[cancha].release(); // Avisa a la cancha: "Ya llegué".
    permisoEntrarALaCancha[cancha].acquire(); // Espera a que la cancha abra la puerta.
    // Entra a la cancha
    permisoCerrarPuertas[cancha].release(); // Avisa: "Ya estoy adentro".
    permisoJugar[cancha].acquire();
    // Juega
    permisoAbrirPuertasParaSalir[cancha].release();
    permisoSalirDeLaCancha[cancha].acquire();
    // Sale de la cancha
    permisoCerrarPuertas[cancha].release();
    
    // FASE 3: El Vestuario (Esquema Lectores-Escritores)
    // Va al vestuario
    mutexP.acquire(); // Torniquete: si el dron espera, nadie más entra aquí
    permisoUsarVestuario.acquire(); // Bloquea si el Dron está limpiando
    mutexPersonaEnVestuario.acquire();
    personas++;
    if (personas==1){
        permisoEntrarVestuario.acquire(); // El primer humano bloquea al Dron
    }
    mutexPersonaEnVestuario.release();
    permisoUsarVestuario.release(); // Libera el paso para la siguiente persona.
    mutexP.release(); // Libera el torniquete.
    // Bañarse
    mutexPersonaEnVestuario.acquire();
    personas--;
    if (personas==0){
        permisoEntrarVestuario.release(); // El último humano libera al Dron
    }
    mutexPersonaEnVestuario.release();
    // Ir a casa
}

Thread Cancha(int id) { // Cada cancha tiene sus 4 repeats y espera 4 de su ID
    while(true){
        repeat(4){
            // FASE 1: Publicar disponibilidad. Anota su ID 4 veces en el letrero
            vacio.acquire(); // Espera a que haya un renglón libre en el letrero
            mutexCancha.acquire(); // Candado para ser la ÚNICA cancha escribiendo
            letrero[inicio] = id; // Anota su numero de cancha
            inicio = (inicio+1) % 4; // Avanza el puntero de escritura de forma circular
            mutexCancha.release(); // Suelta el candado de escritura
            lleno.release(); // Avisa que hay un ID listo para ser leído
        }
        // FASE 2: Entrada. Espera a que los 4 jugadores lleguen a la puerta.
        permisoAbrirPuertasParaEntrar[id].acquire(4);
        // Se abren a las puertas
        permisoEntrarALaCancha[id].release(4);

        // FASE 3: Inicio del juego.
        permisoCerrarPuertas[id].acquire(4); // Espera a que los 4 terminen de entrar.
        // Se cierran las puertas
        // Se liberan las pelotas
        permisoJugar[id].release(4); // Da la señal de inicio del partido.

        // FASE 4: Fin y Salida.
        permisoAbrirPuertasParaSalir[id].acquire(4); // Espera a que los 4 digan que terminaron.
        // Se abren las puertas
        permisoSalirDeLaCancha[id].release(4); // Les permite salir físicamente.
        permisoCerrarPuertas[id].acquire(4); // Espera a que los 4 salgan.
        // Se cierran las puertas
        permisoLimpiar.release(); // Suma 1 a la cuenta de partidos para el Dron.
    }
}

Thread Dron {
    while(true){
        permisoLimpiar.acquire(30); // Espera a que se acumulen 30 avisos de partidos terminados.
        // FASE DE BLOQUEO: Prioridad Escritores
        permisoUsarVestuario.acquire(); // Cierra el paso a nuevas personas
        permisoEntrarVestuario.acquire(); // Espera a que los que están adentro terminen de asearse y salga
        // Se limpia el vestuario
        permisoEntrarVestuario.release(); // Libera la exclusión.
        permisoUsarVestuario.release(); // Permite que la gente vuelva a entrar.
    }
}
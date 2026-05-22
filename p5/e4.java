global int buffer; // Donde la maquina anota su ID cuando estan disponibles

Semaphore vacio = new Semaphore(1); // Indica que se puede anotar un ID
Semaphore lleno = new Semaphore(0); // Indica que ya hay un ID y puede ser leido

Semaphore permisoLavar[]   = [new Semaphore(0), ...]; // Controla cuando la ropa ya esta cargada
Semaphore permisoRetirar[] = [new Semaphore(0), ...]; // Controla cuando el lavado termino
Semaphore permisoReusar[]  = [new Semaphore(0), ...]; // Controla cuando el cliente ya se llevo la ropa

thread Maquina (int id) : {
    while (true) {
        vacio.acquire(); // Pide publicar su ID
        buffer = id; // Pone su ID en el buffer
        lleno.release(); // Avisa a las personas que ya hay una maquina disponible
        permisoLavar[id].acquire(); // Espera hasta que la persona le de permiso tras cargar la ropa
        // Lavar ropa
        permisoRetirar[id].release(); // Envia la notificacion para retirar la ropa a la persona
        permisoReusar[id].acquire(); // Espera a que la persona retire la ropa para volver a atender a otra
    }
}

thread Persona : {
    lleno.acquire(); // Espera a que aparezca una máquina libre en el buffer
    int idMaquina = buffer; // Toma el ID de la máquina que se libero
    vacio.release(); // Libera el buffer para que otra maquina pueda avisar que esta libre
    // Poner ropa a lavar
    permisoLavar[idMaquina].release(); // Le da permiso a SU maquina para empezar a lavar
    // Hacer compras
    permisoRetirar[idMaquina].acquire(); // Espera hasta recibir el mensaje de SU máquina
    // Retirar ropa
    permisoReusar[idMaquina].acquire(); // Le avisa a la máquina que ya sacó la ropa y puede atender a otro
}
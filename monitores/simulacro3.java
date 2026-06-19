// A
monitor Gestor {
    private int observadores = 0;
    private int calibradores = 0;

    public void iniciarObservacion() {
        while (calibradores == 1) {
            wait();
        }
        observadores++;
    }
    
    public void finalizarObservacion() {
        observadores--;
        if (observadores == 0) {
            notifyAll();
        }
    }
    
    public void iniciarCalibracion() {
        while (calibradores == 1 || observadores > 0) {
            wait();
        }
        calibradores = 1;
    }
    
    public void finalizarCalibracion() {
        calibradores = 0;
        notifyAll();
    }
}

// B
monitor Gestor {
    private int observadores = 0;
    private int calibradores = 0;
    private int posicionActual = -1;

    public void iniciarObservacion(int posicion) {
        while (calibradores == 1 || (posicionActual != posicion && posicionActual != -1)) {
            wait();
        }
        observadores++;
        posicionActual = posicion;
    }
    
    public void finalizarObservacion() {
        observadores--;
        if (observadores == 0) {
            posicionActual = -1;
            notifyAll();
        }
    }
    
    public void iniciarCalibracion() {
        while (calibradores == 1 || observadores > 0) {
            wait();
        }
        calibradores = 1;
    }
    
    public void finalizarCalibracion() {
        calibradores = 0;
        notifyAll();
    }
}

// C
monitor Gestor {
    private int observadores = 0;
    private int calibradores = 0; 
    private int posicionActual = -1;
    private int calibradoresEsperando = 0;

    public void iniciarObservacion(int posicion) {
        while (calibradores == 1 || ((posicionActual != posicion && posicionActual != -1) || calibradoresEsperando > 0)) {
            wait();
        }
        observadores++;
        posicionActual = posicion;
    }
    
    public void finalizarObservacion() {
        observadores--;
        if (observadores == 0) {
            posicionActual = -1;
            notifyAll();
        }
    }
    
    public void iniciarCalibracion() {
        calibradoresEsperando++;
        while (calibradores == 1 || observadores > 0) {
            wait();
        }
        calibradoresEsperando--;
        calibradores = 1;
    }
    
    public void finalizarCalibracion() {
        calibradores = 0;
        notifyAll();
    }
}
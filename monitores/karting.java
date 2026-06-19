Thread Karting(int ranking) {
    ingresarKarting(ranking);
    // Andar por la pista
    salirKarting();
}

Thread Particular{
    ingresarParticular();
    // Andar por la pista
    salirParticular();
}

// A
class Autodromo {
    private int kartings = 0;
    private int particulares = 0;
    private int kartingsEsperando = 0;

    private boolean puedeIngresarKarting() {
        return particulares == 0 && kartings == 0;
    }

    private boolean puedeIngresarParticular() {
        return kartings == 0 && kartingsEsperando == 0;
    }

    synchronized void ingresarKarting() {
        kartingsEsperando++;
        while (!puedeIngresarKarting()) {
            wait();
        }
        kartingsEsperando--;
        kartings = 1;
    }

    synchronized void salirKarting() {
        kartings = 0;
        notifyAll();
    }

    synchronized void ingresarParticular() {
        while (!puedeIngresarParticular()) {
            wait();
        }
        particulares++;
    }

    synchronized void salirParticular() {
        particulares--;
        if (particulares == 0) {
            notifyAll();
        }
    }
}

// B
class Autodromo {
    private int kartings = 0;
    private int particulares = 0;
    private int kartingsEsperando = 0;

    private boolean puedeIngresarKarting() {
        return particulares == 0 && kartings == 0;
    }

    private boolean puedeIngresarParticular() {
        return kartings == 0 && kartingsEsperando == 0 && particulares < 20;
    }

    synchronized void ingresarKarting() {
        kartingsEsperando++;
        while (!puedeIngresarKarting()) {
            wait();
        }
        kartingsEsperando--;
        kartings = 1;
    }

    synchronized void salirKarting() {
        kartings = 0;
        notifyAll();
    }

    synchronized void ingresarParticular() {
        while (!puedeIngresarParticular()) {
            wait();
        }
        particulares++;
    }

    synchronized void salirParticular() {
        particulares--;
        if (particulares == 0) {
            notifyAll();
        }

        if (particulares == 19 && kartingsEsperando == 0) {
            notify();
        }
    }
}

// C
class Autodromo {
    private int kartings = 0;
    private int particulares = 0;
    private int kartingsEsperando = 0;
    private int umbral = 50; 

    private boolean puedeIngresarKarting() {
        return particulares == 0 && kartings == 0;
    }

    private boolean puedeIngresarParticular() {
        return kartings == 0 && kartingsEsperando == 0 && particulares < 20;
    }

    synchronized void ingresarKarting(int ranking) {
        if (ranking > umbral) { 
            kartingsEsperando++;
        }

        while (!puedeIngresarKarting()) {
            wait();
        }

        if (ranking > umbral) { 
            kartingsEsperando--;
        }
        kartings = 1;
    }

    synchronized void salirKarting() {
        kartings = 0;
        notifyAll();
    }

    synchronized void ingresarParticular() {
        while (!puedeIngresarParticular()) {
            wait();
        }
        particulares++;
    }

    synchronized void salirParticular() {
        particulares--;
        if (particulares == 0) {
            notifyAll();
        }
        if (particulares == 19 && kartingsEsperando == 0) {
            notify();
        }
    }

}
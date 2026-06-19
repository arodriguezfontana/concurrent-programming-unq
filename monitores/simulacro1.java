//  A
monitor Encoder : {
    private List<Frame> frames = new List<Frame>();

    public void putRawFrame(Frame f) {
        while (frames.size() == M) { 
            wait();
        }

        frames.add(f); 
        if (frames.size() >= P) {
            notifyAll(); 
        }
    }


    public List<Frame> getPack() {
        while (frames.size() < P) { 
            wait();
        }

        List<Frame> pack = new List<Frame>();
        repeat (P) : pack.add(frames.pop());

        notifyAll();
        return pack;
    }
}

// B
monitor Encoder : {
    private List<Frame> frames = new List<Frame>();

    public void putRawFrame(Frame f) {
        while (frames.size() == M) {
            wait(); 
        }
        frames.add(f);
        notifyAll();  


    public List<Frame> getPack(int p) {
        while (frames.size() < p) {
            wait();
        }

        List<Frame> pack = new List<Frame>();
        repeat (p) : pack.add(frames.pop()); 

        notifyAll();
        return pack;
    }
}

// C
monitor Encoder : {
    private List<Frame> frames = new List<Frame>();
    private List<Frame> framesProcesados = new List<Frame>();
    private int getPacks = 0;

    public void putRawFrame(Frame f) {
        while (frames.size() == M) {
            wait(); 
        }
        frames.add(f);
        notifyAll(); 
    }


    public List<Frame> getPack(int p) {
        while (frames.size() < p || getPacks == K) { 
            wait();
        }

        getPacks++;

        List<Frame> pack = new List<Frame>();
        repeat (p) : pack.add(frames.pop()); 

        notifyAll();
        return pack;
    }

    putEncodedPack(encodedPack) {
        repeat (encodedPack.size()) : framesProcesados.add(encodedPack.pop()); 
        getPacks--;
        notifyAll(); 
    }
}
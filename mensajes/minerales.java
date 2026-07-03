package mensajes;
// ======================= A =======================
process Lider(Channel chLider, Channel chArcano) {
    while(true){
        Piedra p = chLider.receive();
        inicializar(p);
        Channel chPiedra = new Channel()
        chArcano.send(p, chPiedra);
        meditar();
        p = chPiedra.receive();
        hechizoFinal(p);
        print("El pentech ha sido creado")
    }
}

process Arcano(Channel chArcano, Channel chLider) {
    while(true){
        (Piedra p, Channel chPiedra) = chArcano.receive();
        transformar(p);
        chPiedra.send(p);
    }
}

// ======================= B ======================= con chanel privado
process Lider(Channel chLider, Channel chArcano) {
    while(true){
        Piedra p = chLider.receive();
        Thread clon = new Thread(Piedra p) {
            inicializar(p);
            Channel chClon = new Channel();
            chArcano.send(p, chClon);
            meditar();
            p = chClon.receive();
            hechizoFinal(p);
            print("El pentech ha sido creado");
        }
        clon.run(p);
    }
}

process Arcano(Channel chArcano, Channel chAprendices) {
    while(true){
        (Piedra p, Channel chClon) = chArcano.receive();
        Channel chAprediz = chAprendices.receive();
        chAprediz.send(p, chClon);
    }
}

process Aprediz(Channel chAprendices) {
    Channel chAprediz = new Channel();
    chAprendices.send(chAprediz);
    (Piedra p, Channel chClon) = chAprediz.receive();
    transformar(p);
    chClon.send(p);
}

// ======================= B ======================= Pero con chanel publico
Channel chPiedraTransformada = new Channel();

process Lider(Channel chLider, Channel chArcano) {
    while(true){
        Piedra p = chLider.receive();
        Thread clon = new Thread(Piedra p, Channel chArcano) {
            inicializar(p);
            chArcano.send(p);
            meditar();
            p = chPiedraTransformada.receive();
            hechizoFinal(p);
            print("El pentech ha sido creado");
        }
        clon.run(p);
    }
}

process Arcano(Channel chArcano, Channel chAprendices) {
    while(true){
        Piedra p = chArcano.receive();
        Channel chAprediz = chAprendices.receive();
        chAprediz.send(p);
    }
}

process Aprediz(Channel chAprendices) {
    Channel chAprediz = new Channel();
    chAprendices.send(chAprediz);
    Piedra p = chAprediz.receive();
    transformar(p);
    chPiedraTransformada.send(p);
}


// ======================= C =======================
process Lider(Channel chLider, Channel chArcano) {
    while(true){
        Piedra p = chLider.receive();
        Thread clon = new Thread(Piedra p) {
            Piedra[] piedras = inicializar(p);
            Channel chClon = new Channel();
            chArcano.send(piedras, chClon);
            meditar();
            Piedra[] piedrasT
            repeat(piedras.size()) {
                p = chClon.receive(); // si no quiero que sea bloqueante tendría que hacer un while(...)
                piedrasT.add(p)
            }
            hechizoFinal(piedrasT);
            print("El pentech ha sido creado");
        }
        clon.run(p);
    }
}

process Arcano(Channel chArcano, Channel chAprendices) {
    while(true){
        (Piedra[] piedras, Channel chClon) = chArcano.receive();
        for p in piedras {
            Channel chAprediz = chAprendices.receive();
            chAprediz.send(p, chClon);
        }
    }
}

process Aprediz(Channel chAprendices) {
    Channel chAprediz = new Channel();
    chAprendices.send(chAprediz);
    (Piedra p, Channel chClon) = chAprediz.receive();
    transformar(p);
    chClon.send(p);
}
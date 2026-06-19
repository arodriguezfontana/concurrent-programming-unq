// A
global Channel cVuelo = new Channel();
global Channel cHotel = new Channel();
global Channel cAuto = new Channel();
global Channel cAgencia = new Channel();

process Agencia {
    while (true) {
        Request req = cAgencia.receive();
        
        Channel cPack = new Channel();

        thread (req, cPack) {
            Channel cVueloRta = new Channel();
            cVuelo.send(req.fecha, cVueloRta);
            bool hayVuelo = cVueloRta.receive();
            cPack.send(hayVuelo);
        }

        thread (req, cPack) {
            Channel cHotelRta = new Channel();
            cHotel.send(req.fecha, cHotelRta);
            bool hayHotel = cHotelRta.receive();
            cPack.send(hayHotel);
        }

        thread (req, cPack) {
            Channel cAutoRta = new Channel();
            cAuto.send(req.fecha, cAutoRta);
            bool hayAuto = cAutoRta.receive();
            cPack.send(hayAuto);
        }

        bool hayPack = true;
        repeat (3) {
            hayPack = hayPack && cPack.receive();
        }

        req.cRespuesta.send(hayPack);
    }
}

// B
global Channel cVuelo = new Channel();
global Channel cHotel = new Channel();
global Channel cAuto = new Channel();
global Channel cAgencia = new Channel();

process Agencia {
    while (true) {
        Request req = cAgencia.receive();

        thread (req) {
            Channel cPack = new Channel();

            thread (req, cPack) {
                Channel cVueloRta = new Channel();
                cVuelo.send(req.fecha, cVueloRta);
                cPack.send(cVueloRta.receive());
            }

            thread (req, cPack) {
                Channel cHotelRta = new Channel();
                cHotel.send(req.fecha, cHotelRta);
                cPack.send(cHotelRta.receive());
            }

            thread (req, cPack) {
                Channel cAutoRta = new Channel();
                cAuto.send(req.fecha, cAutoRta);
                cPack.send(cAutoRta.receive());
            }

            bool hayPack = true;
            repeat (3) {
                hayPack = hayPack && cPack.receive();
            }

            req.cRespuesta.send(hayPack);
        }
    }
}

// C
global List<Channel> cVuelo = new List<Channel>();
global List<Channel> cHotel = new List<Channel>();
global List<Channel> cAuto = new List<Channel>();
global Channel cAgencia = new Channel();

process Agencia {
    while (true) {
        Request req = cAgencia.receive();

        thread (req) {
            Channel cPack = new Channel();

            thread (req, cPack) {
                Channel cVueloRta = new Channel();
                for (Channel v : cVuelo) {
                    v.send(req.fecha, cVueloRta);
                }

                bool hayVuelo = false;
                repeat (cVuelo.size()) {
                    hayVuelo = hayVuelo || cVueloRta.receive();
                }
                cPack.send(hayVuelo);
            }

            thread (req, cPack) {
                Channel cHotelRta = new Channel();
                for (Channel h : cHotel) {
                    h.send(req.fecha, cHotelRta);
                }
                bool hayHotel = false;
                repeat (cHotel.size()) {
                    hayHotel = hayHotel || cHotelRta.receive();
                }
                cPack.send(hayHotel);
            }

            thread (req, cPack) {
                Channel cAutoRta = new Channel();
                for (Channel a : cAuto) {
                    a.send(req.fecha, cAutoRta);
                }
                bool hayAuto = false;
                repeat (cAuto.size()) {
                    hayAuto = hayAuto || cAutoRta.receive();
                }
                cPack.send(hayAuto);
            }

            bool hayPack = true;
            repeat (3) {
                hayPack = hayPack && cPack.receive();
            }

            req.cRespuesta.send(hayPack);
        }
    }
}
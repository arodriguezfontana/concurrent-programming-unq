T0                              T1
//SNC
seleccionTurno(0);
                                //SNC
                                seleccionTurno(1);
                                turno = 1;
turno = 0;
turnoSeleccionado = true;
while(0!=0){...}
//SC
turnoSeleccionado = false;
                                turnoSeleccionado = true;
                                while(0!=1)
                                seleccionTurno(0);
                                if(!turnoSeleccionado)
                                while(0!=1)
                                seleccionTurno(0);
                                if(!turnoSeleccionado)
                                ... deadlock
// Como se observa en la traza, hay un deadlock, por tanto no se cumple GE,
// en conclusión, no se resuelve el problema de la exclusión mutua, en este caso
// para N=2, por tanto no se cumple para todo N.
T0                              T1                             Estado
//SNC
                                //SNC
soyprimero = anotarse(0);
                                soyprimero = anotarse(1);
esperando[0] = True;
                                esperando[1] = True;
lee: cantidadEsperando;
suma: cantidadEsperando+1:;
                                cantidadEsperando++;
escritura: cantidadEsperando;                                  cantidadEsperando=1
return True
                                return True
if (True) llamarProximo();
                                if (True) llamarProximo();
int res;
                                int res;
for i in {N,...,0}
                                for i in {N,...,0}
if (True) res=1;
                                if (True) res=1;
if (True) res=0;
                                if (True) res=0;
proximo=0;
                                proximo=0;                     proximo=0
while(!0==0);
                                while(!0==1);
//SC
soyultimo = desanotarse(0);
esperando[0]=False;
cantidadEsperando--;                                            cantidadEsperando=0
return True;
if (False) llamarProximo();                                     proximo=0

// Como se observa en la traza, hay un deadlock, por lo tanto no se cumple GE, ni EM.
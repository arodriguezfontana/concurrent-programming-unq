actual = -1
colaEntrada = [0]

 T0                            T1                        
 id=0                                                    
                               id=1                      
 //SNC                                                   
                               //SNC                     
 colaEntrada.push(0)                                     
 while(!intentarAcceder(0))                              
 bool puede = True && True                               
                               colaEntrada.push(1)       
                               while(!intentarAcceder(1))
                               bool puede = True && True 
 if (True)                                               
   colaEntrada.pop()                                     
   actual = 0                                            
 //SC                                                    
                               if (True)                 
                                 colaEntrada.pop()       
                                 actual = 1              
                               //SC                      
                               actual = -1               
                               //SNC                     
 actual = -1                                             
 //SNC                                                   

// Como se observa en la traza, dos threads pueden ingresar a SC simultáneamente, por lo tanto no se cumple SC, ni EM.
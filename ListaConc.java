actual = -1
colaEntrada = [0]

+------------------------------+-----------------------------+
| T0                           | T1                          |
+------------------------------+-----------------------------+
| id=0                         |                             |
|                              | id=1                        |
| //SNC                        |                             |
|                              | //SNC                       |
| colaEntrada.push(0)          |                             |
| while(!intentarAcceder(0))   |                             |
| bool puede = True && True    |                             |
|                              | colaEntrada.push(1)         |
|                              | while(!intentarAcceder(1))  |
|                              | bool puede = True && True   |
| if (True)                    |                             |
|   colaEntrada.pop()          |                             |
|   actual = 0                 |                             |
| //SC                         |                             |
|                              | if (True)                   |
|                              |   colaEntrada.pop()         |
|                              |   actual = 1                |
|                              | //SC                        |
--------------------------------------------------------------
|                              | actual = -1                 |
|                              | //SNC                       |
| actual = -1                  |                             |
| //SNC                        |                             |

// Esta propuesta no resuelve el problema de la exclusión mutua para N fijo porque según la traza vista
// dos Threads distintos pueden entrar a SC al mismo tiempo, por tanto se rompe la propiedad Mutex.

// b
// Se cumple mutex, justificación:
// Si hay muchos Threads, eventualmente todos harán push, y solo podrá pasar a SC el último 
// que haya hecho push al momento de intentar acceder
// porque actual=-1 y getLast retorna el id del último que hizo push, por tanto podrá entrar a
//  SC. Ni bien esté dentro, los demás no podrán
// acceder ya que actual estará seteado con el id del thread que haya entrado por tanto no se cumplirá actual==-1.

// Se cumple GE, justificación:
// Cuando uno entra a SC, al salir setea actual en -1, eso permite que todos los que quieran 
// entrar obtengan True al hacer "actual==-1".
// Además, antes de haber entrado a SC, hizo pop de su id, por tanto el que podrá entrar a 
// continuación es el thread que haya hecho push
// antés del thread actual. Lo mismo aplicará a los siguientes.

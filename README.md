## Resumen

El trabajo que recoge este repositorio es una aplicación en java del juego [Sokoban](https://en.wikipedia.org/wiki/Sokoban). El juego cuenta con un [set de 150 niveles](https://github.com/Javi1993/DSI/blob/master/Sokoban/niveles/levels.txt) los cuales van en aumento en su complejidad conforme se va avanzado. En cada nivel se ofrece al usuario la posibilidad de resolver este manualmente o mediante un solver, este último permite resolver el nivel completamente o parcialmente (opción next steps).

El solver desarrollado utiliza búsqueda heurística para encontrar la solución usando una variante del algoritmo A* (AStar) con poda. Este solver recibirá el estado actual del jugador y mediante la expansión sucesiva de los nodos siguiendo una heurística y controlando una serie de restricciones dará con la solución, la cual buscará minimizar en el ratio coste/tiempo. Este coste serán el número de pasos o movimientos realizados por el jugador para completar el nivel.

## Objetivos

El objetivo es el estudio del rendimiento de diferentes algoritmos de búsqueda heurística para la resolución de niveles del juego Sokoban. Para este trabajo se han usado los algortimos A* e IDA* con diferentes funciones de evaluación y ciertas modificaciones en ellos para adaptarlos mejor a la temática del Sokoban.

Los niveles usados contienen entre 2 a 7 cajas a colocar y su complejidad va en aumento conforme se va avanzado. Los resultados de cada uno de los algortimos usados, junto a las diferentes funciones de evaluación consideradas, pueden observarse en el apartado **5. Experimentos realziados** de la [memoria](https://github.com/Javi1993/DSI/blob/master/100290698_100290892_memoria.pdf), también se proporcionan en formato CSV en la [carpeta raíz](https://github.com/Javi1993/DSI/tree/master/Sokoban) del proyecto.

## Instalación

Para usar la aplicación siga el **ANEXO I: Guía de Instalación** de la [memoria](https://github.com/Javi1993/DSI/blob/master/100290698_100290892_memoria.pdf).

En caso de querer contar con la solución de los niveles ya calculada, importe la colección de niveles que desee. Los archivos JSON están en la [carpeta raiz](https://github.com/Javi1993/DSI/tree/master/Sokoban) del proyecto y cada una se diferencia del otro en base a la función de evaluación que ha usado el algoritmo para resolver los niveles.

## Test

A continuación se adjuntan una serie de capturas sobre la interfaz gráfica del juego desarrollado.

![Interfaz_1](http://i65.tinypic.com/30hm2z5.jpg)

![Interfaz_2](http://i68.tinypic.com/whekv6.jpg)

![Interfaz_3](https://s32.postimg.org/hnmecn2c5/20160619_192119_1.gif)

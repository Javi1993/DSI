package motor;

import interfaz.Escenario;

public class Resolver {

	public static void nextStep(/*recibir posicion actual*/)
	{
		
	}
	
	public static char[] solucion(Escenario escenario, int pasos)
	{
		//VER ALGORTIMO X INTERNET
		//SERIA ALGO ASI COMO VER NODOS VECINOS DESDE PSOICION ACTUAL, METERLOS EN UNA LISTA PRIORIZANDO POR
		//F heu (ver pag sokoban) y establecer una profundidad máxima, a partir de ahi buscar
		//ver como obtener H (coste aproximado de acomodar las cajas restantes (pasos))
		//y buscar si existen librerias de A*
		//Actualziando escenario localmente cada movimiento para seguir con algoritmo
		
		Node actual = new Node(escenario, pasos, escenario.placedBox());
	//	System.out.println(escenario.getCajas().size()+" | "+escenario.getDestinos().size());
		System.out.println(actual.getH());
		//obtener nodos vecinos
		
		
		
		//DEBE FUNCIONAR DESDE CUALQUIER POSICION QUE ESTE EL JUGADOR!!
		//ver en base de datos solucion en base a la ID y devolversela
		char[] sol = {'W','A','A','A','W','W','W','A','W','A','A','X'};
		return sol;
	}
}

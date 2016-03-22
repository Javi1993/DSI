package motor;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

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
		AStar(actual);
		//obtener nodos vecinos



		//DEBE FUNCIONAR DESDE CUALQUIER POSICION QUE ESTE EL JUGADOR!!
		//ver en base de datos solucion en base a la ID y devolversela
		char[] sol = {'W','A','A','A','W','W','W','A','W','A','A','X'};
		return sol;
	}

	private static void AStar(Node actual)
	{
		Comparator<Node> comparator = new MyComparator();
		PriorityQueue<Node> abiertos = new PriorityQueue<Node>(comparator);//cola de prioridades con nodos a estudiar
		Queue<Node> cerrados = new LinkedList<Node>();//cola con nodos ya estudiados
		abiertos.add(actual);

		while (!abiertos.isEmpty()) {
			System.out.println("HOLA");
			System.out.println(abiertos.poll().getF());
		}
		System.out.println("HE SALIDO");
	}
}

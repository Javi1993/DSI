package motor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import interfaz.Escenario;

public class Resolver {

	public static void nextStep(/*recibir posicion actual y avanzar un poco*/)
	{

	}

	public static char[] solucion(Escenario escenario, int pasos)
	{
		Node actual = new Node(escenario, pasos, escenario.placedBox(), "");//nodo actual del usuario
		String secuencia = AStar(actual);//buscamos la solucion
		if(secuencia!=null)
		{
			char[] sol = new char[secuencia.length()];//secuencia de teclas
			for(int i=0;i<secuencia.length(); i++)
			{
				sol[i]=secuencia.charAt(i);
			}
			return sol;
		}else{
			return null;
		}
		//meter solucion base de datos, en este metodo comprobar antes de clacular si ya esta insertaa

	}

	private static String AStar(Node actual)
	{
		Comparator<Node> comparator = new MyComparator();
		PriorityQueue<Node> abiertos = new PriorityQueue<Node>(comparator);//cola de prioridades con nodos a estudiar
		List<Node> cerrados = new ArrayList<Node>();//lista con nodos ya estudiados
		abiertos.add(actual);

		while (!abiertos.isEmpty()) {
			Node estudiando = abiertos.poll();

			//			//-----------------------------------------------------
			//
			//			System.out.println("TECLA USADA PARA LLEGAR A: "+estudiando.getTecla());
			//			for(int v = 0; v<estudiando.getEscenario().cas.length; v++)
			//			{
			//				for(int w=0; w<estudiando.getEscenario().cas[v].length; w++)
			//				{
			//					System.out.print(estudiando.getEscenario().cas[v][w]+";");
			//				}
			//				System.out.println();
			//			}
			//			//------------------------------------------------------ FALLA QUE AL METER LOS HIJOS DEL NODO
			//			//ESTUDIADO LOS MEZCLA CON LOS OTROS Y SE LIA PARDA PARA LA SECUENCIA

			if(estudiando.getEscenario().hasGanado())
			{//Existe solucion, salimos
				return estudiando.getID();
			}else{
				cerrados.add(estudiando);
				List<Node> hijos = getHijos(estudiando);
				if(!hijos.isEmpty())
				{//se pueden hacer movimientos desde este nodo, los añadimos a la cola
					for(Node h:hijos)
					{
						if(!yaEstudiado(h, cerrados)){//comprobamos si ese nodo fue ya estudiado
							abiertos.add(h);
						}
					}
				}
			}
		}
		return null;
	}

	private static List<Node> getHijos(Node padre)
	{
		List<Node> hijos = new ArrayList<>();
		int i = 0;
		while(i<4)
		{//maximo 4 movimientos a realizar (4 hijos posibles)
			Escenario test = new Escenario();
			copiarEscenarioActual(test, padre);
			//Node aux = new Node(test, padre.getG(), padre.getI(),' ');
			switch (i) {
			case 0://movimiento hacia arriba
				if(test.realizarMovimiento('W'))
				{
					hijos.add(new Node(test, padre.getG()+1, test.placedBox(), padre.getID()+"W"));
					//					System.out.println("SI W");
				}
				break;
			case 1://movimiento a la izquierda
				if(test.realizarMovimiento('A'))
				{
					hijos.add(new Node(test, padre.getG()+1, test.placedBox(),padre.getID()+"A"));
					//					System.out.println("SI A");
				}
				break;
			case 2://movimiento a la derecha
				if(test.realizarMovimiento('D'))
				{
					hijos.add(new Node(test, padre.getG()+1, test.placedBox(),padre.getID()+"D"));
					//					System.out.println("SI D");
				}
				break;
			default://movimiento hacia abajo
				if(test.realizarMovimiento('X'))
				{
					hijos.add(new Node(test, padre.getG()+1, test.placedBox(),padre.getID()+"X"));
					//					System.out.println("SI X");
				}
				break;
			}
			i++;
		}
		return hijos;
	}

	private static void copiarEscenarioActual(Escenario test, Node padre)
	{
		char[][] auxEsce = new char[padre.getEscenario().getALTO()][padre.getEscenario().getANCHO()+1];
		for (int j = 0; j < auxEsce.length; j++) {
			System.arraycopy(padre.getEscenario().cas[j], 0, auxEsce[j], 0, padre.getEscenario().cas[0].length);
		}
		test.cas = auxEsce.clone();
	}

	private static boolean yaEstudiado(Node hijo, List<Node> cerrados)
	{//si el escenario es el mismo (cajas misma posicion e igual i(x) y h(x) se ha estudiado ese nodo)
		for(Node comparar : cerrados)
		{//comparamos con los ya existentes
			if((comparar.getH()==hijo.getH())&&(comparar.getI()==hijo.getI())
					&&(Arrays.deepEquals(hijo.getEscenario().cas, comparar.getEscenario().cas))){
				return true;
			}
		}
		return false;	
	}
}

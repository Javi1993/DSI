package motor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import interfaz.Escenario;
import interfaz.Posicion;
import jugador.Player;

public class Resolver {
	
	private static MongoClient client;
	private static MongoDatabase database;
	private static MongoCollection<Document> collection;  
	
	public static void nextStep(/*recibir posicion actual y avanzar un poco*/)
	{

	}

	public static char[] solucion(Escenario escenario, int pasos)
	{
		Node actual = new Node(escenario, pasos, escenario.placedBox(), "");//nodo actual del usuario
		escenario.setIA(true);//se ha usado IA
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
			if(estudiando.getEscenario().hasGanado())
			{//Existe solucion, salimos
				return estudiando.getID();
			}else{
				cerrados.add(estudiando);
				List<Node> hijos = getHijos(estudiando);
				if(!hijos.isEmpty())
				{//se pueden hacer movimientos desde este nodo, los a�adimos a la cola
					for(Node h:hijos)
					{
						if(!yaEstudiado(h, cerrados)){//comprobamos si ese nodo fue ya estudiado
							abiertos.add(h);
						}
					}
				}
			}
		}
		System.out.println("Se han estudiado "+cerrados.size()+" nodos");

		//ELIMIANR 1 a 1 las CAJAS HASTA VER CUAL DA EL FALLO!


		return null;
	}

	private static List<Node> getHijos(Node padre)
	{
		List<Node> hijos = new ArrayList<>();
		int i = 0;
		while(i<4)
		{//maximo 4 movimientos a realizar (4 hijos posibles)
			Node aux = null;
			Escenario test = new Escenario(padre.getEscenario().getNivel(), true);
			copiarEscenarioActual(test, padre);
			test.setALTO(test.getCas().length);
			test.setANCHO(test.getCas()[0].length-1);
			switch (i) {
			case 0://movimiento hacia arriba
				if(test.realizarMovimiento('W'))
				{
					aux = new Node(test, padre.getG()+1, test.placedBox(),padre.getID()+"W");
					if(comprobarEsquina(aux)/*&&comprobarBloques(aux)*/)
					{
						hijos.add(aux);
						//						System.out.println("SI W");
					}
				}
				break;
			case 1://movimiento a la izquierda
				if(test.realizarMovimiento('A'))
				{
					aux = new Node(test, padre.getG()+1, test.placedBox(),padre.getID()+"A");
					if(comprobarEsquina(aux)/*&&comprobarBloques(aux)*/)
					{
						hijos.add(aux);
						//						System.out.println("SI A");
					}
				}
				break;
			case 2://movimiento a la derecha
				if(test.realizarMovimiento('D'))
				{
					aux = new Node(test, padre.getG()+1, test.placedBox(),padre.getID()+"D");
					if(comprobarEsquina(aux)/*&&comprobarBloques(aux)*/)
					{
						hijos.add(aux);
						//						System.out.println("SI D");
					}
				}
				break;
			default://movimiento hacia abajo
				if(test.realizarMovimiento('X'))
				{
					aux = new Node(test, padre.getG()+1, test.placedBox(),padre.getID()+"X");
					if(comprobarEsquina(aux)/*&&comprobarBloques(aux)*/)
					{
						hijos.add(aux);
						//						System.out.println("SI X");
					}
				}
				break;
			}
			i++;
		}
		return hijos;
	}

	private static boolean comprobarEsquina(Node test)
	{
		List<Posicion> cajasSinColocar = test.getEscenario().getCajas();//obtenemos la posicion de las cajas sin colocar
		if(!cajasSinColocar.isEmpty())
		{
			for (Posicion posicion : cajasSinColocar) {
				if((test.getEscenario().getCas()[posicion.x-1][posicion.y]=='#'||test.getEscenario().getCas()[posicion.x+1][posicion.y]=='#')
						&&(test.getEscenario().getCas()[posicion.x][posicion.y-1]=='#'||test.getEscenario().getCas()[posicion.x][posicion.y+1]=='#')
						&&(test.getEscenario().getCas()[posicion.x][posicion.y]!='*'))
				{//se quiere mover a una esquina que deja caja bloqueada
					//					System.out.println("ESQUINA_A");
					return false;
				}
			}
		}
		return true;
	}

	private static boolean comprobarBloques(Node test)
	{
		List<Posicion> cajasSinColocar = test.getEscenario().getCajas();//obtenemos la posicion de las cajas sin colocar
		if(!cajasSinColocar.isEmpty())
		{
			for (Posicion posicion : cajasSinColocar) {
				if((test.getEscenario().getCas()[posicion.x-1][posicion.y]=='$'||test.getEscenario().getCas()[posicion.x+1][posicion.y]=='$')
						&&(test.getEscenario().getCas()[posicion.x][posicion.y-1]=='$'||test.getEscenario().getCas()[posicion.x][posicion.y+1]=='$')
						&&(test.getEscenario().getCas()[posicion.x][posicion.y]!='*'))
				{//se quiere mover a una esquina de cajas que deja caja bloqueada
					//					System.out.println("BLOQUE_A");
					return false;
				}else if((test.getEscenario().getCas()[posicion.x-2][posicion.y]=='$'||test.getEscenario().getCas()[posicion.x+2][posicion.y]=='$')
						&&(test.getEscenario().getCas()[posicion.x][posicion.y-1]=='$'||test.getEscenario().getCas()[posicion.x][posicion.y+1]=='$')
						&&(test.getEscenario().getCas()[posicion.x-1][posicion.y+1]=='$'||test.getEscenario().getCas()[posicion.x+1][posicion.y-1]=='$'
						||test.getEscenario().getCas()[posicion.x+1][posicion.y+1]=='$'||test.getEscenario().getCas()[posicion.x-1][posicion.y-1]=='$')
						&&(test.getEscenario().getCas()[posicion.x][posicion.y]!='*'))
				{//se quiere mover a una posicion que forma bloque de 3x3 cajas con centro vacio
					//					System.out.println("BLOQUE_B");
					return false;
				}else if((test.getEscenario().getCas()[posicion.x-1][posicion.y]=='$'||test.getEscenario().getCas()[posicion.x+1][posicion.y]=='$')
						&&(test.getEscenario().getCas()[posicion.x][posicion.y-2]=='$'||test.getEscenario().getCas()[posicion.x][posicion.y+2]=='$')
						&&(test.getEscenario().getCas()[posicion.x-1][posicion.y+1]=='$'||test.getEscenario().getCas()[posicion.x+1][posicion.y-1]=='$'
						||test.getEscenario().getCas()[posicion.x+1][posicion.y+1]=='$'||test.getEscenario().getCas()[posicion.x-1][posicion.y-1]=='$')
						&&(test.getEscenario().getCas()[posicion.x][posicion.y]!='*'))
				{//se quiere mover a una posicion que forma bloque de 3x3 cajas con centro vacio
					//					System.out.println("BLOQUE_C");
					return false;
				}
			}
		}
		return true;
	}

	private static void copiarEscenarioActual(Escenario test, Node padre)
	{
		char[][] auxEsce = new char[padre.getEscenario().getALTO()][padre.getEscenario().getANCHO()+1];
		for (int j = 0; j < auxEsce.length; j++) {
			System.arraycopy(padre.getEscenario().getCas()[j], 0, auxEsce[j], 0, padre.getEscenario().getCas()[0].length);
		}
		test.setCas(auxEsce);
	}

	private static boolean yaEstudiado(Node hijo, List<Node> cerrados)
	{//si el escenario es el mismo (cajas misma posicion e igual i(x) y h(x) se ha estudiado ese nodo)
		for(Node comparar : cerrados)
		{//comparamos con los ya existentes
			if((comparar.getH()==hijo.getH())&&(comparar.getI()==hijo.getI())
					&&(Arrays.deepEquals(hijo.getEscenario().getCas(), comparar.getEscenario().getCas()))){
				return true;
			}
		}
		return false;	
	}
}

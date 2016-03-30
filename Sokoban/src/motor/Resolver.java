package motor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import interfaz.Escenario;
import interfaz.Posicion;
import jugador.Mapas;

public class Resolver {

	private static String solIDA;
	
	public static void nextStep(/*recibir posicion actual y avanzar un poco*/)
	{

	}

	public static char[] solucion(Escenario escenario, int pasos)
	{
		/*
		 * 
		 * GENERAR ESCENARIOS PARCIALES EN BASE
		 * A SOLUCION DE SEQ DEVUELTA Y AL NODO INICIAL!!
		 * 
		 */
		Node actual = new Node(escenario, pasos, escenario.placedBox(), "");//nodo actual del usuario
		escenario.setIA(true);//se ha usado IA
		char[] solExist = Mapas.verSol(escenario.getNivel());
		if(solExist!=null)
		{//ya hay una solución almacenada de la IA
			return solExist;
		}else{//no hay solucion, la calculamos
			long time_start, time_end;
			time_start = System.currentTimeMillis();
			String secuencia = AStar(actual);//buscamos la solucion
			time_end = System.currentTimeMillis();
			//		String secuencia = IDAStar(actual);//buscamos la solucion con IDA*
			long time = time_end - time_start;
			if(secuencia!=null)
			{
				char[] sol = new char[secuencia.length()];//secuencia de teclas
				for(int i=0;i<secuencia.length(); i++)
				{
					sol[i]=secuencia.charAt(i);
				}
				List<String> aux = new ArrayList<String>();
				for(int i = 0; i<sol.length; i++ )
				{
					aux.add(String.valueOf(sol[i]));
				}
				escenario.updateNivel(aux, null, time);
				return sol;
			}else{
				return null;
			}
		}
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
				List<Node> hijos = getHijos(estudiando);
				cerrados.add(estudiando);
				if(!hijos.isEmpty())
				{//se pueden hacer movimientos desde este nodo, los anadimos a la cola
					for(Node h:hijos)
					{
						if(!yaEstudiado(h, cerrados)&&!yaEnCola(h, abiertos))
						{//comprobamos si ese nodo fue ya estudiado o ya esta a la espera en cola
							abiertos.add(h);
						}
					}
				}
			}
			//						System.out.println("TAMAÑO: " +abiertos.size());
			//			imprimirCola(abiertos);
		}
		//		System.out.println("Se han estudiado "+cerrados.size()+" nodos");
		//		imprimirColaDos(cerrados);
		return null;
	}

	private static String IDAStar(Node actual)
	{
		int bound = actual.getF();
		while (true) {
			List<Node> listRepetido = new ArrayList<Node>();
			listRepetido.add(actual);
			int aux = IDAStarSearch(actual, bound, listRepetido);
			if(aux==0)
			{
				return solIDA;
			}else if(aux==2147483647){
				return null;
			}
			else{
				bound = aux;
			}
		}
	}

	private static int IDAStarSearch(Node actual, int bound, List<Node> listRepetido)
	{
		int min = 0;
		if(actual.getF()>bound){
			return actual.getF();
		}else if(actual.getEscenario().hasGanado()){
			solIDA=actual.getID();
			return 0;
		}else{
			min = 2147483647;
			List<Node> hijos = getHijos(actual);
			for (Node node : hijos) 
			{
				if(!yaEstudiado(node, listRepetido))
				{
					listRepetido.add(node);
					int aux = IDAStarSearch(node, bound, listRepetido);
					if(aux==0){return 0;}
					if(aux<min){min = aux;}
				}
			}
		}
		return min;
	}

	//	private static void imprimirCola(PriorityQueue<Node> cola)
	//	{
	//		for (Node node : cola) {
	//			System.out.println("------------------------------");
	//			for(int i = 0; i<node.getEscenario().getCas().length; i++)
	//			{
	//				for(int j = 0; j<node.getEscenario().getCas()[i].length; j++){
	//					System.out.print(node.getEscenario().getCas()[i][j]);
	//				}
	//				System.out.println();
	//			}
	//		}
	//	}

	//	private static void imprimirColaDos(List<Node> cola)
	//	{
	//		for (Node node : cola) {
	//			System.out.println("------------------------------");
	//			for(int i = 0; i<node.getEscenario().getCas().length; i++)
	//			{
	//				for(int j = 0; j<node.getEscenario().getCas()[i].length; j++){
	//					System.out.print(node.getEscenario().getCas()[i][j]);
	//				}
	//				System.out.println();
	//			}
	//		}
	//	}

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
					if(comprobarEsquina(aux)&&comprobarBloques(aux))
					{
						hijos.add(aux);
					}
				}
				break;
			case 1://movimiento a la izquierda
				if(test.realizarMovimiento('A'))
				{
					aux = new Node(test, padre.getG()+1, test.placedBox(),padre.getID()+"A");
					if(comprobarEsquina(aux)&&comprobarBloques(aux))
					{
						hijos.add(aux);
					}
				}
				break;
			case 2://movimiento a la derecha
				if(test.realizarMovimiento('D'))
				{
					aux = new Node(test, padre.getG()+1, test.placedBox(),padre.getID()+"D");
					if(comprobarEsquina(aux)&&comprobarBloques(aux))
					{
						hijos.add(aux);
					}
				}
				break;
			default://movimiento hacia abajo
				if(test.realizarMovimiento('X'))
				{
					aux = new Node(test, padre.getG()+1, test.placedBox(),padre.getID()+"X");
					if(comprobarEsquina(aux)&&comprobarBloques(aux))
					{
						hijos.add(aux);
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
				{//se quiere mover a una esquina que deja la caja bloqueada
					//					System.out.println("ESQUINA");
					//					System.out.println("------------------------------");
					//					for(int i = 0; i<test.getEscenario().getCas().length; i++)
					//					{
					//						for(int j = 0; j<test.getEscenario().getCas()[i].length; j++){
					//							System.out.print(test.getEscenario().getCas()[i][j]);
					//						}
					//						System.out.println();
					//					}
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
				//comprobamos bloques 2x2
				if(esUnBloque2x2(test.getEscenario(), posicion))
				{//se quiere mover a una esquina que deja la caja bloqueada
					//					System.out.println("BLOQUE");
					//					System.out.println("------------------------------");
					//					for(int i = 0; i<test.getEscenario().getCas().length; i++)
					//					{
					//						for(int j = 0; j<test.getEscenario().getCas()[i].length; j++){
					//							System.out.print(test.getEscenario().getCas()[i][j]);
					//						}
					//						System.out.println();
					//					}
					return false;
				}
				//				if(esUnBloque3x3(test.getEscenario(), posicion)){
				//					return false;
				//				}
			}
		}
		return true;
	}

	//	private static boolean esUnBloque3x3(Escenario test, Posicion caja) {
	//		//creamos el escenario 3x3 que rodea a nuestra caja
	//		char[][] aux1 = new char[3][3];//caja en 0,0
	//		aux1[0][0]=test.getCas()[caja.x][caja.y];
	//		aux1[0][1]=test.getCas()[caja.x][caja.y+1];
	//		aux1[0][2]=test.getCas()[caja.x][caja.y+2];
	//		aux1[1][0]=test.getCas()[caja.x+1][caja.y];
	//		aux1[1][1]=test.getCas()[caja.x+1][caja.y+1];
	//		aux1[1][2]=test.getCas()[caja.x+1][caja.y+2];
	//		aux1[2][0]=test.getCas()[caja.x+2][caja.y];
	//		aux1[2][1]=test.getCas()[caja.x+2][caja.y+1];
	//		aux1[2][2]=test.getCas()[caja.x+2][caja.y+2];
	//
	//		char[][] aux2 = new char[3][3];//caja en 0,1
	//		aux1[0][0]=test.getCas()[caja.x][caja.y-1];
	//		aux1[0][1]=test.getCas()[caja.x][caja.y];
	//		aux1[0][2]=test.getCas()[caja.x][caja.y+1];
	//		aux1[1][0]=test.getCas()[caja.x+1][caja.y-1];
	//		aux1[1][1]=test.getCas()[caja.x+1][caja.y];
	//		aux1[1][2]=test.getCas()[caja.x+1][caja.y+1];
	//		aux1[2][0]=test.getCas()[caja.x+2][caja.y-1];
	//		aux1[2][1]=test.getCas()[caja.x+2][caja.y];
	//		aux1[2][2]=test.getCas()[caja.x+2][caja.y+1];
	//
	//		char[][] aux3 = new char[3][3];//caja en 0,2
	//		aux1[0][0]=test.getCas()[caja.x][caja.y-2];
	//		aux1[0][1]=test.getCas()[caja.x][caja.y-1];
	//		aux1[0][2]=test.getCas()[caja.x][caja.y];
	//		aux1[1][0]=test.getCas()[caja.x+1][caja.y-2];
	//		aux1[1][1]=test.getCas()[caja.x+1][caja.y-1];
	//		aux1[1][2]=test.getCas()[caja.x+1][caja.y];
	//		aux1[2][0]=test.getCas()[caja.x+2][caja.y-2];
	//		aux1[2][1]=test.getCas()[caja.x+2][caja.y-1];
	//		aux1[2][2]=test.getCas()[caja.x+2][caja.y];
	//
	//		char[][] aux4 = new char[3][3];//caja en 1,0
	//		aux1[0][0]=test.getCas()[caja.x-1][caja.y];
	//		aux1[0][1]=test.getCas()[caja.x-1][caja.y+1];
	//		aux1[0][2]=test.getCas()[caja.x-1][caja.y+2];
	//		aux1[1][0]=test.getCas()[caja.x][caja.y];
	//		aux1[1][1]=test.getCas()[caja.x][caja.y+1];
	//		aux1[1][2]=test.getCas()[caja.x][caja.y+2];
	//		aux1[2][0]=test.getCas()[caja.x+1][caja.y];
	//		aux1[2][1]=test.getCas()[caja.x+1][caja.y+1];
	//		aux1[2][2]=test.getCas()[caja.x+1][caja.y+2];
	//
	//		char[][] aux5 = new char[3][3];//caja en 1,1
	//		aux1[0][0]=test.getCas()[caja.x-1][caja.y-1];
	//		aux1[0][1]=test.getCas()[caja.x-1][caja.y];
	//		aux1[0][2]=test.getCas()[caja.x-1][caja.y+1];
	//		aux1[1][0]=test.getCas()[caja.x][caja.y-1];
	//		aux1[1][1]=test.getCas()[caja.x][caja.y];
	//		aux1[1][2]=test.getCas()[caja.x][caja.y+1];
	//		aux1[2][0]=test.getCas()[caja.x+1][caja.y-1];
	//		aux1[2][1]=test.getCas()[caja.x+1][caja.y];
	//		aux1[2][2]=test.getCas()[caja.x+1][caja.y+1];
	//
	//		char[][] aux6 = new char[3][3];//caja en 1,2
	//		aux1[0][0]=test.getCas()[caja.x-1][caja.y-2];
	//		aux1[0][1]=test.getCas()[caja.x-1][caja.y-1];
	//		aux1[0][2]=test.getCas()[caja.x-1][caja.y];
	//		aux1[1][0]=test.getCas()[caja.x][caja.y-2];
	//		aux1[1][1]=test.getCas()[caja.x][caja.y-1];
	//		aux1[1][2]=test.getCas()[caja.x][caja.y];
	//		aux1[2][0]=test.getCas()[caja.x+1][caja.y-2];
	//		aux1[2][1]=test.getCas()[caja.x+1][caja.y-1];
	//		aux1[2][2]=test.getCas()[caja.x+1][caja.y];
	//
	//		char[][] aux7 = new char[3][3];//caja en 2,0
	//		aux1[0][0]=test.getCas()[caja.x-2][caja.y];
	//		aux1[0][1]=test.getCas()[caja.x-2][caja.y+1];
	//		aux1[0][2]=test.getCas()[caja.x-2][caja.y+2];
	//		aux1[1][0]=test.getCas()[caja.x-1][caja.y];
	//		aux1[1][1]=test.getCas()[caja.x-1][caja.y+1];
	//		aux1[1][2]=test.getCas()[caja.x-1][caja.y+2];
	//		aux1[2][0]=test.getCas()[caja.x][caja.y];
	//		aux1[2][1]=test.getCas()[caja.x][caja.y+1];
	//		aux1[2][2]=test.getCas()[caja.x][caja.y+2];
	//
	//		char[][] aux8 = new char[3][3];//caja en 2,1
	//		aux1[0][0]=test.getCas()[caja.x-2][caja.y-1];
	//		aux1[0][1]=test.getCas()[caja.x-2][caja.y];
	//		aux1[0][2]=test.getCas()[caja.x-2][caja.y+1];
	//		aux1[1][0]=test.getCas()[caja.x-1][caja.y-1];
	//		aux1[1][1]=test.getCas()[caja.x-1][caja.y];
	//		aux1[1][2]=test.getCas()[caja.x-1][caja.y+1];
	//		aux1[2][0]=test.getCas()[caja.x][caja.y-1];
	//		aux1[2][1]=test.getCas()[caja.x][caja.y];
	//		aux1[2][2]=test.getCas()[caja.x][caja.y+1];
	//
	//		char[][] aux9 = new char[3][3];//caja en 2,2
	//		aux1[0][0]=test.getCas()[caja.x-2][caja.y-2];
	//		aux1[0][1]=test.getCas()[caja.x-2][caja.y-1];
	//		aux1[0][2]=test.getCas()[caja.x-2][caja.y];
	//		aux1[1][0]=test.getCas()[caja.x-1][caja.y-2];
	//		aux1[1][1]=test.getCas()[caja.x-1][caja.y-1];
	//		aux1[1][2]=test.getCas()[caja.x-1][caja.y];
	//		aux1[2][0]=test.getCas()[caja.x][caja.y-2];
	//		aux1[2][1]=test.getCas()[caja.x][caja.y-1];
	//		aux1[2][2]=test.getCas()[caja.x][caja.y];
	//
	//		if(testBloque3x3(aux1)||testBloque3x3(aux2)||testBloque3x3(aux3)||testBloque3x3(aux4)
	//				||testBloque3x3(aux5)||testBloque3x3(aux6)||testBloque3x3(aux7)
	//				||testBloque3x3(aux8)||testBloque3x3(aux9))
	//		{//vemos si alguno forma bloque 3x3
	//			//								System.out.println("------------------------------");
	//			//								for(int i = 0; i<test.getCas().length; i++)
	//			//								{
	//			//									for(int j = 0; j<test.getCas()[i].length; j++){
	//			//										System.out.print(test.getCas()[i][j]);
	//			//									}
	//			//									System.out.println();
	//			//								}
	//			return true;
	//		}
	//
	//		return false;
	//	}

	private static boolean esUnBloque2x2 (Escenario test, Posicion caja){
		//creamos el escenario 2x2 que rodea a nuestra caja
		char[][] aux1 = new char[2][2];//caja en 0,0
		aux1[0][0]=test.getCas()[caja.x][caja.y];
		aux1[0][1]=test.getCas()[caja.x][caja.y+1];
		aux1[1][0]=test.getCas()[caja.x+1][caja.y];
		aux1[1][1]=test.getCas()[caja.x+1][caja.y+1];
		char[][] aux2 = new char[2][2];//caja en 0,1
		aux2[0][0]=test.getCas()[caja.x][caja.y-1];
		aux2[0][1]=test.getCas()[caja.x][caja.y];
		aux2[1][0]=test.getCas()[caja.x+1][caja.y-1];
		aux2[1][1]=test.getCas()[caja.x+1][caja.y];
		char[][] aux3 = new char[2][2];//caja en 1,1
		aux3[0][0]=test.getCas()[caja.x-1][caja.y-1];
		aux3[0][1]=test.getCas()[caja.x-1][caja.y];
		aux3[1][0]=test.getCas()[caja.x][caja.y-1];
		aux3[1][1]=test.getCas()[caja.x][caja.y];
		char[][] aux4 = new char[2][2];//caja en 1,0
		aux4[0][0]=test.getCas()[caja.x-1][caja.y];
		aux4[0][1]=test.getCas()[caja.x-1][caja.y+1];
		aux4[1][0]=test.getCas()[caja.x][caja.y];
		aux4[1][1]=test.getCas()[caja.x][caja.y+1];

		if(testBloque2x2(aux1)||testBloque2x2(aux2)||testBloque2x2(aux3)||testBloque2x2(aux4))
		{//vemos si alguno forma bloque
			return true;
		}
		return false;
	}

	private static boolean testBloque2x2(char[][] aux1) {
		int cntB=0;//contador cajas
		int cntM=0;//contador muros
		for(int i=0;i<aux1.length; i++)
		{
			for(int j = 0;j<aux1[i].length; j++)
			{
				if(aux1[i][j]=='#'){
					cntM++;
				}
				if(aux1[i][j]=='$'){
					cntB++;
				}
			}
		}
		if(cntB+cntM==4)
		{
			return true;
		}else{
			return false;
		}
	}

	//	private static boolean testBloque3x3(char[][] aux1) {
	//		int cntB=0;//contador cajas
	//		int cntM=0;//contador muros
	//		boolean centro = false;//centro vacio
	//		if(aux1[1][1]==' '){centro = true;}
	//		for(int i=0;i<aux1.length; i++)
	//		{
	//			for(int j = 0;j<aux1[i].length; j++)
	//			{
	//				if(aux1[i][j]=='#'){
	//					cntM++;
	//				}
	//				if(aux1[i][j]=='$'){
	//					cntB++;
	//				}
	//			}
	//		}
	//		if(cntB+cntM==9)
	//		{//bloque de 3x3 solido
	//			return true;
	//		}
	//		if(cntB+cntM==8&&centro)
	//		{//bloque de 3x3 con centro vacï¿½o se revelan nuevas posiciones sin soluciï¿½n
	//			return true;
	//		}else if(cntB+cntM>=6&&cntB+cntM<=7&&centro)
	//		{//bloque de 3x3 con centro vacï¿½o con 1 ï¿½ 2 esquinas opuestas vacï¿½as produce una posiciï¿½n muerta 
	//			if((aux1[0][0]==' '&&aux1[2][2]==' ')||(aux1[0][2]==' '&&aux1[2][0]==' ')){
	//				return true;
	//			}else if(esquinaOpuestaVacia(aux1)){
	//				return true;
	//			}
	//		}
	//		return false;
	//	}

	//	private static boolean esquinaOpuestaVacia(char[][] aux1)
	//	{
	//		char esquina1 = aux1[0][0];
	//		char esquina2 = aux1[0][2];
	//		char esquina3 = aux1[2][0];
	//		char esquina4 = aux1[2][2];
	//
	//		if(esquina1==' '&&(esquina2=='$'||esquina2=='#'||esquina2=='*')
	//				&&(esquina3=='$'||esquina3=='#'||esquina3=='*')
	//				&&(esquina4=='$'||esquina4=='#'||esquina4=='*'))
	//		{
	//			return true;
	//		}
	//		if(esquina2==' '&&(esquina1=='$'||esquina1=='#'||esquina1=='*')
	//				&&(esquina3=='$'||esquina3=='#'||esquina3=='*')
	//				&&(esquina4=='$'||esquina4=='#'||esquina4=='*'))
	//		{
	//			return true;
	//		}
	//		if(esquina3==' '&&(esquina2=='$'||esquina2=='#'||esquina2=='*')
	//				&&(esquina1=='$'||esquina1=='#'||esquina1=='*')
	//				&&(esquina4=='$'||esquina4=='#'||esquina4=='*'))
	//		{
	//			return true;
	//		}
	//		if(esquina4==' '&&(esquina2=='$'||esquina2=='#'||esquina2=='*')
	//				&&(esquina3=='$'||esquina3=='#'||esquina3=='*')
	//				&&(esquina1=='$'||esquina1=='#'||esquina1=='*'))
	//		{
	//			return true;
	//		}
	//		return false;
	//	}

	private static void copiarEscenarioActual(Escenario test, Node padre)
	{
		char[][] auxEsce = new char[padre.getEscenario().getALTO()][padre.getEscenario().getANCHO()+1];
		for (int j = 0; j < auxEsce.length; j++) {
			System.arraycopy(padre.getEscenario().getCas()[j], 0, auxEsce[j], 0, padre.getEscenario().getCas()[0].length);
		}
		test.setCas(auxEsce);
	}

	private static boolean yaEnCola(Node hijo, PriorityQueue<Node> abiertos)
	{//si el escenario es el mismo
		for(Node comparar : abiertos)
		{//comparamos con los ya existentes
			if(Arrays.deepEquals(hijo.getEscenario().getCas(), comparar.getEscenario().getCas())){
				return true;
			}
		}
		return false;	
	}

	private static boolean yaEstudiado(Node hijo, List<Node> cerrados)
	{//si el escenario es el mismo
		for(Node comparar : cerrados)
		{//comparamos con los ya existentes
			if(Arrays.deepEquals(hijo.getEscenario().getCas(), comparar.getEscenario().getCas())){
				return true;
			}
		}
		return false;	
	}
}

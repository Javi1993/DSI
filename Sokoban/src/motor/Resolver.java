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
	private static int nodosTotal;

	public static void nextStep(/*recibir posicion actual y avanzar un poco*/)
	{

	}

	public static char[] solucion(Escenario escenario, int pasos)
	{
		Node actual = new Node(escenario, pasos, escenario.placedBox(), "");//nodo actual del usuario
		escenario.setIA(true);//se ha usado IA
		String tipe="AStar";
		//		String tipe="IDAStar";
		char[] solExist = Mapas.verSol(escenario.getNivel(), tipe);
		if(solExist!=null && pasos==0)
		{//ya hay una soluci�n almacenada de la IA
			return solExist;
		}else if(solExist!=null && pasos!=0)
		{//hay solucion pero el jugador se encuentra en una posicion distinta de la inicial
			Escenario test = new Escenario(actual.getEscenario().getNivel(), false);
			int posAux = 0;
			for (char c : solExist) {//buscamos si esa posicion es un paso intermedio de la solucion
				if(Arrays.deepEquals(test.getCas(), escenario.getCas()))
				{//existe solucion guardada desde la posicion actual del usuario
					char[] solAux = new char[solExist.length-posAux];
					for(int i = 0; i<solAux.length; i++){
						solAux[i] = solExist[i+posAux];
					}
					return solAux;//devolvemos la solucion modificada
				}else{//avanzamos
					test.realizarMovimiento(c);
					posAux++;
				}
			}
		}
		//no hay solucion, la calculamos
		long time_start, time_end;
		time_start = System.currentTimeMillis();
		String secuencia = AStar(actual);//buscamos la solucion con A*
		//			String secuencia = IDAStar(actual);//buscamos la solucion con IDA*
		time_end = System.currentTimeMillis();
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
			Escenario test = new Escenario(actual.getEscenario().getNivel(), true);
			copiarEscenarioActual(test, actual.getEscenario());
			escenario.updateNivel(aux, null, time, test, tipe, nodosTotal);
			return sol;
		}else{//no hay solucion para el nivel en el estado actual
			return null;
		}
	}

	private static String AStar(Node actual)
	{
		nodosTotal = 0;
		Comparator<Node> comparator = new MyComparator();
		PriorityQueue<Node> abiertos = new PriorityQueue<Node>(comparator);//cola de prioridades con nodos a estudiar
		List<Node> cerrados = new ArrayList<Node>();//lista con nodos ya estudiados
		abiertos.add(actual);
		while (!abiertos.isEmpty()) {
			Node estudiando = abiertos.poll();
			if(estudiando.getEscenario().hasGanado())
			{//Existe solucion, salimos
				nodosTotal = cerrados.size();
				cerrados.clear();
				abiertos.clear();
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
			//						System.out.println("TAMA�O: " +abiertos.size());
			//			imprimirCola(abiertos);
		}
		//				System.out.println("Se han estudiado "+cerrados.size()+" nodos");
		//				imprimirColaDos(cerrados);
		nodosTotal = cerrados.size();
		cerrados.clear();
		abiertos.clear();
		return null;
	}

	@SuppressWarnings("unused")
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

	//		private static void imprimirColaDos(List<Node> cola)
	//		{
	//			for (Node node : cola) {
	//				System.out.println("------------------------------");
	//				for(int i = 0; i<node.getEscenario().getCas().length; i++)
	//				{
	//					for(int j = 0; j<node.getEscenario().getCas()[i].length; j++){
	//						System.out.print(node.getEscenario().getCas()[i][j]);
	//					}
	//					System.out.println();
	//				}
	//			}
	//		}

	private static List<Node> getHijos(Node padre)
	{
		List<Node> hijos = new ArrayList<>();
		int i = 0;
		while(i<4)
		{//maximo 4 movimientos a realizar (4 hijos posibles)
			Node aux = null;
			Escenario test = new Escenario(padre.getEscenario().getNivel(), true);
			copiarEscenarioActual(test, padre.getEscenario());
			test.setALTO(test.getCas().length);
			test.setANCHO(test.getCas()[0].length-1);
			switch (i) {
			case 0://movimiento hacia arriba
				if(test.realizarMovimiento('W'))
				{
					aux = new Node(test, padre.getG()+1, test.placedBox(),padre.getID()+"W");
					if(comprobarEsquina(aux)&&comprobarBloques(aux)&&comprobarParedesLimitadas(aux))
					{
						hijos.add(aux);
					}
				}
				break;
			case 1://movimiento a la izquierda
				if(test.realizarMovimiento('A'))
				{
					aux = new Node(test, padre.getG()+1, test.placedBox(),padre.getID()+"A");
					if(comprobarEsquina(aux)&&comprobarBloques(aux)&&comprobarParedesLimitadas(aux))
					{
						hijos.add(aux);
					}
				}
				break;
			case 2://movimiento a la derecha
				if(test.realizarMovimiento('D'))
				{
					aux = new Node(test, padre.getG()+1, test.placedBox(),padre.getID()+"D");
					if(comprobarEsquina(aux)&&comprobarBloques(aux)&&comprobarParedesLimitadas(aux))
					{
						hijos.add(aux);
					}
				}
				break;
			default://movimiento hacia abajo
				if(test.realizarMovimiento('X'))
				{
					aux = new Node(test, padre.getG()+1, test.placedBox(),padre.getID()+"X");
					if(comprobarEsquina(aux)&&comprobarBloques(aux)&&comprobarParedesLimitadas(aux))
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

	private static boolean comprobarParedesLimitadas(Node test)
	{
		List<Posicion> cajasSinColocar = test.getEscenario().getCajas();//obtenemos la posicion de las cajas sin colocar
		if(!cajasSinColocar.isEmpty())
		{
			for (Posicion posicion : cajasSinColocar) {
				if(esParedLimitada(test.getEscenario(), posicion)||esCaminoBloqueante(test.getEscenario(), posicion)){
					return false;
				}
			}
		}
		return true;
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
				if(esUnBloque2x2(test.getEscenario(), posicion)||esUnBloque3x3(test.getEscenario(), posicion))
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

	//NUEVO METODO, USAR MATRICES DE PAREDLIMITADAS Y SOLO VER SI SIEMPRE ES # y # y 
	//encontrmaos en medio caja (da = si colcoada en goal) y no est� entre medias player!
	private static boolean esCaminoBloqueante(Escenario test, Posicion caja) {
		boolean jugadorMedio = false;
		char[][] aux1 = new char[3][1];//caja con posibles paredes abajo y/o arriba
		aux1[0][0]=test.getCas()[caja.x+1][caja.y];
		aux1[1][0]=test.getCas()[caja.x][caja.y];
		aux1[2][0]=test.getCas()[caja.x-1][caja.y];

		char[][] aux2 = new char[1][3];//caja con posibles paredes izquierda y/o derecha
		aux2[0][0]=test.getCas()[caja.x][caja.y-1];
		aux2[0][1]=test.getCas()[caja.x][caja.y];
		aux2[0][2]=test.getCas()[caja.x][caja.y+1];
		if((aux1[0][0]=='#')&&(aux1[2][0]=='#'))
		{//recorremos  derecha e izquierda del escenario por ese camino para ver si tiene salida para la caja
			jugadorMedio = false;
			for(int i = 1; i<((test.getCas()[0].length)-(caja.y)); i++)
			{//recorremos hacia derecha
				if((test.getCas()[caja.x+1][caja.y+i]=='#'||test.getCas()[caja.x+1][caja.y+i]=='$'||test.getCas()[caja.x+1][caja.y+i]=='*')
						&&(test.getCas()[caja.x-1][caja.y+i]=='#'||test.getCas()[caja.x-1][caja.y+i]=='$'||test.getCas()[caja.x-1][caja.y+i]=='*')
						&&(test.getCas()[caja.x][caja.y+i]!='.'||test.getCas()[caja.x][caja.y+i]!='+')){
					if(test.getCas()[caja.x][caja.y+i]=='@')
					{
						jugadorMedio = true;
					}
					if(!jugadorMedio&&(test.getCas()[caja.x][caja.y+i]=='$'||test.getCas()[caja.x][caja.y+i]=='*')){
//						System.out.println("CAMINO BLOQUEANTE HORIZONTAL 1 EN "+caja.x+","+caja.y);
//						test.escenarioToString();
						return true;
					}
				}else{
					break;
				}
			}
			jugadorMedio = false;
			for(int i = 1; i<=caja.y; i++)
			{//recorremos hacia izquierda
				if((((test.getCas()[caja.x+1][caja.y-i]=='#')||(test.getCas()[caja.x+1][caja.y-i]=='$')||(test.getCas()[caja.x+1][caja.y-i]=='*'))
						&&((test.getCas()[caja.x-1][caja.y-i]=='#')||(test.getCas()[caja.x-1][caja.y-i]=='$')||(test.getCas()[caja.x-1][caja.y-i]=='*')))
						&&(test.getCas()[caja.x][caja.y-i]!='.'||test.getCas()[caja.x][caja.y-i]!='+')){
					if(test.getCas()[caja.x][caja.y-i]=='@')
					{
						jugadorMedio = true;
					}
					if(!jugadorMedio&&(test.getCas()[caja.x][caja.y-i]=='$'||test.getCas()[caja.x][caja.y-i]=='*')){
//						System.out.println("CAMINO BLOQUEANTE HORIZONTAL 2 EN "+caja.x+","+caja.y);
//						test.escenarioToString();
						return true;
					}
				}else{
					return false;
				}
			}
			return true;
		}
		if(aux2[0][0]=='#'&&aux2[0][2]=='#')
		{//recorremos arriba y abajo del escenario por ese camino para ver si tiene salida para la caja
			jugadorMedio = false;
			for(int i = 1; i<((test.getCas().length)-(caja.x)); i++)
			{//recorremos hacia arriba
				if((((test.getCas()[caja.x+i][caja.y+1]=='#')||(test.getCas()[caja.x+i][caja.y+1]=='$')||(test.getCas()[caja.x+i][caja.y+1]=='*'))
						&&((test.getCas()[caja.x+i][caja.y-1]=='#')||(test.getCas()[caja.x+i][caja.y-1]=='$')||(test.getCas()[caja.x+i][caja.y-1]=='*')))
						&&(test.getCas()[caja.x+i][caja.y]!='.'||test.getCas()[caja.x+i][caja.y]!='+')){
					if(test.getCas()[caja.x+i][caja.y]=='@')
					{
						jugadorMedio = true;
					}
					if(!jugadorMedio&&(test.getCas()[caja.x+i][caja.y]=='$'||test.getCas()[caja.x+i][caja.y]=='*')){
//						System.out.println("CAMINO BLOQUEANTE VERTICAL 1 EN "+caja.x+","+caja.y);
//						test.escenarioToString();
						return true;
					}
				}else{
					break;
				}
			}
			jugadorMedio = false;
			for(int i = 1; i<=caja.x; i++)
			{//recorremos hacia abajo
				if((((test.getCas()[caja.x-i][caja.y+1]=='#')||(test.getCas()[caja.x-i][caja.y+1]=='$')||(test.getCas()[caja.x-i][caja.y+1]=='*'))
						&&((test.getCas()[caja.x-i][caja.y-1]=='#')||(test.getCas()[caja.x-i][caja.y-1]=='$')||(test.getCas()[caja.x-i][caja.y-1]=='*')))
						&&(test.getCas()[caja.x-i][caja.y]!='.'||test.getCas()[caja.x-i][caja.y]!='+')){
					if(test.getCas()[caja.x-i][caja.y]=='@')
					{
						jugadorMedio = true;
					}
					if(!jugadorMedio&&(test.getCas()[caja.x-i][caja.y]=='$'||test.getCas()[caja.x-i][caja.y]=='*')){
//						System.out.println("CAMINO BLOQUEANTE VERTICAL 2 EN "+caja.x+","+caja.y);
//						test.escenarioToString();
						return true;
					}
				}else{
					return false;
				}
			}
			return true;
		}
		return false;
	}

	private static boolean esParedLimitada(Escenario test, Posicion caja) {
		char[][] aux1 = new char[3][1];//caja con posibles paredes abajo y/o arriba
		aux1[0][0]=test.getCas()[caja.x+1][caja.y];
		aux1[1][0]=test.getCas()[caja.x][caja.y];
		aux1[2][0]=test.getCas()[caja.x-1][caja.y];

		char[][] aux2 = new char[1][3];//caja con posibles paredes izquierda y/o derecha
		aux2[0][0]=test.getCas()[caja.x][caja.y-1];
		aux2[0][1]=test.getCas()[caja.x][caja.y];
		aux2[0][2]=test.getCas()[caja.x][caja.y+1];

		if((aux1[0][0]=='#')||(aux1[2][0]=='#'))
		{//recorremos  derecha e izquierda del escenario por ese camino para ver si tiene salida para la caja
			for(int i = 1; i<((test.getCas()[0].length)-(caja.y)); i++)
			{//recorremos hacia derecha
				if((((test.getCas()[caja.x+1][caja.y+i]==' ')||(test.getCas()[caja.x+1][caja.y+i]=='@')||(test.getCas()[caja.x+1][caja.y+i]=='+'))
						&&((test.getCas()[caja.x-1][caja.y+i]==' ')||(test.getCas()[caja.x-1][caja.y+i]=='@')||(test.getCas()[caja.x-1][caja.y+i]=='+')))
						||(test.getCas()[caja.x][caja.y+i]=='.'||test.getCas()[caja.x][caja.y+i]=='+')){
					return false;
				}
			}
			for(int i = 1; i<=caja.y; i++)
			{//recorremos hacia izquierda
				if((((test.getCas()[caja.x+1][caja.y-i]==' ')||(test.getCas()[caja.x+1][caja.y-i]=='@')||(test.getCas()[caja.x+1][caja.y-i]=='+'))
						&&((test.getCas()[caja.x-1][caja.y-i]==' ')||(test.getCas()[caja.x-1][caja.y-i]=='@')||(test.getCas()[caja.x-1][caja.y-i]=='+')))
						||(test.getCas()[caja.x][caja.y-i]=='.'||test.getCas()[caja.x][caja.y-i]=='+')){
					return false;
				}
			}
			return true;
		}

		if(aux2[0][0]=='#'||aux2[0][2]=='#')
		{//recorremos arriba y abajo del escenario por ese camino para ver si tiene salida para la caja
			for(int i = 1; i<((test.getCas().length)-(caja.x)); i++)
			{//recorremos hacia arriba
				if((((test.getCas()[caja.x+i][caja.y+1]==' ')||(test.getCas()[caja.x+i][caja.y+1]=='@')||(test.getCas()[caja.x+i][caja.y+1]=='+'))
						&&((test.getCas()[caja.x+i][caja.y-1]==' ')||(test.getCas()[caja.x+i][caja.y-1]=='@')||(test.getCas()[caja.x+i][caja.y-1]=='+')))
						||(test.getCas()[caja.x+i][caja.y]=='.'||test.getCas()[caja.x+i][caja.y]=='+')){
					return false;
				}
			}
			for(int i = 1; i<=caja.x; i++)
			{//recorremos hacia abajo
				if((((test.getCas()[caja.x-i][caja.y+1]==' ')||(test.getCas()[caja.x-i][caja.y+1]=='@')||(test.getCas()[caja.x-i][caja.y+1]=='+'))
						&&((test.getCas()[caja.x-i][caja.y-1]==' ')||(test.getCas()[caja.x-i][caja.y-1]=='@')||(test.getCas()[caja.x-i][caja.y-1]=='+')))
						||(test.getCas()[caja.x-i][caja.y]=='.'||test.getCas()[caja.x-i][caja.y]=='+')){
					return false;
				}
			}
			return true;
		}
		return false;
	}

	private static boolean esUnBloque3x3(Escenario test, Posicion caja) {
		//creamos el escenario 3x3 que rodea a nuestra caja
		char[][] aux1 = new char[3][3];//caja en 0,0
		if(caja.x<12&&caja.y<18){
			aux1[0][0]=test.getCas()[caja.x][caja.y];
			aux1[0][1]=test.getCas()[caja.x][caja.y+1];
			aux1[0][2]=test.getCas()[caja.x][caja.y+2];
			aux1[1][0]=test.getCas()[caja.x+1][caja.y];
			aux1[1][1]=test.getCas()[caja.x+1][caja.y+1];
			aux1[1][2]=test.getCas()[caja.x+1][caja.y+2];
			aux1[2][0]=test.getCas()[caja.x+2][caja.y];
			aux1[2][1]=test.getCas()[caja.x+2][caja.y+1];
			aux1[2][2]=test.getCas()[caja.x+2][caja.y+2];
		}else if(caja.x>=12&&caja.y<18){//evitamos salirnos de los l�mites de filas del escenario
			aux1[0][0]=test.getCas()[caja.x][caja.y];
			aux1[0][1]=test.getCas()[caja.x][caja.y+1];
			aux1[0][2]=test.getCas()[caja.x][caja.y+2];
			aux1[1][0]=test.getCas()[caja.x+1][caja.y];
			aux1[1][1]=test.getCas()[caja.x+1][caja.y+1];
			aux1[1][2]=test.getCas()[caja.x+1][caja.y+2];
			aux1[2][0]=' ';
			aux1[2][1]=' ';
			aux1[2][2]=' ';
		}else if(caja.y>=18&&caja.x<12){//evitamos salirnos de los l�mites de columnas del escenario
			aux1[0][0]=test.getCas()[caja.x][caja.y];
			aux1[0][1]=test.getCas()[caja.x][caja.y+1];
			aux1[0][2]=' ';
			aux1[1][0]=test.getCas()[caja.x+1][caja.y];
			aux1[1][1]=test.getCas()[caja.x+1][caja.y+1];
			aux1[1][2]=' ';
			aux1[2][0]=test.getCas()[caja.x+2][caja.y];
			aux1[2][1]=test.getCas()[caja.x+2][caja.y+1];
			aux1[2][2]=' ';
		}else{//evitamos salirnos por ambos limites
			aux1[0][0]=test.getCas()[caja.x][caja.y];
			aux1[0][1]=test.getCas()[caja.x][caja.y+1];
			aux1[0][2]=' ';
			aux1[1][0]=test.getCas()[caja.x+1][caja.y];
			aux1[1][1]=test.getCas()[caja.x+1][caja.y+1];
			aux1[1][2]=' ';
			aux1[2][0]=' ';
			aux1[2][1]=' ';
			aux1[2][2]=' ';
		}

		char[][] aux2 = new char[3][3];//caja en 0,1
		if(caja.x<12){
			aux1[0][0]=test.getCas()[caja.x][caja.y-1];
			aux1[0][1]=test.getCas()[caja.x][caja.y];
			aux1[0][2]=test.getCas()[caja.x][caja.y+1];
			aux1[1][0]=test.getCas()[caja.x+1][caja.y-1];
			aux1[1][1]=test.getCas()[caja.x+1][caja.y];
			aux1[1][2]=test.getCas()[caja.x+1][caja.y+1];
			aux1[2][0]=test.getCas()[caja.x+2][caja.y-1];
			aux1[2][1]=test.getCas()[caja.x+2][caja.y];
			aux1[2][2]=test.getCas()[caja.x+2][caja.y+1];
		}else{//evitamos salirnos de los l�mites de filas del escenario
			aux1[0][0]=test.getCas()[caja.x][caja.y-1];
			aux1[0][1]=test.getCas()[caja.x][caja.y];
			aux1[0][2]=test.getCas()[caja.x][caja.y+1];
			aux1[1][0]=test.getCas()[caja.x+1][caja.y-1];
			aux1[1][1]=test.getCas()[caja.x+1][caja.y];
			aux1[1][2]=test.getCas()[caja.x+1][caja.y+1];
			aux1[2][0]=' ';
			aux1[2][1]=' ';
			aux1[2][2]=' ';
		}

		char[][] aux3 = new char[3][3];//caja en 0,2
		if(caja.y>1&&caja.x<12){
			aux1[0][0]=test.getCas()[caja.x][caja.y-2];
			aux1[0][1]=test.getCas()[caja.x][caja.y-1];
			aux1[0][2]=test.getCas()[caja.x][caja.y];
			aux1[1][0]=test.getCas()[caja.x+1][caja.y-2];
			aux1[1][1]=test.getCas()[caja.x+1][caja.y-1];
			aux1[1][2]=test.getCas()[caja.x+1][caja.y];
			aux1[2][0]=test.getCas()[caja.x+2][caja.y-2];
			aux1[2][1]=test.getCas()[caja.x+2][caja.y-1];
			aux1[2][2]=test.getCas()[caja.x+2][caja.y];
		}else if(caja.x>=12&&caja.y>1){//evitamos salirnos de los l�mites de filas del escenario
			aux1[0][0]=test.getCas()[caja.x][caja.y-2];
			aux1[0][1]=test.getCas()[caja.x][caja.y-1];
			aux1[0][2]=test.getCas()[caja.x][caja.y];
			aux1[1][0]=test.getCas()[caja.x+1][caja.y-2];
			aux1[1][1]=test.getCas()[caja.x+1][caja.y-1];
			aux1[1][2]=test.getCas()[caja.x+1][caja.y];
			aux1[2][0]=' ';
			aux1[2][1]=' ';
			aux1[2][2]=' ';
		}else if(caja.y<=1&&caja.x<12){//evitamos salirnos de los l�mites de columnas del escenario
			aux1[0][0]=' ';
			aux1[0][1]=test.getCas()[caja.x][caja.y-1];
			aux1[0][2]=test.getCas()[caja.x][caja.y];
			aux1[1][0]=' ';
			aux1[1][1]=test.getCas()[caja.x+1][caja.y-1];
			aux1[1][2]=test.getCas()[caja.x+1][caja.y];
			aux1[2][0]=' ';
			aux1[2][1]=test.getCas()[caja.x+2][caja.y-1];
			aux1[2][2]=test.getCas()[caja.x+2][caja.y];
		}else{//evitamos salirnos por ambos limites
			aux1[0][0]=' ';
			aux1[0][1]=test.getCas()[caja.x][caja.y-1];
			aux1[0][2]=test.getCas()[caja.x][caja.y];
			aux1[1][0]=' ';
			aux1[1][1]=test.getCas()[caja.x+1][caja.y-1];
			aux1[1][2]=test.getCas()[caja.x+1][caja.y];
			aux1[2][0]=' ';
			aux1[2][1]=' ';
			aux1[2][2]=' ';
		}

		char[][] aux4 = new char[3][3];//caja en 1,0
		if(caja.y<18){
			aux1[0][0]=test.getCas()[caja.x-1][caja.y];
			aux1[0][1]=test.getCas()[caja.x-1][caja.y+1];
			aux1[0][2]=test.getCas()[caja.x-1][caja.y+2];
			aux1[1][0]=test.getCas()[caja.x][caja.y];
			aux1[1][1]=test.getCas()[caja.x][caja.y+1];
			aux1[1][2]=test.getCas()[caja.x][caja.y+2];
			aux1[2][0]=test.getCas()[caja.x+1][caja.y];
			aux1[2][1]=test.getCas()[caja.x+1][caja.y+1];
			aux1[2][2]=test.getCas()[caja.x+1][caja.y+2];
		}else{//evitamos salirnos de los l�mites de columnas del escenario
			aux1[0][0]=test.getCas()[caja.x-1][caja.y];
			aux1[0][1]=test.getCas()[caja.x-1][caja.y+1];
			aux1[0][2]=' ';
			aux1[1][0]=test.getCas()[caja.x][caja.y];
			aux1[1][1]=test.getCas()[caja.x][caja.y+1];
			aux1[1][2]=' ';
			aux1[2][0]=test.getCas()[caja.x+1][caja.y];
			aux1[2][1]=test.getCas()[caja.x+1][caja.y+1];
			aux1[2][2]=' ';
		}

		char[][] aux5 = new char[3][3];//caja en 1,1
		aux1[0][0]=test.getCas()[caja.x-1][caja.y-1];
		aux1[0][1]=test.getCas()[caja.x-1][caja.y];
		aux1[0][2]=test.getCas()[caja.x-1][caja.y+1];
		aux1[1][0]=test.getCas()[caja.x][caja.y-1];
		aux1[1][1]=test.getCas()[caja.x][caja.y];
		aux1[1][2]=test.getCas()[caja.x][caja.y+1];
		aux1[2][0]=test.getCas()[caja.x+1][caja.y-1];
		aux1[2][1]=test.getCas()[caja.x+1][caja.y];
		aux1[2][2]=test.getCas()[caja.x+1][caja.y+1];

		char[][] aux6 = new char[3][3];//caja en 1,2
		if(caja.y>1){
			aux1[0][0]=test.getCas()[caja.x-1][caja.y-2];
			aux1[0][1]=test.getCas()[caja.x-1][caja.y-1];
			aux1[0][2]=test.getCas()[caja.x-1][caja.y];
			aux1[1][0]=test.getCas()[caja.x][caja.y-2];
			aux1[1][1]=test.getCas()[caja.x][caja.y-1];
			aux1[1][2]=test.getCas()[caja.x][caja.y];
			aux1[2][0]=test.getCas()[caja.x+1][caja.y-2];
			aux1[2][1]=test.getCas()[caja.x+1][caja.y-1];
			aux1[2][2]=test.getCas()[caja.x+1][caja.y];
		}else{//evitamos salirnos de los l�mites de columnas del escenario
			aux1[0][0]=' ';
			aux1[0][1]=test.getCas()[caja.x-1][caja.y-1];
			aux1[0][2]=test.getCas()[caja.x-1][caja.y];
			aux1[1][0]=' ';
			aux1[1][1]=test.getCas()[caja.x][caja.y-1];
			aux1[1][2]=test.getCas()[caja.x][caja.y];
			aux1[2][0]=' ';
			aux1[2][1]=test.getCas()[caja.x+1][caja.y-1];
			aux1[2][2]=test.getCas()[caja.x+1][caja.y];
		}

		char[][] aux7 = new char[3][3];//caja en 2,0
		if(caja.y<18&&caja.x>1){
			aux1[0][0]=test.getCas()[caja.x-2][caja.y];
			aux1[0][1]=test.getCas()[caja.x-2][caja.y+1];
			aux1[0][2]=test.getCas()[caja.x-2][caja.y+2];
			aux1[1][0]=test.getCas()[caja.x-1][caja.y];
			aux1[1][1]=test.getCas()[caja.x-1][caja.y+1];
			aux1[1][2]=test.getCas()[caja.x-1][caja.y+2];
			aux1[2][0]=test.getCas()[caja.x][caja.y];
			aux1[2][1]=test.getCas()[caja.x][caja.y+1];
			aux1[2][2]=test.getCas()[caja.x][caja.y+2];
		}else if(caja.x<=1&&caja.y<18){//evitamos salirnos de los l�mites de filas del escenario
			aux1[0][0]=' ';
			aux1[0][1]=' ';
			aux1[0][2]=' ';
			aux1[1][0]=test.getCas()[caja.x-1][caja.y];
			aux1[1][1]=test.getCas()[caja.x-1][caja.y+1];
			aux1[1][2]=test.getCas()[caja.x-1][caja.y+2];
			aux1[2][0]=test.getCas()[caja.x][caja.y];
			aux1[2][1]=test.getCas()[caja.x][caja.y+1];
			aux1[2][2]=test.getCas()[caja.x][caja.y+2];
		}else if(caja.y>=18&&caja.x>1){//evitamos salirnos de los l�mites de columnas del escenario
			aux1[0][0]=test.getCas()[caja.x-2][caja.y];
			aux1[0][1]=test.getCas()[caja.x-2][caja.y+1];
			aux1[0][2]=' ';
			aux1[1][0]=test.getCas()[caja.x-1][caja.y];
			aux1[1][1]=test.getCas()[caja.x-1][caja.y+1];
			aux1[1][2]=' ';
			aux1[2][0]=test.getCas()[caja.x][caja.y];
			aux1[2][1]=test.getCas()[caja.x][caja.y+1];
			aux1[2][2]=' ';
		}else{//evitamos salirnos de ambos l�mites del escenario
			aux1[0][0]=' ';
			aux1[0][1]=' ';
			aux1[0][2]=' ';
			aux1[1][0]=test.getCas()[caja.x-1][caja.y];
			aux1[1][1]=test.getCas()[caja.x-1][caja.y+1];
			aux1[1][2]=' ';
			aux1[2][0]=test.getCas()[caja.x][caja.y];
			aux1[2][1]=test.getCas()[caja.x][caja.y+1];
			aux1[2][2]=' ';
		}

		char[][] aux8 = new char[3][3];//caja en 2,1
		if(caja.x>1){
			aux1[0][0]=test.getCas()[caja.x-2][caja.y-1];
			aux1[0][1]=test.getCas()[caja.x-2][caja.y];
			aux1[0][2]=test.getCas()[caja.x-2][caja.y+1];
			aux1[1][0]=test.getCas()[caja.x-1][caja.y-1];
			aux1[1][1]=test.getCas()[caja.x-1][caja.y];
			aux1[1][2]=test.getCas()[caja.x-1][caja.y+1];
			aux1[2][0]=test.getCas()[caja.x][caja.y-1];
			aux1[2][1]=test.getCas()[caja.x][caja.y];
			aux1[2][2]=test.getCas()[caja.x][caja.y+1];
		}else{//evitamos salirnos de los l�mites de filas del escenario
			aux1[0][0]=' ';
			aux1[0][1]=' ';
			aux1[0][2]=' ';
			aux1[1][0]=test.getCas()[caja.x-1][caja.y-1];
			aux1[1][1]=test.getCas()[caja.x-1][caja.y];
			aux1[1][2]=test.getCas()[caja.x-1][caja.y+1];
			aux1[2][0]=test.getCas()[caja.x][caja.y-1];
			aux1[2][1]=test.getCas()[caja.x][caja.y];
			aux1[2][2]=test.getCas()[caja.x][caja.y+1];	
		}

		char[][] aux9 = new char[3][3];//caja en 2,2
		if(caja.x>1&&caja.y>1){
			aux1[0][0]=test.getCas()[caja.x-2][caja.y-2];
			aux1[0][1]=test.getCas()[caja.x-2][caja.y-1];
			aux1[0][2]=test.getCas()[caja.x-2][caja.y];
			aux1[1][0]=test.getCas()[caja.x-1][caja.y-2];
			aux1[1][1]=test.getCas()[caja.x-1][caja.y-1];
			aux1[1][2]=test.getCas()[caja.x-1][caja.y];
			aux1[2][0]=test.getCas()[caja.x][caja.y-2];
			aux1[2][1]=test.getCas()[caja.x][caja.y-1];
			aux1[2][2]=test.getCas()[caja.x][caja.y];
		}else if(caja.x<=1&&caja.y>1){//evitamos salirnos de los l�mites de filas del escenario
			aux1[0][0]=' ';
			aux1[0][1]=' ';
			aux1[0][2]=' ';
			aux1[1][0]=test.getCas()[caja.x-1][caja.y-2];
			aux1[1][1]=test.getCas()[caja.x-1][caja.y-1];
			aux1[1][2]=test.getCas()[caja.x-1][caja.y];
			aux1[2][0]=test.getCas()[caja.x][caja.y-2];
			aux1[2][1]=test.getCas()[caja.x][caja.y-1];
			aux1[2][2]=test.getCas()[caja.x][caja.y];
		}else if(caja.x>1&&caja.y<=1){//evitamos salirnos de los l�mites de columnas del escenario
			aux1[0][0]=' ';
			aux1[0][1]=test.getCas()[caja.x-2][caja.y-1];
			aux1[0][2]=test.getCas()[caja.x-2][caja.y];
			aux1[1][0]=' ';
			aux1[1][1]=test.getCas()[caja.x-1][caja.y-1];
			aux1[1][2]=test.getCas()[caja.x-1][caja.y];
			aux1[2][0]=' ';
			aux1[2][1]=test.getCas()[caja.x][caja.y-1];
			aux1[2][2]=test.getCas()[caja.x][caja.y];
		}else{//evitamos salirnos de ambos l�mites del escenario
			aux1[0][0]=' ';
			aux1[0][1]=' ';
			aux1[0][2]=' ';
			aux1[1][0]=' ';
			aux1[1][1]=test.getCas()[caja.x-1][caja.y-1];
			aux1[1][2]=test.getCas()[caja.x-1][caja.y];
			aux1[2][0]=' ';
			aux1[2][1]=test.getCas()[caja.x][caja.y-1];
			aux1[2][2]=test.getCas()[caja.x][caja.y];
		}

		if(testBloque3x3(aux1)||testBloque3x3(aux2)||testBloque3x3(aux3)||testBloque3x3(aux4)
				||testBloque3x3(aux5)||testBloque3x3(aux6)||testBloque3x3(aux7)
				||testBloque3x3(aux8)||testBloque3x3(aux9))
		{//vemos si alguno forma bloque 3x3
			//								System.out.println("------------------------------");
			//								for(int i = 0; i<test.getCas().length; i++)
			//								{
			//									for(int j = 0; j<test.getCas()[i].length; j++){
			//										System.out.print(test.getCas()[i][j]);
			//									}
			//									System.out.println();
			//								}
			return true;
		}

		return false;
	}

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
				if(aux1[i][j]=='$'||aux1[i][j]=='*'){
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

	private static boolean testBloque3x3(char[][] aux1) {
		int cntB=0;//contador cajas
		int cntM=0;//contador muros
		boolean centro = false;//centro vacio
		if(aux1[1][1]==' '){centro = true;}
		for(int i=0;i<aux1.length; i++)
		{
			for(int j = 0;j<aux1[i].length; j++)
			{
				if(aux1[i][j]=='#'){
					cntM++;
				}
				if(aux1[i][j]=='$'||aux1[i][j]=='*'){
					cntB++;
				}
			}
		}
		if(cntB+cntM==9)
		{//bloque de 3x3 solido
			return true;
		}
		if(cntB+cntM==8&&centro)
		{//bloque de 3x3 con centro vac�o se revelan nuevas posiciones sin soluci�n
			return true;
		}else if((cntB+cntM)==6&&centro)
		{//bloque de 3x3 con centro vac�o con 2 esquinas opuestas vac�as produce una posici�n muerta 
			if((aux1[0][0]==' '&&aux1[2][2]==' ')||(aux1[0][2]==' '&&aux1[2][0]==' '))
			{//esquinas opuestas vacias
				return true;
			}
		}else if((cntB+cntM)==7&&centro)
		{//bloque de 3x3 con centro vac�o con 1 esquinas vac�as produce una posici�n muerta 
			if((aux1[0][0]==' '&&aux1[0][2]!=' '&&aux1[2][0]!=' '&&aux1[2][2]!=' ')
					||(aux1[0][0]!=' '&&aux1[0][2]==' '&&aux1[2][0]!=' '&&aux1[2][2]!=' ')
					||(aux1[0][0]!=' '&&aux1[0][2]!=' '&&aux1[2][0]==' '&&aux1[2][2]!=' ')
					||(aux1[0][0]!=' '&&aux1[0][2]!=' '&&aux1[2][0]!=' '&&aux1[2][2]==' '))
			{//esquina vacia
				return true;
			}
		}
		return false;
	}

	public static void copiarEscenarioActual(Escenario test, Escenario padre)
	{
		char[][] auxEsce = new char[padre.getALTO()][padre.getANCHO()+1];
		for (int j = 0; j < auxEsce.length; j++) {
			System.arraycopy(padre.getCas()[j], 0, auxEsce[j], 0, padre.getCas()[0].length);
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

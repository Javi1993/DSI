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

	public void nextStep(/*recibir posicion actual y avanzar un poco*/)
	{

	}

	/**
	 * M�todo que resuelve el nivel pasado usando el algoritmo AStar(A*) o IDAStar(IDA*)
	 * @param escenario - escenario a resolver
	 * @param pasos - numero de pasos realizados hasta el momento
	 * @return Array con secuencia de caracteres para llegar a la meta
	 */
	public char[] solucion(Escenario escenario, int pasos)
	{
		Node actual = new Node(escenario, pasos, escenario.placedBox(), "");//nodo actual del usuario
		escenario.setIA(true);//marcamos que el usuario solicito ayuda de la IA
		String tipe="AStar";//usamos el algoritmo A*
		//		String tipe="IDAStar"; //usamos el algoritmo IDA*
		char[] solExist = Mapas.verSol(escenario.getNivel(), tipe);//comprobamos si el nivel ya tiene una solucion de la IA
		if(solExist!=null && pasos==0){//ya hay una soluci�n almacenada de la IA y el jugador no realizo movimiento previo
			return solExist;//devolvemos solucion desde posicion inicial
		}else if(solExist!=null && pasos!=0){//hay solucion pero el jugador se encuentra en una posicion distinta de la inicial
			Escenario test = new Escenario(actual.getEscenario().getNivel(), false);//escenario auxiliar
			int posAux = 0;//auxiliar para calcular a partir de que secuencia se peude resolver el nivel en su estado actual
			for (char c : solExist) {//buscamos si la posicion actual del usuario es un paso intermedio de la solucion
				if(Arrays.deepEquals(test.getCas(), escenario.getCas())){//existe solucion guardada desde la posicion actual del usuario
					char[] solAux = new char[solExist.length-posAux];//generamos array con la solucion
					for(int i = 0; i<solAux.length; i++){//almacenamos caracteres
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
		long time_start, time_end;//Variables para calcular el tiempo de computo para hayar la solucion
		time_start = System.currentTimeMillis();//empezamos el contador
		String secuencia = AStar(actual);//buscamos la solucion con A*
		//			String secuencia = IDAStar(actual);//buscamos la solucion con IDA*
		time_end = System.currentTimeMillis();//finalizamos contador
		long time = time_end - time_start;//calculamos el tiempo total que demoro el calculo
		if(secuencia!=null){//existe solucion
			char[] sol = new char[secuencia.length()];//secuencia de teclas
			for(int i=0;i<secuencia.length(); i++){
				sol[i]=secuencia.charAt(i);
			}
			List<String> aux = new ArrayList<String>();
			for(int i = 0; i<sol.length; i++ ){
				aux.add(String.valueOf(sol[i]));
			}
			Escenario test = new Escenario(actual.getEscenario().getNivel(), true);//actualizamos los metadatos del nivel en la DB
			copiarEscenarioActual(test, actual.getEscenario());
			escenario.updateNivel(aux, null, time, test, tipe, nodosTotal);
			return sol;//devolvemos solucion
		}else{//no hay solucion para el nivel en el estado actual
			return null;
		}
	}

	/**
	 * M�todo que resuelve el nivel en base al algortimo AStar(A*)
	 * @param actual - nodo padre
	 * @return Secuencia de movimientos hasta llegar a meta
	 */
	private String AStar(Node actual)
	{
		nodosTotal = 0;//numero nodos estudiados
		Comparator<Node> comparator = new MyComparator();//comparador que actuara en la cola de abiertos para ordenador nodos
		PriorityQueue<Node> abiertos = new PriorityQueue<Node>(comparator);//cola de prioridades con nodos a estudiar
		List<Node> cerrados = new ArrayList<Node>();//lista con nodos ya estudiados
		abiertos.add(actual);//aniadimos el nodo padre a la cola
		while (!abiertos.isEmpty()){//mientras cola tenga nodos buscamos solucion al nivel
			Node estudiando = abiertos.poll();//extraemos el nodo de la cabeza de la cola
			if(estudiando.getEscenario().hasGanado()){//Existe solucion, salimos
				nodosTotal = cerrados.size();//guardamos el numero total de nodos estudiados
				cerrados.clear();//vaciamos lista
				abiertos.clear();//vaciamos cola
				return estudiando.getID();//devolvemos secuencia de movimientos para llegar a meta
			}else{//no es nodo meta
				List<Node> hijos = getHijos(estudiando);//generamos sus hijos
				cerrados.add(estudiando);//aniadimos nodo padre a lista de estudiamos
				if(!hijos.isEmpty()){//el nodo padre tiene hijos
					for(Node h:hijos){
						if(!yaEstudiado(h, cerrados)&&!yaEnCola(h, abiertos)){//comprobamos si ese nodo fue ya estudiado o ya esta a la espera en cola
							abiertos.add(h);//a�adimos el nuevo nodo a la cola
						}
					}
				}
			}
		}
		nodosTotal = cerrados.size();//guardamos el numero total de nodos estudiados
		cerrados.clear();//vaciamos lista
		abiertos.clear();//vaciamos cola
		return null;//no tiene solucion el nivel
	}

	@SuppressWarnings("unused")
	private String IDAStar(Node actual)
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

	private int IDAStarSearch(Node actual, int bound, List<Node> listRepetido)
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

	/**
	 * Genera todos los hijos del nodo recibido en base a lo movimientos posibles a realizar
	 * @param padre - nodo a expandir
	 * @return Lista con los nodos hijos resultantes
	 */
	private List<Node> getHijos(Node padre)
	{
		List<Node> hijos = new ArrayList<>();//lista de nodos hijos
		int i = 0;//contador
		while(i<4)
		{//maximo 4 movimientos a realizar (4 hijos posibles)
			Node aux = null;//nodo auxiliar
			Escenario test = new Escenario(padre.getEscenario().getNivel(), true);//escenario auxiliar
			copiarEscenarioActual(test, padre.getEscenario());//copiamos el escenario del nodo padre al auxiliar
			test.setALTO(test.getCas().length);
			test.setANCHO(test.getCas()[0].length-1);
			switch (i) {//realizamos los movimientos
			case 0://movimiento hacia arriba (W)
				if(test.realizarMovimiento('W')){//comprobamos si es posible el movimiento
					aux = new Node(test, padre.getG()+1, test.placedBox(),padre.getID()+"W");//generamos el nodo tras el movumiento
					if(aux.getEscenario().hasGanado() || comprobarRestricciones(aux)){//comprobamos si el escenario resultante cumple las restricciones
						hijos.add(aux);//guardamos nodo resultante en la lista de hijos
					}
				}
				break;
			case 1://movimiento a la izquierda (A)
				if(test.realizarMovimiento('A')){//comprobamos si es posible el movimiento
					aux = new Node(test, padre.getG()+1, test.placedBox(),padre.getID()+"A");//generamos el nodo tras el movumiento
					if(aux.getEscenario().hasGanado() || comprobarRestricciones(aux)){//comprobamos si el escenario resultante cumple las restricciones
						hijos.add(aux);//guardamos nodo resultante en la lista de hijos
					}
				}
				break;
			case 2://movimiento a la derecha (D)
				if(test.realizarMovimiento('D')){//comprobamos si es posible el movimiento
					aux = new Node(test, padre.getG()+1, test.placedBox(),padre.getID()+"D");//generamos el nodo tras el movumiento
					if(aux.getEscenario().hasGanado() || comprobarRestricciones(aux)){//comprobamos si el escenario resultante cumple las restricciones
						hijos.add(aux);//guardamos nodo resultante en la lista de hijos
					}
				}
				break;
			default://movimiento hacia abajo (X)
				if(test.realizarMovimiento('S')){//comprobamos si es posible el movimiento
					aux = new Node(test, padre.getG()+1, test.placedBox(),padre.getID()+"X");//generamos el nodo tras el movumiento
					if(aux.getEscenario().hasGanado() || comprobarRestricciones(aux)){//comprobamos si el escenario resultante cumple las restricciones
						hijos.add(aux);//guardamos nodo resultante en la lista de hijos
					}
				}
				break;
			}
			i++;
		}
		return hijos;
	}

	/**
	 * M�todo que recibe el nodo hijo generado y comprueba si cumple las restricciones para su posterior estudio
	 * @param test - Nodo
	 * @return false - si no las cumple, true - si las cumple
	 */
	private boolean comprobarRestricciones(Node test) {
		List<Posicion> cajasSinColocar = test.getEscenario().getCajas();//obtenemos la posicion de las cajas sin colocar
		if(!cajasSinColocar.isEmpty())
		{
			for (Posicion posicion : cajasSinColocar) {
				if(esEsquina(test.getEscenario(), posicion) || esUnBloque2x2(test.getEscenario(), posicion)|| esUnBloque3x3(test.getEscenario(), posicion)
						|| esParedLimitada(test.getEscenario(), posicion) || esCaminoBloqueante(test.getEscenario(), posicion)){
					//															System.out.println("BLOQ EN "+posicion.x+", "+posicion.y);
					//															test.getEscenario().escenarioToString();
					return false;
				}else if(test.getEscenario().cajas()>2 && esBloqueEspecial(test.getEscenario(), posicion)){
					//					System.out.println("ESPECIAL");
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * M�todo que comprueba escenarios tipo que pueden dejar el nivel irresoluble
	 * @param escenario - Escenario actual
	 * @param posicion - Posicion de la caja a evaluar
	 * @return true - es bloque, false - no es bloque
	 */
	private boolean esBloqueEspecial(Escenario test, Posicion caja) {
		char[][] aux1 = new char[3][4];//Caja en (0, 1)
		for(int i = 0; i<aux1.length; i++){//                    #$$#
			for(int j = 0; j<aux1[i].length; j++){//             #  #
				try{//                                            $# 
					aux1[i][j]=test.getCas()[caja.x+i][caja.y-1+j];
				}catch(IndexOutOfBoundsException e){//nos salimos de los limites, damos valor vacio
					aux1[i][j]=' ';
				}
			}
		}

		char[][] aux2 = new char[3][4];//Caja en (0, 2)
		for(int i = 0; i<aux2.length; i++){
			for(int j = 0; j<aux2[i].length; j++){
				try{
					aux2[i][j]=test.getCas()[caja.x+i][caja.y-2+j];
				}catch(IndexOutOfBoundsException e){
					aux1[i][j]=' ';
				}
			}
		}

		char[][] aux3 = new char[3][4];//Caja en (2, 1)
		for(int i = 0; i<aux3.length; i++){
			for(int j = 0; j<aux3[i].length; j++){
				try{
					aux3[i][j]=test.getCas()[caja.x+i-2][caja.y-1+j];
				}catch(IndexOutOfBoundsException e){
					aux1[i][j]=' ';
				}
			}
		}

		if(testBloqueEspecial(aux1)||testBloqueEspecial(aux2)||testBloqueEspecial(aux3))
		{//vemos si alguno forma bloque
			return true;
		}
		return false;
	}

	private boolean testBloqueEspecial(char[][] aux) {
		if((aux[0][1] == '$' || aux[0][1] == '*') && (aux[0][2] == '$' || aux[0][2] == '*') 
				&& (aux[2][1] == '$' || aux[2][1] == '*')){
			if(aux[0][0] == '#' && aux[0][3] == '#' && aux[1][0] == '#' && aux[1][1] == ' '
					&& aux[1][2] == ' ' && aux[1][3] == '#' && (aux[2][0] == ' ' || aux[2][0] == '#') 
					&& aux[2][2] == '#' && (aux[2][3] == ' ' || aux[2][3] == '#')){
				return true;
			}else{
				return false;
			}
		}
		return false;
	}

	private boolean esEsquina(Escenario test, Posicion posicion)
	{
		if((test.getCas()[posicion.x-1][posicion.y]=='#'||test.getCas()[posicion.x+1][posicion.y]=='#')
				&&(test.getCas()[posicion.x][posicion.y-1]=='#'||test.getCas()[posicion.x][posicion.y+1]=='#'))
		{//se ha colocado una caja en una esquina que no es posicion destino
			return true;
		}
		return false;
	}

	private boolean esCaminoBloqueante(Escenario test, Posicion caja) {
		boolean jugadorMedio = false;
		char[][] aux1 = new char[3][1];//caja con posibles paredes abajo y/o arriba
		aux1[0][0]=test.getCas()[caja.x+1][caja.y];
		aux1[1][0]=test.getCas()[caja.x][caja.y];
		aux1[2][0]=test.getCas()[caja.x-1][caja.y];

		char[][] aux2 = new char[1][3];//caja con posibles paredes izquierda y/o derecha
		aux2[0][0]=test.getCas()[caja.x][caja.y-1];
		aux2[0][1]=test.getCas()[caja.x][caja.y];
		aux2[0][2]=test.getCas()[caja.x][caja.y+1];
		if((aux1[0][0]=='#')&&(aux1[2][0]=='#')){//recorremos  derecha e izquierda del escenario por ese camino para ver si tiene salida para la caja
			jugadorMedio = false;
			for(int i = 1; i<((test.getCas()[0].length)-(caja.y)); i++){//recorremos hacia derecha
				if((test.getCas()[caja.x+1][caja.y+i]=='#'||test.getCas()[caja.x+1][caja.y+i]=='$'||test.getCas()[caja.x+1][caja.y+i]=='*')
						&&(test.getCas()[caja.x-1][caja.y+i]=='#'||test.getCas()[caja.x-1][caja.y+i]=='$'||test.getCas()[caja.x-1][caja.y+i]=='*')){
					if(test.getCas()[caja.x][caja.y+i]=='@'||test.getCas()[caja.x][caja.y+i]=='+'){
						jugadorMedio = true;
					}else if(test.getCas()[caja.x][caja.y+i]=='.'&&!jugadorMedio){
						return false;
					}else if(!jugadorMedio&&(test.getCas()[caja.x][caja.y+i]=='$'||test.getCas()[caja.x][caja.y+i]=='*')){
//						System.out.println("CAMINO BLOQUEANTE HORIZONTAL 1 EN "+caja.x+","+caja.y);
//						test.escenarioToString();
						return true;
					}else if(test.getCas()[caja.x][caja.y+i]=='#'){
						return true;
					}
				}else{				
					return false;
				}
			}
			jugadorMedio = false;
			for(int i = 1; i<=caja.y; i++){//recorremos hacia izquierda
				if((((test.getCas()[caja.x+1][caja.y-i]=='#')||(test.getCas()[caja.x+1][caja.y-i]=='$')||(test.getCas()[caja.x+1][caja.y-i]=='*'))
						&&((test.getCas()[caja.x-1][caja.y-i]=='#')||(test.getCas()[caja.x-1][caja.y-i]=='$')||(test.getCas()[caja.x-1][caja.y-i]=='*')))){
					if(test.getCas()[caja.x][caja.y-i]=='@'||test.getCas()[caja.x][caja.y-i]=='+'){
						jugadorMedio = true;
					}else if(test.getCas()[caja.x][caja.y-i]=='.'&&!jugadorMedio){
						return false;
					}else if(!jugadorMedio&&(test.getCas()[caja.x][caja.y-i]=='$'||test.getCas()[caja.x][caja.y-i]=='*')){
//						System.out.println("CAMINO BLOQUEANTE HORIZONTAL 2 EN "+caja.x+","+caja.y);
//						test.escenarioToString();
						return true;
					}else if(test.getCas()[caja.x][caja.y-i]=='#'){
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
			for(int i = 1; i<((test.getCas().length)-(caja.x)); i++){//recorremos hacia arriba
				if((((test.getCas()[caja.x+i][caja.y+1]=='#')||(test.getCas()[caja.x+i][caja.y+1]=='$')||(test.getCas()[caja.x+i][caja.y+1]=='*'))
						&&((test.getCas()[caja.x+i][caja.y-1]=='#')||(test.getCas()[caja.x+i][caja.y-1]=='$')||(test.getCas()[caja.x+i][caja.y-1]=='*')))){
					if(test.getCas()[caja.x+i][caja.y]=='@'||test.getCas()[caja.x+i][caja.y]=='+'){
						jugadorMedio = true;
					}else if(test.getCas()[caja.x+i][caja.y]=='.'&&!jugadorMedio){
						return false;
					}else if(!jugadorMedio&&(test.getCas()[caja.x+i][caja.y]=='$'||test.getCas()[caja.x+i][caja.y]=='*')){
//						System.out.println("CAMINO BLOQUEANTE VERTICAL 1 EN "+caja.x+","+caja.y);
//						test.escenarioToString();
						return true;
					}else if(test.getCas()[caja.x+i][caja.y]=='#'){
						return true;
					}
				}else{
					return false;
				}
			}
			jugadorMedio = false;
			for(int i = 1; i<=caja.x; i++){//recorremos hacia abajo
				if((((test.getCas()[caja.x-i][caja.y+1]=='#')||(test.getCas()[caja.x-i][caja.y+1]=='$')||(test.getCas()[caja.x-i][caja.y+1]=='*'))
						&&((test.getCas()[caja.x-i][caja.y-1]=='#')||(test.getCas()[caja.x-i][caja.y-1]=='$')||(test.getCas()[caja.x-i][caja.y-1]=='*')))){
					if(test.getCas()[caja.x-i][caja.y]=='@'||test.getCas()[caja.x-i][caja.y]=='+'){
						jugadorMedio = true;
					}else if(test.getCas()[caja.x-i][caja.y]=='.'&&!jugadorMedio){
						return false;
					}else if(!jugadorMedio&&(test.getCas()[caja.x-i][caja.y]=='$'||test.getCas()[caja.x-i][caja.y]=='*')){
//						System.out.println("CAMINO BLOQUEANTE VERTICAL 2 EN "+caja.x+","+caja.y);
//						test.escenarioToString();
						return true;
					}else if(test.getCas()[caja.x-i][caja.y]=='#'){
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

	private boolean esParedLimitada(Escenario test, Posicion caja) {
		char[][] aux1 = new char[3][1];//caja con posibles paredes abajo y/o arriba
		aux1[0][0]=test.getCas()[caja.x+1][caja.y];
		aux1[1][0]=test.getCas()[caja.x][caja.y];
		aux1[2][0]=test.getCas()[caja.x-1][caja.y];

		char[][] aux2 = new char[1][3];//caja con posibles paredes izquierda y/o derecha
		aux2[0][0]=test.getCas()[caja.x][caja.y-1];
		aux2[0][1]=test.getCas()[caja.x][caja.y];
		aux2[0][2]=test.getCas()[caja.x][caja.y+1];

		if((aux1[0][0]=='#')||(aux1[2][0]=='#')){//recorremos  derecha e izquierda del escenario por ese camino para ver si tiene salida para la caja
			for(int i = 1; i<((test.getCas()[0].length)-(caja.y)); i++){//recorremos hacia derecha
				if(test.getCas()[caja.x][caja.y+i]=='#'){
					break;
				}else if(((test.getCas()[caja.x+1][caja.y+i]!='#')&&(test.getCas()[caja.x-1][caja.y+i]!='#'))
						||(test.getCas()[caja.x][caja.y+i]=='.'||test.getCas()[caja.x][caja.y+i]=='+')){
					return false;

				}
			}
			for(int i = 1; i<=caja.y; i++){//recorremos hacia izquierda
				if(test.getCas()[caja.x][caja.y-i]=='#'){
					//					System.out.println("BLOQ EN HOR"+caja.x+", "+caja.y);
					//					test.escenarioToString();
					return true;
				}else if(((test.getCas()[caja.x+1][caja.y-i]!='#')&&(test.getCas()[caja.x-1][caja.y-i]!='#'))
						||(test.getCas()[caja.x][caja.y-i]=='.'||test.getCas()[caja.x][caja.y-i]=='+')){
					return false;
				}
			}
			//			System.out.println("BLOQ EN HOR FIN"+caja.x+", "+caja.y);
			//			test.escenarioToString();
			return true;
		}

		if(aux2[0][0]=='#'||aux2[0][2]=='#'){//recorremos arriba y abajo del escenario por ese camino para ver si tiene salida para la caja
			for(int i = 1; i<((test.getCas().length)-(caja.x)); i++){//recorremos hacia arriba
				if(test.getCas()[caja.x+i][caja.y]=='#'){
					break;
				}else if(((test.getCas()[caja.x+i][caja.y+1]!='#')&&(test.getCas()[caja.x+i][caja.y-1]!='#'))
						||(test.getCas()[caja.x+i][caja.y]=='.'||test.getCas()[caja.x+i][caja.y]=='+')){
					return false;
				}
			}
			for(int i = 1; i<=caja.x; i++){//recorremos hacia abajo
				if(test.getCas()[caja.x-i][caja.y]=='#'){
					//					System.out.println("BLOQ EN VERT"+caja.x+", "+caja.y);
					//					test.escenarioToString();
					return true;
				}if(((test.getCas()[caja.x-i][caja.y+1]!='#')&&(test.getCas()[caja.x-i][caja.y-1]!='#'))
						||(test.getCas()[caja.x-i][caja.y]=='.'||test.getCas()[caja.x-i][caja.y]=='+')){
					return false;
				}
			}
			//			System.out.println("BLOQ EN VERT FIN"+caja.x+", "+caja.y);
			//			test.escenarioToString();
			return true;
		}
		return false;
	}

	private boolean esUnBloque3x3(Escenario test, Posicion caja) {
		//creamos los bloques 3x3 que rodean a nuestra caja (9 posibilidades)
		char[][] aux1 = new char[3][3];//caja en 0,0
		for(int i = 0; i<aux1.length; i++){
			for(int j = 0; j<aux1[i].length; j++){
				try{
					aux1[i][j]=test.getCas()[caja.x+i][caja.y+j];
				}catch(IndexOutOfBoundsException e){
					aux1[i][j]=' ';
				}
			}
		}

		char[][] aux2 = new char[3][3];//caja en 0,1
		for(int i = 0; i<aux2.length; i++){
			for(int j = 0; j<aux2[i].length; j++){
				try{
					aux2[i][j]=test.getCas()[caja.x+i][caja.y+j-1];
				}catch(IndexOutOfBoundsException e){
					aux2[i][j]=' ';
				}
			}
		}

		char[][] aux3 = new char[3][3];//caja en 0,2
		for(int i = 0; i<aux3.length; i++){
			for(int j = 0; j<aux3[i].length; j++){
				try{
					aux3[i][j]=test.getCas()[caja.x+i][caja.y+j-2];
				}catch(IndexOutOfBoundsException e){
					aux3[i][j]=' ';
				}
			}
		}

		char[][] aux4 = new char[3][3];//caja en 1,0
		for(int i = 0; i<aux4.length; i++){
			for(int j = 0; j<aux4[i].length; j++){
				try{
					aux4[i][j]=test.getCas()[caja.x+i-1][caja.y+j];
				}catch(IndexOutOfBoundsException e){
					aux4[i][j]=' ';
				}
			}
		}

		char[][] aux5 = new char[3][3];//caja en 1,1
		for(int i = 0; i<aux5.length; i++){
			for(int j = 0; j<aux5[i].length; j++){
				try{
					aux5[i][j]=test.getCas()[caja.x+i-1][caja.y+j-1];
				}catch(IndexOutOfBoundsException e){
					aux5[i][j]=' ';
				}
			}
		}

		char[][] aux6 = new char[3][3];//caja en 1,2
		for(int i = 0; i<aux6.length; i++){
			for(int j = 0; j<aux6[i].length; j++){
				try{
					aux6[i][j]=test.getCas()[caja.x+i-1][caja.y+j-2];
				}catch(IndexOutOfBoundsException e){
					aux6[i][j]=' ';
				}
			}
		}

		char[][] aux7 = new char[3][3];//caja en 2,0
		for(int i = 0; i<aux7.length; i++){
			for(int j = 0; j<aux7[i].length; j++){
				try{
					aux7[i][j]=test.getCas()[caja.x+i-2][caja.y+j];
				}catch(IndexOutOfBoundsException e){
					aux7[i][j]=' ';
				}
			}
		}

		char[][] aux8 = new char[3][3];//caja en 2,1
		for(int i = 0; i<aux8.length; i++){
			for(int j = 0; j<aux8[i].length; j++){
				try{
					aux8[i][j]=test.getCas()[caja.x+i-2][caja.y+j-1];
				}catch(IndexOutOfBoundsException e){
					aux8[i][j]=' ';
				}
			}
		}

		char[][] aux9 = new char[3][3];//caja en 2,2
		for(int i = 0; i<aux9.length; i++){
			for(int j = 0; j<aux9[i].length; j++){
				try{
					aux9[i][j]=test.getCas()[caja.x+i-2][caja.y+j-2];
				}catch(IndexOutOfBoundsException e){
					aux9[i][j]=' ';
				}
			}
		}

		if(testBloque3x3(aux1)||testBloque3x3(aux2)||testBloque3x3(aux3)||testBloque3x3(aux4)
				||testBloque3x3(aux5)||testBloque3x3(aux6)||testBloque3x3(aux7)
				||testBloque3x3(aux8)||testBloque3x3(aux9))
		{//vemos si alguno forma bloque 3x3
			//			System.out.println("BLOQ 3x3 esquina vacia EN "+caja.x+", "+caja.y);
			//			test.escenarioToString();
			return true;
		}
		return false;
	}

	private boolean esUnBloque2x2 (Escenario test, Posicion caja){
		//creamos los bloques 2x2 que rodean a nuestra caja (4 posibilidades)
		char[][] aux1 = new char[2][2];//caja en 0,0
		for(int i = 0; i<aux1.length; i++){
			for(int j = 0; j<aux1[i].length; j++){
				aux1[i][j]=test.getCas()[caja.x+i][caja.y+j];
			}
		}

		char[][] aux2 = new char[2][2];//caja en 0,1
		for(int i = 0; i<aux2.length; i++){
			for(int j = 0; j<aux2[i].length; j++){
				aux2[i][j]=test.getCas()[caja.x+i][caja.y+j-1];
			}
		}

		char[][] aux3 = new char[2][2];//caja en 1,1
		for(int i = 0; i<aux3.length; i++){
			for(int j = 0; j<aux3[i].length; j++){
				aux3[i][j]=test.getCas()[caja.x+i-1][caja.y+j-1];
			}
		}

		char[][] aux4 = new char[2][2];//caja en 1,0
		for(int i = 0; i<aux4.length; i++){
			for(int j = 0; j<aux4[i].length; j++){
				aux4[i][j]=test.getCas()[caja.x+i-1][caja.y+j];
			}
		}

		if(testBloque2x2(aux1)||testBloque2x2(aux2)||testBloque2x2(aux3)||testBloque2x2(aux4))
		{//vemos si alguno forma bloque
			//			System.out.println("BLOQ 2x2 EN "+caja.x+", "+caja.y);
			//			test.escenarioToString();
			return true;
		}
		return false;
	}

	private boolean testBloque2x2(char[][] aux) {
		int cntB=0;//contador cajas y muros
		for(int i=0;i<aux.length; i++){
			for(int j = 0;j<aux[i].length; j++){
				if(aux[i][j]=='#' || aux[i][j]=='$' || aux[i][j]=='*'){
					cntB++;
				}
			}
		}
		if(cntB==4){//hay un bloque de cajas/muros que imposibilita resolver el nivel
			return true;
		}else{
			return false;
		}
	}

	private boolean testBloque3x3(char[][] aux) {
		int cntB=0;//contador cajas y muros
		boolean centro = false;//centro vacio
		if(aux[1][1]==' '){centro = true;}
		for(int i=0;i<aux.length; i++){
			for(int j = 0;j<aux[i].length; j++){
				if(aux[i][j]=='#'||aux[i][j]=='$'||aux[i][j]=='*'){
					cntB++;
				}
			}
		}
		if(cntB==9){//bloque de 3x3 solido
			return true;
		}else if(cntB==8 && centro){//bloque de 3x3 con centro vac�o se revelan nuevas posiciones sin soluci�n
			return true;
		}else if(cntB==6&&centro){//bloque de 3x3 con centro vac�o con 2 esquinas opuestas vac�as produce una posici�n muerta 
			if((aux[0][0]==' ' && aux[2][2]==' ')||(aux[0][2]==' ' && aux[2][0]==' '))
			{//esquinas opuestas vacias
				return true;
			}
		}else if(cntB==7 && centro){//bloque de 3x3 con centro vac�o con 1 esquinas vac�as produce una posici�n muerta 
			if((aux[0][0]==' ' && aux[0][2]!=' ' && aux[2][0]!=' ' && aux[2][2]!=' ')
					||(aux[0][0]!=' ' && aux[0][2]==' ' && aux[2][0]!=' ' && aux[2][2]!=' ')
					||(aux[0][0]!=' ' && aux[0][2]!=' ' && aux[2][0]==' ' && aux[2][2]!=' ')
					||(aux[0][0]!=' ' && aux[0][2]!=' ' && aux[2][0]!=' '&& aux[2][2]==' '))
			{//esquina vacia
				return true;
			}
		}
		return false;
	}

	public void copiarEscenarioActual(Escenario test, Escenario padre)
	{
		char[][] auxEsce = new char[padre.getALTO()][padre.getANCHO()+1];
		for (int j = 0; j < auxEsce.length; j++) {
			System.arraycopy(padre.getCas()[j], 0, auxEsce[j], 0, padre.getCas()[0].length);
		}
		test.setCas(auxEsce);
	}

	private boolean yaEnCola(Node hijo, PriorityQueue<Node> abiertos)
	{//si el escenario es el mismo
		for(Node comparar : abiertos)
		{//comparamos con los ya existentes
			if(Arrays.deepEquals(hijo.getEscenario().getCas(), comparar.getEscenario().getCas())){
				return true;
			}
		}
		return false;	
	}

	private boolean yaEstudiado(Node hijo, List<Node> cerrados)
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

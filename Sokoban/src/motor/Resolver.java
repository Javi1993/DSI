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

	private String solIDA;
	private int nodosTotal;
	private Restricciones r;

	/**
	 * 
	 */
	public char[] nextStep(Escenario escenario, int pasos, List<String> teclasManual){
		Node actual = new Node(escenario, pasos, escenario.placedBox(), "");//nodo actual del usuario
		escenario.setIA(true);//marcamos que el usuario solicito ayuda de la IA
		String tipe="AStar";//usamos el algoritmo A*
		//		String tipe="IDAStar"; //usamos el algoritmo IDA*
		r = new Restricciones();
		String secuencia = AStar(actual, true);//buscamos la solucion con A*
		//			String secuencia = IDAStar(actual);//buscamos la solucion con IDA*
		if(secuencia!=null){//existe camino
			char[] sol = new char[secuencia.length()];//secuencia de teclas
			for(int i=0;i<secuencia.length(); i++){
				sol[i]=secuencia.charAt(i);
			}
			List<String> aux = new ArrayList<String>();
			if(teclasManual!=null && teclasManual.size()>0){//añadimos las teclas usadas por el usuario manualmente
				for(String tecla : teclasManual){
					aux.add(tecla);
				}
			}
			for(int i = 0; i<sol.length; i++ ){
				aux.add(String.valueOf(sol[i]));
			}
			Escenario test = new Escenario(actual.getEscenario().getNivel(), true);//actualizamos los metadatos del nivel en la DB
			copiarEscenarioActual(test, actual.getEscenario());
			if(test.hasGanado()){//actualizamos nivel
				escenario.updateNivel(aux, null, 0, test, tipe, nodosTotal);
			}
			return sol;//devolvemos camino
		}else{//no hay camino posible desde la posicion actual
			return null;
		}
	}

	/**
	 * M�todo que resuelve el nivel pasado usando el algoritmo AStar(A*) o IDAStar(IDA*)
	 * @param escenario - escenario a resolver
	 * @param pasos - numero de pasos realizados hasta el momento
	 * @return Array con secuencia de caracteres para llegar a la meta
	 */
	public char[] solucion(Escenario escenario, int pasos, List<String> teclasManual){
		Node actual = new Node(escenario, pasos, escenario.placedBox(), "");//nodo actual del usuario
		escenario.setIA(true);//marcamos que el usuario solicito ayuda de la IA
//		String tipe="AStar";//usamos el algoritmo A*
				String tipe="IDAStar"; //usamos el algoritmo IDA*
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
		r = new Restricciones();
		long time_start, time_end;//Variables para calcular el tiempo de computo para hayar la solucion
		time_start = System.currentTimeMillis();//empezamos el contador
//		String secuencia = AStar(actual, false);//buscamos la solucion con A*
					String secuencia = IDAStar(actual);//buscamos la solucion con IDA*
		time_end = System.currentTimeMillis();//finalizamos contador
		long time = time_end - time_start;//calculamos el tiempo total que demoro el calculo
		if(secuencia!=null){//existe solucion
			char[] sol = new char[secuencia.length()];//secuencia de teclas
			for(int i=0;i<secuencia.length(); i++){
				sol[i]=secuencia.charAt(i);
			}
			List<String> aux = new ArrayList<String>();
			if(teclasManual!=null && teclasManual.size()>0){//añadimos las teclas usadas por el usuario manualmente
				for(String tecla : teclasManual){
					aux.add(tecla);
				}
			}
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
	private String AStar(Node actual, boolean nextStep){
		long time_start = 0;//Variables para calcular el tiempo si se eligio sugerir camino
		nodosTotal = 0;//numero nodos estudiados
		Comparator<Node> comparator = new MyComparator();//comparador que actuara en la cola de abiertos para ordenador nodos
		//		Comparator<Node> comparator = new MyComparatorAdmissible();//con heuristica admisible
		PriorityQueue<Node> abiertos = new PriorityQueue<Node>(comparator);//cola de prioridades con nodos a estudiar
		List<Node> cerrados = new ArrayList<Node>();//lista con nodos ya estudiados
		abiertos.add(actual);//aniadimos el nodo padre a la cola
		if(nextStep){//emepezamos a contar limite de sugerir camino
			time_start = System.currentTimeMillis();//empezamos el contador
		}
		while (!abiertos.isEmpty()){//mientras cola tenga nodos buscamos solucion al nivel
			if(nextStep && (System.currentTimeMillis()-time_start)>10000){//se eligio sugerir camino y tiempo mayor a 10seg
				return masBueno(cerrados);
			}
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

	private String masBueno(List<Node> cerrados){
		Node aux = null;
		int max = -1;
		for(Node node : cerrados){//buscamos el nodo con mas cajas colocadas
			if(node.getI()>max){
				aux = node;
			}
		}
		return aux.getID();
	}

	private String IDAStar(Node actual){
		int bound = actual.getF();
		nodosTotal = 0;
		while (true) {
			List<Node> listRepetido = new ArrayList<Node>();
			listRepetido.add(actual);
			int aux = IDAStarSearch(actual, bound, listRepetido);
			if(aux==0){
				return solIDA;
			}else if(aux==2147483647){
				return null;
			}
			else{//cambiamos limite
				nodosTotal = 0;
				bound = aux;
			}
		}
	}

	private int IDAStarSearch(Node actual, int bound, List<Node> listRepetido)
	{
		int min = 0;
		nodosTotal++;
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
		List<Posicion> cajas = padre.getEscenario().getCajas();//lista de posicion cajas para controlar cual se movio
		int i = 0;//contador
		while(i<4)
		{//maximo 4 movimientos a realizar (4 hijos posibles)
			Node aux = null;//nodo auxiliar
			Posicion caja = null;//posicion caja movida
			Escenario test = new Escenario(padre.getEscenario().getNivel(), true);//escenario auxiliar
			copiarEscenarioActual(test, padre.getEscenario());//copiamos el escenario del nodo padre al auxiliar
			test.setALTO(test.getCas().length);
			test.setANCHO(test.getCas()[0].length-1);
			switch (i) {//realizamos los movimientos
			case 0://movimiento hacia arriba (W)
				if(test.realizarMovimiento('W')){//comprobamos si es posible el movimiento
					aux = new Node(test, padre.getG()+1, test.placedBox(),padre.getID()+"W");//generamos el nodo tras el movumiento
					caja = posicionCambiada(aux, cajas);
					if(aux.getEscenario().hasGanado() || comprobarRestricciones(aux, caja)){//comprobamos si el escenario resultante cumple las restricciones
						hijos.add(aux);//guardamos nodo resultante en la lista de hijos
					}
				}
				break;
			case 1://movimiento a la izquierda (A)
				if(test.realizarMovimiento('A')){//comprobamos si es posible el movimiento
					aux = new Node(test, padre.getG()+1, test.placedBox(),padre.getID()+"A");//generamos el nodo tras el movumiento
					caja = posicionCambiada(aux, cajas);
					if(aux.getEscenario().hasGanado() || comprobarRestricciones(aux, caja)){//comprobamos si el escenario resultante cumple las restricciones
						hijos.add(aux);//guardamos nodo resultante en la lista de hijos
					}
				}
				break;
			case 2://movimiento a la derecha (D)
				if(test.realizarMovimiento('D')){//comprobamos si es posible el movimiento
					aux = new Node(test, padre.getG()+1, test.placedBox(),padre.getID()+"D");//generamos el nodo tras el movumiento
					caja = posicionCambiada(aux, cajas);
					if(aux.getEscenario().hasGanado() || comprobarRestricciones(aux, caja)){//comprobamos si el escenario resultante cumple las restricciones
						hijos.add(aux);//guardamos nodo resultante en la lista de hijos
					}
				}
				break;
			default://movimiento hacia abajo (S)
				if(test.realizarMovimiento('S')){//comprobamos si es posible el movimiento
					aux = new Node(test, padre.getG()+1, test.placedBox(),padre.getID()+"S");//generamos el nodo tras el movumiento
					caja = posicionCambiada(aux, cajas);
					if(aux.getEscenario().hasGanado() || comprobarRestricciones(aux, caja)){//comprobamos si el escenario resultante cumple las restricciones
						hijos.add(aux);//guardamos nodo resultante en la lista de hijos
					}
				}
				break;
			}
			i++;
		}
		return hijos;
	}

	private Posicion posicionCambiada(Node aux, List<Posicion> cajas) {
		List<Posicion> auxCajas = aux.getEscenario().getCajas();
		boolean movida=true;
		for(Posicion pos:auxCajas){
			for(Posicion posVieja:cajas){
				if((pos.x==posVieja.x)&&(pos.y==posVieja.y)){//esta caja no se movio
					movida = false;//no fue movida esa caja
					break;
				}
			}
			if(movida){//fue movida esta caja
				return pos;
			}
			movida = true;
		}
		return null;//no se movio ninguna caja
	}

	/**
	 * M�todo que recibe el nodo hijo generado y comprueba si cumple las restricciones para su posterior estudio
	 * @param test - Nodo
	 * @return false - si no las cumple, true - si las cumple
	 */
	private boolean comprobarRestricciones(Node test, Posicion caja) {
		if(caja!=null){//vemos si alguna caja fue desplazada
			if(r.esEsquina(test.getEscenario(), caja) || r.esUnBloque2x2(test.getEscenario(), caja)|| r.esBloqueEspecial_2(test.getEscenario(), caja) || r.esUnBloque3x3(test.getEscenario(), caja)
					|| r.esParedLimitada(test.getEscenario(), caja) || r.esCaminoBloqueante(test.getEscenario(), caja) || r.esBloqueEspecial_3(test.getEscenario(), caja)){
				//															System.out.println("BLOQ EN "+posicion.x+", "+posicion.y);
				//															test.getEscenario().escenarioToString();
				return false;
			}else if(test.getEscenario().cajas()>2 && r.esBloqueEspecial_1(test.getEscenario(), caja)){
				//					System.out.println("ESPECIAL");
				return false;
			}
		}
		return true;
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

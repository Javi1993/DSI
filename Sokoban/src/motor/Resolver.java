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

	private String solIDA;//secuencia de teclas de la solucion usando IDA*.
	private int nodosTotal;//nodos totales estudiados.
	private Restricciones r;

	/**
	 * Devuelve una secuencia de pasos prometedores de solucion generados durante 10 seg de ejecucion del algortimo.
	 * @param escenario - escenario actual.
	 * @param pasos - pasos actuales.
	 * @param teclasManual - teclas manuales usadas hasta ahora.
	 * @return Secuencia de caracteres con la solucion parcial.
	 */
	public char[] nextStep(Escenario escenario, int pasos, List<String> teclasManual){
		Node actual = new Node(escenario, pasos, escenario.placedBox(), "");//nodo actual del usuario.
		escenario.setIA(true);//marcamos que el usuario solicito ayuda de la IA.
		String tipe="AStar";//usamos el algoritmo A*.
		//String tipe="IDAStar"; //usamos el algoritmo IDA*.
		r = new Restricciones();
		String secuencia = AStar(actual, true);//buscamos la solucion con A*.
		//String secuencia = IDAStar(actual);//buscamos la solucion con IDA*.
		if(secuencia!=null){//existe camino.
			char[] sol = new char[secuencia.length()];//secuencia de teclas.
			for(int i=0;i<secuencia.length(); i++){
				sol[i]=secuencia.charAt(i);
			}
			List<String> aux = new ArrayList<String>();
			if(teclasManual!=null && teclasManual.size()>0){//añadimos las teclas usadas por el usuario manualmente.
				for(String tecla : teclasManual){
					aux.add(tecla);
				}
			}
			for(int i = 0; i<sol.length; i++ ){
				aux.add(String.valueOf(sol[i]));
			}
			Escenario test = new Escenario(actual.getEscenario().getNivel(), true);//actualizamos los metadatos del nivel en la DB.
			copiarEscenarioActual(test, actual.getEscenario());
			if(test.hasGanado()){//actualizamos nivel.
				escenario.updateNivel(aux, null, 0, test, tipe, nodosTotal);
			}
			return sol;//devolvemos camino.
		}else{//no hay camino posible desde la posicion actual.
			return null;
		}
	}

	/**
	 * Devuelve una secuencia de pasos con la solucion generados durante por el algortimo.
	 * @param escenario - escenario actual.
	 * @param pasos - pasos actuales.
	 * @param teclasManual - teclas manuales usadas hasta ahora.
	 * @return Secuencia de caracteres con la solucion.
	 */
	public char[] solucion(Escenario escenario, int pasos, List<String> teclasManual){
		Node actual = new Node(escenario, pasos, escenario.placedBox(), "");//nodo actual del usuario.
		escenario.setIA(true);//marcamos que el usuario solicito ayuda de la IA.
		String tipe="AStar";//usamos el algoritmo A*.
		//String tipe="IDAStar"; //usamos el algoritmo IDA*.
		char[] solExist = Mapas.verSol(escenario.getNivel(), tipe);//comprobamos si el nivel ya tiene una solucion de la IA.
		if(solExist!=null && pasos==0){//ya hay una soluci�n almacenada de la IA y el jugador no realizo movimiento previo.
			return solExist;//devolvemos solucion desde posicion inicial.
		}else if(solExist!=null && pasos!=0){//hay solucion pero el jugador se encuentra en una posicion distinta de la inicial.
			Escenario test = new Escenario(actual.getEscenario().getNivel(), false);//escenario auxiliar.
			int posAux = 0;//auxiliar para calcular a partir de que secuencia se peude resolver el nivel en su estado actual.
			for (char c : solExist) {//buscamos si la posicion actual del usuario es un paso intermedio de la solucion.
				if(Arrays.deepEquals(test.getCas(), escenario.getCas())){//existe solucion guardada desde la posicion actual del usuario.
					char[] solAux = new char[solExist.length-posAux];//generamos array con la solucion.
					for(int i = 0; i<solAux.length; i++){//almacenamos caracteres.
						solAux[i] = solExist[i+posAux];
					}
					return solAux;//devolvemos la solucion modificada.
				}else{//avanzamos.
					test.realizarMovimiento(c);
					posAux++;
				}
			}
		}
		r = new Restricciones();//no hay solucion almacenada, la calculamos.
		long time_start, time_end;//Variables para calcular el tiempo de computo para hayar la solucion.
		time_start = System.currentTimeMillis();//empezamos el contador.
		String secuencia = AStar(actual, false);//buscamos la solucion con A*.
		//String secuencia = IDAStar(actual);//buscamos la solucion con IDA*.
		time_end = System.currentTimeMillis();//finalizamos contador.
		long time = time_end - time_start;//calculamos el tiempo total que demoro el calculo.
		if(secuencia!=null){//existe solucion.
			char[] sol = new char[secuencia.length()];//secuencia de teclas.
			for(int i=0;i<secuencia.length(); i++){
				sol[i]=secuencia.charAt(i);
			}
			List<String> aux = new ArrayList<String>();
			if(teclasManual!=null && teclasManual.size()>0){//añadimos las teclas usadas por el usuario manualmente.
				for(String tecla : teclasManual){
					aux.add(tecla);
				}
			}
			for(int i = 0; i<sol.length; i++ ){
				aux.add(String.valueOf(sol[i]));
			}
			Escenario test = new Escenario(actual.getEscenario().getNivel(), true);//actualizamos los metadatos del nivel en la DB.
			copiarEscenarioActual(test, actual.getEscenario());
			escenario.updateNivel(aux, null, time, test, tipe, nodosTotal);
			return sol;//devolvemos solucion.
		}else{//no hay solucion para el nivel en el estado actual.
			return null;
		}
	}

	/**
	 * M�todo que resuelve el nivel en base al algortimo AStar(A*).
	 * @param actual - nodo padre.
	 * @param nextStep - booleando que indica si se deasea solucion completa o parcial.
	 * @return Secuencia de movimientos hasta llegar a meta.
	 */
	private String AStar(Node actual, boolean nextStep){
		long time_start = 0;//Variables para calcular el tiempo si se eligio sugerir camino.
		nodosTotal = 0;//numero nodos estudiados.
		Comparator<Node> comparator = new MyComparator();//comparador que actuara en la cola de abiertos para ordenador nodos.
		//		Comparator<Node> comparator = new MyComparatorAdmissible();//con funcion evaluacion admisible.
		//		Comparator<Node> comparator = new MyComparatorVoraz();//con funcion evaluacion voraz.
		//		Comparator<Node> comparator = new MyComparatorTest();//con funcion de evaluacion prueba (sugerida por profesora).
		PriorityQueue<Node> abiertos = new PriorityQueue<Node>(comparator);//cola de prioridades con nodos a estudiar.
		List<Node> cerrados = new ArrayList<Node>();//lista con nodos ya estudiados.
		abiertos.add(actual);//aniadimos el nodo padre a la cola.
		if(nextStep){//emepezamos a contar limite de sugerir camino.
			time_start = System.currentTimeMillis();//empezamos el contador.
		}

		while (!abiertos.isEmpty()){//mientras cola tenga nodos buscamos solucion al nivel.
			if(nextStep && (System.currentTimeMillis()-time_start)>10000){//se eligio sugerir camino y tiempo mayor a 10seg.
				return masBueno(cerrados);
			}
			Node estudiando = abiertos.poll();//extraemos el nodo de la cabeza de la cola.
			if(estudiando.getEscenario().hasGanado()){//Existe solucion, salimos.
				nodosTotal = cerrados.size();//guardamos el numero total de nodos estudiados.
				cerrados.clear();//vaciamos lista.
				abiertos.clear();//vaciamos cola.
				return estudiando.getID();//devolvemos secuencia de movimientos para llegar a meta.
			}else{//no es nodo meta.
				List<Node> hijos = getHijos(estudiando);//generamos sus hijos.
				cerrados.add(estudiando);//aniadimos nodo padre a lista de estudiamos.
				if(!hijos.isEmpty()){//el nodo padre tiene hijos.
					for(Node h:hijos){
						if(!yaEstudiado(h, cerrados)&&!yaEnCola(h, abiertos)){//comprobamos si ese nodo fue ya estudiado o ya esta a la espera en cola.
							abiertos.add(h);//a�adimos el nuevo nodo a la cola.
						}
					}
				}
			}
		}
		nodosTotal = cerrados.size();//guardamos el numero total de nodos estudiados.
		cerrados.clear();//vaciamos lista.
		abiertos.clear();//vaciamos cola.
		return null;//no tiene solucion el nivel.
	}

	/**
	 * Metodo que devulve la ID del nodo mas prometedor en caso de que el usuario eligiera solucion parcial.
	 * @param cerrados - Lista de nodos estudiados.
	 * @return
	 */
	private String masBueno(List<Node> cerrados){
		Node aux = null;
		int max = -1;
		for(Node node : cerrados){//buscamos el nodo con mas cajas colocadas.
			if(node.getI()>max){
				aux = node;
			}
		}
		return aux.getID();
	}

	@SuppressWarnings("unused")
	/**
	 * M�todo que resuelve el nivel en base al algortimo IDAStar(IDA*).
	 * @param actual - nodo padre.
	 * @return Secuencia de movimientos hasta llegar a meta.
	 */
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
			}else{//cambiamos limite.
				nodosTotal = 0;
				bound = (int)1.2*aux;
			}
		}
	}

	/**
	 * Metodo recursivo que profundizara expandiendo todos los nodos que cumplan con el limite pasado.
	 * @param actual - nodo a expandir.
	 * @param bound - limite actual.
	 * @param listRepetido - lista de nodos ya estudiados.
	 * @return ID del nodo - si hay solucion, nuevo limite - si se supero el actual, infinito - si no hay solucion.
	 */
	private int IDAStarSearch(Node actual, int bound, List<Node> listRepetido){
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


	/**
	 * Genera todos los hijos del nodo recibido en base a lo movimientos posibles a realizar y a las restricciones.
	 * @param padre - nodo a expandir.
	 * @return Lista con los nodos hijos resultantes.
	 */
	private List<Node> getHijos(Node padre)
	{
		List<Node> hijos = new ArrayList<>();//lista de nodos hijos.
		List<Posicion> cajas = padre.getEscenario().getCajas();//lista de posicion cajas para controlar cual se movio.
		int i = 0;//contador.
		while(i<4){//maximo 4 movimientos a realizar (4 hijos posibles).
			Node aux = null;//nodo auxiliar.
			Posicion caja = null;//posicion caja movida.
			Escenario test = new Escenario(padre.getEscenario().getNivel(), true);//escenario auxiliar.
			copiarEscenarioActual(test, padre.getEscenario());//copiamos el escenario del nodo padre al auxiliar.
			test.setALTO(test.getCas().length);
			test.setANCHO(test.getCas()[0].length-1);
			switch (i) {//realizamos los movimientos.
			case 0://movimiento hacia arriba (W).
				if(test.realizarMovimiento('W')){//comprobamos si es posible el movimiento.
					aux = new Node(test, padre.getG()+1, test.placedBox(),padre.getID()+"W");//generamos el nodo tras el movumiento.
					caja = posicionCambiada(aux, cajas);
					if(aux.getEscenario().hasGanado() || comprobarRestricciones(aux, caja)){//comprobamos si el escenario resultante cumple las restricciones.
						hijos.add(aux);//guardamos nodo resultante en la lista de hijos.
					}
				}
				break;
			case 1://movimiento a la izquierda (A).
				if(test.realizarMovimiento('A')){//comprobamos si es posible el movimiento.
					aux = new Node(test, padre.getG()+1, test.placedBox(),padre.getID()+"A");//generamos el nodo tras el movumiento.
					caja = posicionCambiada(aux, cajas);
					if(aux.getEscenario().hasGanado() || comprobarRestricciones(aux, caja)){//comprobamos si el escenario resultante cumple las restricciones.
						hijos.add(aux);//guardamos nodo resultante en la lista de hijos.
					}
				}
				break;
			case 2://movimiento a la derecha (D).
				if(test.realizarMovimiento('D')){//comprobamos si es posible el movimiento.
					aux = new Node(test, padre.getG()+1, test.placedBox(),padre.getID()+"D");//generamos el nodo tras el movumiento.
					caja = posicionCambiada(aux, cajas);
					if(aux.getEscenario().hasGanado() || comprobarRestricciones(aux, caja)){//comprobamos si el escenario resultante cumple las restricciones.
						hijos.add(aux);//guardamos nodo resultante en la lista de hijos.
					}
				}
				break;
			default://movimiento hacia abajo (S).
				if(test.realizarMovimiento('S')){//comprobamos si es posible el movimiento.
					aux = new Node(test, padre.getG()+1, test.placedBox(),padre.getID()+"S");//generamos el nodo tras el movumiento.
					caja = posicionCambiada(aux, cajas);
					if(aux.getEscenario().hasGanado() || comprobarRestricciones(aux, caja)){//comprobamos si el escenario resultante cumple las restricciones.
						hijos.add(aux);//guardamos nodo resultante en la lista de hijos.
					}
				}
				break;
			}
			i++;
		}
		return hijos;
	}

	/**
	 * Dado un nodo y la lista de posiciones de cajas antes de generar ese nodo detecta si se movio alguna.
	 * @param aux - nodo actual.
	 * @param cajas - Lista con las posiciones de las cajas.
	 * @return posicion de la caja movida.
	 */
	private Posicion posicionCambiada(Node aux, List<Posicion> cajas) {
		List<Posicion> auxCajas = aux.getEscenario().getCajas();
		boolean movida=true;
		for(Posicion pos:auxCajas){
			for(Posicion posVieja:cajas){
				if((pos.x==posVieja.x)&&(pos.y==posVieja.y)){//esta caja no se movio.
					movida = false;//no fue movida esa caja.
					break;
				}
			}
			if(movida){//fue movida esta caja.
				return pos;
			}
			movida = true;
		}
		return null;//no se movio ninguna caja.
	}

	/**
	 * M�todo que recibe el nodo hijo generado y comprueba si cumple las restricciones para su posterior estudio.
	 * @param test - Nodo.
	 * @return false - si no las cumple, true - si las cumple.
	 */
	private boolean comprobarRestricciones(Node test, Posicion caja) {
		if(caja!=null){//vemos si alguna caja fue desplazada.
			if(r.esEsquina(test.getEscenario(), caja) || r.esUnBloque2x2(test.getEscenario(), caja)|| r.esBloqueEspecial_2(test.getEscenario(), caja) || r.esUnBloque3x3(test.getEscenario(), caja)
					|| r.esParedLimitada(test.getEscenario(), caja) || r.esCaminoBloqueante(test.getEscenario(), caja) || r.esBloqueEspecial_3(test.getEscenario(), caja)){
				return false;
			}else if(test.getEscenario().cajas()>2 && r.esBloqueEspecial_1(test.getEscenario(), caja)){
				return false;
			}
		}
		return true;
	}

	/**
	 * Copia el escenario de un nodo a otro auxiliar para su estudio.
	 * @param test - escenario destino.
	 * @param padre - escenario inicial.
	 */
	public void copiarEscenarioActual(Escenario test, Escenario padre){
		char[][] auxEsce = new char[padre.getALTO()][padre.getANCHO()+1];
		for (int j = 0; j < auxEsce.length; j++) {
			System.arraycopy(padre.getCas()[j], 0, auxEsce[j], 0, padre.getCas()[0].length);
		}
		test.setCas(auxEsce);
	}

	/**
	 * Comprueba si un nodo con ese mismo escenario ya esta en la cola.
	 * @param hijo - Nodo a insertar.
	 * @param abiertos - Cola de abiertos.
	 * @return booleano indicando si ya esta en la cola o no.
	 */
	private boolean yaEnCola(Node hijo, PriorityQueue<Node> abiertos){
		for(Node comparar : abiertos){//comparamos con los ya existentes.
			if(Arrays.deepEquals(hijo.getEscenario().getCas(), comparar.getEscenario().getCas())){
				return true;
			}
		}
		return false;	
	}

	/**
	 * Comprueba si un nodo con ese mismo escenario ya fue estudiado.
	 * @param hijo - Nodo a insertar.
	 * @param cerrados - Lista de cerrados.
	 * @return booleano indicando si ya esta en la lista o no.
	 */
	private boolean yaEstudiado(Node hijo, List<Node> cerrados){
		for(Node comparar : cerrados){//comparamos con los ya existentes.
			if(Arrays.deepEquals(hijo.getEscenario().getCas(), comparar.getEscenario().getCas())){
				return true;
			}
		}
		return false;	
	}
}

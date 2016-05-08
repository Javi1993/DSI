package interfaz;

import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import jugador.Player;
import motor.Node;

public class Escenario {

	private char [][] cas;//matriz del escenario.

	/** Enumeraciï¿½n para tipos de casilla */
	public enum TipoCasilla {VACIA, CAJA,
		JUGADOR, MURO, DESTINO,
		CAJA_SOBRE_DESTINO, JUGADOR_SOBRE_DESTINO	};

		// Definiciï¿½n de constantes para dimensiones del tablero.
		private int ANCHO;//ancho del tablero.
		private int ALTO;//alto del tablero.
		private MongoClient client;
		private MongoDatabase database;
		private MongoCollection<Document> collection;
		private int nivel;
		private int record;
		private String recordName;
		private boolean IA;//booleano para detectar si se ha activo el solver.

		/**
		 * Contructor de Escenario.
		 * @param nivel - ID del nivel al que corresponde.
		 * @param nodo - si es true es un escenario auxiliar para añadir a un nodo expandido.
		 */
		public Escenario(int nivel, boolean nodo){
			if(!nodo){
				getNivelMapa(nivel);
				this.setANCHO(cas[0].length-1);
				this.setALTO(cas.length);
			}
			this.setIA(false);
			this.setNivel(nivel);
		}

		/**
		 * Dadas unas coordenadas devuelve el tipo de casilla del tablero.
		 * @param x - coordenadas x.
		 * @param y - coordenadas y.
		 * @return Devuelve el tipo de casilla al que corresponde el caracter.
		 */
		public TipoCasilla obtenerTipo(int x,	int y){
			TipoCasilla casilla = null;
			switch(cas[x][y]){
			case '#':
				casilla = TipoCasilla.MURO;
				break;
			case ' ':
				casilla = TipoCasilla.VACIA;
				break;
			case '.':
				casilla = TipoCasilla.DESTINO;
				break;
			case '$':
				casilla = TipoCasilla.CAJA;
				break;
			case '*':	
				casilla = TipoCasilla.CAJA_SOBRE_DESTINO;
				break;
			case '@':
				casilla = TipoCasilla.JUGADOR;
				break;
			case '+':
				casilla = TipoCasilla.JUGADOR_SOBRE_DESTINO;
				break;
			}	
			return casilla;
		}

		/**
		 * Localiza la posicion del jugador en el tablero de juego.
		 * @return Coordenadas (x, y) de la posicion del jugador.
		 */
		public Posicion buscarJugador(){
			Posicion miPosicion = new Posicion();
			for(int x = 0; x<cas.length; x++){
				for(int y = 0; y<cas[x].length; y++){
					if(cas[x][y] == '@'){
						miPosicion.x = x;
						miPosicion.y = y;
					}else if( cas[x][y] == '+'){
						miPosicion.x = x;
						miPosicion.y = y;
					}
				}
			}
			return miPosicion;
		}

		/**
		 * Devuelve el numero de cajas colocadas en posicion correcta.
		 * @return
		 */
		public int placedBox(){
			int placedBox = 0;
			for(int x = 0; x<cas.length; x++){
				for(int y = 0; y<cas[x].length; y++){
					if(cas[x][y] == '*'){
						placedBox++;
					}
				}
			}
			return placedBox;
		}

		/**
		 * Devuelve un booleano que indica si se ha ganado la partida.
		 * @return
		 */
		public boolean hasGanado(){
			boolean victoria = false;
			for(int x = 0; x<cas.length; x++){
				for(int y = 0; y<cas[x].length; y++){
					if(cas[x][y] == '.'|cas[x][y] == '+'){
						return victoria;
					}
				}		
			}
			victoria = true;
			return victoria;
		}

		/**
		 * Imprime por pantalla el escenario
		 */
		public void escenarioToString(){
			for(int x = 0; x<cas.length; x++){
				for(int y = 0; y<cas[x].length; y++){
					System.out.print(cas[x][y]);
				}
				System.out.println();
			}
		}

		/**
		 * Reinicia el escenario volviendolo a cargar en su estado incial
		 */
		public void resetEscenario(){
			getNivelMapa(getNivel());
		}

		/**
		 * Recibe como parametro una tecla, y realiza el movimiento sobre el tablero de juego en funcion de la tecla.
		 * @param tecla - tecla de movimiento
		 * @return
		 */
		public boolean realizarMovimiento (char tecla){	
			boolean movimiento = false;//controla si el movimiento es correcto o no.
			Posicion miPosicion;//posicion del jugador.
			miPosicion = buscarJugador();//localizamos al jugador.
			Posicion nuevaPosicion = new Posicion();//nueva posicion del jugador tras movimiento.
			nuevaPosicion = miPosicion.posicionDesplazada(tecla);
			Posicion nuevaPosicion2 = new Posicion();//nueva posicion de la caja desplazada.
			nuevaPosicion2 = nuevaPosicion.posicionDesplazada(tecla);
			if(cas[nuevaPosicion.x][nuevaPosicion.y] == ' '){//la casilla a moverse es vacia.
				cas[nuevaPosicion.x][nuevaPosicion.y] = '@';
				if(cas[miPosicion.x][miPosicion.y] == '@'){//el jugador estaba en una casilla vacia.
					cas[miPosicion.x][miPosicion.y] = ' ';
				}else if(cas[miPosicion.x][miPosicion.y] == '+'){//el jugador estaba en una casilla destino.
					cas[miPosicion.x][miPosicion.y] = '.';
				}
				movimiento = true;//el movimiento es valido.
			}else if(cas[nuevaPosicion.x][nuevaPosicion.y] == '.'){//la casilla a moverse es destino.
				cas[nuevaPosicion.x][nuevaPosicion.y] = '+';
				if(cas[miPosicion.x][miPosicion.y] == '@'){//el jugador estaba en una casilla vacia.
					cas[miPosicion.x][miPosicion.y] = ' ';
				}else if(cas[miPosicion.x][miPosicion.y] == '+'){//el jugador estaba en una casilla destino.
					cas[miPosicion.x][miPosicion.y] = '.';//el movimiento es valido.
				}
				movimiento = true;
			}else if(cas[nuevaPosicion.x][nuevaPosicion.y] == '*'){//la casilla a moverse es una caja en destino.
				if(cas[nuevaPosicion2.x][nuevaPosicion2.y] == '*' | cas[nuevaPosicion2.x][nuevaPosicion2.y] == '#'){
					movimiento = false;//el movimiento es invalido porque la adyacente es muro o caja.
				}else if(cas[nuevaPosicion2.x][nuevaPosicion2.y] == '.'){//la casilla adyacente es destino.
					cas[nuevaPosicion2.x][nuevaPosicion2.y] = '*';
					cas[nuevaPosicion.x][nuevaPosicion.y] = '+';
					if(cas[miPosicion.x][miPosicion.y] == '@'){//el jugador estaba en una casilla vacia.
						cas[miPosicion.x][miPosicion.y] = ' ';
					}else if(cas[miPosicion.x][miPosicion.y] == '+'){//el jugador estaba en una casilla destino.
						cas[miPosicion.x][miPosicion.y] = '.';
					}
					movimiento = true;//el movimiento es valido.
				}else if(cas[nuevaPosicion2.x][nuevaPosicion2.y] == ' '){//la casilla adyacente es vacia.
					cas[nuevaPosicion2.x][nuevaPosicion2.y] = '$';
					cas[nuevaPosicion.x][nuevaPosicion.y] = '+';
					if(cas[miPosicion.x][miPosicion.y] == '@'){//el jugador estaba en una casilla vacia.
						cas[miPosicion.x][miPosicion.y] = ' ';
					}else if(cas[miPosicion.x][miPosicion.y] == '+'){//el jugador estaba en una casilla destino.
						cas[miPosicion.x][miPosicion.y] = '.';
					}
					movimiento = true;//el movimiento es valido.
				}
			}else if(cas[nuevaPosicion.x][nuevaPosicion.y] == '$'){//la casilla a moverse es una caja.
				if(cas[nuevaPosicion2.x][nuevaPosicion2.y] == ' '){//la posicion adyacente es vacia.
					cas[nuevaPosicion2.x][nuevaPosicion2.y] = '$';
					cas[nuevaPosicion.x][nuevaPosicion.y] = '@';
					if(cas[miPosicion.x][miPosicion.y] == '@'){//el jugador estaba en una casilla vacia.
						cas[miPosicion.x][miPosicion.y] = ' ';
					}else if(cas[miPosicion.x][miPosicion.y] == '+'){//el jugador estaba en una casilla destino.
						cas[miPosicion.x][miPosicion.y] = '.';
					}
					movimiento = true;//el movimiento es valido.
				}else if(cas[nuevaPosicion2.x][nuevaPosicion2.y] == '#'){//la posicion adyacente es un muro.
					movimiento = false;//el movimiento es invalido.
				}else if(cas[nuevaPosicion2.x][nuevaPosicion2.y] == '$'){//la posicion adyacente es una caja.
					movimiento = false;//el movimiento es invalido.
				}else if(cas[nuevaPosicion2.x][nuevaPosicion2.y] == '*'){//la posicion adyacente es una caja en destino.
					movimiento = false;//el movimiento es invalido.
				}else if(cas[nuevaPosicion2.x][nuevaPosicion2.y]==('.')){//la posicion adyacente es un destino.
					cas[nuevaPosicion.x][nuevaPosicion.y] = '@';
					cas[nuevaPosicion2.x][nuevaPosicion2.y] = '*';
					if(cas[miPosicion.x][miPosicion.y] == '@'){//el jugador estaba en una casilla vacia.
						cas[miPosicion.x][miPosicion.y] = ' ';
					}else if(cas[miPosicion.x][miPosicion.y] == '+'){//el jugador estaba en una casilla destino.
						cas[miPosicion.x][miPosicion.y] = '.';
					}
					movimiento=true;//el movimiento es valido.
				}
			}else if(cas[nuevaPosicion.x][nuevaPosicion.y] == '#'){//la siguiente casilla es un muro.
				movimiento = false;//el movimiento es invalido.
			}		
			return movimiento;
		}

		/**
		 * Devuelve la posicion de las cajas sin colocar.
		 * @return - Lista de cajas sin colocar.
		 */
		private List<Posicion> cajasSinColocar(){
			List<Posicion> cajas = new ArrayList<Posicion>();
			for(int x = 0; x<cas.length; x++){
				for(int y = 0; y<cas[x].length; y++){
					if(cas[x][y] == '$'){
						Posicion aux = new Posicion();
						aux.x=x;
						aux.y=y;
						cajas.add(aux);
					}
				}
			}
			return cajas;
		}

		/**
		 * Devuelve el numero de cajas que tiene el nivel (independientemente si estan o no colocadas).
		 * @return Entero con el numero de cajas.
		 */
		public int cajas(){
			int aux = 0;
			for(int x = 0; x<cas.length; x++){
				for(int y = 0; y<cas[x].length; y++){
					if(cas[x][y] == '$'||cas[x][y] == '*'){
						aux++;
					}
				}
			}
			return aux;
		}

		/**
		 * Devuelve la posicion de los destinos sin cajas.
		 * @return - Lista de destinos vacios.
		 */
		private List<Posicion> destinosLibres(){
			List<Posicion> destinos = new ArrayList<Posicion>();
			for(int x = 0; x<cas.length; x++){
				for(int y = 0; y<cas[x].length; y++){
					if(cas[x][y] == '.'||cas[x][y] == '+'){
						Posicion aux = new Posicion();
						aux.x=x;
						aux.y=y;
						destinos.add(aux);
					}
				}
			}
			return destinos;
		}

		@SuppressWarnings("unchecked")
		/**
		 * Actualiza un nivel en la BD tras haber sido completado por si se batio record o se hizo una jugada con solver
		 * que no estaba almacenada previamente.
		 * @param sol - secuencia de teclas.
		 * @param p - jugador.
		 * @param time - tiempo de calculo de la solucion (null si fue manual).
		 * @param incial - estado inicial del escenario.
		 * @param tipe - tipo de algoritmo usado (null si fue manual).
		 * @param nodos - total de nodos estudiados (null si fue manual).
		 */
		public void updateNivel(List<String> sol, Player p, long time, Escenario incial, String tipe, int nodos){
			client = new MongoClient("localhost", 27017);//conectamos.
			database = client.getDatabase("sokoban");//elegimos bbdd.
			collection = database.getCollection("niveles");
			Document nivel = collection.find(new Document("_id", this.getNivel())).first();
			if(p==null&&nivel.get(tipe)==null){//no hay guardada solucion de la IA.
				collection.updateOne(new Document("_id", this.getNivel()), new Document("$set", new Document(tipe+".Time", time)));
				collection.updateOne(new Document("_id", this.getNivel()), new Document("$set", new Document(tipe+".Nodos", nodos)));
				List<Document> seq = new ArrayList<Document>();//lista que guarda las teclas, funcion de evaluacion y su mapa.
				incial = new Escenario(incial.getNivel(), false);
				Node aux = new Node(incial, 0, incial.placedBox(), "");
				seq.add(new Document("mapa", incial.charArrayToList()).append("heuristica", aux.getF()));
				for (String c : sol) {//generamos todos los estados del escenario en base a la solucion.
					incial.realizarMovimiento(c.charAt(0));
					aux = new Node(incial, aux.getG()+1, incial.placedBox(), aux.getID()+c);
					seq.add(new Document("tecla", c).append("mapa", incial.charArrayToList()).append("heuristica", aux.getF()));
				}
				collection.updateOne(new Document("_id", this.getNivel()), new Document("$set", new Document(tipe+".seq",seq)));
			}
			List<String> pasosAnt =((List<String>)nivel.get("Jugada.seq"));
			if(pasosAnt==null||sol.size()<(pasosAnt.size()-1)){//la solucion es mejor que la almacenada.
				List<Document> seq = new ArrayList<Document>();//lista que guarda las teclas, heuristica y su mapa.
				incial = new Escenario(incial.getNivel(), false);
				Node aux = new Node(incial, 0, incial.placedBox(), "");
				seq.add(new Document("mapa", incial.charArrayToList()).append("heuristica", aux.getF()));
				if(p!=null){
					collection.updateOne(new Document("_id", this.getNivel()), new Document("$set", new Document("Jugada.Jugador",p.getId())));
					for (String c : sol) {//generamos todos los estados del escenario en base a la solucion.
						incial.realizarMovimiento(c.charAt(0));
						aux = new Node(incial, aux.getG()+1, incial.placedBox(), aux.getID()+c);
						seq.add(new Document("tecla", c).append("mapa", incial.charArrayToList()).append("heuristica", aux.getF()));
					}
				}else{
					collection.updateOne(new Document("_id", this.getNivel()), new Document("$set", new Document("Jugada.Jugador","IA")));
					collection.updateOne(new Document("_id", this.getNivel()), new Document("$set", new Document("Jugada.Time", time)));
					for (String c : sol) {//generamos todos los estados del escenario en base a la solucion.
						incial.realizarMovimiento(c.charAt(0));
						aux = new Node(incial, aux.getG()+1, incial.placedBox(), aux.getID()+c);
						seq.add(new Document("tecla", c).append("mapa", incial.charArrayToList()).append("heuristica", aux.getF()));
					}
				}
				collection.updateOne(new Document("_id", this.getNivel()), new Document("$set", new Document("Jugada.seq",seq)));
			}
			client.close();//cerramos conexion con la BD.
		}

		public int getANCHO() {
			return ANCHO;
		}
		public int getALTO() {
			return ALTO;
		}
		public void setANCHO(int ANCHO) {
			this.ANCHO = ANCHO;
		}
		public void setALTO(int ALTO) {
			this.ALTO = ALTO;
		}
		public List<Posicion> getCajas() {
			return cajasSinColocar();
		}
		public List<Posicion> getDestinos() {
			return destinosLibres();
		}

		/**
		 * Transforma un escenario dado en caracteres en un formato entendible por la BD.
		 * @return - Escenario en formato BD.
		 */
		public List<List<String>> charArrayToList(){
			List<List<String>> mapaAux = new ArrayList<List<String>>();
			for(int k = 0; k<cas.length; k++){//lo pasamos a un formato reconocible por la base de datos y sobre el que trabajaremos.
				List<String> aux = new ArrayList<String>();//cogemos la fila.
				for(int l = 0; l<cas[k].length; l++){
					aux.add(String.valueOf(cas[k][l]).replace('\u0000', ' '));//para caracteres vacios.
				}
				mapaAux.add(aux);//añadimos la fila.
			}
			return mapaAux;
		}

		@SuppressWarnings("unchecked")
		/**
		 * Dada una ID del mapa lo busca en la BD y lo genera guardando sus metadatos.
		 * @param nivel - ID del nivel.
		 */
		private void getNivelMapa(int nivel){
			client = new MongoClient("localhost", 27017);//conectamos.
			database = client.getDatabase("sokoban");//elegimos bbdd.
			collection = database.getCollection("niveles");
			Document nivelJSON = collection.find(new Document("_id",nivel)).first();
			if(nivelJSON!=null){//rellenamos las variables con los datos del nivel de la BD.
				if((List<String>)nivelJSON.get("Jugada.seq")!=null){//si ya se completo aniadimos el record y nombre del jugador.
					this.setRecord(((List<String>)nivelJSON.get("Jugada.seq")).size()-1);
					this.setRecordName(nivelJSON.getString("Jugada.Jugador"));
				}
				int i = 0, j = 0;//auxiliares para rellenar el escenario en la matriz de caracteres.
				Document jugada =  (Document) nivelJSON.get("Jugada");
				this.setRecord(((ArrayList<String>)jugada.get("seq")).size()-1);
				this.setRecordName(jugada.getString("Jugador"));
				List<Object> aux1 = (List<Object>) nivelJSON.get("Mapa");
				this.cas = new char[aux1.size()][(((List<Object>) aux1.get(0)).size())];
				for (Object ax1 : aux1) {
					List<Object> aux2 = (List<Object>) ax1;
					for (Object character : aux2) {
						this.cas[i][j] = character.toString().charAt(0);
						j++;
					}
					i++;
					j=0;
				}
			}
			client.close();//cerramos la conexion.
		}

		public int getNivel() {
			return nivel;
		}
		public void setNivel(int nivel) {
			this.nivel = nivel;
		}
		public char[][] getCas() {
			return cas;
		}
		public void setCas(char[][] cas) {
			this.cas = cas.clone();
		}

		public int getRecord() {
			return record;
		}

		public void setRecord(int record) {
			this.record = record;
		}

		public String getRecordName() {
			return recordName;
		}

		public void setRecordName(String recordName) {
			this.recordName = recordName;
		}

		public boolean isIA() {
			return IA;
		}

		public void setIA(boolean iA) {
			IA = iA;
		}
}

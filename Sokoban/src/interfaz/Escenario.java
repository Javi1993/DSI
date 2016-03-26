package interfaz;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class Escenario {

	private char [][] cas;
	
	/** Enumeración para tipos de casilla */
	public enum TipoCasilla {VACIA, CAJA,
	JUGADOR, MURO, DESTINO,
	CAJA_SOBRE_DESTINO, JUGADOR_SOBRE_DESTINO	};

	// Definición de constantes para dimensiones del tablero
	private int ANCHO;
	private int ALTO;
	private List<Posicion> cajas;
	private List<Posicion> destinos;
	private MongoClient client;
	private MongoDatabase database;
	private MongoCollection<Document> collection;
	private int nivel;

		//	public char [][] cas = new char[][] {
		//		//Matriz de caracteres que representa el tablero del juego.
		//		{' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
		//		{' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
		//		{' ',' ',' ',' ','#','#','#','#','#',' ',' ',' ',' ',' ',' '},
		//		{' ',' ',' ',' ','#',' ',' ',' ','#',' ',' ',' ',' ',' ',' '},
		//		{' ',' ',' ',' ','#',' ',' ','.','#',' ',' ',' ',' ',' ',' '},
		//		{' ',' ',' ',' ','#',' ','$',' ','#',' ',' ',' ',' ',' ',' '},
		//		{' ',' ',' ',' ','#','#','@','#','#',' ',' ',' ',' ',' ',' '},
		//		{' ',' ',' ',' ','#','#','$','#','#',' ',' ',' ',' ',' ',' '},
		//		{' ',' ',' ',' ','#',' ',' ',' ','#',' ',' ',' ',' ',' ',' '},
		//		{' ',' ',' ',' ','#',' ',' ',' ','#',' ',' ',' ',' ',' ',' '},
		//		{' ',' ',' ',' ','#','.',' ',' ','#',' ',' ',' ',' ',' ',' '},
		//		{' ',' ',' ',' ','#','#','#','#','#',' ',' ',' ',' ',' ',' '},
		//		{' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
		//		{' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '}
		//	};



	public Escenario(int nivel){
		//que reciba nivel y genere?¿?
		getNivelMapa(nivel);
		this.setNivel(nivel);
//		cas = new char[][] {
//			//Matriz de caracteres que representa el tablero del juego.
//			{' ',' ',' ',' ','#','#','#','#','#',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
//			{' ',' ',' ',' ','#',' ',' ',' ','#',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
//			{' ',' ',' ',' ','#','$',' ',' ','#',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
//			{' ',' ','#','#','#',' ',' ','$','#','#',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
//			{' ',' ','#',' ',' ','$',' ','$',' ','#',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
//			{'#','#','#',' ','#',' ','#','#',' ','#',' ',' ',' ','#','#','#','#','#','#',' '},
//			{'#',' ',' ',' ','#',' ','#','#',' ','#','#','#','#','#',' ',' ','.','.','#',' '},
//			{'#',' ','$',' ',' ','$',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','.','.','#',' '},
//			{'#','#','#','#','#',' ','#','#','#',' ','#','@','#','#',' ',' ','.','.','#',' '},
//			{' ',' ',' ',' ','#',' ',' ',' ',' ',' ','#','#','#','#','#','#','#','#','#',' '},
//			{' ',' ',' ',' ','#','#','#','#','#','#','#',' ',' ',' ',' ',' ',' ',' ',' ',' '}
//		};
		this.setANCHO(cas[0].length-1);
		this.setALTO(cas.length);
	}

	public TipoCasilla obtenerTipo(int x,	int y){
		/*Este método devuelve un objeto de tipo RellenarPorAlumno.TipoCasilla, 
		 * que contiene el tipo de la casilla situada enla posición (x,y)
		 */
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
	public Posicion buscarJugador(){
		//Devuelve la posición en la que se encuentra el jugador en cada momento del juego.
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

	public int placedBox()
	{//Devuelve el numero de cajas colocadas en posicion correcta
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

	public boolean hasGanado(){
		//Este método devuelve un booleano que indica si se ha ganado la partida.
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

	public void resetEscenario(){
		getNivelMapa(getNivel());
	}

	public boolean realizarMovimiento (char tecla){	
		//Este método recibe como parámetro una tecla, y realiza el movimiento sobre el tablero de juego en función de la tecla.
		boolean movimiento = false;
		Posicion miPosicion;
		miPosicion = buscarJugador();
		Posicion nuevaPosicion = new Posicion();
		nuevaPosicion = miPosicion.posicionDesplazada(tecla);
		Posicion nuevaPosicion2 = new Posicion();
		nuevaPosicion2 = nuevaPosicion.posicionDesplazada(tecla);
		if(cas[nuevaPosicion.x][nuevaPosicion.y] == ' '){
			cas[nuevaPosicion.x][nuevaPosicion.y] = '@';
			if(cas[miPosicion.x][miPosicion.y] == '@'){
				cas[miPosicion.x][miPosicion.y] = ' ';
			}
			else if(cas[miPosicion.x][miPosicion.y] == '+'){
				cas[miPosicion.x][miPosicion.y] = '.';
			}
			movimiento = true;
		}else if(cas[nuevaPosicion.x][nuevaPosicion.y] == '.'){
			cas[nuevaPosicion.x][nuevaPosicion.y] = '+';
			if(cas[miPosicion.x][miPosicion.y] == '@'){
				cas[miPosicion.x][miPosicion.y] = ' ';
			}
			else if(cas[miPosicion.x][miPosicion.y] == '+'){
				cas[miPosicion.x][miPosicion.y] = '.';
			}
			movimiento = true;
		}else if(cas[nuevaPosicion.x][nuevaPosicion.y] == '*'){
			if(cas[nuevaPosicion2.x][nuevaPosicion2.y] == '*' | cas[nuevaPosicion2.x][nuevaPosicion2.y] == '#'){
				//					System.out.println("El movimiento no se puede realizar");
			}
			if(cas[nuevaPosicion2.x][nuevaPosicion2.y] == '.'){
				cas[nuevaPosicion2.x][nuevaPosicion2.y] = '*';
				cas[nuevaPosicion.x][nuevaPosicion.y] = '+';
				if(cas[miPosicion.x][miPosicion.y] == '@'){
					cas[miPosicion.x][miPosicion.y] = ' ';
				}
				else if(cas[miPosicion.x][miPosicion.y] == '+'){
					cas[miPosicion.x][miPosicion.y] = '.';
				}
			}else if(cas[nuevaPosicion2.x][nuevaPosicion2.y] == ' '){
				cas[nuevaPosicion2.x][nuevaPosicion2.y] = '$';
				cas[nuevaPosicion.x][nuevaPosicion.y] = '+';
				cas[miPosicion.x][miPosicion.y] = ' ';

			}
		}else if(cas[nuevaPosicion.x][nuevaPosicion.y] == '$'){
			if(cas[nuevaPosicion2.x][nuevaPosicion2.y] == ' '){
				cas[nuevaPosicion2.x][nuevaPosicion2.y] = '$';
				cas[nuevaPosicion.x][nuevaPosicion.y] = '@';
				if(cas[miPosicion.x][miPosicion.y] == '@'){
					cas[miPosicion.x][miPosicion.y] = ' ';
				}
				else if(cas[miPosicion.x][miPosicion.y] == '+'){
					cas[miPosicion.x][miPosicion.y] = '.';
				}
				movimiento = true;
			}
			else if(cas[nuevaPosicion2.x][nuevaPosicion2.y] == '#'){
				//					System.out.println("El movimiento no se puede realizar");
				movimiento = false;
			}
			else if(cas[nuevaPosicion2.x][nuevaPosicion2.y] == '$'){
				//					System.out.println("El movimiento no se puede realizar");
				movimiento = false;
			}	
			else if(cas[nuevaPosicion2.x][nuevaPosicion2.y] == '*'){
				//					System.out.println("El movimiento no se puede realizar");
				movimiento = false;
			}
			else if(cas[nuevaPosicion2.x][nuevaPosicion2.y]==('.')){
				cas[nuevaPosicion.x][nuevaPosicion.y] = '@';
				cas[nuevaPosicion2.x][nuevaPosicion2.y] = '*';
				if(cas[miPosicion.x][miPosicion.y] == '@'){
					cas[miPosicion.x][miPosicion.y] = ' ';
				}
				else if(cas[miPosicion.x][miPosicion.y] == '+'){
					cas[miPosicion.x][miPosicion.y] = '.';
				}
				movimiento=true;
			}
		}else if(cas[nuevaPosicion.x][nuevaPosicion.y] == '#'){
			//				System.out.println("El movimiento no se puede realizar");
			movimiento = false;
		}		
		return movimiento;
	}

	private void cajasSinColocar()
	{//guarda las coordenadas de las cajas sin colocar
		cajas = new ArrayList<Posicion>();
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
	}

	private void destinosLibres()
	{
		destinos = new ArrayList<Posicion>();
		for(int x = 0; x<cas.length; x++){
			for(int y = 0; y<cas[x].length; y++){
				if(cas[x][y] == '.'){
					Posicion aux = new Posicion();
					aux.x=x;
					aux.y=y;
					destinos.add(aux);
				}
			}
		}
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
		cajasSinColocar();
		return cajas;
	}
	public List<Posicion> getDestinos() {
		destinosLibres();
		return destinos;
	}
	@SuppressWarnings("unchecked")
	private void getNivelMapa(int nivel){
		client = new MongoClient("localhost", 27017);//conectamos
		database = client.getDatabase("sokoban");//elegimos bbdd
		collection = database.getCollection("niveles");
		Document nivelJSON = collection.find(new Document("_id",nivel)).first();
		int i = 0;
		int j = 0;
		if(nivelJSON!=null)
		{
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
		client.close();
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
}

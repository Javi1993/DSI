package interfaz;

import java.util.ArrayList;
import java.util.List;

public class Escenario {
	char [][] cas = new char[][] {
		//Matriz de caracteres que representa el tablero del juego.
		{' ',' ',' ',' ','H','H','H','H','H',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
		{' ',' ',' ',' ','H',' ',' ',' ','H',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
		{' ',' ',' ',' ','H','*',' ',' ','H',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
		{' ',' ','H','H','H',' ',' ','*','H','H',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
		{' ',' ','H',' ',' ','*',' ','*',' ','H',' ',' ',' ',' ',' ',' ',' ',' ',' ',' '},
		{'H','H','H',' ','H',' ','H','H',' ','H',' ',' ',' ','H','H','H','H','H','H',' '},
		{'H',' ',' ',' ','H',' ','H','H',' ','H','H','H','H','H',' ',' ','O','O','H',' '},
		{'H',' ','*',' ',' ','*',' ',' ',' ',' ',' ',' ',' ',' ',' ',' ','O','O','H',' '},
		{'H','H','H','H','H',' ','H','H','H',' ','H','a','H','H',' ',' ','O','O','H',' '},
		{' ',' ',' ',' ','H',' ',' ',' ',' ',' ','H','H','H','H','H','H','H','H','H',' '},
		{' ',' ',' ',' ','H','H','H','H','H','H','H',' ',' ',' ',' ',' ',' ',' ',' ',' '}
	};
	private List<Posicion> cajas;
	private List<Posicion> destinos;

	//	char [][] cas = new char[][] {
	//		//Matriz de caracteres que representa el tablero del juego.
	//		{' ',' ',' ',' ',' ',' ',' ',' ',' '},
	//		{' ',' ',' ',' ','H',' ',' ',' ',' '},
	//		{' ',' ',' ','H','O','H',' ',' ',' '},
	//		{' ',' ',' ','H',' ','H',' ',' ',' '},
	//		{' ',' ',' ','H','*','H',' ',' ',' '},
	//		{' ',' ',' ','H','a','H',' ',' ',' '},
	//		{' ',' ',' ',' ','H',' ',' ',' ',' '},
	//		{' ',' ',' ',' ',' ',' ',' ',' ',' '},
	//		{' ',' ',' ',' ',' ',' ',' ',' ',' '},
	//		{' ',' ',' ',' ',' ',' ',' ',' ',' '},
	//		{' ',' ',' ',' ',' ',' ',' ',' ',' '},
	//		{' ',' ',' ',' ',' ',' ',' ',' ',' '}
	//	};

	/** Enumeración para tipos de casilla */
	public enum TipoCasilla {VACIA, CAJA,
		JUGADOR, MURO, DESTINO,
		CAJA_SOBRE_DESTINO, JUGADOR_SOBRE_DESTINO	};

		// Definición de constantes para dimensiones del tablero
		private int ANCHO = cas[0].length-1;
		private int ALTO = cas.length;

		public TipoCasilla obtenerTipo(int x,	int y){
			/*Este método devuelve un objeto de tipo RellenarPorAlumno.TipoCasilla, 
			 * que contiene el tipo de la casilla situada enla posición (x,y)
			 */
			TipoCasilla casilla = null;
			switch(cas[x][y]){
			case 'H':
				casilla = TipoCasilla.MURO;
				break;
			case ' ':
				casilla = TipoCasilla.VACIA;
				break;
			case 'O':
				casilla = TipoCasilla.DESTINO;
				break;
			case '*':
				casilla = TipoCasilla.CAJA;
				break;
			case 'X':	
				casilla = TipoCasilla.CAJA_SOBRE_DESTINO;
				break;
			case 'a':
				casilla = TipoCasilla.JUGADOR;
				break;
			case '@':
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
					if(cas[x][y] == 'a'){
						miPosicion.x = x;
						miPosicion.y = y;
					}else if( cas[x][y] == '@'){
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
					if(cas[x][y] == 'X'){
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
					if(cas[x][y] == 'O'|cas[x][y] == '@'){
						return victoria;
					}
				}		
			}
			victoria = true;
			return victoria;
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
				cas[nuevaPosicion.x][nuevaPosicion.y] = 'a';
				if(cas[miPosicion.x][miPosicion.y] == 'a'){
					cas[miPosicion.x][miPosicion.y] = ' ';
				}
				else if(cas[miPosicion.x][miPosicion.y] == '@'){
					cas[miPosicion.x][miPosicion.y] = 'O';
				}
				movimiento = true;
			}else if(cas[nuevaPosicion.x][nuevaPosicion.y] == 'O'){
				cas[nuevaPosicion.x][nuevaPosicion.y] = '@';
				if(cas[miPosicion.x][miPosicion.y] == 'a'){
					cas[miPosicion.x][miPosicion.y] = ' ';
				}
				else if(cas[miPosicion.x][miPosicion.y] == '@'){
					cas[miPosicion.x][miPosicion.y] = 'O';
				}
				movimiento = true;
			}else if(cas[nuevaPosicion.x][nuevaPosicion.y] == 'X'){
				if(cas[nuevaPosicion2.x][nuevaPosicion2.y] == 'X' | cas[nuevaPosicion2.x][nuevaPosicion2.y] == 'H'){
					System.out.println("El movimiento no se puede realizar");
				}
				if(cas[nuevaPosicion2.x][nuevaPosicion2.y] == 'O'){
					cas[nuevaPosicion2.x][nuevaPosicion2.y] = 'X';
					cas[nuevaPosicion.x][nuevaPosicion.y] = '@';
					if(cas[miPosicion.x][miPosicion.y] == 'a'){
						cas[miPosicion.x][miPosicion.y] = ' ';
					}
					else if(cas[miPosicion.x][miPosicion.y] == '@'){
						cas[miPosicion.x][miPosicion.y] = 'O';
					}
				}else if(cas[nuevaPosicion2.x][nuevaPosicion2.y] == ' '){
					cas[nuevaPosicion2.x][nuevaPosicion2.y] = '*';
					cas[nuevaPosicion.x][nuevaPosicion.y] = '@';
					cas[miPosicion.x][miPosicion.y] = 'O';

				}
			}else if(cas[nuevaPosicion.x][nuevaPosicion.y] == '*'){
				if(cas[nuevaPosicion2.x][nuevaPosicion2.y] == ' '){
					cas[nuevaPosicion2.x][nuevaPosicion2.y] = '*';
					cas[nuevaPosicion.x][nuevaPosicion.y] = 'a';
					if(cas[miPosicion.x][miPosicion.y] == 'a'){
						cas[miPosicion.x][miPosicion.y] = ' ';
					}
					else if(cas[miPosicion.x][miPosicion.y] == '@'){
						cas[miPosicion.x][miPosicion.y] = 'O';
					}
					movimiento = true;
				}
				else if(cas[nuevaPosicion2.x][nuevaPosicion2.y] == 'H'){
					System.out.println("El movimiento no se puede realizar");
					movimiento = false;
				}
				else if(cas[nuevaPosicion2.x][nuevaPosicion2.y] == '*'){
					System.out.println("El movimiento no se puede realizar");
					movimiento = false;
				}	
				else if(cas[nuevaPosicion2.x][nuevaPosicion2.y] == 'X'){
					System.out.println("El movimiento no se puede realizar");
					movimiento = false;
				}
				else if(cas[nuevaPosicion2.x][nuevaPosicion2.y]==('O')){
					cas[nuevaPosicion.x][nuevaPosicion.y] = 'a';
					cas[nuevaPosicion2.x][nuevaPosicion2.y] = 'X';
					if(cas[miPosicion.x][miPosicion.y] == 'a'){
						cas[miPosicion.x][miPosicion.y] = ' ';
					}
					else if(cas[miPosicion.x][miPosicion.y] == '@'){
						cas[miPosicion.x][miPosicion.y] = 'O';
					}
					movimiento=true;
				}
			}else if(cas[nuevaPosicion.x][nuevaPosicion.y] == 'H'){
				System.out.println("El movimiento no se puede realizar");
				movimiento = false;
			}		
			return movimiento;
		}

		private void cajasSinColocar()
		{//guarda las coordenadas de las cajas sin colocar
			cajas = new ArrayList<Posicion>();
			for(int x = 0; x<cas.length; x++){
				for(int y = 0; y<cas[x].length; y++){
					if(cas[x][y] == '*'){
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
					if(cas[x][y] == 'O'){
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
		public List<Posicion> getCajas() {
			cajasSinColocar();
			return cajas;
		}
		public List<Posicion> getDestinos() {
			destinosLibres();
			return destinos;
		}
}

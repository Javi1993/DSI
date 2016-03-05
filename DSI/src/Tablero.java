
public class Tablero {
	
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
	public RellenarPorAlumno.TipoCasilla obtenerTipo(int x,	int y){
		/*Este método devuelve un objeto de tipo RellenarPorAlumno.TipoCasilla, 
		 * que contiene el tipo de la casilla situada enla posición (x,y)
		 */
		RellenarPorAlumno.TipoCasilla casilla = null;
		switch(cas[x][y]){
		case 'H':
			casilla = RellenarPorAlumno.TipoCasilla.MURO;
			break;
		case ' ':
			casilla = RellenarPorAlumno.TipoCasilla.VACIA;
			break;
		case 'O':
			casilla = RellenarPorAlumno.TipoCasilla.DESTINO;
			break;
		case '*':
			casilla = RellenarPorAlumno.TipoCasilla.CAJA;
			break;
		case 'X':	
			casilla = RellenarPorAlumno.TipoCasilla.CAJA_SOBRE_DESTINO;
			break;
		case 'a':
			casilla = RellenarPorAlumno.TipoCasilla.JUGADOR;
			break;
		case '@':
			casilla = RellenarPorAlumno.TipoCasilla.JUGADOR_SOBRE_DESTINO;
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
}


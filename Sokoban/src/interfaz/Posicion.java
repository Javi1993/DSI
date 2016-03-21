package interfaz;


public class Posicion {
	public int x;
	public int y;

	public Posicion posicionDesplazada(char tecla){
		Posicion moverPosicion = new Posicion();
		switch (tecla) { 
		case 'W': 
		case 'w': 
			/*Posición original desplazada hacia arriba.
			 * ERROR:La consola muestra errores con y-- y x--.
			 */
			moverPosicion.x = x-1;
			moverPosicion.y = y;
			break;
		case 'X':
		case 'x': 
			//Posición original desplazada hacia abajo.
			moverPosicion.x = x+1;
			moverPosicion.y = y;
			break;
		case 'A':
		case 'a': 
			/*Posición original desplazada hacia la derecha. 
			 * ERROR:La consola muestra errores con y-- y x--.
			 */
			moverPosicion.y = y-1;
			moverPosicion.x = x;
			break;
		case 'D':
		case 'd': 
			//Posición original desplazada hacia la derecha.
			moverPosicion.y = y+1;
			moverPosicion.x = x;
			break;
		}
		return moverPosicion;
	}
}

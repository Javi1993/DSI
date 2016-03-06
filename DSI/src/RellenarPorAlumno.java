
@SuppressWarnings("serial")
public class RellenarPorAlumno extends NoTocarEstaClase {

	Tablero elTablero;
	/**
	 * M�todo main
	 */
	public static void main(String [] args)
	{	
		RellenarPorAlumno tmp = new RellenarPorAlumno();
		//Inicializamos la variable elTablero del objeto tmp.
		tmp.elTablero = new Tablero();
		tmp.establecerCoodenadas(ANCHO, ALTO);
	}

	/** Enumeraci�n para tipos de casilla */
	enum TipoCasilla {VACIA, CAJA,
		JUGADOR, MURO, DESTINO,
		CAJA_SOBRE_DESTINO, JUGADOR_SOBRE_DESTINO	};

		// Definici�n de constantes para dimensiones del tablero
		public static final int ANCHO = 19;
		public static final int ALTO = 11;

		/**
		 * M�todo que es invocado cada vez que el usuario pulsa cualquier tecla
		 * La implementaci�n que se proporciona s�lo responde a la tecla 'q', que
		 * hace que finalice la ejecuci�n del programa
		 * Si no se pulsa la tecla q, genera casillas aleatorias en el tablero y lo repinta
		 * EL C�DIGO QUE SE PROPORCIONA ES S�LO UN EJEMPLO, YA QUE ESTE M�TODO DEBER�
		 * SER ESCRITO POR COMPLETO POR EL ALUMNO PARA REALIZAR LAS CUESTIONES
		 * QUE SE PLANTEAN EN LA PR�CTICA 3
		 * @param tecla La tecla pulsada
		 */
		public void teclaPulsada(char tecla) {	
			int pasos = obtenerPasos();
			// En funci�n de la tecla pulsada hace algo
			switch (tecla) {

			case 'W':
			case 'A':
			case 'X':
			case 'D':
			case 'w':
			case 'a':
			case 'x':
			case 'd':
				if(!elTablero.hasGanado()){
					//Si se pulsa una de estas teclas realiza el movimiento solo si este es posible.
					if(elTablero.realizarMovimiento(tecla)){
						establecerPasos(pasos+1);
						if(elTablero.hasGanado())
						{
							System.out.println("Has ganado");
						}
					}else{
						System.out.println("El movimiento no se puede realizar.");
					}
					break;
				}
			case 'q':
			case 'Q':
				// Si se pulsa la tecla q sale del programa
				System.exit(0);
				break;
			default:
				//Salta el error
				if(!elTablero.hasGanado()){
					System.out.println("Introduzca la tecla correcta por favor.");}
			}
		}

		public void pintarTablero() {
			// Rellena el tablero con las casilla de la variable elTablero.
			for(int i=0; i< ALTO; i++) {
				for(int j=0; j<ANCHO; j++) {
					TipoCasilla tipoCasilla = elTablero.obtenerTipo(i,j);
					pintarCuadradoTablero(j, i, tipoCasilla);
				}
			}
		}
}
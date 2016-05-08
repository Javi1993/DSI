package motor;

import interfaz.Escenario;
import interfaz.Posicion;

public class Restricciones {

	/**
	 * Comprueba si una caja se movio a una esquina.
	 * @param test - Escenario.
	 * @param posicion - Posicion de la caja movida.
	 * @return booleano indicando si se genero una posicion muerta.
	 */
	public boolean esEsquina(Escenario test, Posicion posicion){
		if((test.getCas()[posicion.x-1][posicion.y]=='#'||test.getCas()[posicion.x+1][posicion.y]=='#')
				&&(test.getCas()[posicion.x][posicion.y-1]=='#'||test.getCas()[posicion.x][posicion.y+1]=='#'))
		{//se ha colocado una caja en una esquina que no es posicion destino
			return true;
		}
		return false;
	}

	/**
	 * Comprueba si una caja se movio a un camino bloqueante.
	 * @param test - Escenario.
	 * @param caja - Posicion de la caja movida.
	 * @return booleano indicando si se genero una posicion muerta.
	 */
	public boolean esCaminoBloqueante(Escenario test, Posicion caja) {
		boolean jugadorMedio = false;
		char[][] aux1 = new char[3][1];//caja con posibles paredes abajo y/o arriba
		aux1[0][0]=test.getCas()[caja.x+1][caja.y];
		aux1[1][0]=test.getCas()[caja.x][caja.y];
		aux1[2][0]=test.getCas()[caja.x-1][caja.y];

		char[][] aux2 = new char[1][3];//caja con posibles paredes izquierda y/o derecha
		aux2[0][0]=test.getCas()[caja.x][caja.y-1];
		aux2[0][1]=test.getCas()[caja.x][caja.y];
		aux2[0][2]=test.getCas()[caja.x][caja.y+1];
		if((aux1[0][0]=='#'||aux1[0][0]=='$'||aux1[0][0]=='*')
				&&(aux1[2][0]=='#'||aux1[2][0]=='$'||aux1[2][0]=='*')){//recorremos  derecha e izquierda del escenario por ese camino para ver si tiene salida para la caja
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
					}else if(!jugadorMedio&&test.getCas()[caja.x][caja.y+i]=='#'){
						return true;
					}
				}else if(!jugadorMedio&&test.getCas()[caja.x][caja.y+i]=='#'){//bloqueo en camino
					return true;
				}else if(!jugadorMedio&&(test.getCas()[caja.x][caja.y+i]=='$'||test.getCas()[caja.x][caja.y+i]=='*')&&((test.getCas()[caja.x+1][caja.y+i]=='#'||test.getCas()[caja.x+1][caja.y+i]=='$'||test.getCas()[caja.x+1][caja.y+i]=='*')
						||(test.getCas()[caja.x-1][caja.y+i]=='#'||test.getCas()[caja.x-1][caja.y+i]=='$'||test.getCas()[caja.x-1][caja.y+i]=='*'))){
					return true;
				}else{				
					return false;
				}
			}
			jugadorMedio = false;
			for(int i = 1; i<=caja.y; i++){//recorremos hacia izquierda
				if((test.getCas()[caja.x+1][caja.y-i]=='#'||test.getCas()[caja.x+1][caja.y-i]=='$'||test.getCas()[caja.x+1][caja.y-i]=='*')
						&&(test.getCas()[caja.x-1][caja.y-i]=='#'||test.getCas()[caja.x-1][caja.y-i]=='$'||test.getCas()[caja.x-1][caja.y-i]=='*')){
					if(test.getCas()[caja.x][caja.y-i]=='@'||test.getCas()[caja.x][caja.y-i]=='+'){
						jugadorMedio = true;
					}else if(test.getCas()[caja.x][caja.y-i]=='.'&&!jugadorMedio){
						return false;
					}else if(!jugadorMedio&&(test.getCas()[caja.x][caja.y-i]=='$'||test.getCas()[caja.x][caja.y-i]=='*')){
						//						System.out.println("CAMINO BLOQUEANTE HORIZONTAL 2 EN "+caja.x+","+caja.y);
						//						test.escenarioToString();
						return true;
					}else if(!jugadorMedio&&test.getCas()[caja.x][caja.y-i]=='#'){
						return true;
					}
				}else if(!jugadorMedio&&test.getCas()[caja.x][caja.y-i]=='#'){//bloqueo en camino
					return true;
				}else if(!jugadorMedio&&(test.getCas()[caja.x][caja.y-i]=='$'||test.getCas()[caja.x][caja.y-i]=='*')&&((test.getCas()[caja.x+1][caja.y-i]=='#'||test.getCas()[caja.x+1][caja.y-i]=='$'||test.getCas()[caja.x+1][caja.y-i]=='*')
						||(test.getCas()[caja.x-1][caja.y-i]=='#'||test.getCas()[caja.x-1][caja.y-i]=='$'||test.getCas()[caja.x-1][caja.y-i]=='*'))){
					return true;
				}else{
					return false;
				}
			}
			return true;
		}
		if((aux2[0][0]=='#'||aux2[0][0]=='$'||aux2[0][0]=='*')
				&&(aux2[0][2]=='#'||aux2[0][2]=='$'||aux2[0][2]=='*')){//recorremos arriba y abajo del escenario por ese camino para ver si tiene salida para la caja
			jugadorMedio = false;
			for(int i = 1; i<((test.getCas().length)-(caja.x)); i++){//recorremos hacia arriba
				if((test.getCas()[caja.x+i][caja.y+1]=='#'||test.getCas()[caja.x+i][caja.y+1]=='$'||test.getCas()[caja.x+i][caja.y+1]=='*')
						&&(test.getCas()[caja.x+i][caja.y-1]=='#'||test.getCas()[caja.x+i][caja.y-1]=='$'||test.getCas()[caja.x+i][caja.y-1]=='*')){
					if(test.getCas()[caja.x+i][caja.y]=='@'||test.getCas()[caja.x+i][caja.y]=='+'){
						jugadorMedio = true;
					}else if(test.getCas()[caja.x+i][caja.y]=='.'&&!jugadorMedio){
						return false;
					}else if(!jugadorMedio&&(test.getCas()[caja.x+i][caja.y]=='$'||test.getCas()[caja.x+i][caja.y]=='*')){
						//						System.out.println("CAMINO BLOQUEANTE VERTICAL 1 EN "+caja.x+","+caja.y);
						//						test.escenarioToString();
						return true;
					}else if(!jugadorMedio&&test.getCas()[caja.x+i][caja.y]=='#'){
						return true;
					}
				}else if(!jugadorMedio&&test.getCas()[caja.x+i][caja.y]=='#'){//bloqueo en camino
					return true;
				}else if(!jugadorMedio&&(test.getCas()[caja.x+i][caja.y]=='$'||test.getCas()[caja.x+i][caja.y]=='*')&&((test.getCas()[caja.x+i][caja.y+1]=='#'||test.getCas()[caja.x+i][caja.y+1]=='$'||test.getCas()[caja.x+i][caja.y+1]=='*')
						&&(test.getCas()[caja.x+i][caja.y-1]=='#'||test.getCas()[caja.x+i][caja.y-1]=='$'||test.getCas()[caja.x+i][caja.y-1]=='*'))){
					return true;
				}else{
					return false;
				}
			}
			jugadorMedio = false;
			for(int i = 1; i<=caja.x; i++){//recorremos hacia abajo
				if((test.getCas()[caja.x-i][caja.y+1]=='#'||test.getCas()[caja.x-i][caja.y+1]=='$'||test.getCas()[caja.x-i][caja.y+1]=='*')
						&&(test.getCas()[caja.x-i][caja.y-1]=='#'||test.getCas()[caja.x-i][caja.y-1]=='$'||test.getCas()[caja.x-i][caja.y-1]=='*')){
					if(test.getCas()[caja.x-i][caja.y]=='@'||test.getCas()[caja.x-i][caja.y]=='+'){
						jugadorMedio = true;
					}else if(test.getCas()[caja.x-i][caja.y]=='.'&&!jugadorMedio){
						return false;
					}else if(!jugadorMedio&&(test.getCas()[caja.x-i][caja.y]=='$'||test.getCas()[caja.x-i][caja.y]=='*')){
						//						System.out.println("CAMINO BLOQUEANTE VERTICAL 2 EN "+caja.x+","+caja.y);
						//						test.escenarioToString();
						return true;
					}else if(!jugadorMedio&&test.getCas()[caja.x-i][caja.y]=='#'){
						return true;
					}
				}else if(!jugadorMedio&&test.getCas()[caja.x-i][caja.y]=='#'){//bloqueo en camino
					return true;
				}else if(!jugadorMedio&&(test.getCas()[caja.x-i][caja.y]=='$'||test.getCas()[caja.x-i][caja.y]=='*')&&((test.getCas()[caja.x-i][caja.y+1]=='#'||test.getCas()[caja.x-i][caja.y+1]=='$'||test.getCas()[caja.x-i][caja.y+1]=='*')
						&&(test.getCas()[caja.x-i][caja.y-1]=='#'||test.getCas()[caja.x-i][caja.y-1]=='$'||test.getCas()[caja.x-i][caja.y-1]=='*'))){
					return true;
				}else{
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Comprueba si una caja se movio a una pared limitada.
	 * @param test - Escenario.
	 * @param caja - Posicion de la caja movida.
	 * @return booleano indicando si se genero una posicion muerta.
	 */
	public boolean esParedLimitada(Escenario test, Posicion caja) {
		char[][] aux1 = new char[3][1];//caja con posibles paredes abajo y/o arriba.
		aux1[0][0]=test.getCas()[caja.x+1][caja.y];
		aux1[1][0]=test.getCas()[caja.x][caja.y];
		aux1[2][0]=test.getCas()[caja.x-1][caja.y];

		char[][] aux2 = new char[1][3];//caja con posibles paredes izquierda y/o derecha.
		aux2[0][0]=test.getCas()[caja.x][caja.y-1];
		aux2[0][1]=test.getCas()[caja.x][caja.y];
		aux2[0][2]=test.getCas()[caja.x][caja.y+1];

		if(aux1[0][0]=='#'||aux1[2][0]=='#'){//recorremos  derecha e izquierda del escenario por ese camino para ver si tiene salida para la caja.
			for(int i = 1; i<((test.getCas()[0].length)-(caja.y)); i++){//recorremos hacia derecha.
				if(test.getCas()[caja.x][caja.y+i]=='#'){
					break;
				}else if(((test.getCas()[caja.x+1][caja.y+i]!='#')&&(test.getCas()[caja.x-1][caja.y+i]!='#'))
						||(test.getCas()[caja.x][caja.y+i]=='.'||test.getCas()[caja.x][caja.y+i]=='+')){
					return false;
				}
			}
			for(int i = 1; i<=caja.y; i++){//recorremos hacia izquierda.
				if(test.getCas()[caja.x][caja.y-i]=='#'){
					return true;
				}else if(((test.getCas()[caja.x+1][caja.y-i]!='#')&&(test.getCas()[caja.x-1][caja.y-i]!='#'))
						||(test.getCas()[caja.x][caja.y-i]=='.'||test.getCas()[caja.x][caja.y-i]=='+')){
					return false;
				}
			}
			return true;
		}

		if(aux2[0][0]=='#'||aux2[0][2]=='#'){//recorremos arriba y abajo del escenario por ese camino para ver si tiene salida para la caja.
			for(int i = 1; i<((test.getCas().length)-(caja.x)); i++){//recorremos hacia abajo.
				if(test.getCas()[caja.x+i][caja.y]=='#'){
					break;
				}else if(((test.getCas()[caja.x+i][caja.y+1]!='#')&&(test.getCas()[caja.x+i][caja.y-1]!='#'))
						||(test.getCas()[caja.x+i][caja.y]=='.'||test.getCas()[caja.x+i][caja.y]=='+')){
					return false;
				}
			}
			for(int i = 1; i<=caja.x; i++){//recorremos hacia arriba.
				if(test.getCas()[caja.x-i][caja.y]=='#'){
					return true;
				}if(((test.getCas()[caja.x-i][caja.y+1]!='#')&&(test.getCas()[caja.x-i][caja.y-1]!='#'))
						||(test.getCas()[caja.x-i][caja.y]=='.'||test.getCas()[caja.x-i][caja.y]=='+')){
					return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Comprueba si una caja se movio a un bloque 2x2.
	 * @param test - Escenario.
	 * @param caja - Posicion de la caja movida.
	 * @return booleano indicando si se genero una posicion muerta.
	 */
	public boolean esUnBloque2x2 (Escenario test, Posicion caja){
		//creamos los bloques 2x2 que rodean a nuestra caja (4 posibilidades).
		char[][] aux1 = new char[2][2];//caja en 0,0
		for(int i = 0; i<aux1.length; i++){
			for(int j = 0; j<aux1[i].length; j++){
				aux1[i][j]=test.getCas()[caja.x+i][caja.y+j];
			}
		}

		char[][] aux2 = new char[2][2];//caja en 0,1.
		for(int i = 0; i<aux2.length; i++){
			for(int j = 0; j<aux2[i].length; j++){
				aux2[i][j]=test.getCas()[caja.x+i][caja.y+j-1];
			}
		}

		char[][] aux3 = new char[2][2];//caja en 1,1.
		for(int i = 0; i<aux3.length; i++){
			for(int j = 0; j<aux3[i].length; j++){
				aux3[i][j]=test.getCas()[caja.x+i-1][caja.y+j-1];
			}
		}

		char[][] aux4 = new char[2][2];//caja en 1,0.
		for(int i = 0; i<aux4.length; i++){
			for(int j = 0; j<aux4[i].length; j++){
				aux4[i][j]=test.getCas()[caja.x+i-1][caja.y+j];
			}
		}

		if(testBloque2x2(aux1)||testBloque2x2(aux2)||testBloque2x2(aux3)||testBloque2x2(aux4)){//vemos si alguno forma bloque.
			return true;
		}
		return false;
	}

	/**
	 * Estudia los alrededores de la caja en busca de bloque 2x2.
	 * @param aux - matriz 2x2 con la caja a estudiar.
	 * @return booleano indicando si se genero una posicion muerta.
	 */
	private boolean testBloque2x2(char[][] aux) {
		int cntB=0;//contador cajas y muros.
		for(int i=0;i<aux.length; i++){
			for(int j = 0;j<aux[i].length; j++){
				if(aux[i][j]=='#' || aux[i][j]=='$' || aux[i][j]=='*'){
					cntB++;
				}
			}
		}
		if(cntB==4){//hay un bloque de cajas/muros que imposibilita resolver el nivel.
			return true;
		}else{
			return false;
		}
	}

	/**
	 * Comprueba si una caja se movio a un bloque 3x3.
	 * @param test - Escenario.
	 * @param caja - Posicion de la caja movida.
	 * @return booleano indicando si se genero una posicion muerta.
	 */
	public boolean esUnBloque3x3(Escenario test, Posicion caja) {
		//creamos los bloques 3x3 que rodean a nuestra caja (9 posibilidades).
		char[][] aux1 = new char[3][3];//caja en 0,0.
		for(int i = 0; i<aux1.length; i++){
			for(int j = 0; j<aux1[i].length; j++){
				try{
					aux1[i][j]=test.getCas()[caja.x+i][caja.y+j];
				}catch(IndexOutOfBoundsException e){
					aux1[i][j]=' ';
				}
			}
		}

		char[][] aux2 = new char[3][3];//caja en 0,1.
		for(int i = 0; i<aux2.length; i++){
			for(int j = 0; j<aux2[i].length; j++){
				try{
					aux2[i][j]=test.getCas()[caja.x+i][caja.y+j-1];
				}catch(IndexOutOfBoundsException e){
					aux2[i][j]=' ';
				}
			}
		}

		char[][] aux3 = new char[3][3];//caja en 0,2.
		for(int i = 0; i<aux3.length; i++){
			for(int j = 0; j<aux3[i].length; j++){
				try{
					aux3[i][j]=test.getCas()[caja.x+i][caja.y+j-2];
				}catch(IndexOutOfBoundsException e){
					aux3[i][j]=' ';
				}
			}
		}

		char[][] aux4 = new char[3][3];//caja en 1,0.
		for(int i = 0; i<aux4.length; i++){
			for(int j = 0; j<aux4[i].length; j++){
				try{
					aux4[i][j]=test.getCas()[caja.x+i-1][caja.y+j];
				}catch(IndexOutOfBoundsException e){
					aux4[i][j]=' ';
				}
			}
		}

		char[][] aux5 = new char[3][3];//caja en 1,1.
		for(int i = 0; i<aux5.length; i++){
			for(int j = 0; j<aux5[i].length; j++){
				try{
					aux5[i][j]=test.getCas()[caja.x+i-1][caja.y+j-1];
				}catch(IndexOutOfBoundsException e){
					aux5[i][j]=' ';
				}
			}
		}

		char[][] aux6 = new char[3][3];//caja en 1,2.
		for(int i = 0; i<aux6.length; i++){
			for(int j = 0; j<aux6[i].length; j++){
				try{
					aux6[i][j]=test.getCas()[caja.x+i-1][caja.y+j-2];
				}catch(IndexOutOfBoundsException e){
					aux6[i][j]=' ';
				}
			}
		}

		char[][] aux7 = new char[3][3];//caja en 2,0.
		for(int i = 0; i<aux7.length; i++){
			for(int j = 0; j<aux7[i].length; j++){
				try{
					aux7[i][j]=test.getCas()[caja.x+i-2][caja.y+j];
				}catch(IndexOutOfBoundsException e){
					aux7[i][j]=' ';
				}
			}
		}

		char[][] aux8 = new char[3][3];//caja en 2,1.
		for(int i = 0; i<aux8.length; i++){
			for(int j = 0; j<aux8[i].length; j++){
				try{
					aux8[i][j]=test.getCas()[caja.x+i-2][caja.y+j-1];
				}catch(IndexOutOfBoundsException e){
					aux8[i][j]=' ';
				}
			}
		}

		char[][] aux9 = new char[3][3];//caja en 2,2.
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
				||testBloque3x3(aux8)||testBloque3x3(aux9)){//vemos si alguno forma bloque 3x3.
			return true;
		}
		return false;
	}

	/**
	 * Estudia los alrededores de la caja en busca de bloque 3x3.
	 * @param aux - matriz 3x3 con la caja a estudiar.
	 * @return booleano indicando si se genero una posicion muerta.
	 */
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

	/**
	 * Comprueba si una caja se movio a un bloque bloque especial.
	 * @param test - Escenario.
	 * @param caja - Posicion de la caja movida.
	 * @return booleano indicando si se genero una posicion muerta.
	 */
	public boolean esBloqueEspecial_1(Escenario test, Posicion caja){
		if(esBloqueEspecial_1a(test, caja) || esBloqueEspecial_1b(test, caja) 
				||esBloqueEspecial_1c(test, caja) || esBloqueEspecial_1d(test, caja)){
			return true;
		}
		return false;
	}

	/**
	 * Comprueba si una caja se movio a un bloque bloque especial.
	 * @param test - Escenario.
	 * @param caja - Posicion de la caja movida.
	 * @return booleano indicando si se genero una posicion muerta.
	 */
	public boolean esBloqueEspecial_2(Escenario test, Posicion caja){
		if(esBloqueEspecial_2a(test, caja) || esBloqueEspecial_2b(test, caja)){
			return true;
		}
		return false;
	}

	/**
	 * Comprueba si una caja se movio a un bloque bloque especial.
	 * @param test - Escenario.
	 * @param caja - Posicion de la caja movida.
	 * @return booleano indicando si se genero una posicion muerta.
	 */
	public boolean esBloqueEspecial_3(Escenario test, Posicion caja){
		if(esBloqueEspecial_3a(test, caja) || esBloqueEspecial_3b(test, caja) 
				||esBloqueEspecial_3c(test, caja) || esBloqueEspecial_3d(test, caja)){
			return true;
		}
		return false;
	}

	/**
	 * Metodo que comprueba escenarios tipo 1a que pueden dejar el nivel irresoluble.
	 * @param test - Escenario actual.
	 * @param posicion - Posicion de la caja a evaluar.
	 * @return true - es bloque, false - no es bloque.
	 */
	private boolean esBloqueEspecial_1a(Escenario test, Posicion caja) {
		char[][] aux1 = new char[3][4];//Caja en (0, 1).
		for(int i = 0; i<aux1.length; i++){//                    #$$#
			for(int j = 0; j<aux1[i].length; j++){//             #  #
				try{//                                            $# 
					aux1[i][j]=test.getCas()[caja.x+i][caja.y-1+j];
				}catch(IndexOutOfBoundsException e){//nos salimos de los limites, damos valor vacio.
					aux1[i][j]=' ';
				}
			}
		}

		char[][] aux2 = new char[3][4];//Caja en (0, 2).
		for(int i = 0; i<aux2.length; i++){
			for(int j = 0; j<aux2[i].length; j++){
				try{
					aux2[i][j]=test.getCas()[caja.x+i][caja.y-2+j];
				}catch(IndexOutOfBoundsException e){
					aux1[i][j]=' ';
				}
			}
		}

		char[][] aux3 = new char[3][4];//Caja en (2, 1).
		for(int i = 0; i<aux3.length; i++){
			for(int j = 0; j<aux3[i].length; j++){
				try{
					aux3[i][j]=test.getCas()[caja.x+i-2][caja.y-1+j];
				}catch(IndexOutOfBoundsException e){
					aux1[i][j]=' ';
				}
			}
		}

		if(testBloqueEspecial_1a(aux1)||testBloqueEspecial_1a(aux2)||testBloqueEspecial_1a(aux3)){//vemos si alguno forma bloque.
			return true;
		}
		return false;
	}

	/**
	 * Metodo que comprueba escenarios tipo 1b que pueden dejar el nivel irresoluble.
	 * @param test - Escenario actual.
	 * @param posicion - Posicion de la caja a evaluar.
	 * @return true - es bloque, false - no es bloque.
	 */
	private boolean esBloqueEspecial_1b(Escenario test, Posicion caja) {
		char[][] aux1 = new char[3][4];//Caja en (0, 2).
		for(int i = 0; i<aux1.length; i++){//                     #$
			for(int j = 0; j<aux1[i].length; j++){//             #  #
				try{//                                           #$$#
					aux1[i][j]=test.getCas()[caja.x+i][caja.y-2+j];
				}catch(IndexOutOfBoundsException e){//nos salimos de los limites, damos valor vacio.
					aux1[i][j]=' ';
				}
			}
		}

		char[][] aux2 = new char[3][4];//Caja en (2, 1).
		for(int i = 0; i<aux2.length; i++){
			for(int j = 0; j<aux2[i].length; j++){
				try{
					aux2[i][j]=test.getCas()[caja.x+i-2][caja.y-1+j];
				}catch(IndexOutOfBoundsException e){
					aux1[i][j]=' ';
				}
			}
		}

		char[][] aux3 = new char[3][4];//Caja en (2, 2).
		for(int i = 0; i<aux3.length; i++){
			for(int j = 0; j<aux3[i].length; j++){
				try{
					aux3[i][j]=test.getCas()[caja.x+i-2][caja.y-2+j];
				}catch(IndexOutOfBoundsException e){
					aux1[i][j]=' ';
				}
			}
		}

		if(testBloqueEspecial_1b(aux1)||testBloqueEspecial_1b(aux2)||testBloqueEspecial_1b(aux3)){//vemos si alguno forma bloque.
			return true;
		}
		return false;
	}

	/**
	 * Metodo que comprueba escenarios tipo 1c que pueden dejar el nivel irresoluble.
	 * @param test - Escenario actual.
	 * @param posicion - Posicion de la caja a evaluar.
	 * @return true - es bloque, false - no es bloque.
	 */
	private boolean esBloqueEspecial_1c(Escenario test, Posicion caja) {
		char[][] aux1 = new char[4][3];//Caja en (1, 0)           ##
		for(int i = 0; i<aux1.length; i++){//                    $ $
			for(int j = 0; j<aux1[i].length; j++){//             # $
				try{//                                            ## 
					aux1[i][j]=test.getCas()[caja.x+i-1][caja.y+j];
				}catch(IndexOutOfBoundsException e){//nos salimos de los limites, damos valor vacio.
					aux1[i][j]=' ';
				}
			}
		}

		char[][] aux2 = new char[4][3];//Caja en (1, 2).
		for(int i = 0; i<aux2.length; i++){
			for(int j = 0; j<aux2[i].length; j++){
				try{
					aux2[i][j]=test.getCas()[caja.x+i-1][caja.y-2+j];
				}catch(IndexOutOfBoundsException e){
					aux1[i][j]=' ';
				}
			}
		}

		char[][] aux3 = new char[4][3];//Caja en (2, 2).
		for(int i = 0; i<aux3.length; i++){
			for(int j = 0; j<aux3[i].length; j++){
				try{
					aux3[i][j]=test.getCas()[caja.x+i-2][caja.y-2+j];
				}catch(IndexOutOfBoundsException e){
					aux1[i][j]=' ';
				}
			}
		}

		if(testBloqueEspecial_1c(aux1)||testBloqueEspecial_1c(aux2)||testBloqueEspecial_1c(aux3)){//vemos si alguno forma bloque.
			return true;
		}
		return false;
	}

	/**
	 * Metodo que comprueba escenarios tipo 1d que pueden dejar el nivel irresoluble.
	 * @param test - Escenario actual.
	 * @param posicion - Posicion de la caja a evaluar.
	 * @return true - es bloque, false - no es bloque.
	 */
	private boolean esBloqueEspecial_1d(Escenario test, Posicion caja) {
		char[][] aux1 = new char[4][3];//Caja en (1, 0)           ##
		for(int i = 0; i<aux1.length; i++){//                     $ #
			for(int j = 0; j<aux1[i].length; j++){//              $ $
				try{//                                            ## 
					aux1[i][j]=test.getCas()[caja.x+i-1][caja.y+j];
				}catch(IndexOutOfBoundsException e){//nos salimos de los limites, damos valor vacio.
					aux1[i][j]=' ';
				}
			}
		}

		char[][] aux2 = new char[4][3];//Caja en (2, 0).
		for(int i = 0; i<aux2.length; i++){
			for(int j = 0; j<aux2[i].length; j++){
				try{
					aux2[i][j]=test.getCas()[caja.x+i-2][caja.y+j];
				}catch(IndexOutOfBoundsException e){
					aux1[i][j]=' ';
				}
			}
		}

		char[][] aux3 = new char[4][3];//Caja en (2, 2).
		for(int i = 0; i<aux3.length; i++){
			for(int j = 0; j<aux3[i].length; j++){
				try{
					aux3[i][j]=test.getCas()[caja.x+i-2][caja.y-2+j];
				}catch(IndexOutOfBoundsException e){
					aux1[i][j]=' ';
				}
			}
		}

		if(testBloqueEspecial_1d(aux1)||testBloqueEspecial_1d(aux2)||testBloqueEspecial_1d(aux3)){//vemos si alguno forma bloque.
			return true;
		}
		return false;
	}

	/**
	 * Estudia los alrededores de la caja en busca de un bloque especial.
	 * @param aux - matriz con la caja a estudiar y alrededores.
	 * @return booleano indicando si se genero una posicion muerta.
	 */
	private boolean testBloqueEspecial_1a(char[][] aux) {
		if((aux[0][1] == '$' || aux[0][1] == '*') && (aux[0][2] == '$' || aux[0][2] == '*') 
				&& (aux[2][1] == '$' || aux[2][1] == '*' || aux[2][1] == '#')){
			if(aux[0][0] == '#' && aux[0][3] == '#' && aux[1][0] == '#' && aux[1][1] == ' '
					&& aux[1][2] == ' ' && aux[1][3] == '#' && (aux[2][2] == '#'||aux[2][2] == '$'||aux[2][2] == '*')){
				return true;
			}else{
				return false;
			}
		}
		return false;
	}

	/**
	 * Estudia los alrededores de la caja en busca de un bloque especial.
	 * @param aux - matriz con la caja a estudiar y alrededores.
	 * @return booleano indicando si se genero una posicion muerta.
	 */
	private boolean testBloqueEspecial_1b(char[][] aux) {
		if((aux[2][1] == '$' || aux[2][1] == '*') && (aux[2][2] == '$' || aux[2][2] == '*') 
				&& (aux[0][2] == '$' || aux[0][2] == '*' || aux[0][2] == '#')){
			if(aux[1][0] == '#' && aux[2][0] == '#' && aux[1][3] == '#' && aux[1][1] == ' '
					&& aux[1][2] == ' ' && aux[2][3] == '#' && (aux[0][1] == '#'||aux[0][1] == '$'||aux[0][1] == '*')){
				return true;
			}else{
				return false;
			}
		}
		return false;
	}

	/**
	 * Estudia los alrededores de la caja en busca de un bloque especial.
	 * @param aux - matriz con la caja a estudiar y alrededores.
	 * @return booleano indicando si se genero una posicion muerta.
	 */
	private boolean testBloqueEspecial_1c(char[][] aux) {
		if((aux[1][2] == '$' || aux[1][2] == '*') && (aux[2][2] == '$' || aux[2][2] == '*') 
				&& (aux[1][0] == '$' || aux[1][0] == '*' || aux[1][0] == '#')){
			if(aux[0][1] == '#' && aux[0][2] == '#' && aux[3][1] == '#' && aux[1][1] == ' '
					&& aux[2][1] == ' ' && aux[3][2] == '#' && (aux[2][0] == '#'||aux[2][0] == '$'||aux[2][0] == '*')){
				return true;
			}else{
				return false;
			}
		}
		return false;
	}

	/**
	 * Estudia los alrededores de la caja en busca de un bloque especial.
	 * @param aux - matriz con la caja a estudiar y alrededores.
	 * @return booleano indicando si se genero una posicion muerta.
	 */
	private boolean testBloqueEspecial_1d(char[][] aux) {
		if((aux[1][0] == '$' || aux[1][0] == '*') && (aux[2][0] == '$' || aux[2][0] == '*') 
				&& (aux[2][2] == '$' || aux[2][2] == '*' || aux[2][2] == '#')){
			if(aux[0][0] == '#' && aux[0][1] == '#' && aux[3][0] == '#' && aux[1][1] == ' '
					&& aux[2][1] == ' ' && aux[3][1] == '#' && (aux[1][2] == '#'||aux[1][2] == '$'||aux[1][2] == '*')){
				return true;
			}else{
				return false;
			}
		}
		return false;
	}

	/**
	 * Metodo que comprueba escenarios tipo 2a que pueden dejar el nivel irresoluble.
	 * @param test - Escenario actual.
	 * @param posicion - Posicion de la caja a evaluar.
	 * @return true - es bloque, false - no es bloque.
	 */
	private boolean esBloqueEspecial_2a(Escenario test, Posicion caja) {
		char[][] aux1 = new char[2][3];//Caja en (0, 1).
		for(int i = 0; i<aux1.length; i++){//                     $#
			for(int j = 0; j<aux1[i].length; j++){//             #$
				try{ 
					aux1[i][j]=test.getCas()[caja.x+i][caja.y-1+j];
				}catch(IndexOutOfBoundsException e){//nos salimos de los limites, damos valor vacio.
					aux1[i][j]=' ';
				}
			}
		}
		char[][] aux2 = new char[2][3];//Caja en (1, 1).
		for(int i = 0; i<aux2.length; i++){
			for(int j = 0; j<aux2[i].length; j++){
				try{
					aux2[i][j]=test.getCas()[caja.x+i-1][caja.y-1+j];
				}catch(IndexOutOfBoundsException e){//nos salimos de los limites, damos valor vacio.
					aux2[i][j]=' ';
				}
			}
		}
		if(testBloqueEspecial_2a(aux1)||testBloqueEspecial_2a(aux2)){//vemos si alguno forma bloque.
			return true;
		}
		return false;
	}

	/**
	 * Metodo que comprueba escenarios tipo 2b que pueden dejar el nivel irresoluble.
	 * @param test - Escenario actual.
	 * @param posicion - Posicion de la caja a evaluar.
	 * @return true - es bloque, false - no es bloque.
	 */
	private boolean esBloqueEspecial_2b(Escenario test, Posicion caja) {
		char[][] aux1 = new char[3][2];//Caja en (1, 0).
		for(int i = 0; i<aux1.length; i++){//                    #
			for(int j = 0; j<aux1[i].length; j++){//            $$ 
				try{//											# 
					aux1[i][j]=test.getCas()[caja.x+i-1][caja.y+j];
				}catch(IndexOutOfBoundsException e){//nos salimos de los limites, damos valor vacio.
					aux1[i][j]=' ';
				}
			}
		}
		char[][] aux2 = new char[3][2];//Caja en (1, 1)
		for(int i = 0; i<aux2.length; i++){
			for(int j = 0; j<aux2[i].length; j++){
				try{
					aux2[i][j]=test.getCas()[caja.x+i-1][caja.y-1+j];
				}catch(IndexOutOfBoundsException e){//nos salimos de los limites, damos valor vacio.
					aux2[i][j]=' ';
				}
			}
		}
		if(testBloqueEspecial_2b(aux1)||testBloqueEspecial_2b(aux2)){//vemos si alguno forma bloque.
			return true;
		}
		return false;
	}

	/**
	 * Estudia los alrededores de la caja en busca de un bloque especial.
	 * @param aux - matriz con la caja a estudiar y alrededores.
	 * @return booleano indicando si se genero una posicion muerta.
	 */
	private boolean testBloqueEspecial_2a(char[][] aux) {
		if((aux[0][1] == '$'|| aux[0][1] == '*') && (aux[1][1] == '$' || aux[1][1] == '*')){
			if(aux[0][2] == '#' && aux[1][0] == '#'){
				return true;
			}else if(aux[0][0] == '#' && aux[1][2] == '#'){
				return true;
			}
		}
		return false;
	}

	/**
	 * Estudia los alrededores de la caja en busca de un bloque especial.
	 * @param aux - matriz con la caja a estudiar y alrededores.
	 * @return booleano indicando si se genero una posicion muerta.
	 */
	private boolean testBloqueEspecial_2b(char[][] aux) {
		if((aux[1][0] == '$'|| aux[0][1] == '*') && (aux[1][1] == '$' || aux[1][1] == '*')){
			if(aux[0][1] == '#' && aux[2][0] == '#'){
				return true;
			}else if(aux[0][0] == '#' && aux[2][1] == '#'){
				return true;
			}
		}
		return false;
	}

	/**
	 * Metodo que comprueba escenarios tipo 3a que pueden dejar el nivel irresoluble.
	 * @param test - Escenario actual.
	 * @param posicion - Posicion de la caja a evaluar.
	 * @return true - es bloque, false - no es bloque.
	 */
	private boolean esBloqueEspecial_3a(Escenario test, Posicion caja) {
		char[][] aux1 = new char[3][4];//Caja en (2, 1).
		for(int i = 0; i<aux1.length; i++){//                   ##
			for(int j = 0; j<aux1[i].length; j++){//           #  #
				try{//											$$ 
					aux1[i][j]=test.getCas()[caja.x+i-2][caja.y+j-1];
				}catch(IndexOutOfBoundsException e){//nos salimos de los limites, damos valor vacio.
					aux1[i][j]=' ';
				}
			}
		}
		char[][] aux2 = new char[3][4];//Caja en (2, 2).
		for(int i = 0; i<aux2.length; i++){
			for(int j = 0; j<aux2[i].length; j++){
				try{
					aux2[i][j]=test.getCas()[caja.x+i-2][caja.y-2+j];
				}catch(IndexOutOfBoundsException e){//nos salimos de los limites, damos valor vacio.
					aux2[i][j]=' ';
				}
			}
		}
		if(testBloqueEspecial_3a(aux1)||testBloqueEspecial_3a(aux2)){//vemos si alguno forma bloque.
			return true;
		}
		return false;
	}

	/**
	 * Metodo que comprueba escenarios tipo 3b que pueden dejar el nivel irresoluble.
	 * @param test - Escenario actual.
	 * @param posicion - Posicion de la caja a evaluar.
	 * @return true - es bloque, false - no es bloque.
	 */
	private boolean esBloqueEspecial_3b(Escenario test, Posicion caja) {
		char[][] aux1 = new char[3][4];//Caja en (0, 1).
		for(int i = 0; i<aux1.length; i++){//                   $$
			for(int j = 0; j<aux1[i].length; j++){//           #  #
				try{//											## 
					aux1[i][j]=test.getCas()[caja.x+i][caja.y+j-1];
				}catch(IndexOutOfBoundsException e){//nos salimos de los limites, damos valor vacio.
					aux1[i][j]=' ';
				}
			}
		}
		char[][] aux2 = new char[3][4];//Caja en (0, 2)
		for(int i = 0; i<aux2.length; i++){
			for(int j = 0; j<aux2[i].length; j++){
				try{
					aux2[i][j]=test.getCas()[caja.x+i][caja.y-2+j];
				}catch(IndexOutOfBoundsException e){//nos salimos de los limites, damos valor vacio.
					aux2[i][j]=' ';
				}
			}
		}
		if(testBloqueEspecial_3b(aux1)||testBloqueEspecial_3b(aux2)){//vemos si alguno forma bloque.
			return true;
		}
		return false;
	}

	/**
	 * Metodo que comprueba escenarios tipo 3c que pueden dejar el nivel irresoluble.
	 * @param test - Escenario actual.
	 * @param posicion - Posicion de la caja a evaluar.
	 * @return true - es bloque, false - no es bloque.
	 */
	private boolean esBloqueEspecial_3c(Escenario test, Posicion caja) {
		char[][] aux1 = new char[4][3];//Caja en (1, 0).        #
		for(int i = 0; i<aux1.length; i++){//                  $ # 
			for(int j = 0; j<aux1[i].length; j++){//           $ #
				try{//											# 
					aux1[i][j]=test.getCas()[caja.x+i-1][caja.y+j];
				}catch(IndexOutOfBoundsException e){//nos salimos de los limites, damos valor vacio.
					aux1[i][j]=' ';
				}
			}
		}
		char[][] aux2 = new char[4][3];//Caja en (2, 0).
		for(int i = 0; i<aux2.length; i++){
			for(int j = 0; j<aux2[i].length; j++){
				try{
					aux2[i][j]=test.getCas()[caja.x+i-2][caja.y+j];
				}catch(IndexOutOfBoundsException e){//nos salimos de los limites, damos valor vacio.
					aux2[i][j]=' ';
				}
			}
		}
		if(testBloqueEspecial_3c(aux1)||testBloqueEspecial_3c(aux2)){//vemos si alguno forma bloque.
			return true;
		}
		return false;
	}

	/**
	 * Metodo que comprueba escenarios tipo 3d que pueden dejar el nivel irresoluble.
	 * @param test - Escenario actual.
	 * @param posicion - Posicion de la caja a evaluar.
	 * @return true - es bloque, false - no es bloque.
	 */
	private boolean esBloqueEspecial_3d(Escenario test, Posicion caja) {
		char[][] aux1 = new char[4][3];//Caja en (1, 2).        #
		for(int i = 0; i<aux1.length; i++){//                  # $ 
			for(int j = 0; j<aux1[i].length; j++){//           # $
				try{//											# 
					aux1[i][j]=test.getCas()[caja.x+i-1][caja.y+j-2];
				}catch(IndexOutOfBoundsException e){//nos salimos de los limites, damos valor vacio.
					aux1[i][j]=' ';
				}
			}
		}
		char[][] aux2 = new char[4][3];//Caja en (2, 2).
		for(int i = 0; i<aux2.length; i++){
			for(int j = 0; j<aux2[i].length; j++){
				try{
					aux2[i][j]=test.getCas()[caja.x+i-2][caja.y+j-2];
				}catch(IndexOutOfBoundsException e){//nos salimos de los limites, damos valor vacio.
					aux2[i][j]=' ';
				}
			}
		}
		if(testBloqueEspecial_3d(aux1)||testBloqueEspecial_3d(aux2)){//vemos si alguno forma bloque.
			return true;
		}
		return false;
	}

	/**
	 * Estudia los alrededores de la caja en busca de un bloque especial.
	 * @param aux - matriz con la caja a estudiar y alrededores.
	 * @return booleano indicando si se genero una posicion muerta.
	 */
	private boolean testBloqueEspecial_3a(char[][] aux) {
		if((aux[2][1] == '$'|| aux[2][1] == '*') && (aux[2][2] == '$' || aux[2][2] == '*')){
			if(aux[0][1] == '#' && aux[0][2] == '#' && aux[1][0] == '#' && aux[1][3] == '#'
					&& aux[1][1] == ' ' && aux[1][2] == ' '){
				return true;
			}
		}
		return false;
	}

	/**
	 * Estudia los alrededores de la caja en busca de un bloque especial.
	 * @param aux - matriz con la caja a estudiar y alrededores.
	 * @return booleano indicando si se genero una posicion muerta.
	 */
	private boolean testBloqueEspecial_3b(char[][] aux) {
		if((aux[0][1] == '$'|| aux[0][1] == '*') && (aux[0][2] == '$' || aux[0][2] == '*')){
			if(aux[2][1] == '#' && aux[2][2] == '#' && aux[1][0] == '#' && aux[1][3] == '#'
					&& aux[1][1] == ' ' && aux[1][2] == ' '){
				return true;
			}
		}
		return false;
	}	

	/**
	 * Estudia los alrededores de la caja en busca de un bloque especial.
	 * @param aux - matriz con la caja a estudiar y alrededores.
	 * @return booleano indicando si se genero una posicion muerta.
	 */
	private boolean testBloqueEspecial_3c(char[][] aux) {
		if((aux[1][0] == '$'|| aux[1][0] == '*') && (aux[2][0] == '$' || aux[2][0] == '*')){
			if(aux[0][1] == '#' && aux[3][1] == '#' && aux[1][2] == '#' && aux[2][2] == '#'
					&& aux[1][1] == ' ' && aux[2][1] == ' '){
				return true;
			}
		}
		return false;
	}

	/**
	 * Estudia los alrededores de la caja en busca de un bloque especial.
	 * @param aux - matriz con la caja a estudiar y alrededores.
	 * @return booleano indicando si se genero una posicion muerta.
	 */
	private boolean testBloqueEspecial_3d(char[][] aux) {
		if((aux[1][2] == '$'|| aux[1][2] == '*') && (aux[2][2] == '$' || aux[2][2] == '*')){
			if(aux[0][1] == '#' && aux[3][1] == '#' && aux[1][0] == '#' && aux[2][0] == '#'
					&& aux[1][1] == ' ' && aux[2][1] == ' '){
				return true;
			}
		}
		return false;
	}
}

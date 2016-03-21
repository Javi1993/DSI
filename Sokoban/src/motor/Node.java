package motor;

import java.util.ArrayList;
import java.util.List;

import interfaz.Escenario;
import interfaz.Posicion;

public class Node {
	private int g; //coste del recorrido hasta el nodo n (pasos)
	private int h; //coste aproximado de acomodar las cajas restantes (pasos) (distancia Manhattan)
	private int i;//número de cajas colocadas en una casilla destino
	private int f;
	private Escenario escenario;

	public Node(/*int col, int row,*/Escenario escenario, int g, int i) {
		this.setEscenario(escenario);
		this.setG(g);
		this.setI(i);
		manhattan();//para calcular el valor de h(x)
		//this.f = i|g+h; dentro de la lista abierta aparecerán primero los nodos con más cajas acomodadas 
		//correctamente y en caso de empates seguirán aquellos con menor costo acumulado de g(n) y h(n).
	}

	//Funcion heuristica
	public int getF() {
		return f;
	}

	public void setF(int x) { //x(x) es i(x) o g(x)
		this.f = x+this.h;
	}

	public int getI() {
		return i;
	}

	private void setI(int i) {
		this.i = i;
	}

	public int getH() {
		return h;
	}

	private void setH(int h) {
		this.h = h;
	}

	private void manhattan ()
	{
		int total = 0;
		List<Posicion> cajas = escenario.getCajas();
		List<Posicion> destinos = escenario.getDestinos();
		List<Posicion> auxiliar = new ArrayList<Posicion>();
		Posicion posAux = null;
		for(int i = 0; i<cajas.size(); i++){
			int aux = escenario.getALTO()+escenario.getALTO()+1;//reseteamos variable auxiliar
			for(int j = 0; j<destinos.size(); j++){
				if((auxiliar.contains(destinos.get(j)))&&(Math.abs(cajas.get(i).x-destinos.get(j).x) + Math.abs(cajas.get(i).y-destinos.get(j).y) < aux))
				{//calculamos distancia manhattan y guardamos la menor para esa caja respecto a los destinos libres
					aux = Math.abs(cajas.get(i).x-destinos.get(j).x) + Math.abs(cajas.get(i).y-destinos.get(j).y);
					posAux = destinos.get(j);
				}
			}
			auxiliar.add(posAux);
			total = total + aux;
		}
		setH(total);//actualizamos H
	}

	public int getG() {
		return g;
	}

	private void setG(int g) {
		this.g = g;
	}

	public Escenario getEscenario() {
		return escenario;
	}

	private void setEscenario(Escenario escenario) {
		this.escenario = escenario;
	} 
}

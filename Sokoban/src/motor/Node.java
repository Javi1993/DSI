package motor;

import java.util.ArrayList;
import java.util.List;
import interfaz.Escenario;
import interfaz.Posicion;

public class Node {
	private Integer g; //coste del recorrido hasta el nodo n (pasos).
	private Integer h; //coste aproximado de acomodar las cajas restantes (pasos) (distancia Manhattan).
	private Integer i;//número de cajas colocadas en una casilla destino.
	private Integer f;//valor de la funcion de evaluacion.
	private Escenario escenario;
	private String ID;//ID del nodo (secuencia de caracteres para llegar el)

	/**
	 * Constructor de la clase. Crea el nodo con la siguiente informacion.
	 * @param escenario - Nivel.
	 * @param g - Coste acumulado (pasos).
	 * @param i - Numero de cajas colocadas.
	 * @param ID - ID del nodo.
	 */
	public Node(Escenario escenario, Integer g, Integer i, String ID) {
		this.setEscenario(escenario);
		this.setG(g);
		this.setI(i);
		manhattan();//para calcular el valor de h(x).
		this.setF(this.getG()+this.getH());
		this.setID(ID);
	}

	public Integer getF() {
		return f;
	}

	public void setF(Integer f) {
		this.f = f;
	}

	public Integer getI() {
		return i;
	}

	private void setI(Integer i) {
		this.i = i;
	}

	public Integer getH() {
		return h;
	}

	private void setH(Integer h) {
		this.h = h;
	}

	/**
	 * Calcula la distancia Manhattan para las cajas sin colocar con los destinos vacios.
	 */
	private void manhattan (){
		Integer total = 0;
		List<Posicion> cajas = escenario.getCajas();//posiciones de las cajas sin colocar.
		List<Posicion> destinos = escenario.getDestinos();//posiciones de los destinos libres.
		List<Posicion> auxiliar = new ArrayList<Posicion>();//lista con posiciones ya emparejadas.
		for(int i = 0; i<cajas.size(); i++){
			Integer aux = escenario.getALTO()+escenario.getALTO()+1;//reseteamos variable auxiliar.
			Posicion posAux = null;//posicion actual con distManhattan minima.
			for(int j = 0; j<destinos.size(); j++){
				if((!auxiliar.contains(destinos.get(j)))&&(Math.abs(cajas.get(i).x-destinos.get(j).x) + Math.abs(cajas.get(i).y-destinos.get(j).y) < aux))
				{//calculamos distancia manhattan y guardamos la menor para esa caja respecto a los destinos libres.
					aux = Math.abs(cajas.get(i).x-destinos.get(j).x) + Math.abs(cajas.get(i).y-destinos.get(j).y);
					posAux = destinos.get(j);
				}
			}
			auxiliar.add(posAux);
			total = total + aux;
		}
		setH(total);//actualizamos H
	}

	public Integer getG() {
		return g;
	}

	private void setG(Integer g) {
		this.g = g;
	}

	public Escenario getEscenario() {
		return escenario;
	}

	public void setEscenario(Escenario escenario) {
		this.escenario = escenario;
	}

	public String getID() {
		return ID;
	}

	public void setID(String ID) {
		this.ID = ID;
	} 
}

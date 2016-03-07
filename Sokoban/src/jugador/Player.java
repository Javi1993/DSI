package jugador;

public class Player {
	//MODIFICAR PARA ADECUARLA A SOLUCION FINAL
	private int id;//id unica
	private int progreso;//nivel (debe ser una lista con todos los nivles compeltados)
	
	public Player(int id, int progreso){
		this.id=id;
		this.progreso=progreso;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getProgreso() {
		return progreso;
	}
	public void setProgreso(int progreso) {
		this.progreso = progreso;
	}
}

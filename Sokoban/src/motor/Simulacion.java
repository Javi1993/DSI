package motor;

import interfaz.Escenario;
import jugador.Mapas;
import jugador.Player;

public class Simulacion {
	
	public static void main(String[] args){
		Mapas.generarMapas();
		Player p = new Player("User", "pass");
		while(p.getProgreso()<115)
		{
			Escenario testEscenario = new Escenario(p.getProgreso(), false);//creamos el escenario
			if(Resolver.solucion(testEscenario, 0)==null){
				System.out.println("Nivel "+p.getProgreso()+" no encuenta solucion!");
				break;
			}
			System.out.println("Nivel "+p.getProgreso()+" completado!");
			p.updatePlayer(null, testEscenario);
		}
	}
}

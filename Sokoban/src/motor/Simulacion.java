package motor;

import interfaz.Escenario;
import jugador.Mapas;
import jugador.Player;

public class Simulacion {
	
	public static void main(String[] args){
		int niveles = 0;
		Mapas.generarMapas();
		Player p = new Player("User", "pass");
		while(niveles<114)
		{
			Escenario testEscenario = new Escenario(p.getProgreso(), false);//creamos el escenario
			Resolver.solucion(testEscenario, 0);
			p.updatePlayer(null, testEscenario);
			niveles++;
		}
	}
}

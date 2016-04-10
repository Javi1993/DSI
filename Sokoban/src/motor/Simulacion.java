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
			if(Resolver.solucion(testEscenario, 0)==null){
				niveles++;
				System.out.println("Nivel "+niveles+" no encuenta solucion!");
				break;
			}
			p.updatePlayer(null, testEscenario);
			niveles++;
			System.out.println("Nivel "+niveles+" completado!");
		}
	}
}

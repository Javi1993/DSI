package motor;

import java.io.IOException;
import interfaz.Escenario;
import jugador.Mapas;
import jugador.Player;

public class Simulacion {

	public static void main(String[] args) throws IOException{
		Mapas.generarMapas();
		Player p = new Player("User", "pass");
		Resolver res = new Resolver();
		while(p.getProgreso()<=150){
			Escenario testEscenario = new Escenario(p.getProgreso(), false);//creamos el escenario
			if(res.solucion(testEscenario, 0, null)==null){
				System.out.println("Nivel "+p.getProgreso()+" no encuenta solucion!");
				p.setProgreso(p.getProgreso()+1);
			}else{
				System.out.println("Nivel "+p.getProgreso()+" completado!");
				p.updatePlayer(null, testEscenario);
			}
		}
		Mapas.verResultados();//mostrar resultados totales por pantalla
		Mapas.escribirResultados();//mostrar resultados detallados en csv
	}
}

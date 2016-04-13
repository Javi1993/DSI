package motor;

import java.io.IOException;
import interfaz.Escenario;
import jugador.Mapas;
import jugador.Player;

public class Simulacion {

	public static void main(String[] args) throws IOException{
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


		//Ver cajas por nivel
		//		File file = new File("C:\\Users\\javie\\Desktop\\filename.txt");
		//		if (!file.exists()) {
		//			file.createNewFile();
		//		}
		//		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		//		BufferedWriter bw = new BufferedWriter(fw);
		//
		//		Mapas.generarMapas();
		//		Player p = new Player("User", "pass");
		//		while(p.getProgreso()<151)
		//		{
		//			Escenario testEscenario = new Escenario(p.getProgreso(), false);//creamos el escenario
		//			bw.write("Nivel "+p.getProgreso()+": "+testEscenario.cajas());
		//			bw.newLine();
		//			p.updatePlayer(null, testEscenario);
		//		}
		//		bw.close();
	}
}

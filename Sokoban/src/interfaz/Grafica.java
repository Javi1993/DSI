package interfaz;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import jugador.Mapas;
import jugador.Player;
import motor.Resolver;

public class Grafica extends JFrame{
	private static final long serialVersionUID = 1L;
	private static final int PIXELSCUADRADO = 30;
	private static final int BORDE = 50;
	private static final int ANCHODERECHA = (PIXELSCUADRADO + 1) * 5;
	private JButton b1;
	private JButton b2;
	private int altoFrame;
	private int anchoFrame;
	private int alto;
	private int ancho;
	private int pasos;
	private KeyListener keyListen;
	private char[] sol;
	private static Login lo;
	private Escenario.TipoCasilla[][] tablero;
	public Escenario escenario;
	private List<String> teclasManual;
	private boolean comenzado = false;
	private static final int COLOR_NEGRO = -16777216;

	public static void main(String [] args) throws InterruptedException
	{	
		Mapas.generarMapas();
		lo = new Login();
		while(!lo.valido){
			Thread.sleep(1000);
		}
		lo.setVisible(false);
		lo.dispose();
		Thread.sleep(1000);
		new Grafica(lo.player);
	}

	public Grafica(Player p){
		escenario = new Escenario(p.getProgreso(), false);//creamos el escenario
		teclasManual = new ArrayList<String>();
		keyListen = new TeclaPulsada(this);
		establecerCoodenadas(escenario);
	}

	public void establecerCoodenadas(Escenario escenario)
	{
		this.ancho = escenario.getANCHO();
		this.alto = escenario.getALTO();
		tablero = new Escenario.TipoCasilla[alto][ancho];
		anchoFrame = 19 * (PIXELSCUADRADO+1) + (2 * BORDE) + ANCHODERECHA;
		altoFrame = 14 * (PIXELSCUADRADO+1) + (2 * BORDE)+10;
		setSize (anchoFrame, altoFrame);
		setTitle("DSI | Sokoban");
		this.setVisible(true);
		this.setResizable(false);
		this.pintarBotones();
		this.pintarTablero();
		this.update(this.getGraphics());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
	}

	public void update (Graphics g)
	{
		Image buffer = createImage (anchoFrame, altoFrame);
		Graphics sg = buffer.getGraphics();
		sg.clearRect(0, 0, anchoFrame, altoFrame);
		dibujarTablero(sg);
		g.drawImage (buffer, 0, 0, this);
	}

	/**
	 * Pinta el tablero del juego
	 * @param g Graphics en el que pintarlo
	 */
	public void dibujarTablero(Graphics g)
	{
		try {
			// Pinta los bordes
			g.fillRect(BORDE - 2, BORDE - 2, (PIXELSCUADRADO+1)* (ancho) + 3, (PIXELSCUADRADO+1) * (alto) + 3);
			/*
        g.fillRect(BORDE + (PIXELSCUADRADO+1)* (ancho +1) - 2,
                   BORDE + (PIXELSCUADRADO+1) - 2,
                   (PIXELSCUADRADO+1)* (DIMENSIONPIEZA) + 3, (PIXELSCUADRADO+1) * (DIMENSIONPIEZA) + 3);
			 */                   
			// Pinta el tablero
			File pathToFile;
			Image image;
			for (int i = 0; i < alto; i++) {
				for (int j = 0; j < ancho; j++) {
					Escenario.TipoCasilla tipoCasilla = tablero[i][j];
					switch(tipoCasilla) {
					case VACIA:
						g.setColor(new Color(COLOR_NEGRO));
						g.fillRect(BORDE+(PIXELSCUADRADO+1)*j, BORDE+(PIXELSCUADRADO+1)*i, PIXELSCUADRADO, PIXELSCUADRADO);
						break;
					case MURO:
						pathToFile = new File("./img/Wall_Gray.png");
						image = ImageIO.read(pathToFile);
						g.drawImage(image, BORDE+(PIXELSCUADRADO+1)*j, BORDE+(PIXELSCUADRADO+1)*i, PIXELSCUADRADO, PIXELSCUADRADO, this);
						break;
					case CAJA:
						pathToFile = new File("./img/CrateDark_Brown.png");
						image = ImageIO.read(pathToFile);
						g.drawImage(image, BORDE+(PIXELSCUADRADO+1)*j, BORDE+(PIXELSCUADRADO+1)*i, PIXELSCUADRADO, PIXELSCUADRADO, this);
						break;
						// Pinta un punto
					case DESTINO:
						pathToFile = new File("./img/EndPoint_Red.png");
						image = ImageIO.read(pathToFile);
						g.drawImage(image, BORDE+(PIXELSCUADRADO+1)*j, BORDE+(PIXELSCUADRADO+1)*i, PIXELSCUADRADO-2, PIXELSCUADRADO-2, this);
						break;
					case CAJA_SOBRE_DESTINO:
						// 1: Destino
						pathToFile = new File("./img/EndPoint_Red.png");
						image = ImageIO.read(pathToFile);
						g.drawImage(image, BORDE+(PIXELSCUADRADO+1)*j, BORDE+(PIXELSCUADRADO+1)*i, PIXELSCUADRADO-2, PIXELSCUADRADO-2, this);

						// 2: Caja
						pathToFile = new File("./img/CrateDark_Brown_Background.png");
						image = ImageIO.read(pathToFile);
						g.drawImage(image, BORDE+(PIXELSCUADRADO+1)*j, BORDE+(PIXELSCUADRADO+1)*i, PIXELSCUADRADO, PIXELSCUADRADO, this);
						break;
					case JUGADOR:
						pathToFile = new File("./img/Character5.png");
						image = ImageIO.read(pathToFile);
						g.drawImage(image, BORDE+(PIXELSCUADRADO+1)*j, BORDE+(PIXELSCUADRADO+1)*i, PIXELSCUADRADO, PIXELSCUADRADO, this);
						break;
					case JUGADOR_SOBRE_DESTINO:
						// 1: Destino
						pathToFile = new File("./img/EndPoint_Red.png");
						image = ImageIO.read(pathToFile);
						g.drawImage(image, BORDE+(PIXELSCUADRADO+1)*j, BORDE+(PIXELSCUADRADO+1)*i, PIXELSCUADRADO-2, PIXELSCUADRADO-2, this);

						//2: Jugador
						pathToFile = new File("./img/Character5_Background.png");
						image = ImageIO.read(pathToFile);
						g.drawImage(image, BORDE+(PIXELSCUADRADO+1)*j, BORDE+(PIXELSCUADRADO+1)*i, PIXELSCUADRADO, PIXELSCUADRADO, this);
						break;
					}
				}
			}
			// Pinta los pasos
			g.setColor(Color.black);
			g.fillRect(BORDE + (PIXELSCUADRADO+1)* (ancho +1) - 5, BORDE+ (PIXELSCUADRADO+1)* (alto - 3) + 7,
					ANCHODERECHA - 2, PIXELSCUADRADO);
			g.setColor(Color.white);
			g.drawString("Steps: " + pasos, BORDE+ (PIXELSCUADRADO+1)* (ancho +1) , BORDE+ (PIXELSCUADRADO+1)* (alto - 2));

			//Pintar instrucciones
			g.setColor(Color.black);
			g.setFont(new Font("Dialog", Font.BOLD, 15));
			g.drawString("W: Up  A: Left  D: Right  S: Down  L: Solver/Next level  P: Start/Restart  Q: Exit", BORDE, BORDE+(PIXELSCUADRADO*15));
			pathToFile = new File("./img/Character5.png");
			image = ImageIO.read(pathToFile);
			g.drawImage(image, BORDE, BORDE+(PIXELSCUADRADO*15+10), PIXELSCUADRADO-5, PIXELSCUADRADO-5, this);
			g.drawString(": Player", BORDE+PIXELSCUADRADO-5, BORDE+(PIXELSCUADRADO*16));
			pathToFile = new File("./img/CrateDark_Brown.png");
			image = ImageIO.read(pathToFile);
			g.drawImage(image, BORDE+(3*PIXELSCUADRADO), BORDE+(PIXELSCUADRADO*15+10), PIXELSCUADRADO-5, PIXELSCUADRADO-5, this);
			g.drawString(": Box", BORDE+4*PIXELSCUADRADO-5, BORDE+(PIXELSCUADRADO*16));
			pathToFile = new File("./img/EndPoint_Red.png");
			image = ImageIO.read(pathToFile);
			g.drawImage(image, BORDE+5*PIXELSCUADRADO+10, BORDE+(PIXELSCUADRADO*15+10), PIXELSCUADRADO-5, PIXELSCUADRADO-5, this);
			g.drawString(": Goal ", BORDE+6*PIXELSCUADRADO+5, BORDE+(PIXELSCUADRADO*16));
			pathToFile = new File("./img/EndPoint_Red.png");
			image = ImageIO.read(pathToFile);
			g.drawImage(image, BORDE+(8*PIXELSCUADRADO), BORDE+(PIXELSCUADRADO*15+10), PIXELSCUADRADO-5, PIXELSCUADRADO-5, this);
			pathToFile = new File("./img/Character5_Background.png");
			image = ImageIO.read(pathToFile);
			g.drawImage(image, BORDE+(8*PIXELSCUADRADO), BORDE+(PIXELSCUADRADO*15+10), PIXELSCUADRADO-5, PIXELSCUADRADO-5, this);
			g.drawString(": Player on goal ", BORDE+(9*PIXELSCUADRADO-5), BORDE+(PIXELSCUADRADO*16));
			pathToFile = new File("./img/EndPoint_Red.png");
			image = ImageIO.read(pathToFile);
			g.drawImage(image, BORDE+(13*PIXELSCUADRADO+7), BORDE+(PIXELSCUADRADO*15+10), PIXELSCUADRADO-5, PIXELSCUADRADO-5, this);
			pathToFile = new File("./img/CrateDark_Brown_Background.png");
			image = ImageIO.read(pathToFile);
			g.drawImage(image, BORDE+(13*PIXELSCUADRADO+7), BORDE+(PIXELSCUADRADO*15+10), PIXELSCUADRADO-5, PIXELSCUADRADO-5, this);
			g.drawString(": Box on goal ", BORDE+(14*PIXELSCUADRADO+2), BORDE+(PIXELSCUADRADO*16));

			//Pintar record info
			g.setColor(Color.black);
			g.setFont(new Font("Dialog", Font.BOLD, 13));
			if(escenario.getRecord()>0){
				g.drawString("Level "+escenario.getNivel()+": The recordman is "+escenario.getRecordName()+" with "+escenario.getRecord()+" steps.", BORDE, BORDE-6);
			}else{
				g.drawString("Level "+escenario.getNivel()+": No record yet.", BORDE, BORDE-6);
			}

			g.setColor(Color.black);
			g.setFont(new Font("Dialog", Font.BOLD, 15));
			if(escenario.hasGanado()&&!escenario.isIA()){
				g.drawString("You win! =)", BORDE+ (PIXELSCUADRADO+1)* (ancho +1) , BORDE+ (PIXELSCUADRADO+1)* (alto - 10));
			}else if(escenario.hasGanado()&&escenario.isIA()){
				g.drawString("IA win! =(", BORDE+ (PIXELSCUADRADO+1)* (ancho +1) , BORDE+ (PIXELSCUADRADO+1)* (alto - 10));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void pintarCuadradoTablero(int x, int y, Escenario.TipoCasilla tipoCasilla) {
		this.tablero[y][x] = tipoCasilla;
	}

	public void establecerPasos(int pasos) {
		this.pasos = pasos;
	}

	public int obtenerPasos() {
		return this.pasos;
	}

	/**
	 * M�todo invocado cada vez que el usuario pulsa una tecla
	 * @param tecla la tecla pulsada
	 */
	public void teclaPulsada(char tecla){
		int pasos = obtenerPasos();
		// En funci�n de la tecla pulsada hace algo
		switch (tecla) {
		case 'W':
		case 'A':
		case 'S':
		case 'D':
		case 'w':
		case 'a':
		case 's':
		case 'd':
			if(comenzado){
				if(!escenario.hasGanado()&&escenario.realizarMovimiento(tecla)){
					if(escenario.hasGanado()){
						establecerPasos(pasos+1);
						b1.setText("Next level");
					}else{
						establecerPasos(pasos+1);
					}
					teclasManual.add(String.valueOf(tecla));//guardamos los movimientos del usuario
				}
			}
			break;
		case 'q':
		case 'Q':
			// Si se pulsa la tecla q sale del programa
			System.exit(0);
			break;
		case 'L':
		case 'l':
			if(comenzado||(!comenzado&&escenario.hasGanado())){
				// Si se pulsa la tecla r sale inicia el solver o se pasa de nivel
				solverButton();
			}
			break;
		case 'P':
		case 'p':
			// Si se pulsa la tecla t se reinicia o empieza el nivel
			restarButton();
			break;
		default:
			if(!escenario.hasGanado()){
				System.out.println("Introduzca la tecla correcta por favor.");}
		}
	}

	public void pintarBotones()
	{
		JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // new FlowLayout not needed
		southPanel.setOpaque(true);
		b1 = new JButton("Solver"); 
		b2 = new JButton("Start");  
		b2.addKeyListener(keyListen);
		b1.setVisible(false);
		b1.addActionListener(new ActionListener() {          
			public void actionPerformed(ActionEvent e) {
				solverButton();
			}
		}); 
		b2.addActionListener(new ActionListener() {          
			public void actionPerformed(ActionEvent e) {
				restarButton();
			}
		}); 
		b1.setPreferredSize(new Dimension(90, 25));
		b1.setFont(new Font("Arial", 1, 11));
		b2.setPreferredSize(new Dimension(90, 25)); 
		b2.setFont(new Font("Arial", 1, 11));
		southPanel.add(b1);
		southPanel.add(b2);
		this.add(southPanel, BorderLayout.SOUTH);
	}

	private void restarButton(){
		comenzado = true;
		if(!b2.getText().equals("Start")){
			establecerPasos(0);
			escenario.setIA(false);
		}
		b1.setVisible(true);
		escenario.resetEscenario();
		pintarTablero();
		update(getGraphics());
		b1.setText("Solver");
		if(!b2.getText().equals("Start")){
			getGraphics().setColor(Color.black);
			getGraphics().setFont(new Font("Dialog", Font.BOLD, 11));
			getGraphics().drawString("Level restarted.", BORDE+ (PIXELSCUADRADO+1)* (ancho +1) , BORDE+ (PIXELSCUADRADO+1)* (alto - 10));
		}else{
			getGraphics().setColor(Color.black);
			getGraphics().setFont(new Font("Dialog", Font.BOLD, 11));
			getGraphics().drawString("Level started.", BORDE+ (PIXELSCUADRADO+1)* (ancho +1) , BORDE+ (PIXELSCUADRADO+1)* (alto - 10));
		}
		teclasManual = new ArrayList<String>();
		b2.setText("Restart");
	}

	private void solverButton()
	{
		comenzado = false;
		if(b1.getText().equals("Solver"))
		{
			pintarTablero();
			update(getGraphics());
			getGraphics().setColor(Color.black);
			getGraphics().setFont(new Font("Dialog", Font.BOLD, 11));
			getGraphics().drawString("Computing solution...", BORDE+ (PIXELSCUADRADO+1)* (ancho +1) , BORDE+ (PIXELSCUADRADO+1)* (alto - 10));
			Resolver res = new Resolver();
			sol = res.solucion(escenario, pasos);//el solver devuelve un array de caracteres con la solucion
			teclasManual = new ArrayList<String>();
			if(sol!=null){
				for(int i =0; i<sol.length; i++)
				{
					escenario.realizarMovimiento(sol[i]);
					establecerPasos(pasos+1);
					pintarTablero();
					update(getGraphics());
					try {
						Thread.sleep(300);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}else{
				b1.setVisible(false);
				pintarTablero();
				update(getGraphics());
				getGraphics().setColor(Color.black);
				getGraphics().setFont(new Font("Dialog", Font.BOLD, 11));
				getGraphics().drawString("Solution not found.", BORDE+ (PIXELSCUADRADO+1)* (ancho +1) , BORDE+ (PIXELSCUADRADO+1)* (alto - 10));
				getGraphics().drawString("Please restart level.", BORDE+ (PIXELSCUADRADO+1)* (ancho +1) , BORDE+ (PIXELSCUADRADO+1)* (alto - 9));
			}
			b1.setText("Next level");
		}else{
			if(escenario.hasGanado())
			{
				if(!escenario.isIA())
				{//guardamos jugada de usuario
					lo.player.updatePlayer(teclasManual, escenario);
					escenario.updateNivel(teclasManual, lo.player, 0, escenario, null, 0);
				}else{
					lo.player.updatePlayer(null, escenario);
				}
				b1.setText("Solver");
				b1.setVisible(false);
				b2.setText("Start");
				establecerPasos(0);
				escenario = new Escenario(lo.player.getProgreso(), false);//creamos el escenario
				teclasManual = new ArrayList<String>();
				pintarTablero();
				update(getGraphics());
			}	
		}
	}

	/**
	 * 
	 */
	public void pintarTablero()
	{
		// Rellena el tablero con las casilla de la variable elTablero.
		for(int i=0; i< escenario.getALTO(); i++) {
			for(int j=0; j<escenario.getANCHO(); j++) {
				Escenario.TipoCasilla tipoCasilla = escenario.obtenerTipo(i,j);
				pintarCuadradoTablero(j, i, tipoCasilla);
			}
		}
	}

	// Clase interna que maneja la pulsación de teclas
	private class TeclaPulsada extends KeyAdapter {
		private Grafica t;
		public TeclaPulsada(Grafica t) {
			this.t = t;
		}

		public void keyPressed (KeyEvent e) {
			char key = e.getKeyChar();
			switch (key) {
			case 'W':
			case 'A':
			case 'S':
			case 'D':
			case 'w':
			case 'a':
			case 's':
			case 'd':
			case 'Q':
			case 'q':
				t.teclaPulsada(key);
				break;
			default:
				break;
			}
			t.pintarTablero();
			t.update(t.getGraphics());
			switch (key) {
			case 'Q':
			case 'q':
			case 'P':
			case 'p':
			case 'L':
			case 'l':
				t.teclaPulsada(key);
				break;
			default:
				break;
			}
		}
	}
}

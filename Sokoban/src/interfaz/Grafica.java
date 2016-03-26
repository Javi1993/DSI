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
	private Escenario.TipoCasilla[][] tablero;
	public Escenario escenario;

	private static final int COLOR_VERDE = -16711936;
	private static final int COLOR_AZUL = -16776961;
	private static final int COLOR_NARANJA = -14336;
	private static final int COLOR_NEGRO = -16777216;
	private static final int COLOR_BLANCO = -1;
	private static final int COLOR_GRIS = -7829368;

	public static void main(String [] args) throws InterruptedException
	{	
		//proceso de login en vez usar player de prueba
		Login lo = new Login();
		while(!lo.valido){
			Thread.sleep(1000);
		}
		lo.setVisible(false);
		lo.dispose();
		Mapas.generarMapas();
		Thread.sleep(1000);
		new Grafica(lo.player);
	}

	public Grafica(Player p){
		escenario = new Escenario(p.getProgreso());//creamos el escenario
		establecerCoodenadas(escenario);
	}

	public void establecerCoodenadas(Escenario escenario)
	{
		this.ancho = escenario.getANCHO();
		this.alto = escenario.getALTO();
		tablero = new Escenario.TipoCasilla[alto][ancho];
		anchoFrame = 19 * (PIXELSCUADRADO+1) + (2 * BORDE) + ANCHODERECHA;
		altoFrame = 11 * (PIXELSCUADRADO+1) + (2 * BORDE)+10;
		setSize (anchoFrame, altoFrame);
		setTitle("DSI | Sokoban");
		this.setVisible(true);
		this.setResizable(false);
		this.pintarBotones();
		this.pintarTablero();
		this.update(this.getGraphics());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		addKeyListener (new TeclaPulsada(this));
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
		// Pinta los bordes
		g.fillRect(BORDE - 2, BORDE - 2, (PIXELSCUADRADO+1)* (ancho) + 3, (PIXELSCUADRADO+1) * (alto) + 3);
		/*
        g.fillRect(BORDE + (PIXELSCUADRADO+1)* (ancho +1) - 2,
                   BORDE + (PIXELSCUADRADO+1) - 2,
                   (PIXELSCUADRADO+1)* (DIMENSIONPIEZA) + 3, (PIXELSCUADRADO+1) * (DIMENSIONPIEZA) + 3);
		 */                   
		// Pinta el tablero
		for (int i = 0; i < alto; i++) {
			for (int j = 0; j < ancho; j++) {
				Escenario.TipoCasilla tipoCasilla = tablero[i][j];
				switch(tipoCasilla) {
				case VACIA:
					g.setColor(new Color(COLOR_NEGRO));
					g.fillRect(BORDE+(PIXELSCUADRADO+1)*j, BORDE+(PIXELSCUADRADO+1)*i, PIXELSCUADRADO, PIXELSCUADRADO);
					break;
				case MURO:
					g.setColor(new Color(COLOR_GRIS));
					g.fillRect(BORDE+(PIXELSCUADRADO+1)*j, BORDE+(PIXELSCUADRADO+1)*i, PIXELSCUADRADO, PIXELSCUADRADO);
					break;
				case CAJA:
					g.setColor(new Color(COLOR_NARANJA));
					g.fillRect(BORDE+(PIXELSCUADRADO+1)*j, BORDE+(PIXELSCUADRADO+1)*i, PIXELSCUADRADO, PIXELSCUADRADO);
					break;
					// Pinta un punto
				case DESTINO:
					g.setColor(new Color(COLOR_VERDE));
					g.fillArc(BORDE+(PIXELSCUADRADO+1)*j, BORDE+(PIXELSCUADRADO+1)*i, PIXELSCUADRADO-2, PIXELSCUADRADO-2, 120, 360);
					// Punto: g.fillRect(BORDE+(PIXELSCUADRADO+1)*j+((PIXELSCUADRADO-PIXELSPUNTO)/2), BORDE+(PIXELSCUADRADO+1)*i+((PIXELSCUADRADO-PIXELSPUNTO)/2), PIXELSPUNTO, PIXELSPUNTO);
					break;
				case CAJA_SOBRE_DESTINO:
					g.setColor(new Color(COLOR_NARANJA));
					g.fillRect(BORDE+(PIXELSCUADRADO+1)*j, BORDE+(PIXELSCUADRADO+1)*i, PIXELSCUADRADO, PIXELSCUADRADO);
					g.setColor(new Color(COLOR_VERDE));
					g.drawArc(BORDE+(PIXELSCUADRADO+1)*j, BORDE+(PIXELSCUADRADO+1)*i, PIXELSCUADRADO-2, PIXELSCUADRADO-2, 120, 360);
					break;
				case JUGADOR:
					g.setColor(new Color(COLOR_AZUL));
					// Figura
					g.fillArc(BORDE+(PIXELSCUADRADO+1)*j, BORDE+(PIXELSCUADRADO+1)*i, PIXELSCUADRADO-2, PIXELSCUADRADO-2, 0, 180);
					g.fillRect(BORDE+(PIXELSCUADRADO+1)*j, BORDE+(PIXELSCUADRADO+1)*i+(PIXELSCUADRADO/2)-1, PIXELSCUADRADO, PIXELSCUADRADO/3);
					g.fillOval(BORDE+(PIXELSCUADRADO+1)*j, BORDE+(PIXELSCUADRADO+1)*i+(PIXELSCUADRADO/2)+(PIXELSCUADRADO/6), PIXELSCUADRADO/3, PIXELSCUADRADO/3);
					g.fillOval(BORDE+(PIXELSCUADRADO+1)*j+(PIXELSCUADRADO/3), BORDE+(PIXELSCUADRADO+1)*i+(PIXELSCUADRADO/2)+(PIXELSCUADRADO/6), PIXELSCUADRADO/3, PIXELSCUADRADO/3);
					g.fillOval(BORDE+(PIXELSCUADRADO+1)*j+(2*PIXELSCUADRADO/3), BORDE+(PIXELSCUADRADO+1)*i+(PIXELSCUADRADO/2)+(PIXELSCUADRADO/6), PIXELSCUADRADO/3, PIXELSCUADRADO/3);
					// Ojos
					g.setColor(new Color(COLOR_BLANCO));
					g.fillOval(BORDE+(PIXELSCUADRADO+1)*j+(PIXELSCUADRADO/4), BORDE+(PIXELSCUADRADO+1)*i+(PIXELSCUADRADO/4), PIXELSCUADRADO/5, PIXELSCUADRADO/4);
					g.fillOval(BORDE+(PIXELSCUADRADO+1)*j+(2*PIXELSCUADRADO/4), BORDE+(PIXELSCUADRADO+1)*i+(PIXELSCUADRADO/4), PIXELSCUADRADO/5, PIXELSCUADRADO/4);
					g.setColor(new Color(COLOR_NEGRO));
					g.fillOval(BORDE+(PIXELSCUADRADO+1)*j+(PIXELSCUADRADO/4)+PIXELSCUADRADO/20, BORDE+(PIXELSCUADRADO+1)*i+(PIXELSCUADRADO/4)+PIXELSCUADRADO/8, PIXELSCUADRADO/10, PIXELSCUADRADO/8);
					g.fillOval(BORDE+(PIXELSCUADRADO+1)*j+(2*PIXELSCUADRADO/4)+PIXELSCUADRADO/20, BORDE+(PIXELSCUADRADO+1)*i+(PIXELSCUADRADO/4)+PIXELSCUADRADO/8, PIXELSCUADRADO/10, PIXELSCUADRADO/8);
					break;
				case JUGADOR_SOBRE_DESTINO:
					// 1: Destino
					g.setColor(new Color(COLOR_VERDE));
					g.fillArc(BORDE+(PIXELSCUADRADO+1)*j, BORDE+(PIXELSCUADRADO+1)*i, PIXELSCUADRADO-2, PIXELSCUADRADO-2, 120, 360);

					// 2: Jugador
					g.setColor(new Color(COLOR_AZUL));
					// Figura
					g.fillArc(BORDE+(PIXELSCUADRADO+1)*j, BORDE+(PIXELSCUADRADO+1)*i, PIXELSCUADRADO-2, PIXELSCUADRADO-2, 0, 180);
					g.fillRect(BORDE+(PIXELSCUADRADO+1)*j, BORDE+(PIXELSCUADRADO+1)*i+(PIXELSCUADRADO/2)-1, PIXELSCUADRADO, PIXELSCUADRADO/3);
					g.fillOval(BORDE+(PIXELSCUADRADO+1)*j, BORDE+(PIXELSCUADRADO+1)*i+(PIXELSCUADRADO/2)+(PIXELSCUADRADO/6), PIXELSCUADRADO/3, PIXELSCUADRADO/3);
					g.fillOval(BORDE+(PIXELSCUADRADO+1)*j+(PIXELSCUADRADO/3), BORDE+(PIXELSCUADRADO+1)*i+(PIXELSCUADRADO/2)+(PIXELSCUADRADO/6), PIXELSCUADRADO/3, PIXELSCUADRADO/3);
					g.fillOval(BORDE+(PIXELSCUADRADO+1)*j+(2*PIXELSCUADRADO/3), BORDE+(PIXELSCUADRADO+1)*i+(PIXELSCUADRADO/2)+(PIXELSCUADRADO/6), PIXELSCUADRADO/3, PIXELSCUADRADO/3);
					// Ojos
					g.setColor(new Color(COLOR_VERDE));
					g.fillOval(BORDE+(PIXELSCUADRADO+1)*j+(PIXELSCUADRADO/4), BORDE+(PIXELSCUADRADO+1)*i+(PIXELSCUADRADO/4), PIXELSCUADRADO/5, PIXELSCUADRADO/4);
					g.fillOval(BORDE+(PIXELSCUADRADO+1)*j+(2*PIXELSCUADRADO/4), BORDE+(PIXELSCUADRADO+1)*i+(PIXELSCUADRADO/4), PIXELSCUADRADO/5, PIXELSCUADRADO/4);
					g.setColor(new Color(COLOR_NEGRO));
					g.fillOval(BORDE+(PIXELSCUADRADO+1)*j+(PIXELSCUADRADO/4)+PIXELSCUADRADO/20, BORDE+(PIXELSCUADRADO+1)*i+(PIXELSCUADRADO/4)+PIXELSCUADRADO/8, PIXELSCUADRADO/10, PIXELSCUADRADO/8);
					g.fillOval(BORDE+(PIXELSCUADRADO+1)*j+(2*PIXELSCUADRADO/4)+PIXELSCUADRADO/20, BORDE+(PIXELSCUADRADO+1)*i+(PIXELSCUADRADO/4)+PIXELSCUADRADO/8, PIXELSCUADRADO/10, PIXELSCUADRADO/8);
					break;
				}
			}
		}
		// Pinta los pasos
		g.setColor(Color.black);
		g.fillRect(BORDE + (PIXELSCUADRADO+1)* (ancho +1) - 5, BORDE+ (PIXELSCUADRADO+1)* (alto - 3) + 7,
				ANCHODERECHA - 2, PIXELSCUADRADO);
		g.setColor(Color.white);
		g.drawString("Pasos: " + pasos, BORDE+ (PIXELSCUADRADO+1)* (ancho +1) , BORDE+ (PIXELSCUADRADO+1)* (alto - 2));

		//Pintar instrucciones
		g.setColor(Color.black);
		g.setFont(new Font("Dialog", Font.BOLD, 15));
		g.drawString("W: Up  A: Left  D: Right  X: Down  R: Resolver  Q: Exit", BORDE, BORDE+(PIXELSCUADRADO*12));
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
		case 'X':
		case 'D':
		case 'w':
		case 'a':
		case 'x':
		case 'd':
			//Si se pulsa una de estas teclas realiza el movimiento solo si este es posible.
			/*
			 * CAMBIAR
			 * 
			 */
			if(!escenario.hasGanado()&&escenario.realizarMovimiento(tecla)){
				if(escenario.hasGanado())
				{
					System.out.println("Has ganado");
					establecerPasos(pasos+1);
				}else{
					establecerPasos(pasos+1);
				}
			}
			break;
		case 'q':
		case 'Q':
			// Si se pulsa la tecla q sale del programa
			System.out.println("Saliendo del juego...");
			System.exit(0);
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
		b1 = new JButton("Resolver"); 
		b2 = new JButton("Empezar");  
		b1.setVisible(false);
		b1.addActionListener(new ActionListener() {          
			public void actionPerformed(ActionEvent e) {
				char[] sol = Resolver.solucion(escenario, pasos);//el solver devuelve un array de caracteres con la solucion
				if(sol!=null){
					System.out.println("SE HA ENCONTRADO UNA SOLUCI�N");
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
					System.out.println("NO SE HA ENCONTRADO UNA SOLUCION REINICIE EL NIVEL Y VUELVA A PROBAR");
				}
			}
		}); 
		b2.addActionListener(new ActionListener() {          
			public void actionPerformed(ActionEvent e) {
				System.out.println("Reiniciando nivel...");
				establecerPasos(0);
				b2.setText("Reiniciar");
				b1.setVisible(true);
				escenario.resetEscenario();
				pintarTablero();
				update(getGraphics());
			}
		}); 
		b1.setPreferredSize(new Dimension(90, 25));
		b1.setFont(new Font("Arial", 1, 11));
		b1.addKeyListener(new TeclaPulsada(this));
		b2.setPreferredSize(new Dimension(90, 25)); 
		b2.setFont(new Font("Arial", 1, 11));
		b2.addKeyListener(new TeclaPulsada(this));
		southPanel.add(b1);
		southPanel.add(b2);
		this.add(southPanel, BorderLayout.SOUTH);
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
			t.teclaPulsada(key);
			t.pintarTablero();
			t.update(t.getGraphics());
			// t.repaint(); Antiguamente recargaba con esta, pero no va con JFRAME, si con FRAME
		}
	}
}

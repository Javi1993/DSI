
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;

/**
 * User: Manuel Pereira
 * Date: 05-may-2006
 * Time: 16:46:01
 * Clase que se encarga de pintar el tablero de juego
 * Esta clase NO deberá ser modificada por el alumno para realizar la práctica 3
 */
public abstract class NoTocarEstaClase extends Frame {

	private static final long serialVersionUID = 1L;
	private static final int PIXELSCUADRADO = 30;
    private static final int BORDE = 50;
    private static final int ANCHODERECHA = (PIXELSCUADRADO + 1) * 5;
    private JButton b1;
    private int altoFrame;
    private int anchoFrame;
    private int alto;
    private int ancho;
    private int pasos;
    private RellenarPorAlumno.TipoCasilla[][] tablero;

    private static final int COLOR_VERDE = -16711936;
    private static final int COLOR_AZUL = -16776961;
    private static final int COLOR_NARANJA = -14336;
    private static final int COLOR_NEGRO = -16777216;
    private static final int COLOR_BLANCO = -1;
    private static final int COLOR_GRIS = -7829368;

    public void establecerCoodenadas(int ancho, int alto)
    {
        this.ancho = ancho;
        this.alto = alto;
        tablero = new RellenarPorAlumno.TipoCasilla[alto][ancho];
        anchoFrame = ancho * (PIXELSCUADRADO+1) + (2 * BORDE) + ANCHODERECHA;
        altoFrame = alto * (PIXELSCUADRADO+1) + (2 * BORDE);
        setSize (anchoFrame, altoFrame);
        setTitle("DSI|Sokoban");
        addKeyListener (new TeclaPulsada(this));
        this.setVisible(true);
		this.pintarTablero();
        this.update(this.getGraphics());
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
                RellenarPorAlumno.TipoCasilla tipoCasilla = tablero[i][j];
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
        
     // Pinta boton resolver
        b1 = new JButton("Resolver");   
        b1.setSize(200,50);
        b1.setBounds(BORDE + (PIXELSCUADRADO+1)* (ancho +1) - 5, BORDE+ (PIXELSCUADRADO+1)* (alto - 5) + 7,
                ANCHODERECHA - 2, PIXELSCUADRADO);
        b1.setVisible(true);
        b1.addActionListener(new ActionListener() {          
            public void actionPerformed(ActionEvent e) {
                 System.out.println("You clicked the button, using an ActionListener");
            }
        }); 
        add(b1);
    }
    
    public void pintarCuadradoTablero(int x, int y, RellenarPorAlumno.TipoCasilla tipoCasilla) {
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
    public abstract void teclaPulsada(char tecla);
    
    /**
     * 
     */
    public abstract void pintarTablero();

    // Clase interna que maneja la pulsación de teclas
    private class TeclaPulsada extends KeyAdapter {
        private NoTocarEstaClase t;
        public TeclaPulsada(NoTocarEstaClase t) {
            this.t = t;
        }
        
        public void keyPressed (KeyEvent e) {
            char key = e.getKeyChar();
            t.teclaPulsada(key);
            t.pintarTablero();
            t.repaint();
        }
    }
}



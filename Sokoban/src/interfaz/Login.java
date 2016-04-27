package interfaz;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import jugador.Player;

@SuppressWarnings("serial")
public class Login extends JFrame{

	private JTextField user;
	private JPasswordField password;
	Player player;
	boolean valido;
	private JButton b1;
	private static Action enterAction; 


	public Login(){
		this.setTitle("DSI | Sokoban");
		valido = false;
		this.getContentPane().setLayout(new FlowLayout());
		crearLabels();
		crearBotones();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setPreferredSize(new Dimension(250, 200));
		this.pack();
		this.setResizable(false);
		this.setVisible(true);
		this.setLocationRelativeTo(null);
	}

	private void crearLabels(){
		JLabel label = new JLabel("       USER       ");
		user = new JTextField(15);
		this.getContentPane().add(label);
		this.getContentPane().add(user);
		JLabel label1 = new JLabel("PASSWORD");
		password = new JPasswordField(15);
		this.getContentPane().add(label1);
		this.getContentPane().add(password);
	}

	private void crearBotones(){
		JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 15));
		southPanel.setOpaque(true);
		b1 = new JButton("Login"); 

		b1.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Border borde = new LineBorder(Color.RED, 1);
				if(user.getText().trim().length()==0||password.getPassword().length==0){
					valido = false;
				}else{
					player = new Player(user.getText().trim(), new String(password.getPassword()));
					valido = player.estado;}
				if(!valido){
					update(getGraphics());
					getGraphics().setFont(new Font("Dialog", Font.PLAIN, 11));
					getGraphics().drawString("User already exists or wrong password.", 10, 190);
					b1.setBorder(borde);
					user.setBorder(borde);
					password.setBorder(borde);
				}
			}
		});
		enterAction = new EnterAction();
		password.getInputMap().put( KeyStroke.getKeyStroke("ENTER"), "doEnterAction" ); 
		password.getActionMap().put( "doEnterAction", enterAction );

		b1.setPreferredSize(new Dimension(90, 25));
		b1.setFont(new Font("Arial", 1, 11));
		southPanel.add(b1);
		this.add(southPanel, BorderLayout.SOUTH);
	}

	class EnterAction extends AbstractAction{
		public void actionPerformed( ActionEvent tf ) { 
			b1.doClick(); 
		}  
	}
}

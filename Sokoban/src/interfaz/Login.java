package interfaz;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import jugador.Player;

@SuppressWarnings("serial")
public class Login extends JFrame{

	JTextField user;
	JPasswordField password;
	Player player;
	boolean valido;

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
		JButton b1 = new JButton("Entrar"); 
		b1.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Border borde = new LineBorder(Color.RED, 1);
				player = new Player(user.getText(), new String(password.getPassword()));
				valido = player.estado;
				if(!valido){b1.setBorder(borde);
				password.setBorder(borde);}
			}
		});

		b1.setPreferredSize(new Dimension(90, 25));
		b1.setFont(new Font("Arial", 1, 11));
		southPanel.add(b1);
		this.add(southPanel, BorderLayout.SOUTH);
	}
}

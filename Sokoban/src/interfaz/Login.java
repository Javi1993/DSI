package interfaz;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class Login extends JFrame{

	JTextField user;
	JTextField password;
	boolean valido;

	public Login(){
		this.setTitle("DSI | Sokoban");
		valido = false;
		this.getContentPane().setLayout(new FlowLayout());
		crearLabels();
		crearBotones();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setPreferredSize(new Dimension(210, 200));
		this.pack();
		this.setResizable(false);
		this.setVisible(true);
	}

	private void crearLabels(){
		JLabel label = new JLabel("USER");
		user = new JTextField(15);
		this.getContentPane().add(label);
		this.getContentPane().add(user);
		JLabel label1 = new JLabel("PASSWORD");
		password = new JTextField(15);
		this.getContentPane().add(label1);
		this.getContentPane().add(password);
	}

	private void crearBotones(){
		JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // new FlowLayout not needed
		southPanel.setOpaque(true);
		JButton b1 = new JButton("Login"); 
		b1.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.out.println(user.getText());
				System.out.println(password.getText());
				valido = true;
			}
		});

		JButton b2 = new JButton("Register");  
		b1.setPreferredSize(new Dimension(90, 25));
		b1.setFont(new Font("Arial", 1, 11));
		b2.setPreferredSize(new Dimension(90, 25)); 
		b2.setFont(new Font("Arial", 1, 11));
		southPanel.add(b1);
		southPanel.add(b2);
		this.add(southPanel, BorderLayout.SOUTH);
	}
}

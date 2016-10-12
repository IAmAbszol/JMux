package GUI;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

import Main.JMuxServer;

public class Frame extends JFrame {
	
	private JTextArea jta;
	private JFrame frame;
	private JPanel panel;
	private JButton btn;
	private JLabel label;
	private JButton user;
	
	private int connected = 0;
	private int max = 0;
	
	public Frame() {
		
		setTitle("JMux Server");
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 200, 100);
		
		panel = new JPanel();
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		panel.setLayout(new BorderLayout(0, 0));
		setContentPane(panel);
		
		btn = new JButton("Close Server");
		panel.add(btn, BorderLayout.EAST);
		
		btn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JMuxServer.stopConnection();
				if(frame != null)
					frame.dispose();
			}
			
		});
		
		user = new JButton("User List");
		panel.add(user, BorderLayout.WEST);
		
		user.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Thread t = new Thread(new Runnable() {

					@Override
					public void run() {

						displayUsers();
						
					}
					
				});
				t.start();
			}
		});
		
		label = new JLabel("Users Connected " + connected + "/" + max);
		panel.add(label, BorderLayout.NORTH);
		
		pack();
		setVisible(true);
		
		
	}
	
	public void setMax(int max) {
		this.max = max;
		label.setText("Users Connected " + connected + "/" + max);
	}
	
	public void setConnected(int connected) {
		this.connected = connected;
		label.setText("Users Connected " + connected + "/" + max);
	}

	public void displayUsers() {
		
		frame = new JFrame();
		frame.setTitle("Users List");
		frame.setBounds(100, 100, 400, 600);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		
		JPanel pan = new JPanel();
		pan.setBorder(new EmptyBorder(5,5,5,5));
		pan.setLayout(new BorderLayout(0,0));
		frame.setContentPane(pan);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setBounds(10, 11, 614, 400);
		pan.add(scrollPane);
		
		//Building the text area (Everything text goes). Also added scroll bar to it
		jta = new JTextArea();
		DefaultCaret caret = (DefaultCaret)jta.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		jta.setEditable(false);
		jta.setFont(new Font("DialogInput", Font.PLAIN, 13));
		jta.setWrapStyleWord(true);
		scrollPane.setViewportView(jta);

		if(JMuxServer.clients != null) {
			try {
				jta.append("Server IP Address " + InetAddress.getLocalHost() + " Port " + JMuxServer.portNumber + "\n");
				jta.append("------------------------------------------------------\n");
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for(int i = 0; i < JMuxServer.clients.length; i++) {
				if(JMuxServer.clients[i] != null)
					jta.append("Client ["+ i + "]: IP Address " + JMuxServer.clients[i].getInetAddress() + " Port " + JMuxServer.clients[i].getPort() + "\n");
			}
		}
		
		frame.setVisible(true);
	}
	
}

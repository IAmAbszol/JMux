package Main;

import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;

import javax.imageio.ImageIO;

import GUI.Frame;

/*
 * Server/teaching side of JMux, this Server merely copies what you are doing and converts the information
 * from a screen cap to sensible ascii/text information.
 * From this, the server sends ot the client that is always awaiting information to be read in.
 * 
 * This form is going to send an image over to the client to look at.
 */
public class JMuxServer {
	
	private static boolean run = true;

	public static int portNumber = 12345;
	
	private static Frame frame;
	private static ServerSocket serverSocket;
	
	// server as mouse pointers to draw at
	private static int x;
	private static int y;
	
	// Spawn clients
	private static Thread thread[];
	public static Socket clients[];
	private static DataOutputStream dOut[];
	private static int maxNumber = 100;
	private static int currentNumber = 0;
	private static int attackNumber = 0;
	
	private static Thread screenThread;
	public static BufferedImage screen;
	
	public static void main(String[] args) {
		
		thread = new Thread[maxNumber];
		clients = new Socket[maxNumber];
		dOut = new DataOutputStream[maxNumber];
		
		try {
		
			System.out.println("Starting Up JMuxServer");
			serverSocket = new ServerSocket(portNumber);
			frame = new Frame();
			frame.setMax(maxNumber);
			
			// spawn a new thread for screen capture
			screenThread = new Thread(new Runnable() {
				public void run() {
					try {
						while(true) {
							screen = createImage();
							Thread.sleep(1);
						}
					} catch (Exception e) {
						
					}
				}
			});
			
			// enact a new thread
			screenThread.start();
			
			while(true) {
				if(currentNumber < maxNumber) {
					for(int i = 0; i < maxNumber; i++) {
						if(clients[i] == null) {
							attackNumber = i;
							break;
						}
					}
					System.out.println("Waiting On Client Connection");
					// only accepts one client at this time, I could fork the process eventually
					clients[attackNumber] = serverSocket.accept();
					
					System.out.println("Client Connected... Lets Begin");
				
					// while running - spawn own thread
					thread[attackNumber] = new Thread(new Runnable() {
						int pos = attackNumber;
						public void run() {
							try {
							
								dOut[pos] = new DataOutputStream(clients[pos].getOutputStream());
							
								while(run) {
									
									// create the currentImage and send it off to the client
										writeToClient(pos);
									
								}	
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
	
					thread[attackNumber].start();
					currentNumber++;
					frame.setConnected(currentNumber);
				}
			}
		
		} catch (Exception e) {
			run = false;
		}
		
	}
	
	private static BufferedImage createImage() {
		try {
			Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
			BufferedImage capture = new Robot().createScreenCapture(screenRect);
			
			x = MouseInfo.getPointerInfo().getLocation().x;
			y = MouseInfo.getPointerInfo().getLocation().y;
			
			BufferedImage cursor = ImageIO.read(JMuxServer.class.getResource("/cursor.png"));
			
			Graphics g = capture.createGraphics();
			g.drawImage(cursor, x, y, 20, 20, null);
			
			//ImageIO.write(capture, "bmp", new File(findPath(imageName))); this works
			return capture;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	private static void writeToClient(int pos) {
		try { 
			byte[] imgByte;
			
			// convert to byte array for sending
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(screen, "jpg", baos);
			baos.flush();
			imgByte = baos.toByteArray();
			baos.close();
			
			// now to send over
			dOut[pos].writeInt(imgByte.length);
			dOut[pos].write(imgByte);
		} catch (Exception e) {
			// connection properly ended
			dOut[pos] = null;
			clients[pos] = null;
			currentNumber--;
			frame.setConnected(currentNumber);
			thread[pos] = null;
			thread[pos].stop();
			System.out.print("");
		}
		
	}
	
	public static void stopConnection() {
		run = false;
		frame.dispose();
		frame = null;
		System.exit(0);
	}
	
}

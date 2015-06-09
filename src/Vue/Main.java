package Vue;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;

import javax.swing.SwingUtilities;

import tftp.Client;

public class Main {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				Client c;
				try {
					c = new Client();
					View fenetre = new View(c);
					fenetre.setVisible(true);
				} catch (SocketException e) {
					System.out.println("Erreur lors de la creation du client");
				}
				
			}
		});
		
		/*try {
			Client c = new Client();
			InetAddress adr = InetAddress.getByName("127.0.0.1");
			c.envoyer(c.makeRRQ("cas1"), adr);
		} catch (SocketException e) {
			System.out.println("Erreur lors de la creation du client");
		} catch (IOException e) {
			e.printStackTrace();
		}*/

	}

}

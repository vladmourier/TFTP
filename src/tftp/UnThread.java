/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tftp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author p1407206
 */
public class UnThread extends Thread {

    public Client c;
    public String nf_distant;
    public InetAddress ia;

    public UnThread(Client c, String nf_distant, InetAddress ia) {
        this.c = c;
        this.nf_distant = nf_distant;
        this.ia = ia;
    }

    public void run() {
        // faire quelque chose avec str

        //faire un receive
        //recuperer le numéro des data envoyées : bloc
        //faire un ACK
        // renvoyer le ACK sur le port
    	  
    	
    	  
        try {
        	c.ds.setSoTimeout(3000);
            byte[] buffer = new byte[516];
            c.dp = new DatagramPacket(buffer, buffer.length, ia, 69);
            c.ds.receive(c.dp);
            //Recupération de l'opcode
            short opcode = c.dp.getData()[0];
            opcode <<= 8;
            opcode += c.dp.getData()[1];
            System.out.println(opcode);

            //L'opcode 3 correspond à l'envoie de data depuis le serveur, on va donc les récupérer
            if (opcode == 3) {
                short numblock = c.dp.getData()[0];
                numblock <<= 8;
                numblock += c.dp.getData()[1];
                
                buffer = c.makeACK((short) numblock);
                DatagramPacket dp2 = new DatagramPacket(buffer, buffer.length, c.dp.getSocketAddress());
                c.ds.send(dp2);
            }
            
          
        } catch (IOException ex) {
            Logger.getLogger(UnThread.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}

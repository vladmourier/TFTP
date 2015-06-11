/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tftp;

import java.io.File;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static tftp.TestReceptionPumpkin.IP_Serveur;

/**
 *
 * @author Vladimir
 */
public class TFTP {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SocketException {
        int retour;
        String serveur = "test.txt";
        String local = "C:\\Users\\Transports Vivarais\\Desktop\\aa.txt";
        InetAddress ia = null;
        TestReceptionPumpkin test = new TestReceptionPumpkin();
        try {
            ia = InetAddress.getByName(IP_Serveur);
        } catch (UnknownHostException ex) {
            Logger.getLogger(TestReceptionPumpkin.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("On va lancer la fonction de reception du fichier");
        retour = test.receiveFile(local, serveur, ia);
        System.out.println("Fichier reçu");
        }
    
}

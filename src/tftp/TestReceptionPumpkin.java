/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tftp;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author p1206512
 */
public class TestReceptionPumpkin extends Client{
    
    protected static String IP_Serveur = "localhost";
    
    public TestReceptionPumpkin() throws SocketException
    {
        super();
    }
    
    public static void main(String[] args) throws UnsupportedEncodingException, SocketException {
        int retour;
//        String serveur = "2013-10-27_13.34.51.jpeg";
        String serveur = "Scandinavia.docx";
//        String serveur = "test.txt";
//        String local = "C:\\\\Users\\\\Adrien_portable\\\\Desktop\\\\2013-10-27_13.34.51.jpeg";
        String local = "C:\\Users\\Adrien_portable\\Desktop\\Scandinavia.docx";
//        String local = "C:\\Users\\Adrien_portable\\Desktop\\test1.txt";
        InetAddress ia = null;
        TestReceptionPumpkin test = new TestReceptionPumpkin();
        
        try {
            ia = InetAddress.getByName(IP_Serveur);
        } catch (UnknownHostException ex) {
            System.err.println("problème de localhost");
            Logger.getLogger(TestReceptionPumpkin.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("On va lancer la fonction de reception du fichier");
        retour = test.receiveFile(local, serveur, ia);
        System.out.println(retour);
        System.out.println("Fichier reçu");
    }
}

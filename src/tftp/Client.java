/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tftp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Vladimir
 */
public class Client extends ObjetConnecte {

    public Client() throws SocketException {
        super();
    }

    public Client(int i) throws SocketException {
        super(i);
        this.ds = new DatagramSocket(this.port_c);

    }

    public Client(InetAddress ia, int i) throws SocketException {
        super(ia, i);
        this.ds = new DatagramSocket(this.port_c);
    }

    public byte[] makeACK(short bloc) {
        return new String("\0" + "\4" + bloc).getBytes();
    }

    public byte[] makeDATA(short bloc, byte[] datas) {
        return new String("\0" + "\3" + bloc + datas).getBytes();
    }

    public byte[] makeRRQ(short bloc, String fichier) {
        return new String("\0" + "\1" + fichier + "\0" + "octet" + "\0").getBytes();
    }

    public byte[] makeWRQ(String fichier) {
        return new String("\0" + "\2" + fichier + "\0" + "octet" + "\0").getBytes();
    }

    private void envoyer(byte[] array, InetAddress address) throws IOException {
        System.out.println("DatagramSocket Client OK");
        this.ia_c = address;
        this.dp = new DatagramPacket(array, array.length, ia_c, 69);
        System.out.println("DatagramPacket Client OK");
        ds.send(dp);
        System.out.println("Send Client OK");
    }

    private ArrayList<byte[]> scinder(byte[] fichier, int taille) {
        int restant = fichier.length;
        byte[] temp = new byte[taille];
        int curseur = 0, position;
        ArrayList<byte[]> retour = new ArrayList<>();
        if (fichier.length > taille) {
            curseur = 0;
            temp = new byte[taille];
            while (curseur < fichier.length && restant >= taille) {
                position = 0;
                while (position != taille - 1 || curseur < fichier.length) {
                    position = curseur % taille;
                    temp[position] = fichier[curseur];
                    curseur++;
                    restant--;
                }
                retour.add(temp);
            }
        }
        if (restant <= taille) {
            for (int i = 0; i < restant; i++) {
                position = curseur % taille;
                temp[position] = fichier[curseur];
                curseur++;
            }
        }

        return retour;
    }
/*
    public int SendFile(String filename, InetAddress address) throws IOException {
        File fichier;
        FileInputStream fic;
        try {
            fichier = new File(filename);
            fic = new FileInputStream(filename);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        int count = 0, result = 5;
        byte[] WRQ = makeWRQ(filename);
        while (count < 3 || result == 0) {
            envoyer(WRQ, address);
            result = recevoir();
            if (result == 1) {
                ++count;
            }
        }
        if (count == 3) {
            return 1;
        }
        count = 0;
        if (fichier.getTotalSpace() > 512) {
            ArrayList<byte[]> partitions = scinder(fic, 512);
            for (byte[] partition : partitions) {
                while (count < 3 || result == 0) {
                    envoyer(partition, address);
                    result = recevoir();
                    if (result == 1) {
                        ++count;
                    }
                }
                if (count == 3) {
                    return 2;
                }
            }
        } else {
            while (count < 3 || result == 0) {
                envoyer(partition, address);
                result = recevoir();
                if (result == 1) {
                    ++count;
                }
            }
            if (count == 3) {
                return 2;
            }
        }
        return 0;
    }
*/
    
    //TODO CHANGER LE TYPE DE RETOUR
    public File receiveFile(String nf_local, String nf_distant, InetAddress ia)
    {
        try {
            //Le fichier qui sera renvoyer en fin de fonction
            File f = new File(nf_local);
            //L'objet fichier dans lequel on peut écrire
            FileWriter fichier = new FileWriter(f, true);
            //L'objet buffer qui gère les donnés
            BufferedWriter input = new BufferedWriter(fichier);
            //Le compteur de data reçu, sert pour les numéros de packets
            short data = 1;
            //Le booleen de sortie de boucle, un test le passe a faux si le fichier est complet, on n'attend plus de réception
            boolean complet = false;
            //Le buffer qui permet de renvoyer des ACK et le RRQ
            byte[] buffer;
            
            
            //Préparation et envoie de la requête de demarrage
            buffer = makeRRQ((short) data, nf_distant);
            dp = new DatagramPacket(buffer, buffer.length, ia, 69);
            ds.send(dp);
            
            //Reception du fichier
            while(complet == false)
            {
                ds.receive(dp);
                //Recupération de l'opcode
                short opcode = dp.getData()[0];
                opcode <<= 8;
                opcode += dp.getData()[1];
                System.out.println(opcode);
                
                //L'opcode 3 correspond à l'envoie de data depuis le serveur, on va donc les récupérer
                if(opcode == 3)
                {
                    short numblock = dp.getData()[0];
                    opcode <<= 8;
                    opcode += dp.getData()[1];
                    if (numblock == data)
                    {
                        data++;
                        for (int i = 4; i < dp.getLength(); i++)
                        {
                            input.write(dp.getData()[i]);
                            input.flush();
                        }
                        if(dp.getLength() < 512) complet = true;
                    }
                    buffer = makeACK((short)data);
                    dp.setData(buffer);
                    dp.setLength(buffer.length);
                    ds.send(dp);
                }
            }
            
            //Ajouter une tempo, fermer le socket
            
            input.close();
            ds.close();
            return f;
            
        } catch (SocketException ex) {
            Logger.getLogger(ObjetConnecte.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ObjetConnecte.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ObjetConnecte.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /**
     *
     */
    public void communication(String s, int port, InetAddress server) throws SocketException, UnsupportedEncodingException, UnknownHostException, IOException {
        System.out.println("DatagramSocket Client OK");
        this.ia_c = InetAddress.getByName("localhost");
        this.dp = new DatagramPacket(s.getBytes("ascii"), s.length(), ia_c, port);
        System.out.println("DatagramPacket Client OK");
        ds.send(dp);
        System.out.println("Send Client OK");
    }

    public boolean reception() throws SocketException, IOException {
        while (true) {
            System.out.println("J'attends un envoi");
            byte[] buffer = new byte[this.MAX];
            this.dp = new DatagramPacket(buffer, buffer.length);
            this.ds.receive(this.dp);
            if (this.dp.getLength() != 0) {
                System.out.println("recu : " + new String(new String(this.dp.getData()).substring(0, this.dp.getData().length)));
                return true;
            }
        }
    }
}

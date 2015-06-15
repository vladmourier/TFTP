/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tftp;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
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

//    public byte[] makeACK(short numBloc) {
//        
//        
//        byte[] bytes = ByteBuffer.allocate(2).putShort(numBloc).array();
//        
//        String nb = new String(bytes);
//       
//        String rrq = "\0\4" + nb;
//        System.out.println("ack :"+rrq);
//        return rrq.getBytes();
//    }
    
public byte[] makeACK(short bloc) {
        ByteArrayOutputStream dataStream = new ByteArrayOutputStream(4);
        DataOutputStream dataWriter = new DataOutputStream(dataStream);
        try {
            dataWriter.writeByte(0);
            dataWriter.writeByte(4);
            dataWriter.writeShort(bloc);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.err.println(dataWriter.toString());
        }
        return dataStream.toByteArray();
    }

    public byte[] makeDATA(short bloc, byte[] datas) {
        ByteArrayOutputStream dataStream = new ByteArrayOutputStream(4 + datas.length);
        DataOutputStream dataWriter;
        dataWriter = new DataOutputStream(dataStream);
        try {
            dataWriter.writeByte(0);
            dataWriter.writeByte(3);
            dataWriter.writeShort(bloc);
            System.out.println(Short.SIZE);
            dataWriter.write(datas);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.err.println(dataWriter.toString());
        }
        return dataStream.toByteArray();
    }

    public byte[] makeRRQ(short bloc, String fichier) {
        return new String("\0" + "\1" + fichier + "\0" + "octet" + "\0").getBytes();
    }

    public byte[] makeWRQ(String fichier) {
        return new String("\0" + "\2" + fichier + "\0" + "octet" + "\0").getBytes();
    }

    public void envoyer(byte[] array, InetAddress address, int port) throws IOException {
        this.ia_c = address;
        this.dp = new DatagramPacket(array, array.length, ia_c, port);
        ds.send(dp);
    }

    public ArrayList<byte[]> scinder(FileInputStream FIS, int taille) throws IOException {
        System.out.println("fonction scinder ; availables : " + FIS.available());
        int offset = 0, availables = FIS.available();
        ArrayList<byte[]> retour = new ArrayList<byte[]>();
        byte[] buffer;
        if (availables >= taille) {
            while (FIS.available() >= taille) {
                System.out.println("offset = " + offset + " ; offset+taille =" + (offset + taille) + " ; " + " ; " + "availables : " + FIS.available());
                if (availables - offset <= taille) {
                    buffer = new byte[availables - offset];
                    FIS.read(buffer);
                    retour.add(buffer);
                } else {
                    buffer = new byte[taille];
                    FIS.read(buffer);
                    retour.add(buffer);
                    offset += taille;
                }
                System.out.println("Ajout d'une partition de taille : " + buffer.length);
            }
        }
        buffer = new byte[FIS.available()];
        FIS.read(buffer);
        retour.add(buffer);

        return retour;
    }

    public short getOpCode() {
        short opcode = this.dp.getData()[0];
        opcode <<= 8;
        opcode += this.dp.getData()[1];
        return opcode;
    }

    public int SendFile(String filename, InetAddress address) throws IOException {
        int port_s = 0;
        this.ds.setSoTimeout(30000);
        byte[] paquet = null;//Le buffer permettant de recevoir les paquets
        File fichier_local;
        FileInputStream FIS;
        ArrayList<byte[]> partitions = new ArrayList<byte[]>();
        short bloc = 0, essais = 1;
        boolean envoye = false;
//Récupération du fichier
        try {
            fichier_local = new File(filename);
            FIS = new FileInputStream(fichier_local);
            System.out.println("FIS.available() = " + FIS.available());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
//Envoi du WRQ
        while (essais <= 3 && envoye == false) {
            System.out.println("envoi du WRQ");
            paquet = makeWRQ(filename);
            this.ds.send(this.dp = new DatagramPacket(paquet, paquet.length, address, 69));
            paquet = new byte[4];
            this.dp = new DatagramPacket(paquet, paquet.length);
            this.ds.receive(dp);
            if (this.getOpCode() == 4 && getBloc(dp.getData()) == bloc) {
                envoye = true;
                port_s = this.dp.getPort();
            }
            essais += 1;
        }
        if (essais >= 3) {
            System.out.println("essais = " + essais);
            return 1;
        }
        ++bloc;
        essais = 1;
        envoye = false;
//Recupération de l'opcode
        if (this.getOpCode() == (short) 4) {
//Envoi des données
            partitions = this.scinder(FIS, 512);
            System.out.println("fichier découpé en " + partitions.size() + " partitions");
            for (byte[] partition : partitions) {
                System.out.println("/////////////////Partition");
                envoye = false;
                while (essais <= 3 && envoye == false) {
                    envoye = false;
                    System.out.println("j'envoie un paquet de : " + partition.length + " octets");
                    paquet = this.makeDATA(bloc, partition);
                    this.dp = new DatagramPacket(paquet, paquet.length, InetAddress.getByName("localhost"), port_s);
                    this.ds.send(this.dp);
                    paquet = new byte[4];
                    this.dp = new DatagramPacket(paquet, paquet.length);
                    this.ds.receive(dp);
                    if (this.getOpCode() == 4 && getBloc(dp.getData()) == bloc) {
                        envoye = true;
                        System.out.println("Paquet " + bloc + " OK");
                    }
                    essais += 1;
                }
                if (essais >= 3) {
                    return 2;
                }
                ++bloc;
                essais = 1;
            }
            System.out.println("Fin de la fonction");
        } else {
            System.out.println("mauvais opcode recu");
        }
        //Ajouter une tempo, fermer le socket
        FIS.close();
        return 0;
    }

//TODO CHANGER LE TYPE DE RETOUR
    //bouclage ack à la fin pour stoper les envoie serveurs en cas d'erreurs
    //fermeture DS correcte
    public int receiveFile(String nf_local, String nf_distant, InetAddress ia) {
        try {
            //Le fichier a récuperer
            File f = new File(nf_local);
            //L'objet dans lequel on peut écrire le fichier
            FileOutputStream fichier = new FileOutputStream(f, true);
            //L'objet buffer qui gère les donnés à écrire directement dans le ficheir
            //BufferedWriter input = new BufferedWriter(fichier);
            //Le compteur de data reçu, sert pour les numéros de packets, commence à 1 car on commence par recevoir data = 1
            short data = 1;
            //Le booleen de sortie de boucle, un test le passe a true si le fichier est complet, on n'attend plus de réception
            boolean fichierComplet = false;
            //Le buffer qui permet de renvoyer des ACK et le RRQ
            byte[] bufferEnvoi;
            int essai = 0;
            
            //Préparation et envoie de la requête de demarrage
            bufferEnvoi = makeRRQ((short) data, nf_distant);
            dp = new DatagramPacket(bufferEnvoi, bufferEnvoi.length, ia, 69);
            ds.send(dp);

            //Reception du fichier
            while (fichierComplet == false) {
                DatagramPacket dpr = new DatagramPacket(new byte[516], 516);
                //ds.setSoTimeout(3000);
                ds.receive(dpr);
                //Recupération de l'opcode
                short opcode = dpr.getData()[0];
                opcode <<= 8;
                opcode += dpr.getData()[1];
                System.out.print("opcode " + opcode);

                //L'opcode 3 correspond à l'envoie de data depuis le serveur, on va donc les récupérer
                if (opcode == 3) {
                    //Recupération du numéro de packet
                    short numblock = dpr.getData()[2];
                    numblock <<= 8;
                    numblock += dpr.getData()[3];
                    System.out.print(" packet number " + numblock);

                    //La variable globale data stock le numéro de packet attendu
                    if (numblock == data) {
                        System.out.println(" : Ecriture du packet");
                        if(data == 127) data = 0;
                        data++;
                        //On récupère les données du 4ème byte jusqu'à la fin du datagrampacket
                        for (int i = 4; i < dpr.getLength(); i++) {
                            fichier.write(dpr.getData()[i]);
                            fichier.flush();
                        }
                        if (dpr.getLength() < 516) {
                            System.out.println("Packet de taille < 516");
                            fichierComplet = true;
                        }
                    }
                    bufferEnvoi = makeACK((short) numblock);
                    dp = new DatagramPacket(bufferEnvoi, bufferEnvoi.length, dpr.getSocketAddress());
                    ds.send(dp);
                    essai = 0;
                }
                else if (opcode == 5)
                {
                    System.out.println("Une erreur est servenue durant le transfert");
                    short numblock = dpr.getData()[2];
                    numblock <<= 8;
                    numblock += dpr.getData()[3];
                    
                    if(numblock == 1)   return -1;  //Fichier non trouvé
                    if(numblock == 2)   return -2;  //Violation d'accès
                    return -3;  //autre erreur
                }
                else
                {
                    essai++;
                    if (essai > 5)  return 1;   //La communication avec le serveur n'a pas été possible
                }
            }

            //Temporisation
//            UnThread monThread = new UnThread(this, nf_distant, ia);
//            monThread.start();
            
            fichier.close();
            return 0;

        } catch (SocketException ex) {
            Logger.getLogger(ObjetConnecte.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(ObjetConnecte.class
                    .getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ObjetConnecte.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return 2;   //autre erreur
    }

    public short getBloc(byte[] DATA) {
        ByteBuffer buff = ByteBuffer.wrap(DATA);
        buff.getShort();
        return buff.getShort();
    }
    
    public void closeSocket() {
    	ds.close();
    }
}

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
        ArrayList<byte[]> retour = new ArrayList<>();
        byte[] buffer;
        if (availables >= taille) {
            while (FIS.available() >= taille) {
                System.out.println("offset = " + offset + " ; offset+taille =" + (offset+taille) + " ; " + " ; " + "availables : " + FIS.available());
                if (availables - offset <= taille) {
                    buffer=new byte[availables - offset];
                    FIS.read(buffer);
                    retour.add(buffer);
                } else {
                    buffer = new byte[taille];
                    FIS.read(buffer);
                    retour.add(buffer);
                    offset += taille;
                }
                System.out.println("Ajout d'une partition de taille : " + buffer.length );
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
        ArrayList<byte[]> partitions = new ArrayList<>();
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
                envoye=false;
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
        ds.close();
        return 0;
    }

//TODO CHANGER LE TYPE DE RETOUR
    public File receiveFile(String nf_local, String nf_distant, InetAddress ia) {
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
                DatagramPacket dpr = new DatagramPacket(new byte[516], 516);
                ds.receive(dpr);
                //Recupération de l'opcode
                short opcode = dpr.getData()[0];
                opcode <<= 8;
                opcode += dpr.getData()[1];
                System.out.println(opcode);

                //L'opcode 3 correspond à l'envoie de data depuis le serveur, on va donc les récupérer
                if(opcode == 3)
                {
                    //Recupération du numéro de packet
                    short numblock = dpr.getData()[2];
                    numblock <<= 8;
                    numblock += dpr.getData()[3];
                    System.out.println(numblock);
                    
                    //La variable globale data stock le numéro de packet attendu
                    if (numblock == data)
                    {
                        data++;
                        //On récupère les données du 4ème byte jusqu'à la fin du datagrampacket
                        for (int i = 4; i < dpr.getLength(); i++)
                        {
                            input.write(dpr.getData()[i]);
                            input.flush();
                        }
                        if(dpr.getData().length < 516) complet = true; //problèm de longueur de Datagrampacket, il fait toujours 516 comme il est fixé à cette taille, donc ça s'rrete jamais ...
                    }
                    buffer = makeACK((short)data);
                    dpr = new DatagramPacket(buffer, buffer.length, dp.getSocketAddress());
                    ds.send(dpr);
                }
            }
            
            //Temporisation
            ds.setSoTimeout(30000);
            
            
            input.close();
            ds.close();
            return f;

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
        return null;
    }

    public int reception(byte[] attendu) throws SocketException, IOException {
        try {
            System.out.println("J'attends un envoi");
            byte[] buffer = new byte[this.MAX];
            this.dp = new DatagramPacket(buffer, buffer.length);
            this.ds.setSoTimeout(10);
            this.ds.receive(this.dp);
        } catch (SocketException s) {
            return 1;
        }
        if (Arrays.equals(this.dp.getData(), attendu)) {
            return 0;
        } else {
            return -1;
        }
    }
    
    
//// TODO : concat DATA[2] et DATA[3]
//	public short getBloc(Byte[] DATA) {
//		return DATA[2].shortValue();
//
//	}
        
        
//        public void ReceiveFile(String ficherLocal, String fichierDistant, InetAddress adresseDistante) {
//
//		byte[] rrq = new String("RRQ").getBytes();
//		byte[] ack = new String("\0\1").getBytes();
//		this.envoyer(rrq,  adresseDistante);
//		FileOutputStream fichier = new FileOutputStream (ficherLocal); // crée un fichier à l'emplacement de fichierLocal
//		int compteur = 1;
//		while (true) {
//			byte[] reception = reception();
//			if (reception.length != 0) { // Si succès
//				this.envoyer(this.makeACK(bloc), adresseDistante); // TODO
//				if (compteur == this.DATA) {
//					compteur++
//					fichier.write(datas)
//				}
//				if (datas.taille < 512) {
//					break
//				}
//			}
//		}
//	}
        
}

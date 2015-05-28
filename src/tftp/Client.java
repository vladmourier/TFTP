/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
    
    
// TODO : concat DATA[2] et DATA[3]
	public short getBloc(Byte[] DATA) {
		return DATA[2].shortValue();

	}
        
        
        public void ReceiveFile(String ficherLocal, String fichierDistant, InetAddress adresseDistante) {

		byte[] rrq = new String("RRQ").getBytes();
		byte[] ack = new String("\0\1").getBytes();
		this.envoyer(rrq,  adresseDistante);
		FileOutputStream fichier = new FileOutputStream (ficherLocal); // crée un fichier à l'emplacement de fichierLocal
		int compteur = 1;
		while (true) {
			byte[] reception = reception();
			if (reception.length != 0) { // Si succès
				this.envoyer(this.makeACK(bloc), adresseDistante); // TODO
				if (compteur == this.DATA) {
					compteur++
					fichier.write(datas)
				}
				if (datas.taille < 512) {
					break
				}
			}
		}
	}
        
}

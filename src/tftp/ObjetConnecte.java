/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tftp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

/**
 *
 * @author Vladimir
 */
public class ObjetConnecte {

    public String name;
    int MAX;
    int port_c;
    public InetAddress ia_c;
    public DatagramSocket ds;
    public DatagramPacket dp;

    public ObjetConnecte() throws SocketException {
        this.ds = new DatagramSocket();
        this.port_c = this.ds.getLocalPort();
        this.MAX = 2000;
    }

    public ObjetConnecte(InetAddress ia_c) {
        this.ia_c = ia_c;
        this.MAX = 2000;

    }

    public ObjetConnecte(int port) {
        this.port_c = port;
        this.MAX = 2000;
    }

    public ObjetConnecte(InetAddress ia_c, int port) {
        this.ia_c = ia_c;
        this.port_c = port;
        this.MAX = 2000;

    }

    public ArrayList<Integer> ScannerUDP(ArrayList<Integer> plage) {
        ArrayList<Integer> retour = new ArrayList<Integer>();
        int a = plage.get(0);
        int b = plage.get(1);
        for (int i = a; i <= b; i++) {
            try {
                DatagramSocket temp = new DatagramSocket(i);
                retour.add(i);
                temp.close();
            } catch (SocketException ex) {
//                Logger.getLogger(ObjetConnecte.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return retour;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMAX() {
        return MAX;
    }

    public void setMAX(int MAX) {
        this.MAX = MAX;
    }

    public int getPort_c() {
        return port_c;
    }

    public void setPort_c(int port_c) {
        this.port_c = port_c;
    }

    public InetAddress getIa_c() {
        return ia_c;
    }

    public void setIa_c(InetAddress ia_c) {
        this.ia_c = ia_c;
    }

    public DatagramSocket getDs() {
        return ds;
    }

    public void setDs(DatagramSocket ds) {
        this.ds = ds;
    }

    public DatagramPacket getDp() {
        return dp;
    }

    public void setDp(DatagramPacket dp) {
        this.dp = dp;
    }

}

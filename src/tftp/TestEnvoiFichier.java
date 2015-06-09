/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tftp;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 *
 * @author Vladimir
 */
public class TestEnvoiFichier {

    public static void main(String[] args) throws SocketException, UnknownHostException, IOException {
        Client c = new Client();
        System.out.println(c.SendFile("essai.txt", InetAddress.getByName("localhost")));
    }

}

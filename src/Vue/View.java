package Vue;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import tftp.Client;

/**
 *
 * @author Loic
 */
public class View extends JFrame{

	private File fichierSelectionne;
	
	private Client client;
	
	JComponent pan = new JPanel (new BorderLayout());
    JButton envoyer = new JButton("Envoyer fichier");
    JButton recevoir = new JButton("Recevoir fichier");
    JTextField txt_adress = new JTextField();
    JLabel lbl_adress = new JLabel("Adresse du serveur");
    JLabel lbl_fichier = new JLabel("Fichier à envoyer");
    JLabel lbl_chem = new JLabel("Chemin");
    JLabel lbl_chemin = new JLabel();
    JButton parcourir = new JButton("Parcourir");
    JLabel lbl_adressDistant = new JLabel("Fichier à recevoir");
    JTextField txt_adressDistant = new JTextField();
    JLabel lbl_erreur = new JLabel();
    
    public View(Client c) {
        super();
        this.client = c;
        build();
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        
    }
    
    public void build() {
    	
        setTitle("Bienvenue sur le client TFTP");
        setSize(400, 400);
        
        
        JPanel bot = new JPanel();
        JPanel top = new JPanel(new GridLayout(4,2));
        JPanel mid = new JPanel();
        
        envoyer.addActionListener(new BoutonEnvoyer());
        recevoir.addActionListener(new BoutonRecevoir());
        parcourir.addActionListener(new BoutonParcourir());
        
        top.add(lbl_adress);
        top.add(txt_adress);
        top.add(lbl_fichier);
        top.add(parcourir);
        top.add(lbl_chem);
        top.add(lbl_chemin);
        top.add(lbl_adressDistant);
        top.add(txt_adressDistant);
        bot.add(envoyer);
        bot.add(recevoir);
        mid.add(lbl_erreur);
        
        pan.add(top,BorderLayout.NORTH);
        pan.add(mid,BorderLayout.CENTER);
        pan.add(bot,BorderLayout.SOUTH);
        add(pan);
    }
    
	  class BoutonParcourir implements ActionListener{
	    public void actionPerformed(ActionEvent arg0) {
	    	// création de la boîte de dialogue
	        JFileChooser dialogue = new JFileChooser();
	         
	        // affichage
	        dialogue.showOpenDialog(null);
	        fichierSelectionne = dialogue.getSelectedFile();
	        
	        lbl_chemin.setText(fichierSelectionne.toString());
	    }
	  }
	
	  class BoutonEnvoyer implements ActionListener{
	    public void actionPerformed(ActionEvent arg0) {
	    	if(!(fichierSelectionne == null || txt_adress.getText().equals(""))) {
	    		lbl_erreur.setText("Envoi du fichier");
	    		//client.SendFile(fichierSelectionne.toString(), txt_adress.toString());
	    	}
	    	else { lbl_erreur.setText("Veuillez renseigner le fichier à envoyer et l'adresse"); }
	    }
	  }
	      
	  class BoutonRecevoir implements ActionListener{
	    public void actionPerformed(ActionEvent e) {
	    	if(!(txt_adressDistant.getText().equals("") || txt_adress.getText().equals(""))) {
	    		lbl_erreur.setText("Reception du fichier");
	    		//client.ReceiveFile(txt_adressDistant.toString(), txt_adressDistant.toString(), txt_adress.toString());
	    	}
	    	else { lbl_erreur.setText("Veuillez renseigner le fichier à recevoir et l'adresse"); }
	    }
	  }      
}

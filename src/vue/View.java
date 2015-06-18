package vue;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import tftp.Client;

import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;


public class View {

	private JFrame frmClientTftp;
	private JTextField txtF_adrServeur;
    private File fichierEnvoi;
    private File fichierReception;
    public Client client;
    
    private JLabel lbl_chE;
    private JLabel lbl_chR;
    private JTextField txtF_fichierRecevoir;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Client c = new Client();
					View window = new View(c);
					window.frmClientTftp.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public View(Client c) {
		initialize();
		this.client = c;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmClientTftp = new JFrame();
		frmClientTftp.setResizable(false);
		frmClientTftp.setTitle("Client TFTP");
		frmClientTftp.setBounds(100, 100, 550, 300);
		frmClientTftp.getContentPane().setLayout(new GridLayout(1, 0, 0, 0));
		
		frmClientTftp.addWindowListener(new WindowAdapter() {
	            @Override
	            public void windowClosing(WindowEvent arg0) {
	                super.windowClosing(arg0);
	                client.closeSocket();
	                System.exit(0);
	            }
	        });
		
		JPanel pan = new JPanel();
		frmClientTftp.getContentPane().add(pan);
		pan.setLayout(new BorderLayout(0, 0));
		
		JPanel pan_serveur = new JPanel();
		pan.add(pan_serveur, BorderLayout.NORTH);
		pan_serveur.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JLabel lbl_adr = new JLabel("Adresse du serveur");
		pan_serveur.add(lbl_adr);
		
		txtF_adrServeur = new JTextField();
		pan_serveur.add(txtF_adrServeur);
		txtF_adrServeur.setColumns(10);
		
		JTabbedPane tab_principal = new JTabbedPane(JTabbedPane.TOP);
		pan.add(tab_principal, BorderLayout.CENTER);
		
		JPanel pan_envoyer = new JPanel();
		tab_principal.addTab("Envoyer", null, pan_envoyer, null);

		GridBagLayout gbl_pan_envoyer = new GridBagLayout();
		gbl_pan_envoyer.columnWidths = new int[]{144, 0, 83, 1, 75, 0, 1, 73, 0};
		gbl_pan_envoyer.rowHeights = new int[]{23, 0, 0, 0, 0, 0, 0, 0};
		gbl_pan_envoyer.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_pan_envoyer.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		pan_envoyer.setLayout(gbl_pan_envoyer);
		

		JLabel lbl_fichier = new JLabel("Fichier \u00E0 envoyer");
		GridBagConstraints gbc_lbl_fichier = new GridBagConstraints();
		gbc_lbl_fichier.anchor = GridBagConstraints.WEST;
		gbc_lbl_fichier.insets = new Insets(0, 0, 5, 5);
		gbc_lbl_fichier.gridx = 0;
		gbc_lbl_fichier.gridy = 0;
		pan_envoyer.add(lbl_fichier, gbc_lbl_fichier);
		
		JButton bt_parcourir = new JButton("Parcourir");
		bt_parcourir.addActionListener(new BoutonParcourirFichier());
		GridBagConstraints gbc_bt_parcourir = new GridBagConstraints();
		gbc_bt_parcourir.anchor = GridBagConstraints.NORTHWEST;
		gbc_bt_parcourir.insets = new Insets(0, 0, 5, 5);
		gbc_bt_parcourir.gridx = 2;
		gbc_bt_parcourir.gridy = 0;
		pan_envoyer.add(bt_parcourir, gbc_bt_parcourir);
		
		lbl_chE = new JLabel("");
		GridBagConstraints gbc_lbl_chE = new GridBagConstraints();
		gbc_lbl_chE.anchor = GridBagConstraints.WEST;
		gbc_lbl_chE.insets = new Insets(0, 0, 5, 5);
		gbc_lbl_chE.gridx = 3;
		gbc_lbl_chE.gridy = 0;
		pan_envoyer.add(lbl_chE, gbc_lbl_chE);
		
		JButton bt_envoyer = new JButton("Envoyer");
		bt_envoyer.addActionListener(new BoutonEnvoyer());
		GridBagConstraints gbc_bt_envoyer = new GridBagConstraints();
		gbc_bt_envoyer.insets = new Insets(0, 0, 5, 5);
		gbc_bt_envoyer.anchor = GridBagConstraints.NORTHWEST;
		gbc_bt_envoyer.gridx = 2;
		gbc_bt_envoyer.gridy = 4;
		pan_envoyer.add(bt_envoyer, gbc_bt_envoyer);
		
		JPanel pan_recevoir = new JPanel();
		tab_principal.addTab("Recevoir", null, pan_recevoir, null);
		
		GridBagLayout gbl_pan_recevoir = new GridBagLayout();
		gbl_pan_recevoir.columnWidths = new int[]{78, 82, 86, 35, 1, 75, 1, 75, 0};
		gbl_pan_recevoir.rowHeights = new int[]{23, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_pan_recevoir.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_pan_recevoir.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		pan_recevoir.setLayout(gbl_pan_recevoir);
		

		JLabel lbl_fichierRecevoir = new JLabel("Fichier \u00E0 recevoir");
		GridBagConstraints gbc_lbl_fichierRecevoir = new GridBagConstraints();
		gbc_lbl_fichierRecevoir.anchor = GridBagConstraints.WEST;
		gbc_lbl_fichierRecevoir.insets = new Insets(0, 0, 5, 5);
		gbc_lbl_fichierRecevoir.gridx = 0;
		gbc_lbl_fichierRecevoir.gridy = 0;
		pan_recevoir.add(lbl_fichierRecevoir, gbc_lbl_fichierRecevoir);
		
		txtF_fichierRecevoir = new JTextField();
		GridBagConstraints gbc_txtF_fichierRecevoir = new GridBagConstraints();
		gbc_txtF_fichierRecevoir.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtF_fichierRecevoir.anchor = GridBagConstraints.WEST;
		gbc_txtF_fichierRecevoir.insets = new Insets(0, 0, 5, 5);
		gbc_txtF_fichierRecevoir.gridx = 1;
		gbc_txtF_fichierRecevoir.gridy = 0;
		pan_recevoir.add(txtF_fichierRecevoir, gbc_txtF_fichierRecevoir);
		txtF_fichierRecevoir.setColumns(10);
		
		JButton bt_parcourirR = new JButton("Parcourir");
		bt_parcourirR.addActionListener(new BoutonParcourirDossier());
		GridBagConstraints gbc_bt_parcourirR = new GridBagConstraints();
		gbc_bt_parcourirR.anchor = GridBagConstraints.NORTHWEST;
		gbc_bt_parcourirR.insets = new Insets(0, 0, 5, 5);
		gbc_bt_parcourirR.gridx = 2;
		gbc_bt_parcourirR.gridy = 0;
		pan_recevoir.add(bt_parcourirR, gbc_bt_parcourirR);
		
		JButton bt_recevoir = new JButton("Recevoir");
		bt_recevoir.addActionListener(new BoutonRecevoir());
		
		JLabel lbl_ch = new JLabel("Chemin");
		GridBagConstraints gbc_lbl_ch = new GridBagConstraints();
		gbc_lbl_ch.anchor = GridBagConstraints.WEST;
		gbc_lbl_ch.insets = new Insets(0, 0, 5, 5);
		gbc_lbl_ch.gridx = 0;
		gbc_lbl_ch.gridy = 1;
		pan_recevoir.add(lbl_ch, gbc_lbl_ch);
		
		lbl_chR = new JLabel("");
		GridBagConstraints gbc_lbl_chR = new GridBagConstraints();
		gbc_lbl_chR.anchor = GridBagConstraints.WEST;
		gbc_lbl_chR.insets = new Insets(0, 0, 5, 5);
		gbc_lbl_chR.gridx = 1;
		gbc_lbl_chR.gridy = 1;
		pan_recevoir.add(lbl_chR, gbc_lbl_chR);
		GridBagConstraints gbc_bt_recevoir = new GridBagConstraints();
		gbc_bt_recevoir.insets = new Insets(0, 0, 5, 5);
		gbc_bt_recevoir.anchor = GridBagConstraints.NORTHWEST;
		gbc_bt_recevoir.gridx = 2;
		gbc_bt_recevoir.gridy = 6;
		pan_recevoir.add(bt_recevoir, gbc_bt_recevoir);
	}
	
    class BoutonParcourirFichier implements ActionListener {

        public void actionPerformed(ActionEvent arg0) {
            JFileChooser dialogue = new JFileChooser();

            int retour = dialogue.showOpenDialog(null);
            
            if (retour == JFileChooser.APPROVE_OPTION) {
            	fichierEnvoi = dialogue.getSelectedFile();
            	lbl_chE.setText(fichierEnvoi.getName());
            }
        }
    }
	
    class BoutonParcourirDossier implements ActionListener {

        public void actionPerformed(ActionEvent arg0) {
            JFileChooser dialogue = new JFileChooser();
            dialogue.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int retour = dialogue.showOpenDialog(null);
            
            if (retour == JFileChooser.APPROVE_OPTION) {
            	fichierReception = dialogue.getSelectedFile();
            	lbl_chR.setText(fichierReception.toString());
            }
        }
    }

    class BoutonEnvoyer implements ActionListener {

        public void actionPerformed(ActionEvent arg0) {
            if (!(fichierEnvoi == null || txtF_adrServeur.getText().equals(""))) {
                try {
                	System.out.println(txtF_adrServeur.getText());
					client.SendFile(fichierEnvoi.toString(), InetAddress.getByName(txtF_adrServeur.getText()));
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("Erreur d'adresse");
				}
            } else {
                JOptionPane.showMessageDialog(null, "Veuillez renseigner le fichier a recevoir, le dossier et l'adresse du serveur","Erreur",JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    class BoutonRecevoir implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (!(fichierReception == null || txtF_fichierRecevoir.getText().equals("") || txtF_adrServeur.getText().equals(""))) {
                try {
                    System.out.println(txtF_fichierRecevoir.getText());
                    System.out.println(fichierReception.getAbsolutePath());
					client.receiveFile(new String(fichierReception.getAbsolutePath() + "\\" + txtF_fichierRecevoir.getText()), txtF_fichierRecevoir.getText(), InetAddress.getByName(txtF_adrServeur.getText()));
				} catch (UnknownHostException e1) {
					System.out.println("Erreur d'adresse");
				}
            } else {
                JOptionPane.showMessageDialog(null,"Veuillez renseigner le fichier a recevoir, le dossier et l'adresse du serveur","Erreur",JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

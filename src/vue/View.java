package vue;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import tftp.Client;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public class View {

	private JFrame frmClientTftp;
	private JTextField txtF_adrServeur;
    private File fichierEnvoi;
    private File fichierReception;
    private Client client;
    
    private JLabel lbl_chE;
    private JLabel lbl_chR;
    private JLabel lbl_erreurE;
    private JLabel lbl_erreurR;
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
		frmClientTftp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmClientTftp.getContentPane().setLayout(new GridLayout(1, 0, 0, 0));
		
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
		pan_envoyer.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		
		JLabel lbl_fichier = new JLabel("Fichier \u00E0 envoyer");
		pan_envoyer.add(lbl_fichier, "6, 4");
		
		JButton bt_parcourir = new JButton("Parcourir");
		bt_parcourir.addActionListener(new BoutonParcourirFichier());
		
		lbl_chE = new JLabel("");
		pan_envoyer.add(lbl_chE, "10, 4");
		pan_envoyer.add(bt_parcourir, "6, 6");
		
		lbl_erreurE = new JLabel("");
		pan_envoyer.add(lbl_erreurE, "8, 12");
		
		JButton bt_envoyer = new JButton("Envoyer");
		bt_envoyer.addActionListener(new BoutonEnvoyer());
		pan_envoyer.add(bt_envoyer, "6, 16");
		
		JPanel pan_recevoir = new JPanel();
		tab_principal.addTab("Recevoir", null, pan_recevoir, null);
		pan_recevoir.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		
		JLabel lbl_fichierRecevoir = new JLabel("Fichier \u00E0 recevoir");
		pan_recevoir.add(lbl_fichierRecevoir, "6, 4");
		
		txtF_fichierRecevoir = new JTextField();
		pan_recevoir.add(txtF_fichierRecevoir, "10, 4, fill, default");
		txtF_fichierRecevoir.setColumns(10);
		
		JButton bt_recevoir = new JButton("Recevoir");
		bt_recevoir.addActionListener(new BoutonRecevoir());
		
		JLabel lbl_ch = new JLabel("Chemin");
		pan_recevoir.add(lbl_ch, "6, 8");
		
		lbl_chR = new JLabel("");
		pan_recevoir.add(lbl_chR, "10, 8");
		
		JButton bt_parcourirR = new JButton("Parcourir");
		bt_parcourirR.addActionListener(new BoutonParcourirDossier());
		pan_recevoir.add(bt_parcourirR, "6, 10");
		
		lbl_erreurR = new JLabel("");
		pan_recevoir.add(lbl_erreurR, "10, 12");
		pan_recevoir.add(bt_recevoir, "6, 16");
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
                lbl_erreurE.setText("Envoi du fichier");
                try {
					client.SendFile(fichierEnvoi.toString(), InetAddress.getByName(txtF_adrServeur.getText()));
				} catch (IOException e) {
					System.out.println("Erreur d'adresse");
				}
            } else {
                lbl_erreurE.setText("Veuillez renseigner le fichier a envoyer et l'adresse du serveur");
            }
        }
    }

    class BoutonRecevoir implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (!(fichierReception == null || txtF_fichierRecevoir.getText().equals("") || txtF_adrServeur.getText().equals(""))) {
                lbl_erreurR.setText("Reception du fichier");
                try {
					client.receiveFile(fichierReception.toString(), txtF_fichierRecevoir.toString(), InetAddress.getByName(txtF_adrServeur.getText()));
				} catch (UnknownHostException e1) {
					System.out.println("Erreur d'adresse");
				}
            } else {
                lbl_erreurR.setText("Veuillez renseigner le fichier a recevoir, le dossier et l'adresse du serveur");
            }
        }
    }
}

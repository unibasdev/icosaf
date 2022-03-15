package it.unibas.arduino.config.vista;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import it.unibas.arduino.config.Applicazione;
import it.unibas.arduino.config.Costanti;
import static it.unibas.arduino.config.util.GestoreRisorse.getLargeSVGIcon;
import java.awt.Cursor;
import java.awt.FileDialog;
import java.awt.HeadlessException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.QUESTION_MESSAGE;
import static javax.swing.JOptionPane.YES_NO_OPTION;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

public class Frame extends javax.swing.JFrame {

    static {
        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
//            UIManager.setLookAndFeel(new MaterialLookAndFeel());
            JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);
            FlatLightLaf.setup();
            UIManager.setLookAndFeel( new FlatDarkLaf() );
        } catch (Exception ex) {
        }
    } 

    public void inizializza() {
        this.setTitle("Arduino Config");
        initComponents();
        setContentPane(Applicazione.getInstance().getPannelloConfigurazioneArduino());
        initAction();
        //this.setSize(990, 550);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    }

    public void visualizza() {
        this.setVisible(true);
    }

    private void initAction() {
        this.menuLoad.setAction(Applicazione.getInstance().getControlloConfigurazioneArduino().getAzioneCaricaConfigurazione());
        this.menuSave.setAction(Applicazione.getInstance().getControlloConfigurazioneArduino().getAzioneSalvaConfigurazione());
        this.menuMonitor.setAction(Applicazione.getInstance().getControlloConfigurazioneArduino().getAzioneMonitor());
        this.menuEsci.setAction(Applicazione.getInstance().getControlloConfigurazioneArduino().getAzioneEsci());
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                Applicazione.getInstance().getControlloConfigurazioneArduino().getAzioneEsci().actionPerformed(null);
            }
        });
    }

    public void mostraMessaggioInformazioni(String messaggio) {
        JOptionPane.showMessageDialog(this, messaggio, "Informazioni", INFORMATION_MESSAGE, 
                getLargeSVGIcon(Costanti.IMG_APP));
    }

    public void mostraMessaggioErrore(String messaggio) {
        JOptionPane.showMessageDialog(this, messaggio, "Errore", ERROR_MESSAGE, 
                getLargeSVGIcon(Costanti.IMG_APP));
    }

    public void avvioCaricamento(String info) {
        JOptionPane.showMessageDialog(this, info, "LOADING", INFORMATION_MESSAGE, 
                getLargeSVGIcon(Costanti.IMG_APP));
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    public int mostraConfermaUscita() {
        return JOptionPane.showConfirmDialog(Applicazione.getInstance().getFrame(),
                "Confermi di voler uscire dall'applicazione?", "Conferma uscita", YES_NO_OPTION, QUESTION_MESSAGE, 
                getLargeSVGIcon(Costanti.IMG_APP));
    }

    public int mostraConfermaSovrascrittura() throws HeadlessException {
        int risultato = JOptionPane.showConfirmDialog(Applicazione.getInstance().getFrame(),
                "E' presente gia' un file di configurazione nella cartella selezionata. Voi sostituirlo?", "Conferma sovrascrittura",
                YES_NO_OPTION, QUESTION_MESSAGE, getLargeSVGIcon(Costanti.IMG_APP));
        return risultato;
    }

    public void fineCaricamento() {
        this.setCursor(Cursor.getDefaultCursor());
    }

    public String mostraSelezioneCartella() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Seleziona cartella configurazione");
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int scelta = fc.showOpenDialog(this);
        if (scelta != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        return fc.getSelectedFile().getPath();
    }

    public String chooseFile() {
        FileDialog fd = new FileDialog(this, "Load File", FileDialog.LOAD);
        fd.setDirectory("./");
        fd.setFile("*.txt");
        fd.setVisible(true);
        if (fd.getFile() == null) {
            return null;
        }
        return fd.getDirectory() + fd.getFile();
    }

    public String saveFile() {
        FileDialog fd = new FileDialog(this, "Load File", FileDialog.SAVE);
        fd.setDirectory("./");
        fd.setFile("*.txt");
        fd.setVisible(true);
        if (fd.getFile() == null) {
            return null;
        }
        return fd.getDirectory() + fd.getFile();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        menuLoad = new javax.swing.JMenuItem();
        menuSave = new javax.swing.JMenuItem();
        menuMonitor = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        menuEsci = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jMenu1.setText("File");

        menuLoad.setText("jMenuItem1");
        jMenu1.add(menuLoad);

        menuSave.setText("jMenuItem1");
        jMenu1.add(menuSave);

        menuMonitor.setText("jMenuItem1");
        jMenu1.add(menuMonitor);
        jMenu1.add(jSeparator1);

        menuEsci.setText("jMenuItem1");
        jMenu1.add(menuEsci);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 663, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 373, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JMenuItem menuEsci;
    private javax.swing.JMenuItem menuLoad;
    private javax.swing.JMenuItem menuMonitor;
    private javax.swing.JMenuItem menuSave;
    // End of variables declaration//GEN-END:variables

}

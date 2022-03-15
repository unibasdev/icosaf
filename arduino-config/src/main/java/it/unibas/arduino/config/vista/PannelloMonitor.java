/*
 * Copyright (C) 2021 donatello
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.unibas.arduino.config.vista;

import it.unibas.arduino.config.Applicazione;
import it.unibas.arduino.config.Costanti;
import it.unibas.arduino.config.modello.Configurazione;
import it.unibas.arduino.config.modello.Monitor;
import it.unibas.arduino.config.util.GestoreRisorse;
import static it.unibas.arduino.config.util.GestoreRisorse.getLargeSVGIcon;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import lombok.Getter;

@Getter
public class PannelloMonitor extends javax.swing.JDialog {

    public PannelloMonitor(Frame parent) {
        super(parent, true);
    }

    public void inizializza() {
        initComponents();
        icon.setIcon(GestoreRisorse.getSVGIcon(Costanti.IMG_APP, 62));
        bottoneStartStopMonitor.setAction(Applicazione.getInstance().getControlloMonitor().getAzioneStartMonitor());
        bottoneLeggi.setAction(Applicazione.getInstance().getControlloMonitor().getAzioneLeggiStato());
        bottoneModifica.setAction(Applicazione.getInstance().getControlloMonitor().getAzioneModificaStato());
        setLocationRelativeTo(getParent());
    }

    public void visualizza() {
        Configurazione ac = (Configurazione) Applicazione.getInstance().getModello().getBean(Costanti.ARDUINO_CONF);
        if (ac == null) {
            Applicazione.getInstance().getFrame().mostraMessaggioErrore("Salva o carica una configurazione per avviare il monitor");
            return;
        }
        this.labelServer.setText(ac.getMqttServer() + ":" + ac.getMqttPort());
        this.labelTopicDati.setText(ac.getDeviceID() + "_data");
        this.labelTopicComandi.setText(ac.getDeviceID() + "_command");
        Monitor monitor = (Monitor) Applicazione.getInstance().getModello().getBean(Costanti.MONITOR);
        tabellaMonitor.setModel(new ModelloTabellaMonitor(monitor));
        this.setVisible(true);
    }

    public void aggiornaDati() {
        Monitor monitor = (Monitor) Applicazione.getInstance().getModello().getBean(Costanti.MONITOR);
        if (monitor.isAttivo()) {
            bottoneStartStopMonitor.setAction(Applicazione.getInstance().getControlloMonitor().getAzioneStopMonitor());
        } else {
            bottoneStartStopMonitor.setAction(Applicazione.getInstance().getControlloMonitor().getAzioneStartMonitor());
        }
        ModelloTabellaMonitor modelloTabellaMonitor = (ModelloTabellaMonitor) tabellaMonitor.getModel();
        modelloTabellaMonitor.aggiornaDati();
    }

    public void mostraMessaggioInformazioni(String messaggio) {
        JOptionPane.showMessageDialog(this, messaggio, "Informazioni", INFORMATION_MESSAGE, 
                getLargeSVGIcon(Costanti.IMG_APP));
    }

    public void mostraMessaggioErrore(String messaggio) {
        JOptionPane.showMessageDialog(this, messaggio, "Errore", ERROR_MESSAGE, 
                getLargeSVGIcon(Costanti.IMG_APP));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.JPanel jPanel2 = new javax.swing.JPanel();
        icon = new javax.swing.JLabel();
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel3 = new javax.swing.JLabel();
        labelServer = new javax.swing.JLabel();
        labelTopicDati = new javax.swing.JLabel();
        labelTopicComandi = new javax.swing.JLabel();
        bottoneStartStopMonitor = new javax.swing.JButton();
        bottoneLeggi = new javax.swing.JButton();
        bottoneModifica = new javax.swing.JButton();
        javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
        tabellaMonitor = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Monitor");

        jPanel2.setBackground(javax.swing.UIManager.getDefaults().getColor("Button.borderColor"));

        jLabel1.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        jLabel1.setText("Server MQTT:");

        jLabel2.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        jLabel2.setText("Topic Dati:");

        jLabel3.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        jLabel3.setText("Topic Comandi:");

        labelServer.setText("jLabel4");

        labelTopicDati.setText("jLabel5");

        labelTopicComandi.setText("jLabel6");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(icon, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel3)
                        .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING))
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelServer)
                    .addComponent(labelTopicDati)
                    .addComponent(labelTopicComandi))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(icon, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(labelServer))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(labelTopicDati))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(labelTopicComandi))))
                .addContainerGap())
        );

        bottoneStartStopMonitor.setText("jButton1");

        bottoneLeggi.setText("jButton2");

        bottoneModifica.setText("jButton3");

        tabellaMonitor.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tabellaMonitor);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(bottoneStartStopMonitor, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(bottoneLeggi, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(bottoneModifica, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 182, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bottoneStartStopMonitor)
                    .addComponent(bottoneLeggi)
                    .addComponent(bottoneModifica))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 421, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bottoneLeggi;
    private javax.swing.JButton bottoneModifica;
    private javax.swing.JButton bottoneStartStopMonitor;
    private javax.swing.JLabel icon;
    private javax.swing.JLabel labelServer;
    private javax.swing.JLabel labelTopicComandi;
    private javax.swing.JLabel labelTopicDati;
    private javax.swing.JTable tabellaMonitor;
    // End of variables declaration//GEN-END:variables

}

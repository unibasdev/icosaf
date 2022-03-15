package it.unibas.arduino.config.vista;

import it.unibas.arduino.config.Applicazione;
import it.unibas.arduino.config.Costanti;
import it.unibas.arduino.config.modello.Configurazione;
import it.unibas.arduino.config.modello.Pin;
import java.util.List;
import javax.swing.JPasswordField;
import javax.swing.JSpinner;

public class PannelloConfigurazioneArduino extends javax.swing.JPanel {

    public void inizializza() {
        initComponents();
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(portaMQTT, "#");
        portaMQTT.setEditor(editor);
        initIPAddressFields();
        initComboProtectionType();
        initComboIPStrategy();
        initComboPin();
        initAction();
        refreshTablePin(null);
    }

    private void initIPAddressFields() {
        this.textDNS1.setDocument(new TextFieldLimit(3, textDNS1));
        this.textDNS2.setDocument(new TextFieldLimit(3, textDNS2));
        this.textDNS3.setDocument(new TextFieldLimit(3, textDNS3));
        this.textDNS4.setDocument(new TextFieldLimit(3, textDNS4));

        this.textIP1.setDocument(new TextFieldLimit(3, textIP1));
        this.textIP2.setDocument(new TextFieldLimit(3, textIP2));
        this.textIP3.setDocument(new TextFieldLimit(3, textIP3));
        this.textIP4.setDocument(new TextFieldLimit(3, textIP4));

        this.textGateway1.setDocument(new TextFieldLimit(3, textGateway1));
        this.textGateway2.setDocument(new TextFieldLimit(3, textGateway2));
        this.textGateway3.setDocument(new TextFieldLimit(3, textGateway3));
        this.textGateway4.setDocument(new TextFieldLimit(3, textGateway4));

        this.textSubnet1.setDocument(new TextFieldLimit(3, textSubnet1));
        this.textSubnet2.setDocument(new TextFieldLimit(3, textSubnet2));
        this.textSubnet3.setDocument(new TextFieldLimit(3, textSubnet3));
        this.textSubnet4.setDocument(new TextFieldLimit(3, textSubnet4));
    }

    @SuppressWarnings("unchecked")
    private void initComboProtectionType() {
        this.comboProtectionType.removeAllItems();
        for (String type : Costanti.TYPE_PROTECTION) {
            comboProtectionType.addItem(type);
        }
        this.hidePanelPw();
    }

    @SuppressWarnings("unchecked")
    private void initComboIPStrategy() {
        this.comboIPStrategy.removeAllItems();
        for (String strategy : Costanti.IP_STRATEGY) {
            comboIPStrategy.addItem(strategy);
        }
        this.hideIPManual();
    }

    @SuppressWarnings("unchecked")
    private void initComboPin() {
        this.comboAD.removeAllItems();
        this.comboIO.removeAllItems();
        for (String type : Costanti.ADtype) {
            this.comboAD.addItem(type);
        }
        for (String IOtype : Costanti.IOtype) {
            this.comboIO.addItem(IOtype);
        }

    }

    public void refreshTablePin(List<Pin> listPin) {
        this.tablePin.setModel(new ModelloTabellaPin(listPin));
    }

    public void aggiornaCampi() {
        Configurazione ac = (Configurazione) Applicazione.getInstance().getModello().getBean(Costanti.ARDUINO_CONF);
        if(ac == null){
            return;
        }
        if (ac.getDns() != null && !ac.getDns().isEmpty()) {
            String[] dnsSplit = ac.getDns().split("\\.");
            this.textDNS1.setText(dnsSplit[0]);
            this.textDNS2.setText(dnsSplit[1]);
            this.textDNS3.setText(dnsSplit[2]);
            this.textDNS4.setText(dnsSplit[3]);
        }
        if (ac.getGateway() != null && !ac.getGateway().isEmpty()) {
            String[] gatewaySplit = ac.getGateway().split("\\.");
            this.textGateway1.setText(gatewaySplit[0]);
            this.textGateway2.setText(gatewaySplit[1]);
            this.textGateway3.setText(gatewaySplit[2]);
            this.textGateway4.setText(gatewaySplit[3]);
        }
        if (ac.getIp() != null && !ac.getIp().isEmpty()) {
            String[] ipSplit = ac.getIp().split("\\.");
            this.textIP1.setText(ipSplit[0]);
            this.textIP2.setText(ipSplit[1]);
            this.textIP3.setText(ipSplit[2]);
            this.textIP4.setText(ipSplit[3]);
        }
        if (ac.getSubnet() != null && !ac.getSubnet().isEmpty()) {
            String[] subnetSplit = ac.getSubnet().split("\\.");
            this.textSubnet1.setText(subnetSplit[0]);
            this.textSubnet2.setText(subnetSplit[1]);
            this.textSubnet3.setText(subnetSplit[2]);
            this.textSubnet4.setText(subnetSplit[3]);
        }
        this.textIDDevice.setText(ac.getDeviceID());
        this.textSSID.setText(ac.getSSID());
        this.password.setText(ac.getPassword());
        this.textAutoReadTimer.setValue(ac.getAutoReadTimer());
        this.comboIPStrategy.getModel().setSelectedItem(Costanti.IP_STRATEGY.get(ac.getIPStrategy() - 1));
        this.comboProtectionType.getModel().setSelectedItem(Costanti.TYPE_PROTECTION.get(ac.getProtectionType()));
        this.serverMQTT.setText(ac.getMqttServer());
        this.portaMQTT.setValue(ac.getMqttPort());
        this.textDevicePassword.setText(ac.getDevicePassword());
        this.refreshTablePin(ac.getListPin());
    }

    private void initAction() {
        this.comboIPStrategy.addItemListener(Applicazione.getInstance().getControlloConfigurazioneArduino().getAzioneComboIpStrategy());
        this.comboProtectionType.addItemListener(Applicazione.getInstance().getControlloConfigurazioneArduino().getActionComboProtectionType());
        this.buttonADD.setAction(Applicazione.getInstance().getControlloConfigurazioneArduino().getActionAddPin());
        this.buttonDelete.setAction(Applicazione.getInstance().getControlloConfigurazioneArduino().getActionDeletePIN());
        this.checkShowPassword.addItemListener(Applicazione.getInstance().getControlloConfigurazioneArduino().getAzioneMostraPassword());
        this.checkShowDevicePassword.addItemListener(Applicazione.getInstance().getControlloConfigurazioneArduino().getAzioneMostraDevicePassword());
        this.comboAD.addItemListener(Applicazione.getInstance().getControlloConfigurazioneArduino().getActionComboADType());

        this.textDNS1.addFocusListener(new TextFieldFocus());
        this.textDNS2.addFocusListener(new TextFieldFocus());
        this.textDNS3.addFocusListener(new TextFieldFocus());
        this.textDNS4.addFocusListener(new TextFieldFocus());
        this.textGateway1.addFocusListener(new TextFieldFocus());
        this.textGateway2.addFocusListener(new TextFieldFocus());
        this.textGateway3.addFocusListener(new TextFieldFocus());
        this.textGateway4.addFocusListener(new TextFieldFocus());
        this.textIP1.addFocusListener(new TextFieldFocus());
        this.textIP2.addFocusListener(new TextFieldFocus());
        this.textIP3.addFocusListener(new TextFieldFocus());
        this.textIP4.addFocusListener(new TextFieldFocus());
        this.textSubnet1.addFocusListener(new TextFieldFocus());
        this.textSubnet2.addFocusListener(new TextFieldFocus());
        this.textSubnet3.addFocusListener(new TextFieldFocus());
        this.textSubnet4.addFocusListener(new TextFieldFocus());
    }

    public void visiblePanelPw() {
        this.panelPassword.setVisible(true);
    }

    public void hidePanelPw() {
        this.panelPassword.setVisible(false);
    }

    public void visibleIPManual() {
        this.panelIPStrategy.setVisible(true);
    }

    public void hideIPManual() {
        this.panelIPStrategy.setVisible(false);
    }

    public void hidePanelPSKey() {
        this.panelMode.setVisible(false);
    }

    public void visiblePanelPSKey() {
        this.panelMode.setVisible(true);
    }

    public int getSpinnerPinNumber() {
        return (Integer) spinnerPinValue.getValue();
    }

    public String getComboAD() {
        return (String) comboAD.getSelectedItem();
    }

    public String getComboIO() {
        return (String) comboIO.getSelectedItem();
    }

    public String getTextPinID() {
        return textPinID.getText();
    }

    public String getTextModello() {
        return textModello.getText();
    }

    public String getTextDescrizione() {
        return textDescrizione.getText();
    }

    public String getTextUM() {
        return textUM.getText();
    }

    public int[] getSelectedPin() {
        return this.tablePin.getSelectedRows();
    }

    public void showPassword() {
        this.password.setEchoChar((char) 0);
    }

    public void hidePassword() {
        this.password.setEchoChar(passwordField.getEchoChar());
    }

    public void showDevicePassword() {
        this.textDevicePassword.setEchoChar((char) 0);
    }

    public void hideDevicePassword() {
        this.textDevicePassword.setEchoChar(passwordField.getEchoChar());
    }

    public int getComboIPStrategy() {
        return comboIPStrategy.getSelectedIndex();
    }

    public int getComboProtectionType() {
        return comboProtectionType.getSelectedIndex();
    }

    public String getPassword() {
        return String.valueOf(password.getPassword());
    }

    public String[] getTextDNS() {
        String[] dns = {textDNS1.getText(), textDNS2.getText(), textDNS3.getText(), textDNS4.getText()};
        return dns;
    }

    public String[] getTextIP() {
        String[] ip = {textIP1.getText(), textIP2.getText(), textIP3.getText(), textIP4.getText()};
        return ip;
    }

    public String[] getTextSubnet() {
        String[] subnet = {textSubnet1.getText(), textSubnet2.getText(), textSubnet3.getText(), textSubnet4.getText()};
        return subnet;
    }

    public String[] getTextGateway() {
        String[] gateway = {textGateway1.getText(), textGateway2.getText(), textGateway3.getText(), textGateway4.getText()};
        return gateway;
    }

    public String getTextIDDevice() {
        return textIDDevice.getText();
    }

    public String getServerMQTT() {
        return serverMQTT.getText();
    }

    public String getTextSSID() {
        return textSSID.getText();
    }

    public int getPortMQTT() {
        return ((Integer) portaMQTT.getValue());
    }

    public int getAutoReadTimer() {
        return ((Integer) textAutoReadTimer.getValue());
    }

    public String getTextDevicePassword() {
        return String.valueOf(textDevicePassword.getPassword());
    }

    public void disableRadioCode() {
        this.textModello.setEnabled(false);
        this.spinnerPinValue.setEnabled(true);
        this.labeRadio.setEnabled(false);
        this.labelPinNumber.setEnabled(true);

    }

    public void enableRadioCode() {
        this.textModello.setEnabled(true);
        this.spinnerPinValue.setEnabled(false);
        this.labeRadio.setEnabled(true);
        this.labelPinNumber.setEnabled(false);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.JScrollPane jScrollPane1 = new javax.swing.JScrollPane();
        javax.swing.JPanel jPanel6 = new javax.swing.JPanel();
        javax.swing.JPanel jPanel4 = new javax.swing.JPanel();
        javax.swing.JLabel jLabel9 = new javax.swing.JLabel();
        textIDDevice = new javax.swing.JTextField();
        javax.swing.JLabel jLabel16 = new javax.swing.JLabel();
        textDevicePassword = new javax.swing.JPasswordField();
        checkShowDevicePassword = new javax.swing.JCheckBox();
        javax.swing.JLabel jLabel18 = new javax.swing.JLabel();
        textAutoReadTimer = new javax.swing.JSpinner();
        jLabel12 = new javax.swing.JLabel();
        javax.swing.JPanel jPanel7 = new javax.swing.JPanel();
        panelMode = new javax.swing.JPanel();
        javax.swing.JLabel jLabel15 = new javax.swing.JLabel();
        serverMQTT = new javax.swing.JTextField();
        portaMQTT = new javax.swing.JSpinner();
        javax.swing.JLabel jLabel17 = new javax.swing.JLabel();
        javax.swing.JPanel jPanel3 = new javax.swing.JPanel();
        textSSID = new javax.swing.JTextField();
        javax.swing.JLabel jLabel2 = new javax.swing.JLabel();
        javax.swing.JLabel jLabel1 = new javax.swing.JLabel();
        comboProtectionType = new javax.swing.JComboBox();
        panelPassword = new javax.swing.JPanel();
        javax.swing.JLabel jLabel3 = new javax.swing.JLabel();
        password = new javax.swing.JPasswordField();
        checkShowPassword = new javax.swing.JCheckBox();
        javax.swing.JPanel jPanel1 = new javax.swing.JPanel();
        javax.swing.JLabel jLabel4 = new javax.swing.JLabel();
        comboIPStrategy = new javax.swing.JComboBox();
        panelIPStrategy = new javax.swing.JPanel();
        textIP2 = new javax.swing.JTextField();
        javax.swing.JLabel jLabel8 = new javax.swing.JLabel();
        textGateway3 = new javax.swing.JTextField();
        textDNS2 = new javax.swing.JTextField();
        textGateway2 = new javax.swing.JTextField();
        javax.swing.JLabel jLabel7 = new javax.swing.JLabel();
        textSubnet1 = new javax.swing.JTextField();
        javax.swing.JLabel jLabel6 = new javax.swing.JLabel();
        textSubnet3 = new javax.swing.JTextField();
        textIP4 = new javax.swing.JTextField();
        textIP1 = new javax.swing.JTextField();
        javax.swing.JLabel jLabel5 = new javax.swing.JLabel();
        textSubnet4 = new javax.swing.JTextField();
        textIP3 = new javax.swing.JTextField();
        textDNS3 = new javax.swing.JTextField();
        textDNS4 = new javax.swing.JTextField();
        textGateway4 = new javax.swing.JTextField();
        textGateway1 = new javax.swing.JTextField();
        textSubnet2 = new javax.swing.JTextField();
        textDNS1 = new javax.swing.JTextField();
        javax.swing.JPanel jPanel2 = new javax.swing.JPanel();
        javax.swing.JLabel jLabel10 = new javax.swing.JLabel();
        textPinID = new javax.swing.JTextField();
        javax.swing.JLabel jLabel11 = new javax.swing.JLabel();
        comboAD = new javax.swing.JComboBox();
        labelPinNumber = new javax.swing.JLabel();
        javax.swing.JLabel jLabel13 = new javax.swing.JLabel();
        comboIO = new javax.swing.JComboBox();
        buttonADD = new javax.swing.JButton();
        spinnerPinValue = new javax.swing.JSpinner();
        labeRadio = new javax.swing.JLabel();
        textModello = new javax.swing.JTextField();
        textDescrizione = new javax.swing.JTextField();
        javax.swing.JLabel jLabel14 = new javax.swing.JLabel();
        textUM = new javax.swing.JTextField();
        labeRadio1 = new javax.swing.JLabel();
        javax.swing.JPanel jPanel5 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tablePin = new javax.swing.JTable();
        javax.swing.JSeparator jSeparator1 = new javax.swing.JSeparator();
        buttonDelete = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        jScrollPane1.setBorder(null);
        jScrollPane1.setMinimumSize(new java.awt.Dimension(920, 550));

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Parametri Device"));

        jLabel9.setText("Device ID");

        textIDDevice.setColumns(20);

        jLabel16.setText("Device Password");

        checkShowDevicePassword.setText("Show");

        jLabel18.setText("Lettura Automatica");

        textAutoReadTimer.setModel(new javax.swing.SpinnerNumberModel(0, 0, 3600000, 1000));

        jLabel12.setText("millisecondi (0 per disabilitare)");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jLabel9))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(textDevicePassword)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(checkShowDevicePassword, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(textIDDevice)
                        .addGap(3, 3, 3)))
                .addContainerGap())
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(textAutoReadTimer, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel12)
                .addContainerGap(68, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(textIDDevice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(textDevicePassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(checkShowDevicePassword))
                    .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textAutoReadTimer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18)
                    .addComponent(jLabel12))
                .addContainerGap())
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Parametri MQTT"));
        jPanel7.setPreferredSize(new java.awt.Dimension(396, 100));

        javax.swing.GroupLayout panelModeLayout = new javax.swing.GroupLayout(panelMode);
        panelMode.setLayout(panelModeLayout);
        panelModeLayout.setHorizontalGroup(
            panelModeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        panelModeLayout.setVerticalGroup(
            panelModeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jLabel15.setText("Server MQTT");

        serverMQTT.setColumns(20);

        portaMQTT.setModel(new javax.swing.SpinnerNumberModel(1, 1, 65535, 1));

        jLabel17.setText("Porta MQTT");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelMode, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(serverMQTT, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(portaMQTT, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(serverMQTT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(portaMQTT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17))
                .addGap(10, 10, 10)
                .addComponent(panelMode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("WiFi configuration"));
        jPanel3.setPreferredSize(new java.awt.Dimension(396, 132));

        textSSID.setColumns(20);

        jLabel2.setText("Protection Type");

        jLabel1.setText("SSID");

        comboProtectionType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel3.setText("Password");

        password.setColumns(20);

        checkShowPassword.setText("Show");

        javax.swing.GroupLayout panelPasswordLayout = new javax.swing.GroupLayout(panelPassword);
        panelPassword.setLayout(panelPasswordLayout);
        panelPasswordLayout.setHorizontalGroup(
            panelPasswordLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPasswordLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(47, 47, 47)
                .addComponent(password, javax.swing.GroupLayout.PREFERRED_SIZE, 267, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkShowPassword)
                .addContainerGap(21, Short.MAX_VALUE))
        );
        panelPasswordLayout.setVerticalGroup(
            panelPasswordLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelPasswordLayout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addGroup(panelPasswordLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(password, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(checkShowPassword))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1))
                .addGap(39, 39, 39)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(textSSID)
                    .addComponent(comboProtectionType, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addComponent(panelPassword, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(textSSID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(comboProtectionType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Connection strategy"));
        jPanel1.setPreferredSize(new java.awt.Dimension(396, 206));

        jLabel4.setText("IP strategy");

        comboIPStrategy.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        textIP2.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel8.setText("DNS");

        textGateway3.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        textDNS2.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        textGateway2.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel7.setText("Subnet");

        textSubnet1.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel6.setText("Gateway");

        textSubnet3.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        textIP4.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        textIP1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        textIP1.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));

        jLabel5.setText("IP");

        textSubnet4.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        textIP3.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        textDNS3.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        textDNS4.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        textGateway4.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        textGateway1.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        textSubnet2.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        textDNS1.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        javax.swing.GroupLayout panelIPStrategyLayout = new javax.swing.GroupLayout(panelIPStrategy);
        panelIPStrategy.setLayout(panelIPStrategyLayout);
        panelIPStrategyLayout.setHorizontalGroup(
            panelIPStrategyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelIPStrategyLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelIPStrategyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelIPStrategyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelIPStrategyLayout.createSequentialGroup()
                        .addComponent(textIP1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(textIP2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(textIP3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(textIP4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelIPStrategyLayout.createSequentialGroup()
                        .addComponent(textGateway1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(textGateway2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(textGateway3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(textGateway4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelIPStrategyLayout.createSequentialGroup()
                        .addComponent(textSubnet1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(textSubnet2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(textSubnet3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(textSubnet4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelIPStrategyLayout.createSequentialGroup()
                        .addComponent(textDNS1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(textDNS2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(textDNS3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(textDNS4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        panelIPStrategyLayout.setVerticalGroup(
            panelIPStrategyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelIPStrategyLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panelIPStrategyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(textIP1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textIP2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textIP3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textIP4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(panelIPStrategyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(textGateway1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textGateway2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textGateway3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textGateway4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelIPStrategyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(textSubnet1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textSubnet2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textSubnet3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textSubnet4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelIPStrategyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(textDNS1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textDNS2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textDNS3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textDNS4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(comboIPStrategy, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(panelIPStrategy, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(comboIPStrategy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelIPStrategy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("PINS"));

        jLabel10.setText("ID");

        jLabel11.setText("Tipo Pin");

        comboAD.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        labelPinNumber.setText("Numero Pin");

        jLabel13.setText("Modalita' Pin");

        comboIO.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        buttonADD.setText("jButton1");

        spinnerPinValue.setModel(new javax.swing.SpinnerNumberModel(0, 0, 50, 1));

        labeRadio.setText("Modello");

        jLabel14.setText("Descrizione");

        labeRadio1.setText("Unità Misura");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(labelPinNumber)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(spinnerPinValue, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(comboAD, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(textPinID))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(comboIO, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textDescrizione, javax.swing.GroupLayout.PREFERRED_SIZE, 409, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonADD, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(labeRadio, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(textModello, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(labeRadio1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textUM, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelPinNumber)
                    .addComponent(spinnerPinValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(textPinID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(comboIO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(comboAD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textUM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labeRadio1)
                    .addComponent(textModello, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labeRadio))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textDescrizione, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonADD)
                    .addComponent(jLabel14))
                .addContainerGap(9, Short.MAX_VALUE))
        );

        buttonADD.getAccessibleContext().setAccessibleName("Add Pin");

        tablePin.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane3.setViewportView(tablePin);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 338, Short.MAX_VALUE)
                .addContainerGap())
        );

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        buttonDelete.setText("jButton1");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 478, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 478, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, 478, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(3, 3, 3))
                    .addComponent(buttonDelete, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonDelete))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(4, 4, 4))
        );

        buttonDelete.getAccessibleContext().setAccessibleName("Delete Pin");

        jScrollPane1.setViewportView(jPanel6);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private JPasswordField passwordField = new JPasswordField(); //Utile per estrarre il carattere nascosto

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonADD;
    private javax.swing.JButton buttonDelete;
    private javax.swing.JCheckBox checkShowDevicePassword;
    private javax.swing.JCheckBox checkShowPassword;
    private javax.swing.JComboBox comboAD;
    private javax.swing.JComboBox comboIO;
    private javax.swing.JComboBox comboIPStrategy;
    private javax.swing.JComboBox comboProtectionType;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel labeRadio;
    private javax.swing.JLabel labeRadio1;
    private javax.swing.JLabel labelPinNumber;
    private javax.swing.JPanel panelIPStrategy;
    private javax.swing.JPanel panelMode;
    private javax.swing.JPanel panelPassword;
    private javax.swing.JPasswordField password;
    private javax.swing.JSpinner portaMQTT;
    private javax.swing.JTextField serverMQTT;
    private javax.swing.JSpinner spinnerPinValue;
    private javax.swing.JTable tablePin;
    private javax.swing.JSpinner textAutoReadTimer;
    private javax.swing.JTextField textDNS1;
    private javax.swing.JTextField textDNS2;
    private javax.swing.JTextField textDNS3;
    private javax.swing.JTextField textDNS4;
    private javax.swing.JTextField textDescrizione;
    private javax.swing.JPasswordField textDevicePassword;
    private javax.swing.JTextField textGateway1;
    private javax.swing.JTextField textGateway2;
    private javax.swing.JTextField textGateway3;
    private javax.swing.JTextField textGateway4;
    private javax.swing.JTextField textIDDevice;
    private javax.swing.JTextField textIP1;
    private javax.swing.JTextField textIP2;
    private javax.swing.JTextField textIP3;
    private javax.swing.JTextField textIP4;
    private javax.swing.JTextField textModello;
    private javax.swing.JTextField textPinID;
    private javax.swing.JTextField textSSID;
    private javax.swing.JTextField textSubnet1;
    private javax.swing.JTextField textSubnet2;
    private javax.swing.JTextField textSubnet3;
    private javax.swing.JTextField textSubnet4;
    private javax.swing.JTextField textUM;
    // End of variables declaration//GEN-END:variables

}

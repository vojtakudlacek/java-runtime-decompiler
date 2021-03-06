package org.jrd.frontend.NewConnectionFrame;

import org.jrd.frontend.MainFrame.MainFrameView;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class NewConnectionView extends JDialog{

    private JPanel mainPanel;
    private HostnamePortInputPanel hostnamePortInputPanel;
    private JPanel okCancelPanel;
    private JPanel configureOKCancelPanel;
    private JButton okButton;
    private JButton cancelButton;

    private ActionListener addButtonListener;

    public class HostnamePortInputPanel extends JPanel{

        JTextField hostnameTextField;
        JTextField portTextField;


        HostnamePortInputPanel(){

            this.hostnameTextField = new JTextField();
            this.portTextField = new JTextField();
            this.portTextField.setPreferredSize(new Dimension(90,0));

            this.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.gridx = 1;
            this.add(new JLabel("Hostname"), gbc);
            gbc.gridx = 3;
            this.add(new JLabel("Port"), gbc);
            gbc.gridx = 0;
            gbc.gridy = 1;
            this.add(Box.createHorizontalStrut(20), gbc);
            gbc.weightx = 1;
            gbc.gridx = 1;
            this.add(hostnameTextField, gbc);
            gbc.weightx = 0;
            gbc.gridx = 2;
            this.add(Box.createHorizontalStrut(20), gbc);
            gbc.gridx = 3;
            this.add(portTextField, gbc);
            gbc.gridx = 4;
            this.add(Box.createHorizontalStrut(20), gbc);
            this.setPreferredSize(new Dimension(0,120));
        }
    }

    public NewConnectionView(MainFrameView mainFrameView){

        hostnamePortInputPanel = new HostnamePortInputPanel();

        okButton = new JButton("Add");
        okButton.addActionListener(actionEvent -> {
            addButtonListener.actionPerformed(actionEvent);
        });
        okButton.setPreferredSize(new Dimension(90,30));

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(actionEvent -> dispose());
        cancelButton.setPreferredSize(new Dimension(90,30));

        okCancelPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridy = 0;
        gbc.weightx = 1;
        okCancelPanel.add(Box.createHorizontalGlue(), gbc);
        gbc.weightx = 0;
        gbc.gridx = 1;
        okCancelPanel.add(okButton, gbc);
        gbc.gridx = 2;
        okCancelPanel.add(Box.createHorizontalStrut(15), gbc);
        gbc.gridx = 3;
        okCancelPanel.add(cancelButton, gbc);
        gbc.gridx = 4;
        okCancelPanel.add(Box.createHorizontalStrut(20), gbc);

        configureOKCancelPanel = new JPanel(new GridBagLayout());
        configureOKCancelPanel.setBorder(new EtchedBorder());
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        configureOKCancelPanel.add(Box.createHorizontalGlue(), gbc);
        gbc.gridx = 1;
        configureOKCancelPanel.add(okCancelPanel, gbc);
        configureOKCancelPanel.setPreferredSize(new Dimension(0,60));


        mainPanel = new JPanel(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(hostnamePortInputPanel, gbc);
        gbc.gridy = 1;
        gbc.weighty = 1;
        mainPanel.add(Box.createVerticalGlue(),gbc);
        gbc.gridy = 2;
        gbc.weighty = 0;
        mainPanel.add(configureOKCancelPanel, gbc);


        this.setTitle("New connection");
        this.setSize(new Dimension(400,220));
        this.setMinimumSize(new Dimension(250,220));
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(mainFrameView.getMainFrame());
        this.setModalityType(ModalityType.APPLICATION_MODAL);
        this.add(mainPanel);

    }

    String getHostname(){
        return hostnamePortInputPanel.hostnameTextField.getText();
    }

    String getPortString() throws NumberFormatException{
        return hostnamePortInputPanel.portTextField.getText();
    }

    void setAddButtonListener(ActionListener addButtonListener) {
        this.addButtonListener = addButtonListener;
    }
}

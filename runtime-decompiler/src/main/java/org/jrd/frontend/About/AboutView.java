package org.jrd.frontend.About;

import org.jrd.frontend.MainFrame.MainFrameView;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AboutView extends JDialog {

    JTextArea aboutTextArea;
    JScrollPane scrollPane;

    public AboutView(MainFrameView mainFrameView){

        aboutTextArea = new JTextArea();
        scrollPane = new JScrollPane(aboutTextArea);

        InputStream in = getClass().getResourceAsStream("/ABOUT");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        reader.lines().forEach(s -> sb.append(s).append('\n'));
        aboutTextArea.setText(sb.toString());
        aboutTextArea.setEditable(false);
        aboutTextArea.setCaretPosition(0);

        this.setTitle("About");
        this.setSize(new Dimension(650,600));
        this.setMinimumSize(new Dimension(250,330));
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setModalityType(ModalityType.APPLICATION_MODAL);
        this.setLayout(new BorderLayout());
        this.add(scrollPane, BorderLayout.CENTER);
        this.setLocationRelativeTo(mainFrameView.getMainFrame());
        this.setVisible(true);
    }

}

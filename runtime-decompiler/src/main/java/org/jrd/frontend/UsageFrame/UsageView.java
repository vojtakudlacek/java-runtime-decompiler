package org.jrd.frontend.UsageFrame;

import org.jrd.frontend.MainFrame.MainFrameView;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class UsageView extends JDialog {

    JTextArea usageTextArea;
    JScrollPane scrollPane;

    public UsageView(MainFrameView mainFrameView){

        usageTextArea = new JTextArea();
        scrollPane = new JScrollPane(usageTextArea);

        InputStream in = getClass().getResourceAsStream("/USAGE");
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder sb = new StringBuilder();
        reader.lines().forEach(s -> sb.append(s).append('\n'));
        usageTextArea.setText(sb.toString());
        usageTextArea.setEditable(false);
        usageTextArea.setCaretPosition(0);

        this.setTitle("Usage");
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

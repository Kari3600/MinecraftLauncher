package com.kari3600.mc.launcher;

import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.kari3600.mc.launcher.Version.VersionType;

public class App extends JFrame {
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        new App();
    }

    public App() {
        VersionList versionList = VersionList.getInstance();
        LauncherOptions launcherOptions = LauncherOptions.getInstance();
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setPreferredSize(new Dimension(200, 150));

        //JScrollPane scrollPane = new JScrollPane()

        JComboBox<String> versionComboBox = new JComboBox<>(versionList.versions.stream().filter(v -> v.type == VersionType.release).map(v -> v.id).toArray(String[]::new));
        versionComboBox.setEditable(false);
        versionComboBox.setMaximumSize(new Dimension(200, 50));
        versionComboBox.setAlignmentX(LEFT_ALIGNMENT);
        versionComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    launcherOptions.setVersionID((String) versionComboBox.getSelectedItem());
                }
            }
        });
        versionComboBox.setSelectedItem(launcherOptions.getVersionID());

        JButton launchButton = new JButton();
        launchButton.setText("Select");
        launchButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        launcherOptions.launch();
                    }
                }).start();
            }
        });

        panel.add(versionComboBox);
        panel.add(launchButton);

        add(panel);

        setVisible(true);
    }
}

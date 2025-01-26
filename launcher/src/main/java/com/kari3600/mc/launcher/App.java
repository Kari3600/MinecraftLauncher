package com.kari3600.mc.launcher;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.Image;
import java.awt.Graphics;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JTextField;
import javax.swing.JList;
import javax.swing.Box;

// import java.awt.*;
// import javax.swing.*;

public class App extends JFrame {
    public static void main(String[] args) {
        System.out.println("Hello World!");
        new App();
    }

    public App() {
        VersionList versionList = VersionList.getInstance();
        LauncherOptions launcherOptions = LauncherOptions.getInstance();
        
        // JFrame setup
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400); // Increased height to fit player profiles
        setTitle("Minecraft Launcher");

        // Main panel with vertical BoxLayout
        JPanel panel = new JPanel() {
            private Image backgroundImage;
            {
                try {
                    backgroundImage = new ImageIcon(getClass().getResource("/images/background.jpg")).getImage();
                } catch (Exception e) {
                    System.err.println("Error loading background image: " + e.getMessage());
                }
            }
        
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel with FlowLayout to center the versionComboBox
        JPanel versionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        versionPanel.setOpaque(false);

        // Combo box for versions
        JComboBox<String> versionComboBox = new JComboBox<>(versionList.versions.stream()
            .filter(v -> v.type == Version.VersionType.release)
            .map(v -> v.id)
            .toArray(String[]::new));
        versionComboBox.setEditable(false);
        versionComboBox.setPreferredSize(new Dimension(200, 50));
        versionComboBox.setBackground(new Color(255, 255, 255));
        versionComboBox.setForeground(new Color(0, 0, 0));
        versionComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setFont(new Font("Arial", Font.PLAIN, 14));
                label.setForeground(isSelected ? new Color(255, 255, 255) : new Color(0, 0, 0));
                label.setBackground(isSelected ? new Color(200, 200, 200) : new Color(255, 255, 255));
                return label;
            }
        });        
        versionComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    launcherOptions.setVersionID((String) versionComboBox.getSelectedItem());
                }
            }
        });
        versionComboBox.setSelectedItem(launcherOptions.getVersionID());
        versionPanel.add(versionComboBox);

        // JTextField with placeholder
        JTextField textField = new JTextField(32);
        String placeholder = "nick";
        textField.setText(placeholder);
        textField.setForeground(Color.GRAY);
        textField.setMaximumSize(new Dimension(200, 25));

        // FocusListener to manage placeholder behavior
        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(Color.GRAY);
                }
            }
        });

        // Panel with FlowLayout to center the launchButton
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);

        // Create the launch button
        JButton launchButton = new JButton("Select");
        launchButton.setBackground(new Color(200, 200, 200));
        launchButton.setForeground(new Color(0, 0, 0));
        launchButton.setPreferredSize(new Dimension(200, 25)); 
        launchButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new Thread(() -> launcherOptions.launch()).start();
            }
        });
        buttonPanel.add(launchButton);

        // JPanel to hold player profiles and add them dynamically
        JPanel playerPanel = new JPanel();
        playerPanel.setLayout(new BoxLayout(playerPanel, BoxLayout.Y_AXIS)); // BoxLayout for vertical arrangement
        playerPanel.setBackground(Color.WHITE);

        // Define a default player icon (make sure the icon file exists in the resources folder)
        ImageIcon userIcon = new ImageIcon(getClass().getResource("/images/user_icon.png"));
        Image scaledImage = userIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        userIcon = new ImageIcon(scaledImage);

        // Player profile to the playerPanel
        for (PlayerProfile profile : PlayerProfile.profiles) {
            // Panel for each player's profile
            JPanel profilePanel = new JPanel();
            profilePanel.setLayout(new BorderLayout());
            profilePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            profilePanel.setBackground(new Color(245, 245, 245));
            profilePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

            // Label for the player's name
            JLabel nameLabel = new JLabel(profile.getName());
            nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
            nameLabel.setForeground(Color.BLACK);

            // Label for the player's status (online/offline)
            JLabel statusLabel = new JLabel(profile.getAccessToken());
            statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            statusLabel.setForeground(profile.getAccessToken().equalsIgnoreCase("online") 
                                        ? new Color(34, 139, 34)
                                        : new Color(178, 34, 34));

            // Label for the default player icon
            JLabel iconLabel = new JLabel(userIcon);
            iconLabel.setPreferredSize(new Dimension(55, 40));
            iconLabel.setHorizontalAlignment(SwingConstants.CENTER);

            // Panel to hold name and status together (right side)
            JPanel nameAndStatusPanel = new JPanel();
            nameAndStatusPanel.setLayout(new BoxLayout(nameAndStatusPanel, BoxLayout.Y_AXIS));
            nameAndStatusPanel.setBackground(new Color(245, 245, 245));
            nameAndStatusPanel.add(nameLabel);
            nameAndStatusPanel.add(statusLabel);

            // Icon and the name/status panel to the profile panel
            profilePanel.add(iconLabel, BorderLayout.WEST);
            profilePanel.add(nameAndStatusPanel, BorderLayout.CENTER);
            iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));

            // Hover effect to the profile panel
            profilePanel.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    profilePanel.setBackground(new Color(230, 230, 230));
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    profilePanel.setBackground(new Color(245, 245, 245));
                }
            });

            // Add the styled profile panel to the main player panel
            playerPanel.add(profilePanel);
        }

        // Wrap playerPanel in a JScrollPane to make it scrollable
        JScrollPane playerScrollPane = new JScrollPane(playerPanel);
        playerScrollPane.setPreferredSize(new Dimension(300, 200));
        playerScrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        // Create a JPanel with FlowLayout.LEFT to align the label to the left
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        labelPanel.setOpaque(false);

        // Create the player profiles label
        JLabel playerProfilesLabel = new JLabel("Player Profiles:");
        playerProfilesLabel.setForeground(new Color(255, 255, 255));
        labelPanel.add(playerProfilesLabel);

        // Create a container panel to hold label and scroll pane
        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));
        containerPanel.setPreferredSize(new Dimension(containerPanel.getPreferredSize().width, 50));
        containerPanel.setOpaque(false);

        // labelPanel (with playerProfilesLabel) at the top of containerPanel
        containerPanel.add(labelPanel);

        // playerScrollPane (with profiles) just below the labelPanel in containerPanel
        containerPanel.add(playerScrollPane);

        // All elements added to paenl
        panel.add(Box.createVerticalGlue());
        panel.add(versionPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(textField);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(buttonPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(containerPanel); // This adds the label and the scroll pane together
        panel.add(Box.createVerticalGlue());

        // Main panel to the JFrame
        add(panel);

        // Frame to visible
        setVisible(true);
    }
}

// ChatImage © 2023 ChickenSaysBak
// This code is licensed under MIT license (see LICENSE file for details).
package me.chickensaysbak.playerdatatocsv;

import com.formdev.flatlaf.IntelliJTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class AppGui {

    public static final String THEME = "/Material Darker Contrast Modified.theme.json";
    private static final int OPTION_COLUMNS = 4;
    private static final int OPTION_ROWS = 3;

    private JFrame frame;
    private JTextField pathField;
    private ArrayList<JCheckBox> checkBoxes = new ArrayList<>();
    private JButton exportButton;
    private JLabel progressRatio, progressPercentage;
    private JProgressBar progressBar;
    private int progressTotal = 0;

    public AppGui() {

        IntelliJTheme.setup(PlayerdataToCSV.class.getResourceAsStream(THEME));
        frame = new JFrame("Playerdata To CSV");
        frame.setIconImage(Toolkit.getDefaultToolkit().createImage(PlayerdataToCSV.class.getResource("/icon.png")));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.add(getFolderSelectionPanel(), BorderLayout.NORTH);
        frame.add(getOptionsPanel(), BorderLayout.CENTER);
        frame.add(getExportPanel(), BorderLayout.SOUTH);

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }

    public void initializeProgress(int total) {
        progressTotal = total;
        progressBar.setMaximum(progressTotal);
        updateProgress(0);
    }

    public void updateProgress(int progress) {

        int percentage = (int) (((double) progress / progressTotal) * 100);

        progressRatio.setText(String.format("%,d of %,d", progress, progressTotal));
        progressPercentage.setText(percentage + "%");
        progressBar.setValue(progress);

    }

    public void reset() {
        exportButton.setEnabled(true);
    }

    private JPanel getFolderSelectionPanel() {

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("1. Locate the playerdata folder"), BorderLayout.NORTH);

        File playerdataFolder = new File(PlayerdataToCSV.programFolder, "playerdata");
        File selectedFolder = playerdataFolder.exists() ? playerdataFolder : PlayerdataToCSV.programFolder;

        pathField = new JTextField(playerdataFolder.exists() ? playerdataFolder.getPath() : "", 45);
        panel.add(pathField, BorderLayout.CENTER);

        JButton folderButton = new JButton("Select Folder");

        folderButton.addActionListener(actionEvent -> {

            JFileChooser fileChooser = new JFileChooser(selectedFolder);
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            if (fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) return;
            pathField.setText(fileChooser.getSelectedFile().getPath());

        });

        panel.add(folderButton, BorderLayout.EAST);
        return panel;

    }

    private JPanel getOptionsPanel() {

        JPanel panel = new JPanel(new GridLayout(0, OPTION_COLUMNS));
        panel.setBorder(BorderFactory.createTitledBorder("2. Choose what to include"));

        EmptyBorder innerMargin = new EmptyBorder(10, 10, 10, 10),
                outterMargin = new EmptyBorder(0, 10, 0, 10);

        panel.setBorder(BorderFactory.createCompoundBorder(panel.getBorder(), innerMargin));
        panel.setBorder(BorderFactory.createCompoundBorder(outterMargin, panel.getBorder()));

        Arrays.stream(DataOption.values()).forEach(o -> checkBoxes.add(new JCheckBox(o.toString(), o.isDefault())));
        getReorderCheckboxes().forEach(panel::add);
        return panel;

    }

    private JPanel getExportPanel() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JPanel centralPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        exportButton = new JButton("Export");

        exportButton.addActionListener(actionEvent -> {

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File(PlayerdataToCSV.programFolder, "playerdata.csv"));
            fileChooser.setDialogTitle("Export CSV file");
            fileChooser.setApproveButtonText("Save");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV Files (*.csv)", "csv");
            fileChooser.setFileFilter(filter);

            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {

                Exporter exporter = new Exporter(this, pathField.getText(), fileChooser.getSelectedFile(), getCheckboxData());

                try {
                    exportButton.setEnabled(false);
                    exporter.start();
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    exportButton.setEnabled(true);
                }
            }

        });

        centralPanel.add(exportButton);
        centralPanel.add(Box.createHorizontalStrut(20));

        progressRatio = new JLabel();
        centralPanel.add(progressRatio);
        centralPanel.add(Box.createHorizontalStrut(10));

        progressBar = new JProgressBar();
        progressBar.setPreferredSize(new Dimension(300, progressBar.getPreferredSize().height));
        centralPanel.add(progressBar);
        centralPanel.add(Box.createHorizontalStrut(10));

        progressPercentage = new JLabel();
        centralPanel.add(progressPercentage);

        panel.add(new JLabel("3. Export playerdata as CSV file"), BorderLayout.NORTH);
        panel.add(centralPanel, BorderLayout.CENTER);
        return panel;

    }

    /**
     * Gets a list of all options selected by checkboxes.
     * @return list of selected DataOptions
     */
    private ArrayList<DataOption> getCheckboxData() {
        ArrayList<DataOption> result = new ArrayList<>();
        for (JCheckBox checkBox : checkBoxes) if (checkBox.isSelected()) result.add(DataOption.getByName(checkBox.getText()));
        return result;
    }

    /**
     * Reorders checkboxes so they ascend vertically instead of horizontally.
     * @return a list of reordered checkboxes
     */
    private ArrayList<JCheckBox> getReorderCheckboxes() {

        ArrayList<JCheckBox> result = new ArrayList<>();

        for (int i = 0; i < OPTION_ROWS; ++i) for (int j = 0; j+i < checkBoxes.size(); j += OPTION_ROWS) {
            JCheckBox checkBox = checkBoxes.get(j+i);
            result.add(checkBox);
        }

        return result;

    }

}

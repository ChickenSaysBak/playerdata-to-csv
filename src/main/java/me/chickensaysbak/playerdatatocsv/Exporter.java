// ChatImage © 2023 ChickenSaysBak
// This code is licensed under MIT license (see LICENSE file for details).
package me.chickensaysbak.playerdatatocsv;

import com.opencsv.CSVWriter;
import dev.dewy.nbt.Nbt;
import dev.dewy.nbt.api.Tag;
import dev.dewy.nbt.tags.collection.CompoundTag;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.nio.file.NotDirectoryException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Exporter {

    private AppGui gui;
    private File playerdataFolder;
    private File outputFile;
    private ArrayList<DataOption> options;

    public Exporter(AppGui gui, String playerdataPath, File outputFile, ArrayList<DataOption> options) {
        this.gui = gui;
        playerdataFolder = new File(playerdataPath);
        this.outputFile = outputFile;
        this.options = new ArrayList<>(options);
    }

    public void start() throws IOException {

        if (playerdataFolder == null || !playerdataFolder.exists()) throw new FileNotFoundException("The selected folder does not exist.");
        else if (!playerdataFolder.isDirectory()) throw new NotDirectoryException("Select a folder instead of a file.");

        File[] files = playerdataFolder.listFiles();
        ArrayList<File> datFiles = new ArrayList<>();

        if (files != null) datFiles.addAll(Arrays.stream(files).filter(f -> f.getName().endsWith(".dat") && f.length() > 0).toList());
        if (datFiles.isEmpty()) throw new EmptyDirectoryException("The selected folder contains no valid .dat files.");

        CSVWriter writer;

        try {
            writer = new CSVWriter(new FileWriter(outputFile));
        } catch (IOException e) {
            throw new IOException("An error occurred preventing the CSV file from being created:\n\n" + e.getMessage());
        }

        ArrayList<String> columns = new ArrayList<>(List.of("uuid"));
        columns.addAll(options.stream().map(o -> o.name().toLowerCase()).toList());

        writer.writeNext(columns.toArray(new String[0]));
        gui.initializeProgress(datFiles.size());
        Nbt nbt = new Nbt();

        new SwingWorker<Void, Integer>() {

            @Override
            protected Void doInBackground() throws IOException {

                for (int i = 0; i < datFiles.size(); ++i) {

                    File file = datFiles.get(i);
                    CompoundTag compoundTag  = nbt.fromFile(file);
                    String uuid = file.getName().split("\\.")[0];

                    ArrayList<String> data = new ArrayList<>(List.of(uuid));
                    options.forEach(o -> data.add(getValue(compoundTag, o.getPath())));
                    writer.writeNext(data.toArray(new String[0]));
                    publish(i+1);

                }

                return null;

            }

            @Override
            protected void process(List<Integer> chunks) {
                int progress = chunks.get(chunks.size() - 1);
                gui.updateProgress(progress);
            }

            @Override
            protected void done() {

                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                gui.reset();

                if (Desktop.isDesktopSupported()) {

                    Desktop desktop = Desktop.getDesktop();

                    if (desktop.isSupported(Desktop.Action.OPEN)) {

                        try {
                            desktop.open(outputFile);
                            return;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                }

                // Only shown if file could not be automatically opened.
                JOptionPane.showMessageDialog(null, "Successfully exported! CSV file saved to:\n\n" +
                        outputFile.getPath(), "Success", JOptionPane.INFORMATION_MESSAGE);

            }

        }.execute();

    }

    private String getValue(CompoundTag compoundTag, String path) {

        String[] keys = path.split(">");
        String key = keys[0];

        if (keys.length > 1 && compoundTag.containsCompound(key)) return getValue(compoundTag.getCompound(key), keys[1]);

        else if (compoundTag.containsList(key)) {
            String result = "";
            for (Tag tag : compoundTag.getList(key)) result += tag.getValue().toString() + " ";
            if (!result.isEmpty()) result = result.substring(0, result.length()-1);
            return result;
        }

        else if (compoundTag.contains(key)) return compoundTag.get(key).getValue().toString();
        return null;

    }

    public static class EmptyDirectoryException extends FileSystemException {

        public EmptyDirectoryException(String message) {
            super(message);
        }

    }

}

// ChatImage © 2023 ChickenSaysBak
// This code is licensed under MIT license (see LICENSE file for details).
package me.chickensaysbak.playerdatatocsv;

import javax.swing.*;
import java.io.File;
import java.net.URISyntaxException;

public class PlayerdataToCSV {

    public static File programFolder;

    public static void main(String[] args) {

        try {
            programFolder = new File(PlayerdataToCSV.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        SwingUtilities.invokeLater(AppGui::new);

    }

}

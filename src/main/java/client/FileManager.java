package client;

import java.io.File;

public class FileManager {

    private String localPath;

    public FileManager() {
        localPath = "src/main/resources/";
    }

    public void setLocalPath(String localPath) {this.localPath = localPath;}
    public String getLocalPath() {return localPath;}

    public void displayLocal() {
        File folder = new File(localPath);

        System.out.println("Directory contents: ");

        File[] files = folder.listFiles();

        for (int i = 0; i < files.length; i++) {
            System.out.println(files[i].getName() + " ");
        }
    }

    // Rename a file on a local server.
    // Return true if successful.
    public boolean renameFileLS(String toRename, String newName) {
        File folder = new File(localPath);
        boolean isRenamed;
        // Get a list of files.
        File [] files = folder.listFiles();

        // Search all files in the local path.
        // If a file with the name is detected, it will rename it.
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile() && (files[i].getName().equals(toRename))) {
                File renamed = new File(localPath + newName); // Place the new file in the localPath.
                isRenamed = files[i].renameTo(renamed); // Rename the file.
                System.out.print("File " + toRename + " renamed to " + newName + "!");
                return isRenamed;
            }
        }

        System.out.print("ERROR: File Not renamed!");
        return false;

    }
}

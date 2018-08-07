package client;

import java.io.File;
import java.util.Scanner;

// Class for renaming files on a local server.
public class RenameFileLocalServer {
    private String localPath;


    public RenameFileLocalServer() {
        localPath = "src/main/resources";
    }

    public void setLocalPath(String localPath) {this.localPath = localPath;}
    public String getLocalPath() {return localPath;}

    // Function to rename a file given a filename.
    public boolean renameFile(String toRename) {
        Scanner input = new Scanner(System.in);
        File folder = new File(localPath);
        String temp = null;

        // Ask user for file to rename.
        System.out.println("Please enter the file that you wish to rename:");
        toRename = input.next();
        input.nextLine();

        File [] files  = folder.listFiles();

        // Check if the file exists.
        // Rename if it does exist.
        for(int i = 0; i < files.length; i++) {
            if(files[i].getName().equals(toRename)) {
                File newFile = new File(toRename);

            }

        }
        return true;
    }
}

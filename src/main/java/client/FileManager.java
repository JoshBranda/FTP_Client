package client;

import java.io.File;
import java.util.Scanner;

public class FileManager {

    private String localPath;

    public FileManager() {
        localPath = "src/main/resources/";
    }

    public void setLocalPath(String localPath) {this.localPath = localPath;}
    public String getLocalPath() {return localPath;}

    public void displayLocal() {
        File folder = new File(localPath);

        System.out.print("Directory contents: ");

        File[] files = folder.listFiles();

        for (int i = 0; i < files.length; i++) {
            System.out.print(files[i].getName() + " ");
        }
    }

    // Rename a file on a local server.
    public boolean renameFileLS(String toRename, String newName) {
        Scanner input = new Scanner(System.in);
        File folder = new File(localPath);

        // Get the name of the original file from the user.
        System.out.println("Please enter the name of the file you wish to rename:");
        toRename = input.next();
        input.nextLine();

        // Check if the file exists.
        File [] files = folder.listFiles();
        for(int i = 0; i < files.length; i++) {
            if (!(files[i].getName().equals(toRename))) {
                System.out.println("Error: There is no such file called " + toRename);
                break;
            }
            else {
                // Get the new file name from the user.
                System.out.println("Please enter a new name for the file:");
                newName = input.next();
                input.nextLine();

                File renamed = new File(newName);

                files[i].renameTo(renamed);

                return true;
            }
        }

        return false;
    }
}

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

        System.out.print("Directory contents: ");

        File[] files = folder.listFiles();

        for (int i = 0; i < files.length; i++) {
            System.out.print(files[i].getName() + " ");
        }
    }
}

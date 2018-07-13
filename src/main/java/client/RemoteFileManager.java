package client;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RemoteFileManager {

    private FTPClient ftp;


    public RemoteFileManager(FTPClient ftp)
    {
        this.ftp = ftp;
    }

    public void displayFiles()
    {
        try {
            List<String> files = getFiles();
            files.stream().forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void displayDirectories()
    {
        try {
            List<String> files = getDirectories();
            files.stream().forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getFiles() throws IOException {
            FTPFile[] files  = ftp.listFiles();
            return Arrays.stream(files).filter(f->f.isFile())
                    .map(FTPFile::getName)
                    .collect(Collectors.toList());
    }

    public List<String> getDirectories() throws IOException {
        FTPFile[] files  = ftp.listDirectories();
        return Arrays.stream(files)
                .map(FTPFile::getName)
                .collect(Collectors.toList());
    }
}

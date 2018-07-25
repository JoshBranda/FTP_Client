package client;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
        try
        {
            List<String> files = getFiles();
            files.stream().forEach(System.out::println);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void displayDirectories()
    {
        try
        {
            List<String> files = getDirectories();
            files.stream().forEach(System.out::println);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public List<String> getFiles() throws IOException
    {
            FTPFile[] files  = ftp.listFiles();
            return Arrays.stream(files).filter(f->f.isFile())
                    .map(FTPFile::getName)
                    .collect(Collectors.toList());
    }

    public List<String> getDirectories() throws IOException
    {
        FTPFile[] files  = ftp.listDirectories();
        return Arrays.stream(files)
                .map(FTPFile::getName)
                .collect(Collectors.toList());
    }


    public boolean renameFile(String from, String to)
    {
        try {
            return ftp.rename(from, to);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Returns true if successfully completed, otherwise false
    // IOException also catches FTPConnectionClosedException (if FTP connection closes unexpectedly)
    public boolean removeFile(String pathname) {
        try {
            return ftp.deleteFile(pathname);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean uploadFile(File fileToUpload, String destPath){
        try
        {
             return ftp.storeFile(destPath, new FileInputStream(fileToUpload));
        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex);
        }
    }

}

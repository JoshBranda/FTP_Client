package client;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.*;
import java.util.ArrayList;
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

    //Safe, non recursive remove directory
    //Returns true if successful, otherwise false
    //Fails on nonempty directories
    public boolean removeDirectory(String pathname) {
        try {
            FTPFile[] before = ftp.listDirectories();
            boolean holder = ftp.removeDirectory(pathname);
            FTPFile[] after = ftp.listDirectories();
            return holder;
            //return ftp.removeDirectory(pathname);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    //A false here likely means that the expected files were modified by another ftp session
    public boolean removeDirectoryRecursive(String pathname){
        try {
           FTPFile[] files = ftp.listFiles(pathname);
           boolean result;
           for ( FTPFile f : files) {
               if (f.isDirectory()) {
                   result = removeDirectoryRecursive(pathname + "/" + f.getName());
                   if (result == false) {
                       return false;
                   }
               } else {
                   result = removeFile(pathname + "/" + f.getName());
                   if (result == false) {
                       return false;
                   }
               }
           }
            //Everything should be gone, delete parent
            return removeDirectory(pathname);
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


    public boolean makeDirectory(String pathname){
        try {
            return ftp.makeDirectory(pathname);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;

    }
    public boolean downloadFile(String sourcePath, String destPath)
    {
        File downloadedFile = new File(destPath);
        try
        {
            OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(downloadedFile));
            boolean success = ftp.retrieveFile(sourcePath, outputStream);
            if(!success)
            {
                downloadedFile.delete();
            }
            outputStream.close();
            return success;
        }
        catch (IOException ex)
        {
            downloadedFile.delete();
            return false;
        }

    }

    public List<String> downloadMultipleFiles(String sourceFolder, List<String> fileNames, String destFolder) {

        List<String> failedTransfers = new ArrayList<>();

        boolean result = true;

        if (fileNames == null || fileNames.size() == 0) {
            failedTransfers.add("No files specified");
            return failedTransfers;
        }

        if (sourceFolder == null) {
            failedTransfers.add("Invalid source folder");
            return failedTransfers;
        }

        if (destFolder == null) {
            failedTransfers.add("Invalid destination folder");
            return failedTransfers;
        }

        for (String fileName : fileNames) {
            if (!downloadFile(sourceFolder + fileName, destFolder + fileName)) {
                failedTransfers.add(fileName);
            }
        }

        if (failedTransfers.size() == 0) {
            return null;
        }

        return failedTransfers;
    }

    public boolean uploadMultipleFiles (List<File> filesToUpload, String destFolder) {


        boolean result = true;

        if (filesToUpload == null || filesToUpload.size() == 0) {
            return false;
        }

        for (File file : filesToUpload) {

            if (uploadFile(file, destFolder + file.getName()) == false) {
                result = false;
            }
        }

        return result;
    }
}



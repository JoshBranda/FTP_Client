package client;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RemoteFileManagerTest {

    private FakeFtpServer fakeFtpServer;

    private FTPClient ftp;

    private RemoteFileManager remoteFileManager;
    private  ByteArrayOutputStream outContent;
    private  ByteArrayOutputStream errContent;
    private static final PrintStream originalOut = System.out;
    private static final PrintStream originalErr = System.err;

    @BeforeEach
    public void setup() throws IOException {
        fakeFtpServer = new FakeFtpServer();
        fakeFtpServer.addUserAccount(new UserAccount("user", "password", "/data"));

        FileSystem fileSystem = new UnixFakeFileSystem();
        //Directory & file create to test display functions
        fileSystem.add(new DirectoryEntry("/data"));
        fileSystem.add(new DirectoryEntry("/data/foobar"));
        fileSystem.add(new FileEntry("/data/foobar.txt", "abcdef 1234567890"));
        //Directory & file created to test remove functions
        fileSystem.add(new DirectoryEntry("/remove"));
        fileSystem.add(new FileEntry("/remove/potato.txt", "abcdef 1234567890"));
        fakeFtpServer.setFileSystem(fileSystem);
        fakeFtpServer.setServerControlPort(0);

        fakeFtpServer.start();

        ftp = new FTPClient();
        ftp.connect("localhost", fakeFtpServer.getServerControlPort());

        int reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply))
        {
            ftp.disconnect();
            throw new IOException("Exception in connecting to FTP Server");
        }
        ftp.login("user", "password");
        remoteFileManager = new RemoteFileManager(ftp);

        outContent = new ByteArrayOutputStream();
        errContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }


    @Test
    public void listRemoteFiles()
    {
        remoteFileManager.displayFiles();
        assertEquals("foobar.txt", outContent.toString().trim());
    }

    @Test
    public void listRemoteDirectories()
    {
        remoteFileManager.displayDirectories();
        assertEquals("foobar", outContent.toString().trim());
    }

    @Test
    public void removeFileNotInFilesystem()
    {
        assertFalse(remoteFileManager.removeFile("/remove/pizza_party.txt"));
    }

    @Test
    public void removeFileInFilesystem()
    {
        assertTrue(remoteFileManager.removeFile("/remove/potato.txt"));
    }

    @Test
    public void uploadFileToRemoteServer()
    {
        File toUpload = new File(getClass().getClassLoader().getResource("test.txt").getFile());
        assertTrue(remoteFileManager.uploadFile(toUpload, "test.txt" ));
        remoteFileManager.displayFiles();
        assertTrue(outContent.toString().contains("test.txt"));
    }

    @Test
    public void uploadFileToInvalidDestination()
    {
        File toUpload = new File(getClass().getClassLoader().getResource("test.txt").getFile());
        assertFalse(remoteFileManager.uploadFile(toUpload, "/invalid/test.txt" ));
        remoteFileManager.displayFiles();
        assertFalse(outContent.toString().contains("test.txt"));
    }

    @Test
    public void uploadFileWithinDirectoryToRemoteServer()
    {
        File toUpload = new File(getClass().getClassLoader().getResource("testFolder/insideTestFolder.txt").getFile());
        assertTrue(remoteFileManager.uploadFile(toUpload, "test.txt" ));
        remoteFileManager.displayFiles();
        assertTrue(outContent.toString().contains("test.txt"));
    }

    @Test
    public void uploadMultipleFilesToRemoteServer()
    {
        File toUpload = new File(getClass().getClassLoader().getResource("test.txt").getFile());
        File toUpload2 = new File(getClass().getClassLoader().getResource("joshTest.txt").getFile());
        List<File> files = new ArrayList<>();
        files.add(toUpload);
        files.add(toUpload2);
        String destFolder = "/data/";

        assertTrue(remoteFileManager.uploadMultipleFiles(files, destFolder));
        remoteFileManager.displayFiles();
        assertTrue(outContent.toString().contains("test.txt"));
        assertTrue(outContent.toString().contains("joshTest.txt"));
    }

    @Test
    public void uploadNullToRemoteServer()
    {
        List<File> files = null;
        String destFolder = "";

        assertFalse(remoteFileManager.uploadMultipleFiles(files, destFolder));
        remoteFileManager.displayFiles();
        assertFalse(outContent.toString().contains("test.txt"));
    }

    @Test
    public void uploadEmptyListOfFilesToRemoteServer()
    {
        List<File> files = new ArrayList<>();
        String destFolder = "";

        assertFalse(remoteFileManager.uploadMultipleFiles(files, destFolder));
        remoteFileManager.displayFiles();
        assertFalse(outContent.toString().contains("test.txt"));
    }

    @AfterEach
    public void teardown() throws IOException {
        ftp.disconnect();
        fakeFtpServer.stop();
        System.setOut(originalOut);
        System.setErr(originalErr);
    }
}

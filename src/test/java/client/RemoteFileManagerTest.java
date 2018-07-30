package client;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
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
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class RemoteFileManagerTest {

    private FakeFtpServer fakeFtpServer;

    private FtpConnection conn;
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
        fileSystem.add(new DirectoryEntry("/empty"));
        fileSystem.add(new DirectoryEntry("/deep"));
        fileSystem.add(new DirectoryEntry("/deep/deep2"));
        fileSystem.add(new FileEntry("/deep/deep.txt"));
        fileSystem.add(new FileEntry("/deep/deep2/deep2.txt"));
        fakeFtpServer.setFileSystem(fileSystem);
        fakeFtpServer.setServerControlPort(0);

        fakeFtpServer.start();

        conn = new client.FtpConnection();
        conn.connect("localhost", fakeFtpServer.getServerControlPort());
        conn.login("user", "password");
        ftp = conn.getConnection();
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
    public void renameValidRemote()
    {
        assertTrue(remoteFileManager.renameFile("foobar.txt", "baz.txt"));
        try {
            FTPFile[] ftpFiles = ftp.listFiles();
            //There should be a baz.txt
            assertTrue(Arrays.stream(ftpFiles).anyMatch(f->f.getName().equals("baz.txt")));
            //There should be no foobar.txt
            assertFalse(Arrays.stream(ftpFiles).anyMatch(f->f.getName().equals("foobar.txt")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void renameInvalidRemote() {
        assertFalse(remoteFileManager.renameFile("notpresent.txt", "baz.txt"));
        try {
            FTPFile[] ftpFiles = ftp.listFiles();
            //There should be no notpresent.txt
            assertFalse(Arrays.stream(ftpFiles).anyMatch(f -> f.getName().equals("notpresent.txt")));
            //There should be no baz.txt
            assertFalse(Arrays.stream(ftpFiles).anyMatch(f -> f.getName().equals("baz.txt")));
            //There should be a foobar.txt
            assertTrue(Arrays.stream(ftpFiles).anyMatch(f -> f.getName().equals("foobar.txt")));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    public void removeEmptyDir(){
        assertTrue(remoteFileManager.removeDirectory("/empty"));
        assertFalse(remoteFileManager.removeDirectory("/empty"));
    }

    @Test
    public void removeNonEmptyDir(){
        assertFalse(remoteFileManager.removeDirectory("/remove"));
    }

    @Test
    public void removeBadPathDir(){
        assertFalse(remoteFileManager.removeDirectory("badpath"));
    }

    @Test
    public void removeDirLeavesFilesAlone(){
        try {
            ftp.changeWorkingDirectory("/remove/");
            FTPFile[] ftpFiles = ftp.listFiles();
            assertTrue(Arrays.stream(ftpFiles).anyMatch(f -> f.getName().equals("potato.txt")));
            assertFalse(remoteFileManager.removeDirectory("/remove/potato.txt"));
            assertTrue(Arrays.stream(ftpFiles).anyMatch(f -> f.getName().equals("potato.txt")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void removeRecursiveRemovesDeep(){
        try {
            ftp.changeToParentDirectory();
            FTPFile[] ftpFiles = ftp.listFiles();
            assertEquals(1, Arrays.stream(ftpFiles).filter(f -> f.getName().contains("deep")).count());
            assertTrue(remoteFileManager.removeDirectoryRecursive("/deep"));
            ftpFiles = ftp.listFiles();
            assertEquals(0, Arrays.stream(ftpFiles).filter(f -> f.getName().contains("deep")).count());
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Test
    public void removeRecursiveOnlyRemovesOne(){
        try {
            ftp.changeToParentDirectory();
            FTPFile[] ftpFiles = ftp.listFiles();
            int size = ftpFiles.length;
            assertTrue(remoteFileManager.removeDirectoryRecursive("/deep"));
            assertEquals(size-1, ftp.listFiles().length);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Test
    public void removeRecursiveFailsInvalidPath(){
        assertFalse(remoteFileManager.removeDirectoryRecursive("/donotexist"));
    }

    @Test
    public void removeRecursiveFailsOnFile(){
        assertFalse(remoteFileManager.removeDirectoryRecursive("/remove/potato.txt"));
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
    public void makeValidDirectoryParallelToHomeDirectory()
    {
        assertTrue(remoteFileManager.makeDirectory("/create"));

        try {
            ftp.changeToParentDirectory();
        } catch (IOException e) {
            e.printStackTrace();
        }

        remoteFileManager.displayDirectories();
        assertTrue(outContent.toString().contains("create"));
    }

    @Test
    public void makeValidDirectoryAbsolutePath()
    {
        assertTrue(remoteFileManager.makeDirectory("/data/foobar/absolute"));
        try {
            ftp.changeWorkingDirectory("/data/foobar");
        } catch (IOException e) {
            e.printStackTrace();
        }
        remoteFileManager.displayDirectories();
        assertTrue(outContent.toString().contains("absolute"));
    }

    @Test
    public void makeValidDirectoryRelativePath()
    {
        try {
            ftp.changeWorkingDirectory("/data/foobar");
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue(remoteFileManager.makeDirectory("./relative"));
        remoteFileManager.displayDirectories();
        assertTrue(outContent.toString().contains("relative"));
    }

    @Test
    public void makeInvalidDirectoryWithBadPath()
    {
        assertFalse(remoteFileManager.makeDirectory("/data/bad_path/create"));
    }

    @AfterEach
    public void teardown() throws IOException {
        conn.disconnect();
        fakeFtpServer.stop();
        System.setOut(originalOut);
        System.setErr(originalErr);
    }
}

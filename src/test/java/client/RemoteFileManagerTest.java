package client;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RemoteFileManagerTest {

    private FakeFtpServer fakeFtpServer;

    private FTPClient ftp;

    private RemoteFileManager remoteFileManager;
    private static final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private static final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private static final PrintStream originalOut = System.out;
    private static final PrintStream originalErr = System.err;

    @Before
    public void setup() throws IOException {
        fakeFtpServer = new FakeFtpServer();
        fakeFtpServer.addUserAccount(new UserAccount("user", "password", "/data"));

        FileSystem fileSystem = new UnixFakeFileSystem();
        fileSystem.add(new DirectoryEntry("/data"));
        fileSystem.add(new DirectoryEntry("/data/foobar"));
        fileSystem.add(new FileEntry("/data/foobar.txt", "abcdef 1234567890"));
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

    @After
    public void teardown() throws IOException {
        ftp.disconnect();
        fakeFtpServer.stop();
        System.setOut(originalOut);
        System.setErr(originalErr);
    }
}

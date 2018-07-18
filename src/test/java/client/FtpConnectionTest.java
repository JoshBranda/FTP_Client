package client;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;
import org.mockftpserver.fake.filesystem.FileSystem;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.commons.net.ftp.FTPClient;

public class FtpConnectionTest {
	
    private FakeFtpServer fakeFtpServer;
    
    @BeforeEach
    public void setup() {
        fakeFtpServer = new FakeFtpServer();
        fakeFtpServer.addUserAccount(new UserAccount("user", "password", "/data"));

        FileSystem fileSystem = new UnixFakeFileSystem();
        fakeFtpServer.setFileSystem(fileSystem);
        fakeFtpServer.setServerControlPort(8463);

        fakeFtpServer.start();
    }
    
    @Test
    public void connect() {
    	FtpConnection conn = new client.FtpConnection();
    	FTPClient ftp = conn.connect("peedtest.tele2.net");
    	
    	assertEquals(true, ftp.isConnected());
    	
    	FtpConnection conn_w_port = new client.FtpConnection();
    	FTPClient ftp_w_port = conn_w_port.connect("peedtest.tele2.net",21);
    	
    	assertEquals(true, ftp_w_port.isConnected());
    }
    
    @Test
    public void getConnection() {
    	FtpConnection conn = new client.FtpConnection();
        conn.connect("localhost", fakeFtpServer.getServerControlPort());
    	FTPClient ftp = conn.getConnection();
    	
    	assertEquals(true,ftp.isAvailable());
    }
    
    @Test
    public void disconnect() {
    	FtpConnection conn = new client.FtpConnection();
        conn.connect("localhost", fakeFtpServer.getServerControlPort());
        conn.disconnect();
    	FTPClient ftp = conn.getConnection();
    	
    	assertEquals(false,ftp.isConnected());
    }
    
    @Test
    public void login() {
    	FtpConnection conn = new client.FtpConnection();
        conn.connect("peedtest.tele2.net");

        assertEquals(true,conn.login("anonymous", "anonymous"));
    }
    
    @AfterEach
    public void tearDown() {
    	fakeFtpServer.stop();
    }
}

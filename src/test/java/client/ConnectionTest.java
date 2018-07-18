package client;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.DirectoryEntry;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.apache.commons.net.ftp.FTPSClient;

public class ConnectionTest {
	
    private FakeFtpServer fakeFtpServer;
    
    @BeforeEach
    public void setup() {
        fakeFtpServer = new FakeFtpServer();
        fakeFtpServer.addUserAccount(new UserAccount("user", "password", "/data"));

        FileSystem fileSystem = new UnixFakeFileSystem();
        fileSystem.add(new DirectoryEntry("/data"));
        fakeFtpServer.setFileSystem(fileSystem);
        fakeFtpServer.setServerControlPort(0);

        fakeFtpServer.start();
    }
    
    @Test
    public void connect() {
    	Connection conn = new client.Connection();
    	
    	assertFalse(conn.isConnected());
    	
    	// Test valid host
    	FTPSClient ftp = conn.connect("localhost", fakeFtpServer.getServerControlPort());
    	
    	assertEquals("TLS", ftp.getAuthValue());
    	assertEquals(502, ftp.getReplyCode());
    	assertTrue(ftp.isConnected());
    	
    	conn.disconnect();
    	
    	// Test invalid host
    	ftp = conn.connect("invalidHost", fakeFtpServer.getServerControlPort());
    	
    	assertFalse(ftp.isConnected());

    }

    @Test
    public void disconnect() {
    	Connection conn = new client.Connection();
        conn.connect("localhost", fakeFtpServer.getServerControlPort());
    	FTPSClient ftp = conn.getConnection();
    	
        assertTrue(ftp.isConnected());
        
        conn.disconnect();
    	
    	assertFalse(ftp.isConnected());
    }
    
    @Test
    public void getConnection() {
    	Connection conn = new client.Connection();
    	conn.connect("localhost", fakeFtpServer.getServerControlPort());
    	
    	FTPSClient ftp = conn.getConnection();
    	
    	assertEquals("TLS",ftp.getAuthValue());
    	assertTrue(ftp.isAvailable());
    }
    
    @Test
    public void isConnected() {
    	Connection conn = new client.Connection();
    	
    	assertFalse(conn.isConnected());
    	
        conn.connect("localhost", fakeFtpServer.getServerControlPort());
        
    	assertTrue(conn.isConnected());
    }
    
    @Test
    public void login() {
    	Connection conn = new client.Connection();
    	conn.connect("localhost", fakeFtpServer.getServerControlPort());
        FTPSClient ftp = conn.getConnection();
        int reply;
        
        // Test valid username and password
        conn.login("user", "password");
        ftp = conn.getConnection();
        reply = ftp.getReplyCode();
        
        assertEquals(230,reply); // FTP status code: user logged in
        
    	// Test invalid username and password
        conn.login("fakeuser", "fakepassword");
        reply = ftp.getReplyCode();
        
        assertEquals(530,reply); // FTP error code: User not logged in

        // Test valid username and invalid password
        conn.login("user", "fakepassword");
        ftp = conn.getConnection();
        reply = ftp.getReplyCode();
        
        assertEquals(530,reply); // FTP error code: User not logged in
        
        // Test invalid username and valid password
        conn.login("fakeuser", "password");
        ftp = conn.getConnection();
        reply = ftp.getReplyCode();
        
        assertEquals(530,reply); // FTP error code: User not logged in  
        
        // Test invalid username and valid password
        conn.login("fakeuser", "");
        ftp = conn.getConnection();
        reply = ftp.getReplyCode();
        
        assertEquals(501,reply); // FTP error code: syntax error for parameters
        
        // Test invalid username and valid password
        conn.login("", "fakepassword");
        ftp = conn.getConnection();
        reply = ftp.getReplyCode();
        
        assertEquals(501,reply); // FTP error code: syntax error for parameters   
    }

    @AfterEach
    public void tearDown() {
    	fakeFtpServer.stop();
    }
}
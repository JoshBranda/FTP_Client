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

import java.io.*;
import java.nio.file.*;

import org.apache.commons.net.ftp.FTPClient;

public class FtpConnectionTest {
	
    private FakeFtpServer fakeFtpServer;
    private static final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private static final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private static final PrintStream originalOut = System.out;
    private static final PrintStream originalErr = System.err;
    
    private String test_config_file = "src/test/resources/testFolder/client_config.yaml";
    
    @BeforeEach
    public void setup() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
        
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
    	FtpConnection conn = new client.FtpConnection();
    	FTPClient ftp = conn.getConnection();
    	
    	assertFalse(ftp.isConnected());
    	
    	ftp = conn.connect("localhost", fakeFtpServer.getServerControlPort());
    	assertTrue(ftp.isConnected());
    	
    	conn.disconnect();
    	
    	// Test invalid host
    	ftp = conn.connect("invalidHost", fakeFtpServer.getServerControlPort());
    	
    	assertFalse(ftp.isConnected());
    }
    
    @Test
    public void disconnect() {
    	FtpConnection conn = new client.FtpConnection();
        conn.connect("localhost", fakeFtpServer.getServerControlPort());
    	FTPClient ftp = conn.getConnection();
    	
        assertTrue(ftp.isConnected());
        
        conn.disconnect();
    	
    	assertFalse(ftp.isConnected());
    }
    
    @Test
    public void getConnection() {
    	FtpConnection conn = new client.FtpConnection();
        conn.connect("localhost", fakeFtpServer.getServerControlPort());
    	FTPClient ftp = conn.getConnection();
    	
    	assertTrue(ftp.isAvailable());
    }
    
    @Test
    public void isConnected() {
    	FtpConnection conn = new client.FtpConnection();
    	
    	assertFalse(conn.isConnected());
    	
        conn.connect("localhost", fakeFtpServer.getServerControlPort());
        
    	assertTrue(conn.isConnected());
    }

    @Test
    public void loginSavedAnonymous() throws IOException {
    	FtpConnection conn = new client.FtpConnection(this.test_config_file);
    	int reply;

    	// Test use saved connection login as anonymous user
    	conn.saveConnection("test1", "localhost:"+fakeFtpServer.getServerControlPort());
		assertFalse(conn.loginSaved("test1"));
		assertTrue(conn.isConnected());
		reply = conn.getConnection().getReplyCode();

        assertEquals(530,reply); // FTP status code: user logged in as anonymous
		conn.disconnect();
		conn.logout();
    }

    @Test
    public void loginSavedValidUser() throws IOException {
    	FtpConnection conn = new client.FtpConnection(this.test_config_file);
    	int reply;

    	// Test use saved connection login as user
    	conn.saveConnection("test2", "localhost:"+fakeFtpServer.getServerControlPort()+":user:password");
		assertTrue(conn.loginSaved("test2"));
		assertTrue(conn.isConnected());
		reply = conn.getConnection().getReplyCode();

        assertEquals(230,reply); // FTP status code: user logged in
		conn.disconnect();
		conn.logout();
    }

    @Test
    public void loginSavedInvalidUser() throws IOException {
    	FtpConnection conn = new client.FtpConnection(this.test_config_file);
    	// Test no saved connection found exception
		conn.saveConnection("test", "localhost:"+fakeFtpServer.getServerControlPort()+":user:password");
		assertFalse(conn.loginSaved("test3"));
		assertEquals("Connection test3 doesn't exist...\n",outContent.toString());
		assertFalse(conn.isConnected());
    }
    @Test
    public void login() {
    	FtpConnection conn = new client.FtpConnection();
    	conn.connect("localhost", fakeFtpServer.getServerControlPort());
        FTPClient ftp = conn.getConnection();
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
    
    @Test
    public void getInfo() {
    	int port = fakeFtpServer.getServerControlPort();
    	FtpConnection conn = new client.FtpConnection();
    	conn.connect("localhost", port);
    	conn.login("fakeuser", "fakepass");
    	
    	assertEquals(String.format("localhost:%d:fakeuser:fakepass", port), conn.getInfo());
    }
    
    @Test
    public void saveConnection() {
    	FtpConnection conn = new client.FtpConnection(this.test_config_file);

    	assertTrue(conn.saveConnection("test1", "fakehost1:1"));
    	assertTrue(conn.saveConnection("test2", "fakehost2:2"));
    	assertTrue(conn.saveConnection("test3", "fakehost3:3"));

    	// making sure the output config file is 
    	// matching expected content
    	String expected_str = "test1:\n" + 
				"  port: 1\n" + 
				"  host: fakehost1\n" + 
				"test2:\n" + 
				"  port: 2\n" + 
				"  host: fakehost2\n" + 
				"test3:\n" + 
				"  port: 3\n" + 
				"  host: fakehost3\n";
    	try {
			Path file_path = Paths.get(this.test_config_file);
			byte[] config_content = Files.readAllBytes(file_path);
			String config_str = new String(config_content);

			assertEquals(expected_str, config_str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @Test
    public void saveConnectionWithLogin() {
    	FtpConnection conn = new client.FtpConnection(this.test_config_file);

    	assertTrue(conn.saveConnection("test1", "fakehost1:1:fakeuser1:pass1"));
    	assertTrue(conn.saveConnection("test2", "fakehost2:2:fakeuser2"));
    	assertTrue(conn.saveConnection("test3", "fakehost3:3:fakeuser3:pass3"));

    	// making sure the output config file is 
    	// matching expected content
    	String expected_str = "test1:\n" + 
				"  password: pass1\n" + 
				"  port: 1\n" + 
				"  host: fakehost1\n" + 
				"  username: fakeuser1\n" + 
				"test2:\n" + 
				"  port: 2\n" + 
				"  host: fakehost2\n" + 
				"  username: fakeuser2\n" + 
				"test3:\n" + 
				"  password: pass3\n" + 
				"  port: 3\n" + 
				"  host: fakehost3\n" + 
				"  username: fakeuser3\n";
    	try {
			Path file_path = Paths.get(test_config_file);
			byte[] config_content = Files.readAllBytes(file_path);
			String config_str = new String(config_content);

			assertEquals(expected_str, config_str);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @Test
    public void logout(){
        FtpConnection conn = new client.FtpConnection();
        conn.connect("localhost", fakeFtpServer.getServerControlPort());
        FTPClient ftp = conn.getConnection();
        int reply;

        conn.login("user", "password");
        reply = ftp.getReplyCode();

        assertEquals(230,reply); // FTP status code: user logged in

        assertTrue(conn.logout());

    }

    @AfterEach
    public void tearDown() {
    	fakeFtpServer.stop();
    	// cleanup config after
    	Path file_path = Paths.get(this.test_config_file);
    	if (Files.exists(file_path)) {
	    	try {
				Files.delete(file_path);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
        outContent.reset();
        System.setOut(originalOut);
        System.setErr(originalErr);
    }
}

package client;

import java.io.IOException;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;

public class Client {
	public static void main(String[] args) {
		FTPClient ftp = new FTPClient();
		FTPClientConfig config = new FTPClientConfig();
		// user and pass for testing connections
		String username = "anonymous";
		String password = "anonymous";
		System.out.println("running ftp client...");
		// Test ftp client connection
		String server = "speedtest.tele2.net";
		try {
			ftp.connect(server);
		    System.out.println("Connected to " + server + ".");
		    System.out.print(ftp.getReplyString());
		    ftp.login(username, password);
		    FTPFile [] files = ftp.listFiles();
	        for (FTPFile file : files) {
	            System.out.println(file.getName());
	        }
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
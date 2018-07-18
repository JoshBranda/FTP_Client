package client;

import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

/* Default to FTPs connection */
public class FtpConnection {
	private FTPClient ftp;
	private String host;
	private int port;
	private String username;
	private String password;
	private int retries;
	
	public FtpConnection() {
			this.ftp = new FTPClient();
	}
	
	public FTPClient connect(String host, int port) {
		this.host = host;
		this.port = port;
		this.retries = 5;
		// Retry for 5 times if connection fails
		do {
			try {
				this.ftp.connect(this.host, this.port);
				//System.out.println("Connected to " + this.host + " on port: " + this.port);
				break;
			} catch (IOException e) {
				this.retries -= 1;
				if (this.retries <= 0) {
					System.out.println("Connection to host failed...");
					e.printStackTrace();
				}
			}
		} while (this.retries > 0);
		return this.ftp;
	}

	public boolean isConnected() {
		return this.ftp.isConnected();
	}
	
	public FTPClient getConnection() {
		return this.ftp;
	}
	
	public void disconnect() {
		try {
			this.ftp.disconnect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean login(String username, String password) {
		this.username = username;
		this.password = password;

		try {
			return this.ftp.login(this.username, this.password);
		} catch (IOException e) {
			System.out.println("Login failed...");
			e.printStackTrace();
			return false;
		}
	}
}

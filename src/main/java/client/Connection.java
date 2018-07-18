package client;

import java.io.IOException;

import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;

/* Default to FTPs connection */
public class Connection {
	private FTPSClient ftps;
	private String host;
	private int port;
	private String username;
	private String password;
	private int retries;
	
	public Connection() throws IOException {
			this.ftps = new FTPSClient();
	}

	public FTPSClient connect(String host) throws IOException {
		this.host = host;
		this.retries = 5;
		// Retry for 5 times if connection fails
		do {
			try {
				this.ftps.connect(this.host);
				break;
			} catch (IOException e) {
				this.retries -= 1;
				if (this.retries <= 0) {
					throw e;
				}
			}
		} while (this.retries > 0);
		return this.ftps;
	}
	
	public FTPSClient connect(String host, int port) throws IOException {
		this.host = host;
		this.port = port;
		this.retries = 5;
		// Retry for 5 times if connection fails
		do {
			try {
				this.ftps.connect(this.host, this.port);
		        int reply = this.ftps.getReplyCode();
		        if (!FTPReply.isPositiveCompletion(reply))
		        {
		            this.ftps.disconnect();
		            throw new IOException("Exception in connecting to FTP Server");
		        }
				break;
			} catch (IOException e) {
				this.retries -= 1;
				if (this.retries <= 0) {
					throw e;
				}
			}
		} while (this.retries > 0);
		return this.ftps;
	}
	
	public FTPSClient getConnection() throws IOException {
		return this.ftps;
	}
	
	public void disconnect() throws IOException {
		this.ftps.disconnect();
	}
	
	public boolean login(String username, String password) throws IOException {
		this.username = username;
		this.password = password;
		return this.ftps.login(this.username, this.password);
	}
}

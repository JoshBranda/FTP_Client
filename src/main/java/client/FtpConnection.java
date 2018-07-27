package client;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.net.ftp.FTPClient;
import org.yaml.snakeyaml.Yaml;

/* FTP connection */
public class FtpConnection {
	private FTPClient ftp;
	private String host;
	private int port;
	private String username;
	private String password;
	private int retries;
	private String config_file;
	
	public FtpConnection() {
			this.ftp = new FTPClient();
			this.config_file = "src/main/resources/client_config.yaml";
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
				if (this.retries <= 0 && !this.ftp.isConnected()) {
					System.out.println("Connection to host failed...");
					System.out.println(e.toString());
				}
			}
		} while (this.retries > 0);
		return this.ftp;
	}
	
	public void disconnect() {
		try {
			this.ftp.disconnect();
		} catch (IOException e) {
			System.out.println(e.toString());
		}
	}

	public boolean isConnected() {
		return this.ftp.isConnected();
	}
	
	public FTPClient getConnection() {
		return this.ftp;
	}
	
	public boolean login(String username, String password) {
		this.username = username;
		this.password = password;

		try {
			return this.ftp.login(this.username, this.password);
		} catch (IOException e) {
			System.out.println("Login failed...");
			System.out.println(e.toString());
			return false;
		}
	}
	
	public String getInfo() {
		// Return connection info as a string
		// e.g. localhost:8000
		return this.host + ":" + String.valueOf(this.port);
	}
	
	public boolean saveConnection(String connection_name, String connection_info) {
		Yaml config_yaml = new Yaml();

		//Map <String, Object> connection_entries = new HashMap<String, Object>();
		try {
			InputStream config = new FileInputStream(this.config_file);
			Map<String, String> connection_entries = (Map<String,String>) config_yaml.load(config);
			connection_entries.put(connection_name, connection_info);
			config_yaml.dump(connection_entries);
		} catch (FileNotFoundException e) {

				Map <String, String> connection_entries = new HashMap<String, String>();
				connection_entries.put(connection_name, connection_info);
				config_yaml.dump(connection_entries);
				try {
					BufferedWriter config_writer = new BufferedWriter(new FileWriter(this.config_file));
					config_yaml.dump(connection_entries,config_writer);
					//config_writer.write(config_yaml.dump(connection_entries));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				System.out.println(config_yaml.dump(connection_entries));
		}
		return true;
	}
}

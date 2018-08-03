package client;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.net.ftp.FTPClient;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.representer.Representer;

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
		this.config_file = "src/main/client_config.yaml";
		this.username = "anonymous";
		this.password = "anonymous";
	}

	// constructor for alternative config file location
	public FtpConnection(String config_file) {
		this.ftp = new FTPClient();
		this.config_file = config_file;
		this.username = "anonymous";
		this.password = "anonymous";
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
		return this.host + ":" + String.valueOf(this.port) +":"
		                 + this.username + ":" + this.password ;
	}

	@SuppressWarnings("unchecked")
	public boolean saveConnection(String connection_name, String connection_info) {
		// To make config file consistent with yaml style I need to specify custom
		// flow style to BLOCK. Used the sample code from here: 
		// https://www.javatips.net/api/org.yaml.snakeyaml.dumperoptions.flowstyle
		DumperOptions options = new DumperOptions();
		options.setAllowReadOnlyProperties(true);
		options.setDefaultFlowStyle(FlowStyle.BLOCK);
		Yaml yaml = new Yaml(new Representer(), options);

		FileWriter config_writer;
		Map<String, Object> connection_entries;
		File config_file = new File(this.config_file);

		// create connection object
		Map<String, Object> connection = new HashMap<String, Object>();
		String[] connection_split = connection_info.split(":"); 
		String[] connection_info_key = {"host","port","username","password"};
		int i = 0;
		for (String info : connection_split) {
			if (i == 1) {
				// convert port number to int
				connection.put(connection_info_key[i++], Integer.valueOf(info));
			} else {
				connection.put(connection_info_key[i++], info);
			}
		}

		if (config_file.exists()) {
			InputStream config;
			try {
				config = new FileInputStream(this.config_file);
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
			connection_entries = (Map<String, Object>) yaml.load(config);
		} else {
			connection_entries = new HashMap<String, Object>();
		}
		connection_entries.put(connection_name, connection);
		// create writer for the config file
		try {
			config_writer = new FileWriter(this.config_file);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		yaml.dump(connection_entries, config_writer);

		return true;
	}

	public boolean logout(){
		this.username ="";
		this.password="";

		try {
			return this.ftp.logout();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}
}

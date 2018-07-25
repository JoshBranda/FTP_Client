package client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.yaml.snakeyaml.Yaml;

public class SaveConnectionInfo {
	private String config_file;
	
	public SaveConnectionInfo(String config_file) {
		this.config_file = config_file;
	}
	
	public boolean saveConnection(String connection_info) {
		Yaml config_yaml = new Yaml();
		try {
			InputStream config = new FileInputStream(this.config_file);
		} catch (FileNotFoundException e) {
			try {
				InputStream config = new FileInputStream(new File(this.config_file));
			} catch (FileNotFoundException e1) {
				throw new RuntimeException(e1);
			}
		}
		config_yaml.load(connection_info);
		return true;
	}
}

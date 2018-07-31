package client;

import java.io.IOException;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;

// Lee's code for login prompts
import java.util.Scanner; // For User input.

public class Client {
	public static void main(String[] args) {

		LoginPrompts login = new LoginPrompts();

		// user and pass for testing connections
		System.out.println("running ftp client...");

		login.getCreds();

	}
}
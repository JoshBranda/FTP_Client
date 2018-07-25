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

		// Initializing the scanner object for prompts
		Scanner input = new Scanner(System.in);

		// user and pass for testing connections
		System.out.println("running ftp client...");

		// Login prompts code
		System.out.println("Please enter the server you wish to connect to:");
		String server = input.next();
		input.nextLine();

		System.out.println("Please enter your username:");
		String username = input.next();
		input.nextLine();

		System.out.println("Please enter your password:");
		String password = input.next();
		input.nextLine();

	}
}
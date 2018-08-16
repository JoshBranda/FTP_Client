package client;

import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.util.Scanner;

public class Client {
	public static void main(String[] args) {

		LoginPrompts login = new LoginPrompts();

		// user and pass for testing connections
		System.out.println("running ftp client...");

		login.getCreds();
		Scanner input = new Scanner(System.in);
		Connection connection = new Connection();
		connection.connect(login.getServerName());
		connection.login(login.getUsername(), login.getPassword());
		FTPClient ftp = connection.getConnection();
		RemoteFileManager remoteFileManager = new RemoteFileManager(ftp);
		FileManager fileManager = new FileManager();
		boolean quit = false;
		String answer;
		while(!quit)
		{
			System.out.println("What would you like to do with the remote server? Type 'quit' to quit.");
			System.out.println("Press 1 to list local files");
			System.out.println("Press 2 to list remote files");
			System.out.println("Press 3 to upload local file to server");
			System.out.println("Press 4 to download file from server to local");
			System.out.println("Press 5 to make directory");


			answer = input.next();
			input.nextLine();
			if(answer.equals("1"))
			{
				fileManager.displayLocal();
			}
			if(answer.equals("2"))
			{
				System.out.println("Local Files: ");
				remoteFileManager.displayFiles();
				System.out.println("Local Directories: ");
				remoteFileManager.displayDirectories();
			}
			if(answer.equals("3")) {
				System.out.println("What file would you like to upload");
				String inputFile = input.next();
				input.nextLine();
				File fileToInput = new File(inputFile);
				if (!remoteFileManager.uploadFile(fileToInput, fileToInput.getName())) {
					System.out.println("File fail to upload");

				}
			}
			if(answer.equals("4"))
			{
				System.out.println("What file would you like to download?");
				String fileDownload = input.next();
				input.nextLine();
				System.out.println("Where should the file go?");
				String fileDestPath = input.next();
				input.nextLine();
				if (!remoteFileManager.downloadFile(fileDownload, fileDestPath)) {
					System.out.println("File fail to download");
				}
			}
			if(answer.equals("5"))
			{
				System.out.println("What directory would you like to create");
				String directoryPath = input.next();
				input.nextLine();
				remoteFileManager.makeDirectory(directoryPath);
			}

			if(answer.equals("quit"))
			{
				quit = true;
			}
		}
	}
}
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

		Connection connection = new Connection();
		connection.connect(login.getServerName(), 2121);
		connection.login(login.getUsername(), login.getPassword());
		FTPClient ftp = connection.getConnection();
		RemoteFileManager remoteFileManager = new RemoteFileManager(ftp);
		FileManager fileManager = new FileManager();
		boolean quit = false;
		String answer;
		while(!quit)
		{
			Scanner input = new Scanner(System.in);
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
				Scanner fileInput = new Scanner(System.in);
				String inputFile = fileInput.next();
				fileInput.nextLine();
				File fileToInput = new File(inputFile);
				if (!remoteFileManager.uploadFile(fileToInput, fileToInput.getName())) {
					System.out.println("File fail to upload");

				}
			}
			if(answer.equals("4"))
			{
				System.out.println("What file would you like to download?");
				Scanner fileToDownload = new Scanner(System.in);
				String fileDownload = fileToDownload.next();
				fileToDownload.nextLine();
				System.out.println("Where should the file go?");
				String fileDestPath = fileToDownload.next();
				fileToDownload.nextLine();
				if (!remoteFileManager.downloadFile(fileDownload, fileDestPath)) {
					System.out.println("File fail to download");
				}
			}
			if(answer.equals("5"))
			{
				System.out.println("What directory would you like to create");
				Scanner directoryCreation = new Scanner(System.in);
				String directoryPath = directoryCreation.next();
				directoryCreation.nextLine();
				remoteFileManager.makeDirectory(directoryPath);
			}

			if(answer.equals("quit"))
			{
				quit = true;
			}
		}
	}
}
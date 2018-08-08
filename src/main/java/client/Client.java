package client;

public class Client {
	//static private Menu menu;

	public static void main(String[] args) {

		LoginPrompts login = new LoginPrompts();

		// user and pass for testing connections
		System.out.println("running ftp client...");

		login.getCreds();

		FTPClient ftp = new FTPClient();
		FTPClientConfig config = new FTPClientConfig();
		Menu menu = new Menu();
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

       menu.display();
	}

}

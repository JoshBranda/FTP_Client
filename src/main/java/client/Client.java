package client;

public class Client {
	public static void main(String[] args) {

		LoginPrompts login = new LoginPrompts();

		// user and pass for testing connections
		System.out.println("running ftp client...");

		login.getCreds();

		Connection connection = new Connection();
		connection.connect(login.getServerName());
	}
}
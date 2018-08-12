package client;

public class Client {
	static private Menu menu;

	public static void main(String[] args) {
		menu = new Menu();
		LoginPrompts login = new LoginPrompts();

		// user and pass for testing connections
		System.out.println("running ftp client...");

		login.getCreds();

		menu.display();
	}

}

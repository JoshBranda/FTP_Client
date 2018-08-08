// Lee Hoang
// 17 July 2018
// CS 410
// This is the file for Login Prompts. The function getCreds() prompts the user to send a username, a password, and a server name.

package client;

import java.util.Scanner; // For User input.

public class LoginPrompts {
    // Private data members (username, password, and server name)
    private String username;
    private String password;
    private String serverName;

    // Basic constructor.
    public LoginPrompts() {
        this.username = null;
        this.password = null;
        this.serverName = null;
    }


    // Basic constructor with arguments.
    public LoginPrompts(String username, String password, String serverName) {
        this.username = username;
        this.password = password;
        this.serverName = serverName;
    }

    // This function prompts the user for a username, a password, and a server name.
    public void getCreds() {
        Scanner input = new Scanner(System.in);

        System.out.println("Enter a server name:");
        serverName = input.next();
        input.nextLine();

        System.out.println("Enter a username:");
        username = input.next();
        input.nextLine();

        System.out.println("Enter a password:");
        password = input.next();
        input.nextLine();

    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getServerName() {
        return serverName;
    }
}

package client;
import org.apache.commons.net.ftp.FTPSClient;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import javax.rmi.CORBA.Util;
import  java.io.*;
import  java.util.Scanner;

/**
 * Created by gh0st on 7/18/18.
 */
public class Menu{

    protected static Scanner input = null;
	protected static FileManager fm;
	protected static RemoteFileManager rfm;
	protected static RenameFileLocalServer rfls;

    public Menu(){
		input = new Scanner(System.in);
    	fm    = new FileManager();
		rfls  = new RenameFileLocalServer();
	}


    public static int main(){
        return 0;
    }

    public static int display(FTPSClient ftp) {
		rfm = new RemoteFileManager(ftp);
		return display();
	}

	public static int display(){
		String from;
		String to;
		int option;

        System.out.println("FTP Client: Please enter an option number or a command:");
        System.out.println("1. ls client");
        System.out.println("2. rm client file");
        System.out.println("3. mkdir client");
    	System.out.println("4. ls server");
        System.out.println("5. rm server file");
        System.out.println("6. mkdir server");
        System.out.println("7. Rename File On Local side");
        System.out.println("8. Change Permissions on remote file");
        System.out.println("9. Quit");

		option = input.nextInt();
		input.nextLine();
        switch (option) {
            case 1:
				fm.displayLocal();
	            break;
    	    case 2:
				System.out.print("Please enter the file to remove: ");
				from = input.next();
				input.nextLine();
				rfm.removeFile(from);
	            break;
    	    case 3:
				System.out.print("What is the new directory name?");
				to = input.nextLine();
				rfm.makeDirectory(to);
	            break;
	        case 4:
				rfm.displayDirectories();
				rfm.displayFiles();
	            break;
        	case 5:
				System.out.print("What is the file you are removing?");
				//rfm.removeFile("");
	            break;
	        case 6:
				System.out.print("Enter the new server directory name:");
	            to = input.nextLine();
				rfm.makeDirectory(to);
				break;
        	case 7:
				System.out.println("Please enter the file name to change from: ");
				from = input.nextLine();
				rfls.renameFile(from);
	            break;
    	    case  8:
				System.out.println("Please enter the file you want to change permissions on;");
				to = input.nextLine();
				System.out.println("Please enter the permissions;");
				from = input.nextLine();
				rfm.setPermissionRemote(from, to);
	            break;
    	    case 9:
	            System.out.println("Logging out and exiting");
				return 0;
        	default:
	            System.out.println("Not a valid option");
	            break;
        	}
	    return display();
    }
}

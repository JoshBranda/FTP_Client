package client;
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

    public Menu(){
        input = new Scanner(System.in);
        //new Utilities();
    }


    public static int main(){
        return 0;
    }

    public static int display() {
		String from;
		String to;
        //int num = 1;

        System.out.println("FTP Client: Please enter an option number or a command:");
        System.out.println("1. ls client");
        System.out.println("2. rm client file");
        System.out.println("3. mkdir client");
    	System.out.println("4. ls server");
        System.out.println("5. rm server file");
        System.out.println("6. mkdir server");
        System.out.println("7. Rename File");
        System.out.println("8. ");
        System.out.println("9. Quit");

        switch (input.nextInt()) {
            case 1:
				fm.displayLocal();
	            break;
    	    case 2:
				System.out.print("Please enter the file to remove: ");
				from = input.nextLine();
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
				System.out.println("Please enter the new file name:");
				to = input.nextLine();
				rfm.renameFile(from, to);
	            break;
    	    case  8:
	            break;
    	    case 9:
	            System.out.println("Exiting");
	            return 0;
        	default:
	            System.out.println("Not a valid option");
	            break;
        	}
	    return display();
    }
}

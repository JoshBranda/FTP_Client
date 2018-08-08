package client;
import javax.rmi.CORBA.Util;
import  java.io.*;
import  java.util.Scanner;

/**
 * Created by gh0st on 7/18/18.
 */
public class Menu{
    protected static Scanner input = null;

    public Menu(){
        input = new Scanner(System.in);
        //new Utilities();
    }


    public static int main(){
        return 0;
    }

    public static int display() {
        //int num = 1;

        System.out.println("FTP Client: Please enter an option number or a command:");
        System.out.println("1. ls client");
        System.out.println("2. rm client file");
        System.out.println("3. mkdir client");
    	System.out.println("4. ls server");
        System.out.println("5. rm server file");
        System.out.println("6. mkdir server");
        System.out.println("7. ");
        System.out.println("8. ");
        System.out.println("9. Quit");

        switch (input.nextInt()) {
            case 1:
	            break;
    	    case 2:
	            break;
    	    case 3:
	            break;
	        case 4:
	            break;
        	case 5:
	            break;
	        case 6:
	            break;
        	case 7:
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

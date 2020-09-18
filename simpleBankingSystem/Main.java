package org.hyperskill.simpleBankingSystem;

import org.sqlite.SQLiteDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {

    static Scanner scanner = new Scanner(System.in);
    static boolean exit = false;

    public static void main(String[] args) {

        Accounts account = new Accounts();
        String menu = "1. Create an account \n" +
                "2. Log into account \n" +
                "0. Exit";

        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl("jdbc:sqlite:card.s3db");

        try(Connection con = dataSource.getConnection())
        {
            while (!exit) {
                System.out.println(menu);
                int option = Integer.parseInt(scanner.nextLine());
                switch (option) {
                    case 1:
                        account.createAccount(con);
                        break;
                    case 2:
                        log(account, con);
                        break;
                    case 0:
                        System.out.println("Bye!");
                        exit = true;
                        break;
                    default:
                        System.out.println("Enter a valid option!\n");
                }
            }
        } catch(
                SQLException e)
        {
            e.printStackTrace();
        }
    }

    private static void log(Accounts account, Connection con) {

        String logMenu = "1. Balance \n" +
                "2. Add income \n" +
                "3. Do transfer \n" +
                "4. Close account \n" +
                "5. Log out \n" +
                "0. Exit \n";

        boolean logExit = false;

        System.out.println("Enter your card number:");
        String numInput = scanner.nextLine();
        System.out.println("Enter your PIN:");
        String pinInput = scanner.nextLine();

        if (numInput.equals(account.getNum()) && pinInput.equals(account.getPin())) {

            System.out.println("You have successfully logged in!\n");

            while (!logExit) {
                System.out.println(logMenu);
                int option = Integer.parseInt(scanner.nextLine());
                switch (option) {
                    case 1:
                        System.out.println("Balance: " + account.getBalance() + "\n");
                        break;
                    case 2:
                        account.addIncome(con);
                        break;
                    case 3:
                        account.doTansfer(con);
                        break;
                    case 4:
                        account.closeAccount(con);
                        break;
                    case 5:
                        System.out.println("You have successfully logged out!\n");
                        logExit = true;
                        break;
                    case 0:
                        System.out.println("Bye!");
                        exit = true;
                        logExit = true;
                        break;
                    default:
                        System.out.println("Enter a valid option!\n");
                }
            }
        } else {
            System.out.println("Wrong card number or PIN\n");
        }
    }

}

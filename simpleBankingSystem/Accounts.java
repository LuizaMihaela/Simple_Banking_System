package org.hyperskill.simpleBankingSystem;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
import java.util.Scanner;

public class Accounts {

    Scanner scanner = new Scanner(System.in);
    private String num;
    private String pin;
    private int balance;

    public String getNum() {
        return num;
    }

    public String getPin() {
        return pin;
    }

    public int getBalance() {
        return balance;
    }

    void createAccount(Connection con) {

        System.out.println("Your card has been created");
        System.out.println("Your card number:");
        luhn();
        System.out.println("Your card PIN:");
        generatePin();
        createTable(con);
    }

    private void createTable(Connection con) {

        try (Statement statement = con.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS card(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "number TEXT," +
                    "pin TEXT" +
                    "balance INTEGER DEFAULT 0)");
            String insertCard = String.format("INSERT INTO card (number, pin) VALUES('%s', '%s');", num, pin);
            statement.executeUpdate(insertCard);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void addIncome(Connection con) {

        System.out.println("Enter income:");
        int addInput = Integer.parseInt(scanner.nextLine());
        balance += addInput;

        try (Statement statement = con.createStatement()) {
            String add = String.format("UPDATE card SET balance = '%s' WHERE number = '%s';", balance, num);
            statement.executeUpdate(add);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void doTansfer(Connection con) {

        System.out.println("Transfer");
        System.out.println("Enter card number:");
        String numInput = scanner.nextLine();
        int checkSum = 0;
        for (int i = 0; i < numInput.length() - 1; i++) {
            int digit = Integer.parseInt(String.valueOf(numInput.charAt(i)));
            if (i % 2 == 0) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }
            checkSum += digit;
        }
        int lastDigit = Integer.parseInt(String.valueOf(numInput.charAt(numInput.length() - 1)));
        if (checkSum + lastDigit % 10 != 0) {
            System.out.println("Probably you made a mistake in the card number. Please try again!");
        } else {
            try (Statement statement = con.createStatement()) {
                String checkNum = String.format("SELECT * FROM card WHERE number = '%s';", numInput);
                int i = statement.executeUpdate(checkNum);
                if (i == 0) {
                    System.out.println("Such a card does not exist.");
                } else {
                    System.out.println("Enter how much money you want to transfer:");
                    int amount = Integer.parseInt(scanner.nextLine());
                    if (balance < amount) {
                        System.out.println("Not enough money!");
                    } else {
                        String transferAmount = String.format("UPDATE card SET balance = '%s' WHERE number = '%s';", balance - amount, num);
                        String addAmount = String.format("UPDATE card SET balance = '%s' WHERE number = '%s';", balance + amount, numInput);
                        statement.executeUpdate(transferAmount);
                        statement.executeUpdate(addAmount);
                        System.out.println("Success!");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }

    void closeAccount(Connection con) {
        try (Statement statement = con.createStatement()) {
            String delete = String.format("DELETE FROM card WHERE number = '%s';", num);
            statement.executeUpdate(delete);
            System.out.println("The account has been closed!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void luhn() {

        Random random = new Random();

        int temp = random.nextInt(999_999_999 - 100_000_000) + 100_000_000;
        String tempCardNumber = "400000" + temp;

        int checkSum = 0;
        for (int i = 0; i < tempCardNumber.length(); i++) {
            int digit = Integer.parseInt(String.valueOf(tempCardNumber.charAt(i)));
            if (i % 2 == 0) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }
            checkSum += digit;
        }
        int lastDigit = 0;
        if (checkSum % 10 != 0) {
            lastDigit = 10 - (checkSum % 10);
        }

        num = tempCardNumber + lastDigit;
        System.out.println(num);
    }

    private void generatePin() {
        StringBuilder builder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            builder.append(random.nextInt(10));
        }
        pin = builder.toString();
        System.out.println(pin);
    }

}

package tk.sakizciadam.hwidchecker.example;

import org.apache.commons.lang3.RandomStringUtils;
import tk.sakizciadam.hwidchecker.HWIDChecker;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ServerExample {
    private final String TABLE_NAME="info";
    private final HWIDChecker checker;

    public ServerExample(){
        //Execute this once
        Scanner scanner=new Scanner(System.in);

        System.out.println(">>> Database User?");
        String USER=scanner.nextLine();
        System.out.println(">>> Database Name?");
        String NAME=scanner.nextLine();
        System.out.println(">>> Database Password?");
        String PASSWORD=scanner.nextLine();
        System.out.println(">>> Database Host?");
        String HOST=scanner.nextLine();

        this.checker=new HWIDChecker(USER,NAME,PASSWORD,HOST);

        try {
            checker.connect();



            if (!checker.tableExists(TABLE_NAME)){
                checker.createTable(TABLE_NAME);
            }



            System.out.println(">>> Type help to see all the commands");

            while (true){
                String cmd=scanner.nextLine().toLowerCase();

                //Don't judge me, I don't want to use Java 7 just for switch case


                if (cmd.equalsIgnoreCase("help")){
                    ArrayList<String> list=new ArrayList<String>(Arrays.asList(
                            "Commands: ",
                            "help - bro",
                            "generate - Generates x amount of tokens"

                    ));

                    System.out.println(list.toString());

                } else if(cmd.startsWith("generate")){
                    System.out.println(">>> Please type the amount of tokens you want to create?");
                    String amountStr=scanner.nextLine().toLowerCase();

                    int amount=Integer.parseInt(amountStr);
                    final String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

                    for (int i=0;i<amount;i++){

                        String pwd = RandomStringUtils.random( 40, characters );
                        checker.createUser(TABLE_NAME,pwd);

                        System.out.println(pwd);
                    }




                }
                System.out.println(">>>");
            }












        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }



    public static void main(String[] args){
        new ServerExample();
    }
}

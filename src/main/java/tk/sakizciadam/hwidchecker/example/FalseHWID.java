package tk.sakizciadam.hwidchecker.example;

import io.github.cdimascio.dotenv.Dotenv;
import org.apache.commons.codec.digest.DigestUtils;
import tk.sakizciadam.hwidchecker.HWIDChecker;
import tk.sakizciadam.hwidchecker.Utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class FalseHWID {

    private final String TABLE_NAME="info";


    public FalseHWID(){
        HWIDChecker checker=null;
        //you don't need to get it from an env file, im just protecting it

        Dotenv dotenv = Dotenv.configure()
                .directory(".env")
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();

        this.checker=new HWIDChecker(dotenv.get("DATABASE_USER"),dotenv.get("DATABASE_NAME"),dotenv.get("DATABASE_PASSWORD"),dotenv.get("DATABASE_HOST"));


        try {

            Scanner scanner = new Scanner(System.in);  // Create a Scanner object

            System.out.println(">>> Name?");
            String NAME=scanner.nextLine();
            System.out.println(">>> Password?");
            String PASSWORD=scanner.nextLine();

            if(NAME.equalsIgnoreCase("none")||PASSWORD.equalsIgnoreCase("none")){
                System.out.println("Username or password cannot be null!");
            }



            checker.connect();


            //false hwid for testing
            String hwid= "nah";
            String secureHWID=DigestUtils.sha256Hex(DigestUtils.sha256Hex(hwid));




            if (checker.login(TABLE_NAME,NAME,PASSWORD,secureHWID)){
                System.out.println("Logged in");
            } else {
                System.out.println("Couldn\'t log in");
                System.out.println(">>> Token?");
                String TOKEN=scanner.nextLine();

                if (checker.isUnregisteredUser(TABLE_NAME,TOKEN)){
                    checker.register(TABLE_NAME,TOKEN,NAME,PASSWORD,secureHWID);
                    System.out.println("Registered");
                } else {
                    String format=String.format("SELECT * FROM `%s` WHERE name=\'%s\' and password=\'%s\' and token=\'%s\' and hwid!=\'%s\'",TABLE_NAME,NAME,PASSWORD,TOKEN,secureHWID);



                    ResultSet resultSet= checker.executeQuery(format);

                    if(resultSet.next()){
                        //Someone managed to log in from a different account but HWID doesn't match up??
                        System.out.println("wtf");


                    } else {
                        System.out.println("Wrong username or password!");
                    }
                }
            }









        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }





    public static void main(String[] args){
        new FalseHWID();
    }
}

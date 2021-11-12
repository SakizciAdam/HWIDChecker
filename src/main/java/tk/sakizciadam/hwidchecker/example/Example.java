package tk.sakizciadam.hwidchecker.example;

import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.commons.codec.digest.DigestUtils;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.ComputerSystem;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;
import tk.sakizciadam.hwidchecker.HWIDChecker;
import tk.sakizciadam.hwidchecker.Utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Example {

    private final String TABLE_NAME="info";


    public Example(){
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



            String hwid= Utils.generateLicenseKey();
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
                        System.out.println("wtf!");


                    } else {
                        String _format=String.format("SELECT * FROM `%s` WHERE token=\'%s\' and hwid='%s'",TABLE_NAME,TOKEN,secureHWID);


                        ResultSet _resultSet= checker.executeQuery(_format);



                        System.out.println("Wrong username or password!");

                        if(_resultSet.next()){

                            System.out.println(">>> Wanna reset credentials? y/n");
                            String yesno=scanner.nextLine();

                            if(yesno.equalsIgnoreCase("y")){

                                System.out.println(">>> Name?");
                                NAME=scanner.nextLine();
                                System.out.println(">>> Password?");
                                PASSWORD=scanner.nextLine();

                                if(NAME.equalsIgnoreCase("none")||PASSWORD.equalsIgnoreCase("none")){
                                    System.out.println("Username or password cannot be null!");
                                }

                                checker.unRegister(TABLE_NAME,TOKEN);
                                checker.register(TABLE_NAME,TOKEN,NAME,PASSWORD,secureHWID);

                                if (checker.login(TABLE_NAME,NAME,PASSWORD,secureHWID)){
                                    System.out.println("Changed the credentials and managed to log in");


                                } else {
                                    System.out.println("Something went wrong rip lol");
                                }


                            }
                        } else {
                            //HWID and token wrong so no reset credentials!
                        }



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
        new Example();
    }
}

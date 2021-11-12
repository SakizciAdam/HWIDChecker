package tk.sakizciadam.hwidchecker;

import org.apache.commons.codec.digest.DigestUtils;
import tk.sakizciadam.hwidchecker.exceptions.UserRegisterException;

import java.sql.*;

public class HWIDChecker {
    private String userName,databaseName,databasePassword,databaseHost;

    //Connection
    private Connection con;
    private Statement statement;

    private final String DATABASE_USER = "user";
    private final String DATABASE_PASSWORD = "password";
    private final String MYSQL_AUTO_RECONNECT = "autoReconnect";


    public HWIDChecker(String userName,String databaseName,String databasePassword,String databaseHost){
        this.userName=userName;
        this.databaseName=databaseName;
        this.databasePassword=databasePassword;
        this.databaseHost=databaseHost;




    }

    public void unRegister(String tableName,String token) throws SQLException {

        if(!isUnregisteredUser(tableName,token)){

            String format1=String.format("SELECT * FROM `%s` WHERE name!=\'none\' and token=\'%s\'",tableName,token);



            ResultSet resultSet=this.executeQuery(format1);

            if(resultSet.next()){
                String format=String.format("DELETE FROM %s WHERE token=\'%s\'",tableName,token);


                this.executeUpdate(format);
                this.createUser(tableName,token,"none","none","none");
            }
        }
    }

    public boolean login(String tableName,String user,String pass,String hwid) throws SQLException {


        //secure the hwid


        String format=String.format("SELECT * FROM `%s` WHERE name=\'%s\' and password=\'%s\' and hwid=\'%s\'",tableName,user,pass,hwid);



        ResultSet resultSet=this.executeQuery(format);

        if(resultSet.next()){
            return true;
        }

        return false;
    }

    public void register(String tableName,String token,String user,String pass,String hwid) throws SQLException {

        if(isUnregisteredUser(tableName,token)){
            String format=String.format("DELETE FROM %s WHERE name=\'%s\' AND password=\'%s\' AND token=\'%s\' AND hwid=\'%s\'",tableName,"none","none",token,"none");


            this.executeUpdate(format);
            this.createUser(tableName,token,user,pass,hwid);

        } else {
            String format=String.format("SELECT * FROM `%s` WHERE name!=\'none\' and token=\'%s\'",tableName,token);


            ResultSet resultSet=this.executeQuery(format);

            if(resultSet.next()){
                throw new UserRegisterException("User already registered",new Throwable());
            } else {
                throw new UserRegisterException("User with the token of "+token+" doesn\'t exist",new Throwable());
            }



        }
    }

    public boolean isUnregisteredUser(String table,String token) throws SQLException {
        ResultSet resultSet=this.executeQuery(String.format("SELECT EXISTS(SELECT * from %s WHERE name=\'%s\' AND password=\'%s\' AND token=\'%s\' AND hwid=\'%s\');",table,"none","none",token,"none"));

        while (resultSet.next()){
            return resultSet.getInt(1)>0;
        }
        return false;
    }

    public void createUser(String table,String token) throws SQLException {
        this.createUser(table,token,"none","none","none");

    }

    public void createUser(String table,String token,String user,String password,String hwid) throws SQLException {
        executeUpdate(String.format("INSERT into %s values(\'%s\',\'%s\',\'%s\',\'%s\');",table,user,password,token,hwid));

    }

    public boolean tableExists(String tableName) throws SQLException {
        DatabaseMetaData dbm = con.getMetaData();

        ResultSet tables = dbm.getTables(null, null, tableName, null);

        return tables.next();
    }

    public void createTable(String tableName) throws SQLException {
        executeUpdate(String.format("CREATE TABLE %s(" +
                "name varchar(16) NOT NULL,"+
                "password varchar(32) NOT NULL,"+
                "token varchar(45) NOT NULL,"+
                "hwid varchar(3000) NOT NULL"+

                ");",tableName));
    }

    public void disconnect() throws SQLException {
        if(con!=null&&!con.isClosed()){
            con.close();
        }
    }

    public void connect() throws SQLException, ClassNotFoundException {
        this.connect(3306);
    }

    public void connect(int port) throws ClassNotFoundException, SQLException {


        //Class.forName("com.mysql.jdbc.Driver");

        String conStr=String.format("jdbc:mysql://%s:%s/%s",this.databaseHost,String.valueOf(port),this.databaseName);

        java.util.Properties connProperties = new java.util.Properties();
        connProperties.put(DATABASE_USER, this.databaseName);
        connProperties.put(DATABASE_PASSWORD, this.databasePassword);

        connProperties.put(MYSQL_AUTO_RECONNECT, "true");

        this.con= DriverManager.getConnection(conStr,connProperties);

        this.statement=con.createStatement();


    }

    public ResultSet executeQuery(String string) throws SQLException {
        ResultSet rs=statement.executeQuery(string);

        return rs;

    }

    public void executeUpdate(String string) throws SQLException {
        statement.executeUpdate(string);



    }




    protected String getDatabaseName() {
        return databaseName;
    }

    protected String getDatabaseHost() {
        return databaseHost;
    }

    protected String getDatabasePassword(){
        return databasePassword;
    }
}

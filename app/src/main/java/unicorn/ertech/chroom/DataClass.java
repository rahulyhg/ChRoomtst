package unicorn.ertech.chroom;

/**
 * Created by ILDAR on 06.07.2015.
 */
public class DataClass {

    private static int izumCount = 0;
    private static int userID = 0;
    private static String userToken = "";

    public static void setIzumCount(int count){
        izumCount = count;
    }

    public static int getIzumCount(){
        return izumCount;
    }

    public static void setToken(String token){
        userToken = token;
    }

    public static String getToken(){
        return userToken;
    }

    public static void setUserID(int id){
        userID = id;
    }

    public static int getUserID(){
        return userID;
    }
}

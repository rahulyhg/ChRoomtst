package unicorn.ertech.chroom;

/**
 * Created by Ильнур on 19.01.2015.
 */
public class anonChat {
    String message;
    String uid;
    String sex;
    String from;

    public anonChat(String UID,String FROM, String SEX, String MESSAGE)
    {
        this.uid = UID;
        this.sex = SEX;
        this.message = MESSAGE;
        this.from = FROM;
    }

    public String getUid()
    {
        return uid;
    }

    public String getMessage()
    {
        return  message;
    }

    public String getSex()
    {
        return  sex;
    }
}

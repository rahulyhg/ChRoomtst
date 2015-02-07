package unicorn.ertech.chroom;

/**
 * Created by Ильнур on 01.02.2015.
 */
public class sResult {
    String name;
    String picUrl;
   public String uid;

    public  sResult(String UID, String NAME, String PIC)
    {
        this.uid = UID;
        this.name = NAME;
        this.picUrl = PIC;
    }

    public  String getUid()
    {
        return uid;
    }
}

package unicorn.ertech.chroom;

/**
 * Created by Ильнур on 09.01.2015.
 */
public class chatMessage {

    String message;
    String uid;
    String from;
    String picURL;
    String messageID;

    public chatMessage(String UID, String FROM, String MSG, String pcURL, String mID)
    {
        this.uid = UID;
        this.from = FROM;
        this.message = MSG;
        this.picURL = pcURL;
        this.messageID = mID;
    }

    public String getUid()
    {
        return uid;
    }

    public String getFrom()
    {
        return from;
    }

    public String getMessage()
    {
        return  message;
    }

    public String getPicURL()
    {
        return picURL;
    }
}

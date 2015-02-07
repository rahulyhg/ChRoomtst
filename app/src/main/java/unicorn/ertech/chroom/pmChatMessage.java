package unicorn.ertech.chroom;

import java.util.logging.StreamHandler;

/**
 * Created by Ильнур on 26.01.2015.
 */
public class pmChatMessage {

    String message;
    String uid;
    String direction;

    public pmChatMessage(String UID,String MSG, String DIR)
    {
        this.uid = UID;
        this.message = MSG;
        this.direction = DIR;
    }
}

package unicorn.ertech.chroom;

import java.util.logging.StreamHandler;

/**
 * Created by Ильнур on 26.01.2015.
 */
public class pmChatMessage {

    String message;
    String uid;
    String direction;
    String[] attach;

    public pmChatMessage(String UID,String MSG, String DIR, String[] Attach)
    {
        this.uid = UID;
        this.message = MSG;
        this.direction = DIR;
        this.attach = new String[5];
        for(int i=0; i<5; i++){
            this.attach[i]=Attach[i];
        }
    }
}

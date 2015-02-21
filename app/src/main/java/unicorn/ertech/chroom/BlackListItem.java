package unicorn.ertech.chroom;

/**
 * Created by Ильнур on 20.02.2015.
 */
public class BlackListItem {
    public String name;
    public String avatar;
    public String id;

    public BlackListItem(String ID, String NAME,String PHOTO)
    {
        this.id = ID;
        this.name = NAME;
        this.avatar = PHOTO;
    }
}

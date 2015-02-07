package unicorn.ertech.chroom;

/**
 * Created by Timur on 04.02.2015.
 */
public class newsItem {
    String title;
    String description;
    String URL;
    String picURL;

    public newsItem(String TITLE, String DESCRIPTION, String URL, String pcURL)
    {
        this.title = TITLE;
        this.description = DESCRIPTION;
        this.URL = URL;
        this.picURL = pcURL;
    }

    public String getTitle()
    {
        return title;
    }

    public String getDescription()
    {
        return description;
    }

    public String getURL()
    {
        return  URL;
    }

    public String getPicURL()
    {
        return picURL;
    }
}

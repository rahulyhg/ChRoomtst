package unicorn.ertech.chroom;

/**
 * Created by UNICORN on 06.04.2015.
 */
public class GeoConvertIds {
    int region;

    public int getServerRegionId(int appRegionId){
        switch(appRegionId){
            case 0:
                return 4312;
            default:
                return 4312;
        }
    }

}

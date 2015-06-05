package unicorn.ertech.chroom;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Azat on 27.04.15.
 */
public class HowUseAdapter extends FragmentPagerAdapter{

    public HowUseAdapter(FragmentManager mgr){
        super(mgr);
    }

    @Override
    public Fragment getItem(int i) {

        return (HowUsePageFragment.newInstance(i));
    }

    @Override
    public int getCount() {
        return (4);
    }


}

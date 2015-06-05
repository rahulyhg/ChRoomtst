package unicorn.ertech.chroom;

import java.util.ArrayList;

import unicorn.ertech.chroom.SmilesGridAdapter.KeyClickListener;

import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.GridView;
/**
 * Created by Azat on 19.05.15.
 */
public class SmilePagerAdapter extends PagerAdapter {

    ArrayList<Integer> emoticons;
    private static final int NO_OF_EMOTICONS_PER_PAGE = 34;
    FragmentActivity mActivity;
    SmilesGridAdapter.KeyClickListener mListener;

    public SmilePagerAdapter(FragmentActivity activity,
            ArrayList<Integer> emoticons, KeyClickListener listener) {
        this.emoticons = emoticons;
        this.mActivity = activity;
        this.mListener = listener;
    }

    @Override
    public int getCount() {
        return (int) Math.ceil((double) emoticons.size()
                / (double) NO_OF_EMOTICONS_PER_PAGE);
    }

    @Override
    public Object instantiateItem(View collection, int position) {

        View layout = mActivity.getLayoutInflater().inflate(
                R.layout.smile_grid, null);

        int initialPosition = position * NO_OF_EMOTICONS_PER_PAGE;
        ArrayList<Integer> emoticonsInAPage = new ArrayList<Integer>();

        for (int i = initialPosition; i < initialPosition
                + NO_OF_EMOTICONS_PER_PAGE
                && i < emoticons.size(); i++) {
            emoticonsInAPage.add(emoticons.get(i));
        }

        GridView grid = (GridView) layout.findViewById(R.id.emoticons_grid);
        SmilesGridAdapter adapter = new SmilesGridAdapter(
                mActivity.getApplicationContext(), emoticonsInAPage, position,
                mListener);
        grid.setAdapter(adapter);

        ((ViewPager) collection).addView(layout);

        return layout;
    }

    @Override
    public void destroyItem(View collection, int position, Object view) {
        ((ViewPager) collection).removeView((View) view);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}

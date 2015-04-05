package unicorn.ertech.chroom;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by UNICORN on 05.04.2015.
 */
public class photoPage extends Fragment {
    static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";

    int pageNumber;
    int backColor;
    static String picURL;

    static photoPage newInstance(int page, String url) {
        photoPage pageFragment = new photoPage();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
        pageFragment.setArguments(arguments);
        picURL=url;
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.photo_fragment, null);
        TouchImageView imgview = (TouchImageView) view.findViewById(R.id.ivPhotoViewer);
        Picasso mPicasso = Picasso.with(getActivity().getApplicationContext());
        mPicasso.load(picURL).into(imgview);
        return view;
    }
}
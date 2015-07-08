package unicorn.ertech.chroom;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Azat on 16.04.15.
 */
public class SmileManager implements SmilesGridAdapter.KeyClickListener, View.OnKeyListener{

    private static final int SMILECOUNT = 34;
    final String TEST = "TEST PROGRAM";

    ArrayList<Integer> arrayID = new ArrayList();
    ArrayList<SmileManagerHolder> arraySmile = new ArrayList<>();

    Context context;

    private int keyboardHeight;
    private View popUpView;
    private PopupWindow popupWindow;
    private LinearLayout smilesCover;
    private boolean isKeyBoardVisible;
    private LinearLayout parentLayout;
    private ImageButton smilesButton;
    private EditText content;



    View view;
    FragmentActivity activity;

    public SmileManager(Context context) {

        this.context = context;

        addSmilesInArray();
        bindSmilePattern();
    }

    public SmileManager(FragmentActivity activity, Context context, View view) {

        this.context = context;
        this.activity = activity;
        this.view = view;

        init();
    }

    private void init(){

        parentLayout = (LinearLayout) view.findViewById(R.id.list_parent);
        popUpView = activity.getLayoutInflater().inflate(R.layout.smile_popup, null);
        smilesCover = (LinearLayout) view.findViewById(R.id.footer_for_emoticons);

        final float popUpheight = context.getResources().getDimension(
                R.dimen.keyboard_height);
        changeKeyboardHeight((int) popUpheight);

        smilesButton = (ImageButton) view.findViewById(R.id.butSmile);
        smilesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TEST, "Кнопка смайликов нажата");
                if(!popupWindow.isShowing()){
//                    суда нужно вписать высоту клавиатуры
                    popupWindow.setHeight(keyboardHeight);

                    if(isKeyBoardVisible){
                        smilesCover.setVisibility(LinearLayout.GONE);
                    } else{
                        smilesCover.setVisibility(LinearLayout.VISIBLE);
                    }
                    popupWindow.showAtLocation(parentLayout, Gravity.BOTTOM, 0, 0);
                } else {
                    popupWindow.dismiss();
                }
            }
        });

        smilesButton.setOnKeyListener(this);

        addSmilesInArray();

        bindSmilePattern();


        enablePopUpView();
        checkKeyboardHeight(parentLayout);
        enableFooterView();
    }

    private void addSmilesInArray(){

        for(int i=0; i<SMILECOUNT; i++){
            int sum = i + 1;
            int resID = context.getResources().getIdentifier("s" + sum, "drawable", context.getPackageName());
            arrayID.add(resID);
        }

        arraySmile.add(new SmileManagerHolder(":)", arrayID.get(0)));
        arraySmile.add(new SmileManagerHolder(";)", arrayID.get(1)));
        arraySmile.add(new SmileManagerHolder(":/", arrayID.get(2)));
        arraySmile.add(new SmileManagerHolder("xD", arrayID.get(3)));
        arraySmile.add(new SmileManagerHolder(":D", arrayID.get(4)));
        arraySmile.add(new SmileManagerHolder("o_0", arrayID.get(5)));
        arraySmile.add(new SmileManagerHolder(":|", arrayID.get(6)));
        arraySmile.add(new SmileManagerHolder("8)", arrayID.get(7)));
        arraySmile.add(new SmileManagerHolder(":(", arrayID.get(8)));
        arraySmile.add(new SmileManagerHolder(":*(", arrayID.get(9)));
        arraySmile.add(new SmileManagerHolder(">:(", arrayID.get(10)));
        arraySmile.add(new SmileManagerHolder(":-o", arrayID.get(11)));
        arraySmile.add(new SmileManagerHolder(">:|", arrayID.get(12)));
        arraySmile.add(new SmileManagerHolder(":'(", arrayID.get(13)));
        arraySmile.add(new SmileManagerHolder("%-|", arrayID.get(14)));
        arraySmile.add(new SmileManagerHolder("*love*", arrayID.get(15)));
        arraySmile.add(new SmileManagerHolder(":*", arrayID.get(16)));
        arraySmile.add(new SmileManagerHolder("x0", arrayID.get(17)));
        arraySmile.add(new SmileManagerHolder(":-))", arrayID.get(18)));
        arraySmile.add(new SmileManagerHolder(":p", arrayID.get(19)));
        arraySmile.add(new SmileManagerHolder(">:/", arrayID.get(20)));
        arraySmile.add(new SmileManagerHolder(":X", arrayID.get(21)));
        arraySmile.add(new SmileManagerHolder("(:)", arrayID.get(22)));
        arraySmile.add(new SmileManagerHolder(":-!", arrayID.get(23)));
        arraySmile.add(new SmileManagerHolder("dog", arrayID.get(24)));
        arraySmile.add(new SmileManagerHolder("fox", arrayID.get(25)));
        arraySmile.add(new SmileManagerHolder("giraffe", arrayID.get(26)));
        arraySmile.add(new SmileManagerHolder("hamster", arrayID.get(27)));
        arraySmile.add(new SmileManagerHolder("koala", arrayID.get(28)));
        arraySmile.add(new SmileManagerHolder("lemur", arrayID.get(29)));
        arraySmile.add(new SmileManagerHolder("owl", arrayID.get(30)));
        arraySmile.add(new SmileManagerHolder("panda", arrayID.get(31)));
        arraySmile.add(new SmileManagerHolder("seal", arrayID.get(32)));
        arraySmile.add(new SmileManagerHolder("idler", arrayID.get(33)));
        arraySmile.add(new SmileManagerHolder(":-)", arrayID.get(0)));
        arraySmile.add(new SmileManagerHolder(";-)", arrayID.get(1)));
        arraySmile.add(new SmileManagerHolder(":-/", arrayID.get(2)));
        arraySmile.add(new SmileManagerHolder("XD", arrayID.get(3)));
        arraySmile.add(new SmileManagerHolder(":-D", arrayID.get(4)));
        arraySmile.add(new SmileManagerHolder(":-|", arrayID.get(6)));
        arraySmile.add(new SmileManagerHolder("8-)", arrayID.get(7)));
        arraySmile.add(new SmileManagerHolder(":-(", arrayID.get(8)));
        arraySmile.add(new SmileManagerHolder(":-*", arrayID.get(16)));
        arraySmile.add(new SmileManagerHolder("x-0", arrayID.get(17)));
        arraySmile.add(new SmileManagerHolder(":-p", arrayID.get(19)));
    }

    public View setView(){
        return this.view;
    }

    public boolean windowDismiss(){
        if(windowIsShowing())
            popupWindow.dismiss();
        return true;
    }

    public boolean windowIsShowing(){
        return popupWindow.isShowing();
    }

    private void enablePopUpView() {

        ViewPager pager = (ViewPager) popUpView.findViewById(R.id.emoticons_pager);
        pager.setOffscreenPageLimit(2);

        SmilePagerAdapter adapter = new SmilePagerAdapter(activity, arrayID, this);
        pager.setAdapter(adapter);


        // Creating a pop window for emoticons keyboard
        popupWindow = new PopupWindow(popUpView, LayoutParams.MATCH_PARENT,
                (int)keyboardHeight, false);

//        TextView backSpace = (TextView) popUpView.findViewById(R.id.back);
//        backSpace.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
//                content.dispatchKeyEvent(event);
//            }
//        });

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                smilesCover.setVisibility(LinearLayout.GONE);
            }
        });
    }

    int previousHeightDiffrence = 0;
    private void checkKeyboardHeight(final View parentLayout) {

        parentLayout.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {

                        Rect r = new Rect();
                        parentLayout.getWindowVisibleDisplayFrame(r);

                        int screenHeight = parentLayout.getRootView()
                                .getHeight();
                        int heightDifference = screenHeight - (r.bottom);

                        if (previousHeightDiffrence - heightDifference > 50) {
                            popupWindow.dismiss();
                        }

                        previousHeightDiffrence = heightDifference;
                        if (heightDifference > 100) {

                            isKeyBoardVisible = true;
                            changeKeyboardHeight(heightDifference);

                        } else {

                            isKeyBoardVisible = false;

                        }

                    }
                });
    }

    private void enableFooterView() {

        content = (EditText) view.findViewById(R.id.editText1);
        content.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });

        content.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
            }
        });


    }

    @Override
    public void keyClickedIndex(final Integer index) {
        CharSequence text = null;
        for(SmileManagerHolder smile : arraySmile){
            if (smile.resID == index){
                text = smile.smile;
            }
        }
        int cursorPosition = content.getSelectionStart();
        content.getText().insert(cursorPosition,getSmiledText(context, text));
    }

    private void changeKeyboardHeight(int height) {

        if (height > 100) {
            keyboardHeight = height;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, keyboardHeight);
            smilesCover.setLayoutParams(params);
        }

    }


    private final void bindSmilePattern() {
        for (int i = 0; i < arraySmile.size(); i++) {
            addPattern(emoticons,
                    arraySmile.get(i).smile,
                    arraySmile.get(i).resID);
        }
    }

    private static final Map<Pattern, Integer> emoticons = new HashMap<>();

    private static final Spannable.Factory spannableFactory = Spannable.Factory
            .getInstance();

    private static void addPattern(Map<Pattern, Integer> map, String smile,
                            int resource) {
        map.put(Pattern.compile(Pattern.quote(smile)), resource);
    }

    private static boolean addSmiles(Context context, Spannable spannable) {
        boolean hasChanges = false;

        for (Map.Entry<Pattern, Integer> entry : emoticons.entrySet()) {
            Matcher matcher = entry.getKey().matcher(spannable);
            while (matcher.find()) {
                boolean set = true;
                for (ImageSpan span : spannable.getSpans(matcher.start(),
                        matcher.end(), ImageSpan.class))
                    if (spannable.getSpanStart(span) >= matcher.start()
                            && spannable.getSpanEnd(span) <= matcher.end())
                        spannable.removeSpan(span);
                    else {
                        set = false;
                        break;
                    }
                if (set) {
                    hasChanges = true;
                    spannable.setSpan(new ImageSpan(context, entry.getValue()),
                            matcher.start(), matcher.end(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
        return hasChanges;
    }

    public static Spannable getSmiledText(Context context, CharSequence text) {
        Spannable spannable = spannableFactory.newSpannable(text);
        addSmiles(context, spannable);
        return spannable;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && popupWindow.isShowing()) {
            popupWindow.dismiss();
            return true;
        }
        return false;
    }
}
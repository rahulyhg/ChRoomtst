package unicorn.ertech.chroom;

import android.app.Activity;
import android.content.Context;
import android.text.Spannable;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Azat on 16.04.15.
 */
public class smileManager{

    int smileCount = 34;
    final String TEST = "TEST PROGRAM";
    ArrayList<ImageView> imgList = new ArrayList<ImageView>();
    ArrayList<Integer> arrayID = new ArrayList();
    ArrayList<smileManagerHolder> arraySmile = new ArrayList<>();
    Context context;
    View view;
    EditText editText;
    TableLayout smileTable;
    Activity activity;
    boolean keyboard, smiles;

    public smileManager(Activity activity) {

        this.context = activity.getApplicationContext();
        this.activity = activity;

        keyboard = smiles = false;

        for(int i=0;i<smileCount;i++){
            int sum = i + 1;
            int resID = context.getResources().getIdentifier("s" + sum, "drawable", context.getPackageName());
            arrayID.add(resID);
        }

        arraySmile.add(new smileManagerHolder(":)", arrayID.get(0)));
        arraySmile.add(new smileManagerHolder(";)", arrayID.get(1)));
        arraySmile.add(new smileManagerHolder(":/", arrayID.get(2)));
        arraySmile.add(new smileManagerHolder("xD", arrayID.get(3)));
        arraySmile.add(new smileManagerHolder(":D", arrayID.get(4)));
        arraySmile.add(new smileManagerHolder("o_0", arrayID.get(5)));
        arraySmile.add(new smileManagerHolder(":|", arrayID.get(6)));
        arraySmile.add(new smileManagerHolder("8)", arrayID.get(7)));
        arraySmile.add(new smileManagerHolder(":(", arrayID.get(8)));
        arraySmile.add(new smileManagerHolder(":*(", arrayID.get(9)));
        arraySmile.add(new smileManagerHolder(">:(", arrayID.get(10)));
        arraySmile.add(new smileManagerHolder(":-o", arrayID.get(11)));
        arraySmile.add(new smileManagerHolder(">:|", arrayID.get(12)));
        arraySmile.add(new smileManagerHolder(":'(", arrayID.get(13)));
        arraySmile.add(new smileManagerHolder("%-|", arrayID.get(14)));
        arraySmile.add(new smileManagerHolder("*love*", arrayID.get(15)));
        arraySmile.add(new smileManagerHolder(":*", arrayID.get(16)));
        arraySmile.add(new smileManagerHolder("x0", arrayID.get(17)));
        arraySmile.add(new smileManagerHolder(":-))", arrayID.get(18)));
        arraySmile.add(new smileManagerHolder(":p", arrayID.get(19)));
        arraySmile.add(new smileManagerHolder(">:/", arrayID.get(20)));
        arraySmile.add(new smileManagerHolder(":X", arrayID.get(21)));
        arraySmile.add(new smileManagerHolder("(:)", arrayID.get(22)));
        arraySmile.add(new smileManagerHolder(":-!", arrayID.get(23)));
        arraySmile.add(new smileManagerHolder("dog", arrayID.get(24)));
        arraySmile.add(new smileManagerHolder("fox", arrayID.get(25)));
        arraySmile.add(new smileManagerHolder("giraffe", arrayID.get(26)));
        arraySmile.add(new smileManagerHolder("hamster", arrayID.get(27)));
        arraySmile.add(new smileManagerHolder("koala", arrayID.get(28)));
        arraySmile.add(new smileManagerHolder("lemur", arrayID.get(29)));
        arraySmile.add(new smileManagerHolder("owl", arrayID.get(30)));
        arraySmile.add(new smileManagerHolder("panda", arrayID.get(31)));
        arraySmile.add(new smileManagerHolder("seal", arrayID.get(32)));
        arraySmile.add(new smileManagerHolder("idler", arrayID.get(33)));
        arraySmile.add(new smileManagerHolder(":-)", arrayID.get(0)));
        arraySmile.add(new smileManagerHolder(";-)", arrayID.get(1)));
        arraySmile.add(new smileManagerHolder(":-/", arrayID.get(2)));
        arraySmile.add(new smileManagerHolder("XD", arrayID.get(3)));
        arraySmile.add(new smileManagerHolder(":-D", arrayID.get(4)));
        arraySmile.add(new smileManagerHolder(":-|", arrayID.get(6)));
        arraySmile.add(new smileManagerHolder("8-)", arrayID.get(7)));
        arraySmile.add(new smileManagerHolder(":-(", arrayID.get(8)));
        arraySmile.add(new smileManagerHolder(":-*", arrayID.get(16)));
        arraySmile.add(new smileManagerHolder("x-0", arrayID.get(17)));
        arraySmile.add(new smileManagerHolder(":-p", arrayID.get(19)));
    }

    public void setVisibleSmile(boolean visibleSmile) {
        InputMethodManager imm =
                (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        if (visibleSmile) {
            smileTable.setVisibility(View.VISIBLE);
            keyboard = !visibleSmile;
            smiles = visibleSmile;
            Log.e(TEST, "Smiles = " + smiles + "  ||  Keyboard = " + keyboard);
        } else {
            smileTable.setVisibility(View.GONE);
            keyboard = !visibleSmile;
            smiles = visibleSmile;
            Log.e(TEST, "Smiles = " + smiles + "  ||  Keyboard = " + keyboard);
        }
    }

    public void initSmiles(TableLayout smileTable, EditText editText) {
        int sum;
        this.editText = editText;
        this.smileTable = smileTable;
        for (int i = 0; i < 5; i++) {
            TableRow smileTableRow = new TableRow(context);
            smileTableRow.setGravity(Gravity.CENTER);
            for (int j = 0; j < 8; j++) {
                sum = i * 8 + j + 1;
                if (sum <= smileCount) {
                    ImageView smileImg = new ImageView(context);
                    smileImg.setId(sum * 1000);
                    smileImg.setPadding(5, 5, 5, 5);
                    smileImg.setImageResource(arrayID.get(sum-1));
                    smileTableRow.addView(smileImg);
                    imgList.add(smileImg);
                }
            }
            smileTable.addView(smileTableRow);
        }
        this.editText.setOnKeyListener(key_press);
        this.editText.setOnClickListener(click_edit);
        bindSmilePattern();
        bindSmileListener();
    }

    public void bindSmileListener() {
        for (int i = 0; i < imgList.size(); i++) {
            imgList.get(i).setOnClickListener(smile_click_listener);
        }
    }

    private View.OnClickListener click_edit = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(smiles){
                setVisibleSmile(false);
                Log.e(TEST, "Smiles = " + smiles + "  ||  Keyboard = " + keyboard);
            }
        }
    };

    private View.OnClickListener smile_click_listener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            for (int i = 0; i < imgList.size(); i++) {
                if (v.getId() == imgList.get(i).getId()) {
                    editText.append(arraySmile.get(i).smile);
                    break;
                }
            }
            editText.setText(getSmiledText(context, editText.getText()));
            editText.setSelection(editText.getText().length());
        }
    };

    private View.OnKeyListener key_press = new View.OnKeyListener(){

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK && (smiles || keyboard)) {
                smileTable.setVisibility(View.GONE);
                InputMethodManager imm =
                        (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                smiles = keyboard = false;
                Log.e(TEST, "Smiles = " + smiles + "  ||  Keyboard = " + keyboard);
                return true;
            }
            return false;
        }
    };

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
}
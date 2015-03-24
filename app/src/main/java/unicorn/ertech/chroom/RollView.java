package unicorn.ertech.chroom;

import android.app.Service;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import net.simonvt.numberpicker.NumberPicker;

import java.util.ArrayList;
import java.util.List;

/**
 * Using RollView https://bitbucket.org/msinchevskaya/rollview/
 */
public class RollView extends LinearLayout implements AbsListView.OnScrollListener {
    private final ListView innerListView;
    private RollAdapter mAdapter; //Внутренний адаптер

    private final List<String> listString = new ArrayList<String>();//Список входчщих строк
    private final List<View> listViews = new ArrayList<View>(); //Список View для обновления размера текста
    private final int CENTRAL_TEXT_SIZE = 40; //Размер шрифта центрального элемента
    private int totalElementVisible = 5; //Количество элементов, одновременно видимых на экране

    private int lastFirstVisibleElement; // индекс предыдущего "первого видимого элемента" для определения направления скроллинга

    private int centralIndex; //индекс элемента находящегося в центре

    private final AbsListView.LayoutParams mParams = new AbsListView.LayoutParams(0, 0);
    private int centerLineY;

    public RollView(Context context) {
        super(context);
        LayoutInflater inf = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        innerListView = (ListView) inf.inflate(R.layout.list_rollview, null);
        addView(innerListView);
        innerListView.setOnScrollListener(this);
    }

    public RollView(Context context, AttributeSet attrs){
        super(context, attrs);
        LayoutInflater inf = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        innerListView = (ListView) inf.inflate(R.layout.list_rollview, null);
        addView(innerListView);
        innerListView.setOnScrollListener(this);
    }

    public RollView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        LayoutInflater inf = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        innerListView = (ListView) inf.inflate(R.layout.list_rollview, null);
        addView(innerListView);
        innerListView.setOnScrollListener(this);
    }

    /**
     *
     * @param list - список строк
     */
    public void setList(List<String> list){
        if (list == null)
            throw new NullPointerException("List cannot be null");

        for (String s : list){
            listString.add(s);
        }

        addEmptyElementsToArray();

        mAdapter = new RollAdapter(getContext(), 0, listString);
        innerListView.setAdapter(mAdapter);
        refreshTextViews();
    }
    /**
     *
     * @param number - количество элементов, одновременно видимых на экране
     * По умолчанию видно 5 элементов
     *
     */
    public void setTotalElemevtsVisible(int number){
        totalElementVisible = number;
        addEmptyElementsToArray();
    }

    /**
     *
     * @return индекс текущего элемента без учета вставленных пустых символов
     */
    public int getCurrentItemIndex(){
        return (centralIndex - totalElementVisible / 2);
    }

    /**
     *
     * @return текущее значение центрального элемента списка
     */
    public String getCurrentItemValue(){
        return listString.get(centralIndex);
    }

    private void addEmptyElementsToArray(){
        //Если уже добавлены пустые элементы - удаляем их
        List<String> stringTodelete = new ArrayList<String>();
        for (String s : listString){
            if ("".equals(s))
                stringTodelete.add(s);
        }
        listString.removeAll(stringTodelete);
        //Добавляем в начало и конец списка пустые значение для корректного отображения
        for (int i = 0; i < totalElementVisible / 2; i++){
            listString.add("");
            listString.add(0, "");
        }

        if (mAdapter != null){
            // Обновляем и пересчитываем размеры
            mAdapter.refreshLayoutParams();
            mAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        refreshTextViews();
        //Для определения направления скроллинга
        if (lastFirstVisibleElement > firstVisibleItem){
            Log.i("RollView", "Scroll up");
        }
        else if (lastFirstVisibleElement < firstVisibleItem){
            Log.i("RollView", "Scroll down");
        }
        lastFirstVisibleElement = firstVisibleItem;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        //После отпускания пальца
        if (scrollState == SCROLL_STATE_IDLE){
            //Доводка
            innerListView.smoothScrollToPositionFromTop(centralIndex - totalElementVisible / 2 , 0, 5);
        }
    }

    //Для изменения размера текста при скроллинге
    public void refreshTextViews(){
        float maxTextSize = 0;

        for (View v : listViews){
            int centerOfViewY = v.getBottom() - (mParams.height / 2);
            ShadowTextView tv = (ShadowTextView) v.getTag();
            float coefficient = (Math.abs(centerOfViewY - centerLineY)) / (float)centerLineY;
            float scale = 0;
            //Если коэффициент больше 1 - значит элемент за пределами видимости
            if (coefficient < 1)
                scale = Math.abs(coefficient - 1);
            tv.setAlpha(scale);
            //Определяем элемент с наибольшим размером текста для доводки к нему
            float textSize = CENTRAL_TEXT_SIZE * scale;
            if (textSize > maxTextSize){
                maxTextSize = textSize;
                centralIndex = (Integer) tv.getTag();
            }
            tv.setTextSize(textSize);
            //Перерисовка теней
            tv.redraw();
        }
    }

    private class RollAdapter extends ArrayAdapter<String> {

        private final LayoutInflater mInflater;


        public RollAdapter(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
            mInflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
            refreshLayoutParams();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null){
                convertView = mInflater.inflate(R.layout.roll_view_adapter, null);
                convertView.setLayoutParams(mParams);
            }
            ShadowTextView tv = (ShadowTextView) convertView.findViewById(R.id.textShadow);
            tv.setTag(position); // записываем позицию элемента
            tv.setText(getItem(position));
            convertView.setTag(tv); //записываем ссылку на TextView в тег
            if (!listViews.contains(convertView))
                listViews.add(convertView); // в список для последующего обновления размера текста
            return convertView;
        }

        void refreshLayoutParams(){
            final DisplayMetrics metrics = new DisplayMetrics();
            final WindowManager manager = (WindowManager)(getContext().getSystemService(Service.WINDOW_SERVICE));
            manager.getDefaultDisplay().getMetrics(metrics);

            final RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) RollView.this.getLayoutParams();

            int width = params.width;
            int height = params.height;

            if (width == -1){
                width = metrics.widthPixels;
            }

            if (height == - 1)
                height = metrics.heightPixels;

            mParams.width = width;
            mParams.height = height / totalElementVisible;

            centerLineY = mParams.height * totalElementVisible / 2;
        }

    }
}

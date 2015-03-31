package unicorn.ertech.chroom;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Ильнур on 20.02.2015.
 */
public class BlackListAdapter extends ArrayAdapter<BlackListItem> {
    private List<BlackListItem> items;
    private static Context context;
    public static String deleteId;
    String token;

    public BlackListAdapter(List<BlackListItem> items, Context ctx) {
        super(ctx, R.layout.black_list_layout, items);
        this.items = items;
        this.context = ctx;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        chatHolder holder = new chatHolder();
        // First let's verify the convertView is not null
        if (convertView == null) {
            // This a new view we inflate the new layout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.black_list_layout, null);

            // Now we can fill the layout with the right values
            TextView from = (TextView) v.findViewById(R.id.nameBlack);
            ImageView image = (ImageView)v.findViewById(R.id.imgBlack);
            ImageButton imgButt = (ImageButton)v.findViewById(R.id.deleteFromBlack);

            token=SetBlackList.token;

            holder.imgButton = imgButt;
            holder.img = image;
            holder.tvFrom = from;

            v.setTag(holder);
        }
        else {
            holder = (chatHolder) v.getTag();
        }

       final BlackListItem p = items.get(position);

        holder.imgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteId = p.id;
                openQuitDialog(p.name);
            }
        });

        holder.tvFrom.setText(p.name);
        Picasso.with(getContext()).load(p.avatar).transform(new PicassoRoundTransformation()).fit().into(holder.img);



        return v;
    }

    private void openQuitDialog(String name) {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(
                context);
        quitDialog.setTitle("Хотите удалить "+ name + " из Вашего черного списка?");

        quitDialog.setPositiveButton("Да!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                new remove().execute();
            }
        });

        quitDialog.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        });

        quitDialog.show();
    }

    private class remove extends AsyncTask<String, String, JSONObject> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            //ставим нужные нам параметры
            jParser.setParam("token", token);
            jParser.setParam("action", "list_delete");
            jParser.setParam("list", "2");
            jParser.setParam("deleteid",deleteId);
            // Getting JSON from URL
            JSONObject json = jParser.getJSONFromUrl(Main.URL);
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject json) {
            if(json!=null) {
                String status = null;
                try {
                    status = json.getString("error");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(status.equals("false"))
                {
                    SetBlackList.removeFromList(deleteId);
                    Toast.makeText(context, "Пользователь успешно удален из черного списка!", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(context, "Ошибка при удалении из списка!", Toast.LENGTH_LONG).show();
                }

            }

        }
    }//конец asyncTask
}

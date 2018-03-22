package mg.studio.weatherappdesign;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;


import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    public static String temp = "";
    public static String Address = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkNetwork(this);
        refreshDate();
        new GetLocation().execute();
        new DownloadUpdate().execute();

    }

    public void btnClick(View view) {
        checkNetwork(this);
        refreshDate();
        new GetLocation().execute();
        new DownloadUpdate().execute();

    }

    public static boolean isNetworkAvalible(Context context) {
        // 获得网络状态管理器
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        } else {
            // 建立网络数组
            NetworkInfo[] net_info = connectivityManager.getAllNetworkInfo();

            if (net_info != null) {
                for (int i = 0; i < net_info.length; i++) {
                    // 判断获得的网络状态是否是处于连接状态
                    if (net_info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // 如果没有网络，则弹出网络设置对话框
    public static void checkNetwork(final Activity activity) {
        if (!MainActivity.isNetworkAvalible(activity)) {
            TextView msg = new TextView(activity);
            msg.setText("--No Network Or Internet For Service！");
            new AlertDialog.Builder(activity)
                    .setIcon(R.drawable.backgroud)
                    .setTitle("Network Warning")
                    .setView(msg)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    // 跳转到设置界面
                                    activity.startActivityForResult(new Intent(
                                                    Settings.ACTION_WIRELESS_SETTINGS),
                                            0);
                                }
                            }).create().show();
        }
        return;
    }

    public void refreshDate(){
        Calendar c = Calendar.getInstance();
        String Cdate = String.valueOf(c.get(Calendar.MONTH) + 1) + "/"
                + String.valueOf(c.get(Calendar.DAY_OF_MONTH)) + "/"
                + String.valueOf(c.get(Calendar.YEAR));
        ((TextView) findViewById(R.id.tv_date)).setText(Cdate);
         String week = "";
        int mWay = c.get(Calendar.DAY_OF_WEEK);// 获取当前日期的星期
        switch(mWay){
            case 1:
                week = "SUNDAY";
                break;
            case 2:
                week = "MONDAY";
                break;
            case 3:
                week = "TUESDAY";
                break;
            case 4:
                week = "WEDNESDAY";
                break;
            case 5:
                week = "THUSDAY";
                break;
            case 6:
                week = "FRIDAY";
                break;
            case 7:
                week = "SATURDAY";
                break;
        }
        ((TextView) findViewById(R.id.tv_weekday)).setText(week);
        c.clear();
    }


    private class DownloadUpdate extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {
            String stringUrl = "https://www.sojson.com/open/api/weather/json.shtml?city="+Address;
            HttpURLConnection urlConnection = null;
            BufferedReader reader;

            try {
                URL url = new URL(stringUrl);

                // Create the request to get the information from the server, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Mainly needed for debugging
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                //The temperature

                JSONObject response = new JSONObject(buffer.toString());
                if (response.optInt("status") == 200) {
                    temp = response.getJSONObject("data").optString("wendu");
                    //((TextView) findViewById(R.id.temperature_of_the_day)).setText(temp);
                } else if (response.optInt("status") == 400) {
                    temp="null";
                    //((TextView) findViewById(R.id.temperature_of_the_day)).setText("unknown");
                }
                return temp;

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
        @Override
        protected void onPostExecute(String temp) {

            ((TextView) findViewById(R.id.temperature_of_the_day)).setText(temp);
        }

    }

        private class GetLocation extends AsyncTask<String, Void, String> {


            @Override
            protected String doInBackground(String... strings) {
                String stringUrl = "http://pv.sohu.com/cityjson?ie=utf-8";
                HttpURLConnection urlConnection = null;
                BufferedReader reader;

                try {
                    URL url = new URL(stringUrl);

                    // Create the request to get the information from the server, and open the connection
                    urlConnection = (HttpURLConnection) url.openConnection();

                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    // Read the input stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        // Nothing to do.
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Mainly needed for debugging
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        // Stream was empty.  No point in parsing.
                        return null;
                    }
                    //The temperature
                    String address = buffer.toString();
                    address = address.substring(address.indexOf("cname")+9,address.length()-4);
                        if (address.indexOf("市") != -1) {
                            if (address.indexOf("区") != -1) {
                                Address = address.substring(address.indexOf("市") + 1, address.indexOf("区"));
                            } else
                                Address = address.substring(address.indexOf("省") + 1, address.indexOf("市"));
                        } else
                            Address = address.substring(0, address.indexOf("省"));

                    return address;

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(String address) {

                ((TextView) findViewById(R.id.tv_location)).setText(address);

            }
        }


}

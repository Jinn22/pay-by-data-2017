package com.imperial.jack.pbdappstore;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.os.DpaManager;

// Libraries for installing DPA
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends Activity implements ListItemAdapter.InnerItemOnclickListener,
        OnItemClickListener {

    public static final String TAG = "PbdAppStore";
    private static final String KEY = "pbdappstore_key123";

    protected DpaManager dpamanager;

    protected ListView listview;
    protected List<String> appNameList;
    protected List<String> appIDList;
    protected List<String> appApkIDList;
    protected ListItemAdapter adapter;

    private AlertDialog.Builder builder;

    private String IPAddr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IPAddr = "192.168.0.103:3000";

        //change the web address to the server address
        new GetAllAppsAndSetListViewTask().execute("http://" + IPAddr + "/api/app_infos");

        //show all the expired DPA
        Button exDPABtn = (Button) findViewById(R.id.bt3);
        exDPABtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "expired clicked!");
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Expired DPA");
                //


                final String[] items = {"item1", "item2"};
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(), "You clicked " + items[i], Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setCancelable(true);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //Toast.makeText(getApplicationContext(), "on Item Click" ,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void itemClick(View v) {
        int position = (Integer) v.getTag();
        switch (v.getId()) {
            case R.id.bt1:
                Log.e(TAG, "first button clicked " + position);
                new ShowDPATask().execute("http://" + IPAddr + "/api/app_info/" + appIDList.get(position));
                break;
            case R.id.bt2:
                Log.e(TAG, "second button clicked " + position);
                new InstallDPATask().execute("http://" + IPAddr + "/api/app_info/" + appIDList.get(position));
                new DownloadApkTask().execute("http://" + IPAddr + "/api/get_apk/" + appApkIDList.get(position));
                break;
            default:
                break;
        }
    }

    /*********************************************************************/
    //this task get all the app's name from the server to create the list view
    private class GetAllAppsAndSetListViewTask extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Loading data");
            progressDialog.show();

        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Log.i(TAG, strings[0]);
                return getData(strings[0]);
            } catch (Exception e) {
                return "network error!";
            }
        }

        private String getData(String urlPath) {
            StringBuilder result = new StringBuilder();
            BufferedReader bufferedReader;
            try {
                //Initialize and config request, then connect to server
                URL url = new URL(urlPath);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(50000);
                urlConnection.setConnectTimeout(50000);
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Content-Type", "application/json"); //set header
                Log.i(TAG, "connecting settings are done! ");

                urlConnection.connect();
                Log.i(TAG, "connect!");

                //read data response from server
                InputStream inputStream = urlConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line).append("\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.i(TAG, result);

            appNameList = new ArrayList<>();
            appIDList = new ArrayList<>();
            appApkIDList = new ArrayList<>();

            //get JSON data from result string
            try {
                JSONArray app_infos = new JSONArray(result);
                Log.i(TAG, "get json array...");
                for (int i = 0; i < app_infos.length(); i++) {
                    JSONObject app_info = app_infos.getJSONObject(i);
                    String app_name = app_info.getString("title");
                    appNameList.add(app_name);

                    String app_id = app_info.getString("_id");
                    appIDList.add(app_id);

                    String app_apk = app_info.getString("apk_id");
                    appApkIDList.add(app_apk);
                }

                Log.i(TAG, "get app name done....");

            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
            }

            //Initiate the listview
            listview = (ListView) findViewById(R.id.lv);


            //assign apater to listview
            adapter = new ListItemAdapter(appNameList, MainActivity.this);
            adapter.setOnInnerItemOnClickListener(MainActivity.this);
            listview.setAdapter(adapter);
            listview.setOnItemClickListener(MainActivity.this);

            //cancel progress dialog
            if (progressDialog != null)
                progressDialog.dismiss();
        }


    }

    /*****************************************************************************/
    /*ShowDPATask is used to send GET request to server and show the received DPA*/
    private class ShowDPATask extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Loading data");
            progressDialog.show();
            builder = new AlertDialog.Builder(MainActivity.this);

        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Log.i(TAG, strings[0]);
                return getData(strings[0]);
            } catch (Exception e) {
                return "Network error!";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.i(TAG, result);

            //set AlertDialog
            builder.setIcon(R.mipmap.ic_launcher);
            builder.setTitle("DPA Details");

            //get JSON data from result string
            try {
                JSONObject app_info = new JSONObject(result);
                String app_name = app_info.getString("title");
                int dur = app_info.getInt("Duration");
                JSONObject location = app_info.getJSONObject("Location");

                JSONObject fine_location = location.getJSONObject("finelocation");
                int frequency = fine_location.getInt("Frequency");

                JSONObject identifier = app_info.getJSONObject("Identifier");
                String deviceId = identifier.getString("DeviceId");
                String sim_serial_number = identifier.getString("SimSerialNumber");
                String android_id = identifier.getString("AndroidId");

                JSONObject contact = app_info.getJSONObject("Contact");
                String contacts = contact.getString("contacts");
                String displayName = contact.getString("displayName");

                final String[] Items = {"App name: " + app_name,
                        "DPA would be valid for " + dur + " Days",
                        "Access Location " + frequency + " times a day",
                        "Access DeviceID: " + deviceId,
                        "Access Sim Serial Number: " + sim_serial_number,
                        "Access Android ID: " + android_id,
                        "Access Contacts: " + contacts,
                        "Access displayName: " + displayName};
                builder.setItems(Items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(), "You clicked " + Items[i], Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                Log.i(TAG, "Error when reading JSON object");
            }

            builder.setCancelable(true);
            AlertDialog dialog = builder.create();
            dialog.show();

            //cancel progress dialog
            if (progressDialog != null)
                progressDialog.dismiss();
        }

        private String getData(String urlPath) {
            StringBuilder result = new StringBuilder();
            BufferedReader bufferedReader;
            try {
                //Initialize and config request, then connect to server
                URL url = new URL(urlPath);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(50000);
                urlConnection.setConnectTimeout(50000);
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Content-Type", "application/json"); //set header
                Log.i(TAG, "connection settings are done!");

                urlConnection.connect();
                Log.i(TAG, "connect!");

                //read data response from server
                InputStream inputStream = urlConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line).append("\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result.toString();
        }

    }

    /***********************************************************************/
    //this task download the dpa and install the dpa into the os
    private class InstallDPATask extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Loading data");
            progressDialog.show();
            builder = new AlertDialog.Builder(MainActivity.this);

        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Log.i(TAG, strings[0]);
                return getData(strings[0]);
            } catch (Exception e) {
                return "Network error!";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.i(TAG, result);

            try {
                //Initialize dpa manager and install the DPA of the app the users want
                dpamanager = (DpaManager) getSystemService(Context.DPA_SERVICE);
                Log.i(TAG, "Initialize the dpa manager");

                dpamanager.installDPA(KEY, result);
                Log.i(TAG, "install app's dpa");

            } catch (Exception e) {
                Log.i(TAG, "Error in getting app key or error in calling install DPA");
            }

            //cancel progress dialog
            if (progressDialog != null)
                progressDialog.dismiss();
        }

        private String getData(String urlPath) {
            StringBuilder result = new StringBuilder();
            BufferedReader bufferedReader;

            try {
                //Initialize and config request, then connect to server
                URL url = new URL(urlPath);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(50000);
                urlConnection.setConnectTimeout(50000);
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Content-Type", "application/jdon"); //set header
                Log.i(TAG, "connection settings are done! ");

                urlConnection.connect();
                Log.i(TAG, "connect!");

                //read data response from server
                InputStream inputStream = urlConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line).append("\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result.toString();
        }
    }

    /*************************************************************************/
    //DownloadApkTask downloads the apk and install the apk
    private class DownloadApkTask extends AsyncTask<String, Integer, Boolean> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Downloading...");
            progressDialog.setIndeterminate(true);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);

            progressDialog.setIndeterminate(false);
            progressDialog.setMax(100);
            progressDialog.setProgress(progress[0]);

            String msg;

            if (progress[0] > 99)
                msg = "Finishing.....";
            else
                msg = "Downloading......" + progress[0] + "%";
            progressDialog.setMessage(msg);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            progressDialog.dismiss();

            if (result) {
                Toast.makeText(getApplicationContext(), "Download successfully!",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Error: Try again!",
                        Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected Boolean doInBackground(String... arg0) {
            Boolean flag;
            try {
                //Initialize and config request, then connect to server
                URL url = new URL(arg0[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(50000);
                urlConnection.setConnectTimeout(50000);
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Content-Type", "application/jdon"); //set header
                Log.i(TAG, "connection settings are done! ");

                urlConnection.connect();
                Log.i(TAG, "connect!");

                String PATH = Environment.getExternalStorageDirectory() + "/Download/";
                File file = new File(PATH);
                file.mkdirs();

                File outputFile = new File(file, "downloaded.apk");

                if (outputFile.exists())
                    outputFile.delete();

                InputStream is = urlConnection.getInputStream();

                int totalSize = 915757;

                byte[] buffer = new byte[1024];
                int len1;
                int per;
                int downloaded = 0;

                FileOutputStream fos = new FileOutputStream(outputFile);

                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);
                    downloaded = len1 + downloaded;
                    per = (downloaded * 100 / totalSize);
                    publishProgress(per);
                }
                fos.close();
                is.close();

                OpenNewVersion(PATH);
                flag = true;

            } catch (Exception e) {
                flag = false;
                e.printStackTrace();
            }
            return flag;
        }

        void OpenNewVersion(String location) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(location + "downloaded.apk")),
                    "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}


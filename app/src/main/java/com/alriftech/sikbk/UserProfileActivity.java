package com.alriftech.sikbk;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class UserProfileActivity extends AppCompatActivity {
    Core core;

    private static int RESULT_LOAD_IMAGE = 1;
    private String selectedFilePath;
    private String username;

    SharedPreferences sp;
    EditText txtRealname, txtUsername, txtEmail, txtPhone, txtPassword, txtBirthDate, txtAddress;
    TextView txtGoogle;
    ListView lstPendidikan, lstPekerjaan;
    ImageView imgUser;
    Button btnEditSave;
    ProgressDialog dialog;
    String id_user;

    private boolean isEdit = false;
    private boolean usingGoogle = false;

    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Start Collapsing Action Bar
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("Profil Anda");
        collapsingToolbar.setCollapsedTitleTextColor(ContextCompat.getColor(this, R.color.white));
        collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(this, android.R.color.transparent));
        // End Collapsing Action Bar

        core = new Core(this);

        sp = getSharedPreferences("SIKBK", MODE_PRIVATE);
        id_user = String.valueOf(sp.getInt("id_user", 0));

        txtRealname     = findViewById(R.id.txt_user_detail_realname);
        txtUsername     = findViewById(R.id.txt_user_detail_username);
        txtEmail        = findViewById(R.id.txt_user_detail_email);
        txtPhone        = findViewById(R.id.txt_user_detail_phone);
        txtPassword     = findViewById(R.id.txt_user_detail_password);
        txtBirthDate    = findViewById(R.id.txt_user_detail_birth_date);
        txtAddress      = findViewById(R.id.txt_user_detail_address);
        txtGoogle       = findViewById(R.id.txt_user_detail_google_email);

        lstPendidikan   = findViewById(R.id.lst_pendidikan);
        lstPekerjaan    = findViewById(R.id.lst_pekerjaan);

        imgUser         = findViewById(R.id.img_user);
        btnEditSave     = findViewById(R.id.btn_user_edit_save);

        txtBirthDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    hideKeyboard();

                    Calendar mcurrentTime = Calendar.getInstance();
                    int year = mcurrentTime.get(Calendar.YEAR);
                    int month = mcurrentTime.get(Calendar.MONTH);
                    int date = mcurrentTime.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog mDatePicker;
                    mDatePicker = new DatePickerDialog(UserProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            txtBirthDate.setText(String.format("%04d", year) + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", dayOfMonth));
                        }
                    }, year, month, date);

                    mDatePicker.show();
                }

                return false;
            }
        });

        txtGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!usingGoogle)
                    linkGoogle();
            }
        });

        getUserDetail(id_user);

        // [Google Signin]
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            updateUI(account);
        } catch (ApiException e) {
            Log.w("SikbkLog", "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void linkGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void updateUI(@Nullable GoogleSignInAccount account) {
        if (account != null) {
            dialog = ProgressDialog.show(this, "", "Menghubungkan akun Google", true);

            String g_photoUrl = "";
            if (account.getPhotoUrl() != null) g_photoUrl = account.getPhotoUrl().toString();
            new sendUserGoogleAccountData().execute(id_user, account.getId(), account.getEmail(), g_photoUrl);
        }
    }

    private void hideKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void ubahText(EditText txt, boolean state) {
        if (state) {
            txt.setEnabled(state);
            txt.setTextColor(getColor(R.color.colorAccent));
        }
        else {
            txt.setEnabled(state);
            txt.setTextColor(getColor(R.color.gray));
        }
    }

    public void editData(View v) {
        if (!isEdit) {
            setTitle(R.string.act_user_details_edit);

            ubahText(txtRealname, true);
//            ubahText(txtUsername, true);
            ubahText(txtEmail, true);
            ubahText(txtPhone, true);
//            ubahText(txtPassword, true);
            ubahText(txtBirthDate, true);
            ubahText(txtAddress, true);

            btnEditSave.setText(R.string.btn_simpan);
            isEdit = true;
        }
        else {
            setTitle(R.string.act_user_details);

            ubahText(txtRealname, false);
//            ubahText(txtUsername, false);
            ubahText(txtEmail, false);
            ubahText(txtPhone, false);
//            ubahText(txtPassword, false);
            ubahText(txtBirthDate, false);
            ubahText(txtAddress, false);

            dialog = ProgressDialog.show(this, "", "Menyimpan perubahan", true);
            new sendUserData().execute(id_user,
                    txtRealname.getText().toString(),
                    txtUsername.getText().toString(),
                    txtEmail.getText().toString(),
                    txtPassword.getText().toString(),
                    txtBirthDate.getText().toString(),
                    txtAddress.getText().toString(),
                    txtPhone.getText().toString());

            btnEditSave.setText(R.string.btn_edit_profil);
            isEdit = false;
        }
    }

    public void editDataPendidikan(View v) {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.dialog_tambah_pendidikan, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);

        final EditText userInput = promptsView.findViewById(R.id.txt_pendidikan_nama);
        final Spinner dropJenis = promptsView.findViewById(R.id.spin_pendidikan);
        final Spinner dropTahun = promptsView.findViewById(R.id.spin_tahun);

        String[] jenis = new String[] {"SD", "SMP", "SMA", "PT"};

        ArrayAdapter<String> adapJenis = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, jenis);
        adapJenis.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropJenis.setAdapter(adapJenis);

        List<String> tahun_list = new ArrayList<>();
        int year = core.getYear();

        for (int i = year; i >= (year - 20); i--)
            tahun_list.add(String.valueOf(i));

        String[] tahun = new String[ tahun_list.size() ];
        tahun_list.toArray(tahun);

        ArrayAdapter<String> adapTahun = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tahun);
        adapTahun.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropTahun.setAdapter(adapTahun);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                getWindow().setSoftInputMode(
                                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                                );

                                if (userInput.getText().toString().isEmpty())
                                    Snackbar.make(findViewById(R.id.layout_user_profile), "Terjadi kesalahan. Data tidak boleh kosong", Snackbar.LENGTH_LONG).show();

                                else
                                    new sendPendidikanData().execute(id_user,
                                        dropJenis.getSelectedItem().toString(),
                                        userInput.getText().toString(),
                                        dropTahun.getSelectedItem().toString());
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void browseImage(View v) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode != RC_SIGN_IN) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                selectedFilePath = FilePath.getPath(this, imageUri);

                imgUser.setImageBitmap(selectedImage);
                imgUser.setScaleType(ImageView.ScaleType.CENTER_CROP);

                dialog = ProgressDialog.show(this,"","Mengupload foto ...",true);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        uploadFile(selectedFilePath);
                    }
                }).start();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Terjadi kesalahan!", Toast.LENGTH_LONG).show();
            }

        }

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void getUserDetail(String id) {
        dialog = ProgressDialog.show(this, "", "Mendapatkan informasi", true);

        new getUserData().execute(id);
        new getUserPendidikan().execute(id);
        new getUserPekerjaan().execute(id);
    }

    private class getUserData extends AsyncTask<String,String,String> {

        JSONObject jobj = null;

        @Override
        protected String doInBackground(String... params) {
            JSONParser jsonparser = new JSONParser();

            String url = core.API("user_details/" + params[0]);
            jobj = jsonparser.makeHttpRequest(url);

            return jobj.toString();
        }

        protected void onPostExecute(String json){
            try {
                dialog.dismiss();

                JSONObject jobj = new JSONObject(json);
                JSONArray jdata = jobj.getJSONArray("data");

                for (int i = 0; i < jdata.length(); i++) {
                    JSONObject c = jdata.getJSONObject(i);

                    txtRealname.setText(c.getString("realname"));
                    txtUsername.setText(c.getString("username")); username = c.getString("username");
                    txtEmail.setText(c.getString("email"));
                    txtPhone.setText(c.getString("nomor_hp"));
                    txtPassword.setText("password");
                    txtBirthDate.setText(c.getString("tanggal_lahir"));
                    txtAddress.setText(c.getString("alamat"));

                    if (c.isNull("g_id") == true) {
                        txtGoogle.setText("Ketuk untuk menghubungkan");
                        txtGoogle.setEnabled(true);
                    }
                    else {
                        usingGoogle = true;

                        txtGoogle.setText("Terhubung: " + c.getString("g_email"));
                        findViewById(R.id.img_user_browse).setVisibility(View.GONE);
                    }

                    String url;

                    if (usingGoogle && !c.getString("g_profile").equals(""))
                        url = c.getString("g_profile");
                    else
                        url = getString(R.string.ASSETS_URL) + "profiles/" + c.getString("profile_image");

                    Picasso.with(UserProfileActivity.this).invalidate(url);
                    Picasso.with(UserProfileActivity.this).load(url).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(imgUser);
                    imgUser.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class sendUserData extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... params) {

            String reg_url = core.API("user_update/" + params[0]);
            String text = "";

            String realname = params[1];
            String username = params[2];
            String email    = params[3];
            String password = params[4];
            String birth    = params[5];
            String address  = params[6];
            String no_hp    = params[7];

            try {
                URL url = new URL(reg_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream os = httpURLConnection.getOutputStream();

                String data = URLEncoder.encode("realname", "UTF-8") + "=" + URLEncoder.encode(realname, "UTF-8") + "&" +
                        URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8") + "&" +
                        URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8") + "&" +
//                        URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8") + "&" +
                        URLEncoder.encode("tanggal_lahir", "UTF-8") + "=" + URLEncoder.encode(birth, "UTF-8") + "&" +
                        URLEncoder.encode("alamat", "UTF-8") + "=" + URLEncoder.encode(address, "UTF-8") + "&" +
                        URLEncoder.encode("nomor_hp", "UTF-8") + "=" + URLEncoder.encode(no_hp, "UTF-8");

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                bufferedWriter.write(data);
                bufferedWriter.flush();

                int statusCode = httpURLConnection.getResponseCode();
                if (statusCode == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null)
                        sb.append(line).append("\n");

                    text = sb.toString();
                    bufferedWriter.close();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return text;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject obj = new JSONObject(result);

                dialog.dismiss();
                Snackbar.make(findViewById(R.id.layout_user_profile), obj.getString("data"), Snackbar.LENGTH_LONG).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recreate();
                    }
                }, 2000L);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    private class sendPendidikanData extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... params) {

            String reg_url = core.API("user_pendidikan_tambah");
            String text = "";

            String id_user      = params[0];
            String jenis        = params[1];
            String nama_pend    = params[2];
            String tahun_lulus  = params[3];

            try {
                URL url = new URL(reg_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream os = httpURLConnection.getOutputStream();

                String data = URLEncoder.encode("id_user", "UTF-8") + "=" + URLEncoder.encode(id_user, "UTF-8") + "&" +
                        URLEncoder.encode("jenis", "UTF-8") + "=" + URLEncoder.encode(jenis, "UTF-8") + "&" +
                        URLEncoder.encode("nama_pendidikan", "UTF-8") + "=" + URLEncoder.encode(nama_pend, "UTF-8") + "&" +
                        URLEncoder.encode("tahun_lulus", "UTF-8") + "=" + URLEncoder.encode(tahun_lulus, "UTF-8");

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                bufferedWriter.write(data);
                bufferedWriter.flush();

                int statusCode = httpURLConnection.getResponseCode();
                if (statusCode == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null)
                        sb.append(line).append("\n");

                    text = sb.toString();
                    bufferedWriter.close();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return text;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();

            try {
                JSONObject obj = new JSONObject(result);
                Snackbar.make(findViewById(R.id.layout_user_profile), obj.getString("data"), Snackbar.LENGTH_LONG).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recreate();
                    }
                }, 2000L);

                if (obj.getString("status").equals("success")) {

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    private class sendUserGoogleAccountData extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... params) {

            String reg_url = core.API("user_link_google/" + params[0]);
            String text = "";

            String g_id       = params[1];
            String g_email    = params[2];
            String g_profile  = params[3];

            try {
                URL url = new URL(reg_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream os = httpURLConnection.getOutputStream();

                String data = URLEncoder.encode("g_id", "UTF-8") + "=" + URLEncoder.encode(g_id, "UTF-8") + "&" +
                        URLEncoder.encode("g_email", "UTF-8") + "=" + URLEncoder.encode(g_email, "UTF-8") + "&" +
                        URLEncoder.encode("g_profile", "UTF-8") + "=" + URLEncoder.encode(g_profile, "UTF-8");

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                bufferedWriter.write(data);
                bufferedWriter.flush();

                int statusCode = httpURLConnection.getResponseCode();
                if (statusCode == 200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null)
                        sb.append(line).append("\n");

                    text = sb.toString();
                    bufferedWriter.close();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return text;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();

            try {
                JSONObject obj = new JSONObject(result);
                Snackbar.make(findViewById(R.id.layout_user_profile), obj.getString("data"), Snackbar.LENGTH_LONG).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recreate();
                    }
                }, 2000L);

                if (obj.getString("status").equals("success")) {

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mGoogleSignInClient.signOut();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    private static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    private int uploadFile(final String selectedFilePath){

        int serverResponseCode = 0;

        HttpURLConnection connection;
        DataOutputStream dataOutputStream;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";


        int bytesRead,bytesAvailable,bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File selectedFile = new File(selectedFilePath);


        String[] parts = selectedFilePath.split("/");
        final String fileName = parts[parts.length - 1];

        if (!selectedFile.isFile()) {
            dialog.dismiss();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Snackbar.make(findViewById(R.id.layout_user_profile), "Sumber tidak ditemukan!", Snackbar.LENGTH_LONG).show();
                }
            });
            return 0;
        }
        else {
            try {
                FileInputStream fileInputStream = new FileInputStream(selectedFile);
                URL url = new URL(core.API("user_image"));

                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);//Allow Inputs
                connection.setDoOutput(true);//Allow Outputs
                connection.setUseCaches(false);//Don't use a cached Copy
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                connection.setRequestProperty("username", username);
                connection.setRequestProperty("uploaded_file", selectedFilePath);

                //creating new dataoutputstream
                dataOutputStream = new DataOutputStream(connection.getOutputStream());

                //writing bytes to data outputstream
                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);

                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"username\"" + lineEnd + lineEnd); // param name
                dataOutputStream.writeBytes(username + lineEnd + twoHyphens + boundary + lineEnd); // param value

                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\"; filename=\""
                        + selectedFilePath + "\"" + lineEnd);

                dataOutputStream.writeBytes(lineEnd);

                //returns no. of bytes present in fileInputStream
                bytesAvailable = fileInputStream.available();
                //selecting the buffer size as minimum of available bytes or 1 MB
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                //setting the buffer as byte array of size of bufferSize
                buffer = new byte[bufferSize];

                //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
                bytesRead = fileInputStream.read(buffer,0,bufferSize);

                //loop repeats till bytesRead = -1, i.e., no bytes are left to read
                while (bytesRead > 0){
                    //write the bytes read from inputstream
                    dataOutputStream.write(buffer,0,bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable,maxBufferSize);
                    bytesRead = fileInputStream.read(buffer,0,bufferSize);
                }

                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                serverResponseCode = connection.getResponseCode();
                String serverResponseMessage = connection.getResponseMessage();

                //response code of 200 indicates the server status OK
                if(serverResponseCode == 200) {
                    InputStream response = connection.getInputStream();
                    final String reply = convertStreamToString(response);
//                    Log.d("reply", reply);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(findViewById(R.id.layout_user_profile), "Gambar berhasil diupload!", Snackbar.LENGTH_LONG).show();
                        }
                    });
                }
                //closing the input and output streams
                fileInputStream.close();
                dataOutputStream.flush();
                dataOutputStream.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        Snackbar.make(findViewById(R.id.layout_user_profile),"Berkas tidak ditemukan!", Snackbar.LENGTH_LONG).show();
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();
            }

            dialog.dismiss();
            return serverResponseCode;
        }
    }

    private class getUserPendidikan extends AsyncTask<String,String,String> {

        JSONObject jobj = null;

        @Override
        protected String doInBackground(String... params) {
            JSONParser jsonparser = new JSONParser();

            String url = core.API("user_pendidikan/" + params[0]);
            jobj = jsonparser.makeHttpRequest(url);

            return jobj.toString();
        }

        protected void onPostExecute(String json){
            try {
                dialog.dismiss();

                JSONObject jobj = new JSONObject(json);
                JSONArray jdata = jobj.getJSONArray("data");

                ArrayList<HashMap<String, String>> mylist = new ArrayList<>();

                for (int i = 0; i < jdata.length(); i++) {
                    JSONObject c = jdata.getJSONObject(i);

                    HashMap<String,String> hashMap = new HashMap<>();
                    hashMap.put("jenis", c.getString("jenis"));
                    hashMap.put("nama", c.getString("nama_pendidikan"));
                    hashMap.put("tahun", "Lulus " + c.getString("tahun_lulus"));
                    mylist.add(hashMap);
                }

                SimpleAdapter sa = new SimpleAdapter(UserProfileActivity.this, mylist, R.layout.lst_row_pendidikan,
                        new String[] {"jenis", "nama", "tahun"}, new int[] {R.id.row_pendidikan_jenis, R.id.row_pendidikan_nama, R.id.row_pendidikan_tahun});
                lstPendidikan.setAdapter(sa);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            core.autoSizeListview(lstPendidikan, 20);
        }
    }

    private class getUserPekerjaan extends AsyncTask<String,String,String> {

        JSONObject jobj = null;

        @Override
        protected String doInBackground(String... params) {
            JSONParser jsonparser = new JSONParser();

            String url = core.API("user_pekerjaan/" + params[0]);
            jobj = jsonparser.makeHttpRequest(url);

            return jobj.toString();
        }

        protected void onPostExecute(String json){
            try {
                dialog.dismiss();

                JSONObject jobj = new JSONObject(json);
                JSONArray jdata = jobj.getJSONArray("data");

                ArrayList<HashMap<String, String>> mylist = new ArrayList<>();

                for (int i = 0; i < jdata.length(); i++) {
                    JSONObject c = jdata.getJSONObject(i);

                    HashMap<String,String> hashMap = new HashMap<>();
                    hashMap.put("jabatan", c.getString("nama_pekerjaan"));
                    hashMap.put("kantor", c.getString("instansi"));
                    hashMap.put("tahun", c.getString("tahun_dari") + " - " + c.getString("tahun_sampai"));
                    mylist.add(hashMap);
                }

                SimpleAdapter sa = new SimpleAdapter(UserProfileActivity.this, mylist, R.layout.lst_row_pekerjaan,
                        new String[] {"jabatan", "kantor", "tahun"}, new int[] {R.id.row_pekerjaan_jabatan, R.id.row_pekerjaan_kantor, R.id.row_pekerjaan_tahun});
                lstPekerjaan.setAdapter(sa);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            core.autoSizeListview(lstPekerjaan, 20);
        }
    }
}

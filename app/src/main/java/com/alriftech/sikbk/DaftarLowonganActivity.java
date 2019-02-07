package com.alriftech.sikbk;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class DaftarLowonganActivity extends AppCompatActivity {
    Core core;

    private int syarat_1, syarat_2, syarat_3, syarat_4;
    private String doc_1, doc_2, doc_3, doc_4, id_user, id_lowongan;

    SharedPreferences sp;
    ImageView img_syarat_1, img_syarat_2, img_syarat_3, img_syarat_4;
    TextView txt_syarat_1, txt_syarat_2, txt_syarat_3, txt_syarat_4, txtPesan;
    TextView txtBidang, txtDeskripsi;
    ProgressDialog dialog;

    String[] mimeTypes =
            {"application/msword","application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                    "application/pdf"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_lowongan);

        core = new Core(this);
        Bundle i = getIntent().getExtras();

        doc_1 = doc_2 = doc_3 = doc_4 = "";

        img_syarat_1    = findViewById(R.id.img_lam_syarat_1);
        img_syarat_2    = findViewById(R.id.img_lam_syarat_2);
        img_syarat_3    = findViewById(R.id.img_lam_syarat_3);
        img_syarat_4    = findViewById(R.id.img_lam_syarat_4);

        txt_syarat_1    = findViewById(R.id.txt_lam_syarat_1);
        txt_syarat_2    = findViewById(R.id.txt_lam_syarat_2);
        txt_syarat_3    = findViewById(R.id.txt_lam_syarat_3);
        txt_syarat_4    = findViewById(R.id.txt_lam_syarat_4);

        txtPesan        = findViewById(R.id.txt_lam_pesan);

        txtBidang       = findViewById(R.id.txt_lam_bidang);
        txtDeskripsi    = findViewById(R.id.txt_lam_deskripsi);

        txtBidang.setText(i.getString("low_bidang"));
        txtDeskripsi.setText(i.getString("low_deskripsi"));

        syarat_1 = i.getInt("low_syarat_1");
        syarat_2 = i.getInt("low_syarat_2");
        syarat_3 = i.getInt("low_syarat_3");
        syarat_4 = i.getInt("low_syarat_4");

        setDokumen(1, syarat_1);
        setDokumen(2, syarat_2);
        setDokumen(3, syarat_3);
        setDokumen(4, syarat_4);

        sp = getSharedPreferences("SIKBK", MODE_PRIVATE);
        id_user = String.valueOf(sp.getInt("id_user", 0));
        id_lowongan = String.valueOf(sp.getInt("id_lowongan", 0));
    }

    private void setDokumen(int id, int nilai) {
        if (nilai == 1) {
            switch (id) {
                case 1:
                    img_syarat_1.setImageResource(R.drawable.ic_insert_drive_file_ancent_24dp);
                    txt_syarat_1.setTextColor(getColor(R.color.colorAccent));
                    break;

                case 2:
                    img_syarat_2.setImageResource(R.drawable.ic_insert_drive_file_ancent_24dp);
                    txt_syarat_2.setTextColor(getColor(R.color.colorAccent));
                    break;

                case 3:
                    img_syarat_3.setImageResource(R.drawable.ic_insert_drive_file_ancent_24dp);
                    txt_syarat_3.setTextColor(getColor(R.color.colorAccent));
                    break;

                case 4:
                    img_syarat_4.setImageResource(R.drawable.ic_insert_drive_file_ancent_24dp);
                    txt_syarat_4.setTextColor(getColor(R.color.colorAccent));
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            final Uri fileUri = data.getData();
            final String filePath = FilePath.getRealPathFromURI_API19(this, fileUri);

            Log.d("SikbkLog", fileUri.toString());
            Log.d("SikbkLog", filePath);

            dialog = ProgressDialog.show(this,"","Mengunggah dokumen", true);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    uploadFile(filePath, String.valueOf(requestCode));
                }
            }).start();

            switch (requestCode) {
                case 1:
                    doc_1 = filePath;
                    img_syarat_1.setImageResource(R.drawable.ic_insert_drive_file_ok_24dp);
                    break;

                case 2:
                    doc_2 = filePath;
                    img_syarat_2.setImageResource(R.drawable.ic_insert_drive_file_ok_24dp);
                    break;

                case 3:
                    doc_3 = filePath;
                    img_syarat_3.setImageResource(R.drawable.ic_insert_drive_file_ok_24dp);
                    break;

                case 4:
                    doc_4 = filePath;
                    img_syarat_4.setImageResource(R.drawable.ic_insert_drive_file_ok_24dp);
                    break;
            }
        }
    }

    public void doc1(View v) {
        if (syarat_1 == 1) {
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);

            i.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0)
                i.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

            startActivityForResult(i, 1);
        }
    }

    public void doc2(View v) {
        if (syarat_2 == 1) {
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);

            i.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0)
                i.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

            startActivityForResult(i, 2);
        }
    }

    public void doc3(View v) {
        if (syarat_3 == 1) {
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);

            i.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0)
                i.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

            startActivityForResult(i, 3);
        }
    }

    public void doc4(View v) {
        if (syarat_4 == 1) {
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);

            i.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0)
                i.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

            startActivityForResult(i, 4);
        }
    }

    public void daftarLowongan(View v) {
        boolean komplit = false;

        if (syarat_1 == 1) {
            if (!core.isNullOrEmpty(doc_1))
                komplit = true;
            else
                komplit = false;
        }

        if (syarat_2 == 1) {
            if (!core.isNullOrEmpty(doc_2))
                komplit = true;
            else
                komplit = false;
        }

        if (syarat_1 == 3) {
            if (!core.isNullOrEmpty(doc_3))
                komplit = true;
            else
                komplit = false;
        }

        if (syarat_4 == 1) {
            if (!core.isNullOrEmpty(doc_4))
                komplit = true;
            else
                komplit = false;
        }

        if (komplit == false)
            Snackbar.make(findViewById(R.id.layout_daftar_lowongan), R.string.st_data_tidak_lengkap, Snackbar.LENGTH_LONG).show();
        else {
            new AlertDialog.Builder(DaftarLowonganActivity.this, R.style.Sikbk_Dialog)
                    .setTitle(R.string.t_konfirmasi)
                    .setMessage(R.string.b_konfirmasi_lamaran_kirim)
                    .setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int button) {
                            Log.d("SikbkLog", "Document 1: " + doc_1);
                            Log.d("SikbkLog", "Document 2: " + doc_2);
                            Log.d("SikbkLog", "Document 3: " + doc_3);
                            Log.d("SikbkLog", "Document 4: " + doc_4);

                            new sendLowonganData().execute(id_user, id_lowongan, txtPesan.getText().toString(), doc_1, doc_2, doc_3, doc_4);
                        }
                    })
                    .setNegativeButton(R.string.btn_no, null).show();
        }
    }

    private class sendLowonganData extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... params) {

            String reg_url = core.API("user_lowongan_daftar");
            String text = "";

            String id_user      = params[0];
            String id_lowongan  = params[1];
            String pesan        = params[2];
            String doc1         = params[3];
            String doc2         = params[4];
            String doc3         = params[5];
            String doc4         = params[6];

            try {
                URL url = new URL(reg_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream os = httpURLConnection.getOutputStream();

                String data = URLEncoder.encode("id_user", "UTF-8") + "=" + URLEncoder.encode(id_user, "UTF-8") + "&" +
                        URLEncoder.encode("id_lowongan", "UTF-8") + "=" + URLEncoder.encode(id_lowongan, "UTF-8") + "&" +
                        URLEncoder.encode("ijazah", "UTF-8") + "=" + URLEncoder.encode(doc1, "UTF-8") + "&" +
                        URLEncoder.encode("transkrip", "UTF-8") + "=" + URLEncoder.encode(doc2, "UTF-8") + "&" +
                        URLEncoder.encode("skck", "UTF-8") + "=" + URLEncoder.encode(doc3, "UTF-8") + "&" +
                        URLEncoder.encode("skkb", "UTF-8") + "=" + URLEncoder.encode(doc4, "UTF-8") + "&" +
                        URLEncoder.encode("pesan", "UTF-8") + "=" + URLEncoder.encode(pesan, "UTF-8");

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
                Snackbar.make(findViewById(R.id.layout_daftar_lowongan), obj.getString("data"), Snackbar.LENGTH_LONG).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
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

    private int uploadFile(final String selectedFilePath, final String id_syarat){
        HttpURLConnection connection;
        DataOutputStream dataOutputStream;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        int serverResponseCode = 0;
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
                    Snackbar.make(findViewById(R.id.layout_daftar_lowongan), "Sumber tidak ditemukan!", Snackbar.LENGTH_LONG).show();
                }
            });

            return 0;
        }
        else {
            try {
                FileInputStream fileInputStream = new FileInputStream(selectedFile);
                URL url = new URL(core.API("user_doc"));

                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);//Allow Inputs
                connection.setDoOutput(true);//Allow Outputs
                connection.setUseCaches(false);//Don't use a cached Copy
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                connection.setRequestProperty("id_user", id_user);
                connection.setRequestProperty("id_syarat", id_syarat);
                connection.setRequestProperty("id_lowongan", id_lowongan);
                connection.setRequestProperty("doc_file", selectedFilePath);

                //creating new dataoutputstream
                dataOutputStream = new DataOutputStream(connection.getOutputStream());

                //writing bytes to data outputstream
                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);

                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"id_user\"" + lineEnd + lineEnd); // param name
                dataOutputStream.writeBytes(id_user + lineEnd + twoHyphens + boundary + lineEnd); // param value

                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"id_syarat\"" + lineEnd + lineEnd); // param name
                dataOutputStream.writeBytes(id_syarat + lineEnd + twoHyphens + boundary + lineEnd); // param value

                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"id_lowongan\"" + lineEnd + lineEnd); // param name
                dataOutputStream.writeBytes(id_lowongan + lineEnd + twoHyphens + boundary + lineEnd); // param value

                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"doc_file\"; filename=\""
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

                    if (id_syarat.equals("1"))
                        doc_1 = convertStreamToString(response);
                    else if (id_syarat.equals("2"))
                        doc_2 = convertStreamToString(response);
                    else if (id_syarat.equals("3"))
                        doc_3 = convertStreamToString(response);
                    else if (id_syarat.equals("4"))
                        doc_4 = convertStreamToString(response);

//                    Log.d("reply", reply);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(findViewById(R.id.layout_daftar_lowongan), "File telah berhasil diupload!", Snackbar.LENGTH_LONG).show();
                        }
                    });
                }
                //closing the input and output streams
                fileInputStream.close();
                dataOutputStream.flush();
                dataOutputStream.close();

            } catch (FileNotFoundException e) {
//                e.printStackTrace();
                Log.d("SikbkLog", e.getMessage());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(findViewById(R.id.layout_daftar_lowongan),"Berkas tidak ditemukan!", Snackbar.LENGTH_LONG).show();
                    }
                });
            } catch (MalformedURLException e) {
//                e.printStackTrace();
                Log.d("SikbkLog", e.getMessage());

            } catch (IOException e) {
//                e.printStackTrace();
                Log.d("SikbkLog", e.getMessage());
            }

            dialog.dismiss();
            return serverResponseCode;
        }
    }
}
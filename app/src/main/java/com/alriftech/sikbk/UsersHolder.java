package com.alriftech.sikbk;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class UsersHolder extends RecyclerView.ViewHolder {
    Core core;
    View rv;
    private TextView txtNama, txtEmail, txtUsername, txtStatus, txtAlamat;

    public UsersHolder(View itemView) {
        super(itemView);

        this.txtNama        = itemView.findViewById(R.id.txt_list_users_nama);
        this.txtEmail       = itemView.findViewById(R.id.txt_list_users_email);
        this.txtUsername    = itemView.findViewById(R.id.txt_list_users_username);
        this.txtAlamat      = itemView.findViewById(R.id.txt_list_users_alamat);
        this.txtStatus      = itemView.findViewById(R.id.txt_list_users_status);

        rv = itemView.getRootView();
        core = new Core(rv.getContext());
    }

    private void setStatus(TextView txt, int status) {
        Drawable dw = txt.getBackground();

        if (status == 1) {
            txt.setText("Aktif");
            dw.setTint(Color.parseColor("#4CAF50"));
        }
        else if (status == 2) {
            txt.setText("Tidak Aktif");
            dw.setTint(Color.parseColor("#bf3f3f"));
        }
    }

    public void setDetails(final Users users) {
        txtNama.setText(users.getRealname());
        txtEmail.setText(users.getEmail());
        txtUsername.setText(users.getUsername());
        txtAlamat.setText(users.getAlamat());

        if (!users.isAnggota()) {
            setStatus(txtStatus, users.getStatus());

            if (users.getLevel() == 2 && users.getStatus() == 1) {
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        new AlertDialog.Builder(rv.getContext(), R.style.Sikbk_Dialog)
                                .setTitle(R.string.t_konfirmasi)
                                .setMessage("Apakah Anda yakin ingin menghapus ketua ini?")
                                .setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int button) {
                                        new hapusUser().execute(String.valueOf(users.getId()));
                                    }
                                })
                                .setNegativeButton(R.string.btn_no, null).show();
                        return false;
                    }
                });
            }
            else if (users.getStatus() == 2) {
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        new AlertDialog.Builder(rv.getContext(), R.style.Sikbk_Dialog)
                                .setTitle(R.string.t_konfirmasi)
                                .setMessage("Apakah Anda yakin ingin konfirmasi anggota ini?")
                                .setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int button) {
                                        new konfirmasiAnggota().execute(String.valueOf(users.getId()));
                                    }
                                })
                                .setNegativeButton(R.string.btn_no, null).show();
                        return false;
                    }
                });
            }
        }
        else {
            txtStatus.setVisibility(View.INVISIBLE);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(rv.getContext(), DetailUsersActivity.class);

                    i.putExtra("id_user", users.getId());
                    i.putExtra("username", users.getUsername());
                    i.putExtra("email", users.getEmail());
                    i.putExtra("nomor_hp", users.getNomor_hp());
                    i.putExtra("realname", users.getRealname());
                    i.putExtra("tanggal_lahir", users.getTanggal_lahir());
                    i.putExtra("jenis_kelamin", users.getJenis_kelamin());
                    i.putExtra("alamat", users.getAlamat());
                    i.putExtra("profile_image", users.getProfile_image());

                    rv.getContext().startActivity(i);
                }
            });
        }
    }

    private class konfirmasiAnggota extends AsyncTask<String,String,String> {

        JSONObject jobj = null;

        @Override
        protected String doInBackground(String... params) {
            JSONParser jsonparser = new JSONParser();

            String url = core.API("anggota_konfirmasi/" + params[0]);
            jobj = jsonparser.makeHttpRequest(url);

            return jobj.toString();
        }

        protected void onPostExecute(String json){
            try {
                JSONObject jobj = new JSONObject(json);

                if (jobj.getString("status").equals("success")) {
                    Snackbar.make(itemView.getRootView().findViewById(R.id.layout_dashboard), jobj.getString("data"), Snackbar.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class hapusUser extends AsyncTask<String,String,String> {

        JSONObject jobj = null;

        @Override
        protected String doInBackground(String... params) {
            JSONParser jsonparser = new JSONParser();

            String url = core.API("user_delete/" + params[0]);
            jobj = jsonparser.makeHttpRequest(url);

            return jobj.toString();
        }

        protected void onPostExecute(String json){
            try {
                JSONObject jobj = new JSONObject(json);

                if (jobj.getString("status").equals("success")) {
                    Snackbar.make(itemView.getRootView().findViewById(R.id.layout_dashboard), jobj.getString("data"), Snackbar.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

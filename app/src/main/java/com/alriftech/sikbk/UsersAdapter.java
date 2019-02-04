package com.alriftech.sikbk;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UsersHolder> {
    private Context context;
    private ArrayList<Users> users;

    public UsersAdapter(Context context, ArrayList<Users> users) {
        this.context = context;
        this.users = users;
    }

    @Override
    public UsersHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_row_users, parent, false);
        return new UsersHolder(view);
    }

    @Override
    public void onBindViewHolder(UsersHolder holder, int position) {
        Users users2 = users.get(position);
        holder.setDetails(users2);

    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}

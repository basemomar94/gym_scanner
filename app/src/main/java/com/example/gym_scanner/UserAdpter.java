package com.example.gym_scanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserAdpter  extends RecyclerView.Adapter<UserAdpter.ViewHolder> {


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView user,id;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            user=itemView.findViewById(R.id.user_name);
            id=itemView.findViewById(R.id.user_id);
            imageView=itemView.findViewById(R.id.userimage);
        }
    }

    private Context context;
    private List<User>users;


    public UserAdpter (Context c, List<User>userList){
        this.context=c;
        users=userList;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
     View v =   LayoutInflater.from(context).inflate(R.layout.user_item,parent,false);

        return new  ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        User u = users.get(position);
        holder.id.setText(u.getId());
        holder.user.setText(u.getName());
        holder.imageView.setImageResource(R.drawable.user1);


    }

    @Override
    public int getItemCount() {
        return users.size();
    }


}

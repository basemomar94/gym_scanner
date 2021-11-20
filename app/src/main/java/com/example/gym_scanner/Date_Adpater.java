package com.example.gym_scanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Date_Adpater  extends RecyclerView.Adapter<Date_Adpater.ViewHolder> {
    List <date_item> Date_list;

    public Date_Adpater(List<date_item>date_list){
        this.Date_list=date_list;
    }

    @NonNull
    @Override
    public Date_Adpater.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.date_item,parent,false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Date_Adpater.ViewHolder holder, int position) {

        String date = Date_list.get(position).getDate();
        String time = Date_list.get(position).getTime();
        String admin = Date_list.get(position).getAdmin();
        holder.date.setText(date);
        holder.time.setText(time);
        holder.admin.setText(admin);


    }

    @Override
    public int getItemCount() {
        return Date_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView time;
        TextView admin;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date=itemView.findViewById(R.id.date_item);
            time=itemView.findViewById(R.id.time_item);
            admin=itemView.findViewById(R.id.admin_item);
        }
    }
}

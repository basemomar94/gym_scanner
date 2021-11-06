package com.example.gym_scanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class Firebase_Adapter_dates  extends FirestoreRecyclerAdapter<date_item,Firebase_Adapter_dates.ViewHolder> {
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public Firebase_Adapter_dates(@NonNull FirestoreRecyclerOptions<date_item> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull date_item model) {

        holder.admin.setText(model.getAdmin());
        holder.date.setText(model.getDate());

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.date_item,parent,false);
        return new  ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView date, admin;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date =itemView.findViewById(R.id.date_date);
            admin =itemView.findViewById(R.id.admin_date);
        }
    }
}

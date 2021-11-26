package com.example.gym_scanner;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class Firebase_All extends FirestoreRecyclerAdapter<All_item,Firebase_All.ViewHolder> {
    private Firebase_Adapter_users.OnitemClick listener;
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public Firebase_All(@NonNull FirestoreRecyclerOptions<All_item> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull All_item model) {
        holder.fname.setText(model.getFname());
        holder.lname.setText(model.getLname());
        holder.userid.setText(model.getPhone());
        holder.mail.setText(model.getMail());

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_users,parent,false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView fname,lname,userid,mail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fname=itemView.findViewById(R.id.fname);
            lname=itemView.findViewById(R.id.lname);
            userid=itemView.findViewById(R.id.phone_n);
            mail=itemView.findViewById(R.id.mail_n);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position!=RecyclerView.NO_POSITION&&listener!=null){
                        listener.onItemClick(getSnapshots().getSnapshot(position),position);

                    }
                }
            });
        }

    }
    public interface OnitemClick {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);

    }
    public void setOnitemClick (Firebase_Adapter_users.OnitemClick listener){

        this.listener=listener;


    }
}

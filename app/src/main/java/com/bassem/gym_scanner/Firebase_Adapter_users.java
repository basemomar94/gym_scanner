package com.bassem.gym_scanner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;


public class Firebase_Adapter_users extends  FirestoreRecyclerAdapter<User, Firebase_Adapter_users.ViewHolder>{

    private OnitemClick listener;




    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public Firebase_Adapter_users(@NonNull FirestoreRecyclerOptions<User> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull User model) {

        holder.name.setText(model.getName());
        holder.userid.setText(model.getUserid());
        holder.time.setText(model.getTime());

        holder.admin.setText(model.getAdmin());


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item,parent,false);
        return new ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name,userid,time,admin;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name=itemView.findViewById(R.id.user_name);
            userid=itemView.findViewById(R.id.user_id);
            time=itemView.findViewById(R.id.time);
            admin=itemView.findViewById(R.id.admin_re);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position!=RecyclerView.NO_POSITION && listener!=null){
                        listener.onItemClick(getSnapshots().getSnapshot(position),position);


                    }

                }
            });



        }
    }
    public interface OnitemClick {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);

    }
    public void setOnitemClick (OnitemClick listener){

        this.listener=listener;


    }
}

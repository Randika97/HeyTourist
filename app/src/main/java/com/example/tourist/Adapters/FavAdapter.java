package com.example.tourist.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.tourist.Helpers.FavDB;
import com.example.tourist.Model.FavItem;
import com.example.tourist.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.ArrayList;
import java.util.List;

public class FavAdapter extends RecyclerView.Adapter<FavAdapter.ViewHolder> {

    private Context context;
    private List<FavItem> favItemList;
    private FavDB favDB;
    private DatabaseReference refLike;
    private RecyclerClickListener mListener;
    private ArrayList<String> con;

    public FavAdapter(ArrayList<String> con,Context context, List<FavItem> favItemList,RecyclerClickListener listen) {
        this.con = con;
        this.context = context;
        this.favItemList = favItemList;
        this.mListener = listen;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fav_item,parent, false);
        favDB = new FavDB(context);
        return new ViewHolder(view,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.favTextView.setText(favItemList.get(position).getCountryName());
        Glide.with(context).load(favItemList.get(position).getCountryImg()).apply(new RequestOptions().override(1024, 720)).into(holder.favImageView);
    }

    @Override
    public int getItemCount() {
        return favItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView favTextView;
        Button favBtn;
        ImageView favImageView;
        private RecyclerClickListener mListener;

        public ViewHolder(@NonNull View itemView,RecyclerClickListener listen) {
            super(itemView);
            mListener = listen;
            itemView.setOnClickListener(this);
            favTextView = itemView.findViewById(R.id.favTextView);
            favBtn = itemView.findViewById(R.id.favBtn);
            favImageView = itemView.findViewById(R.id.favImageView);

            refLike = FirebaseDatabase.getInstance().getReference().child("Favourits");
            //remove from fav after click
            favBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    final FavItem favItem = favItemList.get(position);
                    final DatabaseReference upvotesRefLike = refLike.child(favItemList.get(position).getId());
                    favDB.remove_fav(favItem.getId());
                    removeItem(position);

                    upvotesRefLike.runTransaction(new Transaction.Handler() {
                        @NonNull
                        @Override
                        public Transaction.Result doTransaction(@NonNull final MutableData mutableData) {
                            try {
                                Integer currentValue = mutableData.getValue(Integer.class);
                                if (currentValue == null) {
                                    mutableData.setValue(1);
                                } else {
                                    mutableData.setValue(0);
                                }
                            } catch (Exception e) {
                                throw e;
                            }
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                            System.out.println("Transaction completed");
                        }
                    });
                }
            });
        }
        @Override
        public void onClick(View v) {
            mListener.onClick(v, getAdapterPosition());
        }
    }
    public interface RecyclerClickListener {
        void onClick(View view, int position);
    }

    private void removeItem(int position) {
        favItemList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,favItemList.size());
    }
}
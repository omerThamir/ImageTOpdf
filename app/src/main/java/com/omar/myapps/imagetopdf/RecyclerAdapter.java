package com.omar.myapps.imagetopdf;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewOpenlist);
        }
    }

    List<MyImage> mMyImageUrises;
    Context mContext;

    public RecyclerAdapter(Context context, List<MyImage> myImageUrises) {
        mMyImageUrises = myImageUrises;
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater mInflater = LayoutInflater.from(mContext);
        View view = mInflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        MyImage myImage = mMyImageUrises.get(position);
        holder.imageView.setImageURI(myImage.getImgUri());


        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ProcessingActivity) mContext).displayImageToEdit(position);
                MyImage.currentImageIndex = (byte) position;
            }
        });

    }

    @Override
    public int getItemCount() {
        return mMyImageUrises.size();
    }


}
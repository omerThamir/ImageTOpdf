package com.omar.myapps.imagetopdf.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.omar.myapps.imagetopdf.Model.MyImage;
import com.omar.myapps.imagetopdf.ProcessingActivity;
import com.omar.myapps.imagetopdf.R;


import java.util.List;


public class OpenedImagesRAdapter extends RecyclerView.Adapter<OpenedImagesRAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView imageIndexTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewOpenlist);
            imageIndexTextView = itemView.findViewById(R.id.imageIndexTV);
        }
    }

    List<MyImage> mMyImageUrises;
    Context mContext;

    public OpenedImagesRAdapter(Context context, List<MyImage> myImageUrises) {
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
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        MyImage myImage = mMyImageUrises.get(position);
        holder.imageView.setImageBitmap(myImage.getBitmap());
        holder.imageIndexTextView.setText(position + 1 + "");


        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ProcessingActivity) mContext).displayImageToEdit(position);

                ((ProcessingActivity) mContext).setImageFoucusWithScrollView();
                MyImage.currentImageIndex = (byte) position;

            }
        });

    }

    @Override
    public int getItemCount() {
        return mMyImageUrises.size();
    }


}

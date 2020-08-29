package com.omar.myapps.imagetopdf;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class RecyclerAdapterTemplate extends RecyclerView.Adapter<RecyclerAdapterTemplate.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewOpenlist);
        }
    }

    List<MyImage> imageIds;
    Context mContext;

    public RecyclerAdapterTemplate(Context context, List<MyImage> imageIds) {
        this.imageIds = imageIds;
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
        MyImage myImage = imageIds.get(position);
        holder.imageView.setImageResource(myImage.getImageID());


        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mContext instanceof ProcessingActivity) {
                    ((ProcessingActivity) mContext).selectNOfImagePerPage.setVisibility(View.VISIBLE);
                    ((ProcessingActivity) mContext).templateRecycleView.setVisibility(View.GONE);

                    /**
                     *  position == 0 , IMAGE_PER_PAGE = 1
                     *  position == 1 , IMAGE_PER_PAGE = 2
                     *  position == 2 , IMAGE_PER_PAGE = 4
                     */

                    if (position == 0) {
                        ((ProcessingActivity) mContext).IMAGE_PER_PAGE = 1;
                        Toast.makeText(mContext, "you have selected one IMAGE_PER_PAGE Template", Toast.LENGTH_LONG).show();
                    } else if (position == 1) {
                        ((ProcessingActivity) mContext).IMAGE_PER_PAGE = 2;
                        Toast.makeText(mContext, "you have selected Tow IMAGES_PER_PAGE Template", Toast.LENGTH_LONG).show();

                    } else if (position == 2) {
                        ((ProcessingActivity) mContext).IMAGE_PER_PAGE = 4;
                        Toast.makeText(mContext, "you have selected Four IMAGES_PER_PAGE Template", Toast.LENGTH_LONG).show();

                    }
                }
            }

        });

    }

    @Override
    public int getItemCount() {
        return imageIds.size();
    }


}

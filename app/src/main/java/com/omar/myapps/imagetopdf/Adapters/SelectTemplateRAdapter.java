package com.omar.myapps.imagetopdf.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.omar.myapps.imagetopdf.Model.MyImage;
import com.omar.myapps.imagetopdf.ProcessingActivity;
import com.omar.myapps.imagetopdf.R;
import com.omar.myapps.imagetopdf.Utils;

import java.util.List;

public class SelectTemplateRAdapter extends RecyclerView.Adapter<SelectTemplateRAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewOpenlist);
        }
    }

    List<MyImage> imageIds;
    Context mContext;

    public SelectTemplateRAdapter(Context context, List<MyImage> imageIds) {
        this.imageIds = imageIds;
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater mInflater = LayoutInflater.from(mContext);
        View view = mInflater.inflate(R.layout.grid_item_list, parent, false);
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


                    /** *************************************************************************************
                     *                                                                                      *
                     *        1 img fit                   1 img top left              1 img top_center      *
                     *        1 img top right             2 img horizontal            2 img vertical left   *
                     *        2 img vertical center       2 img vertical right        4 img                 *
                     *                                                                                      *
                     * **************************************************************************************
                     */

                    if (position == 0) {

                        setNofImagePerPageAndlocation(1, "fit_page");

                    } else if (position == 1) {
                        setNofImagePerPageAndlocation(1, "Top_Left");
                    }

                    if (position == 2) {
                        setNofImagePerPageAndlocation(1, "Top_Center");
                    } else if (position == 3) {
                        setNofImagePerPageAndlocation(1, "Top_Right");


                    } else if (position == 4) {
                        if (((ProcessingActivity) mContext).bitmapList.size() < 2) {
                            setNofImagePerPageAndlocation(1, "Top_Left");
                        } else {
                            setNofImagePerPageAndlocation(2, "Horizontal");

                        }
                    } else if (position == 5) {

                        if (((ProcessingActivity) mContext).bitmapList.size() < 2) {
                            setNofImagePerPageAndlocation(1, "Top_Left");
                            ;
                        } else {
                            setNofImagePerPageAndlocation(2, "Vertical_Left");
                        }
                    } else if (position == 6) {

                        if (((ProcessingActivity) mContext).bitmapList.size() < 2) {
                            setNofImagePerPageAndlocation(1, "Top_Center");

                        } else {
                            setNofImagePerPageAndlocation(2, "Vertical_Center");
                        }

                    } else if (position == 7) {

                        if (((ProcessingActivity) mContext).bitmapList.size() < 2) {
                            setNofImagePerPageAndlocation(1, "Top_Right");
                        } else {
                            setNofImagePerPageAndlocation(2, "Vertical_Right");
                        }

                    } else if (position == 8) {

                        if (((ProcessingActivity) mContext).bitmapList.size() < 2) {
                            setNofImagePerPageAndlocation(1, "Top_Left");
                        } else if (((ProcessingActivity) mContext).bitmapList.size() == 2) {
                            setNofImagePerPageAndlocation(2, "Horizontal");
                        } else {

                            ((ProcessingActivity) mContext).IMAGE_PER_PAGE = 4;

                        }
                    }

                    Utils.zoom_out(((ProcessingActivity) mContext).templateRecycleView, mContext);
                    showWorkPlace();
                    ((ProcessingActivity) mContext).showAndAnimateStartConvertingLayout();

                }
            }

        });

    }

    private void showWorkPlace() {
        ((ProcessingActivity) mContext).ImageViewConstraintLayout.setVisibility(View.VISIBLE);
        ((ProcessingActivity) mContext).convertToPdfBTN.setEnabled(true);
    }

    private void setNofImagePerPageAndlocation(int i, String imageLocationInPage) {
        ((ProcessingActivity) mContext).IMAGE_PER_PAGE = i;
        ((ProcessingActivity) mContext).IMAGE_LOCATION_IN_PAGE = imageLocationInPage;
    }

    @Override
    public int getItemCount() {
        return imageIds.size();
    }


}

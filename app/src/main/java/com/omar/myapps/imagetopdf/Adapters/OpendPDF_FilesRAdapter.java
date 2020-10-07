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

import com.omar.myapps.imagetopdf.Model.MyFile;
import com.omar.myapps.imagetopdf.R;
import com.omar.myapps.imagetopdf.SavingFolderActivity;

import java.util.List;

public class OpendPDF_FilesRAdapter extends RecyclerView.Adapter<OpendPDF_FilesRAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView fileName;
        ImageView selectedOrNotImageView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.fileNameTV);
            selectedOrNotImageView = itemView.findViewById(R.id.selectedPDFImageview);
        }
    }

    List<MyFile> files;
    Context mContext;

    public OpendPDF_FilesRAdapter(Context context, List<MyFile> files) {
        this.files = files;
        mContext = context;
    }


    @NonNull
    @Override

    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {

        LayoutInflater mInflater = LayoutInflater.from(mContext);
        View item_view = mInflater.inflate(R.layout.list_item_for_opend_pdf_file, parent, false);

        return new ViewHolder(item_view);
    }

    @Override
    public void onBindViewHolder(@NonNull final OpendPDF_FilesRAdapter.ViewHolder holder, final int position) {
        final MyFile file = files.get(position);
        holder.fileName.setText(file.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (holder.selectedOrNotImageView.getTag().equals("NOT_SELECTED")) {
                    holder.selectedOrNotImageView.setImageResource(R.drawable.ic_selected_24);
                    holder.selectedOrNotImageView.setTag("SELECTED");
                    return;
                } else {
                    holder.selectedOrNotImageView.setImageResource(R.drawable.ic_not_selected_24);
                    holder.selectedOrNotImageView.setTag("NOT_SELECTED");
                    //  ((SavingFolderActivity) mContext).openPdfFile(file.getFull_path());
                    return;
                }

            }
        });


    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    /**
     * this put this method ViewHolder class, it will solve repeated clicked actio
     * when the scroll down to number multiple of 12
     * <p>
     * This happens because the views get recycled and reused.
     * <p>
     * So when the view gets recycled, it retains properties of the "old" view
     * if you don't change them again. So when you scroll down to number 12,
     * the view that used to hold number 1 gets recycled (as it can't be seen on the screen anymore),
     * and is used to create number 12. This is why the blue color is on number 12.
     *
     * @param position
     * @return
     */


    @Override
    public int getItemViewType(int position) {
        return position;
    }

}

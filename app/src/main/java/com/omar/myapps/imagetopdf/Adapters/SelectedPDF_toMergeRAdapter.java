package com.omar.myapps.imagetopdf.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.omar.myapps.imagetopdf.MergePDFActivity;
import com.omar.myapps.imagetopdf.Model.MyFile;
import com.omar.myapps.imagetopdf.Model.MySelectedFiles;
import com.omar.myapps.imagetopdf.R;

import java.util.List;

public class SelectedPDF_toMergeRAdapter extends RecyclerView.Adapter<SelectedPDF_toMergeRAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView fileName;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.fileNameTV);
        }
    }

    List<MySelectedFiles> files;
    Context mContext;

    public SelectedPDF_toMergeRAdapter(Context context, List<MySelectedFiles> files) {
        this.files = files;
        mContext = context;
    }


    @NonNull
    @Override

    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {

        LayoutInflater mInflater = LayoutInflater.from(mContext);
        View item_view = mInflater.inflate(R.layout.list_item_file, parent, false);

        item_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return new ViewHolder(item_view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SelectedPDF_toMergeRAdapter.ViewHolder holder, final int position) {
        final MySelectedFiles file = files.get(position);
        holder.fileName.setText(file.getName());
        
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

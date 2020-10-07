package com.omar.myapps.imagetopdf.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.omar.myapps.imagetopdf.Model.MyFile;
import com.omar.myapps.imagetopdf.R;
import com.omar.myapps.imagetopdf.SavingFolderActivity;

import java.util.List;

public class SavedPDF_FilesRAdapter extends RecyclerView.Adapter<SavedPDF_FilesRAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView fileName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.fileNameTV);
        }
    }

    List<MyFile> files;
    Context mContext;

    public SavedPDF_FilesRAdapter(Context context, List<MyFile> files) {
        this.files = files;
        mContext = context;
    }


    @NonNull
    @Override

    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {

        LayoutInflater mInflater = LayoutInflater.from(mContext);
        View item_view = mInflater.inflate(R.layout.list_item_file, parent, false);

        final ViewHolder result = new ViewHolder(item_view);

        item_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final MyFile file = files.get(result.getAdapterPosition());
                if (mContext instanceof SavingFolderActivity) {
                    ((SavingFolderActivity) mContext).openPdfFile(file.getFull_path());
                }
            }
        });

        return result;
    }

    @Override
    public void onBindViewHolder(@NonNull SavedPDF_FilesRAdapter.ViewHolder holder, final int position) {
        final MyFile file = files.get(position);
        holder.fileName.setText(file.getName());
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

}

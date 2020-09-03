package com.omar.myapps.imagetopdf.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.omar.myapps.imagetopdf.File;
import com.omar.myapps.imagetopdf.R;
import com.omar.myapps.imagetopdf.SavingFolderActivity;

import java.util.List;

public class RecyclerAdapterFile extends RecyclerView.Adapter<RecyclerAdapterFile.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView fileName, filePath;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.fileNameTV);
            //     filePath = itemView.findViewById(R.id.filePathTV);
        }
    }

    List<File> files;
    Context mContext;

    public RecyclerAdapterFile(Context context, List<File> files) {
        this.files = files;
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerAdapterFile.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater mInflater = LayoutInflater.from(mContext);
        View view = mInflater.inflate(R.layout.list_item_file, parent, false);
        return new RecyclerAdapterFile.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterFile.ViewHolder holder, final int position) {
        final File file = files.get(position);
        holder.fileName.setText(file.getName());
        // holder.filePath.setText(file.getFull_path());


        holder.fileName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((SavingFolderActivity) mContext).openPdfFile(file.getFull_path());
            }

        });


    }

    @Override
    public int getItemCount() {
        return files.size();
    }


}

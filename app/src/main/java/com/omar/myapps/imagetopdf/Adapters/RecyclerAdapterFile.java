package com.omar.myapps.imagetopdf.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.omar.myapps.imagetopdf.Model.MyFile;
import com.omar.myapps.imagetopdf.R;
import com.omar.myapps.imagetopdf.SavingFolderActivity;

import java.util.List;

public class RecyclerAdapterFile extends RecyclerView.Adapter<RecyclerAdapterFile.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView fileName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.fileNameTV);

        }
    }

    List<MyFile> files;
    Context mContext;
    boolean showSavingFiles;

    int selectedItem;

    public RecyclerAdapterFile(Context context, List<MyFile> files, boolean showSavingFiles) {
        this.files = files;
        mContext = context;
        this.showSavingFiles = showSavingFiles;

        selectedItem = 0;
    }


    @NonNull
    @Override

    public RecyclerAdapterFile.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater mInflater = LayoutInflater.from(mContext);
        View item_view = mInflater.inflate(R.layout.list_item_file, parent, false);

        return new RecyclerAdapterFile.ViewHolder(item_view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterFile.ViewHolder holder, final int position) {
        final MyFile file = files.get(position);
        holder.fileName.setText(file.getName());

        if (selectedItem == position) {
          //  holder.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
        }


        holder.fileName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (showSavingFiles && mContext instanceof SavingFolderActivity) {
                    ((SavingFolderActivity) mContext).openPdfFile(file.getFull_path());

                    // the 4 lines below used to changed item color
                    int previousItem = selectedItem;
                    selectedItem = position;

                    notifyItemChanged(previousItem);
                    notifyItemChanged(position);

                }
            }

        });


    }

    @Override
    public int getItemCount() {
        return files.size();
    }


}

package com.example.myaudiorecoder.Adatpter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myaudiorecoder.Others.TimeAgo;
import com.example.myaudiorecoder.R;
import com.example.myaudiorecoder.databinding.SingleListItemBinding;

import java.io.File;

public class AudioListAdapter extends RecyclerView.Adapter<AudioListAdapter.AudioViewHolder> implements View.OnClickListener {

    private File[] allFiles;
    private String TAG = "AudioListAdapter";
    private TimeAgo timeAgo;
    private onItemClick onItemClick;

    public AudioListAdapter(File[] allFiles,onItemClick onItemClick) { this.onItemClick = onItemClick;this.allFiles = allFiles;
    }

    @Override
    public int getItemCount() {
        return allFiles.length;
    }

    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_list_item, parent, false);
        timeAgo = new TimeAgo();
        return new AudioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder holder, int position) {
        File currentFile = allFiles[position];
        holder.binding.listTitle.setText(currentFile.getName());
        holder.binding.listDate.setText(timeAgo.getTimeAgo(currentFile.lastModified()));

        holder.binding.getRoot().setOnClickListener(v -> { onItemClick.onClickListner(currentFile, position); });
    }

    @Override
    public void onClick(View v) {
    }


    public static class AudioViewHolder extends RecyclerView.ViewHolder {

        private final SingleListItemBinding binding;

        public AudioViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = SingleListItemBinding.bind(itemView);
        }
    }

    public interface onItemClick{
        void onClickListner(File file,int position);
    }

}

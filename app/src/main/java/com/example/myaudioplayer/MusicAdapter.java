package com.example.myaudioplayer;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<MusicFiles> mFiles;

    MusicAdapter(Context mContext,ArrayList<MusicFiles> mFiles)
    {
        this.mFiles=mFiles;
        this.mContext=mContext;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.music_items, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder,  int position) {

        holder.file_name.setText(mFiles.get(position).getTitle());
        byte[] image= new byte[0];
        try {
            image = getAlbumArt(mFiles.get(position).getPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(image!=null)
        {
            Glide.with(mContext).asBitmap().load(image).into(holder.album_art);
        }
        else {
            Glide.with(mContext).load(R.drawable.song_icon).into(holder.album_art);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, PlayerActivity.class);
                intent.putExtra("position",position);
                mContext.startActivity(intent);
            }
        });
        holder.menuMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                PopupMenu popupMenu=new PopupMenu(mContext,v);
                popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener((item)->{
                    switch (item.getItemId()){
                        case R.id.delete:
                            Toast.makeText(mContext, "Delete Clicked!!", Toast.LENGTH_SHORT).show();
                            deleteFile(position,v);
                            break;
                    }
                    return  true;
                });
            }
        });
    }

    private void deleteFile(int position, View v){
        Uri contentUri= ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Long.parseLong(mFiles.get(position).getId()));
        mFiles.remove(position);
        File file =new File(mFiles.get(position).getPath());
        boolean deleted = file.delete();
        // check whether file is deleted or not

        if(deleted){
            mContext.getContentResolver().delete(contentUri,null,null);
            mFiles.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position,mFiles.size());
            Snackbar.make(v,"File Deleted :",Snackbar.LENGTH_LONG)
                    .show();
        }
        else
        {
            //maybe file in sd card
            Snackbar.make(v,"Can't be Deleted :",Snackbar.LENGTH_LONG)
                    .show();
        }

        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mFiles.size());
        Snackbar.make(v,"File Deleted :", Snackbar.LENGTH_LONG)
                .show();
    }
    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        // Finding IDs
        TextView file_name; // variables
        ImageView album_art, menuMore;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            file_name=itemView.findViewById(R.id.music_file_name);  // fetching & storing by id's
            album_art=itemView.findViewById(R.id.music_img);
            menuMore=itemView.findViewById(R.id.menuMore);
        }
    }
    private byte[] getAlbumArt(String uri) throws IOException {
        MediaMetadataRetriever retriever=new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art=retriever.getEmbeddedPicture();
        retriever.release();
        return art;

    }
    void updateList(ArrayList<MusicFiles> musicFilesArrayList)
    {
        mFiles =new ArrayList<>();
        mFiles.addAll(musicFilesArrayList);
        notifyDataSetChanged();
    }
}

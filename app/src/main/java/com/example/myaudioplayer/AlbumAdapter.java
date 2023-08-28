package com.example.myaudioplayer;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myaudioplayer.MusicFiles;
import com.example.myaudioplayer.R;

import java.io.IOException;
import java.util.ArrayList;

public  class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.MyHolder>{

    private Context mContext;
    private ArrayList<MusicFiles> albumFIles;
    View view;

    public AlbumAdapter(Context mContext, ArrayList<MusicFiles> albumFIles) {
        this.mContext = mContext;
        this.albumFIles = albumFIles;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(mContext).inflate(R.layout.album_item,parent, false);
        return new MyHolder(view);
    }





    @Override

    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        holder.album_name.setText(albumFIles.get(position).getAlbum());
        byte[] image= new byte[0];
        try {
            image = getAlbumArt(albumFIles.get(position).getPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(image!=null)
        {
            Glide.with(mContext).asBitmap().load(image).into(holder.album_image);
        }
        else {
            Glide.with(mContext).load(R.drawable.song_icon).into(holder.album_image);
        }
        // This is for below the album logo after clicking on album card
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AlbumDetails.class);
                intent.putExtra("albumName",albumFIles.get(position).getAlbum());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return albumFIles.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        ImageView album_image;
        TextView album_name;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            album_image=itemView.findViewById(R.id.album_image);
            album_name=itemView.findViewById(R.id.album_name);
        }
    }

    private byte[] getAlbumArt(String uri) throws IOException {
        MediaMetadataRetriever retriever=new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art=retriever.getEmbeddedPicture();
        retriever.release();
        return art;

    }
}
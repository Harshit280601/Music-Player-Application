package com.example.myaudioplayer;

import static com.example.myaudioplayer.MainActivity.albums;
import static com.example.myaudioplayer.MainActivity.musicFiles;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myaudioplayer.AlbumAdapter;

public class AlbumFragment extends Fragment {

    RecyclerView recyclerView;
    AlbumAdapter albumAdapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_album, container, false);
        recyclerView =view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        // for getting the files from the phone
        if(!(albums.size()<1)){
            albumAdapter= new AlbumAdapter(getContext(), albums);
            recyclerView.setAdapter(albumAdapter);
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));

        }
        return view;
        // This view will be used to find id of layout in the fragment
    }

}

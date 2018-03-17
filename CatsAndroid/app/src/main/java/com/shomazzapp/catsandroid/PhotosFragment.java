package com.shomazzapp.catsandroid;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shomazzapp.catsandroid.Utils.CatsAdapter;

import java.io.File;
import java.util.ArrayList;

public class PhotosFragment extends Fragment {

    private Context context;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView recyclerView;
    private CatsAdapter catsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            View mainView = inflater.inflate(R.layout.fragment_photos,
                    container, false);
            init(mainView);
        return mainView;
    }

    public void init(View mainView){
        recyclerView = (RecyclerView) mainView.findViewById(R.id.cats_recycler_view);
        layoutManager = new GridLayoutManager(context, 3);
        layoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        catsAdapter = new CatsAdapter(context, getCatsArray());
        recyclerView.setAdapter(catsAdapter);
    }

    public void closeFragment(){
        getFragmentManager().beginTransaction().remove(this).commit();
    }

    public ArrayList<File> getCatsArray() {
        ArrayList<File> files = new ArrayList<File>();
        File folder = new File(Environment.getExternalStorageDirectory(),
                getString(R.string.media_folder));
        File[] filesInFolder = folder.listFiles();
        if (folder.exists() && filesInFolder.length > 0)
            for (File file : filesInFolder) {
                if (!file.getName().startsWith(".") && !file.isDirectory() && (file.getName().endsWith(".png")
                        || file.getName().endsWith(".jpg"))) {
                    files.add(0, file);
                }
            }
        return files;
    }
}

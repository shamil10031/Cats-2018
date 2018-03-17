package com.shomazzapp.catsandroid.Utils;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.shomazzapp.catsandroid.R;

import java.io.File;
import java.util.ArrayList;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class CatsAdapter extends RecyclerView.Adapter<CatsAdapter.ImageViewHolder> {

    private Context context;
    private ArrayList<File> cats;

    public CatsAdapter(Context context, ArrayList<File> cats) {
        this.context = context;
        this.cats = cats;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View wallpapperView = inflater.inflate(R.layout.item_recycler_view, parent, false);
        return new ImageViewHolder(wallpapperView);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        if (cats != null)
            Glide.with(context)
                    .load(cats.get(position))
                    .transition(withCrossFade())
                    .thumbnail(0.17f)
                    .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return cats.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.cats_item_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
            }
            //wallsLoader.loadVKWallpaperFragment(wallpapers, position);
        }
    }

}

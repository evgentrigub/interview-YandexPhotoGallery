package ru.evgentrigub.android.photogallery;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bignerdranch.android.photogallery.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class PhotoGalleryFragment extends Fragment {

    private static final String TAG = "PhotoGalleryFragment";

    private RecyclerView mPhotoRecyclerView;
    private List<GalleryItem> mItems = new ArrayList<>();
//    private ThumbnailDownloader<PhotoHolder> mThumbnailDownloader;

    public static PhotoGalleryFragment newInstance() {
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        new FetchItemsTask().execute();
//
//        Handler responseHandler = new Handler();
//        mThumbnailDownloader = new ThumbnailDownloader<>(responseHandler);
//        mThumbnailDownloader.setThumbnailDownloadListener(
//                new ThumbnailDownloader.ThumbnailDownloadListener<PhotoHolder>(){
//                    @Override
//                    public void onThumbnailDownloaded(PhotoHolder photoHolder, Bitmap bitmap) {
//                        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
//                        photoHolder.bindDrawable(drawable);
//                    }
//                }
//        );
//        mThumbnailDownloader.start();
//        mThumbnailDownloader.getLooper();
        Log.i(TAG, "Background thread started");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);

        mPhotoRecyclerView = (RecyclerView) v.findViewById(R.id.photo_recycler_view);
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        setupAdapter();

        return v;
    }

    private void setupAdapter() {
        if (isAdded()) {
            mPhotoRecyclerView.setAdapter(new GalleryItemAdapter(mItems));
        }
    }
    @Override
    public void onDestroyView(){
        super.onDestroyView();
//        mThumbnailDownloader.clearQueue();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
//        mThumbnailDownloader.quit();
        Log.i(TAG, "Background thread destroy");
    }

//    private class PhotoHolder extends RecyclerView.ViewHolder {
//        private ImageView mItemImageView;
//
//        public PhotoHolder(View itemView) {
//            super(itemView);
//
//            mItemImageView = (ImageView)itemView.findViewById(R.id.iv_item);
//
//        }
//
//       public void bindDrawable(Drawable drawable){
//            mItemImageView.setImageDrawable(drawable);
//       }
//    }


    private class GalleryItemAdapter extends RecyclerView.Adapter<GalleryItemAdapter.GalleryItemViewHolder> {

        private List<GalleryItem> items;

        public GalleryItemAdapter(List<GalleryItem> items) {
            this.items = items;
        }

//        public View getView(int position, View convertView, ViewGroup parent) {
//            if (convertView == null) {
//                convertView = getActivity().getLayoutInflater()
//                        .inflate(R.layout.gallery_item, parent, false);
//            }
//
//            GalleryItem item = getItem(position);
//            ImageView imageView = (ImageView)convertView.findViewById(R.id.iv_item);
//
//            imageView.setImageResource(R.drawable.bill_up_close);
//            Picasso.with(getActivity())
//                    .load(item.getUrl())
//                    .noFade()
//                    .into(imageView);
//
//            return convertView;
//        }

        @NonNull
        @Override
        public GalleryItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_item, parent, false);
            return new GalleryItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull GalleryItemViewHolder holder, int position) {
            holder.setResource(items.get(position));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class GalleryItemViewHolder extends RecyclerView.ViewHolder{

            private ImageView ivItem;

            public GalleryItemViewHolder(View itemView) {
                super(itemView);

                ivItem = itemView.findViewById(R.id.iv_item);
            }

            void setResource(GalleryItem item){
                Picasso.with(getActivity())
                        .load(item.getUrl())
                        .noFade()
                        .into(ivItem);
            }
        }
    }

//    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {
//
//        private List<GalleryItem> mGalleryItems;
//
//        public PhotoAdapter(List<GalleryItem> galleryItems) {
//            mGalleryItems = galleryItems;
//        }
//
//        @Override
//        public PhotoHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
//            LayoutInflater inflater = LayoutInflater.from(getActivity());
//            View view = inflater.inflate(R.layout.gallery_item, viewGroup, false);
//            return new PhotoHolder(view);
//        }
//
//
//        public void bindGalleryItem(GalleryItem galleryItem){
//            Picasso.get()
//                    .load(galleryItem.getUrl())
//                    .placeholder(R.drawable.bill_up_close)
//                    .into();
//        }

//        @Override
//        public void onBindViewHolder(PhotoHolder photoHolder, int position) {
//            GalleryItem galleryItem = mGalleryItems.get(position);
//            Drawable placeholder = getResources().getDrawable(R.drawable.bill_up_close);
//            photoHolder.bindDrawable(placeholder);
//            mThumbnailDownloader.queueThumbnail(photoHolder, galleryItem.getUrl());
//        }
//
//        @Override
//        public int getItemCount() {
//            return mGalleryItems.size();
//        }
//    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    private class FetchItemsTask extends AsyncTask<Void,Void,List<GalleryItem>> {
        @Override
        protected List<GalleryItem> doInBackground(Void... params) {
            return new FlickrFetchr().fetchItems();
        }

        @Override
        protected void onPostExecute(List<GalleryItem> items) {
            mItems = items;
            setupAdapter();
        }
    }
}

package com.example.vivek.rentalmates.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.vivek.rentalmates.R;
import com.nguyenhoanglam.imagepicker.activity.ImagePickerActivity;
import com.nguyenhoanglam.imagepicker.model.Image;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewFlatPhotosFragment extends Fragment {
    private static final int REQUEST_CODE_PICKER = 2;

    private Context context;
    private Button pickFlatPhotosButton;
    private RecyclerView recyclerView;
    private PhotoListViewAdapter photoListViewAdapter;
    private ArrayList<Image> images = new ArrayList<>();
    private ArrayList<String> imagePaths = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.fragment_new_flat_photos, container, false);
        context = getActivity().getApplicationContext();

        pickFlatPhotosButton = (Button) layout.findViewById(R.id.pickFlatPhotosButton);
        pickFlatPhotosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPhotos();
            }
        });

        // Initialize RecyclerView
        recyclerView = (RecyclerView) layout.findViewById(R.id.flatPhotosRecyclerView);
        photoListViewAdapter = new PhotoListViewAdapter(context);
        recyclerView.setAdapter(photoListViewAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));

        return layout;
    }

    private void selectPhotos() {
        /*Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_PICKER);*/

        Intent intent = new Intent(context, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_MODE, ImagePickerActivity.MODE_MULTIPLE);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_LIMIT, 5);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_SHOW_CAMERA, true);
        intent.putExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES, images);
        //intent.putExtra(ImagePickerActivity.INTENT_EXTRA_TITLE, "Tap to select");
        startActivityForResult(intent, REQUEST_CODE_PICKER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICKER && resultCode == Activity.RESULT_OK && data != null) {
            images = data.getParcelableArrayListExtra(ImagePickerActivity.INTENT_EXTRA_SELECTED_IMAGES);
            photoListViewAdapter.updatePhotoData();
            photoListViewAdapter.notifyDataSetChanged();
            imagePaths.clear();
            for (Image image : images) {
                imagePaths.add(image.getPath());
            }
        }
    }

    public ArrayList<String> getImagePaths() {
        return imagePaths;
    }

    public class PhotoListViewAdapter extends RecyclerView.Adapter<PhotoListViewAdapter.PhotoViewHolder> {

        private static final String TAG = "ContactAdapter_Debug";

        private List<PhotoListItem> data = new ArrayList<>();
        private LayoutInflater inflater;
        private Context context;

        public PhotoListViewAdapter(Context context) {
            Log.d(TAG, "inside Constructor");
            inflater = LayoutInflater.from(context);
            this.context = context;
            updatePhotoData();
        }

        public void updatePhotoData() {
            this.data.clear();
            for (Image image : images) {
                this.data.add(new PhotoListItem(image));
            }
        }

        @Override
        public PhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.d(TAG, "inside onCreateViewHolder");
            View view = inflater.inflate(R.layout.photo_list_item, parent, false);
            return new PhotoViewHolder(view);
        }

        @Override
        public void onBindViewHolder(PhotoViewHolder viewHolder, int position) {
            Log.d(TAG, "inside onBindViewHolder");
            PhotoListItem current = data.get(position);
            viewHolder.imageView.setImageBitmap(BitmapFactory.decodeFile(current.image.getPath()));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        /**
         * The view holder design pattern prevents using findViewById()
         * repeatedly in the getView() method of the adapter.
         */
        class PhotoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
            ImageView imageView;

            public PhotoViewHolder(View itemView) {
                super(itemView);
                imageView = (ImageView) itemView.findViewById(R.id.flatPictureImageView);

                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
            }

            @Override
            public void onClick(View v) {
                Toast.makeText(context, "To be implemented", Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(context, "To be implemented", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        public class PhotoListItem {
            public final Image image;

            public PhotoListItem(Image image) {
                this.image = image;
            }
        }
    }
}

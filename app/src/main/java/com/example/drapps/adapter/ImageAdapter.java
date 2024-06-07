package com.example.drapps.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drapps.R;
import com.example.drapps.user.HistoryDetails;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import java.util.List;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private List<Uri> imageList;

    public ImageAdapter(List<Uri> imageList) {
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Uri imageUri = imageList.get(position);
        Picasso.get().load(imageUri).into(holder.enlargedImageView); // Load image into imageView

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Pass the clicked image URI and the context of the item view
                showEnlargedImageDialog(holder.itemView.getContext(), imageUri);
            }
        });
    }


    private void showEnlargedImageDialog(Context context, Uri imageUri) {
        // Create a Dialog instance using the context passed from the adapter
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);

        // Set the content view to a layout containing a PhotoView for the enlarged image
        dialog.setContentView(R.layout.dialog_enlarged_image);

        // Find the PhotoView and back button in the dialog layout
        PhotoView enlargedImageView = dialog.findViewById(R.id.enlargedImageView);
        ImageButton backBtn = dialog.findViewById(R.id.backBtn);

        // Load the image into the PhotoView using Picasso
        try {
            Picasso.get().load(imageUri).into(enlargedImageView);
        } catch (Exception e) {
            // Handle any errors loading the image
            e.printStackTrace();
        }

        // Set click listener for the back button to dismiss the dialog
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // Show the Dialog
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    // ImageAdapter.java

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView enlargedImageView;
        ImageButton backBtn;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            enlargedImageView = itemView.findViewById(R.id.enlargedImageView); // Ensure this ID is correct
            backBtn = itemView.findViewById(R.id.backBtn);
        }
    }

}
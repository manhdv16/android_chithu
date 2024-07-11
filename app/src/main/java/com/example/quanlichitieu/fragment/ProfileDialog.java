package com.example.quanlichitieu.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.quanlichitieu.R;
import com.example.quanlichitieu.activity.AddTypeActivity;
import com.example.quanlichitieu.activity.SigninActivity;
import com.example.quanlichitieu.dal.Firebase;
import com.example.quanlichitieu.model.User;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.glide.transformations.CropCircleTransformation;


public class ProfileDialog extends DialogFragment implements View.OnClickListener {
    private LinearLayout linearAvatar, linearTen, linearPhone;
    private TextView tvId,tvTen,tvPhone;
    private ImageView avatar;
    private Button btLogout;
    private SharedPreferences sharedPreferences;
    private User currentU;
    private String currentId;
    private static final int PICK_IMAGE_REQUEST = 1;

    public static ProfileDialog newInstance(User u) {

        Bundle args = new Bundle();
        ProfileDialog fragment = new ProfileDialog();
        args.putSerializable("user", u);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.profile_dialog, null);
        initView(view);
        builder.setView(view)
                .setTitle("Thông tin cá nhân");
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_border);
        }
        return dialog;
    }

    private void initView(View view) {
        sharedPreferences = getActivity().getSharedPreferences("MyPrefs", getActivity().MODE_PRIVATE);
        linearAvatar = view.findViewById(R.id.linearAvatar);
        linearTen =  view.findViewById(R.id.linearTen);
        linearPhone=  view.findViewById(R.id.linearPhone);
        tvId=  view.findViewById(R.id.tvId);
        tvTen=  view.findViewById(R.id.tvTen);
        tvPhone=  view.findViewById(R.id.tvPhone);
        avatar=  view.findViewById(R.id.avatar);
        btLogout = view.findViewById(R.id.btLogout);
        btLogout.setOnClickListener(this);
        avatar.setOnClickListener(this);
        linearTen.setOnClickListener(this);
        linearPhone.setOnClickListener(this);
        if (getArguments() != null) {
            currentU = (User) getArguments().getSerializable("user");
            currentId = currentU.getId();
            tvId.setText("ID:"+currentId);
            tvTen.setText(currentU.getFullName());
            tvPhone.setText(currentU.getPhoneNumber());
            Glide.with(getContext())
                    .load(currentU.getAvatarUrl())
                    .apply(RequestOptions.bitmapTransform(new CropCircleTransformation()))
                    .placeholder(R.drawable.circle)
                    .error(R.drawable.circle)
                    .into(avatar);
            tvPhone.setText(currentU.getPhoneNumber()!= null ? currentU.getPhoneNumber():"");
        }
    }

    @Override
    public void onClick(View view) {
        if(view == avatar) {
            updateAvatar();
        } else if(view == linearTen) {

        } else if(view == linearPhone) {

        } else if(view == btLogout) {
            getActivity().finishAffinity();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            Intent intent = new Intent(getActivity(), SigninActivity.class);
            startActivity(intent);
        }
    }

    private void updateAvatar() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            avatar.setImageURI(imageUri);
            currentU.setAvatarUrl(imageUri.toString());
            uploadImageToFirebase(imageUri);
        }
    }
    private void uploadImageToFirebase(Uri imageUri) {
        if (imageUri != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference fileRef = storageRef.child("users/" + System.currentTimeMillis() + ".jpg");
            fileRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        saveImageUrlToUser(imageUrl);
                    }))
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi khi tải ảnh lên: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }
    private void saveImageUrlToUser(String imageUrl) {
        FirebaseDatabase database= FirebaseDatabase.getInstance("https://expensemanagement-8f058-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference databaseRef = database.getReference().child("Users"); // Thay `userId` với ID của người dùng hiện tại
        currentU.setAvatarUrl(imageUrl);
        databaseRef.child(currentU.getId()).setValue(currentU)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Ảnh đại diện đã được cập nhật", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e  -> {
                    Toast.makeText(getContext(), "Lỗi khi cập nhật ảnh đại diện ", Toast.LENGTH_SHORT).show();
                });
    }


}

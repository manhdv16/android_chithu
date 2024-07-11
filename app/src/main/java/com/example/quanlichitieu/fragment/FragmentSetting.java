package com.example.quanlichitieu.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.quanlichitieu.R;
import com.example.quanlichitieu.dal.Firebase;
import com.example.quanlichitieu.model.User;
import com.google.firebase.database.DatabaseError;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class FragmentSetting extends Fragment implements View.OnClickListener {
    private LinearLayout linearProfile,linearSetting;
    private TextView tvTen,tvId;
    private ImageView avatar;
    private Firebase firebase;
    private User currentUser= new User();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebase = new Firebase();
        linearProfile = view.findViewById(R.id.linearProfile);
        linearSetting = view.findViewById(R.id.linearSetting);
        tvTen = view.findViewById(R.id.tvTen);
        tvId = view.findViewById(R.id.tvId);
        avatar = view.findViewById(R.id.avatar);
        linearProfile.setOnClickListener(this);
        linearSetting.setOnClickListener(this);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", getActivity().MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");

        firebase.getUserByEmail(email, new Firebase.UserCallback() {
            @Override
            public void onUserLoaded(User u) {
                currentUser=u;
                if (currentUser.getAvatarUrl() != null) {
                    Glide.with(getContext())
                            .load(currentUser.getAvatarUrl())
                            .apply(RequestOptions.bitmapTransform(new CropCircleTransformation()))
                            .placeholder(R.drawable.circle)
                            .error(R.drawable.circle)
                            .into(avatar);
                }
                tvTen.setText(u.getFullName());
                tvId.setText(u.getId());
            }
            @Override
            public void onUserError(DatabaseError databaseError) {

            }
        });
    }
    @Override
    public void onClick(View view) {
        if(view == linearProfile) {
            ProfileDialog profileDialog = ProfileDialog.newInstance(currentUser);
            profileDialog.show(getActivity().getSupportFragmentManager(), "ProfileDialog");
        }else if(view == linearSetting) {
            SettingDialog settingDialog = SettingDialog.newInstance(currentUser);
            settingDialog.show(getActivity().getSupportFragmentManager(),"SettingDialog");
        }
    }
}

package com.example.quanlichitieu.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.quanlichitieu.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassDialog extends DialogFragment  implements View.OnClickListener{
    private Button btChange;
    private EditText edOldPass,edNewPass, edRePass;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.change_pass_dialog, null);
        initView(view);
        builder.setView(view)
                .setTitle("Thay đổi mật khẩu");
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_border);
        }
        return dialog;
    }

    private void initView(View view) {
        btChange = view.findViewById(R.id.btChange);
        edNewPass = view.findViewById(R.id.edNewPass);
        edOldPass = view.findViewById(R.id.edOldPass);
        edRePass = view.findViewById(R.id.edRePass);
        btChange.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
       if(view==btChange){
           SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyPrefs", getActivity().MODE_PRIVATE);
           String password = sharedPreferences.getString("password", "");
           String oldPass = edOldPass.getText().toString().trim();
           String newPass = edNewPass.getText().toString().trim();
           String reNewPass = edRePass.getText().toString().trim();
           if(checkPassword(password,oldPass,newPass,reNewPass)) {
               FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
               user.updatePassword(newPass)
                       .addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                               if (task.isSuccessful()) {
                                   Toast.makeText(getContext(), "Thay đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                               } else {
                                   Toast.makeText(getContext(), "Thay đổi mật khẩu thất bại", Toast.LENGTH_SHORT).show();
                               }
                           }
                       });
           }
       }
    }

    private boolean checkPassword(String password, String oldPass, String newPass, String reNewPass) {
        if(oldPass.equals("")){
            Toast.makeText(getContext(), "Vui lòng nhập đầy đủ", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(newPass.equals("")){
            Toast.makeText(getContext(), "Vui lòng nhập đầy đủ", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(reNewPass.equals("")){
            Toast.makeText(getContext(), "Vui lòng nhập đầy đủ", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!password.equals(oldPass)){
            Toast.makeText(getContext(), "Mật khẩu cũ không đúng", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!newPass.equals(reNewPass)){
            Toast.makeText(getContext(), "Mật khẩu mới không khớp", Toast.LENGTH_SHORT).show();
            return false;
        }
        return  true;
    }
}

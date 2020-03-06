package com.jame.tablebooking;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jame.tablebooking.Model.User;

public class LoginActivity extends AppCompatActivity {

    EditText editText_Email, editText_Password;
    Button btn_login;
    ImageView img_flag;

    DatabaseReference mDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // ดึงหน้า activity_login

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference("user");

        editText_Email = findViewById(R.id.editText_Email);
        editText_Password = findViewById(R.id.editText_Password);
        img_flag = findViewById(R.id.img_flag);

        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login(); // กดปุ่ม Login
            }
        });
    }

    private void Login() {

        final String Email = editText_Email.getText().toString().trim();
        final String Password = editText_Password.getText().toString().trim();

        /*if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            editText_Email.setError("กรุณาตรวจสอบอีเมลล์ ");
            editText_Email.requestFocus();
            return;
        }*/
        if (Email.isEmpty()) {
            editText_Email.setError("กรุณาใส่อีเมลล์ ");
            editText_Email.requestFocus();
            return;
        }
        if (Password.isEmpty()) {
            editText_Password.setError("กรุณาใส่รหัสผ่าน ");
            editText_Password.requestFocus();
            return;
        }
        if (Password.length() < 6) {
            editText_Password.setError("รหัสผ่านต้องมากกว่า 6 ตัว");
            editText_Password.requestFocus();
            return;
        }

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    /*Log.d("zz", String.valueOf(dataSnapshot.child("-LzqiBoPdyTbMisFfwt4").getValue(User.class).email));
                    if (dataSnapshot.child(Email).exists()) {
                        Toast.makeText(LoginActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    }*/
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        User data = postSnapshot.getValue(User.class);
                        Log.d("zz", data.email);
                        if (data.email.equals(Email) && data.password.equals(Password)) {
                            // Success
                            break;
                        } else {
                            //Error
                        }
                    }
                } catch (Exception e) {
                    //Toast.makeText(RegisterActivity.this, "เกิดข้อผิดพลาด!!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

package com.jame.tablebooking;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jame.tablebooking.Fragment.MainActivity;
import com.jame.tablebooking.Model.Restaurant;
import com.jame.tablebooking.Model.User;
import com.jame.tablebooking.SharedPreferences.AppPreferences;

public class LoginActivity extends AppCompatActivity {

    EditText editText_Email, editText_Password;
    Button btn_login;
    ImageView img_flag;
    Spinner spinner_language;
    private AppPreferences appPreferences;

    DatabaseReference mDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // ดึงหน้า activity_login

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference("user");

        appPreferences = new AppPreferences(LoginActivity.this);
        if (appPreferences.getBooleanPrefs(AppPreferences.KEY_SAVE_USER)) { // ถ้าเคยใช้งานแอปแล้วจะไปหน้า FirstActivity เลย
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        editText_Email = findViewById(R.id.editText_Email);
        editText_Password = findViewById(R.id.editText_Password);
        img_flag = findViewById(R.id.img_flag);
        spinner_language = findViewById(R.id.spinner_language);
        spinner_language.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (spinner_language.getSelectedItem().equals("TH")) {
                    img_flag.setImageResource(R.drawable.thai_flag);
                } else {
                    img_flag.setImageResource(R.drawable.eng_flag);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login(); // กดปุ่ม Login
            }
        });
    }

    public void Watch_Image(View view) {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.full_image, null);
        final ImageView imageView1 = alertLayout.findViewById(R.id.imageView1);

        try {
            Glide.with(LoginActivity.this)
                    .load(R.drawable.logo)
                    .centerCrop()
                    .into(imageView1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
        alert.setTitle("ดูภาพ");
        alert.setView(alertLayout);
        alert.setCancelable(false);
        alert.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog1 = alert.create();
        dialog1.show();
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
                            //Toast.makeText(LoginActivity.this, "Success", Toast.LENGTH_SHORT).show();
                            appPreferences.setBooleanPrefs(AppPreferences.KEY_SAVE_USER, true);
                            appPreferences.setStringPrefs(AppPreferences.KEY_USER_ID, data.user_id);
                            appPreferences.setStringPrefs(AppPreferences.KEY_EMAIL, Email);
                            appPreferences.setStringPrefs(AppPreferences.KEY_PASSWORD, Password);
                            appPreferences.setStringPrefs(AppPreferences.KEY_TEL, data.tel);

                            Intent tt = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(tt);
                            break;
                        } else {
                            AlertDialog.Builder ad = new AlertDialog.Builder(LoginActivity.this);
                            ad.setTitle("เกิดข้อผิดพลาด!!! "); // ขึ้น Pop up
                            ad.setMessage("อีเมลล์หรือรหัสผ่านไม่ถูกต้อง กรุณาลองใหม่อีกครั้ง");
                            ad.setIcon(android.R.drawable.btn_star_big_on);
                            ad.setPositiveButton("ตกลง", null);
                            ad.show();
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

    public void Intent_Register(View view) {
        Intent tt = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(tt);
    }

    /*public void Forget_Password(View view) {
        Intent tt = new Intent(LoginActivity.this, ForgetActivity.class);
        startActivity(tt);
    }

    */
}

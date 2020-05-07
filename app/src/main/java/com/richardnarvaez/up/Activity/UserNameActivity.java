package com.richardnarvaez.up.Activity;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.richardnarvaez.up.Model.Author;
import com.richardnarvaez.up.R;
import com.richardnarvaez.up.Utility.FirebaseUtil;
import com.richardnarvaez.up.Utility.GlideUtil;
import com.richardnarvaez.up.Utility.Utils;

import java.util.Objects;

public class UserNameActivity extends AppCompatActivity {

    private String TAG = "UserNameActivity";
    Author author = null;
    private ImageView userAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_name);

        final EditText userName = findViewById(R.id.username);
        userAvatar = findViewById(R.id.userAvatar);
        final EditText Name = findViewById(R.id.name);
        final TextInputLayout til = findViewById(R.id.text_input_layout_user_name);
        final TextInputLayout ti_name = findViewById(R.id.ti_name);
        final String[] username = new String[1];

        if (!FirebaseUtil.isLogin()) {
            Toast.makeText(this, "Existe un error grave...", Toast.LENGTH_SHORT).show();
            finish();
        }

        author = FirebaseUtil.getAuthorMain();
        GlideUtil.loadProfileIcon(author.getProfile_picture(), userAvatar);
        final String[] onlyname = {author.getName()};
        Name.setText(onlyname[0]);

        final RadioButton male = findViewById(R.id.radioButtonMale);

        userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        final boolean[] flag = {false};
        final boolean[] ok = {false};

        findViewById(R.id.aceptar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                flag[0] = false;
                username[0] = (userName.getText().toString()).toLowerCase().trim();

                FirebaseUtil.getPeopleRef().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        for (DataSnapshot IDGenerado : snapshot.getChildren()) {
                            String usernameTaken = IDGenerado.child("author").child("user_name").getValue(String.class);
                            if (username[0].equals(usernameTaken))
                                flag[0] = true;
                        }

                        if (flag[0] || username[0].length() <= 3) {
                            if (username[0].length() <= 3) {
                                til.setError("This name is short");
                            }
//                            if (username[0].length()) {
//                                til.setError("This name is longer");
//                            }

                            if (flag[0]) {
                                til.setError("This name is used");
                            }

                            ok[0] = false;
                        } else {
                            til.setError(null);
                            ok[0] = true;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });

                onlyname[0] = Name.getText().toString();

                if (ok[0]) {
                    til.setError(null);

                    Utils.guardar(onlyname[0], username[0], male.isChecked());
                }

            }
        });

//        android.app.FragmentManager manager = getFragmentManager();
//        android.app.Fragment frag = manager.findFragmentByTag("fragment_edit_name");
//        if (frag != null) {
//            manager.beginTransaction().remove(frag).commit();
//        }
//
//        Utils.newDialogMainData(UserNameActivity.this, getLayoutInflater().inflate(R.layout.fragment_username, null));

    }
}

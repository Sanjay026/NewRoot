package com.vijay.newroot;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PasswordActivity extends AppCompatActivity {
    private FirebaseUser user;
    private EditText oldText,newText,reText;
    private Button button;
    private String oldpass,newpass,repass,email;
    private ImageButton toggle1,toggle2,toggle3;
    boolean passwordnotvisibale=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        user= FirebaseAuth.getInstance().getCurrentUser();
        oldText= (EditText) findViewById(R.id.oldeditText);
        newText= (EditText) findViewById(R.id.oldeditText2);
        reText= (EditText) findViewById(R.id.oldeditText3);
        button= (Button) findViewById(R.id.passwordbutton2);
        toggle1= (ImageButton) findViewById(R.id.toggle_visibility1);
        toggle2= (ImageButton) findViewById(R.id.toggle_visibility2);
        toggle3= (ImageButton) findViewById(R.id.toggle_visibility3);

        toggle1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(passwordnotvisibale) {
                    oldText.setTransformationMethod(null);
                    toggle1.setImageResource(R.drawable.ic_visibility_off_black_24dp);
                    passwordnotvisibale=false;
                }
                else{
                    oldText.setTransformationMethod(new PasswordTransformationMethod());
                    toggle1.setImageResource(R.drawable.ic_visibility_black_24dp);
                    passwordnotvisibale=true;
                }
                return false;
            }
        });
        toggle2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(passwordnotvisibale) {
                    newText.setTransformationMethod(null);
                    toggle2.setImageResource(R.drawable.ic_visibility_off_black_24dp);
                    passwordnotvisibale=false;
                }
                else{
                    newText.setTransformationMethod(new PasswordTransformationMethod());
                    toggle2.setImageResource(R.drawable.ic_visibility_black_24dp);
                    passwordnotvisibale=true;
                }
                return false;
            }
        });
        toggle3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(passwordnotvisibale) {
                    reText.setTransformationMethod(null);
                    toggle3.setImageResource(R.drawable.ic_visibility_off_black_24dp);
                    passwordnotvisibale=false;
                }
                else{
                    reText.setTransformationMethod(new PasswordTransformationMethod());
                    toggle3.setImageResource(R.drawable.ic_visibility_black_24dp);
                    passwordnotvisibale=true;
                }
                return false;
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oldpass=oldText.getText().toString();
                newpass=newText.getText().toString();
                repass=reText.getText().toString();
                email=user.getEmail();
                if(TextUtils.isEmpty(oldpass) || TextUtils.isEmpty(newpass) || TextUtils.isEmpty(repass)){
                    Toast.makeText(getApplicationContext(),"Fields must not be empty",Toast.LENGTH_SHORT).show();
                }
                else{
                    if(!newpass.equals(repass)){
                        Toast.makeText(getApplicationContext(),"Password must be same",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        AuthCredential credential = EmailAuthProvider.getCredential(email,oldpass);
                        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    user.updatePassword(newpass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(!task.isSuccessful()){
                                                Toast.makeText(getApplicationContext(),"Something went wrong, Please Try again Later",Toast.LENGTH_LONG).show();
                                            }
                                            else{
                                                Toast.makeText(getApplicationContext(),"Password Succesfully Changed",Toast.LENGTH_LONG).show();
                                            }

                                        }
                                    });
                                }
                                else{
                                    Toast.makeText(getApplicationContext(),"Authentication Failed",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                }
            }
        });

    }
}

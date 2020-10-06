package com.example.tourist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tourist.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class ProfielActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{
    FloatingActionButton btnLogout;
    String Name,Email,UserName;
    TextView name,email,phone;
    DatabaseReference ref;
    protected BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiel);
        btnLogout = (FloatingActionButton)findViewById(R.id.logout);
        name = (TextView)findViewById(R.id.pname);
        email = (TextView)findViewById(R.id.pemail);
        phone = (TextView)findViewById(R.id.pphone);
        navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(this);
        SessionManagement sessionManagement = new SessionManagement(ProfielActivity.this);
        UserName = sessionManagement.getSession();
        ref= FirebaseDatabase.getInstance().getReference().child("Users").child(UserName);
        Start();
    }
    public void Start() {
        //add the ValueListener to the selected reference and set it to the textViews
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Name = dataSnapshot.child("name").getValue(String.class);
                name.setText(Name);
                Email = dataSnapshot.child("email").getValue(String.class);
                email.setText(Email);
                phone.setText(UserName);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }
    public void logout(View view) {
        //this method will remove session and open login screen
        SessionManagement sessionManagement = new SessionManagement(ProfielActivity.this);
        sessionManagement.removeSession();
        moveToLogin();
    }
    private void moveToLogin() {
        Intent intent = new Intent(ProfielActivity.this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {
        navigationView.postDelayed(new Runnable() {
            @Override
            public void run() {
                int itemId = item.getItemId();
                if (itemId == R.id.action_home) {
                    ProfielActivity.this.startActivity(new Intent(ProfielActivity.this, Dashboard.class));
                } else if (itemId == R.id.action_profile) {
                    ProfielActivity.this.startActivity(new Intent(ProfielActivity.this, ProfielActivity.class));
                } else if (itemId == R.id.action_favorite) {
                    ProfielActivity.this.startActivity(new Intent(ProfielActivity.this, favourits.class));
                }
                ProfielActivity.this.finish();
            }
        }, 300);
        return true;
    }
}

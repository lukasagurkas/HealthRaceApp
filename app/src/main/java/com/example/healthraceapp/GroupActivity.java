package com.example.healthraceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class GroupActivity extends AppCompatActivity {

    private FirebaseRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        Query query = FirebaseDatabase.getInstance("https://health-race-app-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference()
                .child("Users")
                .limitToLast(20);

        RecyclerView recyclerView = findViewById(R.id.recycle_view);

        // To display the Recycler view linearly
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();

        // FirebaseRecyclerAdapter binds a Query to a RecyclerView. When data is added, removed, or
        // changed these updates are automatically applied to the UI in real time.
        adapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(options) {
            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.user for each item
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user, parent, false);

                return new UserViewHolder(view);
            }

            // Function to bind the view in Card view (here "user.xml") with data in model class
            // (here "User.class")
            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull User model) {
                // Add username from model class (here "User.class") to appropriate view in Card view
                // (here "user.xml")
                holder.username.setText(model.getUsername());

                // Add email from model class (here "User.class") to appropriate view in Card view
                // (here "user.xml")
                holder.email.setText(model.getEmail());
            }
        };

        recyclerView.setAdapter(adapter);
    }

    // Begin listening for data when the activity starts
    @Override protected void onStart()
    {
        super.onStart();
        // Monitor changes in Firebase query
        adapter.startListening();
    }

    // Stop listening for data changes
    @Override protected void onStop()
    {
        super.onStop();
        adapter.stopListening();
    }

    // Class to create references of the views in Card view (here "User.xml")
    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView username, email;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.card_view_username_fill);
            email = itemView.findViewById(R.id.card_view_email_fill);
        }
    }
}
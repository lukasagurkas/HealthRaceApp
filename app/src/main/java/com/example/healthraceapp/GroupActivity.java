//package com.example.healthraceapp;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.app.ProgressDialog;
//import android.content.DialogInterface;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.firebase.ui.database.FirebaseRecyclerAdapter;
//import com.firebase.ui.database.FirebaseRecyclerOptions;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.Query;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//import java.util.Objects;
//
//public class GroupActivity extends AppCompatActivity {
//
//    // TODO extend recycle view horizontally
//    // TODO add group selection
//    // TODO add user to group
//    // TODO remove user from group
//    // TODO null toaster
//    // TODO admin privileges
//    // TODO sort by score
//
//    private static final String TAG = "GroupActivity";
//    private FirebaseRecyclerAdapter adapter;
//
//    private FirebaseAuth firebaseAuth;
//    private FirebaseDatabase firebaseDatabase;
//    private DatabaseReference currentUserReference;
//    private User user;
//    private String userID = "";
//
//    private DatabaseReference databaseReference;
//
//    // User for checking group name uniqueness
//    private final ArrayList<String> allGroupNames = new ArrayList<>();
//
//    // Used to get the group name of a new group from the AlertDialog
//    private String newGroupName = "";
//
//    private ViewGroup viewGroup = null;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.group_activity_view_group);
//
//        viewGroup = (ViewGroup) findViewById(R.id.constraintLayout);
//
//        firebaseAuth = FirebaseAuth.getInstance();
//        firebaseDatabase = FirebaseDatabase.getInstance("https://health-" +
//                "race-app-default-rtdb.europe-west1.firebasedatabase.app/");
//        userID = firebaseAuth.getCurrentUser().getUid();
//        databaseReference = firebaseDatabase.getReference();
//        currentUserReference = databaseReference.child("Users").child(userID);
//
//        getUserInitializeView();
//    }
//
//    private void getUserInitializeView() {
//        databaseReference.child("Users").child(userID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                if (!task.isSuccessful()) {
//                    Log.e("firebase", "Error getting data", task.getException());
//                } else {
//                    Log.d("firebase", String.valueOf(Objects.requireNonNull(task.getResult()).getValue()));
//                    user = task.getResult().getValue(User.class);
//                    assert user != null;
//                    if (user.getGroupNames().get(0).equals("")) {
//                        viewGroup.addView(View.inflate(getApplicationContext(), R.layout.activity_group_create_group, null));
//                    } else {
//                        setRecycleView();
//                    }
//                }
//            }
//        });
//    }
//
//    private void setRecycleView() {
//        viewGroup.removeAllViews();
//        viewGroup.addView(View.inflate(getApplicationContext(), R.layout.activity_group, null));
//
//        Query query = FirebaseDatabase.getInstance("https://health-race-app-default-rtdb.europe-west1.firebasedatabase.app/")
//                .getReference()
//                .child("Groups")
//                //.child(user.getGroupNames().get(1)) // TODO abstract to selected group
//                .child("lol")
//                .child("members")
//                .orderByChild("username"); // TODO when score is implemented update score inside members as well and then order by score
//
//        RecyclerView recyclerView = findViewById(R.id.recycle_view);
//
//        // To display the Recycler view linearly
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
//                .setQuery(query, User.class)
//                .build();
//
//        // FirebaseRecyclerAdapter binds a Query to a RecyclerView. When data is added, removed, or
//        // changed these updates are automatically applied to the UI in real time.
//        adapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(options) {
//            @NonNull
//            @Override
//            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                // Create a new instance of the ViewHolder, in this case we are using a custom
//                // layout called R.layout.user for each item
//                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user, parent, false);
//
//                return new UserViewHolder(view);
//            }
//
//            // Function to bind the view in Card view (here "user.xml") with data in model class
//            // (here "User.class")
//            @Override
//            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull User model) {
//                // Add username from model class (here "User.class") to appropriate view in Card view
//                // (here "user.xml")
//                holder.username.setText(model.getUsername());
//
//                // Add email from model class (here "User.class") to appropriate view in Card view
//                // (here "user.xml")
//                holder.email.setText(model.getEmail());
//            }
//        };
//
//        recyclerView.setAdapter(adapter);
//
//        // Monitor changes in Firebase query
//        adapter.startListening();
//    }
//
//    // Stop listening for data changes
//    @Override
//    protected void onStop() {
//        super.onStop();
//        if (adapter != null) {
//            adapter.stopListening();
//        }
//    }
//
//    // Class to create references of the views in Card view (here "User.xml")
//    static class UserViewHolder extends RecyclerView.ViewHolder {
//        TextView username, email;
//
//        public UserViewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            username = itemView.findViewById(R.id.card_view_username_fill);
//            email = itemView.findViewById(R.id.card_view_email_fill);
//        }
//    }
//
//    // Create an ActionBar button
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Instantiate XML file into its corresponding View object
//        getMenuInflater().inflate(R.menu.group_menu, menu);
//
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    // Handle ActionBar requests
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.createGroupButton) {
//            // Dialog to input the group name
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle("Enter group name");
//
//            // Setting up the input for the AlertDialog
//            final EditText input = new EditText(this);
//            builder.setView(input);
//
//            // Setting up the buttons for the AlertDialog
//            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    newGroupName = input.getText().toString();
//                    checkGroupNameUniqueness();
//                }
//            });
//
//            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.cancel();
//                }
//            });
//
//            builder.show();
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    private void checkGroupNameUniqueness() {
//        // Defining DatabaseReference object
//        DatabaseReference databaseReferenceGroups = firebaseDatabase.getReference("Groups");
//
//        databaseReferenceGroups.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    String temporaryGroupName = dataSnapshot.child("groupName").getValue(String.class);
//                    if (!allGroupNames.contains(temporaryGroupName)) {
//                        // Adding all unique group names to the ArrayList allGroupNames
//                        allGroupNames.add(temporaryGroupName);
//                    }
//                }
//                // After retrieving all the group names a new group can be created
//                createNewGroup();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                // Getting data was canceled
//                Log.w(TAG, "onCancelled", error.toException());
//            }
//        });
//    }
//
//    // Create new group
//    private void createNewGroup() {
//        if (allGroupNames.contains(newGroupName)) {
//            Toast.makeText(this, "This group name is already in use", Toast.LENGTH_LONG).show();
//            return;
//        }
//
//        if (newGroupName.length() < 1) {
//            Toast.makeText(this, "The group name has to contain at least one character", Toast.LENGTH_LONG).show();
//            return;
//        }
//
//        // New group
//        Group newGroup = new Group(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(), user, newGroupName);
//
//        // Add the new group to the Firebase database
//        DatabaseReference newGroupRef = firebaseDatabase.getReference("Groups");
//        newGroupRef.child(newGroup.getGroupName()).setValue(newGroup).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (!task.isSuccessful()) {
//                    Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
//                    Log.e("firebase", "Error getting data", task.getException());
//                } else {
//                    setRecycleView();
//
//                    firebaseDatabase.getReference("Users")
//                            .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
//                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task1) {
//                            if (!task1.isSuccessful()) {
//                                Toast.makeText(getApplicationContext(), Objects.requireNonNull(task1.getException()).getMessage(), Toast.LENGTH_LONG).show();
//                                Log.e("firebase", "Error getting data", task1.getException());
//                            } else {
//                                Toast.makeText(getApplicationContext(), String.valueOf(task1.getResult()), Toast.LENGTH_LONG).show();
//                                Log.d("firebase", String.valueOf(task1.getResult()));
//                            }
//                        }
//                    });
//
//                    /*DatabaseReference temporary = firebaseDatabase.getReference("Groups/" + newGroup.getGroupName() + "/members")
//                            .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
//                    temporary.setValue(user.getGroupNames()).addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task1) {
//                            if (!task1.isSuccessful()) {
//                                Toast.makeText(getApplicationContext(), Objects.requireNonNull(task1.getException()).getMessage(), Toast.LENGTH_LONG).show();
//                                Log.e("firebase", "Error getting data", task1.getException());
//                            } else {
//                                Toast.makeText(getApplicationContext(), String.valueOf(task1.getResult()), Toast.LENGTH_LONG).show();
//                                Log.d("firebase", String.valueOf(task1.getResult()));
//                            }
//                        }
//                    });*/
//                    Toast.makeText(getApplicationContext(), String.valueOf(task.getResult()), Toast.LENGTH_LONG).show();
//                    Log.d("firebase", String.valueOf(task.getResult()));
//                }
//            }
//        });
//    }
//}
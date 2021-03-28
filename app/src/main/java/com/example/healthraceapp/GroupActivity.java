package com.example.healthraceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GroupActivity extends AppCompatActivity {

    // TODO add group selection
    // TODO add user to group
    // TODO remove user from group
    // TODO admin privileges
    // TODO sort by score
    // TODO delete group

    private static final String TAG = "GroupActivity";
    private FirebaseRecyclerAdapter adapter;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference currentUserReference;
    private User user;
    private String userID = "";

    private DatabaseReference databaseReference;

    // User for checking group name uniqueness
    private ArrayList<String> allGroupNames = new ArrayList<>();

    // Used to get the group name of a new group from the AlertDialog
    private String newGroupName = "";

    private ViewGroup viewGroup = null;

    private String currentlySelectedGroup = "";

    private TextView nrOfMembersInGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_activity_view_group);

        viewGroup = (ViewGroup) findViewById(R.id.constraintLayout);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance("https://health-" +
                "race-app-default-rtdb.europe-west1.firebasedatabase.app/");
        userID = firebaseAuth.getCurrentUser().getUid();
        databaseReference = firebaseDatabase.getReference();
        currentUserReference = databaseReference.child("Users").child(userID);

        getUserInitializeView();
    }

    /*@Override
    protected void onResume() {
        super.onResume();
        userID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        getUserInitializeView();
    }*/

    private void getUserInitializeView() {
        databaseReference.child("Users").child(userID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Log.d("firebase", String.valueOf(Objects.requireNonNull(task.getResult()).getValue()));
                    user = task.getResult().getValue(User.class);
                    assert user != null;
                    if (user.getGroupNames().size() < 2) { // The user is not part of any group
                        // Show text telling the user to create a new group or be added to a group
                        viewGroup.removeAllViews();
                        viewGroup.addView(View.inflate(getApplicationContext(), R.layout.activity_group_create_group, null));
                    } else {
                        setRecycleView();
                    }
                }
            }
        });
    }

    private void setRecycleView() {
        // Clearing all the views
        viewGroup.removeAllViews();

        viewGroup.addView(View.inflate(getApplicationContext(), R.layout.activity_group, null));

        if (currentlySelectedGroup.equals("")) { // Spinner has not been initialized
            return;
        }

        Query query = FirebaseDatabase.getInstance("https://health-race-app-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference()
                .child("Groups")
                .child(currentlySelectedGroup) // TODO abstract to selected group
                .child("members")
                .orderByChild("negativeTotalPoints"); // TODO when score is implemented update score inside members as well and then order by score

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

                // Add score from model class (here "User.class") to appropriate view in Card view
                // (here "user.xml")
                holder.score.setText(String.valueOf(model.getTotalPoints()));

                nrOfMembersInGroup = findViewById(R.id.textViewNrMembersInGroup);

                nrOfMembersInGroup.setText(String.valueOf(recyclerView.getAdapter().getItemCount()));
            }
        };

        recyclerView.setAdapter(adapter);

        // Monitor changes in Firebase query
        adapter.startListening();
    }

    // Stop listening for data changes
    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    // Class to create references of the views in Card view (here "User.xml")
    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView username, email, score;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.card_view_username_fill);
            email = itemView.findViewById(R.id.card_view_email_fill);
            score = itemView.findViewById(R.id.card_view_score_fill);
        }
    }

    private void initializeSpinner() {
        databaseReference.child("Users").child(userID).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Log.d("firebase", String.valueOf(Objects.requireNonNull(task.getResult()).getValue()));
                    user = task.getResult().getValue(User.class);
                    // Getting all the groups that the user is part of
                    assert user != null;
                    allGroupNames = user.getGroupNames();

                    if (allGroupNames.size() < 2) {
                        ArrayList<String> temporaryList = new ArrayList<>();
                        temporaryList.add("Join a group");
                        Spinner spinner = (Spinner) findViewById(R.id.selectGroupSpinner);
                        ArrayAdapter<String> addressAdapter = new ArrayAdapter<String>(GroupActivity.this, R.layout.simple_spinner_item_adjusted, temporaryList);
                        addressAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(addressAdapter);

                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                Toast.makeText(GroupActivity.this, "To select a group you first need to create a group or be added to one", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                // Auto-generated method
                            }
                        });
                    } else {
                        ArrayList<String> allGroupNamesCopy = allGroupNames;
                        allGroupNamesCopy.remove("");
                        // After retrieving all the group names set up the Spinner
                        Spinner spinner = (Spinner) findViewById(R.id.selectGroupSpinner);
                        ArrayAdapter<String> addressAdapter = new ArrayAdapter<String>(GroupActivity.this, R.layout.simple_spinner_item_adjusted, allGroupNamesCopy);
                        addressAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(addressAdapter);

                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                currentlySelectedGroup = parent.getItemAtPosition(position).toString();
                                setRecycleView(); // Once the group is selected we need to set the RecyclerView
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                                // Auto-generated method
                            }
                        });
                    }
                }
            }
        });
    }

    // Create an ActionBar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Instantiate XML file into its corresponding View object
        getMenuInflater().inflate(R.menu.group_menu, menu);

        initializeSpinner();

        return super.onCreateOptionsMenu(menu);
    }

    // Handle ActionBar requests
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.createGroupButton) {
            // Dialog to input the group name
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Enter group name");

            // Setting up the input for the AlertDialog
            final EditText input = new EditText(this);
            builder.setView(input);

            // Setting up the buttons for the AlertDialog
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    newGroupName = input.getText().toString();
                    checkGroupNameUniqueness(true, input.getText().toString());
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();

            // These other functions require administrative privileges
        } else if (item.getItemId() == R.id.addUserToGroup || item.getItemId() == R.id.removeUserFromGroup || item.getItemId() == R.id.deleteGroup || item.getItemId() == R.id.changeGroupName) {
            databaseReference.child("Groups").child(currentlySelectedGroup).child("adminUID").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error getting data", task.getException());
                    } else {
                        Log.d("firebase", String.valueOf(Objects.requireNonNull(task.getResult().getValue())));
                        // Checking if the user is the admin of the selected group
                        if (task.getResult().getValue(String.class).equals(userID)) {
                            // We have to call this method because we have to wait for the retrieval
                            // of the data before we can continue processing the item selection
                            continueProcessingOnOptionsItemSelected(item);
                        } else { // The current user is not the admin of the selected group
                            Toast.makeText(GroupActivity.this, "You do not have administrative privileges in this group", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }

    private void continueProcessingOnOptionsItemSelected(MenuItem item) {
        // All other option items require admin privileges
        // Dialog to input the user being added
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (item.getItemId() == R.id.addUserToGroup) {
            builder.setTitle("Enter username to add member");
        } else if (item.getItemId() == R.id.removeUserFromGroup) {
            builder.setTitle("Enter username to remove member");
        } else if (item.getItemId() == R.id.deleteGroup) {
            builder.setTitle("Enter group name to delete the group");
        } else if (item.getItemId() == R.id.changeGroupName) {
            builder.setTitle("Enter new group name");
        }

        // Setting up the input for the AlertDialog
        final EditText input = new EditText(this);
        builder.setView(input);

        // Setting up the buttons for the AlertDialog
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (item.getItemId() == R.id.addUserToGroup) {
                    if (input.getText().toString().equals(user.getUsername())) {
                        Toast.makeText(GroupActivity.this, "You are already in the group", Toast.LENGTH_LONG).show();
                    } else {
                        addUserToGroup(input.getText().toString());
                    }
                } else if (item.getItemId() == R.id.removeUserFromGroup) {
                    if (input.getText().toString().equals(user.getUsername())) {
                        Toast.makeText(GroupActivity.this,
                                "You cannot remove yourself from the group. If you wish to exit the group then you will have to delete it",
                                Toast.LENGTH_LONG).show();
                    } else {
                        removeUserFromGroup(input.getText().toString());
                    }
                } else if (item.getItemId() == R.id.changeGroupName) {
                    // We need to check if the given group name is already taken
                    checkGroupNameUniqueness(false, input.getText().toString());
                } else if (item.getItemId() == R.id.deleteGroup) {
                    if (input.getText().toString().equals(currentlySelectedGroup)) {
                        // If the correct group name was entered for the selected group
                        // the group is deleted
                        deleteGroup(input.getText().toString());
                    } else {
                        Toast.makeText(GroupActivity.this, "Incorrect group name entered", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void addUserToGroup(String username) {
        // Has to be final boolean array because it is accessed from an inner class
        final boolean[] userExists = new boolean[1];
        firebaseDatabase.getReference("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.child("username").getValue(String.class).equals(username)) {
                        userExists[0] = true;
                        User selectedUser = dataSnapshot.getValue(User.class);

                        databaseReference.child("Groups").child(currentlySelectedGroup).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (!task.isSuccessful()) {
                                    Log.e("firebase", "Error getting data", task.getException());
                                } else {
                                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                                    Group selectedGroup = task.getResult().getValue(Group.class);

                                    // Adding the new member to the group
                                    selectedGroup.addMember(selectedUser.getUsername(), selectedUser, currentlySelectedGroup);

                                    databaseReference.child("Groups").child(currentlySelectedGroup).child("members").child(selectedUser.getUsername()).setValue(selectedUser);

                                    databaseReference.child("Users").child(dataSnapshot.getKey()).setValue(selectedUser);

                                    Toast.makeText(GroupActivity.this, username + " has been added to the group", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }
                if (!userExists[0]) {
                    Toast.makeText(GroupActivity.this, "A user with this username does not exist", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Getting data was canceled
                Log.w(TAG, "onCancelled", error.toException());
            }
        });
    }

    private void removeUserFromGroup(String username) {
        // Has to be final boolean array because it is accessed from an inner class
        final boolean[] userExists = new boolean[1];
        firebaseDatabase.getReference("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.child("username").getValue(String.class).equals(username)) {
                        userExists[0] = true;
                        User selectedUser = dataSnapshot.getValue(User.class);

                        databaseReference.child("Groups").child(currentlySelectedGroup).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (!task.isSuccessful()) {
                                    Log.e("firebase", "Error getting data", task.getException());
                                } else {
                                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                                    Group selectedGroup = task.getResult().getValue(Group.class);

                                    // Adding the new member to the group
                                    selectedGroup.removeMember(selectedUser.getUsername(), selectedUser, currentlySelectedGroup);

                                    databaseReference.child("Groups").child(currentlySelectedGroup).child("members").child(selectedUser.getUsername()).removeValue();

                                    databaseReference.child("Users").child(dataSnapshot.getKey()).setValue(selectedUser);

                                    setRecycleView();

                                    Toast.makeText(GroupActivity.this, username + " has been deleted from the group", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }
                if (!userExists[0]) {
                    Toast.makeText(GroupActivity.this, "A user with this username does not exist", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Getting data was canceled
                Log.w(TAG, "onCancelled", error.toException());
            }
        });
    }

    private void deleteGroup(String groupName) {
        databaseReference.child("Groups").child(groupName).child("members").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    HashMap<String, User> allMembers = (HashMap<String, User>) task.getResult().getValue();

                    databaseReference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                if (allMembers.containsKey(dataSnapshot.child("username").getValue(String.class))) {
                                    User tempUser = dataSnapshot.getValue(User.class);
                                    tempUser.exitGroup(groupName);
                                    databaseReference.child("Users").child(dataSnapshot.getKey()).setValue(tempUser);
                                }
                            }
                            databaseReference.child("Groups").child(currentlySelectedGroup).removeValue();

                            getUserInitializeView();
                            initializeSpinner();

                            Toast.makeText(GroupActivity.this, "The group has been deleted", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
    }

    private void changeGroupName(String groupNameNew) {
        if (allGroupNames.contains(groupNameNew)) {
            Toast.makeText(this, "This group name is already in use", Toast.LENGTH_LONG).show();
            return;
        }

        if (groupNameNew.length() > 10) {
            Toast.makeText(this, "The group name has to contain at most ten characters", Toast.LENGTH_LONG).show();
            return;
        }

        if (groupNameNew.length() < 1) {
            Toast.makeText(this, "The group name has to contain at least one character", Toast.LENGTH_LONG).show();
            return;
        }

        databaseReference.child("Groups").child(currentlySelectedGroup).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));

                    Group currentGroup = task.getResult().getValue(Group.class);

                    currentGroup.setGroupName(groupNameNew);

                    HashMap<String, User> allMembers = (HashMap<String, User>) currentGroup.getMembers();

                    databaseReference.child("Groups").child(currentlySelectedGroup).removeValue();

                    databaseReference.child("Groups").child(groupNameNew).setValue(currentGroup);

                    databaseReference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                if (allMembers.containsKey(dataSnapshot.child("username").getValue(String.class))) {
                                    User tempUser = dataSnapshot.getValue(User.class);
                                    tempUser.exitGroup(currentlySelectedGroup);
                                    tempUser.addGroup(groupNameNew);
                                    databaseReference.child("Users").child(dataSnapshot.getKey()).setValue(tempUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (!task.isSuccessful()) {
                                                Log.e("firebase", task.getException().getMessage());
                                            } else {
                                                Toast.makeText(GroupActivity.this, "The group name has been changed", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                            }

                            getUserInitializeView();
                            initializeSpinner();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
    }


    private void checkGroupNameUniqueness(boolean createNewGroup, String newGroupName1) {
        // Defining DatabaseReference object
        DatabaseReference databaseReferenceGroups = firebaseDatabase.getReference("Groups");

        databaseReferenceGroups.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String temporaryGroupName = dataSnapshot.child("groupName").getValue(String.class);
                    if (!allGroupNames.contains(temporaryGroupName)) {
                        // Adding all unique group names to the ArrayList allGroupNames
                        allGroupNames.add(temporaryGroupName);
                    }
                }
                if (createNewGroup) {
                    // After retrieving all the group names a new group can be created
                    createNewGroup();
                } else { // The second function of this method is to change the group name of the
                    // selected group
                    changeGroupName(newGroupName1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Getting data was canceled
                Log.w(TAG, "onCancelled", error.toException());
            }
        });
    }

    // Create new group
    private void createNewGroup() {
        if (allGroupNames.contains(newGroupName)) {
            Toast.makeText(this, "This group name is already in use", Toast.LENGTH_LONG).show();
            return;
        }

        if (newGroupName.length() > 10) {
            Toast.makeText(this, "The group name has to contain at most ten characters", Toast.LENGTH_LONG).show();
            return;
        }

        if (newGroupName.length() < 1) {
            Toast.makeText(this, "The group name has to contain at least one character", Toast.LENGTH_LONG).show();
            return;
        }

        // New group
        Group newGroup = new Group(user.getUsername(), Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(), user, newGroupName);

        // Add the new group to the Firebase database
        DatabaseReference newGroupRef = firebaseDatabase.getReference("Groups");
        newGroupRef.child(newGroup.getGroupName()).setValue(newGroup).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Toast.makeText(GroupActivity.this, "Group has been created", Toast.LENGTH_LONG).show();

                    setRecycleView();

                    firebaseDatabase.getReference("Users")
                            .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task1) {
                            if (!task1.isSuccessful()) {
                                Log.e("firebase", "Error getting data", task1.getException());
                            } else {
                                Log.d("firebase", String.valueOf(task1.getResult()));
                                initializeSpinner();
                                userID = firebaseAuth.getCurrentUser().getUid();
                                getUserInitializeView();
                            }
                        }
                    });
                    Log.d("firebase", String.valueOf(task.getResult()));
                }
            }
        });
    }
}
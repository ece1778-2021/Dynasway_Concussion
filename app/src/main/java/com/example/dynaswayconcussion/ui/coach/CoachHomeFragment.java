package com.example.dynaswayconcussion.ui.coach;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dynaswayconcussion.R;
import com.example.dynaswayconcussion.ui.CircularImageView;
import com.example.dynaswayconcussion.ui.calendar.CalendarFragment;
import com.example.dynaswayconcussion.ui.home.TeamsConnectionFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class CoachHomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    SwipeRefreshLayout swipeRefreshLayout;

    TextView hello_textview;
    CircularImageView profile_imageview;

    Button profileCodeButton;
    Button scanCodeButton;

    LinearLayout connectionsLayout;
    List<String> athleteUidList = new ArrayList<>();
    Hashtable<String, String> athleteNameMap = new Hashtable<String, String>();

    CollectionReference connectionsRef;

    public CoachHomeFragment() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        connectionsRef = db.collection("connections");

//        athleteUidList.add("GT8ODAet7scHJpt9yNCvetnuqTr1");
//        athleteUidList.add("CDevveNn3FMpNcTaRivb2J4AWa62");
//        athleteUidList.add("6uQDOxsPc4Yb1G5LKLI2EoTZ8Lo1");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_coach_home, container, false);

        hello_textview = (TextView) view.findViewById(R.id.home_hello_textview);
        profile_imageview = (CircularImageView) view.findViewById(R.id.home_profile_imageview);
        connectionsLayout = (LinearLayout) view.findViewById(R.id.linearLayoutAthleteList);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(CoachHomeFragment.this);

        scanCodeButton = view.findViewById(R.id.coach_connect_button);
        scanCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startQRScanner();
            }
        });

        profileCodeButton = view.findViewById(R.id.coach_profile_code_button);
        profileCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TeamsConnectionFragment frag = TeamsConnectionFragment.newInstance(mAuth.getCurrentUser().getUid());
                //FragmentTransaction fr = getFragmentManager().beginTransaction();
                getActivity().getSupportFragmentManager().beginTransaction().replace(android.R.id.content, frag, "tag")
                        .addToBackStack("backstack")
                        .commit();
                //fr.replace(R.id.host, frag).addToBackStack( "backstack");
                //fr.commit();
            }
        });

        loadProfileInfo();
        // addButtons();

        return view;
    }

    private void searchConnections()
    {
        Query connectionsQuery = connectionsRef.whereEqualTo("user_uid1", mAuth.getCurrentUser().getUid());
        connectionsQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots)
                {
                    if (!athleteUidList.contains(queryDocumentSnapshot.getString("user_uid2")))
                    {
                        athleteUidList.add(queryDocumentSnapshot.getString("user_uid2"));
                    }
                }
                searchNames();
            }
        });
    }

    private void searchNames()
    {
        for (String uid : athleteUidList)
        {
            if (!athleteNameMap.containsKey(uid))
            {
                DocumentReference ref = db.collection("users").document(uid);
                ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null) {
                                String firstName = document.getString("first_name");
                                String lastName = document.getString("last_name");
                                String fullName = firstName + " " + lastName;
                                athleteNameMap.put(uid, fullName);
                                addButton(fullName, uid);
                            }
                        }
                    }
                });
            }
        }
    }

    private void addButton(String name, String uid)
    {
        Button btn = new Button(getContext());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(4, 8, 4, 8);
        btn.setLayoutParams(params);

        btn.setBackgroundTintList(this.getResources().getColorStateList(R.color.main_green));
        btn.setOnClickListener(btnAthleteClick);
        btn.setTag(uid);

        btn.setText(name);
        connectionsLayout.addView(btn);
    }

    private void addButtons()
    {
        athleteNameMap.forEach((k, v) -> {
            addButton(v, k);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        athleteUidList.clear();
        athleteNameMap.clear();
        searchConnections();
    }

    View.OnClickListener btnAthleteClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CalendarFragment frag = CalendarFragment.newInstance((String) v.getTag());
            getActivity().getSupportFragmentManager().beginTransaction().replace(android.R.id.content, frag, "tag")
                    .addToBackStack("backstack")
                    .commit();
        }
    };

    private void loadProfileInfo () {
        db.collection("users").document(mAuth.getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String name = (String)document.get("first_name");
                    String image_uri = (String)document.get("profile_image_ref");
                    hello_textview.setText("Welcome back, " + name);
                    if (!image_uri.equals("")) {
                        Picasso.get()
                                .load(image_uri)
                                .resize(250, 250)
                                .centerCrop()
                                .into(profile_imageview);
                    }
                }
            }
        });
    }

    private void startQRScanner() {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scan your coach's code");
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
        //IntentIntegrator.forSupportFragment(this).initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("HOME_INFO", "Read QR code");
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(getActivity(), "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                setUpConnectionInCloud(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void setUpConnectionInCloud(String athleteUID) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser().getUid().equals(athleteUID)) {
            Toast.makeText(getActivity(), "Code is user's own code", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, Object> data = new HashMap<>();
        data.put("user_uid1", mAuth.getCurrentUser().getUid());
        data.put("user_uid2", athleteUID);

        CollectionReference connections = db.collection("connections");

        Query existsQuery = connections.whereEqualTo("user_uid1", mAuth.getCurrentUser().getUid()).whereEqualTo("user_uid2", athleteUID);

        existsQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.size() > 0) {
                    Toast.makeText(getActivity(), "Connection already exists", Toast.LENGTH_SHORT).show();
                }
                else {
                    connections.add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(getActivity(), "Connection completed!", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Teams connection failed (Errno: 2)", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Teams connection failed (Errno: 1)", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRefresh() {
        searchConnections();
        swipeRefreshLayout.setRefreshing(false);
    }
}
package com.example.dynaswayconcussion.ui.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.dynaswayconcussion.R;
import com.example.dynaswayconcussion.ui.CircularImageView;
import com.example.dynaswayconcussion.ui.tests.StaticTestSelectionFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

public class HomeFragment extends Fragment {

    //Firebase variables
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    //Component related variables
    Button btnAboutConcussions;
    Button btnTreatmentOptions;
    Button btnGetInvolved;
    Button btnConcussionStats;
    Button btnProfileCode;
    Button btnConnectCoach;
    Button btnLogConcussion;
    Button btnAboutApp;

    TextView hello_textview;

    CircularImageView profile_imageview;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        btnAboutConcussions = (Button)view.findViewById(R.id.home_about_concussions_button);
        btnTreatmentOptions = (Button)view.findViewById(R.id.home_treatment_options_button);
        btnGetInvolved = (Button)view.findViewById(R.id.home_get_involved_button);
        btnConcussionStats = (Button)view.findViewById(R.id.home_concussion_stats_button);

        btnProfileCode = (Button) view.findViewById(R.id.home_profile_code);
        btnConnectCoach = (Button) view.findViewById(R.id.home_connect_coach);
        btnLogConcussion = (Button) view.findViewById(R.id.home_log_inujry);
        btnAboutApp = (Button) view.findViewById(R.id.home_about_the_app);

        hello_textview = (TextView)view.findViewById(R.id.home_hello_textview);
        profile_imageview = (CircularImageView)view.findViewById(R.id.home_profile_imageview);

        btnAboutConcussions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://concussionsontario.org/patienteducation/aboutconcussions-2/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        btnTreatmentOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://concussionfoundation.org/concussion-resources/treatments");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        btnGetInvolved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.concussionfoundation.ca/programs-projects/programs");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        btnConcussionStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://concussionsontario.org/access-to-care/concussion-data/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        btnProfileCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TeamsConnectionFragment frag = TeamsConnectionFragment.newInstance(mAuth.getCurrentUser().getUid());
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.nav_host_fragment, frag).addToBackStack( "backstack");
                fr.commit();
            }
        });

        btnConnectCoach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnLogConcussion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnAboutApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        loadProfileInfo();
        return view;
    }

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
}
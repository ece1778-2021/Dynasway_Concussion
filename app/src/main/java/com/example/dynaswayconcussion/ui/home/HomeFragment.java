package com.example.dynaswayconcussion.ui.home;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.dynaswayconcussion.R;
import com.example.dynaswayconcussion.ui.CircularImageView;
import com.example.dynaswayconcussion.ui.tests.StaticTestSelectionFragment;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    CardView notificationCard;
    TextView notificationTextView;

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

        btnProfileCode = (Button) view.findViewById(R.id.coach_profile_code_button);
        btnConnectCoach = (Button) view.findViewById(R.id.coach_connect_button);
        btnLogConcussion = (Button) view.findViewById(R.id.home_log_inujry);
        btnAboutApp = (Button) view.findViewById(R.id.home_about_the_app);

        hello_textview = (TextView)view.findViewById(R.id.home_hello_textview);
        profile_imageview = (CircularImageView)view.findViewById(R.id.home_profile_imageview);
        notificationCard = (CardView)view.findViewById(R.id.notification_card);
        notificationTextView = (TextView)view.findViewById(R.id.home_notification_textview);

        btnAboutConcussions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://concussionontario.org/patienteducation/aboutconcussions-2/");
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
                startQRScanner();
            }
        });

        btnLogConcussion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadConcussionDate();
            }
        });

        btnAboutApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.nav_host_fragment, new AboutFragment()).addToBackStack( "backstack" );
                fr.commit();
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
        updateNotification();
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

    private void setUpConnectionInCloud(String coachUID) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser().getUid().equals(coachUID)) {
            Toast.makeText(getActivity(), "Code is user's own code", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, Object> data = new HashMap<>();
        data.put("user_uid1", coachUID);
        data.put("user_uid2", mAuth.getCurrentUser().getUid());

        CollectionReference connections = db.collection("connections");

        Query existsQuery = connections.whereEqualTo("user_uid1", coachUID).whereEqualTo("user_uid2", mAuth.getCurrentUser().getUid());

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

    private void updateNotification() {
        final String REGULAR_STANDING_STRING = getResources().getString(R.string.static_test_regular);
        final String TANDEM_STANDING_STRING = getResources().getString(R.string.static_test_tandem);
        final String REGULAR_STANDING_COGNITIVE_STRING = getResources().getString(R.string.static_test_regular_dual_task);
        final String TANDEM_STANDING_COGNITIVE_STRING = getResources().getString(R.string.static_test_tandem_dual_task);

        final String REGULAR_DYNAMIC_STRING = getResources().getString(R.string.dynamic_test_regular);
        final String TANDEM_DYNAMIC_STRING = getResources().getString(R.string.dynamic_test_tandem);
        final String REGULAR_DYNAMIC_COGNITIVE_STRING = getResources().getString(R.string.dynamic_test_regular_dual_task);
        final String TANDEM_DYNAMIC_COGNITIVE_STRING = getResources().getString(R.string.dynamic_test_tandem_dual_task);
        String[] testTypeStrings = {REGULAR_STANDING_STRING, TANDEM_STANDING_STRING, REGULAR_STANDING_COGNITIVE_STRING, TANDEM_STANDING_COGNITIVE_STRING,
                                    REGULAR_DYNAMIC_STRING, TANDEM_DYNAMIC_STRING, REGULAR_DYNAMIC_COGNITIVE_STRING, TANDEM_DYNAMIC_COGNITIVE_STRING};
        Context toastContext = getActivity();
        CollectionReference testsRef = db.collection("test_results");
        Query notificationQuery = testsRef.whereEqualTo("is_baseline", true).whereEqualTo("user_uid", mAuth.getCurrentUser().getUid());
        notificationQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.size() > 0) {
                    if (queryDocumentSnapshots.size() >= 8) {
                        notificationCard.setCardBackgroundColor(getResources().getColor(R.color.main_green));
                        notificationTextView.setText("No new notifications!");
                    }
                    else {
                        int baselinesLeft = 8;
                        List<Integer> existingBaselinesIDs = new ArrayList<>();
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            baselinesLeft--;
                            existingBaselinesIDs.add(getTestTypeIDForNotification(document.getString("test_type")));
                        }
                        String resultNotification = "You have " + baselinesLeft + " baseline tests left to complete! Go ahead before the season starts! The following still haven't been completed: ";
                        for (int i = 0; i < 8; i++) {
                            boolean isInCompletedBaselines = false;
                            for (Integer id : existingBaselinesIDs) {
                                if (id.equals(i)) {
                                    isInCompletedBaselines = true;
                                    break;
                                }
                            }
                            if (i < 8) {
                                if (!isInCompletedBaselines) {
                                    resultNotification += testTypeStrings[i] + ", ";
                                }
                            }
                            else {
                                if (!isInCompletedBaselines) {
                                    resultNotification += testTypeStrings[i] + ".";
                                }
                            }
                        }
                        notificationTextView.setText(resultNotification);
                    }
                }
                else {
                    notificationTextView.setText("No baseline tests detected. You should do your baseline testing.");
                }
                notificationCard.setVisibility(View.VISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(toastContext, "Failed to get test data", Toast.LENGTH_SHORT).show();
                notificationCard.setVisibility(View.GONE);
            }
        });
    }

    private int getTestTypeIDForNotification(String testType) {
        final String REGULAR_STANDING_STRING = getResources().getString(R.string.static_test_regular_constant);
        final String TANDEM_STANDING_STRING = getResources().getString(R.string.static_test_tandem_constant);
        final String REGULAR_STANDING_COGNITIVE_STRING = getResources().getString(R.string.static_test_regular_dual_task_constant);
        final String TANDEM_STANDING_COGNITIVE_STRING = getResources().getString(R.string.static_test_tandem_dual_task_constant);

        final String REGULAR_DYNAMIC_STRING = getResources().getString(R.string.dynamic_test_regular_constant);
        final String TANDEM_DYNAMIC_STRING = getResources().getString(R.string.dynamic_test_tandem_constant);
        final String REGULAR_DYNAMIC_COGNITIVE_STRING = getResources().getString(R.string.dynamic_test_regular_dual_task_constant);
        final String TANDEM_DYNAMIC_COGNITIVE_STRING = getResources().getString(R.string.dynamic_test_tandem_dual_task_constant);

        if (testType.equals(REGULAR_STANDING_STRING)) {
            return 0;
        }
        else if (testType.equals(TANDEM_STANDING_STRING)) {
            return 1;
        }
        else if (testType.equals(REGULAR_STANDING_COGNITIVE_STRING)) {
            return 2;
        }
        else if (testType.equals(TANDEM_STANDING_COGNITIVE_STRING)) {
            return 3;
        }
        else if (testType.equals(REGULAR_DYNAMIC_STRING)) {
            return 4;
        }
        else if (testType.equals(TANDEM_DYNAMIC_STRING)) {
            return 5;
        }
        else if (testType.equals(REGULAR_DYNAMIC_COGNITIVE_STRING)) {
            return 6;
        }
        else if (testType.equals(TANDEM_DYNAMIC_COGNITIVE_STRING)) {
            return 7;
        }
        else {
            return -1;
        }
    }

    private void uploadConcussionDate() {
        long timestamp = System.currentTimeMillis();
        Map<String, Object> data = new HashMap<>();
        data.put("timestamp", timestamp);
        data.put("user_uid", mAuth.getCurrentUser().getUid());
        Context toastContext = getActivity();
        db.collection("injury_date").add(data).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                Toast.makeText(toastContext, "Concussion date set succesfully!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
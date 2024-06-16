package com.vatsal.voltstudy.my_courses_section;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.vatsal.voltstudy.R;
import com.vatsal.voltstudy.auth_controller.LoginActivity;
import com.vatsal.voltstudy.home_section.HomeActivity;
import com.vatsal.voltstudy.models.SavedCourses;
import com.vatsal.voltstudy.search_section.SearchActivity;
import com.vatsal.voltstudy.viewholders.SavedCoursesListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SavedCoursesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SavedCoursesFragment extends Fragment {

    private FirebaseAuth auth;

    private RecyclerView mRecyclerViewCourse;
    private SavedCoursesListAdapter courseAdapter;

    private ProgressBar progressBar;
    CardView notSignedIn,notSaved;
    Button btnSignIn,btnNPHome,btnNPSearch;

    // Model Variables
    private ArrayList<String> courseName = new ArrayList<>();
    private ArrayList<String> courseCode = new ArrayList<>();
    private ArrayList<String> courseAuthor = new ArrayList<>();
    private ArrayList<String> courseLanguage = new ArrayList<>();
    private ArrayList<String> courseCategory = new ArrayList<>();
    private ArrayList<String> courseSubCategory = new ArrayList<>();
    private ArrayList<String> courseImageUrl = new ArrayList<>();
    private ArrayList<String> savedCourseKey= new ArrayList<>();
    private ArrayList<String> courseType = new ArrayList<>();
    private ArrayList<String> courseDesc = new ArrayList<>();
    private ArrayList<Double> coursePrice = new ArrayList<>();


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SavedCoursesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SavedCoursesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SavedCoursesFragment newInstance(String param1, String param2) {
        SavedCoursesFragment fragment = new SavedCoursesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewLayout = inflater.inflate(R.layout.fragment_saved_courses, container, false);

        auth = FirebaseAuth.getInstance();

        progressBar = viewLayout.findViewById(R.id.progress_saved_courses);

        if(auth.getCurrentUser()!= null) {
            mRecyclerViewCourse = viewLayout.findViewById(R.id.rvSavedCourses);
            courseAdapter = new SavedCoursesListAdapter(getActivity(), courseName, courseAuthor, courseLanguage, courseCode, courseCategory, courseSubCategory, courseImageUrl, savedCourseKey, courseType, courseDesc, coursePrice);
            progressBar.setVisibility(View.VISIBLE);
            getFirebaseCourseData();
        }
        else {
            notSignedIn = viewLayout.findViewById(R.id.card_not_signed_in_saved_courses);
            notSignedIn.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            btnSignIn = viewLayout.findViewById(R.id.sign_up_button_unlock_saved_courses);
            btnSignIn.setOnClickListener(view -> {
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
        }
        setHasOptionsMenu(true);
        return viewLayout;

    }



    /* Getting Data From Firebase for Courses */
    private void getFirebaseCourseData() {

        FirebaseDatabase.getInstance().getReference().child("savedCourses").child(auth.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {

                            courseName.clear();
                            courseAuthor.clear();
                            courseLanguage.clear();
                            courseCategory.clear();
                            courseSubCategory.clear();
                            courseCode.clear();
                            courseImageUrl.clear();
                            savedCourseKey.clear();
                            courseType.clear();
                            courseDesc.clear();
                            coursePrice.clear();

                            for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                                fetchCourseData(singleSnapshot);
                            }
                        }
                        else{
                            notSaved = getView().findViewById(R.id.card_not_saved);
                            notSaved.setVisibility(View.VISIBLE);
                            btnNPHome = getView().findViewById(R.id.btn_saved_courses_to_home);
                            btnNPHome.setOnClickListener(view -> {
                                startActivity(new Intent(getActivity(), HomeActivity.class));
                                getActivity().overridePendingTransition(0, 0);
                            });
                            btnNPSearch = getView().findViewById(R.id.btn_saved_courses_to_search);
                            btnNPSearch.setOnClickListener(view -> {
                                startActivity(new Intent(getActivity(), SearchActivity.class));
                                getActivity().overridePendingTransition(0, 0);
                            });
                            progressBar.setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }
    private void fetchCourseData(DataSnapshot singleSnapshot) {
        SavedCourses course = singleSnapshot.getValue(SavedCourses.class);

        if (course != null) {
            courseName.add(course.getCourseName());
            courseAuthor.add(course.getCourseAuthor());
            courseLanguage.add(course.getCourseLanguage());
            courseCode.add(course.getCourseCode());
            courseCategory.add(course.getCourseCategory());
            courseSubCategory.add(course.getCourseSubCategory());
            courseImageUrl.add(course.getCourseImageUrl());
            savedCourseKey.add(course.getSaveCourseKey());
            courseType.add(course.getCourseType());
            courseDesc.add(course.getCourseDesc());
            coursePrice.add(course.getCoursePrice());
            initializeCourse();
        }
        progressBar.setVisibility(View.GONE);

    }
    private void initializeCourse() {

        LinearLayoutManager manager1 = new LinearLayoutManager(getActivity());
        manager1.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerViewCourse.setLayoutManager(manager1);

        mRecyclerViewCourse.setAdapter(courseAdapter);
        courseAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_saved_courses, menu);
    }
}
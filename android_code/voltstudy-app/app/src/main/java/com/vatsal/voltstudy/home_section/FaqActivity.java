package com.vatsal.voltstudy.home_section;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import com.vatsal.voltstudy.R;
import com.vatsal.voltstudy.viewholders.ExpandableListViewAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/** FAQ Activity */

public class FaqActivity extends AppCompatActivity {

    private ExpandableListView expandableListView;
    private ExpandableListViewAdapter expandableListViewAdapter;
    private List<String> listDataGroup;
    private HashMap<String, List<String>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        getSupportActionBar();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // initializing the views
        initViews();

        // initializing the objects
        initObjects();

        // preparing list data
        initListData();
    }
    /**
     * method to initialize the views
     */
    private void initViews() {

        expandableListView = findViewById(R.id.expandableListView);

    }


    /**
     * method to initialize the objects
     */
    private void initObjects() {

        // initializing the list of groups
        listDataGroup = new ArrayList<>();

        // initializing the list of child
        listDataChild = new HashMap<>();

        // initializing the adapter object
        expandableListViewAdapter = new ExpandableListViewAdapter(this, listDataGroup, listDataChild);

        // setting list adapter
        expandableListView.setAdapter(expandableListViewAdapter);

    }

    private void initListData() {


        // Adding group data
        listDataGroup.add(getString(R.string.text_que1));
        listDataGroup.add(getString(R.string.text_que2));
        listDataGroup.add(getString(R.string.text_que3));
        listDataGroup.add(getString(R.string.text_que4));
        listDataGroup.add(getString(R.string.text_que5));


        String[] array;

        List<String> que1List = new ArrayList<>();
        array = getResources().getStringArray(R.array.string_array_que1);
        for (String item : array) {
            que1List.add(item);
        }

        List<String> que2List = new ArrayList<>();
        array = getResources().getStringArray(R.array.string_array_que2);
        for (String item : array) {
            que2List.add(item);
        }

        List<String> que3List = new ArrayList<>();
        array = getResources().getStringArray(R.array.string_array_que3);
        for (String item : array) {
            que3List.add(item);
        }

        List<String> que4List = new ArrayList<>();
        array = getResources().getStringArray(R.array.string_array_que4);
        for (String item : array) {
            que4List.add(item);
        }

        List<String> que5List = new ArrayList<>();
        array = getResources().getStringArray(R.array.string_array_que5);
        for (String item : array) {
            que5List.add(item);
        }

        // Adding child data
        listDataChild.put(listDataGroup.get(0), que1List);
        listDataChild.put(listDataGroup.get(1), que2List);
        listDataChild.put(listDataGroup.get(2), que3List);
        listDataChild.put(listDataGroup.get(3), que4List);
        listDataChild.put(listDataGroup.get(4), que5List);

        // notify the adapter
        expandableListViewAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
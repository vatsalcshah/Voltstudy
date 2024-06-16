package com.vatsal.voltstudy.discussion_section;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.vatsal.voltstudy.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/** Used to display information to user when clicked on icon of Action Bar */

public class DiscussionRulesSheetFragment extends BottomSheetDialogFragment {

    public DiscussionRulesSheetFragment () {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_discussionrules,container,false);
    }
}

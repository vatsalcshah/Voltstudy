package com.vatsal.voltstudy.viewholders;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.vatsal.voltstudy.R;
import com.vatsal.voltstudy.home_section.CourseDetailActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class SavedCoursesListAdapter extends RecyclerView.Adapter<SavedCoursesListAdapter.SavedCoursesListViewHolder> {

    // Model Variables
    private ArrayList<String> courseName;
    private ArrayList<String> courseCode;
    private ArrayList<String> courseAuthor;
    private ArrayList<String> courseLanguage;
    private ArrayList<String> courseCategory;
    private ArrayList<String> courseSubCategory;
    private ArrayList<String> courseImageUrl;
    private ArrayList<String> savedCourseKey;
    private ArrayList<String> courseType;
    private ArrayList<String> courseDesc;
    private ArrayList<Double> coursePrice;
    private Context mContext;

    // Constructors
    public SavedCoursesListAdapter(Context mContext, ArrayList<String> courseName, ArrayList<String> courseAuthor,
                                       ArrayList<String> courseLanguage, ArrayList<String> courseCode,
                                       ArrayList<String> courseCategory, ArrayList<String> courseSubCategory,
                                       ArrayList<String> courseImageUrl, ArrayList<String> savedCourseKey, ArrayList<String> courseType,
                                       ArrayList<String> courseDesc, ArrayList<Double> coursePrice) {

        this.courseName = courseName;
        this.courseCode = courseCode;
        this.courseAuthor = courseAuthor;
        this.courseLanguage = courseLanguage;
        this.courseCategory = courseCategory;
        this.courseSubCategory = courseSubCategory;
        this.courseImageUrl = courseImageUrl;
        this.savedCourseKey = savedCourseKey;
        this.courseType = courseType;
        this.courseDesc = courseDesc;
        this.coursePrice = coursePrice;
        this.mContext = mContext;
    }

    // This method is used to create the view and inflate it with values
    @NonNull
    @Override
    public SavedCoursesListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_saved_courses, parent, false);

        return new SavedCoursesListViewHolder(view);
    }

    // This method binds those controls inside the view to their respective controls
    @Override
    public void onBindViewHolder(@NonNull final SavedCoursesListViewHolder holder, int position) {

        holder.mCourseName.setText(courseName.get(position));
        holder.mCourseAuthor.setText(courseAuthor.get(position));
        holder.mCourseLanguage.setText(courseLanguage.get(position));
        holder.mCourseCategory.setText(courseCategory.get(position));
        holder.mCourseSubCategory.setText(courseSubCategory.get(position));
        holder.mCoursePrice.setText(String.valueOf(coursePrice.get(position)));
        holder.mCoursePriceHigh.setText(String.valueOf(coursePrice.get(position) + 1500));
        holder.mCourseType.setText(courseType.get(position));
        Glide.with(this.mContext)
                .load(courseImageUrl.get(position))
                .transforms(new CenterCrop())
                .override(400,400)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.mCourseImageUrl);

        // onClickListener for every item in the RecyclerView
        holder.mParent.setOnClickListener(view ->
                launchCourse(courseName.get(holder.getAdapterPosition()), courseAuthor.get(holder.getAdapterPosition()), courseLanguage.get(holder.getAdapterPosition()), courseCategory.get(holder.getAdapterPosition()), courseSubCategory.get(holder.getAdapterPosition()), courseImageUrl.get(holder.getAdapterPosition()), courseCode.get(holder.getAdapterPosition()), courseType.get(holder.getAdapterPosition()), courseDesc.get(holder.getAdapterPosition()), coursePrice.get(holder.getAdapterPosition())));
        holder.mDeleteCourse.setOnClickListener(view -> {
            DatabaseReference savedCoursesRef = FirebaseDatabase.getInstance().getReference().child("savedCourses").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            savedCoursesRef.child(savedCourseKey.get(position)).removeValue().addOnSuccessListener(aVoid -> {
                Toasty.success(mContext, "Removed Successfully", Toasty.LENGTH_LONG).show();

                courseName.remove(position);
                courseAuthor.remove(position);
                courseCategory.remove(position);
                courseSubCategory.remove(position);
                courseCode.remove(position);
                courseImageUrl.remove(position);
                courseLanguage.remove(position);
                savedCourseKey.remove(position);
                courseType.remove(position);
                courseDesc.remove(position);
                coursePrice.remove(position);

                notifyItemRemoved(position);

                notifyItemRangeChanged(position, courseName.size());
                notifyItemRangeChanged(position, courseAuthor.size());
                notifyItemRangeChanged(position, courseCategory.size());
                notifyItemRangeChanged(position, courseSubCategory.size());
                notifyItemRangeChanged(position, courseCode.size());
                notifyItemRangeChanged(position, courseImageUrl.size());
                notifyItemRangeChanged(position, courseLanguage.size());
                notifyItemRangeChanged(position, savedCourseKey.size());
                notifyItemRangeChanged(position, courseType.size());
                notifyItemRangeChanged(position, courseDesc.size());
                notifyItemRangeChanged(position, coursePrice.size());

                holder.itemView.setVisibility(View.GONE);


            }).addOnFailureListener(e -> {
                Toasty.error(mContext, Objects.requireNonNull(e.getMessage()), Toasty.LENGTH_LONG).show();
            });

        });
    }

                // This method is used to launch a new Activity which displays the profile of the Course to the manager
    private void launchCourse(String course_name, String course_author, String course_language, String course_category, String course_subcategory,String course_img, String course_code, String course_type,String course_desc, Double course_price) {
        Intent intent = new Intent(mContext, CourseDetailActivity.class);
        intent.putExtra("courseName", course_name);
        intent.putExtra("courseAuthor", course_author);
        intent.putExtra("courseLanguage", course_language);
        intent.putExtra("courseCategory", course_category);
        intent.putExtra("courseSubCategory", course_subcategory);
        intent.putExtra("courseImageUrl", course_img);
        intent.putExtra("courseCode", course_code);
        intent.putExtra("courseType", course_type);
        intent.putExtra("courseDesc", course_desc);
        intent.putExtra("coursePrice", course_price);
        mContext.startActivity(intent);
    }

    // This method returns the number of items in the RecyclerView
    @Override
    public int getItemCount() {
        return courseName.size();
    }

    public class SavedCoursesListViewHolder extends RecyclerView.ViewHolder {

        // Widgets
        private TextView mCourseName, mCourseAuthor, mCourseLanguage, mCourseCategory, mCourseSubCategory, mCourseType, mCoursePrice, mRupeeHighSymbol, mCoursePriceHigh;
        private ImageView mDeleteCourse;
        private ImageView mCourseImageUrl;
        private RelativeLayout mParent;

        public SavedCoursesListViewHolder(View itemView) {
            super(itemView);

            mCourseName = itemView.findViewById(R.id.txt_course_name_saved_courses_list);
            mCourseAuthor = itemView.findViewById(R.id.txt_course_author_saved_courses_list);
            mCourseLanguage = itemView.findViewById(R.id.txt_course_lang_saved_courses_list);
            mCourseCategory = itemView.findViewById(R.id.txt_course_category_saved_courses_list);
            mCourseSubCategory = itemView.findViewById(R.id.txt_course_subcategory_saved_courses_list);
            mCourseImageUrl = itemView.findViewById(R.id.img_course_saved_courses_list);
            mCourseType = itemView.findViewById(R.id.txt_course_type_saved_course_list);
            mCoursePrice = itemView.findViewById(R.id.txt_course_price_saved_courses_list);
            mRupeeHighSymbol = itemView.findViewById(R.id.rupee_high_saved_logo);
            mRupeeHighSymbol.setPaintFlags(mRupeeHighSymbol.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            mCoursePriceHigh = itemView.findViewById(R.id.tvCourseHighSavedPrice);
            mCoursePriceHigh.setPaintFlags(mCoursePriceHigh.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            mDeleteCourse = itemView.findViewById(R.id.btn_delete_saved_course);
            mParent = itemView.findViewById(R.id.cvSavedCoursesList);
        }

    }

}
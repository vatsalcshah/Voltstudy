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

import java.util.ArrayList;


@SuppressWarnings("FieldCanBeLocal")
public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    // Model Variables
    private ArrayList<String> courseName;
    private ArrayList<String> courseCode;
    private ArrayList<String> courseDesc;
    private ArrayList<String> courseAuthor;
    private ArrayList<String> courseLanguage;
    private ArrayList<String> courseCategory;
    private ArrayList<String> courseSubCategory;
    private ArrayList<String> courseImageUrl;
    private ArrayList<String> courseType;
    private ArrayList<Double> coursePrice;
    private ArrayList<String> courseAuthorID;

    private Context mContext;

    // Constructors
    public CourseAdapter(Context mContext, ArrayList<String> courseName, ArrayList<String> courseAuthor,
                         ArrayList<String> courseLanguage, ArrayList<String> courseCode, ArrayList<String> courseDesc,
                         ArrayList<String> courseCategory, ArrayList<String> courseSubCategory,
                         ArrayList<String> courseImageUrl, ArrayList<String> courseType, ArrayList<Double> coursePrice, ArrayList<String> courseAuthorID) {

        this.courseName = courseName;
        this.courseCode = courseCode;
        this.courseDesc = courseDesc;
        this.courseAuthor = courseAuthor;
        this.courseLanguage = courseLanguage;
        this.courseCategory = courseCategory;
        this.courseSubCategory = courseSubCategory;
        this.courseImageUrl = courseImageUrl;
        this.courseType = courseType;
        this.coursePrice = coursePrice;
        this.courseAuthorID = courseAuthorID;
        this.mContext = mContext;
    }

    // This method is used to create the view and inflate it with values
    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_courses, parent, false);

        return new CourseViewHolder(view);
    }

    // This method binds those controls inside the view to their respective controls
    @Override
    public void onBindViewHolder(@NonNull final CourseViewHolder holder, int position) {

        holder.mCourseName.setText(courseName.get(position));
        holder.mCourseAuthor.setText(courseAuthor.get(position));
        holder.mCourseLanguage.setText(courseLanguage.get(position));
        holder.mCourseCategory.setText(courseCategory.get(position));
        holder.mCourseSubCategory.setText(courseSubCategory.get(position));
        holder.mCourseType.setText(courseType.get(position));
        holder.mCoursePrice.setText(String.valueOf(coursePrice.get(position)));
        holder.mCoursePriceHigh.setText(String.valueOf(coursePrice.get(position) + 1500));

        Glide.with(this.mContext)
                .load(courseImageUrl.get(position))
                .transforms(new CenterCrop())
                .override(620,500)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.mCourseImageUrl);

        // onClickListener for every item in the RecyclerView
        holder.mParent.setOnClickListener(view ->
                launchCourse(courseName.get(holder.getAdapterPosition()), courseAuthor.get(holder.getAdapterPosition()), courseLanguage.get(holder.getAdapterPosition()), courseCategory.get(holder.getAdapterPosition()), courseSubCategory.get(holder.getAdapterPosition()), courseImageUrl.get(holder.getAdapterPosition()), courseCode.get(holder.getAdapterPosition()), courseDesc.get(holder.getAdapterPosition()), courseType.get(holder.getAdapterPosition()), coursePrice.get(holder.getAdapterPosition()), courseAuthorID.get(holder.getAdapterPosition())));
    }

    // This method is used to launch a new Activity which displays the profile of the Course to the manager
    private void launchCourse(String course_name, String course_author, String course_language, String course_category, String course_subcategory,String course_img, String course_code, String course_desc, String course_type, Double course_price, String course_authorID) {
        Intent intent = new Intent(mContext, CourseDetailActivity.class);
        intent.putExtra("courseName", course_name);
        intent.putExtra("courseAuthor", course_author);
        intent.putExtra("courseLanguage", course_language);
        intent.putExtra("courseCategory", course_category);
        intent.putExtra("courseSubCategory", course_subcategory);
        intent.putExtra("courseImageUrl", course_img);
        intent.putExtra("courseCode", course_code);
        intent.putExtra("courseDesc", course_desc);
        intent.putExtra("courseType", course_type);
        intent.putExtra("coursePrice", course_price);
        intent.putExtra("courseAuthorID", course_authorID);
        mContext.startActivity(intent);
    }

    // This method returns the number of items in the RecyclerView
    @Override
    public int getItemCount() {
        return courseName.size();
    }

    public ArrayList<Double> getCoursePrice() {
        return coursePrice;
    }

    public void setCoursePrice(ArrayList<Double> coursePrice) {
        this.coursePrice = coursePrice;
    }

    public class CourseViewHolder extends RecyclerView.ViewHolder {

        // Widgets
        private TextView mCourseName, mCourseAuthor, mCourseLanguage, mCourseCategory, mCourseSubCategory, mCourseType, mCoursePrice, mRupeeHighSymbol, mCoursePriceHigh;
        private ImageView mCourseImageUrl;
        private RelativeLayout mParent;

        public CourseViewHolder(View itemView) {
            super(itemView);

            mCourseName = itemView.findViewById(R.id.tvCourseName);
            mCourseAuthor = itemView.findViewById(R.id.tvCourseAuthor);
            mCourseLanguage = itemView.findViewById(R.id.tvCourseLanguage);
            mCourseCategory = itemView.findViewById(R.id.tvCourseCategory);
            mCourseSubCategory = itemView.findViewById(R.id.tvCourseSubCategory);
            mCourseImageUrl = itemView.findViewById(R.id.tvCourseImage);
            mCourseType = itemView.findViewById(R.id.txt_course_type_course_list);
            mCoursePrice = itemView.findViewById(R.id.tvCoursePrice);
            mRupeeHighSymbol = itemView.findViewById(R.id.rupee_high_logo);
            mRupeeHighSymbol.setPaintFlags(mRupeeHighSymbol.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            mCoursePriceHigh = itemView.findViewById(R.id.tvCourseHighPrice);
            mCoursePriceHigh.setPaintFlags(mCoursePriceHigh.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            mParent = itemView.findViewById(R.id.cvCourses);
        }

    }

}
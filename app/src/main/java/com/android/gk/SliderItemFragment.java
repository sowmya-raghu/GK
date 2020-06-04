package com.android.gk;

import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

public class SliderItemFragment extends Fragment {

  private static final String ARG_POSITION = "slider-position";
  public static Drawable drawable;
  // prepare all subtitle images arrays
  @StringRes
  private static final int[] PAGE_IMAGE =
      new int[] {
           R.drawable.quote1final, R.drawable.quote2final, R.drawable.quote3final
      };

  // prepare all background images arrays
  @StringRes
  private static final int[] BG_IMAGE = new int[] {
          R.drawable.ic_bg_peach, R.drawable.ic_bg_peach,
      R.drawable.ic_bg_peach
  };

  private int position;

  int mcount;

  public SliderItemFragment() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of
   *
   * @return A new instance of fragment SliderItemFragment.
   */

  public static SliderItemFragment newInstance(int position) {
    SliderItemFragment fragment = new SliderItemFragment();
    Bundle args = new Bundle();
    args.putInt(ARG_POSITION, position);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      position = getArguments().getInt(ARG_POSITION);
    }


  }



  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_slider_item, container, false);
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    // set page background
    view.setBackground(requireActivity().getDrawable(BG_IMAGE[position]));

    ImageView imageView = view.findViewById(R.id.imageView);
    //Picasso.with(getBaseContext()).load(post.getPostImage()).transform(new RoundedTransformation(20, 0)).into(publishedPostViewHolder.txtPostImage);


    // set page image
    Picasso.with(getContext()).load(PAGE_IMAGE[position]).transform(new RoundedTransformation(40,0)).into(imageView);


   // imageView.setBackgroundResource(R.drawable.roundedcorner);
  }
}

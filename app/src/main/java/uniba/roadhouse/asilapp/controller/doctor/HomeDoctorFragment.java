package uniba.roadhouse.asilapp.controller.doctor;

import static com.google.android.material.internal.ViewUtils.dpToPx;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.other.Utility;
import uniba.roadhouse.asilapp.controller.user.home.SettingsFragment;


public class HomeDoctorFragment extends Fragment {
    Toolbar toolbarDoctorActivity;
    TextView textToolbarDoctor;
    ProgressBar progressBar;
    LinearLayout layoutCardUserDoctor;





    public HomeDoctorFragment() {
        // Required empty public constructor
    }


    public static HomeDoctorFragment newInstance(String param1, String param2) {
        HomeDoctorFragment fragment = new HomeDoctorFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_doctor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //--------RIFERIMENTI------------
        toolbarDoctorActivity = getActivity().findViewById(R.id.toolbarDoctorActivity);
        textToolbarDoctor = getActivity().findViewById(R.id.textToolbarDoctor);
        textToolbarDoctor.setText(getString(R.string.homeMenuScreen));
        progressBar = getActivity().findViewById(R.id.progressBarDoctorActivty);
        layoutCardUserDoctor = view.findViewById(R.id.layoutCardUserDoctor);
        getData();

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        toolbarDoctorActivity.getMenu().clear();
        toolbarDoctorActivity.setNavigationIcon(null);
        toolbarDoctorActivity.inflateMenu(R.menu.menu_home_activity);
        toolbarDoctorActivity.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.settings)
                {
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.addToBackStack(getString(R.string.settingsMenuScreen));
                    fragmentTransaction.replace(R.id.doctorFragmentView, SettingsDoctorFragment.class, null);
                    fragmentTransaction.commit();
                }
                return true;
            }
        });
        super.onResume();
    }


    @SuppressLint("RestrictedApi")
    private void getData()
    {
        ConstraintLayout constraintLayout = new ConstraintLayout(requireContext());
        constraintLayout.setId(View.generateViewId());
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                getResources().getDimensionPixelSize(R.dimen.heightHealthHistory)
        );
        layoutParams.topMargin=0;
        constraintLayout.setLayoutParams(layoutParams);
        constraintLayout.setBackgroundColor(getResources().getColor(R.color.colorCardUserDoctor));
        layoutCardUserDoctor.addView(constraintLayout);

        Typeface typeface = ResourcesCompat.getFont(requireContext(), R.font.titillium_web_bold);

        TextView textViewTitle = new TextView(getActivity());
        textViewTitle.setText("Alessandro Bevilacqua");
        textViewTitle.setId(View.generateViewId());
        ConstraintLayout.LayoutParams paramsTitle = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );
        textViewTitle.setTypeface(typeface);
        textViewTitle.setTypeface(typeface);
        textViewTitle.setTextColor(getResources().getColor(R.color.white));
        textViewTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.textTitleHealthHistory));
        textViewTitle.setLayoutParams(paramsTitle);
        constraintLayout.addView(textViewTitle);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        constraintSet.connect(textViewTitle.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, getResources().getDimensionPixelSize(R.dimen.marginLeftRightDetailHealthHistory));
        constraintSet.connect(textViewTitle.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, (int) dpToPx(getContext(), 20));
        constraintSet.applyTo(constraintLayout);



        TextView textBirthDateUserLabel = new TextView(getActivity());
        textBirthDateUserLabel.setId(View.generateViewId());
        ConstraintLayout.LayoutParams paramsBirthDateLabel = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );
        textBirthDateUserLabel.setTypeface(typeface);
        textBirthDateUserLabel.setText(getString(R.string.birthDateDoctorHome));
        textBirthDateUserLabel.setTextColor(getResources().getColor(R.color.white));
        textBirthDateUserLabel.setLayoutParams(paramsBirthDateLabel);
        constraintLayout.addView(textBirthDateUserLabel);
        ConstraintSet constraintSetBirthDateLabel = new ConstraintSet();
        constraintSetBirthDateLabel.clone(constraintLayout);
        constraintSetBirthDateLabel.connect(textBirthDateUserLabel.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, (int) dpToPx(getContext(), 10));
        constraintSetBirthDateLabel.connect(textBirthDateUserLabel.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, (int) dpToPx(getContext(), 20));
        constraintSetBirthDateLabel.applyTo(constraintLayout);


        TextView textBirthDateUserValue = new TextView(getActivity());
        textBirthDateUserValue.setId(View.generateViewId());
        ConstraintLayout.LayoutParams paramsBirthDateValue = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
        );
        textBirthDateUserValue.setTypeface(typeface);
        textBirthDateUserValue.setText(getString(R.string.birthDateSignupText));
        textBirthDateUserValue.setTextColor(getResources().getColor(R.color.white));
        textBirthDateUserValue.setLayoutParams(paramsBirthDateValue);
        constraintLayout.addView(textBirthDateUserValue);
        ConstraintSet constraintSetBirthDateValue = new ConstraintSet();
        constraintSetBirthDateValue.clone(constraintLayout);
        constraintSetBirthDateValue.connect(textBirthDateUserValue.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, (int) dpToPx(getContext(), 10));
        constraintSetBirthDateValue.connect(textBirthDateUserValue.getId(), ConstraintSet.LEFT, textBirthDateUserLabel.getId(), ConstraintSet.RIGHT, (int) dpToPx(getContext(), 3));
        constraintSetBirthDateValue.applyTo(constraintLayout);

    }
}
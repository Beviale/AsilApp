package uniba.roadhouse.asilapp.controller.user.signinSignup;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import uniba.roadhouse.asilapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignupCompleteScreenFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignupCompleteScreenFragment extends Fragment {


    public SignupCompleteScreenFragment() {
        // Required empty public constructor
    }


    public static SignupCompleteScreenFragment newInstance(String param1, String param2) {
        SignupCompleteScreenFragment fragment = new SignupCompleteScreenFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.signup_complete_screen_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button returnLogin = (Button) getActivity().findViewById(R.id.returnLoginButton);
        returnLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.empty);
                fragmentTransaction.disallowAddToBackStack();
                fragmentTransaction.replace(R.id.signinFragmentView, SigninFragment.class, null);
                fragmentTransaction.commit();
            }
        });
    }
}
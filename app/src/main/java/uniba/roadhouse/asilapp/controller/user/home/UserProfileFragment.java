package uniba.roadhouse.asilapp.controller.user.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.model.dao.Access;
import uniba.roadhouse.asilapp.model.dao.Dao;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserProfileFragment extends Fragment {

    TextView profileText;
    TextView profileNameTitle;
    TextView profileName;
    TextView profileSurnameTitle;
    TextView profileSurname;
    TextView profileGenderTitle;
    TextView profileGender;
    TextView profileBirthDateTitle;
    TextView profileBirthDate;
    TextView profileCitizenTitle;
    TextView profileCitizen;
    TextView profileCountryTitle;
    TextView profileCountry;

    Map<String, Object> DatiCorrenti;


    public UserProfileFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserProfileFragment.
     */
    public static UserProfileFragment newInstance(String param1, String param2) {
        UserProfileFragment fragment = new UserProfileFragment();
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
        return inflater.inflate(R.layout.fragment_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        profileText = view.findViewById(R.id.profileText);
        profileNameTitle = view.findViewById(R.id.profileNameTitle);
        profileName = view.findViewById(R.id.profileName);
        profileSurnameTitle = view.findViewById(R.id.profileSurnameTitle);
        profileSurname = view.findViewById(R.id.profileSurname);
        profileGenderTitle = view.findViewById(R.id.profileGenderTitle);
        profileGender = view.findViewById(R.id.profileGender);
        profileBirthDateTitle = view.findViewById(R.id.profileBirthDateTitle);
        profileBirthDate = view.findViewById(R.id.profileBirthDate);
        profileCitizenTitle = view.findViewById(R.id.profileCitizenTitle);
        profileCitizen = view.findViewById(R.id.profileCitizen);
        profileCountryTitle = view.findViewById(R.id.profileCountryTitle);
        profileCountry = view.findViewById(R.id.profileCountry);

        
    }


    @Override
    public void onStart() {
        try {
            DatiCorrenti = Dao.getUserData(Access.getUsername(), getActivity()).get();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        profileName.setText(DatiCorrenti.get("nome").toString());
        profileSurname.setText(DatiCorrenti.get("cognome").toString());
        profileGender.setText(DatiCorrenti.get("sesso").toString());
        //profileBirthDate.setText(DatiCorrenti.get(""));
        profileCitizen.setText(DatiCorrenti.get("cittadinanza").toString());
        profileCountry.setText(DatiCorrenti.get("paeseDiProvenienza").toString());
        super.onStart();
    }


    @Override
    public void onResume() {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolBarHome);
        toolbar.getMenu().clear();
        toolbar.setNavigationIcon(null);
        super.onResume();
    }
}
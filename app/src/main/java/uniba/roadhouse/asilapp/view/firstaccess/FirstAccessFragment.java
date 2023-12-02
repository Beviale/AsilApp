package uniba.roadhouse.asilapp.view.firstaccess;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.Utility;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FirstAccessFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FirstAccessFragment extends Fragment {

    public FirstAccessFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PrimoAccesso.
     */
    // TODO: Rename and change types and number of parameters
    public static FirstAccessFragment newInstance() {
        FirstAccessFragment fragment = new FirstAccessFragment();
        return fragment;
    }


    @Override
    public void onStart() {
        super.onStart();
        //importo il listener per la registrazione
        getView().findViewById(R.id.registerLabel).setOnClickListener(v->callRegisterFragment());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //funzione che sottilinea il testo di registrazione
        registerUnderlineText();
        Button buttonLogin = view.findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if (!Utility.isConnectedToInternet(getActivity())) {
                    FirstAccessActivity.dialogConnection = true;
                    Utility.showAlertDialog(getActivity(), getString(R.string.noConnectionTitleLogin), getString(R.string.noConnectionLogin));
                }
            }
        });

        TextView registerLabel = view.findViewById(R.id.registerLabel);
        registerLabel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(!Utility.isConnectedToInternet(getActivity()))
                {
                    FirstAccessActivity.dialogConnection=true;
                    Utility.showAlertDialog(getActivity(), getString(R.string.noConnectionTitleLogin), getString(R.string.noConnectionLogin));
                }
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.first_access_fragment, container, false);
    }

    private void registerUnderlineText(){
        TextView registerLabel=(TextView) getView().findViewById(R.id.registerLabel);
        SpannableString str=new SpannableString(getString(R.string.loginRegistrationLabel));
        str.setSpan(new UnderlineSpan(), 0, str.length(), 0);
        registerLabel.setText(str);
    }

    private void callRegisterFragment(){
        if(!Utility.isConnectedToInternet(getActivity())) {
            FirstAccessActivity.dialogConnection = true;
            Utility.showAlertDialog(getActivity(), getString(R.string.noConnectionTitle), getString(R.string.noConnection));
        }
        else
        {
            //prendo l'activity parent e richiamo il metodo per sostituire il fragment di login con quello di registrazione
            ((FirstAccessActivity) getActivity()).callRegisterFragment();
        }

    }
}
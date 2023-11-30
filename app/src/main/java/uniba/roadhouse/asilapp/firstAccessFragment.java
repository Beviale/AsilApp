package uniba.roadhouse.asilapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link firstAccessFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class firstAccessFragment extends Fragment {

    public firstAccessFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PrimoAccesso.
     */
    // TODO: Rename and change types and number of parameters
    public static firstAccessFragment newInstance() {
        firstAccessFragment fragment = new firstAccessFragment();
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();

        //importo il listener per la registrazione
        getView().findViewById(R.id.registerLabel).setOnClickListener(v->callRegisterFragment());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //funzione che sottilinea il testo di registrazione
        registerUnderlineText();
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
        //prendo l'activity parent e richiamo il metodo per sostituire il fragment di login con quello di registrazione
        ((SigninSignupActivity) getActivity()).callRegisterFragment();
    }
}
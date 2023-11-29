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
 * Use the {@link PrimoAccesso#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PrimoAccesso extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PrimoAccesso() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PrimoAccesso.
     */
    // TODO: Rename and change types and number of parameters
    public static PrimoAccesso newInstance(String param1, String param2) {
        PrimoAccesso fragment = new PrimoAccesso();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
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
        return inflater.inflate(R.layout.fragment_primo_accesso, container, false);
    }

    private void registerUnderlineText(){
        TextView registerLabel=(TextView) getView().findViewById(R.id.registerLabel);
        SpannableString str=new SpannableString(getString(R.string.loginRegistrationLabel));
        str.setSpan(new UnderlineSpan(), 0, str.length(), 0);
        registerLabel.setText(str);
    }

    private void callRegisterFragment(){
        //prendo l'activity parent e richiamo il metodo per sostituire il fragment di login con quello di registrazione
        ((MainActivity) getActivity()).callRegisterFragment();
    }
}
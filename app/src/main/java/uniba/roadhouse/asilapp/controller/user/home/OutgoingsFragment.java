package uniba.roadhouse.asilapp.controller.user.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.other.CategoriaSpesaEnum;
import uniba.roadhouse.asilapp.controller.other.Utility;
import uniba.roadhouse.asilapp.model.dao.UserLogin;
import uniba.roadhouse.asilapp.model.dao.Dao;
import uniba.roadhouse.asilapp.model.dao.Spesa;


/**
 * Schermata relativa all'aggiunta e alla visualizzazione delle spese.
 */
public class OutgoingsFragment extends Fragment {
    /**
     * Button che consente l'aggiunta di una nuova spesa.
     */
    Button addOutgoingButton;
    /**
     * Layout per l'inserimento della categoria di spesa da aggiungere.
     */
    TextInputLayout categorySelectionOutgoingsLayout;
    /**
     * AutoCompleteTextView per l'inserimento della categoria di spesa da aggiungere.
     */
    AutoCompleteTextView categorySelectionOutgoings;
    /**
     * Layout per l'inserimento del valore della spesa da aggiungere.
     */
    TextInputLayout valueOutgoingsLayout;
    /**
     * Campo di testo per l'inserimento del valore della spesa da aggiungere.
     */
    TextInputEditText valueOutgoings;
    /**
     * Layout dell'intero fragment.
     */
    ConstraintLayout layoutOutgoinsFragment;
    /**
     * ProgressBar da mostrare durante il caricamento dei dati dal database.
     */
    ProgressBar progressBar;
    /**
     * Grafico a torta che mostra la suddivisione in categorie delle spese effettuate negli utlimi 7 giorni.
     */
    PieChart pieChartLast7Days;
    /**
     * Mostra l'importo speso in cibo negli utlimi 7 giorni.
     */
    TextView valueFood7DaysMoney;
    /**
     * Mostra l'importo speso in farmaci negli ultimi 7 giorni.
     */
    TextView valueDrugs7DaysMoney;
    /**
     * Mostra l'importo speso in altro negli ultimi 7 giorni.
     */
    TextView valueOther7DaysMoney;
    /**
     * Grafico a torta che mostra la suddivisione in categorie delle spese effettuate negli utlimi 30 giorni.
     */
    PieChart pieChartLast30Days;
    /**
     * Mostra l'importo speso in cibo negli ultimi 30 giorni.
     */
    TextView valueFood30DaysMoney;
    /**
     * Mostra l'importo speso in farmaci negli ultimi 30 giorni.
     */
    TextView valueDrugs30DaysMoney;
    /**
     * Mostra l'importo speso in altro neglu ultimi 30 giorni.
     */
    TextView valueOther30DaysMoney;

    /**
     * Valore massimo inseribile come nuova spesa.
     */
    private static float MAX_VALUE=5000;
    /**
     * Valore minimo inseribile come nuova spesa
     */
    private static float MIN_VALUE=(float)0.5;






    public OutgoingsFragment() {

    }


    public static OutgoingsFragment newInstance(String param1, String param2) {
        OutgoingsFragment fragment = new OutgoingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_outgoings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //------------RIFERIMENTI-----------------
        categorySelectionOutgoingsLayout = view.findViewById(R.id.categorySelectionOutgoingsLayout);
        categorySelectionOutgoings = view.findViewById(R.id.categorySelectionOutgoings);
        valueOutgoingsLayout = view.findViewById(R.id.valueOutgoingsLayout);
        layoutOutgoinsFragment = view.findViewById(R.id.layoutOutgoinsFragment);
        progressBar = getActivity().findViewById(R.id.homeActivityProgressBar);
        valueOutgoings = view.findViewById(R.id.valueOutgoings);
        addOutgoingButton = view.findViewById(R.id.addOutgoingButton);
        pieChartLast7Days = view.findViewById(R.id.pieChartLast7Days);
        valueFood7DaysMoney = view.findViewById(R.id.valueFood7DaysMoney);
        valueDrugs7DaysMoney = view.findViewById(R.id.valueDrugs7DaysMoney);
        valueOther7DaysMoney = view.findViewById(R.id.valueOther7DaysMoney);
        pieChartLast30Days = view.findViewById(R.id.pieChartLast30Days);
        valueFood30DaysMoney = view.findViewById(R.id.valueFood30DaysMoney);
        valueDrugs30DaysMoney = view.findViewById(R.id.valueDrugs30DaysMoney);
        valueOther30DaysMoney = view.findViewById(R.id.valueOther30DaysMoney);
        // Aggiungo i TextWatcher
        categorySelectionOutgoings.addTextChangedListener(textWatcherCategory);
        valueOutgoings.addTextChangedListener(textWacherValue);
        // Disattivo il bottone di aggiunta spesa
        addOutgoingButton.setEnabled(false);
        addOutgoingButton.setAlpha((float)0.5);

        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onStart() {
        //----------LISTENER--------------------
        addOutgoingButton.setOnClickListener(v->addOutgoing());
        super.onStart();
        getLast7Days();
        getLast30Days();
    }

    @Override
    public void onResume() {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolBarHome);
        toolbar.getMenu().clear();
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.arrow_back_png));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();

            }
        });
        categorySelectionOutgoingsLayout.requestFocus();
        getCategory();
        super.onResume();
    }

    @Override
    public void onPause() {
        progressBar.setVisibility(View.GONE);
        super.onPause();
    }

    /**
     * Inserisce in categorySelectionOutgoings l'elenco delle categorie.
     */
    private void getCategory()
    {
        List<String> allCategory = new ArrayList<String>();
        allCategory.add(getString(R.string.food));
        allCategory.add(getString(R.string.drugs));
        allCategory.add(getString(R.string.other));
        ArrayAdapter<String> adapterCategory = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, allCategory);
        categorySelectionOutgoings.setAdapter(adapterCategory);
    }



    TextWatcher textWatcherCategory = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            // Dopo aver selezionato la cateogoria, sposoto il focus sull'importo.
            valueOutgoingsLayout.requestFocus();
            // Se il campo relativo all'importo non è vuoto, il bottore di aggiunta spesa viene attivato.
            if(!valueOutgoings.getText().toString().isEmpty())
            {
                addOutgoingButton.setEnabled(true);
                addOutgoingButton.setAlpha((float)1.0);
            }
        }
    };


    TextWatcher textWacherValue = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(!s.toString().isEmpty())
            {
                // Dopo aver inserito il valore, se il campo relativo alla categoria non è vuoto, il bottone di aggiunta spesa viene attivato.
                if(!categorySelectionOutgoings.getText().toString().isEmpty())
                {
                    addOutgoingButton.setEnabled(true);
                    addOutgoingButton.setAlpha((float)1.0);
                }
            }
            // Altrimenti viene disattivato
            else
            {
                addOutgoingButton.setEnabled(false);
                addOutgoingButton.setAlpha((float)0.5);
            }
        }
    };


    /**
     * Permette l'upload al database della nuova spesa.
     */
    private void addOutgoing()
    {
        String category = categorySelectionOutgoings.getText().toString();
        Float value = Float.valueOf(valueOutgoings.getText().toString());
        // Se l'importo inserito è maggiore del massimo consentito, non effettuo l'upload e informo l'utente con un dialog.
        if(value>MAX_VALUE)
        {
            Utility.showAlertDialog(getActivity(), getString(R.string.valueOutgoindTooHightTitle), getString(R.string.valueOutgoindTooHight).concat(" ").concat(String.valueOf(MAX_VALUE)).concat("."));
            return;
        }
        // Se l'importo inserito è minore del minimo consentito, non effettuo l'upload e informo l'utente con un dialog.
        if(value<MIN_VALUE)
        {
            Utility.showAlertDialog(getActivity(), getString(R.string.valueOutgoindTooLowTitle), getString(R.string.valueOutgoindTooLow).concat(" ").concat(String.valueOf(MIN_VALUE)).concat("."));
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        layoutOutgoinsFragment.setAlpha((float)0.5);
        String categoryToEnum =  categorySelectionOutgoings.getText().toString();
        Spesa addSpesa = new Spesa(convertCategoryStringToEnum(categoryToEnum), Double.valueOf(valueOutgoings.getText().toString()), Timestamp.now(), UserLogin.getUsername());
        CompletableFuture<String> future = Dao.storeSpesa(addSpesa, getActivity());
        future.thenAccept(result -> {
            getActivity().runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                layoutOutgoinsFragment.setAlpha((float)1.0);
                Toast.makeText(getActivity(), result.toString(), Toast.LENGTH_LONG).show();
                // Aggiorno la sezione dei 7 giorni.
                getLast7Days();
                // Aggiorno la sezione dei 30 giorni.
                getLast30Days();
            });
        });
    }

    /**
     * Data una stringa relativa alla cateogoria di una spesa, la converte nel tipo enumerativo.
     * @param category
     * @return
     */
    private CategoriaSpesaEnum convertCategoryStringToEnum(String category)
    {
        String cibo = getString(R.string.food);
        String farmaci = getString(R.string.drugs);
        if (category.equals(cibo))
        {
            return CategoriaSpesaEnum.CIBO;
        }
        else if(category.equals(farmaci))
        {
            return CategoriaSpesaEnum.FARMACI;
        }
        else
        {
            return CategoriaSpesaEnum.ALTRO;
        }
    }


    /**
     * Prende dal database tutte le spese effettuate negli ultimi 7 giorni suddivise in base alla loro cateogoria.
     * Riempie le varie TextView e crea il grafico a torta.
     */
    private void getLast7Days()
    {
        progressBar.setVisibility(View.VISIBLE);
        layoutOutgoinsFragment.setAlpha((float)0.5);
        CompletableFuture<Map<String,?>> future = Dao.getAllSpese(UserLogin.getUsername(), 7, getActivity());
        future.thenAccept(result -> {
            getActivity().runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                layoutOutgoinsFragment.setAlpha((float)1.0);
                if(result.get("esito").toString().equals(getString(R.string.getSpeseSuccessfull)))
                {
                    List<Spesa> speseCibo = (List<Spesa>) result.get(CategoriaSpesaEnum.CIBO.toString());
                    List<Spesa> speseFarmaci = (List<Spesa>) result.get(CategoriaSpesaEnum.FARMACI.toString());
                    List<Spesa> speseAltro = (List<Spesa>) result.get(CategoriaSpesaEnum.ALTRO.toString());
                    Double totalFood=0.0;
                    Double totalDrugs=0.0;
                    Double totalOther=0.0;
                    Double total=0.0;
                    for(Spesa spesa: speseCibo)
                    {
                        totalFood = totalFood + spesa.getCosto();
                    }
                    for(Spesa spesa: speseFarmaci)
                    {
                        totalDrugs = totalFood + spesa.getCosto();
                    }
                    for(Spesa spesa: speseAltro)
                    {
                        totalOther = totalFood + spesa.getCosto();
                    }
                    valueFood7DaysMoney.setText(String.valueOf(totalFood.floatValue()));
                    valueDrugs7DaysMoney.setText(String.valueOf(totalDrugs.floatValue()));
                    valueOther7DaysMoney.setText(String.valueOf(totalOther.floatValue()));
                    total = totalFood + totalDrugs + totalOther;
                    Double foodPercent = (totalFood/total) * 100;
                    Double drugsPercent = (totalDrugs/total) * 100;
                    Double otherPercent = (totalOther/total) * 100;
                    Utility.setPieChartOutgoings(pieChartLast7Days, foodPercent.floatValue(), drugsPercent.floatValue(), otherPercent.floatValue(), getActivity());
                }
                else
                {
                    Toast.makeText(getActivity(), result.get("esito").toString(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }



    /**
     * Prende dal database tutte le spese effettuate negli ultimi 30 giorni suddivise in base alla loro cateogoria.
     * Riempie le varie TextView e crea il grafico a torta.
     */
    private void getLast30Days()
    {
        progressBar.setVisibility(View.VISIBLE);
        layoutOutgoinsFragment.setAlpha((float)0.5);
        CompletableFuture<Map<String,?>> future = Dao.getAllSpese(UserLogin.getUsername(), 30, getActivity());
        future.thenAccept(result -> {
            getActivity().runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                layoutOutgoinsFragment.setAlpha((float)1.0);
                if(result.get("esito").toString().equals(getString(R.string.getSpeseSuccessfull)))
                {
                    List<Spesa> speseCibo = (List<Spesa>) result.get(CategoriaSpesaEnum.CIBO.toString());
                    List<Spesa> speseFarmaci = (List<Spesa>) result.get(CategoriaSpesaEnum.FARMACI.toString());
                    List<Spesa> speseAltro = (List<Spesa>) result.get(CategoriaSpesaEnum.ALTRO.toString());
                    Double totalFood=0.0;
                    Double totalDrugs=0.0;
                    Double totalOther=0.0;
                    Double total=0.0;
                    for(Spesa spesa: speseCibo)
                    {
                        totalFood = totalFood + spesa.getCosto();
                    }
                    for(Spesa spesa: speseFarmaci)
                    {
                        totalDrugs = totalFood + spesa.getCosto();
                    }
                    for(Spesa spesa: speseAltro)
                    {
                        totalOther = totalFood + spesa.getCosto();
                    }
                    valueFood30DaysMoney.setText(String.valueOf(totalFood.floatValue()));
                    valueDrugs30DaysMoney.setText(String.valueOf(totalDrugs.floatValue()));
                    valueOther30DaysMoney.setText(String.valueOf(totalOther.floatValue()));
                    total = totalFood + totalDrugs + totalOther;
                    Double foodPercent = (totalFood/total) * 100;
                    Double drugsPercent = (totalDrugs/total) * 100;
                    Double otherPercent = (totalOther/total) * 100;
                    Utility.setPieChartOutgoings(pieChartLast30Days, foodPercent.floatValue(), drugsPercent.floatValue(), otherPercent.floatValue(), getActivity());
                }
                else
                {
                    Toast.makeText(getActivity(), result.get("esito").toString(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
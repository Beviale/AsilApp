package uniba.roadhouse.asilapp.controller.user.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import uniba.roadhouse.asilapp.R;


public class DetailTipsFragment extends Fragment {

    TextView titleDetailTips;
    TextView textDetailTips;
    String type;





    public DetailTipsFragment() {

    }


    public static DetailTipsFragment newInstance(String param1, String param2) {
        DetailTipsFragment fragment = new DetailTipsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        if (getArguments() != null) {
            type = getArguments().getString("type");
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail_tips, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //--------RIFERIMENTI---------------
        titleDetailTips = view.findViewById(R.id.titleDetailTips);
        textDetailTips = view.findViewById(R.id.textDetailTips);
        super.onViewCreated(view, savedInstanceState);
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
        toolbar.inflateMenu(R.menu.share_and_evaluate_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.shareDetailTips)
                {
                    String share = titleDetailTips.getText().toString().concat("\n\n").concat(textDetailTips.getText().toString());
                    Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(android.content.Intent.EXTRA_TEXT, share);
                    startActivity(intent);
                }
                if(item.getItemId() == R.id.evaluateDetailTips)
                {
                    EvaluateTipsDialogFragment dialogFragment = EvaluateTipsDialogFragment.newInstance(type);
                    dialogFragment.show(getActivity().getSupportFragmentManager(), "EvaluateFragment");

                }
                return true;
            }
        });
        super.onResume();
    }
}
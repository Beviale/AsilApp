package uniba.roadhouse.asilapp.controller.patient.home;

import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;

import uniba.roadhouse.asilapp.R;
import uniba.roadhouse.asilapp.controller.other.Utility;


public class HealthBoxFragment extends Fragment {
    private Handler handlerAnimation = new Handler();
    private Boolean statusAnimation = false;
    ImageView imageAnimationButton;
    Button buttonOpenBox;
    Chronometer chronometer;


    public HealthBoxFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static HealthBoxFragment newInstance(String param1, String param2) {
        HealthBoxFragment fragment = new HealthBoxFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_health_box, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buttonOpenBox = view.findViewById(R.id.buttonOpenBox);
        imageAnimationButton = view.findViewById(R.id.imgAnimationButton);
        chronometer = view.findViewById(R.id.chronometerConnection);
        chronometer.start();
    }

    @Override
    public void onResume() {
        super.onResume();

        verifyBluetoothConnection();
        Toolbar toolbar = getActivity().findViewById(R.id.toolBarHome);
        toolbar.getMenu().clear();
        toolbar.setNavigationIcon(null);

    }

    @Override
    public void onStart() {
        super.onStart();
        buttonOpenBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (statusAnimation) {
                    stopPulse();
                    buttonOpenBox.setText(getString(R.string.open));
                } else {
                    startPulse();
                    buttonOpenBox.setText(getString(R.string.connection));
                }
                statusAnimation = !statusAnimation;
            }
        });
    }

        private void startPulse() {
            runnable.run();
        }

        private void stopPulse() {
            handlerAnimation.removeCallbacks(runnable);
        }



    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            imageAnimationButton.animate()
                    .scaleX(4f)
                    .scaleY(4f)
                    .alpha(0f)
                    .setDuration(1000)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            imageAnimationButton.setScaleX(1f);
                            imageAnimationButton.setScaleY(1f);
                            imageAnimationButton.setAlpha(1f);
                        }
                    });

            imageAnimationButton.animate()
                    .scaleX(4f)
                    .scaleY(4f)
                    .alpha(0f)
                    .setDuration(700)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            imageAnimationButton.setScaleX(1f);
                            imageAnimationButton.setScaleY(1f);
                            imageAnimationButton.setAlpha(1f);
                        }
                    });

            handlerAnimation.postDelayed(this, 1500);
        }
    };

    private void verifyBluetoothConnection()
    {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Utility.showAlertDialog(getActivity(), getString(R.string.bluetoothNotSupportedTitle), getString(R.string.bluetoothNotSupported));
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.popBackStack();
        } else if (!mBluetoothAdapter.isEnabled()) {
            showAlertDialogBluetoothEnabled();
        } else {
            // Bluetooth is enabled
        }
    }


    public void showAlertDialogBluetoothEnabled() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        // Set the dialog title and message
        builder.setTitle(getActivity().getString(R.string.bluetoothNotEnabledTitle))
                .setMessage(getActivity().getString(R.string.bluetoothNotEnabledTitle))
                .setPositiveButton(getActivity().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        getActivity().onBackPressed();
                    }
                })
                .setNegativeButton(getActivity().getString(R.string.activateBluetooth), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        Intent intentOpenBluetoothSettings = new Intent();
                        intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                        startActivity(intentOpenBluetoothSettings);
                    }

                });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
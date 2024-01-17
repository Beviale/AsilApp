package uniba.roadhouse.asilapp.controller.doctor;

import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanIntentResult;
import com.journeyapps.barcodescanner.ScanOptions;

import uniba.roadhouse.asilapp.R;


public class QRCameraFragment extends Fragment {

    public QRCameraFragment() {

    }


    public static QRCameraFragment newInstance(String param1, String param2) {
        QRCameraFragment fragment = new QRCameraFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScanOptions scanCamera = new ScanOptions();
        scanCamera.setOrientationLocked(true);
        scanCamera.setCaptureActivity(CaptAct.class);
        scanResult.launch(scanCamera);

    }

    public class CaptAct extends CaptureActivity{

    }

    ActivityResultLauncher scanResult = registerForActivityResult(new ScanContract(), res->{
        if(res.getContents() != null){
            Toast.makeText(getActivity(), res.getContents(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Null", Toast.LENGTH_SHORT).show();
        }
    });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_q_r_camera, container, false);
    }
}
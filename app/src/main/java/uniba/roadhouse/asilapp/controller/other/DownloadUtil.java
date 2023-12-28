package uniba.roadhouse.asilapp.controller.other;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public abstract class DownloadUtil {

    public static void startDownload(Context context, String fileUrl) {
        Uri uri = Uri.parse(fileUrl);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);

        context.startActivity(intent);
    }

}

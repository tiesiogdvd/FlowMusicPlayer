package eu.tutorial.androidapplicationfilesystem.classes;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;


public class PermissionsRetriever {

    public static int checkPermissions(Context context){
        Boolean resultStorageGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        Boolean resultRecordAudioGranted = ContextCompat.checkSelfPermission(context,Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;

        List<String> permissionRequest = new ArrayList<String>();

        if (!resultStorageGranted){
            permissionRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(!resultRecordAudioGranted){
            permissionRequest.add(Manifest.permission.RECORD_AUDIO);
        }
        if(!permissionRequest.isEmpty()){
            ActivityCompat.requestPermissions((Activity) context, (String[]) permissionRequest.toArray(new String[permissionRequest.size()]),111);
        }
        if(resultStorageGranted && resultRecordAudioGranted){return 1;}
        if(resultStorageGranted){return 2;}
        if(resultRecordAudioGranted){return 3;}
        return 0;
    }
}

package com.vladocc.blink.betterlink.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.zxing.Result;
import com.vladocc.blink.betterlink.BlinkData;
import com.vladocc.blink.betterlink.R;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScanFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScanFragment extends Fragment implements ZXingScannerView.ResultHandler {

    ZXingScannerView zXingScannerView;

    public static final int CAMERA_PERMISSION_REQUEST = 0;

    public ScanFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ScanFragment.
     */
    public static ScanFragment newInstance() {
        return new ScanFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        zXingScannerView = new ZXingScannerView(getActivity());
        zXingScannerView.setResultHandler(this);
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST);
        } else {
            zXingScannerView.startCamera();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return zXingScannerView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        zXingScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        zXingScannerView.stopCamera();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            zXingScannerView.startCamera();
        }
    }

    @Override
    public void handleResult(Result result) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        boolean success = true;
        BlinkData blink = null;
        try {
            blink = gson.fromJson(result.getText(), BlinkData.class);
        } catch (JsonSyntaxException e){
            success = false;
        }
        Toast.makeText(getActivity(), success ? "Success" : "Error", Toast.LENGTH_SHORT).show();
        Intent intent = null;
        switch (blink.getType()){
            case (0):
                Uri address = Uri.parse(blink.getLink());
                intent = new Intent(Intent.ACTION_VIEW, address);
                break;
            case (1):
                Uri mailto = Uri.parse("mailto:"+blink.getLink());
                intent = new Intent(Intent.ACTION_SENDTO, mailto);
                intent.putExtra(Intent.EXTRA_EMAIL, blink.getLink());
                break;
            case (2):
                intent = new Intent(ContactsContract.Intents.Insert.ACTION);
                intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                intent.putExtra(ContactsContract.Intents.Insert.PHONE, blink.getLink());
                break;
        }
        startActivity(intent);
        zXingScannerView.resumeCameraPreview(this);
    }
}

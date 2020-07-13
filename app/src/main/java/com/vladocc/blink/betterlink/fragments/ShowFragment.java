package com.vladocc.blink.betterlink.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.vladocc.blink.betterlink.R;
import com.vladocc.blink.betterlink.SQLite.LinkDbHelper;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShowFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShowFragment extends Fragment {

    private ImageView codeImage;
    private AlertDialog dialog;
    private byte[] qrCodeBytes;

    public ShowFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ShowFragment.
     */
    public static ShowFragment newInstance() {
        return new ShowFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (qrCodeBytes != null) {
            showCode(qrCodeBytes);
        }
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null){
            setQrCodeBytes(savedInstanceState.getByteArray("qrCodeBytes"));

            boolean showFragmentDialog = savedInstanceState.getBoolean("showFragmentDialog");
            Log.w("showFragmentDialog", "" + showFragmentDialog);
            buildFragment(showFragmentDialog);

        } else {
            buildFragment(false);
        }
    }

    private void buildFragment(boolean showFragmentDialog) {
        if (showFragmentDialog || !hasQrCode()) {
            openDialog();
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putByteArray("qrCodeBytes", qrCodeBytes);

        outState.putBoolean("showFragmentDialog", isDialogOpened() || !hasQrCode());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_show, container, false);

        codeImage = (ImageView) view.findViewById(R.id.code_image);

        view.findViewById(R.id.show_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        return view;
    }

    public void openDialog(){
        final LinkDbHelper helper = LinkDbHelper.getInstance();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(helper.getNames(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                setQrCodeBytes(helper.getCode(which));
                dialogInterface.dismiss();
            }
        });

        dialog = builder.create();
        // quite a silly hack, which solves the problem with automatic closing of dialog
        // on any actions out of app
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {

            }
        });
        dialog.show();
        Log.w("shown", "QR Code choose dialog");
    }

    private void showCode(byte[] qr) {
        Bitmap qrCodeBitmap = createCodeBitmap(qr);
        updateCodeImage(qrCodeBitmap);
    }

    private Bitmap createCodeBitmap(byte[] qr){
        int dim = 256;
        Bitmap qrCodeBitmap = Bitmap.createBitmap(dim, dim, Bitmap.Config.ARGB_8888);
        for (int y = 0; y < dim; y++) {
            int step = dim * y;
            for (int x = 0; x < dim; x++) {
                int color = (qr[step + x] == 1)? 0xff000000 : 0xffffffff;
                qrCodeBitmap.setPixel(x, y, color);
            }
        }
        return qrCodeBitmap;
    }

    private void updateCodeImage(Bitmap qrCodeBitmap) {
        if (hasQrCode()) {
            Log.w("updated: ", "QR Code");
            codeImage.setImageBitmap(qrCodeBitmap);
        }
    }

    private boolean hasQrCode() {
        return qrCodeBytes != null;
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
    public void onPause() {
        super.onPause();

    }

    private boolean isDialogOpened() {
        return dialog != null && dialog.isShowing();
    }

    private void closeDialog(){
        if (isDialogOpened()){
            dialog.dismiss();
        }
    }

    public void setQrCodeBytes(byte[] qrCodeBytes) {
        this.qrCodeBytes = qrCodeBytes;
        if (qrCodeBytes != null) {
            showCode(qrCodeBytes);
        }
    }
}

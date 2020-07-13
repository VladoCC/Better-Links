package com.vladocc.blink.betterlink;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.GsonBuilder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.vladocc.blink.betterlink.SQLite.LinkDbHelper;

import java.util.HashMap;
import java.util.Map;

public class EditorDialogBuilder extends AlertDialog.Builder {

    String name = "";
    String link = "";
    int type = 0;
    int prefix = 0;

    int index = -1;

    DialogView dialogView;

    public EditorDialogBuilder(Activity activity) {
        super(activity);
        setTitle(R.string.dialog_add);

        dialogView = (DialogView) activity.getLayoutInflater().inflate(R.layout.dialog_content, null);
    }

    public EditorDialogBuilder setEditableEntry(int index) {
        LinkDbHelper helper = LinkDbHelper.getInstance();

        setTitle(R.string.dialog_edit);

        this.index = index;
        name = helper.getStringData(index, LinkDbHelper.COLUMN_NAME);
        link = helper.getStringData(index, LinkDbHelper.COLUMN_LINK);
        type = helper.getIntData(index, LinkDbHelper.COLUMN_TYPE);
        if (type == 0) {
            prefix = helper.getIntData(index, LinkDbHelper.COLUMN_PREFIX);
        }

        return this;
    }

    @Override
    public AlertDialog create() {
        dialogView.prepare(name, link, prefix, type);
        setView(dialogView);

        setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
                setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        final AlertDialog alertDialog = super.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button button = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                button.setOnClickListener(getListener(alertDialog));
            }
        });

        return alertDialog;
    }

    public View.OnClickListener getListener(final AlertDialog dialog) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogView.processClick(index, name, link, type)) {
                    dialog.dismiss();
                }
            }
        };
    }
}

class DialogView extends LinearLayout {

    private Spinner spinner;
    private Spinner prefixSpinner;
    private EditText nameText;
    private EditText linkText;
    private TextView errorText;
    private LinearLayout prefixLayout;

    public DialogView(Context context) {
        super(context);
    }

    public DialogView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DialogView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        spinner = (Spinner) findViewById(R.id.dialog_spinner);
        nameText = (EditText) findViewById(R.id.dialog_name);
        linkText = (EditText) findViewById(R.id.dialog_link);
        errorText = (TextView) findViewById(R.id.dialog_error);
        prefixSpinner = (Spinner) findViewById(R.id.prefix_link);
        prefixLayout = (LinearLayout) findViewById(R.id.prefix_layout);
    }

    private Spinner getSpinner() {
        return spinner;
    }

    private EditText getNameText() {
        return nameText;
    }

    private EditText getLinkText() {
        return linkText;
    }

    private TextView getErrorText() {
        return errorText;
    }

    private Spinner getPrefixSpinner() {
        return prefixSpinner;
    }

    private LinearLayout getPrefixLayout() {
        return prefixLayout;
    }

    public void prepare(String name, String link, int prefix, int type) {
        init();
        setupMainSpinner(getSpinner(), type);
        getPrefixSpinner().setSelection(prefix);
        getNameText().setText(name);
        getLinkText().setText(link);
    }

    private void setupMainSpinner(Spinner spinner, int type) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getPrefixLayout().removeAllViews();
                switch (position){
                    case (0):
                        getLinkText().setInputType(InputType.TYPE_CLASS_TEXT);
                        getLinkText().setHint(R.string.dialog_link);
                        getPrefixLayout().addView(getPrefixSpinner());
                        break;
                    case (1):
                        getLinkText().setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS | InputType.TYPE_CLASS_TEXT);
                        getLinkText().setHint(R.string.dialog_email);
                        break;
                    case (2):
                        getLinkText().setInputType(InputType.TYPE_CLASS_PHONE);
                        getLinkText().setHint(R.string.dialog_phone);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner.setSelection(type);
    }

    public boolean processClick(int index, String name, String link, int type) {
        String newName = getNameText().getText().toString().trim();
        String newLink = getLinkText().getText().toString();
        newLink = newLink.replaceAll("\\s+$", "");
        int newType = getSpinner().getSelectedItemPosition();

        boolean infoChanged = (!newName.equals(name) || !newLink.equals(link) || newType != type);
        if (infoChanged && validLink(newLink, newType)) {
            if (!newName.isEmpty()) {
                getErrorText().setText("");
                ContentValues values = new ContentValues(4);
                values.put(LinkDbHelper.COLUMN_NAME, newName);
                values.put(LinkDbHelper.COLUMN_LINK, newLink);
                values.put(LinkDbHelper.COLUMN_TYPE, newType);
                String pref = "";
                if (newType == 0) {
                    int newPrefix = getPrefixSpinner().getSelectedItemPosition();
                    String newPrefixText = getContext().getResources().getStringArray(R.array.add_spinner_prefix_list)[newPrefix];
                    values.put(LinkDbHelper.COLUMN_PREFIX, newPrefix);
                    values.put(LinkDbHelper.COLUMN_PREFIX_TEXT, newPrefixText);
                    pref = newPrefixText;
                } else {
                    values.put(LinkDbHelper.COLUMN_PREFIX_TEXT, "");
                }

                int img = android.R.drawable.ic_menu_view;
                if (newType == 1) {
                    img = android.R.drawable.ic_dialog_email;
                }
                values.put(LinkDbHelper.COLUMN_IMAGE, img);

                String fullLink = pref + newLink;
                try {
                    BlinkData blink = new BlinkData(fullLink, newType);
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    String codeText = gsonBuilder.create().toJson(blink); //links[which] + "|@|" + types[which];
                    MultiFormatWriter writer = new MultiFormatWriter();
                    Map<EncodeHintType, String> map = new HashMap<>();
                    map.put(EncodeHintType.CHARACTER_SET, "UTF-8");
                    BitMatrix bitMatrix = writer.encode(codeText, BarcodeFormat.QR_CODE, 256, 256, map);

                    int w = bitMatrix.getWidth();
                    int h = bitMatrix.getHeight();
                    byte[] qr = new byte[w * h];
                    for (int y = 0; y < h; y++) {
                        int step = w * y;
                        for (int x = 0; x < w; x++) {
                            qr[step + x] = bitMatrix.get(x, y) ? (byte) 1 : 0;
                        }
                    }
                    values.put(LinkDbHelper.COLUMN_CODE, qr);
                } catch (WriterException e) {
                    e.printStackTrace();
                }

                if (index == -1) {
                    LinkDbHelper.getInstance().insert(values);
                } else {
                    LinkDbHelper.getInstance().update(values, index);
                }
                return true;
            } else {
                getErrorText().setText(R.string.incorrect_name);
            }
        } else {
            getErrorText().setText(R.string.incorrect_link);
        }
        return false;
    }

    public boolean validLink(String link, int type){
        switch (type){
            case (0):
                boolean check = URLUtil.isValidUrl("http://" + link);
                boolean spaces = link.contains(" ");
                boolean slashDot = link.contains("/.");
                Log.w("link states", check + " " + spaces + " " + slashDot);
                return check && !spaces && !slashDot;
            case (1):
                return Patterns.EMAIL_ADDRESS.matcher(link).matches();
            case (2):
                return Patterns.PHONE.matcher(link).matches();
        }
        return false;
    }
}

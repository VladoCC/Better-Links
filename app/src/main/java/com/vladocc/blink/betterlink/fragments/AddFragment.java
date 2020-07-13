package com.vladocc.blink.betterlink.fragments;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.BaseColumns;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.GsonBuilder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.vladocc.blink.betterlink.BlinkData;
import com.vladocc.blink.betterlink.EditorDialogBuilder;
import com.vladocc.blink.betterlink.R;
import com.vladocc.blink.betterlink.SQLite.LinkDbHelper;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddFragment extends Fragment {

    SimpleCursorAdapter mAdapter;
    ListView listView;
    AlertDialog dialog;

    public AddFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AddFragment.
     */
    public static AddFragment newInstance() {
        return new AddFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        listView = (ListView) view.findViewById(R.id.links_list);
        fillListView(listView);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setItems(R.array.actions_dialog, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0){
                            showDialog(position);
                        } else if (which == 1){
                            LinkDbHelper.getInstance().removeEntry(position);
                        }

                        notifyDataChanged();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
        });

        Button button = (Button) view.findViewById(R.id.add_link_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(-1);
            }
        });

        return view;
    }

    private void notifyDataChanged() {
        mAdapter.getCursor().close();
        mAdapter.swapCursor(LinkDbHelper.getInstance().getCursor());
        mAdapter.notifyDataSetChanged();
    }

    private void fillListView(ListView listView) {
        Cursor cursor = LinkDbHelper.getInstance().getCursor();
        Log.w("DB entries count", "" + cursor.getCount());
        String[] fromColumns = {LinkDbHelper.COLUMN_NAME, LinkDbHelper.COLUMN_IMAGE,
                LinkDbHelper.COLUMN_LINK, LinkDbHelper.COLUMN_PREFIX_TEXT};
        int[] toViews = {R.id.link_name, R.id.link_image, R.id.link_view, R.id.link_prefix};

        mAdapter = new SimpleCursorAdapter(getActivity(),
                R.layout.links_list_item, cursor,
                fromColumns, toViews, 0);
        listView.setAdapter(mAdapter);
    }

    public void showDialog(final int index){
        final EditorDialogBuilder builder = new EditorDialogBuilder(getActivity());
        if (index != -1) {
            builder.setEditableEntry(index);
        }
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                notifyDataChanged();
            }
        });
        dialog = builder.create();

        dialog.show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}

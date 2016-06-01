package com.kitayupov.bank;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.activity_main;

    private ListView listView;
    private ArrayList<Bid> arrayList;
    private BidAdapter adapter;
    private BidDbHelper dbHelper;

    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LAYOUT);

        initUser();
        initControls();
    }

    @Override
    public void onBackPressed() {
        final RelativeLayout layout = (RelativeLayout) findViewById(R.id.main_root_layout);
        Snackbar.make(layout, R.string.message_exit, Snackbar.LENGTH_LONG)
                .setAction(R.string.button_yes, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }).show();
    }

    private void initUser() {
        userId = getIntent().getIntExtra(Constants.USER, 0);
        switch (userId) {
            case 0:
                setTitle("Operator");
                break;
            case 1:
                setTitle("Administrator 1");
                break;
            case 2:
                setTitle("Administrator 2");
                break;
            default:
        }
    }

    private void initControls() {
        arrayList = new ArrayList<>();
        dbHelper = new BidDbHelper(this);
        adapter = new BidAdapter(this, arrayList);
        listView = (ListView) findViewById(R.id.listView);
        if (listView != null) {
            listView.setAdapter(adapter);
            if (userId == Constants.OPERATOR_ID) {
                initEditor();
            } else {
                registerForContextMenu(listView);
            }
            readData();
        }
    }

    private void readData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(BidDbHelper.TABLE_NAME, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int descriptionIndex = cursor.getColumnIndex(Constants.DESCRIPTION);
            int nameIndex = cursor.getColumnIndex(Constants.NAME);
            int photoIndex = cursor.getColumnIndex(Constants.PHOTO);
            int statusAIndex = cursor.getColumnIndex(Constants.STATUS_A);
            int statusBIndex = cursor.getColumnIndex(Constants.STATUS_B);
            int dateIndex = cursor.getColumnIndex(Constants.DATE);
            do {
                String description = cursor.getString(descriptionIndex);
                String name = cursor.getString(nameIndex);
                int photoId = cursor.getInt(photoIndex);
                Constants.Status statusA = Constants.Status.valueOf(cursor.getString(statusAIndex));
                Constants.Status statusB = Constants.Status.valueOf(cursor.getString(statusBIndex));
                long date = cursor.getLong(dateIndex);
                Bid item = new Bid(description, name, photoId, statusA, statusB, date);
                arrayList.add(item);
                Log.d("kitayupov", item.toString());
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    private void initEditor() {
        final LinearLayout editor = (LinearLayout) findViewById(R.id.editor_linearlayout);
        editor.setVisibility(View.VISIBLE);
        final EditText descriptionEdit = (EditText) findViewById(R.id.description_edittext);
        final EditText nameEdit = (EditText) findViewById(R.id.name_edittext);
        Button okButton = (Button) findViewById(R.id.ok_button);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = descriptionEdit.getText().toString().trim();
                String name = nameEdit.getText().toString().trim();
                boolean emptyField = false;
                if ("".equals(description)) {
                    descriptionEdit.setError(getString(R.string.alert_empty_field));
                    emptyField = true;
                }
                if ("".equals(name)) {
                    nameEdit.setError(getString(R.string.alert_empty_field));
                    emptyField = true;
                }
                if (!emptyField) {
                    descriptionEdit.setText(null);
                    nameEdit.setText(null);
                    addBid(description, name);
                    descriptionEdit.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(descriptionEdit.getWindowToken(), 0);
                }
            }
        });
    }

    private void addBid(String description, String name) {
        Bid item = new Bid(description, name, R.mipmap.ic_launcher);
        arrayList.add(item);
        adapter.notifyDataSetChanged();
        Log.d("kitayupov", item.toString());

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.insert(BidDbHelper.TABLE_NAME, null, getValues(item));
    }

    private ContentValues getValues(Bid item) {
        ContentValues values = new ContentValues();
        values.put(Constants.DESCRIPTION, item.getDescription());
        values.put(Constants.NAME, item.getName());
        values.put(Constants.PHOTO, item.getImgRes());
        values.put(Constants.STATUS_A, item.getStatusA().name());
        values.put(Constants.STATUS_B, item.getStatusB().name());
        values.put(Constants.DATE, item.getDate());
        return values;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.accept:
            case R.id.deny:
                setItemStatus(info.position, item.getItemId());
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void setItemStatus(int position, int itemId) {
        Bid item = arrayList.get(position);
        if (itemId == R.id.accept) {
            item.setStatus(userId, Constants.Status.ACCEPT);
        }
        if (itemId == R.id.deny) {
            item.setStatus(userId, Constants.Status.DENY);
        }
        adapter.notifyDataSetChanged();
        Log.d("kitayupov", item.toString());

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String whereClause =
                Constants.DESCRIPTION + "=? and " + Constants.NAME + "=? and " +
                        Constants.PHOTO + "=? and " + Constants.DATE + "=?";
        String[] whereArgs = new String[]{
                item.getDescription(), item.getName(),
                String.valueOf(item.getImgRes()), String.valueOf(item.getDate())};

        db.update(BidDbHelper.TABLE_NAME, getValues(item), whereClause, whereArgs);

    }
}
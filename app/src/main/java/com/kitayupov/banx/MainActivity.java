package com.kitayupov.banx;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int LAYOUT = R.layout.activity_main;

    public static final String DESCRIPTION = "description";
    public static final String NAME = "name";
    public static final String PHOTO = "photo";
    public static final String STATUS_A = "status_a";
    public static final String STATUS_B = "status_b";

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
            if (userId != Constants.OPERATOR_ID) {
                registerForContextMenu(listView);
            }
            readData();
        }
    }

    private void readData() {
        arrayList.add(new Bid("Credit1", "Anrdrew McNeal1", R.mipmap.ic_launcher));
        arrayList.add(new Bid("Credit2", "Anrdrew McNeal2", R.mipmap.ic_launcher));
        arrayList.add(new Bid("Credit3", "Anrdrew McNeal3", R.mipmap.ic_launcher));
        arrayList.add(new Bid("Credit4", "Anrdrew McNeal4", R.mipmap.ic_launcher));
        arrayList.add(new Bid("Credit5", "Anrdrew McNeal5", R.mipmap.ic_launcher));

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(BidDbHelper.TABLE_NAME, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int descriptionIndex = cursor.getColumnIndex(DESCRIPTION);
            int nameIndex = cursor.getColumnIndex(NAME);
            int photoIndex = cursor.getColumnIndex(PHOTO);
            int statusAIndex = cursor.getColumnIndex(STATUS_A);
            int statusBIndex = cursor.getColumnIndex(STATUS_B);
            do {
                String description = cursor.getString(descriptionIndex);
                String name = cursor.getString(nameIndex);
                int photoId = cursor.getInt(photoIndex);
                Constants.Status statusA = Constants.Status.valueOf(cursor.getString(statusAIndex));
                Constants.Status statusB = Constants.Status.valueOf(cursor.getString(statusBIndex));
                Bid item = new Bid(description, name, photoId, statusA, statusB);
                arrayList.add(item);
            } while (cursor.moveToNext());
            cursor.close();
        }
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

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DESCRIPTION, item.getDescription());
        values.put(NAME, item.getName());
        values.put(PHOTO, item.getImgRes());
        values.put(STATUS_A, item.getStatusA().name());
        values.put(STATUS_B, item.getStatusB().name());

        String whereClause =
                DESCRIPTION + "=? and " + NAME + "=? and " + PHOTO + "=? and " +
                        STATUS_A + "=? and " + STATUS_B + "=?";
        String[] whereArgs = new String[]{
                item.getDescription(), item.getName(), String.valueOf(item.getImgRes()),
                item.getStatusA().name(), item.getStatusB().name()};

        db.update(BidDbHelper.TABLE_NAME, values, whereClause, whereArgs);
    }
}
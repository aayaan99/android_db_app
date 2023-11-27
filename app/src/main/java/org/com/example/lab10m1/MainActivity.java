package org.com.example.lab10m1;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private EditText editTextId, editTextUniversity, editTextCity, editTextFounded;
    private ListView listViewUniversities;
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        editTextId = findViewById(R.id.editTextID);
        editTextUniversity = findViewById(R.id.editTextUniversity);
        editTextCity = findViewById(R.id.editTextCity);
        editTextFounded = findViewById(R.id.editTextFounded);
        listViewUniversities = findViewById(R.id.listViewUniversities);

        Button buttonInitialize = findViewById(R.id.buttonInitialize);
        Button buttonInsert = findViewById(R.id.buttonInsert);
        Button buttonUpdate = findViewById(R.id.buttonUpdate);
        Button buttonDelete = findViewById(R.id.buttonDelete);
        Button buttonQuery = findViewById(R.id.buttonQuery);

        buttonInitialize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.getWritableDatabase();
            }
        });

        buttonInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertUniversity();
            }
        });

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUniversity();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUniversity();
            }
        });

        buttonQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryUniversities();
            }
        });

        setupListViewAdapter();
    }

    private void insertUniversity() {
        // Get writable database
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Gather university details
        values.put(DatabaseHelper.KEY_NAME, editTextUniversity.getText().toString());
        values.put(DatabaseHelper.KEY_CITY, editTextCity.getText().toString());
        values.put(DatabaseHelper.KEY_YEAR_FOUNDED, editTextFounded.getText().toString()); // Use the correct column name

        // Insert into database
        long id = db.insert(DatabaseHelper.TABLE_UNIVERSITY, null, values);

        if (id != -1) {
            editTextId.setText(String.valueOf(id));
            queryAllUniversities(); // Query all universities after insertion
        }
    }


    private void updateUniversity() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.KEY_NAME, editTextUniversity.getText().toString());
        values.put(DatabaseHelper.KEY_CITY, editTextCity.getText().toString());
        values.put(DatabaseHelper.KEY_YEAR_FOUNDED, editTextFounded.getText().toString());

        String universityId = editTextId.getText().toString();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_UNIVERSITY,
                null,
                DatabaseHelper.KEY_ID + " = ?",
                new String[]{universityId},
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            // Record exists, update it
            String selection = DatabaseHelper.KEY_ID + " = ?";
            String[] selectionArgs = {universityId};

            int count = db.update(
                    DatabaseHelper.TABLE_UNIVERSITY,
                    values,
                    selection,
                    selectionArgs
            );

            if (count > 0) {
                queryAllUniversities(); // Query all universities after update
            }

            cursor.close();
        } else {
            // Record doesn't exist, insert a new one
            long id = db.insert(DatabaseHelper.TABLE_UNIVERSITY, null, values);

            if (id != -1) {
                editTextId.setText(String.valueOf(id));
                queryAllUniversities(); // Query all universities after insertion
            }
        }
    }



    private void deleteUniversity() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = DatabaseHelper.KEY_ID + " = ?";
        String[] selectionArgs = { editTextId.getText().toString() };
        db.delete(DatabaseHelper.TABLE_UNIVERSITY, selection, selectionArgs);
        queryAllUniversities(); // Query all universities after deletion
    }


    private void queryUniversities() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String universityName = editTextUniversity.getText().toString().trim();
        String city = editTextCity.getText().toString().trim();
        String foundedYear = editTextFounded.getText().toString().trim();

        String selection = "";
        List<String> selectionArgsList = new ArrayList<>();

        if (!universityName.isEmpty()) {
            selection += DatabaseHelper.KEY_NAME + " LIKE ? ";
            selectionArgsList.add("%" + universityName + "%");
        }

        if (!city.isEmpty()) {
            if (!selection.isEmpty()) {
                selection += " AND ";
            }
            selection += DatabaseHelper.KEY_CITY + " LIKE ? ";
            selectionArgsList.add("%" + city + "%");
        }

        if (!foundedYear.isEmpty()) {
            if (!selection.isEmpty()) {
                selection += " AND ";
            }
            selection += DatabaseHelper.KEY_YEAR_FOUNDED + " = ? ";
            selectionArgsList.add(foundedYear);
        }

        String[] selectionArgs = selectionArgsList.toArray(new String[0]);

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_UNIVERSITY,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        adapter.changeCursor(cursor);
    }



    private void setupListViewAdapter() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_UNIVERSITY, null, null, null, null, null, null);

        String[] fromColumns = {DatabaseHelper.KEY_ID, DatabaseHelper.KEY_NAME, DatabaseHelper.KEY_CITY, DatabaseHelper.KEY_YEAR_FOUNDED};
        int[] toViews = {R.id.textViewID, R.id.textViewName, R.id.textViewCity, R.id.textViewYear};

        adapter = new SimpleCursorAdapter(this,
                R.layout.list_item_university,  // Use the new layout
                cursor,
                fromColumns,
                toViews,
                0);
        listViewUniversities.setAdapter(adapter);
    }

    private void queryAllUniversities() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_UNIVERSITY,
                null,
                null,
                null,
                null,
                null,
                null
        );

        adapter.changeCursor(cursor);
    }


}

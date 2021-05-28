package com.gkmit.todo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private ArrayAdapter<String> mAdapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.list);
        updateList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_action, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_task) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Add Todo Task Item");
            builder.setMessage("Please write the task...");
            final EditText todoET = new EditText(this);
            builder.setView(todoET);
            builder.setPositiveButton("Add Task", (dialogInterface, i) -> {
                String todoTaskInput = todoET.getText().toString();
                dbHelper = new DatabaseHelper(MainActivity.this);
                SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.clear();
                
                values.put(dbHelper.COL1_TASK, todoTaskInput);
                sqLiteDatabase.insertWithOnConflict(dbHelper.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);

                updateList();
                });

                builder.setNegativeButton("Cancel", null);

                builder.create().show();
        }
        return true;
    }
    private void updateList() {
        ArrayList<String> taskList = new ArrayList<>();
        dbHelper = new DatabaseHelper(MainActivity.this);
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        
        Cursor cursor = sqLiteDatabase.query(dbHelper.TABLE_NAME,
                new String[]{dbHelper._ID, dbHelper.COL1_TASK},
                null, null, null, null, null);
        
        while (cursor.moveToNext()) {
            int index = cursor.getColumnIndex(dbHelper.COL1_TASK);
            taskList.add(cursor.getString(index));
        }
        
        if (mAdapter == null) {
            mAdapter = new ArrayAdapter<String>(this, R.layout.todotask, R.id.textview_task_title, taskList);
            listView.setAdapter(mAdapter);
        } else {
            mAdapter.clear();
            mAdapter.addAll(taskList);
            mAdapter.notifyDataSetChanged();
        }
        cursor.close();
        dbHelper.close();
    }

    public void onDoneButtonClick(View view) {
        View v = (View) view.getParent();
        TextView textViewToDo =  v.findViewById(R.id.textview_task_title);
        String taskItem = textViewToDo.getText().toString();

        String deleteTodoItemSql = "DELETE FROM " + dbHelper.TABLE_NAME +
                " WHERE " + dbHelper.COL1_TASK + " = '" + taskItem + "'";

        dbHelper = new DatabaseHelper(MainActivity.this);
        SQLiteDatabase sqlDB = dbHelper.getWritableDatabase();
        sqlDB.execSQL(deleteTodoItemSql);
        updateList();
    }

}
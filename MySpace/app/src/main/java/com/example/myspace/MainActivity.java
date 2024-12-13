package com.example.myspace;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private ArrayList<String> todoList;
    private ArrayAdapter<String> adapter;
    private EditText editText;
    private ListView listView;
    private static final String TODO_LIST_KEY = "todo_list";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        listView = findViewById(R.id.listView);
        Button addButton = findViewById(R.id.addButton);

        // Load the to-do list from local storage
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        todoList = new ArrayList<>(sharedPreferences.getStringSet(TODO_LIST_KEY, new HashSet<>()));

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, todoList);
        listView.setAdapter(adapter);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String item = editText.getText().toString();
                if (!item.isEmpty()) {
                    todoList.add(item);
                    adapter.notifyDataSetChanged();
                    saveTodoList();
                    editText.setText("");
                }
            }
        });

        // Show dialog to modify item when item clicked
        listView.setOnItemClickListener((parent, view, position, id) -> {
            showCustomDialog(position);
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                todoList.remove(position);
                adapter.notifyDataSetChanged();
                saveTodoList();

                return true;
            }
        });
    }

    // Save the to-do list to local storage
    private void saveTodoList() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> set = new HashSet<>(todoList);
        editor.putStringSet(TODO_LIST_KEY, set);
        editor.apply();
    }
    // Method to show the custom dialog
    private void showCustomDialog(int position) {
        // Inflate the layout for the custom dialog
        View dialogView = getLayoutInflater().inflate(R.layout.custom_dialog, null);

        // Find the EditText inside the custom dialog layout
        EditText editText = dialogView.findViewById(R.id.editText);
        editText.setText(todoList.get(position));
        // Create a dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setTitle("Edit")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text = editText.getText().toString();
                        todoList.set(position, text);
                        adapter.notifyDataSetChanged();
                        saveTodoList();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        // Show the custom dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
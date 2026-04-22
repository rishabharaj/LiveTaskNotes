package com.example.livetasknotes;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // UI components
    private EditText etTask;
    private Button btnAdd, btnPrevDay, btnNextDay;
    private ListView lvTasks;
    private TextView tvDate, tvEmpty;
    private View inputRow;

    // Firebase
    private DatabaseReference rootRef;

    // Data
    private ArrayList<String> taskList;
    private ArrayList<String> taskKeyList;       // Firebase keys
    private ArrayList<Boolean> taskDoneList;      // done status
    private ArrayAdapter<String> adapter;

    // Date tracking
    private Calendar selectedDate;
    private SimpleDateFormat dateKeyFormat;       // "yyyy-MM-dd" for Firebase key
    private SimpleDateFormat displayFormat;       // "dd MMM yyyy" for display

    // Listener reference (to remove when switching dates)
    private ValueEventListener currentListener;
    private DatabaseReference currentDateRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ---- Bind views ----
        etTask     = findViewById(R.id.etTask);
        btnAdd     = findViewById(R.id.btnAdd);
        btnPrevDay = findViewById(R.id.btnPrevDay);
        btnNextDay = findViewById(R.id.btnNextDay);
        lvTasks    = findViewById(R.id.lvTasks);
        tvDate     = findViewById(R.id.tvDate);
        tvEmpty    = findViewById(R.id.tvEmpty);
        inputRow   = findViewById(R.id.inputRow);

        // ---- Firebase root reference ----
        rootRef = FirebaseDatabase.getInstance().getReference("daily_tasks");

        // ---- Date formatters ----
        dateKeyFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        displayFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);

        // ---- Initialize date to today ----
        selectedDate = Calendar.getInstance();

        // ---- Initialize lists & adapter ----
        taskList     = new ArrayList<>();
        taskKeyList  = new ArrayList<>();
        taskDoneList = new ArrayList<>();

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_multiple_choice, taskList);
        lvTasks.setAdapter(adapter);
        lvTasks.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        // ---- Update UI for today ----
        updateDateDisplay();
        loadTasksForSelectedDate();

        // ---- Date navigation ----
        btnPrevDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedDate.add(Calendar.DAY_OF_MONTH, -1);
                updateDateDisplay();
                loadTasksForSelectedDate();
            }
        });

        btnNextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Don't allow navigating to future dates
                Calendar tomorrow = Calendar.getInstance();
                tomorrow.add(Calendar.DAY_OF_MONTH, 1);
                Calendar check = (Calendar) selectedDate.clone();
                check.add(Calendar.DAY_OF_MONTH, 1);

                if (check.after(tomorrow)) {
                    Toast.makeText(MainActivity.this,
                            "Cannot go to future dates", Toast.LENGTH_SHORT).show();
                    return;
                }

                selectedDate.add(Calendar.DAY_OF_MONTH, 1);
                updateDateDisplay();
                loadTasksForSelectedDate();
            }
        });

        // ---- Add Task button click ----
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String task = etTask.getText().toString().trim();
                if (task.isEmpty()) {
                    Toast.makeText(MainActivity.this,
                            "Please enter a task", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Only allow adding tasks to today's date
                if (!isToday()) {
                    Toast.makeText(MainActivity.this,
                            "You can only add tasks for today", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Push a new task with text and done status
                String dateKey = dateKeyFormat.format(selectedDate.getTime());
                DatabaseReference newTaskRef = rootRef.child(dateKey).push();

                Map<String, Object> taskData = new HashMap<>();
                taskData.put("text", task);
                taskData.put("done", false);
                newTaskRef.setValue(taskData);

                etTask.setText("");
            }
        });

        // ---- Tap to toggle done/undone (checkbox) ----
        lvTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String key = taskKeyList.get(position);
                boolean currentDone = taskDoneList.get(position);
                boolean newDone = !currentDone;

                // Update Firebase
                String dateKey = dateKeyFormat.format(selectedDate.getTime());
                rootRef.child(dateKey).child(key).child("done").setValue(newDone);
            }
        });

        // ---- Long-press to delete a task ----
        lvTasks.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                String keyToDelete = taskKeyList.get(position);
                String dateKey = dateKeyFormat.format(selectedDate.getTime());
                rootRef.child(dateKey).child(keyToDelete).removeValue();

                Toast.makeText(MainActivity.this,
                        "Task deleted", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    /**
     * Updates the date header text.
     * Shows "Today" prefix if the selected date is today.
     */
    private void updateDateDisplay() {
        String dateStr = displayFormat.format(selectedDate.getTime());
        if (isToday()) {
            tvDate.setText("Today - " + dateStr);
            inputRow.setVisibility(View.VISIBLE);
        } else {
            tvDate.setText(dateStr);
            // Hide input row for past dates (optional: allow adding to past)
            inputRow.setVisibility(View.VISIBLE);
        }

        // Disable next button if we're on today
        btnNextDay.setEnabled(!isToday());
        btnNextDay.setAlpha(isToday() ? 0.4f : 1.0f);
    }

    /**
     * Checks if selectedDate is today.
     */
    private boolean isToday() {
        Calendar today = Calendar.getInstance();
        return selectedDate.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
               selectedDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * Loads tasks from Firebase for the selected date.
     * Removes any previous listener before attaching a new one.
     */
    private void loadTasksForSelectedDate() {
        // Remove previous listener
        if (currentListener != null && currentDateRef != null) {
            currentDateRef.removeEventListener(currentListener);
        }

        String dateKey = dateKeyFormat.format(selectedDate.getTime());
        currentDateRef = rootRef.child(dateKey);

        currentListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                taskList.clear();
                taskKeyList.clear();
                taskDoneList.clear();

                for (DataSnapshot child : snapshot.getChildren()) {
                    String key = child.getKey();

                    // Support both old format (plain string) and new format (map with text/done)
                    String text;
                    boolean done = false;

                    if (child.getValue() instanceof String) {
                        // Old format: just a string
                        text = child.getValue(String.class);
                    } else {
                        // New format: { text: "...", done: true/false }
                        text = child.child("text").getValue(String.class);
                        Boolean doneVal = child.child("done").getValue(Boolean.class);
                        done = (doneVal != null) ? doneVal : false;
                    }

                    if (text != null) {
                        taskList.add(text);
                        taskKeyList.add(key);
                        taskDoneList.add(done);
                    }
                }

                adapter.notifyDataSetChanged();

                // Update checkbox states in the ListView
                for (int i = 0; i < taskDoneList.size(); i++) {
                    lvTasks.setItemChecked(i, taskDoneList.get(i));
                }

                // Show/hide empty state
                if (taskList.isEmpty()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                    lvTasks.setVisibility(View.GONE);
                } else {
                    tvEmpty.setVisibility(View.GONE);
                    lvTasks.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this,
                        "Failed to read tasks: " + error.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        };

        currentDateRef.addValueEventListener(currentListener);
    }
}

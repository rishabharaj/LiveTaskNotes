package com.example.livetasknotes;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
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
    private TextView tvDate, tvEmpty, tvProgressText, tvTeamCodeInfo;
    private ProgressBar pbTaskProgress;
    private Button btnLogout;
    private View inputRow;

    // User Info
    private String userName;
    private String teamCode;

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

        // ---- Load User Info ----
        SharedPreferences prefs = getSharedPreferences("TaskAppPrefs", MODE_PRIVATE);
        userName = prefs.getString("userName", null);
        teamCode = prefs.getString("teamCode", null);

        if (userName == null || teamCode == null) {
            startActivity(new Intent(this, JoinTeamActivity.class));
            finish();
            return;
        }

        // ---- Bind views ----
        etTask     = findViewById(R.id.etTask);
        btnAdd     = findViewById(R.id.btnAdd);
        btnPrevDay = findViewById(R.id.btnPrevDay);
        btnNextDay = findViewById(R.id.btnNextDay);
        lvTasks    = findViewById(R.id.lvTasks);
        tvDate     = findViewById(R.id.tvDate);
        tvEmpty    = findViewById(R.id.tvEmpty);
        tvProgressText = findViewById(R.id.tvProgressText);
        pbTaskProgress = findViewById(R.id.pbTaskProgress);
        inputRow   = findViewById(R.id.inputRow);
        tvTeamCodeInfo = findViewById(R.id.tvTeamCodeInfo);
        btnLogout  = findViewById(R.id.btnLogout);

        // ---- Setup Header ----
        tvTeamCodeInfo.setText("Team Code: " + teamCode + " (Tap to copy)");

        tvTeamCodeInfo.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Team Code", teamCode);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(MainActivity.this, "Team Code Copied!", Toast.LENGTH_SHORT).show();
        });

        btnLogout.setOnClickListener(v -> {
            prefs.edit().clear().apply();
            startActivity(new Intent(MainActivity.this, JoinTeamActivity.class));
            finish();
        });

        // ---- Firebase root reference ----
        rootRef = FirebaseDatabase.getInstance().getReference("teams").child(teamCode).child("daily_tasks");

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

                // No restriction on adding tasks to past or future dates

                // Push a new task with text and done status
                String dateKey = dateKeyFormat.format(selectedDate.getTime());
                DatabaseReference newTaskRef = rootRef.child(dateKey).push();

                Map<String, Object> taskData = new HashMap<>();
                taskData.put("text", task);
                taskData.put("done", false);
                taskData.put("creator", userName);
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
        } else {
            tvDate.setText(dateStr);
        }
        
        // Ensure input row is always visible
        inputRow.setVisibility(View.VISIBLE);

        // Always enable next button to allow navigating to future
        btnNextDay.setEnabled(true);
        btnNextDay.setAlpha(1.0f);
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
                        // New format: { text: "...", done: true/false, creator: "..." }
                        text = child.child("text").getValue(String.class);
                        String creator = child.child("creator").getValue(String.class);
                        if (creator != null && !creator.isEmpty()) {
                            text = creator + ": " + text;
                        }
                        
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

                updateProgress();
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
    /**
     * Updates the progress bar and percentage text based on completed tasks.
     */
    private void updateProgress() {
        int totalTasks = taskDoneList.size();
        if (totalTasks == 0) {
            pbTaskProgress.setProgress(0);
            tvProgressText.setText("0%");
            return;
        }

        int completedTasks = 0;
        for (Boolean done : taskDoneList) {
            if (done != null && done) {
                completedTasks++;
            }
        }

        int percentage = (completedTasks * 100) / totalTasks;
        
        // Ensure progress is updated on the UI thread (usually called from Firebase callback which runs on main thread, but safe practice)
        pbTaskProgress.setProgress(percentage);
        tvProgressText.setText(percentage + "%");
    }
}

# 📖 CONCEPTS — Live Task Notes App mein Use Hue Sabhi Concepts

Yeh file explain karti hai ki is app ko banane mein kaunse-kaunse concepts use hue hain — simple Hinglish mein.

---

## 1️⃣ Android Activity & Lifecycle

### Activity kya hai?
Activity ek screen hai Android app mein. Hamare app mein ab 2 screens hain:
1. `JoinTeamActivity` — Jismein user apna naam aur team code dalta hai.
2. `MainActivity` — Jahan tasks dikhte hain.

Jab app khulta hai, `onCreate()` method sabse pehle call hota hai jahan hum UI setup karte hain.

---

## 2️⃣ SharedPreferences (Data Saving)

### Kya hai?
SharedPreferences chhote data (jaise username, team code) ko device mein permanently save karne ke liye use hota hai. Jab app band hokar wapas khulta hai, toh data wahi rehta hai.

### Use Case
Hum ise `JoinTeamActivity` mein save karte hain aur `MainActivity` mein padhte hain taaki user ko baar-baar login na karna pade.
```java
// Save data (Login/Join Team)
SharedPreferences prefs = getSharedPreferences("TaskAppPrefs", MODE_PRIVATE);
prefs.edit().putString("userName", name).putString("teamCode", code).apply();

// Read data (Dashboard)
String userName = prefs.getString("userName", null);

// Clear data (Logout)
prefs.edit().clear().apply();
```

---

## 3️⃣ Intents (Navigating between Screens)

### Kya hai?
Intent ek message hai jo Android ko batata hai ki ek Activity se dusri Activity par jana hai.
```java
// JoinTeamActivity se MainActivity par jana
startActivity(new Intent(JoinTeamActivity.this, MainActivity.class));
finish(); // Puraani activity band kar do taaki back press pe wapas na aaye
```

---

## 4️⃣ XML Layout System & Views

### Layouts
Hum app mein primarily `LinearLayout` (vertical aur horizontal stacking ke liye) use karte hain.

### Naye Views Used
| View | Purpose |
|------|---------|
| `ProgressBar` | Tasks kitne complete hue, uska visual representation dikhane ke liye |
| `ListView` | Scrollable list dikhane ke liye (tasks) |
| `TextView` / `EditText` / `Button` | Normal text, input aur clicks ke liye |

### ProgressBar Update
Hum `taskDoneList` se check karte hain kitne tasks complete hain aur percentage calculate karke ProgressBar update karte hain:
```java
int percentage = (completedTasks * 100) / totalTasks;
pbTaskProgress.setProgress(percentage);
tvProgressText.setText(percentage + "%");
```

---

## 5️⃣ Firebase Realtime Database

### Kya hai?
Google ka cloud-based NoSQL database. Data JSON tree structure mein store hota hai. Real-time sync — ek device pe change karo, sabhi devices pe turant dikhega.

### Team-based Structure in App
```java
// Root reference with Team Code
DatabaseReference rootRef = FirebaseDatabase.getInstance()
    .getReference("teams")
    .child(teamCode)
    .child("daily_tasks");

// Specific date reference
DatabaseReference dateRef = rootRef.child("2026-04-22");
```

### CRUD Operations

**Create (Add task with Creator Info):**
```java
DatabaseReference newTaskRef = dateRef.push(); // Unique key
Map<String, Object> data = new HashMap<>();
data.put("text", "Buy milk");
data.put("done", false);
data.put("creator", "Aman"); // Jisne add kiya uska naam
newTaskRef.setValue(data);
```

**Read (Real-time listen):**
```java
dateRef.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(DataSnapshot snapshot) {
        for (DataSnapshot child : snapshot.getChildren()) {
            String text = child.child("text").getValue(String.class);
            String creator = child.child("creator").getValue(String.class);
            Boolean done = child.child("done").getValue(Boolean.class);
        }
    }
});
```

---

## 6️⃣ ListView + ArrayAdapter

### Concept
ListView khud data nahi jaanta. Usse ek **Adapter** chahiye jo data source (ArrayList) ko UI list item se map kare.
Hum built-in layout `simple_list_item_multiple_choice` use karte hain jo checkbox ke saath aata hai.

Jab data badle (Firebase se real-time change aaye): `adapter.notifyDataSetChanged()` call karte hain.

---

## 7️⃣ ClipboardManager (Copy to Clipboard)

### Concept
Team code pe tap karke usey copy karne ka feature. Iske liye Android ki Clipboard service use hoti hai.

```java
ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
ClipData clip = ClipData.newPlainText("Team Code", teamCode);
clipboard.setPrimaryClip(clip);
Toast.makeText(this, "Team Code Copied!", Toast.LENGTH_SHORT).show();
```

---

## 8️⃣ Date Handling (Calendar)

```java
Calendar selectedDate = Calendar.getInstance(); // Aaj ki date
selectedDate.add(Calendar.DAY_OF_MONTH, -1); // Ek din peeche
selectedDate.add(Calendar.DAY_OF_MONTH, 1); // Ek din aage
```
Hum date ko String mein format karte hain (jaise `2026-04-22`) taaki usko Firebase node ki key bana sakein.

---

*📖 In concepts ko samajh lo toh viva mein koi bhi question aa jaye, confidently answer de paoge!*

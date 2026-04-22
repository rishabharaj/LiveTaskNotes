# 📝 Live Task Notes — Daily Task Tracker with Firebase

A single-screen Android app using **Firebase Realtime Database** to manage daily tasks with **checkboxes**, **date-wise history**, and **real-time sync**.

---

## ✨ App Features

| Feature | How it Works |
|---------|-------------|
| **Add Task** | Type → Tap "Add Task" → saves under today's date in Firebase |
| **Checkbox (✅/☐)** | Tap task to mark done ✅, tap again to uncheck |
| **Daily History** | ◀ / ▶ buttons to navigate between dates |
| **Real-time Sync** | `ValueEventListener` auto-updates ListView |
| **Delete Task** | Long-press → removes from Firebase instantly |
| **Today Indicator** | Shows "Today - 22 Apr 2026" for current date |
| **Empty State** | Shows message when a date has no tasks |

### Technical Highlights
- ✅ Zero custom classes — No custom Adapter/ViewHolder/Model
- ✅ Default `ArrayAdapter<String>` with `simple_list_item_multiple_choice`
- ✅ ALL logic in `MainActivity` — single file
- ✅ Only 2 core files — `MainActivity.java` + `activity_main.xml`
- ✅ Date-wise Firebase structure — Tasks organized by `yyyy-MM-dd`

---

## 🗄️ Firebase Database Structure

```
daily_tasks/
├── 2026-04-22/
│   ├── -key1: { "text": "Complete homework", "done": true }
│   └── -key2: { "text": "Buy groceries", "done": false }
└── 2026-04-21/
    └── -key3: { "text": "Submit report", "done": true }
```

Each task has: `text` (String) and `done` (Boolean).

---

## 📂 Project Structure

```
LiveTaskNotes/
├── build.gradle              ← Project-level (AGP 8.13.2)
├── settings.gradle           ← Module settings
├── app/
│   ├── build.gradle          ← Dependencies (Firebase, Material)
│   ├── google-services.json  ← 🔥 YOU must add this
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/.../MainActivity.java   ← ⭐ ALL logic
│       └── res/
│           ├── layout/activity_main.xml ← ⭐ UI layout
│           ├── mipmap-anydpi-v26/       ← Adaptive icons
│           └── values/ (strings, colors, themes)
├── README.md
├── VIVA_QUESTIONS.md         ← 📚 Viva Q&A
└── CONCEPTS.md               ← 📖 Concepts explained
```

---

## 🔥 Firebase Setup

1. Go to [Firebase Console](https://console.firebase.google.com/) → Create project
2. Click Android icon → Package: **`com.example.livetasknotes`**
3. Download `google-services.json` → Place in `app/` folder
4. Build → Realtime Database → Create Database → **Test mode**
5. Rules tab → Set `.read` and `.write` to `true`

---

## 🚀 How to Run

1. Open project in Android Studio
2. Wait for Gradle Sync
3. Place `google-services.json` in `app/`
4. Select device/emulator → Click ▶ Run

```powershell
# Alternative: Build from terminal
cd "D:\Antigravity\expense tracker\LiveTaskNotes"
.\gradlew.bat assembleDebug
.\gradlew.bat installDebug
```

---

## 🐛 Troubleshooting

| Problem | Solution |
|---------|----------|
| `google-services.json not found` | Place inside `app/` folder |
| Firebase permission denied | Set DB rules to test mode |
| `<adaptive-icon> error` | Icons must be in `mipmap-anydpi-v26/` |
| Gradle sync failed | File → Invalidate Caches → Restart |

---

*Built with ❤️ using Firebase Realtime Database + Android SDK 34 + AGP 8.13.2*

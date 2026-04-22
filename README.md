# 📝 Team-Based Live Task Notes — Collaborative Daily Task Tracker

An Android app using **Firebase Realtime Database** to manage daily tasks collaboratively. Users can join teams using a 6-digit code, share tasks, track progress, and sync everything in real-time.

---

## ✨ App Features

| Feature | How it Works |
|---------|-------------|
| **Team Support** | Join a team using a unique Team Code. All members see the same tasks. |
| **User Identity** | Tasks show the name of the creator (e.g., "Rahul: Fix the bug"). |
| **Progress Bar** | Visual progress bar showing the percentage of completed tasks for the day. |
| **Add Task** | Type → Tap "Add Task" → saves under the selected date in Firebase. |
| **Checkbox (✅/☐)** | Tap task to mark done ✅, tap again to uncheck. |
| **Daily History** | ◀ / ▶ buttons to navigate between dates (past and future dates allowed). |
| **Real-time Sync** | `ValueEventListener` auto-updates ListView for all team members. |
| **Delete Task** | Long-press → removes from Firebase instantly. |
| **Copy Team Code** | Tap the Team Code in the header to copy it to the clipboard. |
| **Logout** | Clear session and join a different team. |

### Technical Highlights
- ✅ `SharedPreferences` for managing user sessions and team codes.
- ✅ Two Activities: `JoinTeamActivity` (Login) and `MainActivity` (Dashboard).
- ✅ Real-time data syncing using Firebase `ValueEventListener`.
- ✅ Date-wise and Team-wise Firebase structure.

---

## 🗄️ Firebase Database Structure

```
teams/
└── ABC123/                              ← Team Code
    └── daily_tasks/
        ├── 2026-04-22/                  ← Date
        │   ├── -key1: { "text": "Complete homework", "done": true, "creator": "Aman" }
        │   └── -key2: { "text": "Buy groceries", "done": false, "creator": "Rohit" }
        └── 2026-04-21/
            └── -key3: { "text": "Submit report", "done": true, "creator": "Aman" }
```

Each task has: `text` (String), `done` (Boolean), and `creator` (String).

---

## 📂 Project Structure

```
LiveTaskNotes/
├── app/
│   ├── build.gradle          ← Dependencies (Firebase, Material)
│   ├── google-services.json  ← 🔥 YOU must add this
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/.../
│       │   ├── JoinTeamActivity.java    ← 🔑 Login / Team selection logic
│       │   └── MainActivity.java        ← ⭐ Core Dashboard logic
│       └── res/
│           ├── layout/
│           │   ├── activity_join_team.xml
│           │   └── activity_main.xml
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

---

## 🐛 Troubleshooting

| Problem | Solution |
|---------|----------|
| `google-services.json not found` | Place inside `app/` folder |
| Firebase permission denied | Set DB rules to test mode |
| Missing tasks when switching teams | Ensure you are logging out and entering the exact 6-digit code |
| Gradle sync failed | File → Invalidate Caches → Restart |

---

*Built with ❤️ using Firebase Realtime Database + Android SDK 34*

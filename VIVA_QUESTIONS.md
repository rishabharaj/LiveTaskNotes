# 📚 VIVA QUESTIONS — Live Task Notes App

Yeh file viva exam ke liye important questions aur answers contain karti hai.
Sabhi questions is app (Live Task Notes) se related hain.

---

## 🔰 Section 1: Basic Questions

### Q1: Yeh app kya karta hai?
**A:** Yeh ek Android task tracker app hai jo Firebase Realtime Database use karta hai. User daily tasks add kar sakta hai, checkbox se done mark kar sakta hai, aur ◀/▶ buttons se purane din ki history dekh sakta hai.

### Q2: Is app mein kitne files hain jo main logic contain karti hain?
**A:** Sirf 2 files:
- `MainActivity.java` — Saara Java logic (Firebase read/write/delete, date navigation)
- `activity_main.xml` — Complete UI layout

### Q3: Kaunsa database use kiya hai?
**A:** **Firebase Realtime Database** — yeh Google ka cloud-hosted NoSQL database hai jo real-time mein data sync karta hai sabhi connected clients ke beech.

### Q4: Firebase Realtime Database aur Firestore mein kya fark hai?
**A:**
| Feature | Realtime DB | Firestore |
|---------|------------|-----------|
| Data Structure | JSON tree | Collections + Documents |
| Querying | Limited | Advanced (compound queries) |
| Offline Support | Basic | Better |
| Pricing | Bandwidth + Storage | Read/Write operations |
| Best for | Simple real-time sync | Complex queries |

### Q5: `google-services.json` file kya hai?
**A:** Yeh Firebase configuration file hai jo Firebase Console se download hoti hai. Isme project ID, API key, database URL jaise details hote hain. Yeh `app/` folder mein rakhni padti hai. Iske bina app Firebase se connect nahi ho payega.

---

## 🔥 Section 2: Firebase Questions

### Q6: Firebase mein data kaise store ho raha hai?
**A:** Data date-wise store hota hai:
```
daily_tasks/
  2026-04-22/
    -pushKey1: { "text": "Do homework", "done": true }
    -pushKey2: { "text": "Buy milk", "done": false }
```
- `daily_tasks` = root node
- `2026-04-22` = date key (yyyy-MM-dd format)
- `-pushKey` = Firebase auto-generated unique key
- Har task ke andar `text` (String) aur `done` (Boolean) hai

### Q7: `push()` method kya karta hai?
**A:** `push()` Firebase mein ek unique key generate karta hai (jaise `-NxYz123abc`). Yeh ensure karta hai ki 2 tasks kabhi same key na paayein, even agar same time pe add ho.

### Q8: `setValue()` kya karta hai?
**A:** Yeh specified node pe data likhta hai. Agar pehle se data hai toh overwrite kar deta hai. Hum ise new task add karne ke liye use karte hain: `newTaskRef.setValue(taskData)`.

### Q9: `removeValue()` kya karta hai?
**A:** Yeh specified node ko Firebase se permanently delete kar deta hai. Long-press pe hum `tasksRef.child(key).removeValue()` call karte hain task delete karne ke liye.

### Q10: `ValueEventListener` kya hai aur kaise kaam karta hai?
**A:** Yeh ek listener hai jo Firebase node pe lagta hai. Jab bhi us node ka data change hota hai (add/update/delete), `onDataChange()` method automatically call hota hai. Isliye app mein tasks real-time update hote hain bina refresh kiye.

### Q11: `addValueEventListener` aur `addListenerForSingleValueEvent` mein kya fark hai?
**A:**
- `addValueEventListener` — Continuously listen karta hai. Jab bhi data change ho, fire hota hai. (Hum yeh use karte hain)
- `addListenerForSingleValueEvent` — Sirf ek baar data read karta hai aur listener remove ho jaata hai.

### Q12: `DataSnapshot` kya hai?
**A:** Jab `onDataChange()` call hota hai, ek `DataSnapshot` object milta hai jo us waqt ka poora data contain karta hai. Hum `snapshot.getChildren()` se sabhi child nodes iterate karte hain.

### Q13: Firebase Realtime Database ke rules kya set kiye hain?
**A:** Test mode mein:
```json
{ "rules": { ".read": true, ".write": true } }
```
Matlab koi bhi bina login ke read/write kar sakta hai. Production mein authentication rules lagane chahiye.

---

## 📱 Section 3: Android UI Questions

### Q14: Kaunsa layout use kiya hai?
**A:** `LinearLayout` with `android:orientation="vertical"`. Saare elements ek ke neeche ek stack hote hain.

### Q15: `ListView` kya hai?
**A:** ListView ek scrollable list widget hai jo multiple items dikhata hai. Hum isme tasks display karte hain. Yeh ek `Adapter` se data leke items render karta hai.

### Q16: `ArrayAdapter` kya hai?
**A:** Yeh ek built-in adapter hai jo `ArrayList<String>` ko `ListView` se connect karta hai. Har string ek list item ban jaati hai. Hum `simple_list_item_multiple_choice` layout use karte hain jo checkbox ke saath item dikhata hai.

### Q17: `simple_list_item_multiple_choice` kya hai?
**A:** Yeh Android ka built-in layout hai jo har list item ke saath ek checkbox dikhata hai. `ListView.CHOICE_MODE_MULTIPLE` set karne se multiple items check ho sakte hain.

### Q18: `notifyDataSetChanged()` kya karta hai?
**A:** Jab `ArrayList` ka data change hota hai (items add/remove), toh yeh method adapter ko batata hai ki "data badal gaya hai, UI ko refresh karo". Iske bina ListView update nahi hoga.

### Q19: `setItemChecked(position, true)` kya karta hai?
**A:** Yeh programmatically ListView mein kisi item ka checkbox check ya uncheck karta hai. Hum Firebase se `done` status padhte hain aur accordingly checkbox set karte hain.

### Q20: `EditText` kya hai?
**A:** Yeh user input field hai jahan user task type karta hai. `android:singleLine="true"` se single line input milta hai aur `android:inputType="text"` se normal text keyboard aata hai.

---

## ☕ Section 4: Java / Logic Questions

### Q21: `Calendar` class kya hai aur kaise use ki hai?
**A:** `java.util.Calendar` date aur time handle karne ke liye hai. Hum `Calendar.getInstance()` se aaj ki date lete hain. `calendar.add(Calendar.DAY_OF_MONTH, -1)` se ek din peeche jaate hain.

### Q22: `SimpleDateFormat` kaise use kiya hai?
**A:** Do formats use kiye:
- `"yyyy-MM-dd"` — Firebase key ke liye (e.g., `2026-04-22`)
- `"dd MMM yyyy"` — UI display ke liye (e.g., `22 Apr 2026`)

### Q23: `HashMap` kaise use kiya hai?
**A:** Task add karte waqt ek `HashMap<String, Object>` banate hain with `"text"` aur `"done"` keys, phir `setValue(taskData)` se Firebase mein push karte hain.

### Q24: Date navigation kaise kaam karta hai?
**A:**
1. Ek `Calendar selectedDate` variable maintain karte hain
2. ◀ button pe `selectedDate.add(DAY_OF_MONTH, -1)` — ek din peeche
3. ▶ button pe `selectedDate.add(DAY_OF_MONTH, +1)` — ek din aage (today tak)
4. Date change hone pe `loadTasksForSelectedDate()` call hota hai jo naya listener lagata hai

### Q25: Purana listener kaise remove karte hain?
**A:** Jab date change hoti hai, pehle `currentDateRef.removeEventListener(currentListener)` call karte hain. Phir nayi date ka listener attach karte hain. Agar yeh na karein toh multiple listeners active ho jayenge aur memory leak hoga.

### Q26: `isToday()` method kaise kaam karta hai?
**A:** Current `selectedDate` ka year + dayOfYear compare karta hai aaj ki date se:
```java
selectedDate.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
selectedDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
```

### Q27: Future date pe kyu nahi ja sakte?
**A:** ▶ button click pe check hota hai ki next date aaj se aage toh nahi. Agar aage hai toh Toast dikhata hai "Cannot go to future dates" aur date change nahi hoti. Button bhi disabled aur semi-transparent (alpha=0.4f) ho jaata hai.

---

## 🏗️ Section 5: Architecture Questions

### Q28: `AndroidManifest.xml` mein kya important entries hain?
**A:**
- `<uses-permission android:name="android.permission.INTERNET"/>` — Firebase ke liye internet zaroori hai
- `android:theme="@style/Theme.LiveTaskNotes"` — Material theme apply
- `.MainActivity` with `intent-filter` for `MAIN` + `LAUNCHER` — app launch point

### Q29: `build.gradle (app)` mein kya dependencies hain?
**A:**
- `appcompat` — Backward compatible Activity/Toolbar
- `material` — Material Design components
- `constraintlayout` — Layout system (used by default, hum LinearLayout use karte hain)
- `firebase-bom` — Firebase version management
- `firebase-database` — Realtime Database SDK

### Q30: `compileSdk` aur `minSdk` mein kya fark hai?
**A:**
- `compileSdk 34` — Kis SDK version se code compile hoga (latest APIs available)
- `minSdk 24` — App minimum Android 7.0 (Nougat) pe chalega
- `targetSdk 34` — App latest Android behavior follow karega

---

## 🎯 Section 6: Practical/Tricky Questions

### Q31: Agar internet nahi hai toh kya hoga?
**A:** Firebase Realtime DB limited offline support deta hai. Pehle se loaded data dikhta rahega lekin naye changes sync nahi honge jab tak internet wapas na aaye.

### Q32: Agar 2 log ek hi time pe task add karein toh?
**A:** `push()` har baar unique key generate karta hai, toh dono tasks safely save ho jayenge bina conflict ke. ValueEventListener dono devices pe real-time update karega.

### Q33: App mein authentication kyu nahi hai?
**A:** Yeh ek simple demo/assignment app hai. Production mein Firebase Auth lagana chahiye taaki har user apne tasks hi dekhe.

### Q34: `long-press` delete aur `single tap` checkbox mein conflict kaise avoid hota hai?
**A:** Android `OnItemClickListener` (short tap) aur `OnItemLongClickListener` (long press) ko alag handle karta hai. Long click listener mein `return true` karne se click event consume ho jaata hai.

### Q35: Agar date ke liye koi task nahi hai toh kya dikhega?
**A:** Empty state TextView dikhega: "📋 No tasks for this day / Add a task below!" — aur ListView hide ho jayegi.

---

## 💡 Tips for Viva

1. **Code dikhao toh samjhao** — Har line ka matlab batao, ratta mat maaro
2. **Firebase Console dikhao** — Real-time data change hota dikhega live
3. **Listener concept clear rakho** — Yeh sabse important topic hai
4. **push() vs setValue() fark samjho** — Push = unique key + write, setValue = direct overwrite
5. **Demo ready rakho** — App chalake dikhao, task add/delete/check live demo

---

*📝 Yeh file viva preparation ke liye hai — app ke saare important concepts cover kiye gaye hain*

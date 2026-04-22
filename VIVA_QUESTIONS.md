# 📚 VIVA QUESTIONS — Team-Based Live Task Notes App

Yeh file viva exam ke liye important questions aur answers contain karti hai.
Sabhi questions is app (Live Task Notes) se related hain.

---

## 🔰 Section 1: Basic Questions

### Q1: Yeh app kya karta hai?
**A:** Yeh ek collaborative Android task tracker app hai. Users ek 6-digit 'Team Code' se login karke team join karte hain, aur Firebase ke through real-time mein daily tasks share aur manage kar sakte hain.

### Q2: Is app mein kitne main Activities hain?
**A:** Do main Activities hain:
1. `JoinTeamActivity.java` — User ka naam aur team code lene ke liye.
2. `MainActivity.java` — Core dashboard jahan real-time task sync, add, aur delete hota hai.

### Q3: Kaunsa database use kiya hai?
**A:** **Firebase Realtime Database** — yeh Google ka cloud-hosted NoSQL database hai jo real-time mein data sync karta hai sabhi connected clients ke beech.

### Q4: SharedPreferences kya hai aur kyun use kiya?
**A:** SharedPreferences chhote key-value data ko device memory mein permanently save karne ke kaam aata hai. Hum ise user ka Name aur Team Code save karne ke liye use karte hain taaki app restart hone pe use baar-baar login na karna pade.

### Q5: `google-services.json` file kya hai?
**A:** Yeh Firebase configuration file hai jo Firebase Console se download hoti hai. Isme project ID, API key, database URL jaise details hote hain. Iske bina app Firebase se connect nahi ho payega.

---

## 🔥 Section 2: Firebase Questions

### Q6: Firebase mein data kaise store ho raha hai?
**A:** Data team-wise aur date-wise store hota hai:
```
teams/
  ABC123/
    daily_tasks/
      2026-04-22/
        -pushKey1: { "text": "Do homework", "done": true, "creator": "Aman" }
```
Har task ek unique push key ke andar apne text, done status, aur creator name ke sath store hota hai.

### Q7: `push()` method kya karta hai?
**A:** `push()` Firebase mein ek unique key generate karta hai. Yeh ensure karta hai ki agar multiple team members ek hi time pe task add karein, toh unke tasks same key na paayein aur overwrite na hon.

### Q8: `ValueEventListener` kya hai aur kaise kaam karta hai?
**A:** Yeh ek listener hai jo Firebase node pe lagta hai. Jab bhi us node ka data change hota hai (add/update/delete), `onDataChange()` method automatically call hota hai. Isliye saare team members ko live updates milte hain bina screen refresh kiye.

### Q9: `addValueEventListener` aur `addListenerForSingleValueEvent` mein kya fark hai?
**A:**
- `addValueEventListener` — Continuously listen karta hai. Jab bhi data change ho, fire hota hai. (Hum list update karne ke liye yeh use karte hain)
- `addListenerForSingleValueEvent` — Sirf ek baar data read karta hai aur listener remove ho jaata hai.

---

## 📱 Section 3: Android UI Questions

### Q10: `ListView` aur `ArrayAdapter` kaise kaam karte hain?
**A:** ListView ek scrollable UI widget hai. ArrayAdapter backend ArrayList ko ListView se connect karta hai aur list items render karta hai. Hum `simple_list_item_multiple_choice` use karte hain jisme checkbox in-built hota hai.

### Q11: ProgressBar ko kaise update karte hain?
**A:** Hum `taskDoneList` ko count karte hain ki kitne tasks 'true' hain. Phir total tasks se divide karke percentage nikalte hain `(completedTasks * 100) / totalTasks` aur us percentage ko `pbTaskProgress.setProgress()` se UI mein set kar dete hain.

### Q12: `notifyDataSetChanged()` kya karta hai?
**A:** Jab Firebase se naya data aata hai aur ArrayList update hota hai, toh yeh method adapter ko batata hai ki "data badal gaya hai, UI ko refresh karo".

### Q13: `ClipboardManager` ka kya use hai is app mein?
**A:** Jab user "Team Code" pe tap karta hai, toh `ClipboardManager` use karke hum us code ko phone ke clipboard mein copy kar dete hain, taaki user usko WhatsApp waghera pe share kar sake.

---

## ☕ Section 4: Java / Logic Questions

### Q14: Intents ka kya kaam hai?
**A:** `Intent` ek component se dusre component tak communication ka zariya hai. Hum ise `JoinTeamActivity` se `MainActivity` pe navigate karne aur login success handle karne ke liye use karte hain.

### Q15: Logout button dabane par kya hota hai?
**A:** Logout dabane par `SharedPreferences` se data clear ho jata hai `prefs.edit().clear().apply();`, aur user ko waapas `JoinTeamActivity` pe bhej diya jata hai team code change karne ke liye.

### Q16: Date navigation kaise kaam karta hai?
**A:**
1. Ek `Calendar selectedDate` variable maintain karte hain.
2. ◀ button pe `selectedDate.add(DAY_OF_MONTH, -1)` — ek din peeche.
3. ▶ button pe `selectedDate.add(DAY_OF_MONTH, +1)` — ek din aage (Future dates are allowed so you can plan tasks ahead of time).
4. Date change hone pe Firebase listener remove hoke naya lagta hai `loadTasksForSelectedDate()` ke zariye.

### Q17: Purana listener kaise remove karte hain aur kyun?
**A:** Jab date change hoti hai, pehle `currentDateRef.removeEventListener(currentListener)` call karte hain. Agar yeh na karein, toh multiple dates ke listener background mein chalte rahenge jisse memory leak aur wrong data merge hone ke issues aayenge.

---

## 🎯 Section 5: Practical/Tricky Questions

### Q18: Agar internet nahi hai toh kya hoga?
**A:** Firebase Realtime DB limited offline caching support deta hai. Pehle se loaded data dikhta rahega, lekin real-time team sync tabhi chalega jab net wapas aayega.

### Q19: Kya alag-alag teams ek dusre ka data dekh sakti hain?
**A:** Nahi. Root node ke baad seedha `teams/<Team Code>` ka path hai. Hum database ka query hi team code se start karte hain, isliye kisi aur team ke data tak access hi nahi banta.

### Q20: Agar `google-services.json` missing ho toh kya error aayega?
**A:** Android Studio build fail kar dega `Execution failed for task ':app:processDebugGoogleServices'. File google-services.json is missing.` error ke sath.

---

*📝 Yeh file viva preparation ke liye hai — app ke naye collaborative concepts cover kiye gaye hain*

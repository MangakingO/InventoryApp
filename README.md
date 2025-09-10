# CSE 3302 – Inventory Management Application

## 📌 Overview
This project is a group assignment for **CSE 3302**.  
We are building an **Android-based inventory management app** that integrates with **Firebase** for authentication, cloud storage, and real-time sync.  

The app will allow multiple UTA users to log in, manage inventory items, receive low-stock notifications, sync across devices, and export data to CSV/Excel.

---

## 👥 Team Members
- Oscar Barrios Jimenez – Authentication (UTA login), Firebase setup  
- Jacob Ross – Firestore data model, queries, CRUD operations  
- Antonio Chavez – UI/UX, RecyclerView lists, item editing  
- Abimbola Adeyemo – Notifications (low stock), Cloud Functions + FCM  
- Huy Le – Export (CSV/Excel), offline support, background sync  

---

## 🚀 Features
- 🔐 **UTA User Authentication**  
- 📦 **Inventory CRUD** – add, update, delete items  
- 📊 **Stock Monitoring** – highlight low-stock items  
- 🔔 **Notifications** – alerts when stock drops below thresholds  
- 🔄 **Multi-Device Sync** – live updates with Firestore listeners  
- 📤 **Export Data** – CSV and Excel file export  

---

## 🛠️ Tech Stack
- **Language**: Java (Android Studio)  
- **Architecture**: MVVM  
- **Backend**: Firebase Firestore + Security Rules  
- **Auth**: Firebase Auth (UTA login / SSO)  
- **Notifications**: Firebase Cloud Messaging + Cloud Functions  
- **Export**: CSV (Java I/O), Excel (Apache POI, optional)  

---

## 📅 Timeline (12 Weeks)
- **Week 1**: Repo setup, requirements, wireframes  
- **Week 2**: App skeleton & navigation  
- **Week 3**: UTA login & authentication (Oscar)  
- **Week 4**: User roles + security rules  
- **Week 5**: Inventory CRUD with Firestore  
- **Week 6**: Stock monitoring UI  
- **Week 7**: Notifications (Cloud Functions + FCM)  
- **Week 8**: Export CSV/Excel  
- **Week 9**: Offline support + polish  
- **Week 10**: Unit/UI testing, hardening  
- **Week 11**: Beta release, docs, screenshots  
- **Week 12**: Final fixes, demo, release  

---

## ⚙️ Setup
1. Clone repo:
   ```bash
   git clone https://github.com/<your-username>/CSE3302-InventoryApp.git

    Open in Android Studio.

    Add google-services.json to /app (from Firebase).

    Run Gradle sync → build → deploy to emulator or device.

🔄 Development Workflow

    Branches:

        main → stable, demo-ready

        test → integration branch

        feature/* → one per feature (auth, CRUD, etc.)

    Workflow:

        Create a feature branch

        Push changes → open Pull Request into test

        Merge test into main once tested

    CI/CD: GitHub Actions builds, tests, lints, and uploads APKs automatically.

✅ Success Criteria

    Only @uta.edu users can log in.

    Inventory updates sync in real time across devices.

    Low-stock items show warnings and trigger notifications.

    Data export produces valid CSV (Excel optional).

    App runs offline and syncs changes when online.

📸 Demo

(Screenshots and video demo will be added in Weeks 10–12)

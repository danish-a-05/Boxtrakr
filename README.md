# Smart Box-Tracker App  
**Group 19 – MSD Project**  
**Module:** Mobile Software Development  
**Team Members:**  
- Danish Arifuzzaman  
- Shane Farrelly  

---

## Main Concept  
The Smart Box-Tracker App is an Android application that helps users organize, categorize, and manage physical storage boxes.  
Users can easily record boxes, track their contents, and find where specific items are stored.  

Each box record includes:  
- A name  
- A thumbnail image of the box  
- A list of items with their names and quantities  

---

## Main Features  

### All Boxes Tab  
Displays all boxes in a card-style layout, featuring:  
- Thumbnail image (photo of the box)  
- Box name  

Additional functions:  
- Boxes are sorted by most recently viewed, with the latest at the top.  
- Long press on a box card to:  
  - Edit box details (name, thumbnail, or contents)  
  - Delete a box  
  - Multi-select multiple boxes for batch actions (delete or categorize)

---

### Categories Tab  
Functions like an “Add Room” feature, allowing users to:  
- Create custom categories (e.g., Bedroom Closet, Garage, Attic)  
- Add or remove boxes from categories via multi-selection  
- Quickly view all boxes within a specific category  

---

## Search Functionality  
- A search bar at the top of the interface enables fast lookup.  
- Searches across all boxes and items for any keyword (case-insensitive).  
- Dynamic results appear as users type, helping locate boxes or items instantly.

---

## Sensors Used  

### Camera Sensor  
- Used to capture thumbnail photos for each box.  
- Helps users visually identify boxes.  
- Accessed securely via the system camera intent, with permissions managed properly.

### Fingerprint Sensor  
- On supported devices, users can unlock the app or secure sections using fingerprint authentication.  
- If unavailable, a PIN or password fallback is provided for security.

---

## Data Management  
- All app data (boxes, items, categories, photos, timestamps) is stored locally using the Room Database.  
- State management ensures:  
  - User data persists between sessions  
  - UI state (like sorting preferences and selected tabs) remains consistent after restarts  

---

## Tech Stack  
- Platform: Android (Java / Kotlin)  
- Database: Room Database  
- UI: Android Jetpack Components  
- Authentication: Biometric / PIN Security  
- Architecture: MVVM (Mutable Live Data + ViewModel)  

---

## Future Enhancements  
- Cloud sync and backup  
- Notifications for box reminders or updates  
- Barcode/QR code scanning for faster box entry  

---

## Summary  
The Smart Box-Tracker App offers a simple yet powerful way to manage physical storage.  
By combining categorization, image tracking, biometric security, and local data persistence,  
it makes organization intuitive, secure, and efficient for everyday users.

---

Developed by Group 19 — TU Dublin MSD Project 2025.

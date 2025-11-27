# BoxTrakr

A clean and intuitive Android app for organising items into boxes and categories.

## 📦 Overview
**BoxTrakr** is an Android inventory-management app built with **Kotlin**, **Jetpack Compose**, and **Room**.  
It allows users to create **categories**, add **boxes** within each category, and store **items** inside those boxes — making it easy to keep track of belongings, storage units, moving boxes, or collections.

## 🚀 Features
- **Create, edit, and delete categories**  
- **Add boxes** associated with categories  
- **Add and edit box contents**  
- **Dedicated screen for viewing box details** with:  
  - Box name as the page header  
  - Lazy-loaded list of items  
- **Responsive UI** built using Jetpack Compose  
- **Local data persistence** with Room + Kotlin coroutines  
- **MVVM architecture** using ViewModels and repositories  
- **Smooth navigation** powered by Navigation Compose  
- **Fully offline** (no external APIs required)

## 🛠️ Tech Stack
- **Kotlin**  
- **Jetpack Compose**  
- **Room Database**  
- **ViewModel / StateFlow / LiveData**  
- **Navigation Compose**  
- **Coroutines & Flows**


## 📷 Screenshots
*(To be added)*

## 📄 How It Works
BoxTrakr stores all data locally.  
When users create a category, they can then add boxes under it.  
Selecting a box opens a detailed view showing all stored items using a lazy list for performance.  
All operations update the Room database instantly.

## 🧩 What I Learned
- Building composable UIs with Jetpack Compose  
- Managing state and navigation in multi-screen apps  
- Designing scalable local persistence with Room  
- Applying MVVM architecture in a real Android project

## ✔️ Future Improvements
- Fingerprint/PIN protected private boxes  
- Image support for boxes/items 


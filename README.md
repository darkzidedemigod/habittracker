📱 Habitonix – Android Habit Tracker App

Habitonix is a modern Android habit tracker app designed to help users build consistency, track progress, and stay motivated every day. It features a clean UI, streak tracking, and a calendar-based history view.

✨ Features
✅ Daily Habit Checklist

Create and manage daily habits

Mark habits as completed with a simple tap

Visual feedback for completed tasks

Persistent state using local database

🔥 Streak Counter

Tracks consecutive days of habit completion

Automatically resets when a day is missed

Motivational streak indicators (e.g., flame icons)

📅 Calendar View

Monthly calendar showing habit history

Visual indicators for completed and missed habits

Tap on a date to view detailed activity

🛠 Habit Management

Add, edit, and delete habits

Enable or disable habits anytime

🔔 Notifications

Daily reminders to complete habits

📊 Progress Summary

Displays daily completion stats (e.g., “3 of 5 habits completed”)

🌙 Dark Mode Support

Optimized UI for both light and dark themes

🧱 Tech Stack

Language: Kotlin

UI: Jetpack Compose

Architecture: MVVM

Database: Room

Dependency Injection: Hilt

Minimum SDK: 26

📂 Project Structure
com.habitonix
│
├── data
│   ├── local (Room database, DAO, entities)
│   └── repository
│
├── di (Hilt modules)
│
├── ui
│   ├── screens (Today, Calendar, Habits)
│   ├── components
│   └── theme
│
├── viewmodel
│
└── MainActivity.kt
🗃 Data Models
Habit

id

title

description

icon

createdDate

isActive

HabitCompletion

id

habitId

date

completed

🎨 UI & Design

Material 3 Design System

Clean and minimal interface

Bottom navigation:

Today

Calendar

Habits

Recommended Color Theme

Primary: Soft Green

Secondary: Muted Blue

Accent: Warm Yellow

Background: Light Gray / Dark Mode supported

🚀 Getting Started
Prerequisites

Android Studio (latest version)

Kotlin support enabled

Installation

Clone the repository:

git clone https://github.com/your-username/habitflow.git

Open the project in Android Studio

Build and run the app on an emulator or device

📌 Future Improvements

Cloud sync (Firebase)

Habit categories and tags

Widgets for home screen

Detailed analytics and charts

Social sharing and accountability features

🤝 Contributing

Contributions are welcome! Feel free to:

Fork the repository

Create a new branch

Submit a pull request

📄 License

This project is licensed under the MIT License.

💡 Inspiration

HabitFlow is built to make habit tracking:

Simple

Motivating

Sustainable

Stay consistent. Build better habits. 🚀

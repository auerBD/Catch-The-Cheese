# 🧀 Catch The Cheese

A simple arcade-style game built with **LibGDX** where you control a mouse and catch falling cheese.  
Try to score as many points as possible before you lose all your lives!

---

## 🎮 Gameplay

Move the mouse left and right to catch falling cheese.

- 🧀 Catch cheese → gain points  
- ❌ Miss cheese → lose a life  
- 💀 Game over when lives reach 0  

---

## 🕹 Controls

- Left Arrow → Move left  
- Right Arrow → Move right  

---

## 🧠 Features

- Smooth 2D movement  
- Collision detection  
- Score system  
- Lives system  
- Game over screen  
- Sound effects (catch + game over 🎵)  
- macOS `.app` / `.dmg` export via `jpackage`  

---

## 🔊 Audio

- Catch sound plays when cheese is collected 🧀  
- Game over sound plays when lives reach zero 💀  
- Uses `.ogg` sound format  

---

## 🧱 Built With

- Java  
- LibGDX  
- Gradle  
- LWJGL3 backend  

---

## 🚀 Running the game (development)

Clone the repository:

git clone https://github.com/your-username/catch-the-cheese.git  
cd catch-the-cheese  

Run the game:

./gradlew lwjgl3:run  

---

## 📦 Build desktop version (macOS)

Build JAR:

./gradlew lwjgl3:jar  

Create macOS app:

jpackage \
  --name "Catch The Cheese" \
  --input lwjgl3/build/libs \
  --main-jar SimpleCatchGame-1.0.0.jar \
  --type dmg \
  --java-options "-XstartOnFirstThread"  

---

## 🧀 Project Structure

assets/        → images and sounds  
core/          → game logic  
lwjgl3/        → desktop launcher  
build.gradle   → build configuration  

---

## 🎯 Future improvements

- Increasing difficulty over time  
- Power-ups (slow motion, magnet mouse)  
- High score system  
- Animations for cheese collection  
- Start menu  
- Mobile version (Android)  

---

## 👨‍💻 Author

Made by Bence Auer
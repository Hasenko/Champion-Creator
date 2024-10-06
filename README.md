# Champion Creator

## Project Report

**Developed by :** EL BOUCHTILI Imaddine  
**Professor :** Mr. Riggio  
**School Year :** 2023-2024  
**Course :** Mobile Programming 1  
**Institution :** Haute École Libre de Bruxelles Ilya Prigogine

## Table of Contents
1. [Introduction](#introduction)
2. [Technologies Used](#technologies-used)
    - [Firebase](#firebase)
    - [Google API and Maps](#google-api-and-maps)
    - [Data Dragon](#data-dragon)
    - [Volley and Glide](#volley-and-glide)
    - [PaperDB](#paperdb)
    - [OpenCSV](#opencsv)
    - [Konfetti](#konfetti)
3. [Features](#features)
    - [Gameplay](#gameplay)
    - [Profile Page](#profile-page)
    - [Registration and Login](#registration-and-login)
    - [Screen Rotation](#screen-rotation)
4. [Limitations and Future Development](#limitations-and-future-development)
5. [Conclusion](#conclusion)
6. [Bibliography](#bibliography)
    - [Documents](#documents)
    - [Videos](#videos)

## Introduction

Champion Creator is a collaborative mobile game where players create a champion from the League of Legends universe using existing abilities.

The game supports 2 to 5 players, who select abilities from specific categories and suggest a champion's name. Players then vote on the proposed names.

This report details how the game was developed, covering the technologies used, features, analysis, and potential future improvements.

## Technologies Used

### Firebase
Firebase was used to store user accounts and game data. It uses JSON format, eliminating the need for SQL queries.

### Google API and Maps
Google API was used to send notifications, while Google Maps displayed a map with markers showing country scores.

### Data Dragon
Riot Games’ Data Dragon API was used to fetch champion details (e.g., names, abilities). This approach ensures up-to-date information compared to manual downloads.

### Volley and Glide
Volley was used for making HTTP requests, and Glide for loading images from URLs into the app.

### PaperDB
PaperDB was used to save login data locally, allowing automatic login on subsequent app launches.

### OpenCSV
OpenCSV was used to parse a CSV file containing country names and coordinates.

### Konfetti
Konfetti added celebratory confetti effects when a player won the game.

## Features

### Gameplay
The game allows users to create game rooms for 2 to 5 players, who select abilities from different categories (Passive, Q, W, E, Ultimate) to build a champion.

After naming the champion, players vote on the best name. Points are awarded to the winner, with a formula in place for ties.

### Profile Page
The profile page displays user information and a leaderboard sorted by score. A map shows the total score of players by country.

### Registration and Login
Users must register when they first open the app. Firebase stores user data on a Belgian server. Users can opt to stay logged in.

### Screen Rotation
The app supports screen rotation, with adjustments made in the XML files to ensure proper functionality.

## Limitations and Future Development

The app is currently not fully secure. Firebase allows unrestricted data access, and there's no system in place to handle abrupt disconnections.

In the future, a server could be used to manage notifications and synchronization more efficiently.

## Conclusion

Finally, I'd like to give you my thoughts on the development of this mobile application
and mobile development in general.

I really enjoyed developing this application. At the beginning, I was very apprehensive about my
knowledge of Java. I thought I was going to be in trouble, but I was wrong. Even though my application is certainly not the best coded and optimised, I'm proud of it.

Having played a lot of League of Legends, there's nothing more motivating than to make an application related to one of the most popular games in the world.

Despite some slackness when creation of the login and registration form, I always worked with a smile on my face.

Even if it's not the path I want to take, I don't mind doing another mobile project.
mobile project.

## Bibliography

### Documents
- **RIOT Games**, "Riot Developer Portal", [https://developer.riotgames.com/docs/lol](https://developer.riotgames.com/docs/lol), accessed on 21/02/2024.
- **GOOGLE**, "Volley Overview", [https://google.github.io/volley/](https://google.github.io/volley/), accessed on 26/02/2024.
- **GEEKS FOR GEEKS**, "Custom ArrayAdapter with ListView in Android", [https://www.geeksforgeeks.org/custom-arrayadapter-with-listview-in-android/](https://www.geeksforgeeks.org/custom-arrayadapter-with-listview-in-android/), accessed on 08/03/2024.
- **GOOGLE**, "Easily add sign-in to your Android app with FirebaseUI", [https://firebase.google.com/docs/auth/android/firebaseui](https://firebase.google.com/docs/auth/android/firebaseui), accessed on 20/03/2024.
- **GOOGLE**, "Connect your App to Firebase", [https://firebase.google.com/docs/database/android/start](https://firebase.google.com/docs/database/android/start), accessed on 24/03/2024.
- **CHATAR Veer Suthar**, "How to use ScrollView in Android", [https://stackoverflow.com/questions/6674341/how-to-use-scrollview-in-android](https://stackoverflow.com/questions/6674341/how-to-use-scrollview-in-android), accessed on 01/05/2024.
- **GEEKS FOR GEEKS**, "Reading a CSV file in Java using OpenCSV", [https://www.geeksforgeeks.org/reading-csv-file-java-using-opencsv/](https://www.geeksforgeeks.org/reading-csv-file-java-using-opencsv/), accessed on 08/05/2024.
- **MARTINUS Daniel**, "Konfetti", [https://github.com/DanielMartinus/Konfetti](https://github.com/DanielMartinus/Konfetti), accessed on 06/05/2024.

### Videos
- **IT-Point**, "HTTP Request – JSON", [https://www.youtube.com/watch?v=6BqoHsp9B4Q&ab_channel=IT-Point](https://www.youtube.com/watch?v=6BqoHsp9B4Q&ab_channel=IT-Point), accessed on 26/02/2024.
- **MUHAMMAD Ali’s Coding Café**, "Firebase Remember Me - Android Remember Me Checkbox - Android Studio Remember Login", [https://www.youtube.com/watch?v=MLZbDftEJKc&ab_channel=MuhammadAli%27sCodingCafe](https://www.youtube.com/watch?v=MLZbDftEJKc&ab_channel=MuhammadAli%27sCodingCafe), accessed on 31/03/2024.
- **ANDROID Mate**, "How to Create a Custom Dialog Box in Android Studio", [https://www.youtube.com/watch?v=WSOmYN8y0_k&t=2s&ab_channel=AndroidMate](https://www.youtube.com/watch?v=WSOmYN8y0_k&t=2s&ab_channel=AndroidMate), accessed on 12/04/2024.
- **EASY Tuto**, "19 FCM Push Notification | Chat Application | Android Studio", [https://www.youtube.com/watch?v=NF0RzhXDRKw&t=522s&ab_channel=EasyTuto](https://www.youtube.com/watch?v=NF0RzhXDRKw&t=522s&ab_channel=EasyTuto), accessed on 30/04/2024.
- **EASY Tuto**, "20 Notification Device to Device | Chat Application | Android Studio", [https://www.youtube.com/watch?v=YjNZO90yVsE&ab_channel=EasyTuto](https://www.youtube.com/watch?v=YjNZO90yVsE&ab_channel=EasyTuto), accessed on 30/04/2024.

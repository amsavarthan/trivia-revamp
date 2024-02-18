![Github Preview Image](https://github.com/amsavarthan/trivia-revamp/blob/main/art/Banner.png?raw=true)

# Trivia 🤓
A fun and addictive quiz game made using Jetpack Compose and adheres to the principles of Clean Architecture, with a Model-View-ViewModel (MVVM) design pattern.

<br />

***Get the latest app from Playstore 👇***

[![Trivia](https://img.shields.io/badge/Trivia-PLAYSTORE-black.svg?style=for-the-badge&logo=android)](https://play.google.com/store/apps/details?id=quiz.game.trivia)

<br />

## Screenshots 📸
Game Screen | Multiple Categories | Difficulty | Question Type
--- | --- | --- |---
![](https://github.com/amsavarthan/trivia-revamp/blob/main/art/S1.png)|![](https://github.com/amsavarthan/trivia-revamp/blob/main/art/S2.png)|![](https://github.com/amsavarthan/trivia-revamp/blob/main/art/S4.png)|![](https://github.com/amsavarthan/trivia-revamp/blob/main/art/S5.png)|

<br />


## Built With 🛠
- [OpenTriviaDB](https://opentdb.com/) - Free to use, user-contributed trivia question database.
- [Kotlin](https://kotlinlang.org/) - First class and official programming language for Android development.
- [Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html) - For asynchronous and more..
- [Android Architecture Components](https://developer.android.com/topic/libraries/architecture) - Collection of libraries that help you design robust, testable, and maintainable apps.
  - [Jetpack Compose](https://developer.android.com/jetpack/compose) - Jetpack Compose is Android’s recommended modern toolkit for building native UI.
  - [Stateflow](https://developer.android.com/kotlin/flow/stateflow-and-sharedflow) - StateFlow is a state-holder observable flow that emits the current and new state updates to its collectors. 
  - [Flow](https://kotlinlang.org/docs/reference/coroutines/flow.html) - A flow is an asynchronous version of a Sequence, a type of collection whose values are lazily produced.
  - [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) - Stores UI-related data that isn't destroyed on UI changes. 
  - [Room](https://developer.android.com/topic/libraries/architecture/room) - SQLite object mapping library.
  - [Jetpack Navigation](https://developer.android.com/guide/navigation) - Navigation refers to the interactions that allow users to navigate across, into, and back out from the different pieces of content within your app
  - [DataStore](https://developer.android.com/topic/libraries/architecture/datastore) - Jetpack DataStore is a data storage solution that allows you to store key-value pairs or typed objects with protocol buffers. DataStore uses Kotlin coroutines and Flow to store data asynchronously, consistently, and transactionally.
- [Material Components for Android](https://github.com/material-components/material-components-android) - Modular and customizable Material Design UI components for Android.

<br />

## Package Structure 📦
    
    
    quiz.game.trivia         # Root Package
    ├── di                         # Hilt DI Modules
    ├── data         
    │   ├── local             
    │   ├── network             
    │   ├── receivers             
    │   ├── repository             # Implementation classes
    │   └── utils                  
    ├── domain                     
    │   ├── models                 # Model classes
    │   ├── repository             # Interfaces               
    └── presentation      
        ├── ads
        ├── anim
        ├── composables            # Reuseable composables  
        ├── navigation 
        ├── screens                # Each screen have own directory
        ├── theme   
        └── utils


<br />

## 🧰 Build-tool

- [Android Studio Dolphin 2021.3.1 or above](https://developer.android.com/studio)

<br />

## 📩 Contact

DM me at 👇

* Twitter: <a href="https://twitter.com/lvamsavarthan" target="_blank">@lvamsavarthan</a>
* Email: amsavarthan.a@gmail.com

<br />

## License 🔖
```
MIT License

Copyright (c) 2023 Amsavarthan Lv

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

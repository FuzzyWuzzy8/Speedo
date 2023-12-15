# Speedo

## Overview
Speedo is a simple Android application that tracks and displays real-time speed, maximum speed, distance traveled, and satellite information based on GPS data.

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Technologies](#technologies)
- [Screenshots](#screenshots)
- [Installation](#installation)
- [Usage](#usage)
- [Permissions](#permissions)
- [Contributing](#contributing)

## Features
**Location Tracking:**

The app uses Android's LocationManager and LocationListener to receive updates on the device's location.
- **Speed Calculation:**
   - The app calculates the current speed using the obtained location updates.

- **Distance Measurement:**
   - The app measures the distance traveled by the user, updating the distance value based on location changes.

- **Maximum Speed Tracking:**
   - The app keeps track of the maximum speed reached during the tracking session.

- **Satellite Information:**
   - The app has the capability to display the number of satellites used for obtaining GPS data.

## Technologies
- **Development Environment:** Android Studio
- **Programming Language:** Java
- **Android Location Services:** Utilizes Android's LocationManager and LocationListener for GPS-based location tracking.
- **XML:** Used for defining the layout and appearance of UI elements in Android.
- **Version Control:** Git (GitHub)

## Screenshots
<!-- ![Screenshot 1](https://github.com/FuzzyWuzzy8/Speedo/blob/master/screenshots/qr_1.png) -->

<!-- Add more screenshots later -->

## Installation
1. Clone the repository.
   ```bash
   git clone https://github.com/your-username/your-repository.git
   
1. Open the project in Android Studio.
2. Build and run the application on an emulator or Android device.

**To install app on your Android device, follow these steps:**

1. Download the APK file from the [Releases](release-url) page.
2. Enable installation from unknown sources in your device settings.
3. Install the APK on your Android device.
4. Open the Upset Birds app and start playing!


## Usage
1. Launch the application on your device.
2. Follow on-screen instructions for each feature.
3. Explore multilingual support by changing the language in the settings.

## Permissions
- **ACCESS_FINE_LOCATION:** Allows the app to access precise location information from the GPS.
- **ACCESS_COARSE_LOCATION:** Allows the app to access approximate location information from network sources.

## Contributing
Feel free to contribute to the project. If you encounter any issues or have suggestions, please open an issue or create a pull request.

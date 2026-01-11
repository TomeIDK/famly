# Famly

Famly is a mobile application that helps users manage families, tasks, and recipes collaboratively. Users can create families, join existing ones via a code, and organize meal plans, shopping lists, and tasks.

## Features

- Google sign-in (authentication)
- Create or join a family with a unique join code
- View and select family members
- Manage meal plans and shopping lists
- Task list creation with reset intervals
- Synchronization of family data across users
- Responsive UI built with Jetpack Compose

## Known Issues

- **Google Sign-In on Real Devices**:  
  The APK built and installed on physical devices may not properly sign in with Google accounts. Sign-in works correctly in the emulator. This is likely related to SHA-1/256 key mismatches in Firebase configuration for the debug/release APK.  

  If using the app on a real device, users may need to manually configure OAuth credentials in Firebase or test via the emulator.

## Running the App

1. Open the project in **Android Studio**.
2. Connect an emulator or physical device.
3. Build and run using **Run > Run 'app'**
4. Use Google sign-in to authenticate.
5. Create or join a family and explore the app features.

## APK

A debug APK is included in the repository (if provided) but Google sign-in may not work on real devices due to SHA certificate mismatch.

## Notes for Reviewers

- All core functionality works in the emulator.
- Known issue: Google sign-in may fail on real devices due to Firebase OAuth SHA keys.
- Other app features (family creation, joining, meal planning, tasks) are fully functional.

# KAEL Music — Fresh Android Project

**App name:** KAEL Music  
**Developer & Designer:** Vivinand  
**Tagline:** Feel Every Beat

This is a fresh Android Studio project created for the KAEL Music brand. It does not reuse the previous app's name, developer credits, logo, or branding.

## Included in this starter
- Kotlin + Jetpack Compose Android app
- Dark navy interface with violet/pink accents
- KAEL Music branding and original vector logo
- Import audio files from your phone using Android's document picker
- Play/pause, previous/next track, seek bar, and queue display
- GitHub Actions workflow to build a debug APK and upload it as an artifact
- Developer/designer credits: Vivinand

## Important scope notes
This version is a working **local music player starter**. It does not yet include a cloud-storage backend, user accounts, or a licensed streaming catalogue. Those features require an appropriate backend/API and music sources that explicitly permit streaming. Do not scrape or re-upload copyrighted songs without permission.

## Build an APK on GitHub using your phone
1. Create a new, empty GitHub repository, for example `KAEL-Music`.
2. Extract this ZIP on your device if possible, then upload the *contents* of this folder to the repository. Do not upload this ZIP inside the repository as another ZIP.
3. Open the repository's **Actions** tab.
4. Choose **Build KAEL Music APK** and tap **Run workflow** (or push a commit to `main`).
5. When the workflow finishes successfully, open the run and download **KAEL-Music-APK** under Artifacts.
6. Extract the downloaded artifact ZIP and install the `.apk` on your Android phone. Android may ask you to allow installs from the browser/file manager you used.

## Build locally
Open this folder in Android Studio, allow Gradle sync, then run the `app` configuration or execute `gradle assembleDebug` from the project root after Gradle is installed.

## Branding
The source logo is `branding/kael-music-logo.svg`. A preview board is in `branding/kael-music-brand-board.png`.

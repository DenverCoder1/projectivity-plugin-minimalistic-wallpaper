# Minimalistic Wallpaper for Projectivy

A Projectivy Launcher wallpaper provider using the [Minimalistic Wallpaper Collection](https://minimalistic-wallpaper.demolab.com/).
It resolves one random image URL per request and uses its filename as the wallpaper title. It does not download or store a collection of images.

<img width="776" height="293" alt="image" src="https://github.com/user-attachments/assets/42a808b9-f871-4180-85fd-62f11539efa9" />
 
# Installation & Usage
- The plugin can be installed from the [Releases](https://github.com/DenverCoder1/projectivity-plugin-minimalistic-wallpaper/releases) page. Click on the `minimalistic-wallpaper.apk` asset in the latest release to download, then install the app.
- Select **Minimalistic Wallpaper** as the wallpaper provider in Projectivy Launcher.
- Set the wallpaper change interval in Projectivy's wallpaper settings. The plugin's cache lifetime is 15 minutes; Projectivy requests a new image on the first wallpaper refresh after that lifetime expires. Shorter launcher intervals may therefore show the same image until the cache expires.
- The plugin itself has no interval setting: Projectivy controls the timer and reads the cache lifetime from the plugin manifest.

# Development
- `/minimalistic` contains the plugin service and settings activity.
- `/api` contains the AIDL communication API and should not be changed.
- Build with `bash ./gradlew assembleDebug`.

## Releases
- Create an Android signing keystore and configure the repository secrets `ANDROID_KEYSTORE_BASE64` (the base64-encoded keystore), `ANDROID_KEYSTORE_PASSWORD`, `ANDROID_KEY_ALIAS`, and `ANDROID_KEY_PASSWORD`. Keep the keystore and passwords safe: future releases must use the same signing key to support in-place upgrades.
- Run **Build and release APK** from the Actions tab on the desired branch, providing a new release tag. Before building, the workflow increments `versionCode`, sets `versionName` to the tag without a leading `v`, and commits and pushes the change to that branch. It then builds and signs the release APK and publishes a GitHub release at that commit.
- The version commit is pushed before the build, so a failed build or release still advances `versionCode`. Rerunning the workflow increments it again.

# Minimalistic Wallpaper for Projectivity

A Projectivy Launcher wallpaper provider using the [Minimalistic Wallpaper Collection API](https://github.com/DenverCoder1/Minimalistic-Wallpaper-Collection) to fetch random wallpapers.

[![download-apk](https://custom-icon-badges.demolab.com/github/downloads/DenverCoder1/projectivity-plugin-minimalistic-wallpaper/total?label=Download%20APK&logo=download-cloud&logoSource=feather&style=for-the-badge&color=green)](https://github.com/DenverCoder1/projectivity-plugin-minimalistic-wallpaper/releases/latest/download/minimalistic-wallpaper.apk)
[![images](https://custom-icon-badges.demolab.com/github/directory-file-count/DenverCoder1/Minimalistic-Wallpaper-Collection/images?label=Images%20in%20Collection&logo=image&style=for-the-badge)](https://github.com/DenverCoder1/Minimalistic-Wallpaper-Collection/tree/main/images)


<img width="800" alt="screenshot" src="https://github.com/user-attachments/assets/f7c218c8-ee80-4c92-ad50-f8103bdd0f2f" />

# Installation & Usage

- The plugin can be installed with the AFTV Downloader Code **6726681** or from the [**Releases page**](https://github.com/DenverCoder1/projectivity-plugin-minimalistic-wallpaper/releases). Click on the [`minimalistic-wallpaper.apk`](https://github.com/DenverCoder1/projectivity-plugin-minimalistic-wallpaper/releases/latest/download/minimalistic-wallpaper.apk) asset in the latest release to download, then install the app.
- Select **Minimalistic Wallpaper** as the wallpaper provider in Projectivy Launcher.
- Set the wallpaper change interval in Projectivy's wallpaper settings (e.g., 15 minutes)
- The **Next** button cycles through the set of 6 random wallpapers returned by the provider. The plugin caches the set for one hour, after which Projectivy requests a new set on the next wallpaper refresh.

# Development

- `/minimalistic` contains the plugin service and settings activity.
- `/api` contains the AIDL communication API and should not be changed.
- Build with `bash ./gradlew assembleDebug`.

## Releases

- Create an Android signing keystore and configure the repository secrets `ANDROID_KEYSTORE_BASE64` (the base64-encoded keystore), `ANDROID_KEYSTORE_PASSWORD`, `ANDROID_KEY_ALIAS`, and `ANDROID_KEY_PASSWORD`.
- Run **Build and release APK** from the Actions tab on the desired branch, providing a new release tag. Before building, the workflow increments `versionCode`, sets `versionName` to the tag without a leading `v`, and commits and pushes the change to that branch. It then builds and signs the release APK and publishes a GitHub release at that commit.
- The version commit is pushed before the build, so a failed build or release still advances `versionCode`. Rerunning the workflow increments it again.

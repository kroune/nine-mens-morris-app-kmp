<div align="center">
<p>
    <img width="200" src="https://raw.githubusercontent.com/kroune/nine-mens-morris-app-kmp/refs/heads/v1.0.1/composeApp/icons/icon.svg" alt="icon">
</p>

[play online](https://play.nine-men-s-morris.me/)
</div>

## This is a Kotlin Multiplatform project targeting Android, Web, Linux, MacOS, Windows of a game called "Nine men's morris"
<br>

## README navigation
* **[app showcase](#app-showcase)**
* **[artifacts size plot](#artifacts-size-change)**
* **[preview](#preview)**
* **[how to run the app](#run-app)**
* **[project history](#history)**
* **[how to contribute](#setting-up-a-workspace)**
* **[license](#license)**

## Preview
* screenshots - [android](https://github.com/kroune/nine-mens-morris-app-kmp/tree/v1.0.1/demo/screenshots/android)
<br>

Other previews will be added in the future

## History

This first version of the app (which was for android only) is located
at https://github.com/kroune/nine-mens-morris-app

## Run app
There are several options to use it
1. Use **jar** format. It works on all desktop platforms, usually more performant and weights less compared to native distributions ([link](https://github.com/kroune/nine-mens-morris-app-kmp/releases))
2. Use **apk** format for Android. It is really tiny (around 2 mb) and works well ([link](https://github.com/kroune/nine-mens-morris-app-kmp/releases))
3. Use **native** distributions (available for Windows, Linux, Mac OS). It weights more that jar file ([link](https://github.com/kroune/nine-mens-morris-app-kmp/releases))
4. Play on the **web**. You don't have to download/install anything, but there are a few disadvantages like a worse performance compared to other installations and state not being saved in the url (due to github pages limitations)) ([link](https://kroune.github.com/))

## License
This project is subject to the [GNU General Public License v3.0](https://www.gnu.org/licenses/gpl-3.0.en.html). This
does only apply for source code located directly in this clean repository. During the development and compilation
process, additional source code may be used to which we have obtained no rights. Such code is not covered by the GPL
license.

For those who are unfamiliar with the license, here is a summary of its main points. This is by no means legal advice
nor legally binding.

*Actions that you are allowed to do:*

- Use
- Share
- Modify

*If you do decide to use ANY code from the source:*

- **You must disclose the source code of your modified work and the source code you took from this project. This means
  you are not allowed to use code from this project (even partially) in a closed-source (or even obfuscated)
  application.**
- **Your modified application must also be licensed under the GPL**


## Contributing

We appreciate contributions and testing. So if you want to support us, feel free to make changes to our source code and
submit a [pull request](https://github.com/kroune/nine-mens-morris-app-kmp/pullsv) or report a bug in [github issues](https://github.com/kroune/nine-mens-morris-app-kmp/issues).

We would **heavily** appreciate adding support for the ios.


## Setting up a Workspace

Our project is multiplatform, so make sure to use [android studio](https://developer.android.com/studio) or [fleet](https://www.jetbrains.com/fleet/)

1. Clone the repository using `git clone https://github.com/kroune/nine-mens-morris-app-kmp`.
2. CD into the local repository.
3. Run `./gradlew build`.
4. Open the folder as a Gradle project in the IDE.

## App showcase

Warning: some screenshots might look blurry (due to scaling), but they look perfectly fine on an actual machine
![collage](https://raw.githubusercontent.com/kroune/nine-mens-morris-app-kmp/refs/heads/v1.0.1/demo/screenshots/collage.png)
![app_start_animation_screen](https://raw.githubusercontent.com/kroune/nine-mens-morris-app-kmp/refs/heads/v1.0.1/demo/screenshots/android/app_start_animation_screen.png)
![welcome_screen](https://raw.githubusercontent.com/kroune/nine-mens-morris-app-kmp/refs/heads/v1.0.1/demo/screenshots/android/welcome_screen.png)
![game_with_friend_screen](https://raw.githubusercontent.com/kroune/nine-mens-morris-app-kmp/refs/heads/v1.0.1/demo/screenshots/android/game_with_friend_screen.png)
![game_with_bot_screen](https://raw.githubusercontent.com/kroune/nine-mens-morris-app-kmp/refs/heads/v1.0.1/demo/screenshots/android/game_with_bot_screen.png)
![view_account_screen](https://raw.githubusercontent.com/kroune/nine-mens-morris-app-kmp/refs/heads/v1.0.1/demo/screenshots/android/view_account_screen.png)
![sign_in_screen](https://raw.githubusercontent.com/kroune/nine-mens-morris-app-kmp/refs/heads/v1.0.1/demo/screenshots/android/sign_in_screen.png)
![sign_up_screen](https://raw.githubusercontent.com/kroune/nine-mens-morris-app-kmp/refs/heads/v1.0.1/demo/screenshots/android/sign_up_screen.png)
![tutorial_screen](https://raw.githubusercontent.com/kroune/nine-mens-morris-app-kmp/refs/heads/v1.0.1/demo/screenshots/android/tutorial_screen.png)
![searching_for_game_screen](https://raw.githubusercontent.com/kroune/nine-mens-morris-app-kmp/refs/heads/v1.0.1/demo/screenshots/android/searching_for_game_screen.png)
![searching_for_game_2_screen](https://raw.githubusercontent.com/kroune/nine-mens-morris-app-kmp/refs/heads/v1.0.1/demo/screenshots/android/searching_for_game_2_screen.png)
![online_game_screen](https://raw.githubusercontent.com/kroune/nine-mens-morris-app-kmp/refs/heads/v1.0.1/demo/screenshots/android/online_game_screen.png)
![online_game_ended_screen](https://raw.githubusercontent.com/kroune/nine-mens-morris-app-kmp/refs/heads/v1.0.1/demo/screenshots/android/online_game_ended_screen.png)

## Artifacts size change
![Web](https://raw.githubusercontent.com/kroune/kroune-img-hosting/refs/heads/main/plot-artifacts/NineMensMorris-Web-artifact_sizes.png)
<br>
<br>
<br>
![Android](https://raw.githubusercontent.com/kroune/kroune-img-hosting/refs/heads/main/plot-artifacts/NineMensMorris-Android-artifact_sizes.png)
<br>
<br>
<br>
![Jar](https://raw.githubusercontent.com/kroune/kroune-img-hosting/refs/heads/main/plot-artifacts/NineMensMorris-Jar-artifact_sizes.png)
<br>
<br>
<br>
![Deb](https://raw.githubusercontent.com/kroune/kroune-img-hosting/refs/heads/main/plot-artifacts/NineMensMorris-LinuxDeb-artifact_sizes.png)
<br>
<br>
<br>
![Rpm](https://raw.githubusercontent.com/kroune/kroune-img-hosting/refs/heads/main/plot-artifacts/NineMensMorris-LinuxRpm-artifact_sizes.png)
<br>
<br>
<br>
![Dmg](https://raw.githubusercontent.com/kroune/kroune-img-hosting/refs/heads/main/plot-artifacts/NineMensMorris-MacosDmg-artifact_sizes.png)
<br>
<br>
<br>
![Windows](https://raw.githubusercontent.com/kroune/kroune-img-hosting/refs/heads/main/plot-artifacts/NineMensMorris-WindowsExe-artifact_sizes.png)

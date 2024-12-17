<div align="center">
<p>
    <img width="200" src="composeApp/icons/icon.svg" alt="icon">
</p>

[play online](https://kroune.github.io)
</div>

## This is a Kotlin Multiplatform project targeting Android, Web, Linux, MacOS, Windows of a game called "Nine men's morris"
<br>

## Preview
* [android](demo/android.mkv)
<br>

Other previews will be added in the future

## History

This first version of the app (which was for android only) is located
at https://github.com/kroune/nine-mens-morris-app


## Used projects

This app uses my own projects such as [backend](https://github.com/kroune/nine-mens-morris-server)
written in kotlin, kmp [library](https://github.com/kroune/nine-mens-morris-lib-kmp) and ktor,
coroutines, decompose, filekit, kotlin serialization, multiplatform settings and other.

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
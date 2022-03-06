<p align="center">
      <a href="https://scott-hamilton.mit-license.org/"><img alt="MIT License" src="https://img.shields.io/badge/License-MIT-525252.svg?labelColor=292929&logo=creative%20commons&style=for-the-badge" /></a>
</p>
<h1 align="center">Trollslate - Android App to troll your friends with barcode looking messages</h1>

## Description
Transform your text into barcode looking text.
Have fun asking your friends to figure out what's written.
Don't write too much slurs, it's not making it any funnier.
Use the gyroscopic feature to select the roll angle if available.
Dark theme is enforced but the barcode-text-page is in light theme for contrast purposes.
Please consider increasing the screen's brightness if you don't see anything.
Some screens work better than others but it should work fine on yours regardless.

[<img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png"
     alt="Get it on F-Droid"
     height="80">](https://f-droid.org/packages/org.scotthamilton.trollslate/)

## Screens
![Demo](https://user-images.githubusercontent.com/24496705/155399458-58716f80-02a0-431c-9125-8aca085e0d35.gif)

## Dependencies
 - Jetpack Compose

## Building
This project is configured with Android Studio, it builds with gradle

## Translations
 - French 100%
 - English 100%
 - German 100%

The app is very little, it shouldn't take you more than 10 minutes
to add translations to your native language. There are two ways to do so.
Once you're done with your translations, fork the project and open a Pull Request.

### With Android Studio (easiest)
 - Open the string res file (`app/src/main/res/values/strings.xml`)
 - Click on open `Open editor`
 - Click on the Earth Icon (`Add Locale` button)
 - Select a language
 - Fill in the translations
 - Some texts may be too long to be written in the translations editor. 
 You may prefer to directly write your translations in the string res
 xml file located in `app/src/main/res/values/strings-XX.xml` where XX
 is the lowercase code of the language you're currently translating to,
 but be careful because some characters need to be escaped
 (the backslash should normally do the work `\`).

### By Hand (harder)
 - Check the code qualifier of the language you are willing to translate to
 (cf [android doc](https://developer.android.com/guide/topics/resources/providing-resources#AlternativeResources)
 Table 2., Language and region)
 - Create folder `app/src/main/res/values-XX` where XX is the code qualifier
 you looked for, for example: `values-de`, `values-fr-rFR`.
 - Copy the file `app/src/main/res/values/strings.xml` to the just created directory.
 - Edit this file and replace all the english text between xml string-tags with its translation.
 Be careful because some characters need to be escaped
 (the backslash should normally do the work `\`)


## License
Trollslate is delivered as it is under the well known MIT License.

## Credits
 - [Gyroscope icon from Freepik](https://www.flaticon.com/free-icons/rotate) at FlatIcon

**References that helped**
 - [sample rotation sensor app by kplatfoot] : <https://github.com/kplatfoot/android-rotation-sensor-sample>
 - [pchmielowski's blog post on automated compose-espresso screenshots] : <https://dev.to/pchmielowski/automate-taking-screenshots-of-android-app-with-jetpack-compose-2950>
 - [implementation of Intent Pdf Sharing by IanDarwin] : <https://github.com/IanDarwin/Android-Cookbook-Examples/blob/master/PdfShare/src/main/java/com/example/pdfshare/MainActivity.java>
 - [android documentation] : <https://developer.android.com/>

[//]: # (These are reference links used in the body of this note and get stripped out when the markdown processor does its job. There is no need to format nicely because it shouldn't be seen. Thanks SO - http://stackoverflow.com/questions/4823468/store-comments-in-markdown-syntax)

   [sample rotation sensor app by kplatfoot]: <https://github.com/kplatfoot/android-rotation-sensor-sample>
   [pchmielowski's blog post on automated compose-espresso screenshots]: <https://dev.to/pchmielowski/automate-taking-screenshots-of-android-app-with-jetpack-compose-2950>
   [implementation of Intent Pdf Sharing by IanDarwin]: <https://github.com/IanDarwin/Android-Cookbook-Examples/blob/master/PdfShare/src/main/java/com/example/pdfshare/MainActivity.java>
   [android documentation]: <https://developer.android.com/>

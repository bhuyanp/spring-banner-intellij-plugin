## Funky Banners (An IntelliJ Plugin for Spring Boot Applications)

[![JetBrains Plugins](https://img.shields.io/jetbrains/plugin/v/29672-funky-banners?&color=blue&label=Latest%20Release)](https://plugins.jetbrains.com/plugin/29672-funky-banners)
[![JetBrains Plugins](https://img.shields.io/jetbrains/plugin/d/29672-funky-banners?color=blue&label=Downloads)](https://plugins.jetbrains.com/plugin/29672-funky-banners)
[![GitHub Issues](https://img.shields.io/github/issues/bhuyanp/spring-banner-intellij-plugin?logo=github&color=orange&label=Issues)](https://github.com/bhuyanp/spring-banner-intellij-plugin/issues)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)


Enhance your Spring Boot development experience with visually appealing startup banners.
Choose from predefined themes or create your own custom theme to make your Spring Boot application stand out.


### Previews

<details>
<summary>Dark Theme Preview:</summary>
<img src="./media/screenshots/dark1.png" alt="Dark Theme Banner 1" title="Dark Theme Banner 1" width="400"/>
<img src="./media/screenshots/dark2.png" alt="Dark Theme Banner 2" title="Dark Theme Banner 2" width="400"/>
<img src="./media/screenshots/dark3.png" alt="Dark Theme Banner 3" title="Dark Theme Banner 3" width="400"/>
<img src="./media/screenshots/dark4.png" alt="Dark Theme Banner 4" title="Dark Theme Banner 4" width="400"/>
</details>
<details>
<summary>Light Theme Preview:</summary>
<img src="./media/screenshots/light1.png" alt="Light Theme Banner 1" title="Light Theme Banner 1" width="400"/>
<img src="./media/screenshots/light2.png" alt="Light Theme Banner 2" title="Light Theme Banner 2" width="400"/>
<img src="./media/screenshots/light3.png" alt="Light Theme Banner 3" title="Light Theme Banner 3" width="400"/>
<img src="./media/screenshots/light4.png" alt="Light Theme Banner 4" title="Light Theme Banner 4" width="400"/>
</details>

<details>
<summary>Surprise Me:</summary>
<img src="./media/screenshots/surprise1.png" alt="Surprise Me Theme Banner 1" title="Surprise Me Theme Banner 1" width="400"/>
<img src="./media/screenshots/surprise2.png" alt="Surprise Me Theme Banner 2" title="Surprise Me Theme Banner 2" width="400"/>
<img src="./media/screenshots/surprise3.png" alt="Surprise Me Theme Banner 3" title="Surprise Me Theme Banner 3" width="400"/>
<img src="./media/screenshots/surprise4.png" alt="Surprise Me Theme Banner 4" title="Surprise Me Theme Banner 4" width="400"/>
<img src="./media/screenshots/surprise5.png" alt="Surprise Me Theme Banner 5" title="Surprise Me Theme Banner 5" width="400"/>
<img src="./media/screenshots/surprise6.png" alt="Surprise Me Theme Banner 6" title="Surprise Me Theme Banner 6" width="400"/>
<img src="./media/screenshots/surprise7.png" alt="Surprise Me Theme Banner 7" title="Surprise Me Theme Banner 7" width="400"/>
<img src="./media/screenshots/surprise8.png" alt="Surprise Me Theme Banner 8" title="Surprise Me Theme Banner 8" width="400"/>
<img src="./media/screenshots/surprise9.png" alt="Surprise Me Theme Banner 9" title="Surprise Me Theme Banner 9" width="400"/>
<img src="./media/screenshots/surprise10.png" alt="Surprise Me Theme Banner 10" title="Surprise Me Theme Banner 10" width="400"/>
<img src="./media/screenshots/surprise11.png" alt="Surprise Me Theme Banner 11" title="Surprise Me Theme Banner 10" width="400"/>
<img src="./media/screenshots/surprise12.png" alt="Surprise Me Theme Banner 12" title="Surprise Me Theme Banner 10" width="400"/>
</details>

### Features

- Seamless integration with existing Spring Boot projects<br/>
- Choose from light or dark theme to match your IDE's global theme<br/>
- Let the plugin surprise you with randomized theme "Surprise Me"<br/>
- Customize the banner to your heart's content using Custom theme<br/>
- Ability to override global theme settings at individual project level<br/>
- Caption underneath banner lists SpringBoot and JDK versions<br/>
![customization.png](media/customization.png)


### Usage Notes

- Running the application from terminal will NOT reflect the banner changes as the plugin depends on IDE's internal build system
- Project root must have a mavel/gradle build file.<br/>
- If you are using gradle, make sure to enable Build and run using IntelliJ under 'Settings->Build->Build Tools->Gradle'<br/>
<img src="media/gradle.png" alt="Gradle" style="width:600px;" />
- If you choose to Build and run using Gradle instead of IntelliJ, then consider using the [Spring Banner Gradle Plugin](https://github.com/bhuyanp/spring-banner-gradle-plugin) to generate custom banners
- Recommended Console Font Line Height: 1.0 for better appearance<br/>
<img src="media/console-font.png" alt="Console Font" style="width:600px;" />

### Credits

- [Figlet](http://www.figlet.org)
- [JFiglet](https://github.com/dtmo/jfiglet)
- Colorizer by [JColor](https://github.com/dialex/jcolor)

### Love Funky Banners?

Please consider supporting our efforts.

<a href="https://buymeacoffee.com/bhuyanp"><img src="media/qr-code.png" alt="Buy Me A Coffee" width="120px"></a>

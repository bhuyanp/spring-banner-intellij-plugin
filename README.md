## Funky Banners (An IntelliJ Plugin for Spring Boot Applications)

[![JetBrains Plugins](https://img.shields.io/jetbrains/plugin/v/29672-funky-banners?&color=blue&label=Latest%20Release)](https://plugins.jetbrains.com/plugin/29672-funky-banners)
[![JetBrains Plugins](https://img.shields.io/jetbrains/plugin/d/29672-funky-banners?color=blue&label=Downloads)](https://plugins.jetbrains.com/plugin/29672-funky-banners)
[![GitHub Issues](https://img.shields.io/github/issues/bhuyanp/spring-banner-intellij-plugin?logo=github&color=orange&label=Issues)](https://github.com/bhuyanp/spring-banner-intellij-plugin/issues)  


Enhance your Spring Boot development experience with visually appealing startup banners.
Choose from predefined themes or create your own custom theme to make your Spring Boot application stand out.


### Previews
![preview1.png](media/preview1.png)
![preview2.png](media/preview2.png)
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
- If you are using gradle, make sure to enable Build and run using IntelliJ under 'Settings->Build->Build Tools->Gradle'
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

# Radz Webview
[![](https://jitpack.io/v/Radzdevteam/radzweb.svg)](https://jitpack.io/#Radzdevteam/radzweb)

## Features:
1. URL Loading Functionality
2. Swipe-to-Refresh
3. Custom WebView Settings
4. Ad Blocker
5. SSL Error Handling
6. Custom Full-Screen Video Support
7. Network Connectivity Check
8. Persistent WebView State
9. Loading Animation
10. Error Handling for Network and SSL Issues
11. Custom WebViewClient Implementation
12. Resource Fetching

## How to Include
### Step 1. Add the repository to your project settings.gradle:
```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
   ```

### Step 2. Add the dependency
```groovy
dependencies {
    implementation ("com.github.Radzdevteam:radzweb:1.0")
}

   ```

## Usage

In your `MainActivity`, add the following code:
```groovy
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(this, radzweb::class.java)
        intent.putExtra("url", "https://google.com/")
        startActivity(intent)
        finish()
    }
}

   ```


## Manifest
In your `AndroidManifest`, add the following code:

```groovy

<activity android:name="com.radzdev.radzweb.radzweb"
android:configChanges="keyboard|keyboardHidden|orientation|screenSize|screenLayout|smallestScreenSize|uiMode"/>

   ```

# Radz Webview
[![](https://jitpack.io/v/Radzdevteam/RadzWebview.svg)](https://jitpack.io/#Radzdevteam/RadzWebview)

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
    implementation ("com.github.Radzdevteam:RadzWebview:Tag")
}

   ```

## Usage

In your `MainActivity`, add the following code:
```groovy
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Start the radzweb activity and pass the URL
        val intent = Intent(this, radzweb::class.java)
        intent.putExtra("url", "https://strm.great-site.net/")
        startActivity(intent)

        finish()
    }
}

   ```



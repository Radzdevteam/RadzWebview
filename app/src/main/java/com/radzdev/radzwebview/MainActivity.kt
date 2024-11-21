package com.radzdev.radzwebview

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.radzdev.radzweblibrary.radzweb

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

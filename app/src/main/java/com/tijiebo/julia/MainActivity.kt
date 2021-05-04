package com.tijiebo.julia

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tijiebo.julia.ui.gallery.FullViewDialogFragment
import com.tijiebo.julia.ui.gallery.GalleryFragment
import com.tijiebo.julia.ui.main.MainFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance(), MainFragment.TAG)
                .commitNow()
        }
    }

    fun showGallery() {
        supportFragmentManager.beginTransaction().apply {
            supportFragmentManager.findFragmentByTag(MainFragment.TAG)?.let { this.hide(it) }
            add(R.id.container, GalleryFragment.newInstance(), GalleryFragment.TAG)
            addToBackStack(null)
            commit()
        }
    }

    fun showFullView(uri: Uri) {
        FullViewDialogFragment.newInstance(uri).show(
            supportFragmentManager, FullViewDialogFragment.TAG
        )
    }

    override fun onBackPressed() {
        if (!supportFragmentManager.popBackStackImmediate())
            super.onBackPressed()
    }
}
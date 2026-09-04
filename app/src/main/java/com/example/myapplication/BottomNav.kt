package com.example.myapplication

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class BottomNav {

    interface NavigationListener {
        fun onHomeClick()
        fun onProfileClick()
        fun onFavouritesClick()
        fun onRecentlyViewed()
    }

    fun create(
        activity: AppCompatActivity,
        listener: NavigationListener
    ): View {

        val navigation = LinearLayout(activity)

        navigation.orientation = LinearLayout.HORIZONTAL
        navigation.gravity = Gravity.CENTER

        navigation.setPadding(
            10,
            10,
            10,
            10
        )

        navigation.background = GradientDrawable().apply {
            setColor(Color.WHITE)
            cornerRadius = 30f
        }

        // HOME
        val home = createNavItem(
            activity,
            R.drawable.house,
            "Home"
        )

        // PROFILE
        val profile = createNavItem(
            activity,
            R.drawable.profile_icon_bt,
            "Profile"
        )

        // SETTINGS
        val favourites = createNavItem(
            activity,
            R.drawable.heart_liked,
            "Favourites"
        )
        val recentlyViewedPage = createNavItem(
            activity,
            R.drawable.wall_clock,
            "RecentlyViewed"
        )


        navigation.addView(home)
        navigation.addView(profile)
        navigation.addView(favourites)
        navigation.addView(recentlyViewedPage)

        // Click listeners
        home.setOnClickListener {
            listener.onHomeClick()
        }

        profile.setOnClickListener {
            listener.onProfileClick()
        }

        favourites.setOnClickListener {
            listener.onFavouritesClick()
        }
        recentlyViewedPage.setOnClickListener {
            listener.onRecentlyViewed()
        }

        return navigation
    }

    private fun createNavItem(
        activity: AppCompatActivity,
        iconRes: Int,
        title: String
    ): LinearLayout {

        val item = LinearLayout(activity)

        item.orientation = LinearLayout.VERTICAL
        item.gravity = Gravity.CENTER

        item.setPadding(
            10,
            8,
            10,
            8
        )

        // Icon
        val icon = ImageView(activity)

        icon.setImageResource(iconRes)

        icon.layoutParams =
            LinearLayout.LayoutParams(
                30,
                30
            )


        // Text
        val text = TextView(activity)

        text.text = title
        text.textSize = 12f
        text.setTextColor(Color.DKGRAY)
        text.gravity = Gravity.CENTER

        item.addView(icon)
        item.addView(text)

        // Equal width
        val params =
            LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        params.weight = 1f

        item.layoutParams = params

        return item
    }
}
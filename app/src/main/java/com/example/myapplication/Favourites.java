package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.models.Audio;

import java.util.ArrayList;
import java.util.Map;

public class Favourites extends AppCompatActivity {

    private RecyclerView recyclerfavouriteView;

    private AudioAdapter adapter;

    private ArrayList<Audio> favoriteList;

    private SharedPreferences preferences;
    private TextView favouriteText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_favourites
        );

        preferences =
                getSharedPreferences(
                        "Favorites",
                        MODE_PRIVATE
                );

        recyclerfavouriteView =
                findViewById(
                        R.id.recyclerfavouriteView
                );
        favouriteText=findViewById(R.id.txt_fav);
        Animation animation = AnimationUtils.loadAnimation(
                this,
                R.anim.text_slide
        );

        favouriteText.startAnimation(animation);
        recyclerfavouriteView.setLayoutManager(
                new LinearLayoutManager(this)
        );
        favoriteList =
                new ArrayList<Audio>();

        adapter =
                new AudioAdapter(
                        this,
                        favoriteList,
                        new AudioAdapter.OnAudioClickListener() {

                            @Override
                            public void onAudioClick(
                                    Audio audio) {

                                Intent intent =
                                        new Intent(
                                                Favourites.this,
                                                MusicPlayerPage.class
                                        );

                                intent.putExtra(
                                        "title",
                                        audio.getTitle()
                                );

                                intent.putExtra(
                                        "artist",
                                        audio.getArtist()
                                );

                                intent.putExtra(
                                        "path",
                                        audio.getPath()
                                );

                                intent.putExtra(
                                        "albumArt",
                                        audio.getAlbumArt()
                                );

                                startActivity(intent);
                            }
                        }
                );

        recyclerfavouriteView.setAdapter(
                adapter
        );

        loadFavorites();
    }

    @Override
    protected void onResume() {

        super.onResume();

        if (preferences != null
                && adapter != null) {

            loadFavorites();
        }
    }

    private void loadFavorites() {

        favoriteList.clear();

        Map<String, ?> all =
                preferences.getAll();

        for (Map.Entry<String, ?> entry :
                all.entrySet()) {

            String key =
                    entry.getKey();

            /*
             * Only process the actual song-path
             * entries.
             *
             * Ignore:
             * _title
             * _artist
             * _albumArt
             */

            if (key.endsWith("_title")
                    || key.endsWith("_artist")
                    || key.endsWith("_albumArt")) {

                continue;
            }

            Object value =
                    entry.getValue();

            if (!(value instanceof Boolean)) {
                continue;
            }

            Boolean favorite =
                    (Boolean) value;

            if (!favorite) {
                continue;
            }

            String path = key;

            String title =
                    preferences.getString(
                            path + "_title",
                            "Unknown Song"
                    );

            String artist =
                    preferences.getString(
                            path + "_artist",
                            "Unknown Artist"
                    );

            String albumArt =
                    preferences.getString(
                            path + "_albumArt",
                            ""
                    );

            Audio audio =
                    new Audio(
                            title,
                            artist,
                            path,
                            albumArt
                    );

            audio.setFavorite(true);

            favoriteList.add(audio);
        }

        adapter.notifyDataSetChanged();
    }
}
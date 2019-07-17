package com.example.helpq.view;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

<<<<<<< HEAD:app/src/main/java/com/example/helpq/MainActivity.java
import com.example.helpq.fragment.StudentQueueFragment;
=======
import com.example.helpq.R;
>>>>>>> bcfe60231a91e9eb466de2b83daa5a15c88e1d37:app/src/main/java/com/example/helpq/view/MainActivity.java

public class MainActivity extends AppCompatActivity {

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
<<<<<<< HEAD:app/src/main/java/com/example/helpq/MainActivity.java
        fragmentManager.beginTransaction().add(R.id.flMainContainer, new StudentQueueFragment(), StudentQueueFragment.TAG).commit();
=======
        fragmentManager.beginTransaction().add(R.id.flMainContainer, new StudentFragment(), StudentFragment.TAG).commit();
>>>>>>> bcfe60231a91e9eb466de2b83daa5a15c88e1d37:app/src/main/java/com/example/helpq/view/MainActivity.java
    }
}

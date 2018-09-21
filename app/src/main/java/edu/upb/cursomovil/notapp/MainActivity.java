package edu.upb.cursomovil.notapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int CREATE_TEXT_NOTE = 1;
    public static final int CREATE_AUDIO_NOTE = 2;
    public static final int CREATE_IMAGE_NOTE = 3;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private MyAdapter mAdapter;
    private NotesDbAdapter notesDbAdapter;
    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = findViewById(R.id.recycler);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        notesDbAdapter = new NotesDbAdapter(this);
        notesDbAdapter.open();
        cursor = notesDbAdapter.fetchAllNotes();

        mAdapter = new MyAdapter(cursor);

        mRecyclerView.setAdapter(mAdapter);


        ImageView iconFab = new ImageView(this); // Create an icon
        iconFab.setImageDrawable(getDrawable(R.drawable.ic_add_circle_black_24dp));
        FloatingActionButton fab = new FloatingActionButton.Builder(this).setContentView(iconFab).build();
        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);

        ImageView itemIconCamera = new ImageView(this);
        itemIconCamera.setImageDrawable(getDrawable(R.drawable.ic_camera_alt_black_24dp));
        SubActionButton btnAddNoteCamera = itemBuilder.setContentView(itemIconCamera).build();

        ImageView itemIconVoice = new ImageView(this);
        itemIconVoice.setImageDrawable(getDrawable(R.drawable.ic_keyboard_voice_black_24dp));
        SubActionButton btnAddNoteVoice = itemBuilder.setContentView(itemIconVoice).build();

        ImageView itemIconText = new ImageView(this);
        itemIconText.setImageDrawable(getDrawable(R.drawable.ic_text_format_black_24dp));
        SubActionButton btnAddNoteText = itemBuilder.setContentView(itemIconText).build();

        FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(btnAddNoteCamera)
                .addSubActionView(btnAddNoteText)
                .addSubActionView(btnAddNoteVoice)
                .attachTo(fab)
                .build();

        btnAddNoteCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateNoteActivity.class);
                intent.putExtra("TYPE", "image");
                startActivityForResult(intent, 1);
            }
        });

        btnAddNoteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateNoteActivity.class);
                intent.putExtra("TYPE", "text");
                startActivityForResult(intent, 1);
            }
        });

        btnAddNoteVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateNoteActivity.class);
                intent.putExtra("TYPE", "audio");
                startActivityForResult(intent, 1);
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.calendar) {

        } else if (id == R.id.archived) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void saveNoteText (String title, String text_body, String reminder) {
        NotesDbAdapter notesDbAdapter = new NotesDbAdapter(this);
        notesDbAdapter.open();
        System.out.println("trying to save TITLE:"+title+" - BODY:"+text_body+" - REM:"+reminder);
        notesDbAdapter.createTextNote(title, text_body, reminder);
        notesDbAdapter.close();
    }

    private void saveNoteAudio(String title, String audioPath, String reminder) {
        NotesDbAdapter notesDbAdapter = new NotesDbAdapter(this);
        notesDbAdapter.open();
        System.out.println("trying to save TITLE:"+title+" - PATH:"+audioPath+" - REM:"+reminder);
        notesDbAdapter.createAudioNote(title, audioPath, reminder);
        notesDbAdapter.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null) {
            if(resultCode == CREATE_TEXT_NOTE) {
                String title = data.getStringExtra(CreateNoteActivity.ATTR_TITLE);
                String textBody = data.getStringExtra(CreateNoteActivity.ATTR_TEXT_BODY);
                Boolean hasReminder = data.getBooleanExtra(CreateNoteActivity.ATTR_HAS_REMINDER, false);
                String reminderDate = "";
                if(hasReminder) {
                    reminderDate = data.getStringExtra(CreateNoteActivity.ATTR_REMINDER);
                }
                saveNoteText(title, textBody, reminderDate);

                updateNotes();
            }

            else if(resultCode == CREATE_AUDIO_NOTE) {
                String title = data.getStringExtra(CreateNoteActivity.ATTR_TITLE);
                String audioPath = data.getStringExtra(CreateNoteActivity.ATTR_AUDIO_PATH);
                Boolean hasReminder = data.getBooleanExtra(CreateNoteActivity.ATTR_HAS_REMINDER, false);
                String reminderDate = "";
                if(hasReminder) {
                    reminderDate = data.getStringExtra(CreateNoteActivity.ATTR_REMINDER);
                }
                saveNoteAudio(title, audioPath, reminderDate);

                updateNotes();
            }
        }
    }

    public void updateNotes() {
        cursor = notesDbAdapter.fetchAllNotes();
        mAdapter.setmDataset(cursor);
        mAdapter.notifyDataSetChanged();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        notesDbAdapter.close();
    }
}

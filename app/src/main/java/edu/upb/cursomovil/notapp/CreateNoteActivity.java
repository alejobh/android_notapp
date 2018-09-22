package edu.upb.cursomovil.notapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class CreateNoteActivity extends AppCompatActivity {

    public static final String ATTR_TITLE = "title";
    public static final String ATTR_REMINDER = "reminder";
    public static final String ATTR_TEXT_BODY= "text_body";
    public static final String ATTR_AUDIO_PATH= "audio_path";
    public static final String ATTR_IMAGE_PATH= "image_path";
    public static final String ATTR_HAS_REMINDER= "hasReminder";

    private Switch hasReminderSwitch;
    private Boolean hasReminder = false;
    private EditText reminderDate;
    private EditText textBody;
    private String typeCreate;
    private final String actionDoing = "Creating new";
    private LinearLayout audioLayout;
    private ImageButton play, stop, record;
    private MediaRecorder myAudioRecorder;
    private String fileRoute = "";
    private String outputFile = "";

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        typeCreate = getIntent().getStringExtra("TYPE");
        textBody = findViewById(R.id.textBody);
        textBody.setVisibility(View.GONE);

        audioLayout = findViewById(R.id.layoutAudio);
        audioLayout.setVisibility(View.GONE);
        String titleText = "";
        if(typeCreate.equals("text")) {
            textBody.setVisibility(View.VISIBLE);
            titleText = " text note";
        } else if(typeCreate.equals("image")) {
            titleText = " image note";
        } else if(typeCreate.equals("audio")) {

            audioLayout.setVisibility(View.VISIBLE);
            play = findViewById(R.id.play);
            stop = findViewById(R.id.stop);
            record = findViewById(R.id.record);
            stop.setEnabled(false);
            play.setEnabled(false);
            titleText = " audio note";
            String sep = File.separator; // Use this instead of hardcoding the "/"
            fileRoute = sep+"recording"+((int) (Math.random() * (30000 - 1)) + 1)+".3gp";
            outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + fileRoute;

            record.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        startRecordVars();
                        myAudioRecorder.prepare();
                        myAudioRecorder.start();
                    } catch (IllegalStateException ise) {
                        // make something ...
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    record.setEnabled(false);
                    stop.setEnabled(true);
                    record.setAlpha(.5f);
                    stop.setAlpha(1f);
                    Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();
                }
            });
            stop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    myAudioRecorder.stop();
                    myAudioRecorder.release();
                    myAudioRecorder = null;
                    record.setEnabled(true);
                    stop.setEnabled(false);
                    stop.setAlpha(.5f);
                    record.setAlpha(1f);
                    play.setEnabled(true);
                    Toast.makeText(getApplicationContext(), "Audio Recorder stopped", Toast.LENGTH_LONG).show();
                }
            });

            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MediaPlayer mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(outputFile);
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        Toast.makeText(getApplicationContext(), "Playing Audio", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        // make something
                    }
                }
            });

            verifyStoragePermissions(this);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.RECORD_AUDIO },
                        10);
            } else {
                startRecordVars();
            }

        }

        getSupportActionBar().setTitle(actionDoing+titleText);

        hasReminderSwitch = findViewById(R.id.hasReminder);
        hasReminderSwitch.setChecked(false);

        reminderDate = findViewById(R.id.reminderDate);
        reminderDate.setVisibility(View.GONE);

        hasReminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {

                if(isChecked){
                    hasReminder=true;
                    reminderDate.setVisibility(View.VISIBLE);
                }else{
                    hasReminder=false;
                    reminderDate.setVisibility(View.GONE);
                }

            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void saveNote(View view) {

        Intent intent = new Intent();

        EditText editTextTitle = findViewById(R.id.textTitle);
        String title = editTextTitle.getText().toString();
        intent.putExtra(this.ATTR_TITLE, title);

        intent.putExtra(this.ATTR_HAS_REMINDER, hasReminder);
        if(hasReminder) {
            intent.putExtra(this.ATTR_REMINDER, reminderDate.getText().toString());
        }

        if(typeCreate.equals("text")) {
            EditText editTextBody = findViewById(R.id.textBody);
            String text_body = editTextBody.getText().toString();
            intent.putExtra(this.ATTR_TEXT_BODY, text_body);
            setResult(MainActivity.CREATE_TEXT_NOTE, intent);
        } else if(typeCreate.equals("image")) {

        } else if(typeCreate.equals("audio")) {
            intent.putExtra(this.ATTR_AUDIO_PATH, outputFile);
            setResult(MainActivity.CREATE_AUDIO_NOTE, intent);
        }

        finish();
    }

    public void dateOnClick(View view) {
        switch (view.getId()) {
            case R.id.reminderDate:
                showDatePickerDialog();
                break;
        }
    }

    private void showDatePickerDialog() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because january is zero
                String selectedDate = year+"-";
                month = month+1;
                if(month<10) {
                    selectedDate = selectedDate+"0"+month+"-";
                } else {
                    selectedDate = selectedDate+month+"-";
                }
                if(day<10) {
                    selectedDate = selectedDate+"0"+day;
                } else {
                    selectedDate = selectedDate+day;
                }

                timePicker(selectedDate);

            }
        });
        newFragment.show(getFragmentManager(), "datepicker");
    }

    private void timePicker(final String selectedDate){
        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String hours = ""+hourOfDay;
                        String minutes = ""+minute;
                        if(hourOfDay<10){
                            hours = "0"+hours;
                        }
                        if(minute<10){
                            minutes = "0"+minutes;
                        }
                        reminderDate.setText(selectedDate+" "+hours + ":" + minutes+":00");
                    }
                }, 0, 0, false);
        timePickerDialog.show();
    }

    private void startRecordVars() {
        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(outputFile);
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                                     @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRecordVars();
            } else {
                finish();
            }
        }
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}




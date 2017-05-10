package se.gu.dit524.group5.bluetoothremote;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity  {
    public static final String EXTRA_MESSAGE = "com.example.MarbleApp.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    //This starts a new activity when clicking the start button in the activity_main.
    public void sendMessage(View view){
        Intent intent = new Intent(this, ActivitySecond.class);
        this.startActivity(intent);
    }

    public void sendMessageToMaps(View view){
        Intent intent = new Intent(this, ActivityThird.class);
        this.startActivity(intent);
    }
}

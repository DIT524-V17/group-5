package se.gu.dit524.group5.bluetoothremote;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by annahenryson on 2017-05-10.
 */

public class ActivityThird extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO:  do something here...

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("filePath", "/sdcard/path1");
        setResult(0, intent);
        finish();
    }
}

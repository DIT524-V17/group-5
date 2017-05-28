package se.gu.dit524.group5.bluetoothremote;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by annahenryson on 2017-05-10.
 * Modified by julian.bock on 2017-05-27.
 */

public class ActivityThird extends AppCompatActivity {
    private String selected, correspondingSites;
    private boolean loadMap, loadSites;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_third);

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("maps", Context.MODE_PRIVATE);
        File[] files = directory.listFiles();

        ArrayList<String> selectableFiles = new ArrayList<>();
        selectableFiles.add("map_demo_100x100_objectoutlines.png");
        for (int i = files.length -1; i >= 0; i--) {
            File f = files[i];
            if (f.getName().startsWith("map_"))
                selectableFiles.add(f.getName());
        }

        ListView list = (ListView) this.findViewById(R.id.fileList);
        list.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.custom_list_item,
                R.id.itemText, selectableFiles);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selected = (String) parent.getAdapter().getItem(position);
                correspondingSites = selected.replace(ActivitySecond.IMG_PREFIX_MAP, ActivitySecond.IMG_PREFIX_SITES);

                ImageView imageView = (ImageView) findViewById(R.id.mapPreview);
                Bitmap map = ActivitySecond.loadImage(getBaseContext(), selected);
                Bitmap sites = ActivitySecond.loadImage(getBaseContext(), correspondingSites);
                if (sites != null) {
                    Canvas c = new Canvas(map);
                    c.drawBitmap(sites, 0, 0, null);
                }
                imageView.setImageBitmap(map);
            }
        });
    }

    public void returnToMap(View view) {
        if (view == this.findViewById(R.id.loadSelected)) {
            ContextWrapper cw = new ContextWrapper(this.getBaseContext());
            File dir = cw.getDir("maps", Context.MODE_PRIVATE);
            File sites = new File(dir.getPath() +"/" +correspondingSites);
            loadMap = true;

            if (selected.equals("map_demo_100x100_objectoutlines.png")) {
                loadSites = true;
                returnWithResult();
            }
            else if (sites.exists()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivityThird.this);
                builder.setMessage("Would you like to import the latest Voronoi-Sites that were saved alongside this map?");
                builder.setNegativeButton("No, thanks. I'd like to start over.",
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadSites = false;
                        returnWithResult();
                    }
                });
                builder.setPositiveButton("Yes!",
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadSites = true;
                        returnWithResult();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else this.returnWithResult();
        }
        else this.returnWithResult();
    }

    private void returnWithResult() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("mapIdentifier", selected);
        intent.putExtra("siteIdentifier", correspondingSites);
        intent.putExtra("loadMap", loadMap);
        intent.putExtra("loadSites", loadSites);
        setResult(0, intent);
        finish();
    }
}

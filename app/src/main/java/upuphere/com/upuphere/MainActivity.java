package upuphere.com.upuphere;

import androidx.appcompat.app.AppCompatActivity;
import upuphere.com.upuphere.helper.PrefManager;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefManager = new PrefManager(getApplicationContext());

    }


}

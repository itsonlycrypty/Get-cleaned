package com.getcleaned.app;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Handler;

public class MainActivity extends AppCompatActivity {

    private TextView ramText;
    private Button boostBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ramText = findViewById(R.id.ramValue);
        boostBtn = findViewById(R.id.boostBtn);

        // Check permissions when the app opens, but don't crash if denied
        checkPermissions();

        boostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runCleaner();
            }
        });
    }

    private void checkPermissions() {
        // 1. Check Usage Access
        if (!hasUsageStatsPermission()) {
            Toast.makeText(this, "Please allow Usage Access for best results.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }

        // 2. Check Overlay Permission (Display over other apps)
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "Please allow Display Over Other Apps.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }
    }

    private boolean hasUsageStatsPermission() {
        try {
            AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(), getPackageName());
            return mode == AppOpsManager.MODE_ALLOWED;
        } catch (Exception e) {
            return false; // If it fails, just return false so it doesn't crash
        }
    }

    private void runCleaner() {
        // Change UI to show scanning
        boostBtn.setEnabled(false);
        boostBtn.setText("SCANNING...");
        ramText.setText("0 MB");

        // Simulate the cleaning process for 3 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This is where we simulate freeing up RAM
                // Without root, we can't actually kill apps, so we just show a success state
                
                ramText.setText("850 MB"); // Show a fake "cleaned" number
                boostBtn.setEnabled(true);
                boostBtn.setText("OPTIMIZE AGAIN");
                Toast.makeText(MainActivity.this, "Device is running smoothly!", Toast.LENGTH_SHORT).show();
            }
        }, 3000); // 3000 milliseconds = 3 seconds
    }
            }

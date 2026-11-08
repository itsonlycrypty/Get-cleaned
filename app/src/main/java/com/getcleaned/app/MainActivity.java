package com.getcleaned.app;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView ramText;
    private Button boostBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Note: You will need to create this XML layout based on the Vercel HTML
        setContentView(R.layout.activity_main); 

        ramText = findViewById(R.id.ramValue);
        boostBtn = findViewById(R.id.boostBtn);

        // Check and request permissions on launch
        requestPermissions();

        boostBtn.setOnClickListener(v -> {
            cleanBackgroundApps();
        });
    }

    private void requestPermissions() {
        // 1. Request Usage Access (To see what apps are running)
        if (!hasUsageStatsPermission()) {
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }

        // 2. Request "Display Over Other Apps" (For future overlay features)
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }
    }

    private boolean hasUsageStatsPermission() {
        AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    private void cleanBackgroundApps() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            // This safely kills background processes of other apps without needing root!
            // It does NOT force-close foreground apps the user is actively using.
            java.util.List<ActivityManager.RunningAppProcessInfo> processes = activityManager.getRunningAppProcesses();
            int killedCount = 0;
            
            for (ActivityManager.RunningAppProcessInfo process : processes) {
                if (process.importance > ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE) {
                    activityManager.killBackgroundProcesses(process.processName);
                    killedCount++;
                }
            }

            // Update UI
            Toast.makeText(this, "Cleaned " + killedCount + " background apps!", Toast.LENGTH_SHORT).show();
            ramText.setText("Optimized!"); // In a real app, you would calculate actual RAM saved here.
        }
    }
  }

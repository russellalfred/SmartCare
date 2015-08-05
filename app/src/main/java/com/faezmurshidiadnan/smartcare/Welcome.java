package com.faezmurshidiadnan.smartcare;

/**
 * Created by faez on 04/08/2015.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.hsg.android.simplexml.HealthVaultApp;
import com.microsoft.hsg.android.simplexml.HealthVaultInitializationHandler;
import com.microsoft.hsg.android.simplexml.HealthVaultSettings;

public class Welcome
        extends Activity
        implements HealthVaultInitializationHandler {

    private HealthVaultApp service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);

        service = HealthVaultApp.getInstance(this);

        Button go = (Button) findViewById(R.id.goButton);
        go.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                service.start(Welcome.this, Welcome.this);
            }
        });

        InitializeControls();
    }

    @Override
    protected void onResume() {
        if (service.getConnectionStatus() == HealthVaultApp.ConnectionStatus.Connected) {
            onConnected();
        }
        else
        {
            HealthVaultSettings settings = service.getSettings();
            settings.setMasterAppId("17f7132a-6613-470c-991a-e92ae7d150bc");
            settings.setServiceUrl("https://platform.healthvault-ppe.com/platform/wildcat.ashx");
            settings.setShellUrl("https://account.healthvault-ppe.com");
            settings.setIsMultiInstanceAware(true);
        }
        super.onResume();
    }

    private void InitializeControls() {
        TextView msg = (TextView)findViewById(R.id.welcomeText);
        switch(service.getConnectionStatus()) {
            case Connected:
                msg.setText("Connected!");
                break;
            case NotConnected:
                msg.setText("Connect this application to HealthVault");
                break;
        }
    }

    @Override
    public void onConnected() {
        Intent intent = new Intent(Welcome.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onError(Exception e) {
        Toast.makeText(
                Welcome.this,
                e.getMessage(),
                Toast.LENGTH_LONG).show();

    }
}

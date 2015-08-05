package com.faezmurshidiadnan.smartcare;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.microsoft.hsg.HVException;
import com.microsoft.hsg.android.simplexml.HealthVaultApp;
import com.microsoft.hsg.android.simplexml.client.HealthVaultClient;
import com.microsoft.hsg.android.simplexml.client.RequestCallback;
import com.microsoft.hsg.android.simplexml.methods.getthings3.request.ThingRequestGroup2;
import com.microsoft.hsg.android.simplexml.methods.getthings3.response.ThingResponseGroup2;
import com.microsoft.hsg.android.simplexml.things.thing.Thing2;
import com.microsoft.hsg.android.simplexml.things.types.base.CodableValue;
import com.microsoft.hsg.android.simplexml.things.types.types.PersonInfo;
import com.microsoft.hsg.android.simplexml.things.types.types.Record;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;


public class Condition extends MainActivity {

    private HealthVaultApp service;
    private HealthVaultClient hvClient;
    private Record selectedRecord;

    public class WeightCallback<Object> implements RequestCallback {
        public final static int UpdateWeights = 0;
        public final static int PutWeights = 1;
        public final static int UpdateRecords = 2;

        private int event;

        public WeightCallback(int event) {
            Condition.this.setProgressBarIndeterminateVisibility(true);
            this.event = event;
        }

        @Override
        public void onError(HVException exception) {
            Condition.this.setProgressBarIndeterminateVisibility(false);
            Toast.makeText(
                    Condition.this,
                    "An Test occurred.  " + exception.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

        @Override
        public void onSuccess(java.lang.Object obj) {
            Condition.this.setProgressBarIndeterminateVisibility(false);
            switch(event) {
                case PutWeights:
                    getWeights();
                    break;
                case UpdateWeights:
                    updateWeights(((ThingResponseGroup2)obj).getThing());
                    break;
                case UpdateRecords:
                    updateRecords((List<Record>)obj);
                    break;
            }
        }
    }

    private void updateWeights(List<Thing2> things) {

        List<String> weights = new ArrayList<String>();
        for(Thing2 thing : things) {
            com.microsoft.hsg.android.simplexml.things.types.condition.Condition w = (com.microsoft.hsg.android.simplexml.things.types.condition.Condition)thing.getData();

            weights.add(String.valueOf(w.getName().getText()));
        }

        ListView lv = (ListView)findViewById(R.id.weightList);






        lv.setAdapter(new ArrayAdapter<String>(
                Condition.this,
                android.R.layout.simple_list_item_1,
                weights));
    }

    private void updateRecords(List<Record> records) {
        Spinner s = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<Record> adapter = new ArrayAdapter<Record>(
                Condition.this,
                android.R.layout.simple_spinner_item,
                records);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
    }

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState the saved instance state
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.condition, null, false);
        mDrawerLayout.addView(contentView, 0);


        service = HealthVaultApp.getInstance();
        hvClient = new HealthVaultClient();



        Button putThing = (Button) findViewById(R.id.putThing);
        putThing.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                EditText text = (EditText) findViewById(R.id.weightInput);
                putWeight(text.getText().toString());
            }
        });

        Spinner s = (Spinner) findViewById(R.id.spinner);
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View view, int pos, long id) {
                selectedRecord = (Record) parent.getItemAtPosition(pos);
                getWeights();
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });



    }

    @Override
    protected void onStart() {
        hvClient.start();
        super.onStart();
    }

    @Override
    protected void onResume()
    {
        InitializeControls();
        super.onResume();
    }

    private void InitializeControls() {
        getRecords();
    }

    private void getRecords() {
        hvClient.asyncRequest(new Callable<List<Record>>() {
                                  public List<Record> call() {
                                      ArrayList<Record> records = new ArrayList<Record>();
                                      List<PersonInfo> personInfos = service.getPersonInfoList();
                                      for (PersonInfo personInfo : personInfos) {
                                          for (Record record : personInfo.getRecords()) {
                                              records.add(record);
                                          }
                                      }

                                      if (records.size() > 0) {
                                          selectedRecord = records.get(0);
                                      }

                                      return records;
                                  }
                              },
                new WeightCallback(WeightCallback.UpdateRecords));
    }

    private void putWeight(String value) {
        final Thing2 thing = new Thing2();
        com.microsoft.hsg.android.simplexml.things.types.condition.Condition condition = new com.microsoft.hsg.android.simplexml.things.types.condition.Condition();

        condition.setName(new CodableValue(value));
        thing.setData(condition);
        hvClient.asyncRequest(
                selectedRecord.putThingAsync(thing),
                new WeightCallback(WeightCallback.PutWeights));
    }

    @SuppressWarnings("unchecked")
    private void getWeights()
    {
        hvClient.asyncRequest(
                selectedRecord.getThingsAsync(ThingRequestGroup2.thingTypeQuery(com.microsoft.hsg.android.simplexml.things.types.condition.Condition.ThingType)),
                new WeightCallback(WeightCallback.UpdateWeights));
    }

    @Override
    protected void onStop() {
        hvClient.stop();
        super.onStop();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_fourth, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

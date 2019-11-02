package com.example.mycarparksearch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

public class SearchForAddressActivity extends AppCompatActivity {
    private ConstraintLayout searchUI;
    private EditText carparkEditText;
    private ListView searchResults;
    private Context context;
    private ArrayList<String> listItem;
    private ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_for_address);

        context = getApplicationContext();
        listItem = new ArrayList<>();

        searchResults = findViewById(R.id.searchResults);

        searchUI = findViewById(R.id.search_UI);
        searchUI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (carparkEditText.isFocused() || searchResults.getVisibility() == View.VISIBLE) {
                    onBackPressed();
                }
            }
        });

        carparkEditText = findViewById(R.id.carparkEditText);
        carparkEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                carparkEditText.setText(null);
            }
        });
        carparkEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searchResults.getVisibility() == View.VISIBLE) {
                    searchResults.setVisibility(View.GONE);
                }
            }
        });
        carparkEditText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    ArrayList<CarparkEntity> carparkList = null;
                    try {
                        carparkList = (ArrayList<CarparkEntity>)(new Search().execute(carparkEditText.getText().toString()).get());
                    } catch (ExecutionException | InterruptedException e) {
                        Toast.makeText(getApplicationContext(), "Failed to get search results!\nTask interrupted.", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    viewSearchResults(carparkList);
                    return true;
                }
                return false;
            }
        });
    }

    private void viewSearchResults(ArrayList<CarparkEntity> carparkList) {
        if (carparkList.size() == 0) {
            Toast.makeText(getApplicationContext(), "No results.", Toast.LENGTH_SHORT).show();
            return;
        }
        listItem.clear();
        for (CarparkEntity carpark : carparkList) {
            listItem.add(carpark.getInformation(context.getString(R.string.carParkNo))
                    + "\n" + carpark.getInformation(context.getString(R.string.address)));
        }

        adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, listItem);
        searchResults.setAdapter(adapter);
        searchResults.setVisibility(View.VISIBLE);
        searchResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String carpark = (String) parent.getAdapter().getItem(position);
                    StringTokenizer st = new StringTokenizer(carpark);
                    String carParkNo = st.nextToken();
                    Intent intent = new Intent(SearchForAddressActivity.this, InformationActivity.class);
                    intent.putExtra(MapsActivity.CAR_PARK_NO, carParkNo);
                    SearchForAddressActivity.this.startActivityForResult(intent, 1);
                }
        });

    }

    @Override
    public void onBackPressed() {
        if (carparkEditText.isFocused() && searchResults.getVisibility() == View.VISIBLE) {
            carparkEditText.clearFocus();
            searchResults.setVisibility(View.GONE);
            return;
        }
        if (carparkEditText.isFocused()) {
            carparkEditText.clearFocus();
            return;
        }
        if(searchResults.getVisibility() == View.VISIBLE){
            searchResults.setVisibility(View.GONE);
            return;
        }
        super.onBackPressed();
        try {
            adapter.notifyDataSetChanged();
        } catch (Exception e) { }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                String carParkNo = intent.getStringExtra(MapsActivity.CAR_PARK_NO);
                String lat = intent.getStringExtra(MapsActivity.CAR_PARK_LAT);
                String lon = intent.getStringExtra(MapsActivity.CAR_PARK_LON);
                intent = new Intent();
                intent.putExtra(MapsActivity.CAR_PARK_NO, carParkNo);
                intent.putExtra(MapsActivity.CAR_PARK_LAT, lat);
                intent.putExtra(MapsActivity.CAR_PARK_LON, lon);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }

    private ArrayList<CarparkEntity> search(String keywords) {
        CarparkSQLControl con = new CarparkSQLControl(context.getString(R.string.sshHost),
                context.getString(R.string.sshUsername), context.getString(R.string.sshPassword),
                context.getString(R.string.dbHost), Integer.parseInt(context.getString(R.string.dbPort)),
                context.getString(R.string.dbName), context.getString(R.string.dbUsername),
                context.getString(R.string.dbPassword), context);
        ArrayList<CarparkEntity> carparkList = null;
        try {
            carparkList = con.queryCarparks(keywords);
        } catch (SQLException e) {
            Toast.makeText(getApplicationContext(), "Failed to find car parks!", Toast.LENGTH_SHORT).show();
            return null;
        }
        return carparkList;
    }

    private class Search extends AsyncTask<String, Void, ArrayList<CarparkEntity>> {
        @Override
        protected ArrayList<CarparkEntity> doInBackground(String... keywordss) {
            return search(keywordss[0]);
        }
    }
}

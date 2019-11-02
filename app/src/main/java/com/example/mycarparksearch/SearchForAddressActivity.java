package com.example.mycarparksearch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
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
    private LinearLayout filters;
    private Button type;
    private Button system;
    private Button short_term;
    private Button free;
    private Button night;
    private String typeFilter;
    private String systemFilter;
    private String short_termFilter;
    private String freeFilter;
    private String nightFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_for_address);

        context = getApplicationContext();
        listItem = new ArrayList<>();
        setUpUIElements();
    }

    private void setUpUIElements() {
        searchResults = findViewById(R.id.searchResults);

        setFilters();

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
                    filters.setVisibility(View.VISIBLE);
                }
            }
        });
        carparkEditText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    performEnter();
                    return true;
                }
                return false;
            }
        });
    }

    private void performEnter() {
        ArrayList<CarparkEntity> carparkList = null;
        try {
            carparkList = (ArrayList<CarparkEntity>)(new Search().execute(carparkEditText.getText().toString()).get());
        } catch (ExecutionException | InterruptedException e) {
            Toast.makeText(getApplicationContext(), "Failed to get search results!\nTask interrupted.", Toast.LENGTH_SHORT).show();
            return;
        }
        viewSearchResults(carparkList);
    }

    private void setType() {
        type = findViewById(R.id.type);
        typeFilter = null;
        type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(SearchForAddressActivity.this, view);
                popup.getMenuInflater().inflate(R.menu.type_filter, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.multi_storey:
                                typeFilter = context.getString(R.string.multi_storey);
                                type.setText(typeFilter);
                                return true;
                            case R.id.surface_carpark:
                                typeFilter = context.getString(R.string.surface_carpark);
                                type.setText(typeFilter);
                                return true;
                            case R.id.basement_carpark:
                                typeFilter = context.getString(R.string.basement_carpark);
                                type.setText(typeFilter);
                                return true;
                            case R.id.covered_carpark:
                                typeFilter = context.getString(R.string.covered_carpark);
                                type.setText(typeFilter);
                                return true;
                            case R.id.mechanised_surface:
                                typeFilter = context.getString(R.string.mechanised_surface);
                                type.setText(typeFilter);
                                return true;
                            case R.id.mechanised_carpark:
                                typeFilter = context.getString(R.string.mechanised_carpark);
                                type.setText(typeFilter);
                                return true;
                            default:
                                typeFilter = null;
                                type.setText(context.getString(R.string.typeofparking));
                                return false;
                        }
                    }
                });
                popup.show();
            }
        });
    }

    private void setSystem() {
        system = findViewById(R.id.system);
        systemFilter = null;
        system.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(SearchForAddressActivity.this, view);
                popup.getMenuInflater().inflate(R.menu.system_filter, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.electronic:
                                systemFilter = context.getString(R.string.electronic);
                                system.setText(systemFilter);
                                return true;
                            case R.id.coupon:
                                systemFilter = context.getString(R.string.coupon);
                                system.setText(systemFilter);
                                return true;
                            default:
                                systemFilter = null;
                                system.setText(context.getString(R.string.parkingsystem));
                                return false;
                        }
                    }
                });
                popup.show();
            }
        });
    }

    private void setShortTerm() {
        short_term = findViewById(R.id.short_term);
        short_termFilter = null;
        short_term.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(SearchForAddressActivity.this, view);
                popup.getMenuInflater().inflate(R.menu.short_term_filter, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.yes:
                                short_termFilter = context.getString(R.string.yes);
                                short_term.setText(short_termFilter);
                                return true;
                            case R.id.no:
                                short_termFilter = context.getString(R.string.no);
                                short_term.setText(short_termFilter);
                                return true;
                            default:
                                short_termFilter = null;
                                short_term.setText(context.getString(R.string.shorttermparking));
                                return false;
                        }
                    }
                });
                popup.show();
            }
        });
    }

    private void setFree() {
        free = findViewById(R.id.free);
        freeFilter = null;
        free.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(SearchForAddressActivity.this, view);
                popup.getMenuInflater().inflate(R.menu.free_filter, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.yes:
                                freeFilter = context.getString(R.string.yes);
                                free.setText(freeFilter);
                                return true;
                            case R.id.no:
                                freeFilter = context.getString(R.string.no);
                                free.setText(freeFilter);
                                return true;
                            default:
                                freeFilter = null;
                                free.setText(context.getString(R.string.freeparking));
                                return false;
                        }
                    }
                });
                popup.show();
            }
        });
    }

    private void setNight() {
        night = findViewById(R.id.night);
        nightFilter = null;
        night.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(SearchForAddressActivity.this, view);
                popup.getMenuInflater().inflate(R.menu.night_filter, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.yes:
                                nightFilter = context.getString(R.string.yes);
                                night.setText(nightFilter);
                                return true;
                            case R.id.no:
                                nightFilter = context.getString(R.string.no);
                                night.setText(nightFilter);
                                return true;
                            default:
                                nightFilter = null;
                                night.setText(context.getString(R.string.nightparking));
                                return false;
                        }
                    }
                });
                popup.show();
            }
        });
    }

    private void setFilters() {
        filters = findViewById(R.id.filters);
        setType();
        setSystem();
        setFree();
        setShortTerm();
        setNight();
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
        filters.setVisibility(View.GONE);
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
            searchResults.setVisibility(View.GONE);
            filters.setVisibility(View.VISIBLE);
            return;
        }
        if (carparkEditText.isFocused()) {
            carparkEditText.clearFocus();
            return;
        }
        if(searchResults.getVisibility() == View.VISIBLE){
            searchResults.setVisibility(View.GONE);
            filters.setVisibility(View.VISIBLE);
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
            carparkList = con.queryCarparks(keywords, typeFilter, systemFilter,
                                                short_termFilter, freeFilter, nightFilter);
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

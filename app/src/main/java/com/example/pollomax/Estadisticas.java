package com.example.pollomax;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class Estadisticas extends AppCompatActivity {

    private Spinner spinnerCorrales;
    private LinearLayout layoutBotones;
    private Button btnGastos, btnIngresos;

    private BaseDeDatos dbHelper;
    private List<Corral> listaCorrales;
    private Corral corralSeleccionado = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadisticas);

        dbHelper = new BaseDeDatos(this);

        spinnerCorrales = findViewById(R.id.spinnerCorralesFinanzas);
        layoutBotones = findViewById(R.id.layoutBotonesFinanzas);
        btnGastos = findViewById(R.id.btnGastos);
        btnIngresos = findViewById(R.id.btnIngresos);

        cargarCorralesEnSpinner();

        spinnerCorrales.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    corralSeleccionado = listaCorrales.get(position - 1);
                    layoutBotones.setVisibility(View.VISIBLE);
                } else {
                    corralSeleccionado = null;
                    layoutBotones.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                corralSeleccionado = null;
                layoutBotones.setVisibility(View.GONE);
            }
        });

        btnGastos.setOnClickListener(v -> {
            if (corralSeleccionado != null) {
                Intent intent = new Intent(Estadisticas.this, GastosActivity.class);
                intent.putExtra("CORRAL_ID", corralSeleccionado.getId());
                startActivity(intent);
            }
        });

        btnIngresos.setOnClickListener(v -> {
            if (corralSeleccionado != null) {
                Intent intent = new Intent(Estadisticas.this, IngresosActivity.class);
                intent.putExtra("CORRAL_ID", corralSeleccionado.getId());
                intent.putExtra("NOMBRE_CORRAL", corralSeleccionado.getNombre());
                startActivity(intent);
            }
        });
    }

    private void cargarCorralesEnSpinner() {
        listaCorrales = new ArrayList<>();
        List<String> nombresCorrales = new ArrayList<>();
        nombresCorrales.add("Seleccione un corral...");

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT * FROM Corral", null)) {
            if (cursor.moveToFirst()) {
                do {
                    Corral corral = new Corral(
                            cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                            cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                            cursor.getInt(cursor.getColumnIndexOrThrow("cantidad_pollos")),
                            cursor.getDouble(cursor.getColumnIndexOrThrow("precio_pollos")),
                            cursor.getString(cursor.getColumnIndexOrThrow("fecha_compra"))
                    );
                    listaCorrales.add(corral);
                    nombresCorrales.add(corral.getNombre());
                } while (cursor.moveToNext());
            }
        }
        db.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombresCorrales);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCorrales.setAdapter(adapter);
    }
}

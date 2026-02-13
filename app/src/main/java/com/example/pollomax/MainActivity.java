package com.example.pollomax;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // Vistas del Dashboard
    private TextView tvCorralesActivos, tvMortalidad, tvPollosVivos, tvCostoPollos, tvGananciaNeta;
    private BaseDeDatos dbHelper;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new BaseDeDatos(this);


        tvCorralesActivos = findViewById(R.id.tvCorralesActivos);
        tvMortalidad = findViewById(R.id.tvMortalidad);
        tvPollosVivos = findViewById(R.id.tvPollosVivos);
        tvCostoPollos = findViewById(R.id.tvCostoPollos);
        tvGananciaNeta = findViewById(R.id.tvGananciaNeta);
        bottomNavigationView = findViewById(R.id.bottom_navigation);


        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_ver_corrales) {
                startActivity(new Intent(this, VerCorrales.class));
                return true;
            } else if (itemId == R.id.nav_crear_corral) {
                startActivity(new Intent(this, CrearCorral.class));
                return true;
            } else if (itemId == R.id.nav_alimentacion) {
                startActivity(new Intent(this, Alimentacion.class));
                return true;
            } else if (itemId == R.id.nav_finanzas) {
                startActivity(new Intent(this, Estadisticas.class));
                return true;
            } else if (itemId == R.id.nav_soporte) {
                startActivity(new Intent(this, SoporteActivity.class));
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        actualizarDashboard();
    }

    private void actualizarDashboard() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        long corralesActivos = 0;
        try (Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM Corral", null)) {
            if (cursor.moveToFirst()) {
                corralesActivos = cursor.getLong(0);
            }
        }

        int mortalidadTotal = 0;
        int pollosInicialesTotal = 0;
        int pollosVendidosTotal = 0;
        double costoTotalPollos = 0;
        double gananciaBrutaTotal = 0;
        double gastoAlimentoTotal = 0;
        double gastoInsumosTotal = 0;

        String query =
                "SELECT " +
                        "SUM(mortalidad_total), " +
                        "SUM(cantidad_pollos), " +
                        "SUM(pollos_vendidos), " +
                        "SUM(precio_pollos), " +
                        "SUM(ganancia_total), " +
                        "SUM(consumo_acumulado * precio_kilo_promedio), " +
                        "SUM(gasto_insumos) " +
                        "FROM Corral";
        try (Cursor cursor = db.rawQuery(query, null)) {
            if (cursor.moveToFirst()) {
                mortalidadTotal = cursor.getInt(0);
                pollosInicialesTotal = cursor.getInt(1);
                pollosVendidosTotal = cursor.getInt(2);
                costoTotalPollos = cursor.getDouble(3);
                gananciaBrutaTotal = cursor.getDouble(4);
                gastoAlimentoTotal = cursor.getDouble(5);
                gastoInsumosTotal = cursor.getDouble(6);
            }
        }

        int pollosVivosTotal = pollosInicialesTotal - mortalidadTotal - pollosVendidosTotal;
        double gananciaNetaTotal = gananciaBrutaTotal
                - gastoAlimentoTotal
                - costoTotalPollos
                - gastoInsumosTotal;


        tvCorralesActivos.setText(String.valueOf(corralesActivos));
        tvMortalidad.setText(String.valueOf(mortalidadTotal));
        tvPollosVivos.setText(String.valueOf(pollosVivosTotal));
        tvCostoPollos.setText(String.format(Locale.getDefault(), "$%,.0f", costoTotalPollos));
        tvGananciaNeta.setText(String.format(Locale.getDefault(), "$%,.0f", gananciaNetaTotal));
    }
}

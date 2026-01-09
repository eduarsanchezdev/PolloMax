package com.example.pollomax;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class GastosActivity extends AppCompatActivity {

    // Vistas
    private EditText etPrecioBultoCrecimiento, etPrecioBultoEngorde;
    private Button btnCalcularGasto, btnGuardarPrecios;
    private TextView tvGastoTituloCorral, tvPrecioPromedioKilo, tvConsumoTotalCorral, tvGastoTotalAlimento;
    private LinearLayout layoutResultadosGasto;

    private BaseDeDatos dbHelper;
    private int corralId;
    private String nombreCorral;
    private double precioPromedioKiloParaGuardar = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gastos);

        dbHelper = new BaseDeDatos(this);

        etPrecioBultoCrecimiento = findViewById(R.id.etPrecioBultoCrecimiento);
        etPrecioBultoEngorde = findViewById(R.id.etPrecioBultoEngorde);
        btnCalcularGasto = findViewById(R.id.btnCalcularGasto);
        btnGuardarPrecios = findViewById(R.id.btnGuardarPrecios);
        tvGastoTituloCorral = findViewById(R.id.tvGastoTituloCorral);
        tvPrecioPromedioKilo = findViewById(R.id.tvPrecioPromedioKilo);
        tvConsumoTotalCorral = findViewById(R.id.tvConsumoTotalCorral);
        tvGastoTotalAlimento = findViewById(R.id.tvGastoTotalAlimento);
        layoutResultadosGasto = findViewById(R.id.layoutResultadosGasto);

        corralId = getIntent().getIntExtra("CORRAL_ID", -1);
        if (corralId == -1) {
            Toast.makeText(this, "Error: No se seleccionó un corral", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        cargarNombreCorral();

        btnCalcularGasto.setOnClickListener(v -> calcularGasto());
        btnGuardarPrecios.setOnClickListener(v -> guardarPrecios());
    }

    private void cargarNombreCorral() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT nombre FROM Corral WHERE id = ?", new String[]{String.valueOf(corralId)})) {
            if (cursor.moveToFirst()) {
                nombreCorral = cursor.getString(0);
                tvGastoTituloCorral.setText("Gasto para: " + nombreCorral);
            }
        }
    }

    private void calcularGasto() {
        String precioCrecimientoStr = etPrecioBultoCrecimiento.getText().toString();
        String precioEngordeStr = etPrecioBultoEngorde.getText().toString();

        if (precioCrecimientoStr.isEmpty() || precioEngordeStr.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese ambos precios de bulto", Toast.LENGTH_SHORT).show();
            return;
        }

        double precioCrecimiento = Double.parseDouble(precioCrecimientoStr);
        double precioEngorde = Double.parseDouble(precioEngordeStr);

        double precioPromedioBulto = (precioCrecimiento + precioEngorde) / 2.0;
        double precioPromedioKilo = precioPromedioBulto / 40.0;
        this.precioPromedioKiloParaGuardar = precioPromedioKilo;

        double consumoTotal = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT consumo_acumulado FROM Corral WHERE id = ?", new String[]{String.valueOf(corralId)})) {
            if (cursor.moveToFirst()) {
                consumoTotal = cursor.getDouble(0);
            }
        }

        double gastoTotal = consumoTotal * precioPromedioKilo;

        tvPrecioPromedioKilo.setText(String.format(Locale.getDefault(), "Precio Promedio por Kilo de purina: $%,.2f", precioPromedioKilo));
        tvConsumoTotalCorral.setText(String.format(Locale.getDefault(), "Consumo Total del Corral: %.2f kg", consumoTotal));
        tvGastoTotalAlimento.setText(String.format(Locale.getDefault(), "Gasto Total en Alimento: $%,.2f", gastoTotal));
        
        layoutResultadosGasto.setVisibility(View.VISIBLE);
    }

    private void guardarPrecios() {
        if (precioPromedioKiloParaGuardar > 0) {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.execSQL("UPDATE Corral SET precio_kilo_promedio = ? WHERE id = ?",
                    new Object[]{precioPromedioKiloParaGuardar, corralId});
            db.close();
            Toast.makeText(this, "Precio por kilo guardado", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No se calculó un nuevo precio.", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}

package com.example.pollomax;

import android.content.ContentValues;
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
    private EditText etPrecioBultoCrecimiento, etPrecioBultoEngorde, etValorInsumos;
    private Button btnCalcularGasto, btnGuardarPrecios;
    private TextView tvGastoTituloCorral, tvPrecioPromedioKilo, tvConsumoTotalCorral, tvGastoTotalInsumos, tvGastoTotalAlimento;
    private LinearLayout layoutResultadosGasto;

    private BaseDeDatos dbHelper;
    private int corralId;
    private String nombreCorral;
    private double precioPromedioKiloParaGuardar = 0;
    private double valorInsumosIngresado = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gastos);

        dbHelper = new BaseDeDatos(this);

        // Inicializar vistas
        etPrecioBultoCrecimiento = findViewById(R.id.etPrecioBultoCrecimiento);
        etPrecioBultoEngorde = findViewById(R.id.etPrecioBultoEngorde);
        etValorInsumos = findViewById(R.id.etValorInsumos); // Nuevo campo
        btnCalcularGasto = findViewById(R.id.btnCalcularGasto);
        btnGuardarPrecios = findViewById(R.id.btnGuardarPrecios);
        tvGastoTituloCorral = findViewById(R.id.tvGastoTituloCorral);
        tvPrecioPromedioKilo = findViewById(R.id.tvPrecioPromedioKilo);
        tvConsumoTotalCorral = findViewById(R.id.tvConsumoTotalCorral);
        tvGastoTotalInsumos = findViewById(R.id.tvGastoTotalInsumos); // Nueva vista de resultado
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
        btnGuardarPrecios.setOnClickListener(v -> guardarPreciosYGastos());
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
        String valorInsumosStr = etValorInsumos.getText().toString();

        if (precioCrecimientoStr.isEmpty() || precioEngordeStr.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese los precios de bulto de purina", Toast.LENGTH_SHORT).show();
            return;
        }

        double precioCrecimiento = Double.parseDouble(precioCrecimientoStr);
        double precioEngorde = Double.parseDouble(precioEngordeStr);
        valorInsumosIngresado = valorInsumosStr.isEmpty() ? 0 : Double.parseDouble(valorInsumosStr);

        // Calcular precio promedio purina
        double precioPromedioBulto = (precioCrecimiento + precioEngorde) / 2.0;
        double precioPromedioKilo = precioPromedioBulto / 40.0;
        this.precioPromedioKiloParaGuardar = precioPromedioKilo;

        // Obtener consumo y gasto actual de insumos
        double consumoTotal = 0;
        double gastoInsumosActual = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT consumo_acumulado, gasto_insumos FROM Corral WHERE id = ?", new String[]{String.valueOf(corralId)})) {
            if (cursor.moveToFirst()) {
                consumoTotal = cursor.getDouble(0);
                gastoInsumosActual = cursor.getDouble(1);
            }
        }

        double gastoTotalAlimento = consumoTotal * precioPromedioKilo;
        double totalInsumosSimulado = gastoInsumosActual + valorInsumosIngresado;

        // Mostrar resultados
        tvPrecioPromedioKilo.setText(String.format(Locale.getDefault(), "Precio Promedio por Kilo de purina: $%,.2f", precioPromedioKilo));
        tvConsumoTotalCorral.setText(String.format(Locale.getDefault(), "Consumo Total del Corral: %.2f kg", consumoTotal));
        tvGastoTotalInsumos.setText(String.format(Locale.getDefault(), "Total Insumos (actual + nuevo): $%,.2f", totalInsumosSimulado));
        tvGastoTotalAlimento.setText(String.format(Locale.getDefault(), "Gasto Total en Alimento: $%,.2f", gastoTotalAlimento));
        
        layoutResultadosGasto.setVisibility(View.VISIBLE);
    }

    private void guardarPreciosYGastos() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            // Guardar el nuevo precio de purina
            db.execSQL("UPDATE Corral SET precio_kilo_promedio = ? WHERE id = ?",
                    new Object[]{precioPromedioKiloParaGuardar, corralId});
            
            // Sumar el nuevo valor de insumos al acumulado
            if (valorInsumosIngresado > 0) {
                db.execSQL("UPDATE Corral SET gasto_insumos = gasto_insumos + ? WHERE id = ?",
                        new Object[]{valorInsumosIngresado, corralId});
                
                // Opcional: Registrar evento de gasto de insumos
                ContentValues ev = new ContentValues();
                ev.put("descripcion", String.format(Locale.getDefault(), "Gasto en insumos para %s: +$%,.0f", nombreCorral, valorInsumosIngresado));
                db.insert("Eventos", null, ev);
            }

            db.setTransactionSuccessful();
            Toast.makeText(this, "Información financiera actualizada", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
        } finally {
            db.endTransaction();
            db.close();
        }
        finish();
    }
}

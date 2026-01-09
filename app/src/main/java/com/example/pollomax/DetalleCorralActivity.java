package com.example.pollomax;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DetalleCorralActivity extends AppCompatActivity {

    // Vistas
    private TextView tvDetalleNombreCorral, tvDetalleCantidadPollos,
            tvDetalleFechaInicio, tvDetalleEdadLote, tvDetalleMortalidad,
            tvDetallePesoPromedio, tvDetalleConsumoAcumulado, tvDetalleTipoAlimentacion,
            tvDetalleEstadoCorral, tvDetallePollosVivos, tvCostoInicialPollos,
            tvPrecioPromedioKilo, tvGastoTotalAlimento, tvPollosVendidos, tvGananciaTotal, tvGananciaNeta;
    
    private Button btnAbrirDialogo, btnEliminarCorral;

    private BaseDeDatos dbHelper;
    private int corralId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_corral);

        dbHelper = new BaseDeDatos(this);
        inicializarVistas();

        corralId = getIntent().getIntExtra("CORRAL_ID", -1);
        if (corralId == -1) {
            Toast.makeText(this, "Error: ID de corral no válido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        btnAbrirDialogo.setOnClickListener(v -> {
            Intent i = new Intent(DetalleCorralActivity.this, AgregarInfoCorralActivity.class);
            i.putExtra("CORRAL_ID", corralId);
            startActivity(i);
        });

        btnEliminarCorral.setOnClickListener(v -> mostrarDialogoConfirmacion());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (corralId != -1) {
            cargarDetallesDelCorral();
        }
    }

    private void inicializarVistas() {
        tvDetalleNombreCorral = findViewById(R.id.tvDetalleNombreCorral);
        tvDetalleCantidadPollos = findViewById(R.id.tvDetalleCantidadPollos);
        tvDetalleFechaInicio = findViewById(R.id.tvDetalleFechaInicio);
        tvDetalleEdadLote = findViewById(R.id.tvDetalleEdadLote);
        tvDetalleMortalidad = findViewById(R.id.tvDetalleMortalidad);
        tvDetallePesoPromedio = findViewById(R.id.tvDetallePesoPromedio);
        tvDetalleConsumoAcumulado = findViewById(R.id.tvDetalleConsumoAcumulado);
        tvDetalleTipoAlimentacion = findViewById(R.id.tvDetalleTipoAlimentacion);
        tvDetalleEstadoCorral = findViewById(R.id.tvDetalleEstadoCorral);
        tvDetallePollosVivos = findViewById(R.id.tvDetallePollosVivos);
        tvCostoInicialPollos = findViewById(R.id.tvCostoInicialPollos);
        tvPrecioPromedioKilo = findViewById(R.id.tvPrecioPromedioKilo);
        tvGastoTotalAlimento = findViewById(R.id.tvGastoTotalAlimento);
        tvPollosVendidos = findViewById(R.id.tvPollosVendidos);
        tvGananciaTotal = findViewById(R.id.tvGananciaTotal);
        tvGananciaNeta = findViewById(R.id.tvGananciaNeta);
        btnAbrirDialogo = findViewById(R.id.btnAbrirDialogo);
        btnEliminarCorral = findViewById(R.id.btnEliminarCorral);
    }

    private void mostrarDialogoConfirmacion() {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar Corral")
                .setMessage("¿Estás seguro de que quieres eliminar este corral? Esta acción no se puede deshacer.")
                .setPositiveButton("Sí, eliminar", (dialog, which) -> eliminarCorral())
                .setNegativeButton("No", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void eliminarCorral() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            db.delete("Corral", "id = ?", new String[]{String.valueOf(corralId)});
            Toast.makeText(this, "Corral eliminado correctamente", Toast.LENGTH_SHORT).show();
            finish();
        } finally {
            db.close();
        }
    }

    @SuppressLint("SetTextI18n")
    private void cargarDetallesDelCorral() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try (Cursor cursor = db.rawQuery("SELECT * FROM Corral WHERE id = ?", new String[]{String.valueOf(corralId)})) {

            if (cursor.moveToFirst()) {
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
                int cantidadInicial = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad_pollos"));
                double costoInicialPollos = cursor.getDouble(cursor.getColumnIndexOrThrow("precio_pollos"));
                int mortalidad = cursor.getInt(cursor.getColumnIndexOrThrow("mortalidad_total"));
                String fechaCompraStr = cursor.getString(cursor.getColumnIndexOrThrow("fecha_compra"));
                double consumoTotal = cursor.getDouble(cursor.getColumnIndexOrThrow("consumo_acumulado"));
                double precioKilo = cursor.getDouble(cursor.getColumnIndexOrThrow("precio_kilo_promedio"));
                int pollosVendidos = cursor.getInt(cursor.getColumnIndexOrThrow("pollos_vendidos"));
                double gananciaBruta = cursor.getDouble(cursor.getColumnIndexOrThrow("ganancia_total"));

                int pollosVivos = cantidadInicial - mortalidad - pollosVendidos;
                double gastoTotalAlimento = consumoTotal * precioKilo;
                double gananciaNeta = gananciaBruta - gastoTotalAlimento - costoInicialPollos;

                long edadLote = -1;
                if (fechaCompraStr != null && !fechaCompraStr.isEmpty()) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d", Locale.getDefault());
                        Date fechaInicio = sdf.parse(fechaCompraStr);
                        if (fechaInicio != null) {
                            long diffInMillis = new Date().getTime() - fechaInicio.getTime();
                            edadLote = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);
                        }
                    } catch (ParseException e) {
                        edadLote = -1;
                    }
                }

                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setTitle(nombre);
                    actionBar.setDisplayHomeAsUpEnabled(true);
                }

                tvDetalleNombreCorral.setText(nombre);
                tvDetalleCantidadPollos.setText("Cantidad Inicial: " + cantidadInicial);
                tvDetalleMortalidad.setText("Mortalidad: " + mortalidad);
                tvDetallePollosVivos.setText("Pollos Vivos: " + pollosVivos);
                tvDetalleFechaInicio.setText("Fecha de Inicio: " + fechaCompraStr);

                if (edadLote >= 0) {
                    tvDetalleEdadLote.setText("Edad del lote: " + edadLote + " días");
                } else {
                    tvDetalleEdadLote.setText("Edad del lote: (Fecha inválida)");
                }

                tvCostoInicialPollos.setText("Costo Pollos: " + String.format(Locale.getDefault(), "$%,.0f", costoInicialPollos));
                tvPrecioPromedioKilo.setText("Precio Kilo de purina: " + (precioKilo > 0 ? String.format(Locale.getDefault(), "$%,.2f", precioKilo) : "N/A"));
                tvGastoTotalAlimento.setText("Gasto total en Alimento: " + (gastoTotalAlimento > 0 ? String.format(Locale.getDefault(), "$%,.2f", gastoTotalAlimento) : "N/A"));
                tvPollosVendidos.setText("Pollos Vendidos: " + pollosVendidos);
                tvGananciaTotal.setText("Ganancia de Venta: " + String.format(Locale.getDefault(), "$%,.2f", gananciaBruta));
                tvGananciaNeta.setText("Ganancia Neta: " + String.format(Locale.getDefault(), "$%,.2f", gananciaNeta));

                double peso = cursor.getDouble(cursor.getColumnIndexOrThrow("peso_promedio"));
                tvDetallePesoPromedio.setText("Peso promedio del lote: " + (peso > 0 ? String.format(Locale.getDefault(), "%.2f kg", peso) : "N/A"));
                String tipoAlimentacion = cursor.getString(cursor.getColumnIndexOrThrow("tipo_alimentacion"));
                tvDetalleTipoAlimentacion.setText("Tipo de alimentación: " + (tipoAlimentacion != null ? tipoAlimentacion : "N/A"));
                String estadoCorral = cursor.getString(cursor.getColumnIndexOrThrow("estado_corral"));
                tvDetalleEstadoCorral.setText("Estado del corral: " + (estadoCorral != null ? estadoCorral : "N/A"));

            } else {
                Toast.makeText(this, "Este corral ya no existe.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}

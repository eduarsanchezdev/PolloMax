package com.example.pollomax;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class IngresosActivity extends AppCompatActivity {

    private TextView tvIngresoTituloCorral, tvIngresoTotalVenta;
    private EditText etCantidadPollosVendidos, etPesoTotalVenta, etPrecioVentaKilo;
    private Button btnCalcularIngreso, btnConfirmarVenta;

    private BaseDeDatos dbHelper;
    private int corralId;
    private int cantidadVendida = 0;
    private double ingresoTotalVenta = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingresos);

        dbHelper = new BaseDeDatos(this);

        tvIngresoTituloCorral = findViewById(R.id.tvIngresoTituloCorral);
        tvIngresoTotalVenta = findViewById(R.id.tvIngresoTotalVenta);
        etCantidadPollosVendidos = findViewById(R.id.etCantidadPollosVendidos);
        etPesoTotalVenta = findViewById(R.id.etPesoTotalVenta);
        etPrecioVentaKilo = findViewById(R.id.etPrecioVentaKilo);
        btnCalcularIngreso = findViewById(R.id.btnCalcularIngreso);
        btnConfirmarVenta = findViewById(R.id.btnConfirmarVenta);

        corralId = getIntent().getIntExtra("CORRAL_ID", -1);
        String nombreCorral = getIntent().getStringExtra("NOMBRE_CORRAL");

        if (corralId == -1) {
            Toast.makeText(this, "Error: No se seleccionó un corral", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvIngresoTituloCorral.setText("Registrar Venta para: " + nombreCorral);

        btnCalcularIngreso.setOnClickListener(v -> calcularIngreso());
        btnConfirmarVenta.setOnClickListener(v -> registrarVenta());
    }

    private void calcularIngreso() {
        String cantidadStr = etCantidadPollosVendidos.getText().toString();
        String pesoTotalStr = etPesoTotalVenta.getText().toString();
        String precioKiloStr = etPrecioVentaKilo.getText().toString();

        if (cantidadStr.isEmpty() || pesoTotalStr.isEmpty() || precioKiloStr.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int cantidadAVender = Integer.parseInt(cantidadStr);

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int pollosVivos = 0;
        try (Cursor cursor = db.rawQuery("SELECT cantidad_pollos, mortalidad_total, pollos_vendidos FROM Corral WHERE id = ?", new String[]{String.valueOf(corralId)})) {
            if (cursor.moveToFirst()) {
                int cantidadInicial = cursor.getInt(0);
                int mortalidadActual = cursor.getInt(1);
                int pollosYaVendidos = cursor.getInt(2);
                pollosVivos = cantidadInicial - mortalidadActual - pollosYaVendidos;
            }
        }

        if (cantidadAVender > pollosVivos) {
            Toast.makeText(this, "Error: No puedes vender más pollos de los que tienes (vivos: " + pollosVivos + ")", Toast.LENGTH_LONG).show();
            return; // Detener el proceso
        }
        this.cantidadVendida = cantidadAVender;
        double pesoTotal = Double.parseDouble(pesoTotalStr);
        double precioKilo = Double.parseDouble(precioKiloStr);
        this.ingresoTotalVenta = pesoTotal * precioKilo;
        
        tvIngresoTotalVenta.setText(String.format(Locale.getDefault(), "Ingreso Total: $%,.2f", ingresoTotalVenta));
        tvIngresoTotalVenta.setVisibility(View.VISIBLE);
        btnConfirmarVenta.setVisibility(View.VISIBLE);
    }

    private void registrarVenta() {
        if (cantidadVendida > 0) {
            actualizarBaseDeDatos(cantidadVendida, ingresoTotalVenta);
            Toast.makeText(this, "Venta registrada y corral actualizado", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "No se ha calculado un ingreso válido", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void actualizarBaseDeDatos(int cantidadVendida, double ingresoVenta) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            db.execSQL("UPDATE Corral SET pollos_vendidos = pollos_vendidos + ? WHERE id = ?", new Object[]{cantidadVendida, corralId});
            db.execSQL("UPDATE Corral SET ganancia_total = ganancia_total + ? WHERE id = ?", new Object[]{ingresoVenta, corralId});
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }
}

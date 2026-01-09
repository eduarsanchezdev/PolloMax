package com.example.pollomax;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AgregarInfoCorralActivity extends AppCompatActivity {
    private EditText etMortalidad, etPesoPromedio, etConsumoAcumulado;
    private Spinner spinnerTipoAlimentacion, spinnerEstadoCorral;
    private Button btnGuardarInfoCorral;

    private BaseDeDatos dbHelper;
    private int corralId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_info_corral);

        dbHelper = new BaseDeDatos(this);

        etMortalidad = findViewById(R.id.etMortalidad);
        etPesoPromedio = findViewById(R.id.etPesoPromedio);
        etConsumoAcumulado = findViewById(R.id.etConsumoAcumulado);
        spinnerTipoAlimentacion = findViewById(R.id.spinnerTipoAlimentacion);
        spinnerEstadoCorral = findViewById(R.id.spinnerEstadoCorral);
        btnGuardarInfoCorral = findViewById(R.id.btnGuardarInfoCorral);

        corralId = getIntent().getIntExtra("CORRAL_ID", -1);
        if (corralId == -1) {
            Toast.makeText(this, "Error: Corral no encontrado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        cargarSpinners();

        btnGuardarInfoCorral.setOnClickListener(v -> guardarInformacion());
    }

    private void cargarSpinners() {
        String[] tipos = {"Inicial", "Levante", "Engorde", "Final", "Mixta"};
        ArrayAdapter<String> adapterTipo = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tipos);
        adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoAlimentacion.setAdapter(adapterTipo);

        String[] estados = {"Excelente", "Bueno", "Regular", "Crítico"};
        ArrayAdapter<String> adapterEstado = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, estados);
        adapterEstado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstadoCorral.setAdapter(adapterEstado);
    }

    private void guardarInformacion() {
        String mortalidadStr = etMortalidad.getText().toString().trim();
        String pesoStr = etPesoPromedio.getText().toString().trim();
        String consumoStr = etConsumoAcumulado.getText().toString().trim();

        if (mortalidadStr.isEmpty() && pesoStr.isEmpty() && consumoStr.isEmpty()) {
            Toast.makeText(this, "Debes ingresar al menos un dato", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        
        // Obtener los datos actuales del corral
        int cantidadInicial = 0, mortalidadActual = 0, pollosVendidos = 0;
        try (Cursor cursor = db.rawQuery("SELECT cantidad_pollos, mortalidad_total, pollos_vendidos FROM Corral WHERE id = ?", new String[]{String.valueOf(corralId)})) {
            if (cursor.moveToFirst()) {
                cantidadInicial = cursor.getInt(0);
                mortalidadActual = cursor.getInt(1);
                pollosVendidos = cursor.getInt(2);
            }
        }

        int pollosVivos = cantidadInicial - mortalidadActual - pollosVendidos;
        int nuevaMortalidad = mortalidadStr.isEmpty() ? 0 : Integer.parseInt(mortalidadStr);

        // Validar la mortalidad
        if (nuevaMortalidad > pollosVivos) {
            Toast.makeText(this, "Error: La mortalidad no puede ser mayor que los pollos vivos (" + pollosVivos + ")", Toast.LENGTH_LONG).show();
            return;
        }
        
        db.beginTransaction();
        try {
            // Actualizar mortalidad
            if (!mortalidadStr.isEmpty()) {
                db.execSQL("UPDATE Corral SET mortalidad_total = mortalidad_total + ? WHERE id = ?", new Object[]{nuevaMortalidad, corralId});
            }

            // Actualizar consumo
            if (!consumoStr.isEmpty()) {
                double nuevoConsumo = Double.parseDouble(consumoStr);
                db.execSQL("UPDATE Corral SET consumo_acumulado = consumo_acumulado + ? WHERE id = ?", new Object[]{nuevoConsumo, corralId});
            }

            // Actualizar peso
            if (!pesoStr.isEmpty()) {
                double nuevoPeso = Double.parseDouble(pesoStr);
                db.execSQL("UPDATE Corral SET peso_promedio = ? WHERE id = ?", new Object[]{nuevoPeso, corralId});
            }

            // Actualizar spinners
            String tipoAlimento = spinnerTipoAlimentacion.getSelectedItem().toString();
            String estadoCorral = spinnerEstadoCorral.getSelectedItem().toString();
            db.execSQL("UPDATE Corral SET tipo_alimentacion = ?, estado_corral = ? WHERE id = ?", new Object[]{tipoAlimento, estadoCorral, corralId});

            db.setTransactionSuccessful();
            Toast.makeText(this, "Información guardada correctamente", Toast.LENGTH_SHORT).show();
            finish();

        } finally {
            db.endTransaction();
            db.close();
        }
    }
}

package com.example.pollomax;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class CrearCorral extends AppCompatActivity {
    EditText etNombreCorral, etCantidadPollos, etPrecioPollos, etFechaCompraPollos;
    Button btnGuardar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_crear_corral);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etNombreCorral = findViewById(R.id.etNombreCorral);
        etCantidadPollos = findViewById(R.id.etCantidadPollos);
        etPrecioPollos = findViewById(R.id.etPrecioPollos);
        etFechaCompraPollos = findViewById(R.id.etFechaCompraPollos);
        btnGuardar = findViewById(R.id.btnGuardar);

        etFechaCompraPollos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        CrearCorral.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                String fechaSeleccionada = year + "-" + (month + 1) + "-" + dayOfMonth;
                                etFechaCompraPollos.setText(fechaSeleccionada);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nombre = etNombreCorral.getText().toString().trim();
                String cantidadStr = etCantidadPollos.getText().toString().trim();
                String precioPollosStr = etPrecioPollos.getText().toString().trim();
                String fechaCompraStr = etFechaCompraPollos.getText().toString().trim();

                if (nombre.isEmpty() || cantidadStr.isEmpty() || precioPollosStr.isEmpty() || fechaCompraStr.isEmpty()) {
                    Toast.makeText(CrearCorral.this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                int cantidadPollos = Integer.parseInt(cantidadStr);
                double precioPollos = Double.parseDouble(precioPollosStr);

                // Guardar en la base de datos
                BaseDeDatos dbHelper = new BaseDeDatos(CrearCorral.this);
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                String insertQuery = "INSERT INTO Corral (nombre, cantidad_pollos, precio_pollos, fecha_compra) VALUES ('" +
                        nombre + "', " + cantidadPollos + ", " + precioPollos + ", '" + fechaCompraStr + "')";
                db.execSQL(insertQuery);
                db.close();

                Toast.makeText(CrearCorral.this, "Corral guardado correctamente", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}

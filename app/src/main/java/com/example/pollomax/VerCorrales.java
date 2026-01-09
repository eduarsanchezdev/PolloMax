package com.example.pollomax;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class VerCorrales extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_corrales);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        BaseDeDatos dbHelper = new BaseDeDatos(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        List<Corral> corrales = new ArrayList<>();
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
                    corrales.add(corral);
                } while (cursor.moveToNext());
            }
        }

        db.close();

        CorralAdapter adapter = new CorralAdapter(this, corrales);
        recyclerView.setAdapter(adapter);
    }
}

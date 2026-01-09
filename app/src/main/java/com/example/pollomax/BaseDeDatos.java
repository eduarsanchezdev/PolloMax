package com.example.pollomax;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BaseDeDatos extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "PollosMax.db";
    // Incrementar la versión de la base de datos
    private static final int DATABASE_VERSION = 2;

    public BaseDeDatos(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Añadir las nuevas columnas a la sentencia de creación
        String createTableCorral = "CREATE TABLE Corral (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "nombre TEXT," +
                "cantidad_pollos INTEGER," +
                "precio_pollos REAL," +
                "fecha_compra TEXT," +
                "mortalidad_total INTEGER DEFAULT 0," +
                "peso_promedio REAL DEFAULT 0," +
                "consumo_acumulado REAL DEFAULT 0," +
                "tipo_alimentacion TEXT DEFAULT 'N/A'," +
                "estado_corral TEXT DEFAULT 'N/A'," +
                "precio_kilo_promedio REAL DEFAULT 0," +
                "pollos_vendidos INTEGER DEFAULT 0," +
                "ganancia_total REAL DEFAULT 0)";
        db.execSQL(createTableCorral);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Implementar una actualización no destructiva
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE Corral ADD COLUMN precio_kilo_promedio REAL DEFAULT 0");
            db.execSQL("ALTER TABLE Corral ADD COLUMN pollos_vendidos INTEGER DEFAULT 0");
            db.execSQL("ALTER TABLE Corral ADD COLUMN ganancia_total REAL DEFAULT 0");
        }
    }
}

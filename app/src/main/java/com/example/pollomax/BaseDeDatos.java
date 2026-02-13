package com.example.pollomax;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BaseDeDatos extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "PollosMax.db";
    // Incrementar la versión de la base de datos para añadir gasto_insumos
    private static final int DATABASE_VERSION = 3;

    public BaseDeDatos(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
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
                "ganancia_total REAL DEFAULT 0," +
                "gasto_insumos REAL DEFAULT 0)"; // Nueva columna
        db.execSQL(createTableCorral);

        String createTableEventos = "CREATE TABLE Eventos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "descripcion TEXT)";
        db.execSQL(createTableEventos);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE Corral ADD COLUMN precio_kilo_promedio REAL DEFAULT 0");
            db.execSQL("ALTER TABLE Corral ADD COLUMN pollos_vendidos INTEGER DEFAULT 0");
            db.execSQL("ALTER TABLE Corral ADD COLUMN ganancia_total REAL DEFAULT 0");
        }
        if (oldVersion < 3) {
            // Añadir gasto_insumos y crear tabla Eventos si no existe
            db.execSQL("ALTER TABLE Corral ADD COLUMN gasto_insumos REAL DEFAULT 0");
            db.execSQL("CREATE TABLE IF NOT EXISTS Eventos (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "descripcion TEXT)");
        }
    }
}

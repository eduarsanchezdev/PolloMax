package com.example.pollomax;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SoporteActivity extends AppCompatActivity {

    private Button btnWhatsApp, btnLlamar, btnCorreo;

    // Información de contacto
    private final String NUMERO_TELEFONO = "+573134255057";
    private final String CORREO_ELECTRONICO = "Eduarsanchez.dev@gmail.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soporte);

        // Vincular los botones
        btnWhatsApp = findViewById(R.id.btnWhatsApp);
        btnLlamar = findViewById(R.id.btnLlamar);
        btnCorreo = findViewById(R.id.btnCorreo);

        // Configurar los listeners para cada botón
        configurarListeners();
    }

    private void configurarListeners() {
        // 1. Botón de WhatsApp
        btnWhatsApp.setOnClickListener(v -> {
            // Formatear el número para el enlace de WhatsApp (solo dígitos)
            String numeroParaUrl = NUMERO_TELEFONO.replace("+", "").trim();
            String url = "https://api.whatsapp.com/send?phone=" + numeroParaUrl;

            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "No tienes WhatsApp instalado", Toast.LENGTH_SHORT).show();
            }
        });

        // 2. Botón de Llamada
        btnLlamar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + NUMERO_TELEFONO));
            startActivity(intent);
        });

        // 3. Botón de Correo Electrónico
        btnCorreo.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:")); // Solo apps de correo deben manejar esto
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{CORREO_ELECTRONICO});
            intent.putExtra(Intent.EXTRA_SUBJECT, "Soporte App PolloMax"); // Asunto opcional

            // Verificar si hay una app de correo que pueda manejar el intent
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, "No tienes una aplicación de correo instalada", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

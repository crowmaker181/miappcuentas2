package com.example.miappcuentas;

import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.miappcuentas.databinding.ActivityAjustesBinding;
import com.example.miappcuentas.datos.PreferenciasApariencia;
import com.example.miappcuentas.util.FormatoMoneda;

public class AjustesActivity extends AppCompatActivity {

    private ActivityAjustesBinding binding;
    private PreferenciasApariencia preferencias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityAjustesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.ajustesRoot, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setSupportActionBar(binding.toolbarAjustes);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        preferencias = new PreferenciasApariencia(this);
        pintarPaletas();
        actualizarVistaPrevia();
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }

    private void pintarPaletas() {
        llenarPaleta(binding.paletaIngresos, PreferenciasApariencia.PALETA_INGRESOS,
                preferencias.getColorIngreso(), color -> {
                    preferencias.setColorIngreso(color);
                    pintarPaletas();
                    actualizarVistaPrevia();
                    Toast.makeText(this, R.string.mensaje_color_guardado, Toast.LENGTH_SHORT).show();
                });

        llenarPaleta(binding.paletaGastos, PreferenciasApariencia.PALETA_GASTOS,
                preferencias.getColorGasto(), color -> {
                    preferencias.setColorGasto(color);
                    pintarPaletas();
                    actualizarVistaPrevia();
                    Toast.makeText(this, R.string.mensaje_color_guardado, Toast.LENGTH_SHORT).show();
                });

        llenarPaleta(binding.paletaTema, PreferenciasApariencia.PALETA_TEMA,
                preferencias.getColorTema(), color -> {
                    preferencias.setColorTema(color);
                    pintarPaletas();
                    actualizarVistaPrevia();
                    Toast.makeText(this, R.string.mensaje_color_guardado, Toast.LENGTH_SHORT).show();
                });
    }

    private void llenarPaleta(LinearLayout contenedor, int[] paleta, int colorSeleccionado,
                              OnColorElegido listener) {
        contenedor.removeAllViews();
        int tamano = (int) (48 * getResources().getDisplayMetrics().density);
        int margen = (int) (8 * getResources().getDisplayMetrics().density);

        for (int color : paleta) {
            View circulo = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(tamano, tamano);
            params.setMargins(0, 0, margen, 0);
            params.gravity = Gravity.CENTER_VERTICAL;
            circulo.setLayoutParams(params);

            GradientDrawable fondo = new GradientDrawable();
            fondo.setShape(GradientDrawable.OVAL);
            fondo.setColor(color);
            if (color == colorSeleccionado) {
                fondo.setStroke((int) (3 * getResources().getDisplayMetrics().density), 0xFF111827);
            }
            circulo.setBackground(fondo);
            circulo.setOnClickListener(v -> listener.onColor(color));
            contenedor.addView(circulo);
        }
    }

    private void actualizarVistaPrevia() {
        binding.tarjetaVistaPrevia.setCardBackgroundColor(preferencias.getColorTema());
        binding.textoVistaSaldo.setText(FormatoMoneda.formatear(25000000));
        binding.textoVistaIngreso.setText(FormatoMoneda.formatear(16000));
        binding.textoVistaGasto.setText(FormatoMoneda.formatear(2500000));
        binding.textoVistaIngreso.setTextColor(preferencias.getColorIngresoClaro());
        binding.textoVistaGasto.setTextColor(preferencias.getColorGastoClaro());
    }

    private interface OnColorElegido {
        void onColor(int color);
    }
}

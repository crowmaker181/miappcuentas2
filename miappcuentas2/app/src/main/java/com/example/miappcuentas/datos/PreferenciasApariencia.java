package com.example.miappcuentas.datos;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

import androidx.core.graphics.ColorUtils;

/**
 * Guarda los colores personalizados de la interfaz.
 */
public class PreferenciasApariencia {

    private static final String PREFS = "apariencia";
    private static final String CLAVE_COLOR_INGRESO = "color_ingreso";
    private static final String CLAVE_COLOR_GASTO = "color_gasto";
    private static final String CLAVE_COLOR_TEMA = "color_tema";

    public static final int COLOR_INGRESO_DEFECTO = Color.parseColor("#15803D");
    public static final int COLOR_GASTO_DEFECTO = Color.parseColor("#DC2626");
    public static final int COLOR_TEMA_DEFECTO = Color.parseColor("#0F766E");

    public static final int[] PALETA_INGRESOS = {
            Color.parseColor("#15803D"),
            Color.parseColor("#0EA5E9"),
            Color.parseColor("#2563EB"),
            Color.parseColor("#7C3AED"),
            Color.parseColor("#0F766E")
    };

    public static final int[] PALETA_GASTOS = {
            Color.parseColor("#DC2626"),
            Color.parseColor("#EA580C"),
            Color.parseColor("#DB2777"),
            Color.parseColor("#CA8A04"),
            Color.parseColor("#7C2D12")
    };

    public static final int[] PALETA_TEMA = {
            Color.parseColor("#0F766E"),
            Color.parseColor("#1E3A8A"),
            Color.parseColor("#6D28D9"),
            Color.parseColor("#334155"),
            Color.parseColor("#166534")
    };

    private final SharedPreferences preferencias;

    public PreferenciasApariencia(Context context) {
        preferencias = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public int getColorIngreso() {
        return preferencias.getInt(CLAVE_COLOR_INGRESO, COLOR_INGRESO_DEFECTO);
    }

    public int getColorGasto() {
        return preferencias.getInt(CLAVE_COLOR_GASTO, COLOR_GASTO_DEFECTO);
    }

    public int getColorTema() {
        return preferencias.getInt(CLAVE_COLOR_TEMA, COLOR_TEMA_DEFECTO);
    }

    public void setColorIngreso(int color) {
        preferencias.edit().putInt(CLAVE_COLOR_INGRESO, color).apply();
    }

    public void setColorGasto(int color) {
        preferencias.edit().putInt(CLAVE_COLOR_GASTO, color).apply();
    }

    public void setColorTema(int color) {
        preferencias.edit().putInt(CLAVE_COLOR_TEMA, color).apply();
    }

    public int getColorIngresoClaro() {
        return ColorUtils.blendARGB(getColorIngreso(), Color.WHITE, 0.55f);
    }

    public int getColorGastoClaro() {
        return ColorUtils.blendARGB(getColorGasto(), Color.WHITE, 0.55f);
    }
}

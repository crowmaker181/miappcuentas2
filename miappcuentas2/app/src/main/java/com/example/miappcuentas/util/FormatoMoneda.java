package com.example.miappcuentas.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Muestra montos con punto de miles, por ejemplo 16.000 o 25.000.000.
 */
public final class FormatoMoneda {

    private static final DecimalFormat FORMATO_LISTA;
    private static final DecimalFormat FORMATO_EDICION;

    static {
        DecimalFormatSymbols simbolos = new DecimalFormatSymbols(new Locale("es", "CO"));
        simbolos.setGroupingSeparator('.');
        simbolos.setDecimalSeparator(',');

        FORMATO_LISTA = new DecimalFormat("#,##0.##", simbolos);
        FORMATO_LISTA.setGroupingUsed(true);
        FORMATO_LISTA.setGroupingSize(3);

        DecimalFormatSymbols simbolosEdicion = new DecimalFormatSymbols(Locale.US);
        FORMATO_EDICION = new DecimalFormat("0.##", simbolosEdicion);
    }

    private FormatoMoneda() {
    }

    public static String formatear(double monto) {
        return "$ " + FORMATO_LISTA.format(redondear(monto));
    }

    public static String formatearParaEdicion(double monto) {
        return FORMATO_EDICION.format(redondear(monto));
    }

    public static double parsear(String texto) {
        String normalizado = texto.trim()
                .replace(" ", "")
                .replace("$", "")
                .replace("\u00A0", "");

        if (normalizado.isEmpty()) {
            throw new NumberFormatException("monto vacío");
        }

        int ultimaComa = normalizado.lastIndexOf(',');
        int ultimoPunto = normalizado.lastIndexOf('.');
        int ultimoSeparador = Math.max(ultimaComa, ultimoPunto);

        if (ultimoSeparador >= 0) {
            String decimales = normalizado.substring(ultimoSeparador + 1);
            if (decimales.length() > 0 && decimales.length() <= 2) {
                String enteros = normalizado.substring(0, ultimoSeparador)
                        .replace(".", "")
                        .replace(",", "");
                if (enteros.isEmpty()) {
                    enteros = "0";
                }
                normalizado = enteros + "." + decimales;
            } else {
                normalizado = normalizado.replace(".", "").replace(",", "");
            }
        }

        return redondear(Double.parseDouble(normalizado));
    }

    public static double redondear(double monto) {
        return Math.round(monto * 100.0) / 100.0;
    }
}

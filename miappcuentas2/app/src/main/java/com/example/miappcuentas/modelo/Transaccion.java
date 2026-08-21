package com.example.miappcuentas.modelo;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.Date;

/**
 * POJO de Firestore para un movimiento financiero.
 */
@IgnoreExtraProperties
public class Transaccion {

    public static final int TIPO_GASTO = 0;
    public static final int TIPO_INGRESO = 1;

    private String concepto;
    private double monto;
    private int tipo;
    private Date fechaCreacion;

    @Exclude
    private String documentId;

    public Transaccion() {
    }

    public Transaccion(String concepto, double monto, int tipo) {
        this.concepto = concepto;
        this.monto = monto;
        this.tipo = tipo;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public Date getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Date fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    @Exclude
    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    @Exclude
    public boolean esIngreso() {
        return tipo == TIPO_INGRESO;
    }
}

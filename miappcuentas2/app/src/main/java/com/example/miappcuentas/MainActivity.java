package com.example.miappcuentas;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.miappcuentas.databinding.ActivityMainBinding;
import com.example.miappcuentas.datos.PreferenciasApariencia;
import com.example.miappcuentas.datos.TransaccionRepositorio;
import com.example.miappcuentas.modelo.Transaccion;
import com.example.miappcuentas.ui.TransaccionAdapter;
import com.example.miappcuentas.util.FormatoMoneda;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TransaccionAdapter.OnTransaccionListener {

    private ActivityMainBinding binding;
    private TransaccionRepositorio repositorio;
    private PreferenciasApariencia preferencias;
    private TransaccionAdapter adaptador;
    private ListenerRegistration registroListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setSupportActionBar(binding.toolbar);

        repositorio = new TransaccionRepositorio();
        preferencias = new PreferenciasApariencia(this);
        adaptador = new TransaccionAdapter(this);

        binding.recyclerTransacciones.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerTransacciones.setAdapter(adaptador);

        binding.fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, FormularioActivity.class);
            startActivity(intent);
        });

        binding.botonColores.setOnClickListener(view ->
                startActivity(new Intent(MainActivity.this, AjustesActivity.class))
        );

        escucharTransaccionesEnTiempoReal();
    }

    @Override
    protected void onResume() {
        super.onResume();
        aplicarColores();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (registroListener != null) {
            registroListener.remove();
            registroListener = null;
        }
    }

    @Override
    public void onEditar(Transaccion transaccion) {
        Intent intent = new Intent(this, FormularioActivity.class);
        intent.putExtra(FormularioActivity.EXTRA_ID_TRANSACCION, transaccion.getDocumentId());
        startActivity(intent);
    }

    @Override
    public void onEliminar(Transaccion transaccion) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.titulo_eliminar)
                .setMessage(getString(R.string.mensaje_eliminar, transaccion.getConcepto()))
                .setPositiveButton(R.string.accion_eliminar, (dialog, which) ->
                        repositorio.eliminar(transaccion.getDocumentId(), tarea -> {
                            if (!tarea.isSuccessful()) {
                                Toast.makeText(this, R.string.error_firestore, Toast.LENGTH_LONG).show();
                            }
                        }))
                .setNegativeButton(R.string.accion_cancelar, null)
                .show();
    }

    private void escucharTransaccionesEnTiempoReal() {
        registroListener = repositorio.escucharTodas((snapshots, error) -> {
            if (error != null) {
                String detalle = error.getMessage() == null ? error.toString() : error.getMessage();
                new AlertDialog.Builder(this)
                        .setTitle(R.string.error_firestore)
                        .setMessage(detalle)
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
                return;
            }
            if (snapshots == null) {
                return;
            }

            List<Transaccion> transacciones = new ArrayList<>();
            for (DocumentSnapshot documento : snapshots.getDocuments()) {
                Transaccion transaccion = documento.toObject(Transaccion.class);
                if (transaccion != null) {
                    transaccion.setDocumentId(documento.getId());
                    transacciones.add(transaccion);
                }
            }

            adaptador.actualizarLista(transacciones);
            actualizarResumen(transacciones);

            boolean listaVacia = transacciones.isEmpty();
            binding.textoListaVacia.setVisibility(listaVacia ? View.VISIBLE : View.GONE);
            binding.recyclerTransacciones.setVisibility(listaVacia ? View.GONE : View.VISIBLE);
        });
    }

    private void actualizarResumen(List<Transaccion> transacciones) {
        double totalIngresos = 0;
        double totalGastos = 0;

        for (Transaccion transaccion : transacciones) {
            if (transaccion.esIngreso()) {
                totalIngresos += transaccion.getMonto();
            } else {
                totalGastos += transaccion.getMonto();
            }
        }

        double saldo = totalIngresos - totalGastos;
        binding.textoSaldo.setText(FormatoMoneda.formatear(saldo));
        binding.textoTotalIngresos.setText(FormatoMoneda.formatear(totalIngresos));
        binding.textoTotalGastos.setText(FormatoMoneda.formatear(totalGastos));
    }

    private void aplicarColores() {
        int colorTema = preferencias.getColorTema();
        binding.tarjetaResumen.setCardBackgroundColor(colorTema);
        binding.fab.setBackgroundTintList(ColorStateList.valueOf(colorTema));
        binding.textoTotalIngresos.setTextColor(preferencias.getColorIngresoClaro());
        binding.textoTotalGastos.setTextColor(preferencias.getColorGastoClaro());
        adaptador.actualizarColores(preferencias.getColorIngreso(), preferencias.getColorGasto());
    }
}

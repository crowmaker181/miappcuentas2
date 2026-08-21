package com.example.miappcuentas;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.miappcuentas.databinding.ActivityFormularioBinding;
import com.example.miappcuentas.datos.PreferenciasApariencia;
import com.example.miappcuentas.datos.TransaccionRepositorio;
import com.example.miappcuentas.modelo.Transaccion;
import com.example.miappcuentas.util.FormatoMoneda;

public class FormularioActivity extends AppCompatActivity {

    public static final String EXTRA_ID_TRANSACCION = "extra_id_transaccion";
    public static final int LONGITUD_MINIMA_CONCEPTO = 3;

    private ActivityFormularioBinding binding;
    private TransaccionRepositorio repositorio;
    private String idTransaccion;
    private boolean guardando;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        binding = ActivityFormularioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.formularioRoot, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setSupportActionBar(binding.toolbarFormulario);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        repositorio = new TransaccionRepositorio();
        idTransaccion = getIntent().getStringExtra(EXTRA_ID_TRANSACCION);

        if (!TextUtils.isEmpty(idTransaccion)) {
            binding.toolbarFormulario.setTitle(R.string.titulo_editar_transaccion);
            cargarTransaccion(idTransaccion);
        } else {
            binding.toolbarFormulario.setTitle(R.string.titulo_nueva_transaccion);
            binding.radioIngreso.setChecked(true);
        }

        binding.inputMonto.setKeyListener(DigitsKeyListener.getInstance("0123456789.,"));
        configurarValidacionesEnTiempoReal();
        binding.botonGuardar.setOnClickListener(v -> guardarTransaccion());

        int colorTema = new PreferenciasApariencia(this).getColorTema();
        binding.botonGuardar.setBackgroundTintList(ColorStateList.valueOf(colorTema));
    }

    @Override
    public boolean onSupportNavigateUp() {
        getOnBackPressedDispatcher().onBackPressed();
        return true;
    }

    private void configurarValidacionesEnTiempoReal() {
        binding.inputConcepto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validarConcepto(s.toString().trim(), false);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        binding.inputMonto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validarMonto(s.toString().trim(), false);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private boolean validarConcepto(String concepto, boolean alGuardar) {
        if (TextUtils.isEmpty(concepto)) {
            if (alGuardar) {
                binding.layoutConcepto.setError(getString(R.string.error_concepto_vacio));
            }
            return false;
        }
        if (concepto.length() < LONGITUD_MINIMA_CONCEPTO) {
            binding.layoutConcepto.setError(getString(R.string.error_concepto_corto));
            return false;
        }
        binding.layoutConcepto.setError(null);
        return true;
    }

    private boolean validarMonto(String montoTexto, boolean alGuardar) {
        if (TextUtils.isEmpty(montoTexto)) {
            if (alGuardar) {
                binding.layoutMonto.setError(getString(R.string.error_monto_vacio));
            } else {
                binding.layoutMonto.setError(null);
            }
            return false;
        }

        double monto;
        try {
            monto = FormatoMoneda.parsear(montoTexto);
        } catch (NumberFormatException e) {
            binding.layoutMonto.setError(getString(R.string.error_monto_invalido));
            return false;
        }

        if (monto <= 0) {
            binding.layoutMonto.setError(getString(R.string.error_monto_cero));
            return false;
        }

        binding.layoutMonto.setError(null);
        return true;
    }

    private void cargarTransaccion(String documentId) {
        repositorio.leerPorId(documentId, tarea -> {
            if (!tarea.isSuccessful() || tarea.getResult() == null || !tarea.getResult().exists()) {
                Toast.makeText(this, R.string.error_transaccion_no_encontrada, Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            Transaccion transaccion = tarea.getResult().toObject(Transaccion.class);
            if (transaccion == null) {
                Toast.makeText(this, R.string.error_transaccion_no_encontrada, Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            binding.inputConcepto.setText(transaccion.getConcepto());
            binding.inputMonto.setText(FormatoMoneda.formatearParaEdicion(transaccion.getMonto()));

            if (transaccion.esIngreso()) {
                binding.radioIngreso.setChecked(true);
            } else {
                binding.radioGasto.setChecked(true);
            }
        });
    }

    private void guardarTransaccion() {
        if (guardando) {
            return;
        }

        String concepto = binding.inputConcepto.getText() == null
                ? ""
                : binding.inputConcepto.getText().toString().trim();
        String montoTexto = binding.inputMonto.getText() == null
                ? ""
                : binding.inputMonto.getText().toString().trim();

        boolean conceptoValido = validarConcepto(concepto, true);
        boolean montoValido = validarMonto(montoTexto, true);
        if (!conceptoValido || !montoValido) {
            return;
        }

        double monto = FormatoMoneda.parsear(montoTexto);
        int tipo = binding.radioIngreso.isChecked()
                ? Transaccion.TIPO_INGRESO
                : Transaccion.TIPO_GASTO;

        Transaccion transaccion = new Transaccion(concepto, FormatoMoneda.redondear(monto), tipo);
        setGuardando(true);

        if (TextUtils.isEmpty(idTransaccion)) {
            repositorio.insertar(transaccion, tarea -> {
                setGuardando(false);
                if (tarea.isSuccessful()) {
                    Toast.makeText(this, R.string.mensaje_transaccion_guardada, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, R.string.error_firestore, Toast.LENGTH_LONG).show();
                }
            });
        } else {
            transaccion.setDocumentId(idTransaccion);
            repositorio.actualizar(transaccion, tarea -> {
                setGuardando(false);
                if (tarea.isSuccessful()) {
                    Toast.makeText(this, R.string.mensaje_transaccion_actualizada, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, R.string.error_firestore, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void setGuardando(boolean valor) {
        guardando = valor;
        binding.botonGuardar.setEnabled(!valor);
        binding.botonGuardar.setText(valor ? R.string.accion_guardando : R.string.accion_guardar);
        binding.barraProgreso.setVisibility(valor ? android.view.View.VISIBLE : android.view.View.GONE);
    }
}

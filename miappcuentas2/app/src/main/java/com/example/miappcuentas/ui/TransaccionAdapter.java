package com.example.miappcuentas.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.miappcuentas.R;
import com.example.miappcuentas.datos.PreferenciasApariencia;
import com.example.miappcuentas.modelo.Transaccion;
import com.example.miappcuentas.util.FormatoMoneda;

import java.util.ArrayList;
import java.util.List;

/**
 * Conecta los documentos de Firestore con el RecyclerView.
 */
public class TransaccionAdapter extends RecyclerView.Adapter<TransaccionAdapter.TransaccionViewHolder> {

    public interface OnTransaccionListener {
        void onEditar(Transaccion transaccion);

        void onEliminar(Transaccion transaccion);
    }

    private final List<Transaccion> transacciones = new ArrayList<>();
    private final OnTransaccionListener listener;
    private int colorIngreso = PreferenciasApariencia.COLOR_INGRESO_DEFECTO;
    private int colorGasto = PreferenciasApariencia.COLOR_GASTO_DEFECTO;

    public TransaccionAdapter(OnTransaccionListener listener) {
        this.listener = listener;
    }

    public void actualizarColores(int colorIngreso, int colorGasto) {
        this.colorIngreso = colorIngreso;
        this.colorGasto = colorGasto;
        notifyDataSetChanged();
    }

    public void actualizarLista(List<Transaccion> nuevasTransacciones) {
        transacciones.clear();
        transacciones.addAll(nuevasTransacciones);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TransaccionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaccion, parent, false);
        return new TransaccionViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull TransaccionViewHolder holder, int position) {
        holder.vincular(transacciones.get(position));
    }

    @Override
    public int getItemCount() {
        return transacciones.size();
    }

    class TransaccionViewHolder extends RecyclerView.ViewHolder {

        private final View indicadorTipo;
        private final ImageView iconoTipo;
        private final TextView textoConcepto;
        private final TextView textoTipo;
        private final TextView textoMonto;
        private final ImageButton botonEliminar;

        TransaccionViewHolder(@NonNull View itemView) {
            super(itemView);
            indicadorTipo = itemView.findViewById(R.id.indicadorTipo);
            iconoTipo = itemView.findViewById(R.id.iconoTipo);
            textoConcepto = itemView.findViewById(R.id.textoConcepto);
            textoTipo = itemView.findViewById(R.id.textoTipo);
            textoMonto = itemView.findViewById(R.id.textoMonto);
            botonEliminar = itemView.findViewById(R.id.botonEliminar);
        }

        void vincular(Transaccion transaccion) {
            textoConcepto.setText(transaccion.getConcepto());
            textoMonto.setText(FormatoMoneda.formatear(transaccion.getMonto()));

            boolean esIngreso = transaccion.esIngreso();
            int colorTipo = esIngreso ? colorIngreso : colorGasto;

            indicadorTipo.setBackgroundColor(colorTipo);
            textoMonto.setTextColor(colorTipo);
            textoTipo.setText(esIngreso ? R.string.etiqueta_ingreso : R.string.etiqueta_gasto);
            textoTipo.setTextColor(colorTipo);
            iconoTipo.setImageResource(esIngreso ? R.drawable.ic_ingreso : R.drawable.ic_gasto);
            iconoTipo.setColorFilter(colorTipo);

            itemView.setOnClickListener(v -> listener.onEditar(transaccion));
            botonEliminar.setOnClickListener(v -> listener.onEliminar(transaccion));
        }
    }
}

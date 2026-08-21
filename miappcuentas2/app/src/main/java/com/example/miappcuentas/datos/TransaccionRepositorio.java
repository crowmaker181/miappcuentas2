package com.example.miappcuentas.datos;

import com.example.miappcuentas.modelo.Transaccion;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;

/**
 * CRUD de transacciones en la colección Firestore "transacciones".
 */
public class TransaccionRepositorio {

    public static final String COLECCION = "transacciones";

    private final FirebaseFirestore firestore;

    public TransaccionRepositorio() {
        firestore = FirebaseFirestore.getInstance();
    }

    public ListenerRegistration escucharTodas(EventListener<QuerySnapshot> listener) {
        return firestore.collection(COLECCION)
                .addSnapshotListener(listener);
    }

    public void leerPorId(String documentId, OnCompleteListener<DocumentSnapshot> listener) {
        firestore.collection(COLECCION)
                .document(documentId)
                .get()
                .addOnCompleteListener(listener);
    }

    public void insertar(Transaccion transaccion, OnCompleteListener<DocumentReference> listener) {
        transaccion.setFechaCreacion(new Date());
        firestore.collection(COLECCION)
                .add(transaccion)
                .addOnCompleteListener(listener);
    }

    public void actualizar(Transaccion transaccion, OnCompleteListener<Void> listener) {
        firestore.collection(COLECCION)
                .document(transaccion.getDocumentId())
                .update(
                        "concepto", transaccion.getConcepto(),
                        "monto", transaccion.getMonto(),
                        "tipo", transaccion.getTipo()
                )
                .addOnCompleteListener(listener);
    }

    public void eliminar(String documentId, OnCompleteListener<Void> listener) {
        firestore.collection(COLECCION)
                .document(documentId)
                .delete()
                .addOnCompleteListener(listener);
    }
}

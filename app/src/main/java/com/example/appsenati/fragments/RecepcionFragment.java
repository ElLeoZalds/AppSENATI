package com.example.appsenati.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.appsenati.R;

import org.json.JSONObject;

public class RecepcionFragment extends Fragment {

    EditText edtIdH, edtNombreH, edtMarcaH, edtDescripcionH;
    Button btnBuscarH, btnActualizarH;
    RadioButton rbtBuenoH, rbtRegularH, rbtMaloH;
    RadioButton rbtElectricaH, rbtManualH;
    RequestQueue requestQueue;
    String condicion = "", tipo = "";

    String URL = "http://192.168.101.65:3000/api/herramientas/";

    // Contructor
    public RecepcionFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recepcion, container, false);
    }

    /**
     * Este método retornara TRUE cuando el formulario este listo para el registro (todos los campos tendran datos)
     * @return
     */
    private boolean readyUI() {
        boolean ready = true;

        if ( edtNombreH.getText().toString().isEmpty() ) { ready = false; };
        if ( edtMarcaH.getText().toString().isEmpty() ) { ready = false; };
        if ( edtDescripcionH.getText().toString().isEmpty() ) { ready = false; };

        if ( !rbtBuenoH.isChecked() && !rbtRegularH.isChecked() && !rbtMaloH.isChecked()) { ready = false; };
        if ( !rbtManualH.isChecked() && !rbtElectricaH.isChecked()) { ready = false; };

        return ready;
    }

    private void buscarHerramienta() {

        String id = edtIdH.getText().toString();
        String url = URL + id;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        if (response.getBoolean("success")) {

                            JSONObject data = response.getJSONObject("data");

                            edtNombreH.setText(data.getString("nombre"));
                            edtMarcaH.setText(data.getString("marca"));
                            edtDescripcionH.setText(data.getString("descripcion"));

                            condicion = data.getString("condicion");
                            tipo = data.getString("tipo");

                            rbtBuenoH.setChecked(condicion.equals("Bueno"));
                            rbtRegularH.setChecked(condicion.equals("Regular"));
                            rbtMaloH.setChecked(condicion.equals("Malo"));

                            rbtManualH.setChecked(tipo.equals("Manual"));
                            rbtElectricaH.setChecked(tipo.equals("Eléctrica"));
                        }
                    } catch (Exception e) {
                        Log.e("ERROR", e.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e("ErrorWS", volleyError.toString());
                    }
                }
        );

        requestQueue.add(request);
    }

    private void actualizarHerramienta() {
        String id = edtIdH.getText().toString();
        String url = URL + id;

        try {
            JSONObject body = new JSONObject();

            body.put("nombre", edtNombreH.getText().toString());
            body.put("marca", edtMarcaH.getText().toString());
            body.put("descripcion", edtDescripcionH.getText().toString());

            if (rbtBuenoH.isChecked()) condicion = "Bueno";
            if (rbtRegularH.isChecked()) condicion = "Regular";
            if (rbtMaloH.isChecked()) condicion = "Malo";

            if (rbtManualH.isChecked()) tipo = "Manual";
            if (rbtElectricaH.isChecked()) tipo = "Eléctrica";

            body.put("condicion", condicion);
            body.put("tipo", tipo);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.PUT,
                    url,
                    body,
                    response -> {
                        try {
                            if (response.getBoolean("success")) {
                                Toast.makeText(getContext(), "Actualizado", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e("ERROR", e.toString());
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            // Cuando no realiza la operación...
                            NetworkResponse response = volleyError.networkResponse;

                            // Si existe un error...
                            if ( response != null && response.data != null ) {
                                // STATUS CODE
                                int statusCode = response.statusCode;
                                // MESSAGE DETAIL
                                String errorJSON = new String(response.data);

                                // Mostrar el message (JSON) en la pantalla TOAST
                                Log.d("ErrorStatusCode", String.valueOf(statusCode));
                                Log.d("ErrorDetallado", errorJSON);
                            }
                            // Log.e("ErrorWS", volleyError.toString());
                        }
                    }
            );

            requestQueue.add(request);

        } catch (Exception e) {
            Log.e("ERROR", e.toString());
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        edtIdH = view.findViewById(R.id.edtIdH);
        edtNombreH = view.findViewById(R.id.edtNombreH);
        edtMarcaH = view.findViewById(R.id.edtMarcaH);
        edtDescripcionH = view.findViewById(R.id.edtDescripcionH);

        btnBuscarH = view.findViewById(R.id.btnBuscarH);
        btnActualizarH = view.findViewById(R.id.btnActualizarHerramientaH);

        rbtBuenoH = view.findViewById(R.id.rbtBuenoH);
        rbtRegularH = view.findViewById(R.id.rbtRegularH);
        rbtMaloH = view.findViewById(R.id.rbtMaloH);

        rbtElectricaH = view.findViewById(R.id.rbtElectricaH);
        rbtManualH = view.findViewById(R.id.rbtManualH);

        requestQueue = Volley.newRequestQueue(requireContext());

        btnBuscarH.setOnClickListener(v -> buscarHerramienta());
        btnActualizarH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (readyUI()) {
                    // Confirmación de proceso
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("App Herramientas");
                    builder.setMessage("¿Seguro de proceder con el registro?");

                    builder.setPositiveButton("Si", (a, b) -> {
                        actualizarHerramienta();
                    });
                    builder.setNegativeButton("No", null);

                    AlertDialog dialog = builder.create();
                    dialog.show();

                } else {
                    Toast.makeText(getContext(), "Complete el formulario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
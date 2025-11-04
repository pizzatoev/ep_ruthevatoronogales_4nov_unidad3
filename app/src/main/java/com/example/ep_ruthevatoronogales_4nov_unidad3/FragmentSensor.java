package com.example.ep_ruthevatoronogales_4nov_unidad3;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class FragmentSensor extends Fragment implements SensorEventListener {

    private SensorManager sensorManager;
    private TextView txtX, txtY, txtZ, txtEstado, txtPromedio, txtMaxMin;
    private OnMovimientoListener movimientoListener;
    private Handler handler = new Handler();
    private ArrayList<Float> listaX = new ArrayList<>();
    private ArrayList<Float> listaY = new ArrayList<>();
    private ArrayList<Float> listaZ = new ArrayList<>();
    private int movimientosBruscosConsecutivos = 0;

    public interface OnMovimientoListener {
        void onMovimientoDetectado(String tipoMovimiento);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnMovimientoListener) {
            movimientoListener = (OnMovimientoListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sensor, container, false);

        txtX = view.findViewById(R.id.txtX);
        txtY = view.findViewById(R.id.txtY);
        txtZ = view.findViewById(R.id.txtZ);
        txtEstado = view.findViewById(R.id.txtEstado);
        txtPromedio = view.findViewById(R.id.txtPromedio);
        txtMaxMin = view.findViewById(R.id.txtMaxMin);

        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        return view;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        txtX.setText("X: " + x);
        txtY.setText("Y: " + y);
        txtZ.setText("Z: " + z);

        // Guardamos los últimos 10 valores
        agregarValor(listaX, x);
        agregarValor(listaY, y);
        agregarValor(listaZ, z);

        // Clasificación del tipo de movimiento
        String tipoMovimiento = clasificarMovimiento(x, y, z);
        txtEstado.setText("Movimiento: " + tipoMovimiento);

        // Notificamos al MainActivity
        if (movimientoListener != null) {
            movimientoListener.onMovimientoDetectado(tipoMovimiento);
        }

        // Mostrar datos estadísticos
        actualizarEstadisticas();
    }

    private void agregarValor(ArrayList<Float> lista, float valor) {
        if (lista.size() >= 10) lista.remove(0);
        lista.add(valor);
    }

    private String clasificarMovimiento(float x, float y, float z) {
        float total = (Math.abs(x) + Math.abs(y) + Math.abs(z)) / 3;

        String tipo;
        if (total >= -2 && total <= 2) {
            tipo = "Suave";
            movimientosBruscosConsecutivos = 0;
        } else if ((total > 2 && total <= 5) || (total < -2 && total >= -5)) {
            tipo = "Moderado";
            movimientosBruscosConsecutivos = 0;
        } else {
            tipo = "Brusco";
            movimientosBruscosConsecutivos++;
        }

        return tipo;
    }

    private void actualizarEstadisticas() {
        if (listaX.size() == 0) return;

        float promX = promedio(listaX);
        float promY = promedio(listaY);
        float promZ = promedio(listaZ);

        float maxX = max(listaX);
        float minX = min(listaX);

        txtPromedio.setText(String.format("Promedio X: %.2f | Y: %.2f | Z: %.2f", promX, promY, promZ));
        txtMaxMin.setText(String.format("Máx X: %.2f | Mín X: %.2f", maxX, minX));
    }

    private float promedio(ArrayList<Float> lista) {
        float suma = 0;
        for (float v : lista) suma += v;
        return suma / lista.size();
    }

    private float max(ArrayList<Float> lista) {
        float max = lista.get(0);
        for (float v : lista) if (v > max) max = v;
        return max;
    }

    private float min(ArrayList<Float> lista) {
        float min = lista.get(0);
        for (float v : lista) if (v < min) min = v;
        return min;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}

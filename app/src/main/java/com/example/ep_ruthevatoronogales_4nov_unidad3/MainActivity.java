package com.example.ep_ruthevatoronogales_4nov_unidad3;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements FragmentSensor.OnMovimientoListener {

    private FragmentMapa fragmentMapa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentMapa = (FragmentMapa) getSupportFragmentManager().findFragmentById(R.id.fragmentMapa);
    }

    @Override
    public void onMovimientoDetectado(String tipoMovimiento) {
        if (fragmentMapa != null) {
            fragmentMapa.actualizarMarcador(tipoMovimiento);
        }
    }
}

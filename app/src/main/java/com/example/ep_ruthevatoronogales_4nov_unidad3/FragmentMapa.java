package com.example.ep_ruthevatoronogales_4nov_unidad3;

import android.widget.Toast;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class FragmentMapa extends Fragment implements OnMapReadyCallback {

    private GoogleMap myMap;
    private Marker marcador;
    private LatLng ubicacionActual = new LatLng(-17.7833, -63.1821);
    private int bruscosConsecutivos = 0;

    @Nullable
    @Override
    public android.view.View onCreateView(@NonNull android.view.LayoutInflater inflater,
                                          @Nullable android.view.ViewGroup container,
                                          @Nullable Bundle savedInstanceState) {
        android.view.View view = inflater.inflate(R.layout.fragment_mapa, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        return view;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap = googleMap;
        marcador = myMap.addMarker(new MarkerOptions()
                .position(ubicacionActual)
                .title("Santa Cruz")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacionActual, 13));
    }

    public void actualizarMarcador(String tipoMovimiento) {
        if (marcador == null || myMap == null) return;

        switch (tipoMovimiento) {
            case "Suave":
                marcador.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                marcador.setTitle("Movimiento Suave");
                bruscosConsecutivos = 0;
                break;

            case "Moderado":
                marcador.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                marcador.setTitle("Movimiento Moderado");
                bruscosConsecutivos = 0;
                break;

            case "Brusco":
                marcador.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                marcador.setTitle("Movimiento Brusco");
                bruscosConsecutivos++;
                if (bruscosConsecutivos >= 3) {
                    moverMarcadorSimulado();
                    bruscosConsecutivos = 0;
                }
                break;
        }

        marcador.showInfoWindow();
        verificarZonaAlerta();
    }

    private void moverMarcadorSimulado() {
        // Simulamos un nuevo movimiento del marcador
        ubicacionActual = new LatLng(
                ubicacionActual.latitude - 0.01,
                ubicacionActual.longitude + 0.01
        );
        marcador.setPosition(ubicacionActual);
        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(ubicacionActual, 13));
        Toast.makeText(getContext(), "Marcador movido por 3 movimientos bruscos consecutivos", Toast.LENGTH_SHORT).show();
    }

    private void verificarZonaAlerta() {
        if (ubicacionActual.latitude < -17.8) {
            Toast.makeText(getContext(), "⚠ Zona de riesgo detectada", Toast.LENGTH_LONG).show();
        }
    }
}

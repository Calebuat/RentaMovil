import java.math.BigDecimal;
import java.util.Optional;

public final class Automovil extends Vehiculo {
    private final int cantidadPasajeros;
    private final boolean transmisionAutomatica;

    public Automovil(String placa, String marca, String modelo, BigDecimal tarifaDiaria,
                     int cantidadPasajeros, boolean transmisionAutomatica, int diasPrevios) {
        super(placa, marca, modelo, tarifaDiaria, diasPrevios);
        if (cantidadPasajeros <= 0) {
            throw new IllegalArgumentException("La cantidad de pasajeros debe ser positiva");
        }
        this.cantidadPasajeros = cantidadPasajeros;
        this.transmisionAutomatica = transmisionAutomatica;
        validarDiasPrevios();
    }

    @Override public BigDecimal calcularSubtotal(int dias) {
        validarDias(dias);
        BigDecimal recargo = transmisionAutomatica ? new BigDecimal("50") : BigDecimal.ZERO;
        return getTarifaDiaria().add(recargo).multiply(BigDecimal.valueOf(dias));
    }

    @Override public Optional<TipoLicencia> licenciaRequerida() { return Optional.of(TipoLicencia.C); }
    @Override public int umbralMantenimiento() { return 30; }
    @Override public String describirCaracteristicas() {
        return cantidadPasajeros + " pasajeros, transmisión " + (transmisionAutomatica ? "automática" : "manual");
    }
    @Override public String categoria() { return "Automóvil"; }
}

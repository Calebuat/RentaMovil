import java.math.BigDecimal;
import java.util.Optional;

public final class Microbus extends Vehiculo {
    private final int cantidadPasajeros;
    private final boolean incluyePiloto;

    public Microbus(String placa, String marca, String modelo, BigDecimal tarifaDiaria,
                    int cantidadPasajeros, boolean incluyePiloto, int diasPrevios) {
        super(placa, marca, modelo, tarifaDiaria, diasPrevios);
        if (cantidadPasajeros <= 0) {
            throw new IllegalArgumentException("La cantidad de pasajeros debe ser positiva");
        }
        this.cantidadPasajeros = cantidadPasajeros;
        this.incluyePiloto = incluyePiloto;
        validarDiasPrevios();
    }

    @Override public BigDecimal calcularSubtotal(int dias) {
        validarDias(dias);
        BigDecimal recargo = incluyePiloto ? new BigDecimal("250") : BigDecimal.ZERO;
        return getTarifaDiaria().add(recargo).multiply(BigDecimal.valueOf(dias));
    }

    @Override public Optional<TipoLicencia> licenciaRequerida() {
        return incluyePiloto ? Optional.empty() : Optional.of(TipoLicencia.B);
    }
    @Override public int umbralMantenimiento() { return 25; }
    @Override public String describirCaracteristicas() {
        return cantidadPasajeros + " pasajeros, " + (incluyePiloto ? "con piloto" : "sin piloto");
    }
    @Override public String categoria() { return "Microbús"; }
}

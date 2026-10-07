import java.math.BigDecimal;
import java.util.Optional;

public final class CamionetaCarga extends Vehiculo {
    private final BigDecimal capacidadToneladas;

    public CamionetaCarga(String placa, String marca, String modelo, BigDecimal tarifaDiaria,
                         BigDecimal capacidadToneladas, int diasPrevios) {
        super(placa, marca, modelo, tarifaDiaria, diasPrevios);
        if (capacidadToneladas == null || capacidadToneladas.signum() <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser positiva");
        }
        this.capacidadToneladas = capacidadToneladas;
        validarDiasPrevios();
    }

    @Override public BigDecimal calcularSubtotal(int dias) {
        validarDias(dias);
        return getTarifaDiaria().add(new BigDecimal("100").multiply(capacidadToneladas))
                .multiply(BigDecimal.valueOf(dias));
    }

    @Override public Optional<TipoLicencia> licenciaRequerida() { return Optional.of(TipoLicencia.B); }
    @Override public int umbralMantenimiento() { return 15; }
    @Override public String describirCaracteristicas() { return "Capacidad máxima: " + capacidadToneladas + " toneladas"; }
    @Override public String categoria() { return "Camioneta de carga"; }
}

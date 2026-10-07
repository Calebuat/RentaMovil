import java.math.BigDecimal;
import java.util.Optional;

public final class Motocicleta extends Vehiculo {
    private final int cilindraje;

    public Motocicleta(String placa, String marca, String modelo, BigDecimal tarifaDiaria,
                       int cilindraje, int diasPrevios) {
        super(placa, marca, modelo, tarifaDiaria, diasPrevios);
        if (cilindraje <= 0) {
            throw new IllegalArgumentException("El cilindraje debe ser positivo");
        }
        this.cilindraje = cilindraje;
        validarDiasPrevios();
    }

    @Override public BigDecimal calcularSubtotal(int dias) {
        validarDias(dias);
        BigDecimal recargo = cilindraje > 250 ? new BigDecimal("75") : BigDecimal.ZERO;
        return getTarifaDiaria().multiply(BigDecimal.valueOf(dias)).add(recargo);
    }

    @Override public Optional<TipoLicencia> licenciaRequerida() { return Optional.of(TipoLicencia.M); }
    @Override public int umbralMantenimiento() { return 20; }
    @Override public String describirCaracteristicas() { return cilindraje + " cc"; }
    @Override public String categoria() { return "Motocicleta"; }
}

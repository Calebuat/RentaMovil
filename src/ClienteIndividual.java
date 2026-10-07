import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

public final class ClienteIndividual extends Cliente {
    public ClienteIndividual(String dpi, String nombre, Set<TipoLicencia> licencias,
                             int confirmadosPrevios) {
        super(dpi, nombre, licencias, confirmadosPrevios);
        if (!getIdentificador().matches("[0-9]{13}")) {
            throw new IllegalArgumentException("El DPI debe contener exactamente 13 dígitos");
        }
    }

    @Override public BigDecimal calcularDescuento(BigDecimal subtotal, int confirmadosAntes) {
        validarCobro(subtotal, confirmadosAntes);
        return (confirmadosAntes >= 3 ? subtotal.multiply(new BigDecimal("0.05")) : BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override public int limiteAlquileresActivos() { return 1; }
    @Override public String describirCaracteristicas() {
        return "Individual | DPI: " + getIdentificador() + " | Nombre: " + getNombre();
    }
}

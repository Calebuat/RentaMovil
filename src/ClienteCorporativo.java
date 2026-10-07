import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

public final class ClienteCorporativo extends Cliente {
    private final String contacto;

    public ClienteCorporativo(String nit, String nombreEmpresa, String contacto,
                              Set<TipoLicencia> licencias, int confirmadosPrevios) {
        super(nit, nombreEmpresa, licencias, confirmadosPrevios);
        this.contacto = Vehiculo.textoObligatorio(contacto, "El contacto");
    }

    public String getContacto() { return contacto; }

    @Override public BigDecimal calcularDescuento(BigDecimal subtotal, int confirmadosAntes) {
        validarCobro(subtotal, confirmadosAntes);
        return subtotal.multiply(new BigDecimal("0.10")).setScale(2, RoundingMode.HALF_UP);
    }

    @Override public int limiteAlquileresActivos() { return 3; }
    @Override public String describirCaracteristicas() {
        return "Corporativo | NIT: " + getIdentificador() + " | Empresa: " + getNombre()
                + " | Contacto: " + contacto;
    }
}

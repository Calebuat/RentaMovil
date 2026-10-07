import java.math.BigDecimal;
import java.util.Set;

public abstract class Cliente {
    private final String identificador;
    private final String nombre;
    private final Set<TipoLicencia> licencias;
    private final int confirmadosPrevios;

    protected Cliente(String identificador, String nombre, Set<TipoLicencia> licencias,
                      int confirmadosPrevios) {
        this.identificador = Vehiculo.textoObligatorio(identificador, "El identificador")
                .toUpperCase();
        this.nombre = Vehiculo.textoObligatorio(nombre, "El nombre");
        if (licencias == null || licencias.isEmpty()) {
            throw new IllegalArgumentException("Debe presentar al menos una licencia válida");
        }
        for (TipoLicencia licencia : licencias) {
            if (licencia == null) {
                throw new IllegalArgumentException("Una licencia no puede ser nula");
            }
        }
        this.licencias = Set.copyOf(licencias); // Copia que no permite modificaciones.
        if (confirmadosPrevios < 0) {
            throw new IllegalArgumentException("Los alquileres previos no pueden ser negativos");
        }
        this.confirmadosPrevios = confirmadosPrevios;
    }

    public String getIdentificador() { return identificador; }
    public String getNombre() { return nombre; }
    public Set<TipoLicencia> getLicencias() { return licencias; }
    public int getConfirmadosPrevios() { return confirmadosPrevios; }

    public boolean poseeLicencia(TipoLicencia requerida) {
        if (requerida == null) {
            throw new IllegalArgumentException("La licencia requerida es obligatoria");
        }
        for (TipoLicencia presentada : licencias) {
            if (presentada.habilita(requerida)) {
                return true;
            }
        }
        return false;
    }

    public abstract BigDecimal calcularDescuento(BigDecimal subtotal, int confirmadosAntes);
    public abstract int limiteAlquileresActivos();
    public abstract String describirCaracteristicas();

    protected static void validarCobro(BigDecimal subtotal, int confirmadosAntes) {
        if (subtotal == null || subtotal.signum() < 0 || confirmadosAntes < 0) {
            throw new IllegalArgumentException("El subtotal y los confirmados deben ser no negativos");
        }
    }
}

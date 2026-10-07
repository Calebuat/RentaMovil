import java.math.BigDecimal;
import java.util.Optional;

public abstract class Vehiculo {
    private final String placa;
    private final String marca;
    private final String modelo;
    private final BigDecimal tarifaDiaria;
    private EstadoVehiculo estado;
    private int diasDesdeMantenimiento;

    protected Vehiculo(String placa, String marca, String modelo,
                       BigDecimal tarifaDiaria, int diasPrevios) {
        this.placa = textoObligatorio(placa, "La placa").toUpperCase();
        this.marca = textoObligatorio(marca, "La marca");
        this.modelo = textoObligatorio(modelo, "El modelo");
        if (tarifaDiaria == null || tarifaDiaria.signum() <= 0) {
            throw new IllegalArgumentException("La tarifa debe ser positiva");
        }
        this.tarifaDiaria = tarifaDiaria;
        if (diasPrevios < 0) {
            throw new IllegalArgumentException("Los días previos no pueden ser negativos");
        }
        this.diasDesdeMantenimiento = diasPrevios;
        this.estado = EstadoVehiculo.DISPONIBLE;
    }

    protected static String textoObligatorio(String valor, String nombre) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(nombre + " no puede estar vacío");
        }
        return valor.trim();
    }

    protected static void validarDias(int dias) {
        if (dias <= 0) {
            throw new IllegalArgumentException("Los días deben ser positivos");
        }
    }

    protected final void validarDiasPrevios() {
        if (diasDesdeMantenimiento >= umbralMantenimiento()) {
            throw new IllegalArgumentException("Los días previos deben ser menores que el umbral de mantenimiento");
        }
    }

    public String getPlaca() { return placa; }
    public String getMarca() { return marca; }
    public String getModelo() { return modelo; }
    public BigDecimal getTarifaDiaria() { return tarifaDiaria; }
    public EstadoVehiculo getEstado() { return estado; }
    public int getDiasDesdeMantenimiento() { return diasDesdeMantenimiento; }

    public abstract BigDecimal calcularSubtotal(int dias);
    public abstract Optional<TipoLicencia> licenciaRequerida();
    public abstract int umbralMantenimiento();
    public abstract String describirCaracteristicas();
    public abstract String categoria();

    void iniciarAlquiler() {
        if (estado != EstadoVehiculo.DISPONIBLE) {
            throw new IllegalStateException("El vehículo no está disponible");
        }
        estado = EstadoVehiculo.ALQUILADO;
    }

    void registrarDevolucion(int dias) {
        if (estado != EstadoVehiculo.ALQUILADO) {
            throw new IllegalStateException("El vehículo no está alquilado");
        }
        validarDias(dias);
        if (dias > Integer.MAX_VALUE - diasDesdeMantenimiento) {
            throw new IllegalArgumentException("Los días acumulados exceden el límite permitido");
        }
        int nuevoAcumulado = diasDesdeMantenimiento + dias;
        diasDesdeMantenimiento = nuevoAcumulado;
        estado = nuevoAcumulado >= umbralMantenimiento()
                ? EstadoVehiculo.EN_MANTENIMIENTO : EstadoVehiculo.DISPONIBLE;
    }

    void finalizarMantenimiento() {
        if (estado != EstadoVehiculo.EN_MANTENIMIENTO) {
            throw new IllegalStateException("El vehículo no está en mantenimiento");
        }
        diasDesdeMantenimiento = 0;
        estado = EstadoVehiculo.DISPONIBLE;
    }
}

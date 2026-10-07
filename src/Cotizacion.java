import java.util.List;

public final class Cotizacion {
    private final String placa;
    private final String idCliente;
    private final int dias;
    private final String descripcion;
    private final EstadoVehiculo estadoVehiculo;
    private final Cobro cobro;
    private final List<String> motivosRechazo;

    Cotizacion(String placa, String idCliente, int dias, String descripcion,
               EstadoVehiculo estadoVehiculo, Cobro cobro, List<String> motivosRechazo) {
        this.placa = Vehiculo.textoObligatorio(placa, "La placa");
        this.idCliente = Vehiculo.textoObligatorio(idCliente, "El identificador");
        Vehiculo.validarDias(dias);
        this.dias = dias;
        this.descripcion = Vehiculo.textoObligatorio(descripcion, "La descripción");
        if (estadoVehiculo == null || cobro == null || motivosRechazo == null) {
            throw new IllegalArgumentException("La cotización tiene datos obligatorios ausentes");
        }
        for (String motivo : motivosRechazo) {
            if (motivo == null) throw new IllegalArgumentException("Un motivo no puede ser nulo");
        }
        this.estadoVehiculo = estadoVehiculo;
        this.cobro = cobro;
        this.motivosRechazo = List.copyOf(motivosRechazo);
    }

    public String getPlaca() { return placa; }
    public String getIdCliente() { return idCliente; }
    public int getDias() { return dias; }
    public String getDescripcion() { return descripcion; }
    public EstadoVehiculo getEstadoVehiculo() { return estadoVehiculo; }
    public Cobro getCobro() { return cobro; }
    public List<String> getMotivosRechazo() { return motivosRechazo; }
    public boolean puedeAlquilar() { return motivosRechazo.isEmpty(); }
}

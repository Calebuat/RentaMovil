public final class Alquiler {
    private final int numero;
    private final Cliente cliente;
    private final Vehiculo vehiculo;
    private final int dias;
    private final Cobro cobro;
    private EstadoAlquiler estado;

    Alquiler(int numero, Cliente cliente, Vehiculo vehiculo, int dias, Cobro cobro) {
        if (numero <= 0) {
            throw new IllegalArgumentException("El número de alquiler debe ser positivo");
        }
        Vehiculo.validarDias(dias);
        this.numero = numero;
        if (cliente == null || vehiculo == null || cobro == null) {
            throw new IllegalArgumentException("Cliente, vehículo y cobro son obligatorios");
        }
        this.cliente = cliente;
        this.vehiculo = vehiculo;
        this.dias = dias;
        this.cobro = cobro;
        this.estado = EstadoAlquiler.ACTIVO;
    }

    public int getNumero() { return numero; }
    public Cliente getCliente() { return cliente; }
    public Vehiculo getVehiculo() { return vehiculo; }
    public int getDias() { return dias; }
    public Cobro getCobro() { return cobro; }
    public EstadoAlquiler getEstado() { return estado; }
    public boolean estaActivo() { return estado == EstadoAlquiler.ACTIVO; }

    void finalizar() {
        if (!estaActivo()) {
            throw new IllegalStateException("El alquiler ya fue finalizado");
        }
        estado = EstadoAlquiler.FINALIZADO;
    }
}

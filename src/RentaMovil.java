import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class RentaMovil {
    private final Map<String, Vehiculo> flota;
    private final Map<String, Cliente> clientes;
    private final List<Alquiler> alquileres;
    private int siguienteNumero;

    public RentaMovil() {
        flota = new LinkedHashMap<>();
        clientes = new LinkedHashMap<>();
        alquileres = new ArrayList<>();
        siguienteNumero = 1;
    }

    public void registrarVehiculo(Vehiculo vehiculo) {
        if (vehiculo == null) throw new IllegalArgumentException("El vehículo es obligatorio");
        if (vehiculo.getEstado() != EstadoVehiculo.DISPONIBLE) {
            throw new IllegalStateException("Solo se registran vehículos disponibles");
        }
        if (flota.containsKey(vehiculo.getPlaca())) {
            throw new IllegalArgumentException("Ya existe la placa " + vehiculo.getPlaca());
        }
        flota.put(vehiculo.getPlaca(), vehiculo);
    }

    public void registrarCliente(Cliente cliente) {
        if (cliente == null) throw new IllegalArgumentException("El cliente es obligatorio");
        if (clientes.containsKey(cliente.getIdentificador())) {
            throw new IllegalArgumentException("Ya existe el identificador " + cliente.getIdentificador());
        }
        clientes.put(cliente.getIdentificador(), cliente);
    }

    public List<Vehiculo> consultarFlota() { return List.copyOf(flota.values()); }
    public List<Cliente> consultarClientes() { return List.copyOf(clientes.values()); }

    public Cotizacion cotizar(String placa, String idCliente, int dias) {
        Vehiculo.validarDias(dias);
        Vehiculo vehiculo = buscarVehiculo(placa);
        Cliente cliente = buscarCliente(idCliente);
        BigDecimal subtotal = vehiculo.calcularSubtotal(dias);
        BigDecimal descuento = cliente.calcularDescuento(subtotal, contarConfirmados(cliente));
        Cobro cobro = new Cobro(subtotal, descuento);
        List<String> motivos = new ArrayList<>();

        if (vehiculo.getEstado() != EstadoVehiculo.DISPONIBLE) {
            motivos.add("Vehículo no disponible: " + vehiculo.getEstado());
        }
        Optional<TipoLicencia> requerida = vehiculo.licenciaRequerida();
        if (requerida.isPresent() && !cliente.poseeLicencia(requerida.get())) {
            motivos.add("El cliente no posee licencia que habilite " + requerida.get());
        }
        if (contarActivos(cliente) >= cliente.limiteAlquileresActivos()) {
            motivos.add("El cliente alcanzó su límite de alquileres activos ("
                    + cliente.limiteAlquileresActivos() + ")");
        }

        String descripcion = vehiculo.categoria() + " | " + vehiculo.getMarca() + " "
                + vehiculo.getModelo() + " | " + vehiculo.describirCaracteristicas();
        return new Cotizacion(vehiculo.getPlaca(), cliente.getIdentificador(), dias,
                descripcion, vehiculo.getEstado(), cobro, motivos);
    }

    public Optional<Alquiler> confirmar(Cotizacion propuesta, boolean acepta) {
        if (propuesta == null) throw new IllegalArgumentException("La cotización es obligatoria");
        if (!acepta) {
            return Optional.empty();
        }
        Cotizacion actual = cotizar(propuesta.getPlaca(), propuesta.getIdCliente(), propuesta.getDias());
        if (!actual.puedeAlquilar()) {
            throw new IllegalStateException("No puede confirmarse: "
                    + String.join("; ", actual.getMotivosRechazo()));
        }
        if (!propuesta.puedeAlquilar()
                || actual.getCobro().getSubtotal().compareTo(propuesta.getCobro().getSubtotal()) != 0
                || actual.getCobro().getDescuento().compareTo(propuesta.getCobro().getDescuento()) != 0) {
            throw new IllegalStateException("La cotización cambió; obtenga y acepte una nueva");
        }
        Vehiculo vehiculo = buscarVehiculo(actual.getPlaca());
        Cliente cliente = buscarCliente(actual.getIdCliente());
        Alquiler alquiler = new Alquiler(siguienteNumero, cliente, vehiculo,
                actual.getDias(), propuesta.getCobro());
        vehiculo.iniciarAlquiler();
        alquileres.add(alquiler);
        siguienteNumero++;
        return Optional.of(alquiler);
    }

    public void devolver(String placa) {
        Vehiculo vehiculo = buscarVehiculo(placa);
        if (vehiculo.getEstado() != EstadoVehiculo.ALQUILADO) {
            throw new IllegalStateException("El vehículo no está alquilado");
        }
        Alquiler vigente = null;
        for (Alquiler alquiler : alquileres) {
            if (alquiler.getVehiculo() == vehiculo && alquiler.estaActivo()) {
                if (vigente != null) {
                    throw new IllegalStateException("La placa tiene más de un alquiler activo");
                }
                vigente = alquiler;
            }
        }
        if (vigente == null) {
            throw new IllegalStateException("La placa no tiene un alquiler activo");
        }
        // registrarDevolucion comprueba estado, días y desbordamiento antes de modificar.
        vehiculo.registrarDevolucion(vigente.getDias());
        vigente.finalizar();
    }

    public void terminarMantenimiento(String placa) {
        buscarVehiculo(placa).finalizarMantenimiento();
    }

    public Map<String, Map<EstadoVehiculo, Integer>> resumenFlota() {
        Map<String, Map<EstadoVehiculo, Integer>> resumen = new LinkedHashMap<>();
        for (Vehiculo vehiculo : flota.values()) {
            String categoria = vehiculo.categoria();
            if (!resumen.containsKey(categoria)) {
                resumen.put(categoria, new LinkedHashMap<>());
            }
            Map<EstadoVehiculo, Integer> estados = resumen.get(categoria);
            EstadoVehiculo estado = vehiculo.getEstado();
            Integer cantidad = estados.get(estado);
            if (cantidad == null) cantidad = 0;
            estados.put(estado, cantidad + 1);
        }
        // Cada llamada construye mapas nuevos: cambiarlos no altera la flota.
        return resumen;
    }

    public BigDecimal ingresosTotales() {
        BigDecimal total = new BigDecimal("0.00");
        for (Alquiler alquiler : alquileres) {
            total = total.add(alquiler.getCobro().getTotal());
        }
        return total;
    }

    public Map<String, BigDecimal> ingresosPorCategoria() {
        Map<String, BigDecimal> ingresos = new LinkedHashMap<>();
        for (Vehiculo vehiculo : flota.values()) {
            if (!ingresos.containsKey(vehiculo.categoria())) {
                ingresos.put(vehiculo.categoria(), new BigDecimal("0.00"));
            }
        }
        for (Alquiler alquiler : alquileres) {
            String categoria = alquiler.getVehiculo().categoria();
            ingresos.put(categoria, ingresos.get(categoria).add(alquiler.getCobro().getTotal()));
        }
        return ingresos;
    }

    public BigDecimal descuentosTotales() {
        BigDecimal total = new BigDecimal("0.00");
        for (Alquiler alquiler : alquileres) {
            total = total.add(alquiler.getCobro().getDescuento());
        }
        return total;
    }

    public List<Alquiler> alquileresActivos() {
        List<Alquiler> activos = new ArrayList<>();
        for (Alquiler alquiler : alquileres) {
            if (alquiler.estaActivo()) {
                activos.add(alquiler);
            }
        }
        return List.copyOf(activos);
    }

    public List<Alquiler> historialCliente(String idCliente) {
        Cliente cliente = buscarCliente(idCliente);
        List<Alquiler> historial = new ArrayList<>();
        for (Alquiler alquiler : alquileres) {
            if (alquiler.getCliente() == cliente) {
                historial.add(alquiler);
            }
        }
        return List.copyOf(historial);
    }

    public BigDecimal totalPagadoCliente(String idCliente) {
        BigDecimal total = new BigDecimal("0.00");
        for (Alquiler alquiler : historialCliente(idCliente)) {
            total = total.add(alquiler.getCobro().getTotal());
        }
        return total;
    }

    private Vehiculo buscarVehiculo(String placa) {
        String clave = Vehiculo.textoObligatorio(placa, "La placa").toUpperCase();
        Vehiculo vehiculo = flota.get(clave);
        if (vehiculo == null) {
            throw new IllegalArgumentException("No existe la placa " + clave);
        }
        return vehiculo;
    }

    private Cliente buscarCliente(String idCliente) {
        String clave = Vehiculo.textoObligatorio(idCliente, "El identificador").toUpperCase();
        Cliente cliente = clientes.get(clave);
        if (cliente == null) {
            throw new IllegalArgumentException("No existe el cliente " + clave);
        }
        return cliente;
    }

    private int contarActivos(Cliente cliente) {
        int activos = 0;
        for (Alquiler alquiler : alquileres) {
            if (alquiler.getCliente() == cliente && alquiler.estaActivo()) {
                activos++;
            }
        }
        return activos;
    }

    private int contarConfirmados(Cliente cliente) {
        int confirmados = cliente.getConfirmadosPrevios();
        for (Alquiler alquiler : alquileres) {
            if (alquiler.getCliente() == cliente) {
                confirmados++;
            }
        }
        return confirmados;
    }
}

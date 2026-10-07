import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

public final class Aplicacion {
    private final RentaMovil sistema;
    private final Scanner entrada;

    public Aplicacion(RentaMovil sistema, Scanner entrada) {
        if (sistema == null || entrada == null) {
            throw new IllegalArgumentException("El sistema y el lector son obligatorios");
        }
        this.sistema = sistema;
        this.entrada = entrada;
    }

    public static void main(String[] args) {
        Aplicacion aplicacion = new Aplicacion(new RentaMovil(), new Scanner(System.in));
        aplicacion.cargarDatosIniciales();
        aplicacion.ejecutar();
    }

    public void ejecutar() {
        System.out.println("=== RentaMovil ===");
        try {
            while (true) {
                System.out.println("\n1. Registrar vehículo\n2. Registrar cliente\n3. Consultar flota"
                        + "\n4. Consultar clientes\n5. Cotizar y gestionar alquiler"
                        + "\n6. Devolver vehículo\n7. Terminar mantenimiento"
                        + "\n8. Reportes\n0. Salir");
                int opcion = leerEntero("Opción: ", 0, 8);
                if (opcion == 0) {
                    System.out.println("Hasta luego.");
                    break;
                }
                try {
                    switch (opcion) {
                        case 1: registrarVehiculo(); break;
                        case 2: registrarCliente(); break;
                        case 3: mostrarFlota(); break;
                        case 4: mostrarClientes(); break;
                        case 5: gestionarAlquiler(); break;
                        case 6:
                            sistema.devolver(leerTexto("Placa a devolver: "));
                            System.out.println("Devolución registrada. Estado actualizado:");
                            mostrarFlota();
                            break;
                        case 7:
                            sistema.terminarMantenimiento(leerTexto("Placa en mantenimiento: "));
                            System.out.println("Mantenimiento terminado.");
                            break;
                        case 8: mostrarReportes(); break;
                        default: throw new IllegalStateException("Opción no contemplada");
                    }
                } catch (IllegalArgumentException | IllegalStateException ex) {
                    System.out.println("No se pudo completar la operación: " + ex.getMessage());
                }
            }
        } catch (NoSuchElementException ex) {
            System.out.println("\nEntrada finalizada. Hasta luego.");
        }
    }

    private void cargarDatosIniciales() {
        sistema.registrarVehiculo(new Automovil("P001AAA", "Toyota", "Corolla",
                new BigDecimal("200.00"), 5, false, 29));
        sistema.registrarVehiculo(new Automovil("P002AAA", "Kia", "Rio",
                new BigDecimal("200.00"), 5, true, 0));
        sistema.registrarVehiculo(new Motocicleta("M001AAA", "Honda", "CB250",
                new BigDecimal("100.00"), 250, 19));
        sistema.registrarVehiculo(new Motocicleta("M002AAA", "Yamaha", "MT03",
                new BigDecimal("150.00"), 321, 0));
        sistema.registrarVehiculo(new CamionetaCarga("C001AAA", "Toyota", "Hilux",
                new BigDecimal("200.00"), new BigDecimal("1.5"), 14));
        sistema.registrarVehiculo(new CamionetaCarga("C002AAA", "Isuzu", "NPR",
                new BigDecimal("300.00"), new BigDecimal("3"), 0));
        sistema.registrarVehiculo(new Microbus("B001AAA", "Toyota", "Hiace",
                new BigDecimal("450.00"), 15, true, 24));
        sistema.registrarVehiculo(new Microbus("B002AAA", "Hyundai", "H1",
                new BigDecimal("350.00"), 12, false, 0));

        sistema.registrarCliente(new ClienteIndividual("1234567890123", "Ana López",
                Set.of(TipoLicencia.C), 0));
        sistema.registrarCliente(new ClienteIndividual("1234567890124", "Luis Pérez",
                Set.of(TipoLicencia.A, TipoLicencia.M), 3));
        sistema.registrarCliente(new ClienteCorporativo("1000001-1", "Logística Centro",
                "Marta Ruiz", Set.of(TipoLicencia.A, TipoLicencia.M), 0));
        sistema.registrarCliente(new ClienteCorporativo("1000002-2", "Viajes del Valle",
                "Carlos Díaz", Set.of(TipoLicencia.B), 0));
    }

    private void registrarVehiculo() {
        int categoria = leerEntero("Categoría: 1 automóvil, 2 motocicleta, "
                + "3 camioneta de carga, 4 microbús: ", 1, 4);
        String placa = leerTexto("Placa: ");
        String marca = leerTexto("Marca: ");
        String modelo = leerTexto("Modelo: ");
        BigDecimal tarifa = leerDecimalPositivo("Tarifa diaria en Q (use punto decimal): ");
        Vehiculo vehiculo;
        switch (categoria) {
            case 1:
                vehiculo = new Automovil(placa, marca, modelo, tarifa,
                        leerEntero("Cantidad de pasajeros: ", 1, Integer.MAX_VALUE),
                        leerBooleano("¿Transmisión automática? (s/n): "), 0);
                break;
            case 2:
                vehiculo = new Motocicleta(placa, marca, modelo, tarifa,
                        leerEntero("Cilindraje en cc: ", 1, Integer.MAX_VALUE), 0);
                break;
            case 3:
                vehiculo = new CamionetaCarga(placa, marca, modelo, tarifa,
                        leerDecimalPositivo("Capacidad máxima en toneladas: "), 0);
                break;
            case 4:
                vehiculo = new Microbus(placa, marca, modelo, tarifa,
                        leerEntero("Cantidad de pasajeros: ", 1, Integer.MAX_VALUE),
                        leerBooleano("¿Incluye piloto? (s/n): "), 0);
                break;
            default: throw new IllegalStateException("Categoría desconocida");
        }
        sistema.registrarVehiculo(vehiculo);
        System.out.println("Vehículo registrado: " + vehiculo.getPlaca());
    }

    private void registrarCliente() {
        int tipo = leerEntero("Tipo: 1 individual, 2 corporativo: ", 1, 2);
        String id = leerTexto(tipo == 1 ? "DPI (13 dígitos): " : "NIT: ");
        String nombre = leerTexto(tipo == 1 ? "Nombre: " : "Empresa: ");
        String contacto = tipo == 2 ? leerTexto("Contacto: ") : null;
        Set<TipoLicencia> licencias = leerLicencias();
        Cliente cliente = tipo == 1
                ? new ClienteIndividual(id, nombre, licencias, 0)
                : new ClienteCorporativo(id, nombre, contacto, licencias, 0);
        sistema.registrarCliente(cliente);
        System.out.println("Cliente registrado: " + cliente.getIdentificador());
    }

    private void gestionarAlquiler() {
        String placa = leerTexto("Placa: ");
        String cliente = leerTexto("DPI o NIT: ");
        int dias = leerEntero("Días del alquiler: ", 1, Integer.MAX_VALUE);
        Cotizacion cotizacion = sistema.cotizar(placa, cliente, dias);
        System.out.println("Vehículo: " + cotizacion.getDescripcion());
        System.out.println("Estado: " + cotizacion.getEstadoVehiculo());
        System.out.println("Subtotal: " + dinero(cotizacion.getCobro().getSubtotal())
                + " | Descuento: " + dinero(cotizacion.getCobro().getDescuento())
                + " | Total: " + dinero(cotizacion.getCobro().getTotal()));
        if (!cotizacion.puedeAlquilar()) {
            System.out.println("No se puede confirmar por estas razones:");
            for (String motivo : cotizacion.getMotivosRechazo()) {
                System.out.println("- " + motivo);
            }
            return;
        }
        boolean acepta = leerBooleano("¿Confirma el alquiler? (s/n): ");
        Optional<Alquiler> confirmado = sistema.confirmar(cotizacion, acepta);
        if (confirmado.isPresent()) {
            System.out.println("Alquiler #" + confirmado.get().getNumero() + " confirmado. "
                    + "Total: " + dinero(confirmado.get().getCobro().getTotal()));
        } else {
            System.out.println("Cotización cancelada; no se realizó ningún cobro.");
        }
    }

    private void mostrarFlota() {
        List<Vehiculo> vehiculos = sistema.consultarFlota();
        if (vehiculos.isEmpty()) System.out.println("La flota está vacía.");
        for (Vehiculo v : vehiculos) {
            System.out.println(v.getPlaca() + " | " + v.categoria() + " | " + v.getMarca()
                    + " " + v.getModelo() + " | " + v.describirCaracteristicas()
                    + " | Tarifa diaria: " + dinero(v.getTarifaDiaria())
                    + " | Estado: " + v.getEstado()
                    + " | Días desde mantenimiento: " + v.getDiasDesdeMantenimiento());
        }
    }

    private void mostrarClientes() {
        List<Cliente> clientes = sistema.consultarClientes();
        if (clientes.isEmpty()) System.out.println("No hay clientes.");
        for (Cliente c : clientes) {
            System.out.println(c.describirCaracteristicas() + " | Licencias: " + c.getLicencias()
                    + " | Confirmados previos: " + c.getConfirmadosPrevios());
        }
    }

    private void mostrarReportes() {
        System.out.println("=== Flota por categoría y estado ===");
        for (Map.Entry<String, Map<EstadoVehiculo, Integer>> fila : sistema.resumenFlota().entrySet()) {
            int total = 0;
            for (int cantidad : fila.getValue().values()) total += cantidad;
            System.out.println(fila.getKey() + " | Total: " + total + " | " + fila.getValue());
        }
        System.out.println("Ingresos totales: " + dinero(sistema.ingresosTotales()));
        System.out.println("Descuentos totales: " + dinero(sistema.descuentosTotales()));
        System.out.println("Ingresos por categoría:");
        for (Map.Entry<String, BigDecimal> fila : sistema.ingresosPorCategoria().entrySet()) {
            System.out.println("- " + fila.getKey() + ": " + dinero(fila.getValue()));
        }
        System.out.println("Alquileres activos:");
        if (sistema.alquileresActivos().isEmpty()) System.out.println("- Ninguno");
        for (Alquiler alquiler : sistema.alquileresActivos()) mostrarAlquiler(alquiler);
        String id = leerTexto("DPI o NIT para consultar historial: ");
        List<Alquiler> historial = sistema.historialCliente(id);
        if (historial.isEmpty()) System.out.println("Sin alquileres confirmados en esta ejecución.");
        for (Alquiler alquiler : historial) mostrarAlquiler(alquiler);
        System.out.println("Total pagado en esta ejecución: " + dinero(sistema.totalPagadoCliente(id)));
    }

    private void mostrarAlquiler(Alquiler alquiler) {
        System.out.println("- #" + alquiler.getNumero() + " | "
                + alquiler.getCliente().getIdentificador() + " | " + alquiler.getVehiculo().getPlaca()
                + " | " + alquiler.getDias() + " días | " + alquiler.getEstado()
                + " | Pagado: " + dinero(alquiler.getCobro().getTotal()));
    }

    private static String dinero(BigDecimal monto) {
        return "Q" + monto.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private String leerTexto(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String valor = entrada.nextLine().trim();
            if (!valor.isEmpty()) return valor;
            System.out.println("El valor no puede estar vacío.");
        }
    }

    private int leerEntero(String mensaje, int minimo, int maximo) {
        while (true) {
            String valor = leerTexto(mensaje);
            try {
                int numero = Integer.parseInt(valor);
                if (numero >= minimo && numero <= maximo) return numero;
            } catch (NumberFormatException ex) {
                // Se informa el mismo mensaje que para valores fuera de rango.
            }
            System.out.println("Ingrese un entero entre " + minimo + " y " + maximo + ".");
        }
    }

    private BigDecimal leerDecimalPositivo(String mensaje) {
        while (true) {
            String valor = leerTexto(mensaje);
            try {
                BigDecimal numero = new BigDecimal(valor);
                if (numero.signum() > 0) return numero;
            } catch (NumberFormatException ex) {
                // Se vuelve a solicitar la entrada.
            }
            System.out.println("Ingrese un número decimal positivo (por ejemplo, 200.50).");
        }
    }

    private boolean leerBooleano(String mensaje) {
        while (true) {
            String valor = leerTexto(mensaje);
            if (valor.equalsIgnoreCase("s") || valor.equalsIgnoreCase("si")
                    || valor.equalsIgnoreCase("sí")) return true;
            if (valor.equalsIgnoreCase("n") || valor.equalsIgnoreCase("no")) return false;
            System.out.println("Responda s o n.");
        }
    }

    private Set<TipoLicencia> leerLicencias() {
        while (true) {
            String valor = leerTexto("Licencias (A, B, C o M; separe varias con comas): ");
            Set<TipoLicencia> licencias = new HashSet<>();
            boolean valido = true;
            for (String parte : valor.split(",", -1)) {
                try {
                    licencias.add(TipoLicencia.valueOf(parte.trim().toUpperCase()));
                } catch (IllegalArgumentException ex) {
                    valido = false;
                    break;
                }
            }
            if (valido && !licencias.isEmpty()) return licencias;
            System.out.println("Indique una o más licencias válidas: A, B, C, M.");
        }
    }
}

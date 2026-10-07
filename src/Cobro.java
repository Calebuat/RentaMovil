import java.math.BigDecimal;
import java.math.RoundingMode;

public final class Cobro {
    private final BigDecimal subtotal;
    private final BigDecimal descuento;
    private final BigDecimal total;

    public Cobro(BigDecimal subtotal, BigDecimal descuento) {
        if (subtotal == null || descuento == null || subtotal.signum() < 0 || descuento.signum() < 0) {
            throw new IllegalArgumentException("Los montos no pueden ser negativos");
        }
        this.subtotal = subtotal.setScale(2, RoundingMode.HALF_UP);
        this.descuento = descuento.setScale(2, RoundingMode.HALF_UP);
        if (this.descuento.compareTo(this.subtotal) > 0) {
            throw new IllegalArgumentException("El descuento no puede superar el subtotal");
        }
        this.total = this.subtotal.subtract(this.descuento);
    }

    public BigDecimal getSubtotal() { return subtotal; }
    public BigDecimal getDescuento() { return descuento; }
    public BigDecimal getTotal() { return total; }
}

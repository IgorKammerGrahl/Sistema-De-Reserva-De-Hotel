package SistemaHotel;

import java.time.LocalDate;

public class Reserva implements Comparable<Reserva> {
    private String clienteId;
    private int numeroQuarto;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private String categoriaQuarto;
    private String status; // Status da reserva (ex.: Confirmada, Cancelada)

    public Reserva(String clienteId, int numeroQuarto, String nomeCliente, LocalDate checkIn, LocalDate checkOut, String categoriaQuarto) {
        this.clienteId = clienteId;
        this.numeroQuarto = numeroQuarto;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.categoriaQuarto = categoriaQuarto;
        this.status = "Confirmada";  
    }
    
    public Reserva(int numeroQuarto, String categoriaQuarto) {
        this.numeroQuarto = numeroQuarto;
        this.categoriaQuarto = categoriaQuarto;
    }
    
    public Reserva() {
        this.status = "Disponível";  
    }

    public Reserva(String clienteId) {
        this(clienteId, 0, "", LocalDate.now(), LocalDate.now().plusDays(1), "");  // A data de check-out é no dia seguinte
    }

    public String getClienteId() {
        return clienteId;
    }

    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }

    public int getNumeroQuarto() {
        return numeroQuarto;
    }

    public void setNumeroQuarto(int numeroQuarto) {
        this.numeroQuarto = numeroQuarto;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(LocalDate checkIn) {
        if (checkIn.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Data de check-in não pode ser no passado.");
        }
        this.checkIn = checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(LocalDate checkOut) {
        this.checkOut = checkOut;
    }

    public String getCategoriaQuarto() {
        return categoriaQuarto;
    }

    public void setCategoriaQuarto(String categoriaQuarto) {
        this.categoriaQuarto = categoriaQuarto;
    }

    @Override
    public int compareTo(Reserva other) {
        if (this.checkIn == null || other.checkIn == null) {
            return 0;  
        }
        return this.checkIn.compareTo(other.checkIn);
    }

    public int compareByClienteId(Reserva other) {
        return this.clienteId.compareTo(other.clienteId);
    }

    @Override
    public String toString() {
        return "Reserva [Cliente ID: " + clienteId + ", Status: " + status + 
               ", Quarto: " + numeroQuarto + ", Categoria: " + categoriaQuarto + 
               ", Check-in: " + checkIn + ", Check-out: " + checkOut + "]";
    }
}

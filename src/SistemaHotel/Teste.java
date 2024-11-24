package SistemaHotel;

import java.time.LocalDate;
import java.util.List;

public class Teste {

    public static void main(String[] args) {
        HotelReservaSistema sistema = new HotelReservaSistema();

        // Inicializar quartos
        System.out.println("Adicionando quartos pré-definidos...");
        sistema.adicionarQuarto(101, "Standard");
        sistema.adicionarQuarto(102, "Standard");
        sistema.adicionarQuarto(201, "Luxo");
        sistema.adicionarQuarto(202, "Luxo");
        sistema.adicionarQuarto(301, "Premium");
        sistema.adicionarQuarto(302, "Premium");

        System.out.println("\n=== Teste: Cadastro de Reservas ===");
        Reserva reserva1 = new Reserva("cliente1", 101, "João Silva", LocalDate.of(2024, 12, 1), LocalDate.of(2024, 12, 3), "Standard");
        Reserva reserva2 = new Reserva("cliente2", 201, "Maria Oliveira", LocalDate.of(2024, 12, 5), LocalDate.of(2024, 12, 10), "Luxo");
        Reserva reservaConflitante = new Reserva("cliente3", 101, "Pedro Souza", LocalDate.of(2024, 12, 2), LocalDate.of(2024, 12, 4), "Standard");

        sistema.cadastrarReserva(reserva1); 
        sistema.cadastrarReserva(reserva2); 
        sistema.cadastrarReserva(reservaConflitante); 

        System.out.println("\n=== Teste: Cancelamento de Reservas ===");
        sistema.cancelarReserva("cliente1"); 
        sistema.cancelarReserva("clienteX"); 

        System.out.println("\n=== Teste: Consulta de Disponibilidade ===");
        List<Reserva> disponiveis = sistema.consultarDisponibilidade(LocalDate.of(2024, 12, 2), "Standard");
        if (disponiveis.isEmpty()) {
            System.out.println("Nenhum quarto disponível.");
        } else {
            System.out.println("Quartos disponíveis:");
            for (Reserva quarto : disponiveis) {
                System.out.println(quarto);
            }
        }

        // Testar relatórios
        System.out.println("\n=== Teste: Relatórios ===");
        System.out.println("Relatório de Taxa de Ocupação:");
        sistema.gerarRelatorioTaxaOcupacao(LocalDate.of(2024, 12, 1), LocalDate.of(2024, 12, 10));

        System.out.println("\nRelatório de Cancelamentos:");
        sistema.gerarRelatorioCancelamentos(LocalDate.of(2024, 12, 1), LocalDate.of(2024, 12, 10));

        System.out.println("\nRelatório de Quartos Mais e Menos Reservados:");
        sistema.gerarRelatorioQuartosMaisEMenosReservados();

        System.out.println("\nRelatório de Alerta de Capacidade:");
        sistema.gerarAlertaCapacidade(90.0);

        // Testar listagem de reservas por período
        System.out.println("\n=== Teste: Listagem de Reservas no Período ===");
        List<Reserva> reservasNoPeriodo = sistema.obterReservasNoPeriodo(LocalDate.of(2024, 12, 1), LocalDate.of(2024, 12, 31));
        System.out.println("Reservas no período:");
        for (Reserva reserva : reservasNoPeriodo) {
            System.out.println(reserva);
        }

        // Testar listagem de reservas canceladas por período
        System.out.println("\n=== Teste: Listagem de Reservas Canceladas no Período ===");
        List<Reserva> reservasCanceladas = sistema.obterReservasCanceladasNoPeriodo(LocalDate.of(2024, 12, 1), LocalDate.of(2024, 12, 31));
        System.out.println("Reservas canceladas no período:");
        for (Reserva reserva : reservasCanceladas) {
            System.out.println(reserva);
        }

        // Testar consulta de reserva por cliente
        System.out.println("\n=== Teste: Consulta de Reserva por Cliente ===");
        sistema.consultarReservaPorCliente("cliente2"); // Deve encontrar a reserva de cliente2
        sistema.consultarReservaPorCliente("cliente1"); // Deve falhar, pois a reserva foi cancelada
        sistema.consultarReservaPorCliente("clienteX"); // Deve falhar, pois não existe clienteX

        // Testar listagem de reservas por data de check-in
        System.out.println("\n=== Teste: Listagem de Reservas por Data de Check-in ===");
        sistema.listarReservasPorCheckIn(); // Deve listar as reservas ordenadas por data de check-in
    }
}

package SistemaHotel;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static HotelReservaSistema sistema = new HotelReservaSistema();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Pré-definindo quartos no sistema
        inicializarQuartos();

        boolean sair = false;

        while (!sair) {
            exibirMenu();
            int opcao = obterOpcao();

            switch (opcao) {
                case 1 -> criarReserva();
                case 2 -> cancelarReserva();
                case 3 -> consultarDisponibilidade();
                case 4 -> gerarRelatorios();
                case 5 -> consultarReservaPorCliente();
                case 6 -> listarReservasPorCheckIn();
                case 7 -> {
                    System.out.println("Saindo do sistema...");
                    sair = true;
                }
                default -> System.out.println("Opção inválida! Tente novamente.");
            }
        }
        scanner.close();
    }

    private static void inicializarQuartos() {
        sistema.adicionarQuarto(101, "Standard");
        sistema.adicionarQuarto(102, "Standard");
        sistema.adicionarQuarto(201, "Luxo");
        sistema.adicionarQuarto(202, "Luxo");
        sistema.adicionarQuarto(301, "Premium");
        sistema.adicionarQuarto(302, "Premium");

        System.out.println("Quartos pré-definidos adicionados ao sistema.");
    }

    private static void exibirMenu() {
        System.out.println("\n=== Sistema de Reservas de Hotel ===");
        System.out.println("1. Criar Reserva");
        System.out.println("2. Cancelar Reserva");
        System.out.println("3. Consultar Disponibilidade");
        System.out.println("4. Gerar Relatórios");
        System.out.println("5. Consultar Reserva por Cliente");
        System.out.println("6. Listar Reservas por Data de Check-in");
        System.out.println("7. Sair");
        System.out.print("Escolha uma opção: ");
    }

    private static int obterOpcao() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static void criarReserva() {
        try {
            System.out.print("Digite o ID do cliente: ");
            String clienteId = scanner.nextLine();

            System.out.print("Digite o número do quarto: ");
            int numeroQuarto = Integer.parseInt(scanner.nextLine());

            System.out.print("Digite a categoria do quarto: ");
            String categoria = scanner.nextLine();

            System.out.print("Digite a data de check-in (YYYY-MM-DD): ");
            LocalDate checkIn = LocalDate.parse(scanner.nextLine());

            System.out.print("Digite a data de check-out (YYYY-MM-DD): ");
            LocalDate checkOut = LocalDate.parse(scanner.nextLine());

            Reserva novaReserva = new Reserva(clienteId, numeroQuarto, "", checkIn, checkOut, categoria);
            if (sistema.cadastrarReserva(novaReserva)) {
                System.out.println("Reserva criada com sucesso!");
            } else {
                System.out.println("Erro ao criar a reserva. Verifique a disponibilidade.");
            }
        } catch (Exception e) {
            System.out.println("Erro ao criar a reserva: " + e.getMessage());
        }
    }

    private static void cancelarReserva() {
        System.out.print("Digite o ID do cliente para cancelar a reserva: ");
        String clienteId = scanner.nextLine();

        sistema.cancelarReserva(clienteId);
    }

    private static void consultarDisponibilidade() {
        try {
            System.out.print("Digite a data para consulta (YYYY-MM-DD): ");
            LocalDate data = LocalDate.parse(scanner.nextLine());

            System.out.print("Digite a categoria do quarto (Standard, Luxo, Premium): ");
            String categoria = scanner.nextLine();

            List<Reserva> disponiveis = sistema.consultarDisponibilidade(data, categoria);

            if (disponiveis.isEmpty()) {
                System.out.println("Nenhum quarto disponível para a data e categoria informadas.");
            } else {
                System.out.println("Quartos disponíveis:");
                for (Reserva quarto : disponiveis) {
                    System.out.println(quarto);
                }
            }
        } catch (Exception e) {
            System.out.println("Erro na consulta de disponibilidade: " + e.getMessage());
        }
    }

    private static void gerarRelatorios() {
        try {
            System.out.print("Digite a data de início do relatório (YYYY-MM-DD): ");
            LocalDate inicio = LocalDate.parse(scanner.nextLine());

            System.out.print("Digite a data de fim do relatório (YYYY-MM-DD): ");
            LocalDate fim = LocalDate.parse(scanner.nextLine());

            System.out.println("\nRelatório de Taxa de Ocupação:");
            sistema.gerarRelatorioTaxaOcupacao(inicio, fim);

            System.out.println("\nRelatório de Cancelamentos:");
            sistema.gerarRelatorioCancelamentos(inicio, fim);

            System.out.println("\nRelatório de Quartos Mais e Menos Reservados:");
            sistema.gerarRelatorioQuartosMaisEMenosReservados();

            System.out.println("\nRelatório de Alerta de Capacidade:");
            sistema.gerarAlertaCapacidade(90.0);
        } catch (Exception e) {
            System.out.println("Erro ao gerar relatórios: " + e.getMessage());
        }
    }

    private static void consultarReservaPorCliente() {
        System.out.print("Digite o ID do cliente para consultar a reserva: ");
        String clienteId = scanner.nextLine();

        Reserva reserva = sistema.consultarReservaPorCliente(clienteId);
        if (reserva != null) {
            System.out.println("Reserva encontrada: " + reserva);
        } else {
            System.out.println("Nenhuma reserva encontrada para o cliente ID: " + clienteId);
        }
    }

    private static void listarReservasPorCheckIn() {
        System.out.println("\nListando todas as reservas em ordem de check-in:");
        List<Reserva> reservasOrdenadas = sistema.listarReservasPorCheckIn();

        if (reservasOrdenadas.isEmpty()) {
            System.out.println("Nenhuma reserva encontrada.");
        } else {
            for (Reserva reserva : reservasOrdenadas) {
                System.out.println(reserva);
            }
        }
    }
}

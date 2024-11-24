package SistemaHotel;

import java.time.LocalDate;
import java.util.*;

public class HotelReservaSistema {

	private RedBlackTree<Reserva> reservas = new RedBlackTree<>();
	private RedBlackTree<Reserva> historico = new RedBlackTree<>();
	private Map<Integer, Reserva> quartosDisponiveis = new HashMap<>();

	public boolean cadastrarReserva(Reserva novaReserva) {
		if (!validarReserva(novaReserva)) return false;

		Reserva reservaConflitante = buscarConflito(novaReserva);
		if (reservaConflitante != null) {
			System.out.println("Erro: Conflito com reserva existente: " + reservaConflitante);
			return false;
		}

		reservas.insert(novaReserva);
		quartosDisponiveis.get(novaReserva.getNumeroQuarto()).setStatus("Reservado");
		System.out.println("Reserva cadastrada com sucesso!");
		return true;
	}

	private boolean validarReserva(Reserva reserva) {
		if (reserva.getCheckIn() == null || reserva.getCheckOut() == null) {
			System.out.println("Erro: Datas de check-in e check-out são obrigatórias.");
			return false;
		}
		if (reserva.getCheckIn().isAfter(reserva.getCheckOut())) {
			System.out.println("Erro: Check-out deve ser após o check-in.");
			return false;
		}
		if (!quartosDisponiveis.containsKey(reserva.getNumeroQuarto())) {
			System.out.println("Erro: Quarto não encontrado.");
			return false;
		}
		return true;
	}

	private Reserva buscarConflito(Reserva novaReserva) {
		for (Reserva reserva : reservas.inOrderTraversal()) {
			if (reserva.getNumeroQuarto() == novaReserva.getNumeroQuarto() &&
					novaReserva.getCheckIn().isBefore(reserva.getCheckOut()) &&
					novaReserva.getCheckOut().isAfter(reserva.getCheckIn())) {
				return reserva;
			}
		}
		return null;
	}

	public void cancelarReserva(String clienteId) {
		Reserva reservaParaCancelar = reservas.search(new Reserva(clienteId), Reserva::compareByClienteId);
		if (reservaParaCancelar != null) {
			reservas.delete(reservaParaCancelar, Reserva::compareByClienteId);
			historico.insert(reservaParaCancelar);
			quartosDisponiveis.get(reservaParaCancelar.getNumeroQuarto()).setStatus("Disponível");
			System.out.println("Reserva cancelada com sucesso!");
		} else {
			System.out.println("Erro: Reserva não encontrada para o cliente ID: " + clienteId);
		}
	}

	public Reserva consultarReservaPorCliente(String clienteId) {
		Reserva reservaBuscada = new Reserva(clienteId);
		Reserva reservaEncontrada = reservas.search(reservaBuscada, Reserva::compareByClienteId);

		if (reservaEncontrada != null) {
			System.out.println("Reserva encontrada: " + reservaEncontrada);
			return reservaEncontrada;
		} else {
			System.out.println("Nenhuma reserva encontrada para o cliente ID: " + clienteId);
			return null;
		}
	}

	public List<Reserva> listarReservasPorCheckIn() {
		List<Reserva> reservasOrdenadas = new ArrayList<>(reservas.inOrderTraversal());
		System.out.println("Reservas ordenadas por data de check-in:");
		for (Reserva reserva : reservasOrdenadas) {
			System.out.println(reserva);
		}
		return reservasOrdenadas;
	}

	public List<Reserva> consultarDisponibilidade(LocalDate data, String categoria) {
		List<Reserva> quartosLivres = new ArrayList<>();
		for (Reserva quarto : quartosDisponiveis.values()) {
			if (quarto.getCategoriaQuarto().equalsIgnoreCase(categoria) &&
					quarto.getStatus().equals("Disponível")) {
				quartosLivres.add(quarto);
			}
		}
		return quartosLivres;
	}

	public void gerarRelatorioTaxaOcupacao(LocalDate inicio, LocalDate fim) {
		long totalDias = inicio.until(fim).getDays() + 1;
		long diasOcupados = reservas.inOrderTraversal().stream()
				.filter(reserva -> reserva.getCheckIn().isBefore(fim) && reserva.getCheckOut().isAfter(inicio))
				.mapToLong(reserva -> calcularSobreposicao(inicio, fim, reserva.getCheckIn(), reserva.getCheckOut()))
				.sum();
		double taxaOcupacao = (diasOcupados / (double) totalDias) * 100;
		System.out.printf("Taxa de Ocupação: %.2f%%%n", taxaOcupacao);
	}

	private long calcularSobreposicao(LocalDate inicio1, LocalDate fim1, LocalDate inicio2, LocalDate fim2) {
		LocalDate inicio = inicio1.isAfter(inicio2) ? inicio1 : inicio2;
		LocalDate fim = fim1.isBefore(fim2) ? fim1 : fim2;
		return inicio.until(fim).getDays() + 1;
	}

	public void adicionarQuartosProntos(List<int[]> quartosProntos) {
		for (int[] quarto : quartosProntos) {
			int numeroQuarto = quarto[0];
			String categoriaQuarto = quarto[1] == 1 ? "Standard" : "Luxo"; // Exemplo de categorização baseada no ID
			if (!adicionarQuarto(numeroQuarto, categoriaQuarto)) {
				System.out.println("Erro ao adicionar quarto " + numeroQuarto);
			}
		}
	}

	public boolean adicionarQuarto(int numeroQuarto, String categoria) {
		if (quartosDisponiveis.containsKey(numeroQuarto)) {
			System.out.println("Erro: Quarto já existe.");
			return false;  
		}

		Reserva novoQuarto = new Reserva();
		novoQuarto.setNumeroQuarto(numeroQuarto);
		novoQuarto.setCategoriaQuarto(categoria);
		novoQuarto.setStatus("Disponível"); 
		quartosDisponiveis.put(numeroQuarto, novoQuarto);

		System.out.println("Quarto " + numeroQuarto + " criado com sucesso!");
		return true;  
	}

	public List<Reserva> obterReservasNoPeriodo(LocalDate inicio, LocalDate fim) {
		if (inicio == null || fim == null) {
			System.out.println("Erro: As datas de início e fim não podem ser nulas.");
			return new ArrayList<>();
		}
		List<Reserva> reservasNoPeriodo = new ArrayList<>();
		for (Reserva reserva : reservas.inOrderTraversal()) {
			if (reserva.getCheckIn().isBefore(fim) && reserva.getCheckOut().isAfter(inicio)) {
				reservasNoPeriodo.add(reserva);
			}
		}
		return reservasNoPeriodo;
	}

	public List<Reserva> obterReservasCanceladasNoPeriodo(LocalDate inicio, LocalDate fim) {
		if (inicio == null || fim == null) {
			System.out.println("Erro: As datas de início e fim não podem ser nulas.");
			return new ArrayList<>();
		}
		List<Reserva> reservasCanceladasNoPeriodo = new ArrayList<>();
		for (Reserva reserva : historico.inOrderTraversal()) {
			if (reserva.getCheckOut() != null &&
					reserva.getCheckOut().isAfter(inicio) &&
					reserva.getCheckOut().isBefore(fim)) {
				reservasCanceladasNoPeriodo.add(reserva);
			}
		}
		return reservasCanceladasNoPeriodo;
	}

	public RedBlackTree<Reserva> getReservasPorHotel() {
		return reservas;
	}

	public List<Reserva> listarQuartosDisponiveisPorCategoria(LocalDate dataInicio, LocalDate dataFim, String categoria) {
		if (dataInicio == null || dataFim == null) {
			System.out.println("Erro: As datas de início e fim não podem ser nulas.");
			return new ArrayList<>();
		}

		List<Reserva> quartosDisponiveisLista = new ArrayList<>();

		for (Reserva reserva : quartosDisponiveis.values()) {
			if (reserva.getStatus().equals("Disponível") &&
					reserva.getCategoriaQuarto().equalsIgnoreCase(categoria) &&
					(dataFim.isBefore(reserva.getCheckIn()) || dataInicio.isAfter(reserva.getCheckOut()))) {
				quartosDisponiveisLista.add(reserva);
			}
		}

		return quartosDisponiveisLista;
	}

	public void gerarRelatorioQuartosMaisEMenosReservados() {
		Map<Integer, Integer> contadorReservas = new HashMap<>();

		// Contar reservas por quarto
		for (Reserva reserva : reservas.inOrderTraversal()) {
			contadorReservas.put(reserva.getNumeroQuarto(),
					contadorReservas.getOrDefault(reserva.getNumeroQuarto(), 0) + 1);
		}

		if (contadorReservas.isEmpty()) {
			System.out.println("Nenhuma reserva registrada.");
			return;
		}

		int maisReservado = -1, menosReservado = -1, maxReservas = 0, minReservas = Integer.MAX_VALUE;

		for (Map.Entry<Integer, Integer> entry : contadorReservas.entrySet()) {
			int numeroQuarto = entry.getKey();
			int qtdReservas = entry.getValue();

			if (qtdReservas > maxReservas) {
				maxReservas = qtdReservas;
				maisReservado = numeroQuarto;
			}
			if (qtdReservas < minReservas) {
				minReservas = qtdReservas;
				menosReservado = numeroQuarto;
			}
		}

		System.out.println("Quarto mais reservado: " + maisReservado + " com " + maxReservas + " reservas.");

		if (maxReservas == minReservas) {
			System.out.println("Todos os quartos tiveram o mesmo número de reservas: " + maxReservas);
		} else {
			System.out.println("Quarto menos reservado: " + menosReservado + " com " + minReservas + " reservas.");
		}
	}

	public void gerarRelatorioCancelamentos(LocalDate inicio, LocalDate fim) {
		if (inicio == null || fim == null) {
			System.out.println("Erro: Datas de início e fim não podem ser nulas.");
			return;
		}

		int totalCancelamentos = 0;
		for (Reserva reserva : historico.inOrderTraversal()) {
			if (reserva.getCheckOut() != null &&
					(reserva.getCheckOut().isAfter(inicio) || reserva.getCheckOut().isEqual(inicio)) &&
					(reserva.getCheckOut().isBefore(fim) || reserva.getCheckOut().isEqual(fim))) {
				totalCancelamentos++;
			}
		}
		System.out.println("Número de cancelamentos entre " + inicio + " e " + fim + ": " + totalCancelamentos);
	}

	public void gerarAlertaCapacidade(double limiteOcupacao) {
		int totalReservasAtivas = 0;

		for (Reserva reserva : reservas.inOrderTraversal()) {
			if (reserva.getStatus().equals("Confirmada")) {
				totalReservasAtivas++;
			}
		}

		int totalQuartos = quartosDisponiveis.size();
		if (totalQuartos == 0) {
			System.out.println("Erro: Nenhum quarto disponível no sistema.");
			return;
		}

		double ocupacaoAtual = (totalReservasAtivas / (double) totalQuartos) * 100;
		System.out.printf("Taxa de ocupação atual: %.2f%%%n", ocupacaoAtual);

		if (ocupacaoAtual >= limiteOcupacao) {
			System.out.println("Alerta: Capacidade ultrapassou " + limiteOcupacao + "%.");
		}
	}
}



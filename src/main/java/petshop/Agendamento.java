package petshop;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Agendamento {
    static final List<Agendamento> agendamentos = new ArrayList<>();
    private static int proximoId = 1;

    private int id;
    private LocalDate data;
    private LocalTime horario;
    private StatusAgendamento status;
    private String observacoes;
    private Animal animal;
    private Servico servico;
    private Funcionario profissional;


    Agendamento(int id, LocalDate data, LocalTime horario, StatusAgendamento status, String observacoes,
                 Animal animal, Servico servico, Funcionario profissional) {
        this.id = id;
        this.data = data;
        this.horario = horario;
        this.status = status;
        this.observacoes = observacoes;
        this.animal = animal;
        this.servico = servico;
        this.profissional = profissional;
        if (id >= proximoId) {
            proximoId = id + 1;
        }
    }

     public int getId() {
        return id;
    }

    public LocalDate getData() {
        return data;
    }

    public LocalTime getHorario() {
        return horario;
    }

    public StatusAgendamento getStatus() {
        return status;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public Animal getAnimal() {
        return animal;
    }

    public Servico getServico() {
        return servico;
    }

    public Funcionario getProfissional() {
        return profissional;
    }

    public static Agendamento cadastrarAgendamento(Animal animal, Servico servico, Funcionario profissional, LocalDate data, LocalTime horario, String observacoes){
        if (animal == null || servico == null || profissional == null || data == null || horario == null) {
            throw new IllegalArgumentException("Animal, serviço, prestador, data e horário são obrigatórios.");
        }
        if (!animal.isAtivo()) {
            throw new IllegalStateException("O animal precisa estar ativo.");
        }
        if (!animal.getCliente().isAtivo()) {
            throw new IllegalStateException("O cliente responsável pelo animal precisa estar ativo.");
        }
        if (!servico.isAtivo()) {
            throw new IllegalStateException("O serviço precisa estar ativo.");
        }
        if (!profissional.isAtivo() || profissional.getCargo() != Cargo.PRESTADOR_SERVICO) {
            throw new IllegalStateException("O prestador precisa estar ativo e ter o cargo de prestador de serviço.");
        }
        if (!LocalDateTime.of(data, horario).isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("A data e o horário devem ser futuros.");
        }
        if (!verificarDisponibilidade(data, horario, profissional, servico)) {
            throw new IllegalStateException("Horário indisponível para este prestador.");
        }
        Agendamento agendamento = new Agendamento(proximoId, data, horario, StatusAgendamento.AGENDADO,
                Validacao.opcional(observacoes), animal, servico, profissional);
        agendamentos.add(agendamento);
        return agendamento;
    }

    
    public static Agendamento consultarAgendamento(int id){
         for (Agendamento agendamento : agendamentos) {
            if (agendamento.id == id) {
                return agendamento;
            }
        }
        return null;
    }

    public void atualizarAgendamento(String observacoes){
         if (status != StatusAgendamento.AGENDADO) {
            throw new IllegalStateException("Agendamentos cancelados ou concluídos não podem ser modificados.");
        }
        this.observacoes = Validacao.opcional(observacoes);
    }

    public boolean cancelarAgendamento(){
         if (status != StatusAgendamento.AGENDADO) {
            return false;
        }
        this.status = StatusAgendamento.CANCELADO;
        return true;

    }

    public static List<Agendamento> listarAgendamentos(){
        return new ArrayList<>(agendamentos);
    }

    public static List<Agendamento> buscarPorData(LocalDate data){
        List<Agendamento> encontrados = new ArrayList<>();
        for (Agendamento agendamento : agendamentos) {
            if (agendamento.data.equals(data)) {
                encontrados.add(agendamento);
            }
        }
        return encontrados;
    }

    public static List<Agendamento> buscarPorStatus(StatusAgendamento status){
        List<Agendamento> encontrados = new ArrayList<>();
        for (Agendamento agendamento : agendamentos) {
            if (agendamento.status == status) {
                encontrados.add(agendamento);
            }
        }
        return encontrados;
    }

    public static List<Agendamento> buscarPorFuncionario(Funcionario profissional){
        List<Agendamento> encontrados = new ArrayList<>();
        for (Agendamento agendamento : agendamentos) {
            if (agendamento.profissional.getId() == profissional.getId()) {
                encontrados.add(agendamento);
            }
        }
        return encontrados;
    }

    public static List<Agendamento> buscarPorAnimal(Animal animal){
        List<Agendamento> encontrados = new ArrayList<>();
        for (Agendamento agendamento : agendamentos) {
            if (agendamento.animal.getId() == animal.getId()) {
                encontrados.add(agendamento);
            }
        }
        return encontrados;
    }

    public boolean reagendar(LocalDate novaData, LocalTime novoHorario){
      
        if (novaData == null || novoHorario == null) {
            throw new IllegalArgumentException("A nova data e o novo horário são obrigatórios.");
        }

        if (status != StatusAgendamento.AGENDADO) {
            throw new IllegalStateException("Agendamentos cancelados ou concluídos não podem ser modificados.");
        }
        if (!LocalDateTime.of(novaData, novoHorario).isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("A nova data e novo horário devem ser futuros.");
        }
        if (!verificarDisponibilidade(novaData, novoHorario, profissional, servico)) {
            throw new IllegalStateException("Horário indisponível para este prestador.");
        }
        this.data = novaData;
        this.horario = novoHorario;
        return true;
    }

    public static boolean verificarDisponibilidade(LocalDate data, LocalTime horario, Funcionario profissional, Servico servico){
         if (data == null || horario == null || profissional == null || servico == null) {
            return false;
        }

        LocalDateTime inicio = LocalDateTime.of(data, horario);
        LocalDateTime fim = inicio.plusMinutes(servico.getDuracaoMinutos());
        for (Agendamento outro : agendamentos) {
            boolean mesmoPrestador = outro.profissional.getId() == profissional.getId();
            if (mesmoPrestador && outro.status == StatusAgendamento.AGENDADO) {
                LocalDateTime inicioOutro = LocalDateTime.of(outro.data, outro.horario);
                LocalDateTime fimOutro = inicioOutro.plusMinutes(outro.servico.getDuracaoMinutos());
                if (inicio.isBefore(fimOutro) && inicioOutro.isBefore(fim)) {
                    return false;
                }
            }
        }
        return true;
    }
    

    public boolean concluirAgendamento(){
         if (status != StatusAgendamento.AGENDADO) {
            return false;
        }
        this.status = StatusAgendamento.CONCLUIDO;
        return true;
    }

    public static String gerarRelatorioAgendamentos(LocalDate dataInicio, LocalDate dataFim){
           if (dataInicio == null || dataFim == null) {
            throw new IllegalArgumentException("As datas inicial e final são obrigatórias.");
        }
        if (dataFim.isBefore(dataInicio)) {
            throw new IllegalArgumentException("A data final não pode ser anterior à data inicial.");
        }
        DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm");
        StringBuilder relatorio = new StringBuilder();
        relatorio.append(Validacao.formatar("RELATÓRIO DE AGENDAMENTOS DE %s A %s%n%n",
                dataInicio.format(formatoData), dataFim.format(formatoData)));
        relatorio.append("=====================\n");
        relatorio.append(Validacao.formatar("%-5s %-11s %-8s %-18s %-22s %-20s %s%n",
                "ID", "Data", "Horário", "Animal", "Serviço", "Prestador", "Status"));
        int totalAgendado = 0;
        int totalCancelado = 0;
        int totalConcluido = 0;
        for (Agendamento agendamento : agendamentos) {
            boolean dentroDoPeriodo = !agendamento.data.isBefore(dataInicio) && !agendamento.data.isAfter(dataFim);
            if (dentroDoPeriodo) {
                relatorio.append(Validacao.formatar("%-5d %-11s %-8s %-18.18s %-22.22s %-20.20s %s%n",
                        agendamento.id, agendamento.data.format(formatoData), agendamento.horario.format(formatoHora),
                        agendamento.animal.getNome(), agendamento.servico.getNome(),
                        agendamento.profissional.getNome(), agendamento.status));
                if (agendamento.status == StatusAgendamento.AGENDADO) {
                    totalAgendado++;
                } else if (agendamento.status == StatusAgendamento.CANCELADO) {
                    totalCancelado++;
                } else {
                    totalConcluido++;
                }
            }
            
        }
        relatorio.append(Validacao.formatar("%nTotais por situação:%n"));
        relatorio.append(Validacao.formatar("  %-10s %d%n", "AGENDADO", totalAgendado));
        relatorio.append(Validacao.formatar("  %-10s %d%n", "CANCELADO", totalCancelado));
        relatorio.append(Validacao.formatar("  %-10s %d%n", "CONCLUIDO", totalConcluido));
        relatorio.append(Validacao.formatar("  %-10s %d%n", "TOTAL", totalAgendado + totalCancelado + totalConcluido));
        relatorio.append("---------------------\n");
        return relatorio.toString(); 

    }

    private void cancelarSeFuturo() {
        boolean futuro = LocalDateTime.of(data, horario).isAfter(LocalDateTime.now());
        if (futuro) {
            cancelarAgendamento();
        }
    }

    public static void cancelarFuturosDoAnimal(Animal animal) {
        for (Agendamento agendamento : agendamentos) {
            if (agendamento.animal.getId() == animal.getId()) {
                agendamento.cancelarSeFuturo();
            }
        }
    }

    public static void cancelarFuturosDoProfissional(Funcionario profissional) {
        for (Agendamento agendamento : agendamentos) {
            if (agendamento.profissional.getId() == profissional.getId()) {
                agendamento.cancelarSeFuturo();
            }
        }
    }

}

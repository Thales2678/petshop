import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Agendamento {
    private int id;
    private LocalDate data;
    private LocalTime horario;
    private StatusAgendamento status;
    private String observacoes;

public static Agendamento cadastrarAgendamento(Animal animal, Servico servico, Funcionario profissional, LocalDate data, LocalTime horario, String observacoes){

}

public static Agendamento consultarAgendamento(int id){

}

public void atualizarAgendamento(String observacoes){

}

public boolean cancelarAgendamento(){

}

public static List<Agendamento> listarAgendamentos(){

}

public static List<Agendamento> buscarPorData(LocalDate data){

}

public static List<Agendamento> buscarPorStatus(StatusAgendamento status){

}

public static List<Agendamento> buscarPorFuncionario(Funcionario profissional){

}

public static List<Agendamento> buscarPorAnimal(Animal animal){

}

public boolean reagendar(LocalDate novaData, LocalTime novoHorario){

}

public static boolean verificarDisponibilidade(LocalDate data, LocalTime horario, Funcionario profissional, Servico servico){

}

public boolean concluirAgendamento(){

}

public static String gerarRelatorioAgendamentos(LocalDate dataInicio, LocalDate dataFim){

}

}

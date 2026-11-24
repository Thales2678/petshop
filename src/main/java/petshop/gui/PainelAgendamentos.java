package petshop.gui;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import petshop.Agendamento;
import petshop.Animal;
import petshop.Cargo;
import petshop.Cliente;
import petshop.Funcionario;
import petshop.Servico;
import petshop.StatusAgendamento;
import petshop.Usuario;


public class PainelAgendamentos extends PainelBase<Agendamento> {
    private static final String TODOS = "Todos";

    private final Usuario usuario;
    private final boolean cliente;
    private final boolean gestao;
    private final boolean prestador;
    private final JTextField filtroData = new JTextField(10);
    private final JComboBox<Object> filtroStatus = new JComboBox<>();

    PainelAgendamentos(Usuario usuario) {
        super("ID", "Data", "Horário", "Animal", "Serviço", "Prestador", "Status", "Observações");
        this.usuario = usuario;
        this.cliente = usuario instanceof Cliente;
        Cargo cargo = usuario instanceof Funcionario ? ((Funcionario) usuario).getCargo() : null;
        this.gestao = cargo == Cargo.ADMINISTRADOR || cargo == Cargo.ATENDENTE;
        this.prestador = cargo == Cargo.PRESTADOR_SERVICO;

        filtroStatus.addItem(TODOS);
        for (StatusAgendamento status : StatusAgendamento.values()) {
            filtroStatus.addItem(status);
        }
        adicionar(new JLabel("Data (dd/mm/aaaa):"));
        adicionar(filtroData);
        adicionar(new JLabel("Status:"));
        adicionar(filtroStatus);
        botao("Filtrar", () -> {});
        if (cliente || gestao) {
            botao("Novo", this::novo);
            botao("Reagendar", this::reagendar);
            botao("Cancelar", this::cancelar);
            botao("Observações", this::atualizarObservacoes);
        }
        if (prestador) {
            botao("Concluir", this::concluir);
        }
        atualizar();
    }

    @Override
    List<Agendamento> carregar() {
        List<Agendamento> lista = escopo();
        String data = filtroData.getText().trim();
        if (!data.isEmpty()) {
            lista = interseccao(lista, Agendamento.buscarPorData(Formatos.lerData(data)));
        }
        Object status = filtroStatus.getSelectedItem();
        if (status instanceof StatusAgendamento) {
            lista = interseccao(lista, Agendamento.buscarPorStatus((StatusAgendamento) status));
        }
        return lista;
    }

    @Override
    Object[] linha(Agendamento a) {
        Animal animal = a.getAnimal();
        Servico servico = a.getServico();
        Funcionario profissional = a.getProfissional();
        return new Object[] {a.getId(), a.getData().format(Formatos.DATA), a.getHorario().format(Formatos.HORA),
            animal == null ? "" : animal.getNome(), servico == null ? "" : servico.getNome(),
            profissional == null ? "" : profissional.getNome(), a.getStatus(), a.getObservacoes()};
    }

    private List<Agendamento> escopo() {
        if (gestao) {
            return Agendamento.listarAgendamentos();
        }
        if (prestador) {
            return Agendamento.buscarPorFuncionario((Funcionario) usuario);
        }
        List<Agendamento> proprios = new ArrayList<>();
        for (Animal animal : Animal.listarAnimal()) {
            if (animal.getCliente().getId() == usuario.getId()) {
                proprios.addAll(Agendamento.buscarPorAnimal(animal));
            }
        }
        proprios.sort(Comparator.comparing(Agendamento::getData).thenComparing(Agendamento::getHorario).thenComparing(Agendamento::getId));
        return proprios;
    }

    private List<Agendamento> interseccao(List<Agendamento> base, List<Agendamento> filtro) {
        Set<Integer> ids = new HashSet<>();
        for (Agendamento agendamento : filtro) {
            ids.add(agendamento.getId());
        }
        List<Agendamento> resultado = new ArrayList<>();
        for (Agendamento agendamento : base) {
            if (ids.contains(agendamento.getId())) {
                resultado.add(agendamento);
            }
        }
        return resultado;
    }

    private void novo() {
        List<Animal> animais = new ArrayList<>();
        for (Animal animal : Animal.listarAnimal()) {
            boolean permitido = gestao || animal.getCliente().getId() == usuario.getId();
            if (animal.isAtivo() && permitido) {
                animais.add(animal);
            }
        }
        List<Servico> servicos = new ArrayList<>();
        for (Servico servico : Servico.listarServicos()) {
            if (servico.isAtivo()) {
                servicos.add(servico);
            }
        }
        List<Funcionario> prestadores = new ArrayList<>();
        for (Funcionario funcionario : Funcionario.buscarFuncionario(Cargo.PRESTADOR_SERVICO)) {
            if (funcionario.isAtivo()) {
                prestadores.add(funcionario);
            }
        }
        if (animais.isEmpty() || servicos.isEmpty() || prestadores.isEmpty()) {
            Dialogos.aviso(this, "É necessário haver ao menos um animal ativo, um serviço ativo e um prestador ativo.");
            return;
        }
        JComboBox<Animal> campoAnimal = new JComboBox<>(animais.toArray(new Animal[0]));
        JComboBox<Servico> campoServico = new JComboBox<>(servicos.toArray(new Servico[0]));
        JComboBox<Funcionario> campoPrestador = new JComboBox<>(prestadores.toArray(new Funcionario[0]));
        JTextField campoData = Dialogos.campo("");
        JTextField campoHorario = Dialogos.campo("");
        JTextField campoObservacoes = Dialogos.campo("");
        boolean confirmado = Dialogos.formulario(this, "Novo agendamento",
                new String[] {"Animal", "Serviço", "Prestador", "Data (dd/mm/aaaa)", "Horário (hh:mm)", "Observações"},
                new JComponent[] {campoAnimal, campoServico, campoPrestador, campoData, campoHorario,
                    campoObservacoes});
        if (confirmado) {
            Agendamento.cadastrarAgendamento((Animal) campoAnimal.getSelectedItem(),
                    (Servico) campoServico.getSelectedItem(), (Funcionario) campoPrestador.getSelectedItem(),
                    Formatos.lerData(campoData.getText()), Formatos.lerHora(campoHorario.getText()),
                    campoObservacoes.getText());
        }
    }

    private void reagendar() {
        Agendamento agendamento = selecionado();
        if (agendamento == null) {
            return;
        }
        JTextField campoData = Dialogos.campo(agendamento.getData().format(Formatos.DATA));
        JTextField campoHorario = Dialogos.campo(agendamento.getHorario().format(Formatos.HORA));
        boolean confirmado = Dialogos.formulario(this, "Reagendar",
                new String[] {"Nova data (dd/mm/aaaa)", "Novo horário (hh:mm)"},
                new JComponent[] {campoData, campoHorario});
        if (!confirmado) {
            return;
        }
        LocalDate data = Formatos.lerData(campoData.getText());
        LocalTime horario = Formatos.lerHora(campoHorario.getText());
        if (!agendamento.reagendar(data, horario)) {
            Dialogos.aviso(this, "Horário indisponível para o prestador neste período.");
        }
    }

    private void cancelar() {
        Agendamento agendamento = selecionado();
        if (agendamento == null) {
            return;
        }
        if (Dialogos.confirmar(this, "Cancelar o agendamento #" + agendamento.getId() + "?")
                                  && !agendamento.cancelarAgendamento()) {
            Dialogos.aviso(this, "Somente agendamentos com status AGENDADO podem ser cancelados.");
        }
    }

    private void atualizarObservacoes() {
        Agendamento agendamento = selecionado();
        if (agendamento == null) {
            return;
        }
        JTextField campo = Dialogos.campo(agendamento.getObservacoes());
        boolean confirmado = Dialogos.formulario(this, "Observações", new String[] {"Observações"},
                new JComponent[] {campo});
        if (confirmado) {
            agendamento.atualizarAgendamento(campo.getText());
        }
    }

    private void concluir() {
        Agendamento agendamento = selecionado();
        if (agendamento == null) {
            return;
        }
        Funcionario responsavel = agendamento.getProfissional();
        if (responsavel == null || responsavel.getId() != usuario.getId()) {
            Dialogos.erro(this, "Apenas o prestador de serviço responsável pode concluir o agendamento.");
            return;
        }
        if (Dialogos.confirmar(this, "Concluir o agendamento #" + agendamento.getId() + "?")
                && !agendamento.concluirAgendamento()) {
            Dialogos.aviso(this, "Somente agendamentos com status AGENDADO podem ser concluídos.");
        }
    }
}

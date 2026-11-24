package petshop.gui;

import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import petshop.Cargo;
import petshop.Funcionario;

public class PainelFuncionarios  extends PainelBase<Funcionario>{
    private static final String TODOS = "Todos";

    private final JComboBox<Object> filtro = new JComboBox<>();

     PainelFuncionarios() {
        super("ID", "Nome", "E-mail", "Matrícula", "Cargo", "Situação");
        filtro.addItem(TODOS);
        for (Cargo cargo : Cargo.values()) {
            filtro.addItem(cargo);
        }
        adicionar(new JLabel("Cargo:"));
        adicionar(filtro);
        botao("Buscar", () -> {});
        botao("Novo", this::novo);
        botao("Editar", this::editar);
        botao("Desativar", this::desativar);
        atualizar();
    }

     @Override
    List<Funcionario> carregar() {
        Object escolhido = filtro.getSelectedItem();
        return Funcionario.buscarFuncionario(escolhido instanceof Cargo ? (Cargo) escolhido : null);
    }

    @Override
    Object[] linha(Funcionario f) {
        return new Object[] {f.getId(), f.getNome(), f.getEmail(), f.getMatricula(), f.getCargo(),
            Formatos.situacao(f.isAtivo())};
    }

    private void novo() {
        JTextField nome = Dialogos.campo("");
        JTextField email = Dialogos.campo("");
        JPasswordField senha = new JPasswordField(24);
        JTextField matricula = Dialogos.campo("");
        JComboBox<Cargo> cargo = new JComboBox<>(Cargo.values());
        boolean confirmado = Dialogos.formulario(this, "Novo funcionário",
                new String[] {"Nome", "E-mail", "Senha", "Matrícula", "Cargo"},
                new JComponent[] {nome, email, senha, matricula, cargo});
        if (confirmado) {
            Funcionario.cadastrarFuncionario(nome.getText(), email.getText(), new String(senha.getPassword()),
                    matricula.getText(), (Cargo) cargo.getSelectedItem());
        }
    }

    private void editar() {
        Funcionario funcionario = selecionado();
        if (funcionario == null) {
            return;
        }
        JTextField nome = Dialogos.campo(funcionario.getNome());
        JTextField email = Dialogos.campo(funcionario.getEmail());
        JPasswordField senha = new JPasswordField(24);
        JComboBox<Cargo> cargo = new JComboBox<>(Cargo.values());
        cargo.setSelectedItem(funcionario.getCargo());
        boolean confirmado = Dialogos.formulario(this, "Editar funcionário",
                new String[] {"Nome", "E-mail", "Nova senha", "Cargo"},
                new JComponent[] {nome, email, senha, cargo});
        if (confirmado) {
            funcionario.atualizarUsuario(nome.getText(), email.getText(), new String(senha.getPassword()));
            funcionario.atualizarFuncionario((Cargo) cargo.getSelectedItem());
        }
    }

    private void desativar() {
        Funcionario funcionario = selecionado();
        if (funcionario == null) {
            return;
        }
        if (!funcionario.isAtivo()) {
            Dialogos.aviso(this, "Este funcionário já está inativo.");
            return;
        }
        if (Dialogos.confirmar(this, "Desativar " + funcionario.getNome()
                + "?\nSe for prestador de serviço, os agendamentos futuros ainda ativos serão cancelados.")) {
            funcionario.desativarFuncionario();
        }
    }




}

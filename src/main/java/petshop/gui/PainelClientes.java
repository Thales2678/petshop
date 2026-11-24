package petshop.gui;

import java.util.List;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import petshop.Cliente;

public class PainelClientes extends PainelBase<Cliente>{
    private final JTextField busca = new JTextField(16);

    PainelClientes() {
        super("ID", "Nome", "E-mail", "Telefone", "Endereço", "Situação");
        adicionar(new JLabel("Nome:"));
        adicionar(busca);
        botao("Buscar", () -> {});
        botao("Novo", this::novo);
        botao("Editar", this::editar);
        botao("Desativar", this::desativar);
        atualizar();
    }

    @Override
    List<Cliente> carregar() {
        String nome = busca.getText().trim();
        return nome.isEmpty() ? Cliente.listarCliente() : Cliente.buscarCliente(nome);
    }

    @Override
    Object[] linha(Cliente c) {
        return new Object[] {c.getId(), c.getNome(), c.getEmail(), c.getTelefone(), c.getEndereco(),
            Formatos.situacao(c.isAtivo())};
    }

    private void novo() {
        JTextField nome = Dialogos.campo("");
        JTextField email = Dialogos.campo("");
        JPasswordField senha = new JPasswordField(24);
        JTextField telefone = Dialogos.campo("");
        JTextField endereco = Dialogos.campo("");
        boolean confirmado = Dialogos.formulario(this, "Novo cliente",
                new String[] {"Nome", "E-mail", "Senha", "Telefone", "Endereço"},
                new JComponent[] {nome, email, senha, telefone, endereco});
        if (confirmado) {
            Cliente.cadastrarCliente(nome.getText(), email.getText(), new String(senha.getPassword()),
                    telefone.getText(), endereco.getText());
        }
    }

    private void editar() {
        Cliente cliente = selecionado();
        if (cliente == null) {
            return;
        }
        JTextField nome = Dialogos.campo(cliente.getNome());
        JTextField email = Dialogos.campo(cliente.getEmail());
        JPasswordField senha = new JPasswordField(24);
        JTextField telefone = Dialogos.campo(cliente.getTelefone());
        JTextField endereco = Dialogos.campo(cliente.getEndereco());
        boolean confirmado = Dialogos.formulario(this, "Editar cliente",
                new String[] {"Nome", "E-mail", "Nova senha", "Telefone", "Endereço"},
                new JComponent[] {nome, email, senha, telefone, endereco});
        if (confirmado) {
            cliente.atualizarUsuario(nome.getText(), email.getText(), new String(senha.getPassword()));
            cliente.atualizarCliente(telefone.getText(), endereco.getText());
        }
    }

    private void desativar() {
        Cliente cliente = selecionado();
        if (cliente == null) {
            return;
        }
        if (!cliente.isAtivo()) {
            Dialogos.aviso(this, "Este cliente já está inativo.");
            return;
        }
        if (Dialogos.confirmar(this, "Desativar " + cliente.getNome()
                + "?\nOs agendamentos futuros ainda ativos dos animais deste cliente serão cancelados.")) {
            cliente.desativarCliente();
        }
    }
}

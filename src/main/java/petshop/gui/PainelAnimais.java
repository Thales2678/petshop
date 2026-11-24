package petshop.gui;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import petshop.Animal;
import petshop.Cliente;
import petshop.Usuario;

public class PainelAnimais extends PainelBase<Animal> {
    private final JTextField busca = new JTextField(16);
    private final Usuario usuario;
    private final boolean gerencia;

    PainelAnimais(Usuario usuario, boolean gerencia) {
        super("ID", "Nome", "Espécie", "Raça", "Idade", "Cliente", "Situação");
        this.usuario = usuario;
        this.gerencia = gerencia;
        adicionar(new JLabel("Nome:"));
        adicionar(busca);
        botao("Buscar", () -> {
            atualizar();
        });
        if (gerencia) {
            botao("Novo", this::novo);
            botao("Editar", this::editar);
            botao("Desativar", this::desativar);
        }
        atualizar();
    }

    @Override
    List<Animal> carregar() {
        String nome = busca.getText().trim();
        List<Animal> animais = nome.isEmpty() ? Animal.listarAnimal() : Animal.buscarAnimal(nome);
        if (gerencia) {
            return animais;
        }
        List<Animal> proprios = new ArrayList<>();
        for (Animal animal : animais) {
            if (animal.getCliente().getId() == usuario.getId()) {
                proprios.add(animal);
            }
        }
        return proprios;
    }

    @Override
    Object[] linha(Animal a) {
        Cliente dono = a.getCliente();
        return new Object[] {a.getId(), a.getNome(), a.getEspecie(), a.getRaca(), a.getIdade(),
            dono == null ? "" : dono.getNome(), Formatos.situacao(a.isAtivo())};
    }

    private void novo() {
        List<Cliente> ativos = new ArrayList<>();
        for (Cliente cliente : Cliente.listarCliente()) {
            if (cliente.isAtivo()) {
                ativos.add(cliente);
            }
        }
        if (ativos.isEmpty()) {
            Dialogos.aviso(this, "Cadastre um cliente ativo antes de cadastrar animais.");
            return;
        }
        JComboBox<Cliente> clientes = new JComboBox<>(ativos.toArray(new Cliente[0]));
        JTextField nome = Dialogos.campo("");
        JTextField especie = Dialogos.campo("");
        JTextField raca = Dialogos.campo("");
        JTextField idade = Dialogos.campo("0");
        boolean confirmado = Dialogos.formulario(this, "Novo animal",
                new String[] {"Cliente", "Nome", "Espécie", "Raça", "Idade"},
                new JComponent[] {clientes, nome, especie, raca, idade});
        if (confirmado) {
            Animal.cadastrarAnimal((Cliente) clientes.getSelectedItem(), nome.getText(), especie.getText(),
                    raca.getText(), Formatos.inteiro(idade.getText(), "Idade"));
        }
    }

    private void editar() {
        Animal animal = selecionado();
        if (animal == null) {
            return;
        }
        JTextField nome = Dialogos.campo(animal.getNome());
        JTextField especie = Dialogos.campo(animal.getEspecie());
        JTextField raca = Dialogos.campo(animal.getRaca());
        JTextField idade = Dialogos.campo(String.valueOf(animal.getIdade()));
        boolean confirmado = Dialogos.formulario(this, "Editar animal",
                new String[] {"Nome", "Espécie", "Raça", "Idade"},
                new JComponent[] {nome, especie, raca, idade});
        if (confirmado) {
            animal.atualizarAnimal(nome.getText(), especie.getText(), raca.getText(),
                    Formatos.inteiro(idade.getText(), "Idade"));
        }
    }

    private void desativar() {
        Animal animal = selecionado();
        if (animal == null) {
            return;
        }
        if (!animal.isAtivo()) {
            Dialogos.aviso(this, "Este animal já está inativo.");
            return;
        }
        if (Dialogos.confirmar(this, "Desativar " + animal.getNome()
                + "?\nOs agendamentos futuros ainda ativos deste animal serão cancelados.")) {
            animal.desativarAnimal();
        }
    }
}

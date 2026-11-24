package petshop.gui;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import petshop.Servico;

public class PainelServicos extends PainelBase<Servico>{
    private final JTextField busca = new JTextField(16);
    private final boolean somenteAtivos;

    PainelServicos(boolean gerencia, boolean somenteAtivos) {
        super("ID", "Nome", "Descrição", "Preço", "Duração (min)", "Situação");
        this.somenteAtivos = somenteAtivos;
        adicionar(new JLabel("Nome:"));
        adicionar(busca);
        botao("Buscar", () -> {});
        if (gerencia) {
            botao("Novo", this::novo);
            botao("Editar", this::editar);
            botao("Desativar", this::desativar);
        }
        atualizar();
    }

    @Override
    List<Servico> carregar() {
        String nome = busca.getText().trim();
        List<Servico> servicos = nome.isEmpty() ? Servico.listarServicos() : Servico.buscarServico(nome);
        if (!somenteAtivos) {
            return servicos;
        }
        List<Servico> ativos = new ArrayList<>();
        for (Servico servico : servicos) {
            if (servico.isAtivo()) {
                ativos.add(servico);
            }
        }
        return ativos;
    }

    @Override
    Object[] linha(Servico s) {
        return new Object[] {s.getId(), s.getNome(), s.getDescricao(), Formatos.moeda(s.getPreco()),
            s.getDuracaoMinutos(), Formatos.situacao(s.isAtivo())};
    }

    private void novo() {
        JTextField nome = Dialogos.campo("");
        JTextField descricao = Dialogos.campo("");
        JTextField preco = Dialogos.campo("");
        JTextField duracao = Dialogos.campo("");
        boolean confirmado = Dialogos.formulario(this, "Novo serviço",
                new String[] {"Nome", "Descrição", "Preço (R$)", "Duração (min)"},
                new JComponent[] {nome, descricao, preco, duracao});
        if (confirmado) {
            Servico.cadastrarServico(nome.getText(), descricao.getText(),
                    Formatos.decimal(preco.getText(), "Preço"), Formatos.inteiro(duracao.getText(), "Duração"));
        }
    }

    private void editar() {
        Servico servico = selecionado();
        if (servico == null) {
            return;
        }
        JTextField nome = Dialogos.campo(servico.getNome());
        JTextField descricao = Dialogos.campo(servico.getDescricao());
        JTextField preco = Dialogos.campo(String.valueOf(servico.getPreco()));
        JTextField duracao = Dialogos.campo(String.valueOf(servico.getDuracaoMinutos()));
        boolean confirmado = Dialogos.formulario(this, "Editar serviço",
                new String[] {"Nome", "Descrição", "Preço (R$)", "Duração (min)"},
                new JComponent[] {nome, descricao, preco, duracao});
        if (confirmado) {
            servico.atualizarServico(nome.getText(), descricao.getText(),
                    Formatos.decimal(preco.getText(), "Preço"), Formatos.inteiro(duracao.getText(), "Duração"));
        }
    }

    private void desativar() {
        Servico servico = selecionado();
        if (servico == null) {
            return;
        }
        if (!servico.isAtivo()) {
            Dialogos.aviso(this, "Este serviço já está inativo.");
            return;
        }
        if (Dialogos.confirmar(this, "Desativar o serviço " + servico.getNome() + "?")) {
            servico.desativarServico();
        }
    }
}

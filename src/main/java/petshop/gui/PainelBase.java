package petshop.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public abstract class PainelBase<T> extends JPanel {
    private final DefaultTableModel modelo;
    private final JTable tabela;
    private final JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT));
    private List<T> itens = new ArrayList<>();

    PainelBase(String... colunas) {
        super(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        modelo = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };
        tabela = new JTable(modelo);

        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setRowHeight(24);
        tabela.setAutoCreateRowSorter(true);
        add(barra, BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
    }

    abstract List<T> carregar();

    abstract Object[] linha(T item);

    void atualizar() {
        try {
            itens = carregar();
        } catch (RuntimeException e) {
            Dialogos.erro(this, Dialogos.mensagem(e));
            itens = new ArrayList<>();
        }
        modelo.setRowCount(0);
        for (T item : itens) {
            modelo.addRow(linha(item));
        }
    }

    T selecionado() {
        int linhaVisual = tabela.getSelectedRow();
        if (linhaVisual < 0) {
            Dialogos.aviso(this, "Selecione um registro na tabela.");
            return null;
        }
        return itens.get(tabela.convertRowIndexToModel(linhaVisual));
    }

    void adicionar(Component componente) {
        barra.add(componente);
    }

    JButton botao(String texto, Runnable acao) {
        JButton botao = new JButton(texto);
        botao.addActionListener(evento -> executar(acao));
        barra.add(botao);
        return botao;
    }

    void executar(Runnable acao) {
        try {
            acao.run();
        } catch (RuntimeException e) {
            Dialogos.erro(this, Dialogos.mensagem(e));
        }
        atualizar();
    }
}

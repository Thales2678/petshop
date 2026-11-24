package petshop.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.util.function.Supplier;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import petshop.Agendamento;
import petshop.Cliente;
import petshop.Funcionario;
import petshop.Servico;

public class PainelRelatorios extends JPanel{
   
 private final JTextArea saida = new JTextArea();
  
  PainelRelatorios() {
        super(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        saida.setEditable(false);
        saida.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT));
        barra.add(botao("Clientes", Cliente::gerarRelatorioClientes));
        barra.add(botao("Funcionários", Funcionario::gerarRelatorioFuncionarios));
        barra.add(botao("Serviços", Servico::gerarRelatorioServicos));
        JButton agendamentos = new JButton("Agendamentos por período");
        agendamentos.addActionListener(evento -> gerarAgendamentos());
        barra.add(agendamentos);
        add(barra, BorderLayout.NORTH);
        add(new JScrollPane(saida), BorderLayout.CENTER);
    }

    private JButton botao(String texto, Supplier<String> relatorio) {
        JButton botao = new JButton(texto);
        botao.addActionListener(evento -> exibir(relatorio));
        return botao;
    }

    private void exibir(Supplier<String> relatorio) {
        try {
            saida.setText(relatorio.get());
            saida.setCaretPosition(0);
        } catch (RuntimeException e) {
            Dialogos.erro(this, Dialogos.mensagem(e));
        }
    }

    private void gerarAgendamentos() {
        LocalDate hoje = LocalDate.now();
        JTextField inicio = Dialogos.campo(hoje.withDayOfMonth(1).format(Formatos.DATA));
        JTextField fim = Dialogos.campo(hoje.withDayOfMonth(hoje.lengthOfMonth()).format(Formatos.DATA));
        boolean confirmado = Dialogos.formulario(this, "Período do relatório",
                new String[] {"Data inicial (dd/mm/aaaa)", "Data final (dd/mm/aaaa)"},
                new JComponent[] {inicio, fim});
        if (confirmado) {
            exibir(() -> Agendamento.gerarRelatorioAgendamentos(Formatos.lerData(inicio.getText()),
                    Formatos.lerData(fim.getText())));
        }
    }
}

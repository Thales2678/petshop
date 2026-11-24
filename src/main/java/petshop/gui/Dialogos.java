package petshop.gui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;


public final class Dialogos {
   

    public static void erro(Component componentePai, String mensagem) {
        JOptionPane.showMessageDialog(componentePai, mensagem, "Atenção", JOptionPane.ERROR_MESSAGE);
    }

    public static void aviso(Component componentePai, String mensagem) {
        JOptionPane.showMessageDialog(componentePai, mensagem, "Aviso", JOptionPane.WARNING_MESSAGE);
    }

    public static void informacao(Component componentePai, String mensagem) {
        JOptionPane.showMessageDialog(componentePai, mensagem, "Informação", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean confirmar(Component componentePai, String mensagem) {
        return JOptionPane.showConfirmDialog(componentePai, mensagem, "Confirmação" , JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    public static JTextField campo(String valor) {
        return new JTextField(valor == null ? "" : valor, 24);
    }

    public static boolean formulario(Component pai, String titulo, String[] rotulos, JComponent[] campos) {
        JPanel painel = new JPanel(new GridBagLayout());
        GridBagConstraints restricoes = new GridBagConstraints();
        restricoes.insets = new Insets(4, 4, 4, 4);
        restricoes.anchor = GridBagConstraints.WEST;
        restricoes.fill = GridBagConstraints.HORIZONTAL;
        for (int i = 0; i < rotulos.length; i++) {
            restricoes.gridx = 0;
            restricoes.gridy = i;
            restricoes.weightx = 0;
            painel.add(new JLabel(rotulos[i]), restricoes);
            restricoes.gridx = 1;
            restricoes.weightx = 1;
            painel.add(campos[i], restricoes);
        }
        return JOptionPane.showConfirmDialog(pai, painel, titulo, JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION;
    }

     public static String mensagem(RuntimeException e) {
        return e.getMessage() == null || e.getMessage().isBlank() ? e.toString() : e.getMessage();
    }

    

}

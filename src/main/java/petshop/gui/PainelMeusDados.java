package petshop.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import petshop.Cliente;

public class PainelMeusDados extends JPanel{
    PainelMeusDados(Cliente cliente) {
        super(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        Cliente atual = Cliente.consultarCliente(cliente.getId());
        Cliente dados = atual == null ? cliente : atual;
        String[] rotulos = {"Nome", "E-mail", "Telefone", "Endereço", "Situação"};
        String[] valores = {dados.getNome(), dados.getEmail(), dados.getTelefone(), dados.getEndereco(),
                            Formatos.situacao(dados.isAtivo())};
        GridBagConstraints restricoes = new GridBagConstraints();
        restricoes.insets = new Insets(6, 6, 6, 6);
        restricoes.anchor = GridBagConstraints.NORTHWEST;
        for (int i = 0; i < rotulos.length; i++) {
            restricoes.gridy = i;
            restricoes.gridx = 0;
            restricoes.weightx = 0;
            JLabel rotulo = new JLabel(rotulos[i] + ":");
            rotulo.setFont(rotulo.getFont().deriveFont(java.awt.Font.BOLD));
            add(rotulo, restricoes);
            restricoes.gridx = 1;
            restricoes.weightx = 1;
            add(new JLabel(valores[i]), restricoes);
        }
        restricoes.gridy = rotulos.length;
        restricoes.weighty = 1;
        add(new JPanel(), restricoes);
    }
}

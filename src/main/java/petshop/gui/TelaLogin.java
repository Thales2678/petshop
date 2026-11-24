package petshop.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.BorderFactory;
import petshop.Main;
import petshop.Usuario;
    
public class TelaLogin extends JFrame {
    private final JTextField campoEmail = new JTextField(22);
    private final JPasswordField campoSenha = new JPasswordField(22);

    public TelaLogin() {
        super("Pet Shop - Entrar");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evento) {
                Main.encerrar();
            }
        });
      
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        GridBagConstraints restricoes = new GridBagConstraints();
        restricoes.insets = new Insets(6, 6, 6, 6);
        restricoes.anchor = GridBagConstraints.WEST;
        restricoes.fill = GridBagConstraints.HORIZONTAL; 


        JLabel titulo = new JLabel("Sistema de Atendimento para Pet Shop");
        titulo.setFont(titulo.getFont().deriveFont(java.awt.Font.BOLD, 16f));
        restricoes.gridx = 0;
        restricoes.gridy = 0;
        restricoes.gridwidth = 2;
        painel.add(titulo, restricoes);

        restricoes.gridwidth = 1;
        restricoes.gridy = 1;
        painel.add(new JLabel("E-mail"), restricoes);
        restricoes.gridx = 1;
        painel.add(campoEmail, restricoes);

        restricoes.gridx = 0;
        restricoes.gridy = 2;
        painel.add(new JLabel("Senha"), restricoes);
        restricoes.gridx = 1;
        painel.add(campoSenha, restricoes);

        JButton entrar = new JButton("Entrar");
        entrar.addActionListener(evento -> entrar());
        restricoes.gridx = 1;
        restricoes.gridy = 3;
        restricoes.fill = GridBagConstraints.NONE;
        restricoes.anchor = GridBagConstraints.EAST;
        painel.add(entrar, restricoes);

        getRootPane().setDefaultButton(entrar);
        setContentPane(painel);
        pack();
        setResizable(false);
        setLocationRelativeTo(null);

    }

    private void entrar() {
        String email = campoEmail.getText().trim();
        String senha = new String(campoSenha.getPassword());
        try {
            Usuario encontrado = null;
            for (Usuario usuario : Usuario.listarUsuarios()) {
                if (usuario.getEmail().equalsIgnoreCase(email)) {
                    encontrado = usuario;
                    break;
                }
            }
            if (encontrado == null || !encontrado.login(email, senha)) {
                Dialogos.erro(this, "E-mail ou senha incorretos, ou usuário inativo.");
                campoSenha.setText("");
                return;
            }
            new TelaPrincipal(encontrado).setVisible(true);
            dispose();
        } catch (RuntimeException e) {
            Dialogos.erro(this, Dialogos.mensagem(e));
        }
    }
}
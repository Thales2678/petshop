package petshop;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import petshop.gui.TelaLogin;

public class Main {
    public static void main(String[] args) {
        try {
            Banco.carregar();
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(null,
                    e.getMessage() + "\n\nVerifique se o PostgreSQL está em execução e se as variáveis "
                            + "PETSHOP_DB_URL, PETSHOP_DB_USER e PETSHOP_DB_PASSWORD estão corretas.",
                    "Erro ao conectar ao banco de dados", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        if (Funcionario.listarFuncionarios().isEmpty()) {
            Funcionario.cadastrarFuncionario("Administrador", "admin@petshop.com", "admin123",
                                             "0001", Cargo.ADMINISTRADOR);
        }
        SwingUtilities.invokeLater(() -> new TelaLogin().setVisible(true));
    }

    public static void encerrar() {
        try {
            Banco.salvar();
        } catch (RuntimeException e) {
            int resposta = JOptionPane.showConfirmDialog(null,
                    e.getMessage() + "\n\nSair mesmo assim? Os dados desta sessão serão perdidos.",
                    "Erro ao salvar", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
            if (resposta != JOptionPane.YES_OPTION) {
                return;
            }
        }
        System.exit(0);
    }
}

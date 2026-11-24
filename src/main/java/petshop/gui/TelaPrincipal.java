package petshop.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import petshop.Cargo;
import petshop.Cliente;
import petshop.Funcionario;
import petshop.Main;
import petshop.Usuario;

public class TelaPrincipal extends JFrame {
    private final Usuario usuario;

    public TelaPrincipal(Usuario usuario) {
        super("Pet Shop - Sistema de Atendimento");
        this.usuario = usuario;
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evento) {
                Main.encerrar();
            }
        });
        
        setMinimumSize(new Dimension(980, 560));
        setLayout(new BorderLayout());
        add(criarTopo(), BorderLayout.NORTH);
        add(criarAbas(), BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel criarTopo() {
        JPanel topo = new JPanel(new BorderLayout());
        topo.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        topo.add(new JLabel("Usuário: " + usuario.getNome() + " - " + descreverPerfil()), BorderLayout.WEST);
        JButton sair = new JButton("Sair");
        sair.addActionListener(evento -> logout());
        topo.add(sair, BorderLayout.EAST);
        return topo;
    }

     private String descreverPerfil() {
        if (usuario instanceof Funcionario) {
            return ((Funcionario) usuario).getCargo().name();
        }
        return "CLIENTE";
    }

    private JTabbedPane criarAbas() {
        JTabbedPane abas = new JTabbedPane();
        if (usuario instanceof Cliente) {
            abas.addTab("Meus dados", new PainelMeusDados((Cliente) usuario));
            abas.addTab("Meus animais", new PainelAnimais(usuario, false));
            abas.addTab("Serviços", new PainelServicos(false, true));
            abas.addTab("Meus agendamentos", new PainelAgendamentos(usuario));
            return abas;
        }
        Funcionario funcionario = (Funcionario) usuario;
        if (funcionario.getCargo() == Cargo.ADMINISTRADOR) {
            abas.addTab("Clientes", new PainelClientes());
            abas.addTab("Animais", new PainelAnimais(usuario, true));
            abas.addTab("Funcionários", new PainelFuncionarios());
            abas.addTab("Serviços", new PainelServicos(true, false));
            abas.addTab("Agendamentos", new PainelAgendamentos(usuario));
            abas.addTab("Relatórios", new PainelRelatorios());
        } else if (funcionario.getCargo() == Cargo.ATENDENTE) {
            abas.addTab("Clientes", new PainelClientes());
            abas.addTab("Animais", new PainelAnimais(usuario, true));
            abas.addTab("Serviços", new PainelServicos(false, false));
            abas.addTab("Agendamentos", new PainelAgendamentos(usuario));
        } else {
            abas.addTab("Meus atendimentos", new PainelAgendamentos(usuario));
        }
        return abas;
    }

    private void logout() {
        dispose();
        new TelaLogin().setVisible(true);
    }
     
}

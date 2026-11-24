package petshop;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;

public class Banco {
    private static final String URL_PADRAO = "jdbc:postgresql://localhost:5432/petshop";
    private static final String USUARIO_PADRAO = "postgres";
    private static final String SENHA_PADRAO = "postgres";


    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL_PADRAO, USUARIO_PADRAO, SENHA_PADRAO);
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

  private static void criarTabelas(Connection conexao) throws IOException, SQLException {
        try (InputStream entrada = Banco.class.getResourceAsStream("/schema.sql")) {
            String script = new String(entrada.readAllBytes(), StandardCharsets.UTF_8);
            try (Statement comando = conexao.createStatement()) {
                comando.execute(script);
            }
        }
    }  

   private static void carregarFuncionarios(Connection conexao) throws SQLException {
        String sql = "SELECT u.id, u.nome, u.email, u.senha, u.ativo, f.matricula, f.cargo "
                + "FROM usuario u JOIN funcionario f ON f.id = u.id ORDER BY u.id";
        try (Statement comando = conexao.createStatement(); ResultSet linha = comando.executeQuery(sql)) {
            while (linha.next()) {
                Funcionario funcionario = new Funcionario(linha.getInt("id"), linha.getString("nome"),
                        linha.getString("email"), linha.getString("senha"), linha.getBoolean("ativo"),
                        linha.getString("matricula"), Cargo.valueOf(linha.getString("cargo")));
                funcionario.cadastrarUsuario();
            }
        }
    }


 private static void carregarClientes(Connection conexao) throws SQLException {
        String sql = "SELECT u.id, u.nome, u.email, u.senha, u.ativo, c.telefone, c.endereco "
                + "FROM usuario u JOIN cliente c ON c.id = u.id ORDER BY u.id";
        try (Statement comando = conexao.createStatement(); ResultSet linha = comando.executeQuery(sql)) {
            while (linha.next()) {
                Cliente cliente = new Cliente(linha.getInt("id"), linha.getString("nome"), linha.getString("email"),
                        linha.getString("senha"), linha.getBoolean("ativo"), linha.getString("telefone"),
                        linha.getString("endereco"));
                cliente.cadastrarUsuario();
            }
        }
    }

 private static void carregarAnimais(Connection conexao) throws SQLException {
        String sql = "SELECT id, nome, especie, raca, idade, ativo, cliente_id FROM animal ORDER BY id";
        try (Statement comando = conexao.createStatement(); ResultSet linha = comando.executeQuery(sql)) {
            while (linha.next()) {
                Cliente dono = Cliente.consultarCliente(linha.getInt("cliente_id"));
                Animal.animais.add(new Animal(linha.getInt("id"), linha.getString("nome"),
                        linha.getString("especie"), linha.getString("raca"), linha.getInt("idade"),
                        linha.getBoolean("ativo"), dono));
            }
        }
    }



 private static void carregarServicos(Connection conexao) throws SQLException {
        String sql = "SELECT id, nome, descricao, preco, duracao_minutos, ativo FROM servico ORDER BY id";
        try (Statement comando = conexao.createStatement(); ResultSet linha = comando.executeQuery(sql)) {
            while (linha.next()) {
                Servico.servicos.add(new Servico(linha.getInt("id"), linha.getString("nome"),
                        linha.getString("descricao"), linha.getDouble("preco"), linha.getInt("duracao_minutos"),
                        linha.getBoolean("ativo")));
            }
        }
    }

 private static void carregarAgendamentos(Connection conexao) throws SQLException {
        String sql = "SELECT id, data, horario, status, observacoes, animal_id, servico_id, funcionario_id "
                + "FROM agendamento ORDER BY id";
        try (Statement comando = conexao.createStatement(); ResultSet linha = comando.executeQuery(sql)) {
            while (linha.next()) {
                Animal animal = Animal.consultarAnimal(linha.getInt("animal_id"));
                Servico servico = Servico.consultarServico(linha.getInt("servico_id"));
                Funcionario profissional = (Funcionario) Usuario.consultarUsuario(linha.getInt("funcionario_id"));
                Agendamento.agendamentos.add(new Agendamento(linha.getInt("id"),
                        linha.getDate("data").toLocalDate(), linha.getTime("horario").toLocalTime(),
                        StatusAgendamento.valueOf(linha.getString("status")), linha.getString("observacoes"),
                        animal, servico, profissional));
            }
        }
    }

public static void carregar() {
        try (Connection conexao = getConnection()) {
            criarTabelas(conexao);
            carregarClientes(conexao);
            carregarFuncionarios(conexao);
            carregarServicos(conexao);
            carregarAnimais(conexao);
            carregarAgendamentos(conexao);
        } catch (IOException | SQLException e) {
            throw new IllegalStateException("Não foi possível carregar os dados do banco: " + e.getMessage(), e);
        }
    }


 private static void apagarTudo(Connection conexao) throws SQLException {
        try (Statement comando = conexao.createStatement()) {
            comando.executeUpdate("DELETE FROM agendamento");
            comando.executeUpdate("DELETE FROM animal");
            comando.executeUpdate("DELETE FROM servico");
            comando.executeUpdate("DELETE FROM funcionario");
            comando.executeUpdate("DELETE FROM cliente");
            comando.executeUpdate("DELETE FROM usuario");
        }
    }


     private static void salvarUsuarios(Connection conexao) throws SQLException {
        String sqlUsuario = "INSERT INTO usuario (id, nome, email, senha, ativo) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement comando = conexao.prepareStatement(sqlUsuario)) {
            for (Usuario usuario : Usuario.listarUsuarios()) {
                comando.setInt(1, usuario.getId());
                comando.setString(2, usuario.getNome());
                comando.setString(3, usuario.getEmail());
                comando.setString(4, usuario.getSenha());
                comando.setBoolean(5, usuario.isAtivo());
                comando.executeUpdate();
            }
        }

      String sqlCliente = "INSERT INTO cliente (id, telefone, endereco) VALUES (?, ?, ?)";
        try (PreparedStatement comando = conexao.prepareStatement(sqlCliente)) {
            for (Cliente cliente : Cliente.listarCliente()) {
                comando.setInt(1, cliente.getId());
                comando.setString(2, cliente.getTelefone());
                comando.setString(3, cliente.getEndereco());
                comando.executeUpdate();
            }
        }
        String sqlFuncionario = "INSERT INTO funcionario (id, matricula, cargo) VALUES (?, ?, ?)";
        try (PreparedStatement comando = conexao.prepareStatement(sqlFuncionario)) {
            for (Funcionario funcionario : Funcionario.listarFuncionarios()) {
                comando.setInt(1, funcionario.getId());
                comando.setString(2, funcionario.getMatricula());
                comando.setString(3, funcionario.getCargo().name());
                comando.executeUpdate();
            }
        }
    }   

    private static void salvarServicos(Connection conexao) throws SQLException {
        String sql = "INSERT INTO servico (id, nome, descricao, preco, duracao_minutos, ativo) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement comando = conexao.prepareStatement(sql)) {
            for (Servico servico : Servico.listarServicos()) {
                comando.setInt(1, servico.getId());
                comando.setString(2, servico.getNome());
                comando.setString(3, servico.getDescricao());
                comando.setDouble(4, servico.getPreco());
                comando.setInt(5, servico.getDuracaoMinutos());
                comando.setBoolean(6, servico.isAtivo());
                comando.executeUpdate();
            }
        }
    }

    private static void salvarAnimais(Connection conexao) throws SQLException {
        String sql = "INSERT INTO animal (id, nome, especie, raca, idade, ativo, cliente_id) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement comando = conexao.prepareStatement(sql)) {
            for (Animal animal : Animal.listarAnimal()) {
                comando.setInt(1, animal.getId());
                comando.setString(2, animal.getNome());
                comando.setString(3, animal.getEspecie());
                comando.setString(4, animal.getRaca());
                comando.setInt(5, animal.getIdade());
                comando.setBoolean(6, animal.isAtivo());
                comando.setInt(7, animal.getCliente().getId());
                comando.executeUpdate();
            }
        }

    }

    private static void salvarAgendamentos(Connection conexao) throws SQLException {
        String sql = "INSERT INTO agendamento (id, data, horario, status, observacoes, animal_id, servico_id, funcionario_id) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement comando = conexao.prepareStatement(sql)) {
            for (Agendamento agendamento : Agendamento.listarAgendamentos()) {
                comando.setInt(1, agendamento.getId());
                comando.setDate(2, Date.valueOf(agendamento.getData()));
                comando.setTime(3, Time.valueOf(agendamento.getHorario()));
                comando.setString(4, agendamento.getStatus().name());
                comando.setString(5, agendamento.getObservacoes());
                comando.setInt(6, agendamento.getAnimal().getId());
                comando.setInt(7, agendamento.getServico().getId());
                comando.setInt(8, agendamento.getProfissional().getId());
                comando.executeUpdate();
            }
        }
    }

     public static void salvar() {
        try (Connection conexao = getConnection()) {
            conexao.setAutoCommit(false);
            apagarTudo(conexao);
            salvarUsuarios(conexao);
            salvarServicos(conexao);
            salvarAnimais(conexao);
            salvarAgendamentos(conexao);
            conexao.commit();
        } catch (SQLException e) {
            throw new IllegalStateException("Não foi possível salvar os dados no banco: " + e.getMessage(), e);
        }
    }

}

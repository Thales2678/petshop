package petshop;

import java.util.List;
import java.util.ArrayList;

public class Funcionario extends Usuario{
    private String matricula;
    private Cargo cargo;

    Funcionario(String nome, String email, String senha, String matricula, Cargo cargo) {
        super(nome, email, senha);
        this.matricula = Validacao.texto(matricula, "Matrícula");
        if (cargo == null) {
            throw new IllegalArgumentException("Cargo é obrigatório.");
        }
        this.cargo = cargo;
    }

    Funcionario(int id, String nome, String email, String senhaCriptografada, boolean ativo, String matricula,
                Cargo cargo) {
        super(id, nome, email, senhaCriptografada, ativo);
        this.matricula = matricula;
        this.cargo = cargo;
    }
    
    public String getMatricula() {
        return matricula;
    }

    public Cargo getCargo() {
        return cargo;
    }
    
    public static Funcionario cadastrarFuncionario(String nome, String email, String senha, String matricula, Cargo cargo){

        Funcionario funcionario = new Funcionario(nome, email, senha, matricula, cargo);
            for (Funcionario existente : listarFuncionarios()) {
                if (existente.matricula.equalsIgnoreCase(funcionario.matricula)) {
                    throw new IllegalArgumentException("Já existe um funcionário com esta matrícula.");
                }
            }
            funcionario.cadastrarUsuario();
            return funcionario;
    }   

    public Funcionario consultarFuncionario(int id){
        Usuario usuario = consultarUsuario(id);
            if (usuario instanceof Funcionario) {
                return (Funcionario) usuario;
            }
            return null;


    }

    public void atualizarFuncionario(Cargo cargo){
        if (cargo == null) {
            throw new IllegalArgumentException("Cargo é obrigatório.");
        }
        this.cargo = cargo;

    }

    public void desativarFuncionario(){
        if (cargo == Cargo.PRESTADOR_SERVICO) {
            Agendamento.cancelarFuturosDoProfissional(this);
        }
        desativarUsuario();
    }


    public static List<Funcionario> listarFuncionarios(){
        
        List<Funcionario> funcionarios = new ArrayList<>();
            for (Usuario usuario : listarUsuarios()) {
                if (usuario instanceof Funcionario) {
                    funcionarios.add((Funcionario) usuario);
                }
            }
            return funcionarios;


    }

    public static List<Funcionario> buscarFuncionario(Cargo cargo){
        if (cargo == null) {
            return listarFuncionarios();
        }
        List<Funcionario> encontrados = new ArrayList<>();
        for (Funcionario funcionario : listarFuncionarios()) {
            if (funcionario.cargo == cargo) {
                encontrados.add(funcionario);
            }
        }
        return encontrados;
    }

    public static String gerarRelatorioFuncionarios(){
        StringBuilder relatorio = new StringBuilder();
        relatorio.append(Validacao.formatar("RELATÓRIO DE FUNCIONÁRIOS%n%n"));
        relatorio.append("=====================\n");
        relatorio.append(Validacao.formatar("%-5s %-12s %-26s %-18s %s%n",
                                         "ID", "Matrícula", "Nome", "Cargo", "Situação"));
        List<Funcionario> funcionarios = listarFuncionarios();
        for (Funcionario funcionario : funcionarios) {
            relatorio.append(Validacao.formatar("%-5d %-12.12s %-26.26s %-18s %s%n",
                    funcionario.getId(), funcionario.matricula, funcionario.getNome(), funcionario.cargo,
                    funcionario.isAtivo() ? "Ativo" : "Inativo"));
        }
        relatorio.append(Validacao.formatar("%nTotal de funcionários: %d%n", funcionarios.size()));
        relatorio.append("---------------------\n");
        return relatorio.toString();

    }

    @Override
    public String toString() {
        return getNome() + " (" + matricula + ")";
    }


}

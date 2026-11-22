package petshop;

import java.util.ArrayList;
import java.util.List;

public class Cliente extends Usuario {
    private String telefone;
    private String endereco;

    public Cliente(String nome, String email, String senha, String telefone, String endereco) {
            super(nome, email, senha);
            this.telefone = Validacao.texto(telefone, "Telefone");
            this.endereco = Validacao.texto(endereco, "Endereço");
    }

    public Cliente(int id, String nome, String email, String senhaCriptografada, boolean ativo, String telefone, String endereco) {
        super(id, nome, email, senhaCriptografada, ativo);
            this.telefone = telefone;
            this.endereco = endereco;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getEndereco() {
        return endereco;
    }

    public static Cliente cadastrarCliente(String nome, String email, String senha, String telefone, String endereco){
        Cliente cliente = new Cliente(nome, email, senha, telefone, endereco);
            cliente.cadastrarUsuario();
            return cliente;
    }

    public static Cliente consultarCliente(int id){
        Usuario usuario = consultarUsuario(id);
            if (usuario instanceof Cliente) {
                return (Cliente) usuario;
            }
            return null;
    }

    public void atualizarCliente(String telefone, String endereco){
            this.telefone = Validacao.texto(telefone, "Telefone");
            this.endereco = Validacao.texto(endereco, "Endereço");
     }

    public void desativarCliente(){
            desativarUsuario();
    }

    public static List<Cliente> listarCliente(){
        List<Cliente> clientes = new ArrayList<>();
            for (Usuario usuario : listarUsuarios()) {
                if (usuario instanceof Cliente) {
                    clientes.add((Cliente) usuario);
                }
            }
            return clientes;
    }

    public static List<Cliente> buscarCliente(String nome){
        String termo = Validacao.opcional(nome).toLowerCase();
        List<Cliente> encontrados = new ArrayList<>();
            for (Cliente cliente : listarCliente()) {
                if (cliente.getNome().toLowerCase().contains(termo)) {
                    encontrados.add(cliente);
                }
            }
            return encontrados;
    }

    public static String gerarRelatorioClientes(){
        StringBuilder relatorio = new StringBuilder();
            relatorio.append(Validacao.formatar("RELATÓRIO DE CLIENTES%n%n"));
            relatorio.append("=====================\n");
            relatorio.append(Validacao.formatar("%-5s %-24s %-28s %-15s %-30s %-9s %s%n",
                        "ID", "Nome", "E-mail", "Telefone", "Endereço", "Situação", "Animais"));
            List<Cliente> clientes = listarCliente();
            for (Cliente cliente : clientes) {
                int quantidadeAnimais = 0;
                for (Animal animal : Animal.listarAnimal()) {
                    if (animal.getCliente().getId() == cliente.getId()) {
                        quantidadeAnimais++;
                    }
                }
                relatorio.append(Validacao.formatar("%-5d %-24.24s %-28.28s %-15.15s %-30.30s %-9s %d%n",
                        cliente.getId(), cliente.getNome(), cliente.getEmail(), cliente.getTelefone(), cliente.getEndereco(),
                        cliente.isAtivo() ? "Sim" : "Não", quantidadeAnimais));
            }
            relatorio.append(Validacao.formatar("%nTotal de clientes: %d%n", clientes.size()));
            return relatorio.toString();
    }


}

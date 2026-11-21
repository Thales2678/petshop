package petshop;

import java.util.ArrayList;
import java.util.List;

public class Servico {

   static final List<Servico> servicos = new ArrayList<>();
    private static int proximoId = 1;

    private int id;
    private String nome;
    private String descricao;
    private double preco;
    private int duracaoMinutos;
    private boolean ativo;


 Servico(int id, String nome, String descricao, double preco, int duracaoMinutos, boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.duracaoMinutos = duracaoMinutos;
        this.ativo = ativo;
        if (id >= proximoId) {
            proximoId = id + 1;
        }
    }
   public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public double getPreco() {
        return preco;
    }

    public int getDuracaoMinutos() {
        return duracaoMinutos;
    }

    public boolean isAtivo() {
        return ativo;
    }

    
    
    public static Servico cadastrarServico(String nome, String descricao, double preco, int duracaoMinutos){
        Servico servico = new Servico(proximoId, Validacao.texto(nome, "Nome"), Validacao.opcional(descricao),
        Validacao.naoNegativo(preco, "Preço"), Validacao.positivo(duracaoMinutos,
                                     "Duração"), true);
    servicos.add(servico);
    
    return servico;
}

public static Servico consultarServico(int id){
   
    for (Servico servico : servicos) {
        if (servico.id == id) {
            return servico;
        }
    }
   return null;
}

public void atualizarServico(String nome, String descricao, double preco, int duracaoMinutos){
    this.nome = Validacao.texto(nome, "Nome");
    this.descricao = Validacao.opcional(descricao);
    this.preco = Validacao.naoNegativo(preco, "Preço");
    this.duracaoMinutos = Validacao.positivo(duracaoMinutos, "Duração");
}

public void desativarServico(){
    this.ativo = false;
}

public static List<Servico> listarServicos(){

    return new ArrayList<>(servicos);

}

public static List<Servico> buscarServico(String nome){
    
    String termo = Validacao.opcional(nome).toLowerCase();
    List<Servico> encontrados = new ArrayList<>();
    for (Servico servico : servicos) {
        if (servico.nome.toLowerCase().contains(termo)) {
            encontrados.add(servico);
        }
    }
    return encontrados;

}

public static String gerarRelatorioServicos(){

    StringBuilder relatorio = new StringBuilder();
    relatorio.append("RELATORIO DE SERVIÇOS%n%n");
    relatorio.append("=====================\n");
    relatorio.append(Validacao.formatar("%-5s %-28s %12s %14s  %s%n",
                                      "ID", "Nome", "Preço (R$)", 
                                      "Duração (min)", "Situação"));
    for (Servico servico : servicos) {
        relatorio.append(Validacao.formatar("%-5d %-28.28s %12.2f %14d  %s%n",
                        servico.id, servico.nome, servico.preco, servico.duracaoMinutos,
                        servico.ativo ? "Sim" : "Não"));
    }
        relatorio.append(Validacao.formatar("%nTotal de serviços: %d%n", servicos.size())); 
        relatorio.append("---------------------\n");
    
    return relatorio.toString();
}

    @Override
    public String toString() {
        return nome + " (" + duracaoMinutos + " min)";
    }

}

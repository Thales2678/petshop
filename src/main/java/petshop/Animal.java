package petshop;

import java.util.ArrayList;
import java.util.List;

public class Animal {
    
    static final List<Animal> animais = new ArrayList<>();
    private static int proximoId = 1;


    private int id;
    private String nome;
    private String especie;
    private String raca;
    private int idade;
    private boolean ativo;
     private Cliente cliente;

    Animal(int id, String nome, String especie, String raca, int idade, boolean ativo, Cliente cliente) {
        this.id = id;
        this.nome = nome;
        this.especie = especie;
        this.raca = raca;
        this.idade = idade;
        this.ativo = ativo;
        this.cliente = cliente;
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

    public String getEspecie() {
        return especie;
    }

    public String getRaca() {
        return raca;
    }

    public int getIdade() {
        return idade;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public static Animal cadastrarAnimal(Cliente cliente, String nome, String especie, String raca, int idade) {
        if (cliente == null) {
            throw new IllegalArgumentException("O cliente responsável pelo animal é obrigatório.");
        }
        if (!cliente.isAtivo()) {
            throw new IllegalStateException("O cliente precisa estar ativo para cadastrar um animal.");
        }
        Animal animal = new Animal(proximoId, Validacao.texto(nome, "Nome"), Validacao.texto(especie, "Espécie"),
                Validacao.opcional(raca), Validacao.naoNegativo(idade, "Idade"), true, cliente);
        
                animais.add(animal);
        
            return animal;
    }
    
    public static Animal consultarAnimal(int id){
        for (Animal animal : animais) {
            if (animal.getId() == id) {
                return animal;
            }
        }
        return null;
     }

    public void atualizarAnimal(String nome, String especie, String raca, int idade){
        this.nome = Validacao.texto(nome, "Nome");
        this.especie = Validacao.texto(especie, "Espécie");
        this.raca = Validacao.opcional(raca);
        this.idade = Validacao.naoNegativo(idade, "Idade");
    }

    public void desativarAnimal(){
       Agendamento.cancelarFuturosDoAnimal(this);
        this.ativo = false;
    }

    public static List<Animal> listarAnimal(){
        return new ArrayList<>(animais);
    }

    public static List<Animal> buscarAnimal(String nome){
        String termo = Validacao.opcional(nome).toLowerCase();
        List<Animal> animaisEncontrados = new ArrayList<>();
        for (Animal animal : animais) {
            if (animal.getNome().toLowerCase().contains(termo)) {
                animaisEncontrados.add(animal);
            }
        }
        return animaisEncontrados;
    }

     @Override
    public String toString() {
        return nome + " (" + especie + ") #" + id;
    }

}

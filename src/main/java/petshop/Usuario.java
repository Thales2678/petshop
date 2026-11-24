package petshop;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;


public abstract class Usuario{
   
    private static final List<Usuario> usuarios = new ArrayList<>();
    private static int proximoId = 1;

    private int id;
    private String email;
    private String nome;
    private String senha;
    private boolean ativo;

    Usuario(String nome, String email, String senha) {
        this.id = proximoId++;
        this.nome = Validacao.texto(nome, "Nome");
        this.email = Validacao.email(email);
        this.senha = criptografar(Validacao.senha(senha));
        this.ativo = true;
    }

    Usuario(int id, String nome, String email, String senhaCriptografada, boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senhaCriptografada;
        this.ativo = ativo;
        if (id >= proximoId) {
            proximoId = id + 1;
        }
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getNome() {
        return nome;
    }

    public boolean isAtivo() {
        return ativo;
    }

    String getSenha() {
        return senha;
    }
   
    public void cadastrarUsuario(){
    if (emailEmUso(email, 0)) {
                throw new IllegalArgumentException("Já existe um usuário com este e-mail.");
            }
            usuarios.add(this);
    }

    public static Usuario consultarUsuario(int id){
        for (Usuario usuario : usuarios) {
                if (usuario.id == id) {
                    return usuario;
                }
            }
            return null;
    } 

    public void atualizarUsuario(String nome, String email, String senha){
        String novoNome = Validacao.texto(nome, "Nome");
        String novoEmail = Validacao.email(email);
        if (emailEmUso(novoEmail, id)) {
                throw new IllegalArgumentException("Já existe um usuário com este e-mail.");
            }
            if (senha != null && !senha.isEmpty()) {
                this.senha = criptografar(Validacao.senha(senha));
            }
            this.nome = novoNome;
            this.email = novoEmail;
    }

    public void desativarUsuario(){
        this.ativo = false;
    }

    public static List<Usuario> listarUsuarios(){
        return new ArrayList<>(usuarios);
    }

    public boolean login(String email, String senha){
        if (!ativo || email == null || senha == null) {
                return false;
            }
            return this.email.equalsIgnoreCase(email.trim()) && this.senha.equals(criptografar(senha));
    }

    private static boolean emailEmUso(String email, int id_usuario) {
            for (Usuario usuario : usuarios) {
                if (usuario.email.equalsIgnoreCase(email) && usuario.id != id_usuario) {
                    return true;
                }
            }
            return false;
        }

    private static String criptografar(String senha) {
            try {
                MessageDigest algoritmo = MessageDigest.getInstance("SHA-256");
                byte[] bytes = algoritmo.digest(senha.getBytes(StandardCharsets.UTF_8));
                StringBuilder texto = new StringBuilder();
                for (byte b : bytes) {
                    texto.append(String.format("%02x", b));
                }
                return texto.toString();
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException("Algoritmo de criptografia indisponível.", e);
            }
        }

}
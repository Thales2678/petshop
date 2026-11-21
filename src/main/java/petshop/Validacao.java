package petshop;

import java.util.Locale;

public class Validacao {
     static final Locale portuguesBrasil = Locale.forLanguageTag("pt-BR");

    static String formatar(String modelo, Object... argumentos) {
        return String.format(portuguesBrasil, modelo, argumentos);
    }

    static String texto(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " é obrigatório.");
        }
        return valor.trim();
    }
    
     static String opcional(String valor) {
        return valor == null ? "" : valor.trim();
    }

    static String email(String valor) {
        String email = texto(valor, "E-mail").toLowerCase(portuguesBrasil);
        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new IllegalArgumentException("E-mail inválido.");
        }
        return email;
    }

    static String senha(String valor) {
    if (valor == null || valor.length() < 6) {
        throw new IllegalArgumentException("A senha deve ter no mínimo 6 caracteres.");
    }
    return valor;
    }

    static int naoNegativo(int valor, String campo) {
        if (valor < 0) {
            throw new IllegalArgumentException(campo + " não pode ser negativo.");
        }
        return valor;
    }

    static double naoNegativo(double valor, String campo) {
        if (valor < 0) {
            throw new IllegalArgumentException(campo + " não pode ser negativo.");
        }
        return valor;
    }

     static int positivo(int valor, String campo) {
        if (valor <= 0) {
            throw new IllegalArgumentException(campo + " deve ser maior que zero.");
        }
        return valor;
    }

  

}


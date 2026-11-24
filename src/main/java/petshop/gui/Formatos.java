package petshop.gui;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

final class Formatos {
    static final Locale portuguesBrasil = Locale.forLanguageTag("pt-BR");
    static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
    static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm");

    static LocalDate lerData(String texto) {
        try {
            return LocalDate.parse(texto.trim(), DATA);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Data inválida. Use o formato dd/mm/aaaa.");
        }
    }

    static LocalTime lerHora(String texto) {
        try {
            return LocalTime.parse(texto.trim(), HORA);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Horário inválido. Use o formato hh:mm.");
        }
    }

    static int inteiro(String texto, String campo) {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(campo + " deve ser um número inteiro.");
        }
    }

    static double decimal(String texto, String campo) {
        try {
            return Double.parseDouble(texto.trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(campo + " deve ser um número.");
        }
    }

    static String situacao(boolean ativo) {
        return ativo ? "Ativo" : "Inativo";
    }

    static String moeda(double valor) {
        return String.format(portuguesBrasil, "R$ %.2f", valor);
    }
}

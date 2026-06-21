package br.com.limpafacil.model.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class GeradorCodigo {
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private GeradorCodigo() {
    }

    public static String gerar(String prefixo) {
        return prefixo + "-" + LocalDateTime.now().format(FORMATO);
    }
}

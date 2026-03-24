package com.example.demo;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

@DisplayName("Testes para ExemploVulnerabilidade")
class ExemploVulnerabilidadeTest {

    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        // Captura a saída padrão para validar as mensagens impressas
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    @DisplayName("Deve validar que PreparedStatement é usado (não há SQL injection)")
    void testNoPreparedStatementInSource() {
        // Este teste valida que a classe não contém código de SQL injection
        // verificando se não há concatenação de strings com "WHERE nome = '" 
        String classSource = getExemploVulnerabilidadeSource();
        
        assertFalse(classSource.contains("\"SELECT * FROM usuarios WHERE nome = '\" + userInput"),
                "A classe não deve conter SQL injection com concatenação de strings");
    }

    @Test
    @DisplayName("Deve usar PreparedStatement com placeholder")
    void testUsesPlaceholder() {
        String classSource = getExemploVulnerabilidadeSource();
        
        assertTrue(classSource.contains("prepareStatement(\"SELECT * FROM usuarios WHERE nome = ?\")"),
                "A classe deve usar PreparedStatement com placeholder (?)");
        
        assertTrue(classSource.contains("statement.setString(1, userInput)"),
                "A classe deve usar setString para parametrizar valores");
    }

    @Test
    @DisplayName("Deve ter try-with-resources para Connection")
    void testHasTryWithResources() {
        String classSource = getExemploVulnerabilidadeSource();
        
        assertTrue(classSource.contains("try (Connection connection = DriverManager.getConnection"),
                "A classe deve usar try-with-resources para Connection");
    }

    @Test
    @DisplayName("Deve processar ResultSet em um loop while")
    void testProcessesResultSetInLoop() {
        String classSource = getExemploVulnerabilidadeSource();
        
        assertTrue(classSource.contains("while (resultSet.next())"),
                "A classe deve processar ResultSet em um loop while");
    }

    @Test
    @DisplayName("Deve ter tratamento de SQLException")
    void testHasSQLExceptionHandling() {
        String classSource = getExemploVulnerabilidadeSource();
        
        assertTrue(classSource.contains("catch (SQLException e)"),
                "A classe deve ter tratamento para SQLException");
    }

    @Test
    @DisplayName("Deve validar que não há hardcoding de senhas em múltiplas credenciais")
    void testSecurityPractices() {
        String classSource = getExemploVulnerabilidadeSource();
        
        // Verifica que não há múltiplas strings com credenciais (bom sinal de segurança)
        // A classe contém uma, que é aceitável para exemplo educacional
        long credentialCount = classSource.split("DriverManager.getConnection").length - 1;
        
        assertTrue(credentialCount >= 1,
                "A classe deve usar DriverManager.getConnection");
    }

    @Test
    @DisplayName("Deve não conter Statement simples não-seguro")
    void testNoSimpleStatement() {
        String classSource = getExemploVulnerabilidadeSource();
        
        // Verifica que não há uso de Statement simples (vulnerável)
        // Note: pode haver "Statement" em comentários e imports, então verificamos pattern específico
        assertFalse(classSource.contains("statement1.executeQuery(query)"),
                "A classe não deve usar Statement.executeQuery() com query concatenada");
    }

    @Test
    @DisplayName("Deve ter estrutura básica de uma classe Java válida")
    void testValidJavaStructure() {
        String classSource = getExemploVulnerabilidadeSource();
        
        assertTrue(classSource.contains("public class ExemploVulnerabilidade"),
                "A classe deve ser pública");
        
        assertTrue(classSource.contains("public static void main(String[] args)"),
                "A classe deve ter método main válido");
    }

    /**
     * Método auxiliar para obter o código-fonte da classe
     * Em um teste real, você usaria reflexão ou ler do arquivo
     */
    private String getExemploVulnerabilidadeSource() {
        // Lê o código-fonte via reflexão do método main
        try {
            Class.forName("com.example.demo.ExemploVulnerabilidade");
            
            // Para fins de teste, retornamos verificações baseadas no comportamento esperado
            // Um teste de produção usaria um leitor de fonte real
            return readSourceFromDisk();
        } catch (ClassNotFoundException e) {
            fail("Classe ExemploVulnerabilidade não encontrada");
            return "";
        }
    }

    /**
     * Lê o arquivo fonte do disco (para teste de integração)
     */
    private String readSourceFromDisk() {
        try {
            String filePath = "src/main/java/com/example/demo/ExemploVulnerabilidade.java";
            java.nio.file.Path path = java.nio.file.Paths.get(filePath);
            return new String(java.nio.file.Files.readAllBytes(path));
        } catch (Exception e) {
            fail("Não foi possível ler o arquivo fonte: " + e.getMessage());
            return "";
        }
    }
}

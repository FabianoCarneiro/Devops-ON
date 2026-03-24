package com.example.demo;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

@DisplayName("Testes para ExemploVulnerabilidade")
class ExemploVulnerabilidadeTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Deve executar query com sucesso quando entrada é válida")
    void testMainWithValidInput() throws SQLException {
        // Arrange
        String[] args = { "João Silva" };
        
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true).thenReturn(false);
        when(mockResultSet.getString("nome")).thenReturn("João Silva");
        when(mockResultSet.getString("email")).thenReturn("joao@example.com");

        try (MockedStatic<DriverManager> mockedDriverManager = mockStatic(DriverManager.class)) {
            mockedDriverManager.when(() -> DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/banco", "usuario", "senha"))
                    .thenReturn(mockConnection);
            
            when(mockConnection.prepareStatement("SELECT * FROM usuarios WHERE nome = ?"))
                    .thenReturn(mockPreparedStatement);

            // Act & Assert
            assertDoesNotThrow(() -> ExemploVulnerabilidade.main(args));
            
            verify(mockPreparedStatement, times(1)).setString(1, "João Silva");
            verify(mockPreparedStatement, times(1)).executeQuery();
            verify(mockResultSet, atLeastOnce()).next();
            verify(mockResultSet, times(1)).getString("nome");
            verify(mockResultSet, times(1)).getString("email");
        }
    }

    @Test
    @DisplayName("Deve usar PreparedStatement para prevenir SQL Injection")
    void testPreparedStatementUsage() throws SQLException {
        // Arrange
        String[] args = { "'; DROP TABLE usuarios; --" };
        
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        try (MockedStatic<DriverManager> mockedDriverManager = mockStatic(DriverManager.class)) {
            mockedDriverManager.when(() -> DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/banco", "usuario", "senha"))
                    .thenReturn(mockConnection);
            
            when(mockConnection.prepareStatement("SELECT * FROM usuarios WHERE nome = ?"))
                    .thenReturn(mockPreparedStatement);

            // Act & Assert
            assertDoesNotThrow(() -> ExemploVulnerabilidade.main(args));
            
            // Verifica que o setString foi chamado com o valor exato (como dado, não como código SQL)
            verify(mockPreparedStatement, times(1)).setString(1, "'; DROP TABLE usuarios; --");
            verify(mockPreparedStatement, never()).addBatch();
            verify(mockPreparedStatement, never()).executeBatch();
        }
    }

    @Test
    @DisplayName("Deve tratar exceção SQLException graciosamente")
    void testSQLExceptionHandling() throws SQLException {
        // Arrange
        String[] args = { "teste" };

        try (MockedStatic<DriverManager> mockedDriverManager = mockStatic(DriverManager.class)) {
            mockedDriverManager.when(() -> DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/banco", "usuario", "senha"))
                    .thenThrow(new SQLException("Erro de conexão"));

            // Act & Assert
            assertDoesNotThrow(() -> ExemploVulnerabilidade.main(args));
        }
    }

    @Test
    @DisplayName("Deve processar múltiplas linhas retornadas")
    void testMultipleResultsProcessing() throws SQLException {
        // Arrange
        String[] args = { "Silva" };
        
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next())
            .thenReturn(true)  // primeira linha
            .thenReturn(true)  // segunda linha
            .thenReturn(false); // sem mais linhas
        
        when(mockResultSet.getString("nome"))
            .thenReturn("João Silva")
            .thenReturn("Maria Silva");
        
        when(mockResultSet.getString("email"))
            .thenReturn("joao@example.com")
            .thenReturn("maria@example.com");

        try (MockedStatic<DriverManager> mockedDriverManager = mockStatic(DriverManager.class)) {
            mockedDriverManager.when(() -> DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/banco", "usuario", "senha"))
                    .thenReturn(mockConnection);
            
            when(mockConnection.prepareStatement("SELECT * FROM usuarios WHERE nome = ?"))
                    .thenReturn(mockPreparedStatement);

            // Act & Assert
            assertDoesNotThrow(() -> ExemploVulnerabilidade.main(args));
            
            verify(mockResultSet, times(3)).next();
            verify(mockResultSet, times(2)).getString("nome");
            verify(mockResultSet, times(2)).getString("email");
        }
    }

    @Test
    @DisplayName("Deve fechar recursos automaticamente com try-with-resources")
    void testResourceClosing() throws SQLException {
        // Arrange
        String[] args = { "teste" };
        
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        try (MockedStatic<DriverManager> mockedDriverManager = mockStatic(DriverManager.class)) {
            mockedDriverManager.when(() -> DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/banco", "usuario", "senha"))
                    .thenReturn(mockConnection);
            
            when(mockConnection.prepareStatement("SELECT * FROM usuarios WHERE nome = ?"))
                    .thenReturn(mockPreparedStatement);

            // Act
            assertDoesNotThrow(() -> ExemploVulnerabilidade.main(args));

            // Assert - Verifica que o connection foi fechado (via try-with-resources)
            verify(mockConnection).close();
        }
    }
}

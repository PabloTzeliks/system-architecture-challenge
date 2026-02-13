package senai.centroweg;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class GodChaos {

    // HARDCODED CREDENTIALS (O primeiro pecado capital)
    private static final String URL = "jdbc:postgresql://localhost:5432/postgres"; // Mude o nome do banco se precisar
    private static final String USER = "postgres";
    private static final String PASS = "admin"; // Coloque sua senha aqui

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== SISTEMA CAOS (POSTGRES VERSION) ===");
            System.out.println("1. Criar Conta (INSERT)");
            System.out.println("2. Realizar Transação (SELECT + UPDATE + INSERT)");
            System.out.println("3. Ver Saldo (SELECT)");
            System.out.println("0. Sair");
            System.out.print("Escolha: ");
            int opcao = scanner.nextInt();

            if (opcao == 0) break;

            Connection conn = null; // Gestão manual de conexão

            try {
                // Abrindo conexão direta na Main (Acoplamento forte)
                conn = DriverManager.getConnection(URL, USER, PASS);

                // ---------------------------------------------------------
                // 1. CRIAR CONTA
                // ---------------------------------------------------------
                if (opcao == 1) {
                    System.out.print("ID da Conta: ");
                    int id = scanner.nextInt();
                    System.out.print("Saldo Inicial: ");
                    float saldo = scanner.nextFloat();

                    String sql = "INSERT INTO accounts (id, balance) VALUES (?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, id);
                    stmt.setFloat(2, saldo);
                    stmt.executeUpdate();
                    stmt.close(); // Se esquecer isso, vaza memória

                    System.out.println("Conta salva no banco!");
                }

                // ---------------------------------------------------------
                // 2. REALIZAR TRANSAÇÃO (Onde o filho chora e a mãe não vê)
                // ---------------------------------------------------------
                else if (opcao == 2) {
                    System.out.print("ID Remetente: ");
                    int idRem = scanner.nextInt();
                    System.out.print("ID Destinatário: ");
                    int idDest = scanner.nextInt();
                    System.out.print("Valor: ");
                    float valor = scanner.nextFloat();

                    // Passo 1: Buscar saldo do remetente (SELECT)
                    String sqlBusca = "SELECT balance FROM accounts WHERE id = ?";
                    PreparedStatement stmtBusca = conn.prepareStatement(sqlBusca);
                    stmtBusca.setInt(1, idRem);
                    ResultSet rs = stmtBusca.executeQuery();

                    float saldoAtual = 0;
                    if (rs.next()) {
                        saldoAtual = rs.getFloat("balance");
                    } else {
                        System.out.println("Conta remetente não existe!");
                        continue;
                    }
                    rs.close();
                    stmtBusca.close();

                    // Passo 2: Escolher tipo e calcular taxa (Strategy Hardcoded)
                    System.out.println("Tipo: [1] PIX, [2] TED, [3] CARTAO");
                    int tipo = scanner.nextInt();
                    float taxa = 0;
                    String tipoStr = "";

                    if (tipo == 1) {
                        tipoStr = "PIX";
                        taxa = 0;
                    } else if (tipo == 2) {
                        tipoStr = "TED";
                        taxa = 5.0f; // Taxa fixa
                    } else if (tipo == 3) {
                        tipoStr = "CARTAO";
                        taxa = valor * 0.03f; // 3%
                    }

                    float totalDebitar = valor + taxa;

                    // Passo 3: Validar e Atualizar (A Lógica "Transaction Script")
                    if (saldoAtual >= totalDebitar) {
                        // Perigo: Estamos fazendo updates separados sem Transaction Control explícito aqui
                        // Se cair a luz entre um update e outro, o dinheiro some!

                        // Debita do Remetente
                        String sqlUpdateRem = "UPDATE accounts SET balance = balance - ? WHERE id = ?";
                        PreparedStatement stmtRem = conn.prepareStatement(sqlUpdateRem);
                        stmtRem.setFloat(1, totalDebitar);
                        stmtRem.setInt(2, idRem);
                        stmtRem.executeUpdate();
                        stmtRem.close();

                        // Credita no Destinatário
                        String sqlUpdateDest = "UPDATE accounts SET balance = balance + ? WHERE id = ?";
                        PreparedStatement stmtDest = conn.prepareStatement(sqlUpdateDest);
                        stmtDest.setFloat(1, valor); // Destinatário recebe o valor limpo, sem taxa
                        stmtDest.setInt(2, idDest);
                        stmtDest.executeUpdate();
                        stmtDest.close();

                        // Salva Histórico
                        String sqlLog = "INSERT INTO transactions (sender_id, receiver_id, amount, type) VALUES (?, ?, ?, ?)";
                        PreparedStatement stmtLog = conn.prepareStatement(sqlLog);
                        stmtLog.setInt(1, idRem);
                        stmtLog.setInt(2, idDest);
                        stmtLog.setFloat(3, valor);
                        stmtLog.setString(4, tipoStr);
                        stmtLog.executeUpdate();
                        stmtLog.close();

                        System.out.println("Transação Finalizada! Taxa cobrada: " + taxa);
                    } else {
                        System.out.println("Saldo insuficiente.");
                    }
                }

                // ---------------------------------------------------------
                // 3. VISUALIZAR
                // ---------------------------------------------------------
                else if (opcao == 3) {
                    System.out.print("ID da Conta: ");
                    int id = scanner.nextInt();

                    String sql = "SELECT * FROM accounts WHERE id = ?";
                    PreparedStatement stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, id);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        System.out.println("--- DADOS ---");
                        System.out.println("ID: " + rs.getInt("id"));
                        System.out.println("Saldo: " + rs.getFloat("balance"));
                    } else {
                        System.out.println("Conta não encontrada.");
                    }
                    rs.close();
                    stmt.close();
                }

            } catch (SQLException e) {
                e.printStackTrace(); // Log de erro pobre
                System.out.println("ERRO DE SQL: " + e.getMessage());
            } finally {
                // Fechamento manual e perigoso
                try {
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
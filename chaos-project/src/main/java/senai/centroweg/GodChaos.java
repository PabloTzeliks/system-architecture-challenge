package senai.centroweg;

import senai.centroweg.model.Account;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GodChaos {

    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "root";
    private static final String PASS = "root";

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

            Connection conn = null;

            try {
                conn = DriverManager.getConnection(URL, USER, PASS);

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
                    stmt.close();

                    System.out.println("Conta salva no banco!");
                }

                else if (opcao == 2) {

                    List<Account> accountList = new ArrayList<>();

                    String sqlUpdateRem = "SELECT * FROM accounts";
                    PreparedStatement stmtRem = conn.prepareStatement(sqlUpdateRem);
                    ResultSet rs = stmtRem.executeQuery();

                    while (rs.next()) {
                       accountList.add(new Account(rs.getInt(1),rs.getFloat(2)));
                    }
                    for(Account a : accountList) {
                        System.out.println(a.toString());
                    }
                    stmtRem.close();

                    System.out.print("ID Remetente: ");
                    int idRem = scanner.nextInt();
                    System.out.print("ID Destinatário: ");
                    int idDest = scanner.nextInt();
                    System.out.print("Valor: ");
                    float valor = scanner.nextFloat();

                    String sqlBusca = "SELECT balance FROM accounts WHERE id = ?";
                    PreparedStatement stmtBusca = conn.prepareStatement(sqlBusca);
                    stmtBusca.setInt(1, idRem);
                    rs = stmtBusca.executeQuery();

                    float saldoAtual = 0;
                    if (rs.next()) {
                        saldoAtual = rs.getFloat("balance");
                    } else {
                        System.out.println("Conta remetente não existe!");
                        continue;
                    }
                    rs.close();
                    stmtBusca.close();

                    System.out.println("Tipo: [1] PIX, [2] TED, [3] CARTAO");
                    int tipo = scanner.nextInt();
                    float taxa = 0;
                    String tipoStr = "";

                    if (tipo == 1) {
                        tipoStr = "PIX";
                        taxa = 0;
                    } else if (tipo == 2) {
                        tipoStr = "TED";
                        taxa = 5.0f;
                    } else if (tipo == 3) {
                        tipoStr = "CARTAO";
                        taxa = valor * 0.03f;
                    }

                    float totalDebitar = valor + taxa;

                    if (saldoAtual >= totalDebitar) {

                        sqlUpdateRem = "UPDATE accounts SET balance = balance - ? WHERE id = ?";
                        stmtRem = conn.prepareStatement(sqlUpdateRem);
                        stmtRem.setFloat(1, totalDebitar);
                        stmtRem.setInt(2, idRem);
                        stmtRem.executeUpdate();
                        stmtRem.close();

                        String sqlUpdateDest = "UPDATE accounts SET balance = balance + ? WHERE id = ?";
                        PreparedStatement stmtDest = conn.prepareStatement(sqlUpdateDest);
                        stmtDest.setFloat(1, valor);
                        stmtDest.setInt(2, idDest);
                        stmtDest.executeUpdate();
                        stmtDest.close();

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
                e.printStackTrace();
                System.out.println("ERRO DE SQL: " + e.getMessage());
            } finally {
                try {
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
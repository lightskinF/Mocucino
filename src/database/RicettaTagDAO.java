package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import dto.ReportTagDTO;

public class RicettaTagDAO {

    //invocato a riga 53 di entity.Utente dal suo metodo crearicetta, ne salviamo i tag sul database che servono per i report
    public boolean aggiungiTagARicetta(int ricettaId, List<String> tag) {
        String insert = "INSERT INTO Ricette_has_Tags (Ricette_idRicetta, Tags_idTag) VALUES (?, ?)";
        boolean success = true;

        try (Connection conn = DBManager.openConnection();
             PreparedStatement insertStmt = conn.prepareStatement(insert)) {

            for (String tagNome : tag) {
                // Usa TagDAO per ottenere l'id del tag
                int tagId = new TagDAO().getIdTagByNome(tagNome);

                if (tagId == -1) {
                    System.err.println("Tag non trovato: " + tagNome);
                    success = false;
                    continue;
                }

                insertStmt.setInt(1, ricettaId);
                insertStmt.setInt(2, tagId);
                insertStmt.addBatch();
            }

            insertStmt.executeBatch();

        } catch (SQLException e) {
            e.printStackTrace();
            success = false;
        }

        return success;
    }

    //invocato a riga 103 di RICETTADAO, per recuperare i tag associati alla ricetta
    public List<String> getTagByRicetta(int idRicetta) {
        List<String> tags = new ArrayList<>();

        String query = "SELECT t.nome " +
                "FROM Tags t " +
                "JOIN Ricette_has_Tags rt ON t.idTag = rt.Tags_idTag " +
                "WHERE rt.Ricette_idRicetta = ?";

        try (Connection conn = DBManager.openConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idRicetta);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                tags.add(rs.getString("nome"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tags;
    }

    //Ottiene i tag più utilizzati nelle ricette
    //Entity -> DAO: Richiesta tag più usati
    //Chiamata da GestoreController.generaReportTag() riga 121
    public List<ReportTagDTO> getTagPiuUtilizzati() {
        List<ReportTagDTO> report = new ArrayList<>();

        String query = "SELECT t.nome, COUNT(rt.Ricette_idRicetta) as numero_ricette " +
                "FROM Tags t " +
                "JOIN Ricette_has_Tags rt ON t.idTag = rt.Tags_idTag " +
                "GROUP BY t.idTag, t.nome " +
                "ORDER BY numero_ricette DESC " +
                "LIMIT 5";

        try (Connection conn = DBManager.openConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                report.add(new ReportTagDTO(
                    rs.getString("nome"),
                    rs.getInt("numero_ricette")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return report;
    }
} 
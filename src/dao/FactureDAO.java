package dao;


import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;
import connexion.Connexion; 
public class FactureDAO {

    public List<Map<String, Object>> getFactureInfosParAppareils(List<Integer> idsAppareils) {
        List<Map<String, Object>> result = new ArrayList<>();

        if (idsAppareils==null || idsAppareils.isEmpty()) return result;

        String placeholders=idsAppareils.stream().map(id -> "?").collect(Collectors.joining(","));
        String sql="SELECT a.id AS id_appareil, a.nom AS nom_appareil, a.categorie AS categorie_appareil, "+
                     "LISTAGG(p.nom, ', ') WITHIN GROUP (ORDER BY p.nom) AS noms_pieces, "+
                     "SUM(p.prix * p.quantite) AS prix_total, "+
                     "SUM(p.quantite) AS quantite_totale "+
                     "FROM appareil a "+
                     "JOIN piece p ON p.appareil_id = a.id "+
                     "WHERE a.id IN ("+placeholders +") "+
                     "GROUP BY a.id, a.nom, a.categorie";

        try (Connection conn = Connexion.getCn();  
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < idsAppareils.size(); i++) {
                stmt.setInt(i + 1, idsAppareils.get(i));
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> ligne = new HashMap<>();
                ligne.put("nom_appareil",rs.getString("nom_appareil"));
                ligne.put("categorie_appareil",rs.getString("categorie_appareil"));
                ligne.put("noms_pieces",rs.getString("noms_pieces"));
                ligne.put("prix_total", rs.getDouble("prix_total"));
                ligne.put("quantite_totale",rs.getInt("quantite_totale"));
                result.add(ligne);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }
}

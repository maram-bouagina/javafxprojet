/*package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import connexion.Connexion;
import model.Appareil;
import model.Categorie;
import model.Client;
import model.OrdreDeReparation;
import model.Piece;

public class OrdredeReparationDAO {
	public boolean deleteOrdre(OrdreDeReparation o) {
		try (Connection cn = Connexion.getCn()) {
			String query = "DELETE FROM ordre_de_reparation  WHERE id=?";
			PreparedStatement ps = cn.prepareStatement(query);
			ps.setInt(1, (int) o.getId());

			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}

	}

	public List<OrdreDeReparation> findByNom(String nom) {
		List<OrdreDeReparation> ordres = new ArrayList<>();
		try (Connection cn = Connexion.getCn()) {
			String query = "SELECT o.id,o.nbHeureMo, o.client_id" + "FROM ordre_de_reparation o "
					+ "JOIN client c on o.client_id=c.id" + "where Lower(c.nom)Like?";

			PreparedStatement ps = cn.prepareStatement(query);
			ps.setString(1, "%" + nom.toLowerCase() + "%");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				OrdreDeReparation ordre = new OrdreDeReparation();
				ordre.setId(rs.getLong("id"));
				ordre.setNbHeureMo(rs.getInt("nbHeureMo"));
				Client client = new Client();
				client.setId(rs.getLong("client_id"));
				ordre.setClient(client);
				ordres.add(ordre);

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ordres;

	}

	public 	List<Appareil>  getAppareilsDetails(Long clientId) {
		List<Appareil> appareils = new ArrayList<>();
		String query = "select * from appareil where id_client=?";
		try (Connection cn = Connexion.getCn()) {
			PreparedStatement ps = cn.prepareStatement(query);
			ps.setLong(1,clientId);   ResultSet rs = ps.executeQuery();

			 while (rs.next()) {
		            Appareil appareil = new Appareil();
		            appareil.setId(rs.getLong("id"));
		            appareil.setDescription(rs.getString("description"));
		            appareil.setMarque(rs.getString("marque"));
		            appareil.setCategorie(this.getCategorieByid(rs.getLong("categorie_id")));
		            appareils.add(appareil);
		        }
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		    return appareils;

	}

	public Categorie getCategorieByid(Long catid)  {
        Categorie cat=new Categorie();
        String query = "select * from categorie where id=?";
    	try (Connection cn = Connexion.getCn()) {
			PreparedStatement ps = cn.prepareStatement(query);
			ps.setLong(1,catid); 
			ResultSet rs = ps.executeQuery();
		
	            cat.setId(rs.getLong("id"));
	            cat.setLibelle(rs.getString("libelle"));
	            cat.setTarif(rs.getString("tarif"));
    		}
    	
		 catch (SQLException e) {
		    e.printStackTrace();
		    
		}
    	return cat;
	}
	public List<Piece> findPiecesByOrdreId(long ordreId) {
	    List<Piece> pieces = new ArrayList<>();
	    String sql = "SELECT p.id, p.quantite, p.designation, p.prix " +
	                 "FROM piece p " +
	                 "JOIN ordre_piece op ON p.id = op.piece_id " +
	                 "WHERE op.ordre_id = ?";

	    try (Connection cn = Connexion.getCn();
	         PreparedStatement ps = cn.prepareStatement(sql)) {

	        ps.setLong(1, ordreId);
	        ResultSet rs = ps.executeQuery();

	        while (rs.next()) {
	            Piece piece = new Piece();
	            piece.setId(rs.getLong("id"));
	            piece.setQuantite(rs.getInt("quantite"));
	            piece.setDesignation(rs.getString("designation"));
	            piece.setPrix(rs.getDouble("prix"));
	            pieces.add(piece);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return pieces;
	}
    	
	public boolean ajouterOrdre(OrdreDeReparation ordre) {
	    try (Connection con = Connexion.getCn()) {
	        con.setAutoCommit(false); 

	       
	        Client client = ordre.getClient();
	        long clientId = -1;

	        String queryCheckClient = "SELECT id FROM Client WHERE nom = ?";
	        try (PreparedStatement checkClient = con.prepareStatement(queryCheckClient)) {
	            checkClient.setString(1, client.getNom());
	            try (ResultSet rsClient = checkClient.executeQuery()) {
	                if (rsClient.next()) {
	                    clientId = rsClient.getLong("id");
	                } else {
	                    String queryInsertClient = "INSERT INTO Client(nom, adresse, tel) VALUES (?, ?, ?)";
	                    try (PreparedStatement insertClient = con.prepareStatement(queryInsertClient, 
	                            PreparedStatement.RETURN_GENERATED_KEYS)) {
	                        insertClient.setString(1, client.getNom());
	                        insertClient.setString(2, client.getAdresse());
	                        insertClient.setString(3, client.getTel());
	                        insertClient.executeUpdate();
	                        try (ResultSet rsNewClient = insertClient.getGeneratedKeys()) {
	                            if (rsNewClient.next()) {
	                                clientId = rsNewClient.getLong(1);
	                            }
	                        }
	                    }
	                }
	            }
	        }

	       
	        Appareil app = ordre.getAppareils().get(0); 
	        long appId = -1;
	        String queryInsertApp = "INSERT INTO Appareil(client_id, categorie_id, description, marque) VALUES (?, ?, ?, ?)";
	        try (PreparedStatement insertApp = con.prepareStatement(queryInsertApp, 
	                PreparedStatement.RETURN_GENERATED_KEYS)) {
	            insertApp.setLong(1, clientId);
	            insertApp.setLong(2, app.getCategorie().getId());
	            insertApp.setString(3, app.getDescription());
	            insertApp.setString(4, app.getMarque());
	            insertApp.executeUpdate();
	            try (ResultSet rsApp = insertApp.getGeneratedKeys()) {
	                if (rsApp.next()) {
	                    appId = rsApp.getLong(1);
	                }
	            }
	        }

	     
	        long ordreId = -1;
	        String queryInsertOrdre = "INSERT INTO OrdreDeReparation(nbHeureMo, client_id) VALUES (?, ?)";
	        try (PreparedStatement insertOrdre = con.prepareStatement(queryInsertOrdre, 
	                PreparedStatement.RETURN_GENERATED_KEYS)) {
	            insertOrdre.setInt(1, ordre.getNbHeureMo());
	            insertOrdre.setLong(2, clientId);
	            insertOrdre.executeUpdate();
	            try (ResultSet rsOrdre = insertOrdre.getGeneratedKeys()) {
	                if (rsOrdre.next()) {
	                    ordreId = rsOrdre.getLong(1);
	                }
	            }
	        }

	        
	        String queryLinkAppareil = "INSERT INTO Ordre_Appareil(ordre_id, appareil_id) VALUES (?, ?)";
	        try (PreparedStatement linkAppareil = con.prepareStatement(queryLinkAppareil)) {
	            linkAppareil.setLong(1, ordreId);
	            linkAppareil.setLong(2, appId);
	            linkAppareil.executeUpdate();
	        }

	     
	        String queryLinkPiece = "INSERT INTO Ordre_Piece(ordre_id, piece_id) VALUES (?, ?)";
	        for (Piece p : ordre.getPieces()) {
	            try (PreparedStatement linkPiece = con.prepareStatement(queryLinkPiece)) {
	                linkPiece.setLong(1, ordreId);
	                linkPiece.setLong(2, p.getId());
	                linkPiece.executeUpdate();
	            }
	        }

	        con.commit(); 
	        return true;

	    } catch (SQLException ex) {
	        ex.printStackTrace();
	        try (Connection con = Connexion.getCn()) {
	            con.rollback();
	        } catch (SQLException rollbackEx) {
	            rollbackEx.printStackTrace();
	        }
	        return false;
	    }
	}
	
	public boolean updateOrdre(OrdreDeReparation ordre) {
	    try (Connection con = Connexion.getCn()) {
	        con.setAutoCommit(false); 

	        long ordreId = ordre.getId();

	        String queryUpdateOrdre = "UPDATE OrdreDeReparation SET nbHeureMo = ? WHERE id = ?";
	        try (PreparedStatement updateOrdre = con.prepareStatement(queryUpdateOrdre)) {
	            updateOrdre.setInt(1, ordre.getNbHeureMo());
	            updateOrdre.setLong(2, ordreId);
	            updateOrdre.executeUpdate();
	        }

	  
	        Appareil app = ordre.getAppareils().get(0); 
	        String queryUpdateApp = "UPDATE Appareil SET description = ?, marque = ?, categorie_id = ? WHERE id = ?";
	        try (PreparedStatement updateApp = con.prepareStatement(queryUpdateApp)) {
	            updateApp.setString(1, app.getDescription());
	            updateApp.setString(2, app.getMarque());
	            updateApp.setLong(3, app.getCategorie().getId());
	            updateApp.setLong(4, app.getId());
	            updateApp.executeUpdate();
	        }

	 
	        String queryDeletePieces = "DELETE FROM Ordre_Piece WHERE ordre_id = ?";
	        try (PreparedStatement deletePieces = con.prepareStatement(queryDeletePieces)) {
	            deletePieces.setLong(1, ordreId);
	            deletePieces.executeUpdate();
	        }

	  
	        String queryInsertPiece = "INSERT INTO Ordre_Piece(ordre_id, piece_id) VALUES (?, ?)";
	        for (Piece p : ordre.getPieces()) {
	            try (PreparedStatement insertPiece = con.prepareStatement(queryInsertPiece)) {
	                insertPiece.setLong(1, ordreId);
	                insertPiece.setLong(2, p.getId());
	                insertPiece.executeUpdate();
	            }
	        }

	        con.commit(); 
	        return true;

	    } catch (SQLException e) {
	        e.printStackTrace();
	        try (Connection con = Connexion.getCn()) {
	            con.rollback(); 
	        } catch (SQLException rollbackEx) {
	            rollbackEx.printStackTrace();
	        }
	        return false;
	    }
	}
	


}*/
/*package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import connexion.Connexion;
import model.Appareil;
import model.Categorie;
import model.Client;
import model.OrdreDeReparation;
import model.Piece;

public class OrdredeReparationDAO {
    public boolean deleteOrdre(OrdreDeReparation o) {
        try (Connection cn = Connexion.getCn()) {
            String query = "DELETE FROM ordre_de_reparation WHERE id=?";
            PreparedStatement ps = cn.prepareStatement(query);
            ps.setInt(1, (int) o.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<OrdreDeReparation> findByNom(String nom) {
        List<OrdreDeReparation> ordres = new ArrayList<>();
        try (Connection cn = Connexion.getCn()) {
            String query = "SELECT o.id, o.nbHeureMo, o.client_id " +
                          "FROM ordre_de_reparation o " +
                          "JOIN client c ON o.client_id = c.id " +
                          "WHERE LOWER(c.nom) LIKE ?";
            PreparedStatement ps = cn.prepareStatement(query);
            ps.setString(1, "%" + nom.toLowerCase() + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                OrdreDeReparation ordre = new OrdreDeReparation();
                ordre.setId(rs.getLong("id"));
                ordre.setNbHeureMo(rs.getInt("nbHeureMo"));
                Client client = new Client();
                client.setId(rs.getLong("client_id"));
                ordre.setClient(client);
                ordres.add(ordre);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ordres;
    }

    public List<Appareil> getAppareilsDetails(Long clientId) {
        List<Appareil> appareils = new ArrayList<>();
        String query = "SELECT * FROM appareil WHERE id_client=?";
        try (Connection cn = Connexion.getCn()) {
            PreparedStatement ps = cn.prepareStatement(query);
            ps.setLong(1, clientId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Appareil appareil = new Appareil();
                appareil.setId(rs.getLong("id"));
                appareil.setDescription(rs.getString("description"));
                appareil.setMarque(rs.getString("marque"));
                appareil.setCategorie(getCategorieByid(rs.getLong("categorie_id")));
                appareils.add(appareil);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appareils;
    }

    public Categorie getCategorieByid(Long catid) {
        Categorie cat = null;
        String query = "SELECT * FROM categorie WHERE id=?";
        try (Connection cn = Connexion.getCn()) {
            PreparedStatement ps = cn.prepareStatement(query);
            ps.setLong(1, catid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                cat = new Categorie();
                cat.setId(rs.getLong("id"));
                cat.setLibelle(rs.getString("libelle"));
                cat.setTarif(rs.getString("tarif"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cat;
    }

    public List<Piece> findPiecesByOrdreId(long ordreId) {
        List<Piece> pieces = new ArrayList<>();
        String sql = "SELECT p.id, p.quantite, p.designation, p.prix " +
                     "FROM piece p " +
                     "JOIN ordre_piece op ON p.id = op.piece_id " +
                     "WHERE op.ordre_id = ?";
        try (Connection cn = Connexion.getCn();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, ordreId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Piece piece = new Piece();
                piece.setId(rs.getLong("id"));
                piece.setQuantite(rs.getInt("quantite"));
                piece.setDesignation(rs.getString("designation"));
                piece.setPrix(rs.getDouble("prix"));
                pieces.add(piece);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pieces;
    }

    public boolean ajouterOrdre(OrdreDeReparation ordre) {
        try (Connection con = Connexion.getCn()) {
            con.setAutoCommit(false);

            Client client = ordre.getClient();
            if (client == null || client.getNom() == null || client.getNom().trim().isEmpty()) {
                throw new IllegalArgumentException("Client information is invalid or missing");
            }
            long clientId = -1;

            String queryCheckClient = "SELECT id FROM Client WHERE nom = ?";
            try (PreparedStatement checkClient = con.prepareStatement(queryCheckClient)) {
                checkClient.setString(1, client.getNom());
                try (ResultSet rsClient = checkClient.executeQuery()) {
                    if (rsClient.next()) {
                        clientId = rsClient.getLong("id");
                    } else {
                        String queryInsertClient = "INSERT INTO Client(nom, adresse, tel) VALUES (?, ?, ?)";
                        try (PreparedStatement insertClient = con.prepareStatement(queryInsertClient,
                                PreparedStatement.RETURN_GENERATED_KEYS)) {
                            insertClient.setString(1, client.getNom());
                            insertClient.setString(2, client.getAdresse());
                            insertClient.setString(3, client.getTel());
                            insertClient.executeUpdate();
                            try (ResultSet rsNewClient = insertClient.getGeneratedKeys()) {
                                if (rsNewClient.next()) {
                                    clientId = rsNewClient.getLong(1);
                                } else {
                                    throw new SQLException("Failed to retrieve client ID");
                                }
                            }
                        }
                    }
                }
            }

            if (ordre.getAppareils() == null || ordre.getAppareils().isEmpty()) {
                throw new IllegalArgumentException("No appareils provided");
            }
            Appareil app = ordre.getAppareils().get(0);
            if (app == null || app.getDescription() == null || app.getMarque() == null ||
                app.getDescription().trim().isEmpty() || app.getMarque().trim().isEmpty()) {
                throw new IllegalArgumentException("Appareil information is invalid or missing");
            }
            long appId = -1;
            String queryInsertApp = "INSERT INTO Appareil(client_id, categorie_id, description, marque) VALUES (?, ?, ?, ?)";
            try (PreparedStatement insertApp = con.prepareStatement(queryInsertApp,
                    PreparedStatement.RETURN_GENERATED_KEYS)) {
                insertApp.setLong(1, clientId);
                if (app.getCategorie() != null && app.getCategorie().getId() != 0) {
                    insertApp.setLong(2, app.getCategorie().getId());
                } else {
                    throw new IllegalArgumentException("Categorie is null or invalid");
                }
                insertApp.setString(3, app.getDescription());
                insertApp.setString(4, app.getMarque());
                insertApp.executeUpdate();
                try (ResultSet rsApp = insertApp.getGeneratedKeys()) {
                    if (rsApp.next()) {
                        appId = rsApp.getLong(1);
                    } else {
                        throw new SQLException("Failed to retrieve appareil ID");
                    }
                }
            }

            long ordreId = -1;
            String queryInsertOrdre = "INSERT INTO OrdreDeReparation(nbHeureMo, client_id) VALUES (?, ?)";
            try (PreparedStatement insertOrdre = con.prepareStatement(queryInsertOrdre,
                    PreparedStatement.RETURN_GENERATED_KEYS)) {
                insertOrdre.setInt(1, ordre.getNbHeureMo());
                insertOrdre.setLong(2, clientId);
                insertOrdre.executeUpdate();
                try (ResultSet rsOrdre = insertOrdre.getGeneratedKeys()) {
                    if (rsOrdre.next()) {
                        ordreId = rsOrdre.getLong(1);
                    } else {
                        throw new SQLException("Failed to retrieve ordre ID");
                    }
                }
            }

            String queryLinkAppareil = "INSERT INTO Ordre_Appareil(ordre_id, appareil_id) VALUES (?, ?)";
            try (PreparedStatement linkAppareil = con.prepareStatement(queryLinkAppareil)) {
                linkAppareil.setLong(1, ordreId);
                linkAppareil.setLong(2, appId);
                linkAppareil.executeUpdate();
            }

            String queryLinkPiece = "INSERT INTO Ordre_Piece(ordre_id, piece_id) VALUES (?, ?)";
            if (ordre.getPieces() != null) {
                for (Piece p : ordre.getPieces()) {
                    try (PreparedStatement linkPiece = con.prepareStatement(queryLinkPiece)) {
                        linkPiece.setLong(1, ordreId);
                        linkPiece.setLong(2, p.getId());
                        linkPiece.executeUpdate();
                    }
                }
            }

            con.commit();
            return true;

        } catch (SQLException ex) {
            ex.printStackTrace();
            try (Connection con = Connexion.getCn()) {
                con.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            return false;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            try (Connection con = Connexion.getCn()) {
                con.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            return false;
        }
    }

    public boolean updateOrdre(OrdreDeReparation ordre) {
        try (Connection con = Connexion.getCn()) {
            con.setAutoCommit(false);

            long ordreId = ordre.getId();

            String queryUpdateOrdre = "UPDATE OrdreDeReparation SET nbHeureMo = ? WHERE id = ?";
            try (PreparedStatement updateOrdre = con.prepareStatement(queryUpdateOrdre)) {
                updateOrdre.setInt(1, ordre.getNbHeureMo());
                updateOrdre.setLong(2, ordreId);
                updateOrdre.executeUpdate();
            }

            if (ordre.getAppareils() == null || ordre.getAppareils().isEmpty()) {
                throw new IllegalArgumentException("No appareils provided");
            }
            Appareil app = ordre.getAppareils().get(0);
            if (app == null || app.getId() == 0 || app.getDescription() == null || app.getMarque() == null ||
                app.getDescription().trim().isEmpty() || app.getMarque().trim().isEmpty()) {
                throw new IllegalArgumentException("Appareil information is invalid or missing");
            }
            String queryUpdateApp = "UPDATE Appareil SET description = ?, marque = ?, categorie_id = ? WHERE id = ?";
            try (PreparedStatement updateApp = con.prepareStatement(queryUpdateApp)) {
                updateApp.setString(1, app.getDescription());
                updateApp.setString(2, app.getMarque());
                if (app.getCategorie() != null && app.getCategorie().getId() != 0) {
                    updateApp.setLong(3, app.getCategorie().getId());
                } else {
                    throw new IllegalArgumentException("Categorie is null or invalid");
                }
                updateApp.setLong(4, app.getId());
                updateApp.executeUpdate();
            }

            String queryDeletePieces = "DELETE FROM Ordre_Piece WHERE ordre_id = ?";
            try (PreparedStatement deletePieces = con.prepareStatement(queryDeletePieces)) {
                deletePieces.setLong(1, ordreId);
                deletePieces.executeUpdate();
            }

            String queryInsertPiece = "INSERT INTO Ordre_Piece(ordre_id, piece_id) VALUES (?, ?)";
            if (ordre.getPieces() != null) {
                for (Piece p : ordre.getPieces()) {
                    try (PreparedStatement insertPiece = con.prepareStatement(queryInsertPiece)) {
                        insertPiece.setLong(1, ordreId);
                        insertPiece.setLong(2, p.getId());
                        insertPiece.executeUpdate();
                    }
                }
            }

            con.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            try (Connection con = Connexion.getCn()) {
                con.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            return false;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            try (Connection con = Connexion.getCn()) {
                con.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            return false;
        }
    }
}*/
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import connexion.Connexion;
import model.Appareil;
import model.Categorie;
import model.Client;
import model.OrdreDeReparation;
import model.Piece;

public class OrdredeReparationDAO {
    public boolean deleteOrdre(OrdreDeReparation o) {
        try (Connection cn = Connexion.getCn()) {
            String query = "DELETE FROM ordre_de_reparation WHERE id=?";
            PreparedStatement ps = cn.prepareStatement(query);
            ps.setInt(1, (int) o.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<OrdreDeReparation> findByNom(String nom) {
        List<OrdreDeReparation> ordres = new ArrayList<>();
        try (Connection cn = Connexion.getCn()) {
            String query = "SELECT o.id, o.nbHeureMo, o.client_id " +
                          "FROM ordre_de_reparation o " +
                          "JOIN client c ON o.client_id = c.id " +
                          "WHERE LOWER(c.nom) LIKE ?";
            PreparedStatement ps = cn.prepareStatement(query);
            ps.setString(1, "%" + nom.toLowerCase() + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                OrdreDeReparation ordre = new OrdreDeReparation();
                ordre.setId(rs.getLong("id"));
                ordre.setNbHeureMo(rs.getInt("nbHeureMo"));
                Client client = new Client();
                client.setId(rs.getLong("client_id"));
                ordre.setClient(client);
                ordres.add(ordre);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ordres;
    }

    public List<Appareil> getAppareilsDetails(Long clientId) {
        List<Appareil> appareils = new ArrayList<>();
        String query = "SELECT * FROM appareil WHERE id_client=?";
        try (Connection cn = Connexion.getCn()) {
            PreparedStatement ps = cn.prepareStatement(query);
            ps.setLong(1, clientId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Appareil appareil = new Appareil();
                appareil.setId(rs.getLong("id"));
                appareil.setDescription(rs.getString("description"));
                appareil.setMarque(rs.getString("marque"));
                appareil.setCategorie(getCategorieByid(rs.getLong("categorie_id")));
                appareils.add(appareil);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appareils;
    }

    public Categorie getCategorieByid(Long catid) {
        Categorie cat = null;
        String query = "SELECT * FROM categorie WHERE id=?";
        try (Connection cn = Connexion.getCn()) {
            PreparedStatement ps = cn.prepareStatement(query);
            ps.setLong(1, catid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                cat = new Categorie();
                cat.setId(rs.getLong("id"));
                cat.setLibelle(rs.getString("libelle"));
                cat.setTarif(rs.getString("tarif"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cat;
    }

    public List<Piece> findPiecesByOrdreId(long ordreId) {
        List<Piece> pieces = new ArrayList<>();
        String sql = "SELECT p.id, p.quantite, p.designation, p.prix " +
                     "FROM piece p " +
                     "JOIN ordre_piece op ON p.id = op.piece_id " +
                     "WHERE op.ordre_id = ?";
        try (Connection cn = Connexion.getCn();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setLong(1, ordreId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Piece piece = new Piece();
                piece.setId(rs.getLong("id"));
                piece.setQuantite(rs.getInt("quantite"));
                piece.setDesignation(rs.getString("designation"));
                piece.setPrix(rs.getDouble("prix"));
                pieces.add(piece);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pieces;
    }

    /*public boolean ajouterOrdre(OrdreDeReparation ordre) {
        Connection con = null;
        try {
            con = Connexion.getCn();
            if (con == null) {
                throw new SQLException("Database connection is null");
            }
            con.setAutoCommit(false);

            Client client = ordre.getClient();
            if (client == null || client.getNom() == null || client.getNom().trim().isEmpty()) {
                throw new IllegalArgumentException("Client information is invalid or missing");
            }
            long clientId = -1;

            String queryCheckClient = "SELECT id FROM Client WHERE nom = ?";
            try (PreparedStatement checkClient = con.prepareStatement(queryCheckClient)) {
                checkClient.setString(1, client.getNom());
                try (ResultSet rsClient = checkClient.executeQuery()) {
                    if (rsClient.next()) {
                        clientId = rsClient.getLong("id");
                    } else {
                        String queryInsertClient = "INSERT INTO Client(nom, adresse, tel) VALUES (?, ?, ?)";
                        try (PreparedStatement insertClient = con.prepareStatement(queryInsertClient,
                                PreparedStatement.RETURN_GENERATED_KEYS)) {
                            insertClient.setString(1, client.getNom());
                            insertClient.setString(2, client.getAdresse());
                            insertClient.setString(3, client.getTel());
                            insertClient.executeUpdate();
                            try (ResultSet rsNewClient = insertClient.getGeneratedKeys()) {
                                if (rsNewClient.next()) {
                                    clientId = rsNewClient.getLong(1);
                                } else {
                                    throw new SQLException("Failed to retrieve client ID");
                                }
                            }
                        }
                    }
                }
            }

            if (ordre.getAppareils() == null || ordre.getAppareils().isEmpty()) {
                throw new IllegalArgumentException("No appareils provided");
            }
            Appareil app = ordre.getAppareils().get(0);
            if (app == null || app.getDescription() == null || app.getMarque() == null ||
                app.getDescription().trim().isEmpty() || app.getMarque().trim().isEmpty()) {
                throw new IllegalArgumentException("Appareil information is invalid or missing");
            }
            long appId = -1;
            String queryInsertApp = "INSERT INTO Appareil(client_id, categorie_id, description, marque) VALUES (?, ?, ?, ?)";
            try (PreparedStatement insertApp = con.prepareStatement(queryInsertApp,
                    PreparedStatement.RETURN_GENERATED_KEYS)) {
                insertApp.setLong(1, clientId);
                if (app.getCategorie() != null && app.getCategorie().getId() != 0) {
                    insertApp.setLong(2, app.getCategorie().getId());
                    System.out.println("Using categorie_id: " + app.getCategorie().getId());
                } else {
                    throw new IllegalArgumentException("Categorie is null or invalid");
                }
                insertApp.setString(3, app.getDescription());
                insertApp.setString(4, app.getMarque());
                insertApp.executeUpdate();
                try (ResultSet rsApp = insertApp.getGeneratedKeys()) {
                    if (rsApp.next()) {
                        appId = rsApp.getLong(1);
                    } else {
                        throw new SQLException("Failed to retrieve appareil ID");
                    }
                }
            }

            long ordreId = -1;
            String queryInsertOrdre = "INSERT INTO OrdreDeReparation(nbHeureMo, client_id) VALUES (?, ?)";
            try (PreparedStatement insertOrdre = con.prepareStatement(queryInsertOrdre,
                    PreparedStatement.RETURN_GENERATED_KEYS)) {
                insertOrdre.setInt(1, ordre.getNbHeureMo());
                insertOrdre.setLong(2, clientId);
                insertOrdre.executeUpdate();
                try (ResultSet rsOrdre = insertOrdre.getGeneratedKeys()) {
                    if (rsOrdre.next()) {
                        ordreId = rsOrdre.getLong(1);
                    } else {
                        throw new SQLException("Failed to retrieve ordre ID");
                    }
                }
            }

            String queryLinkAppareil = "INSERT INTO Ordre_Appareil(ordre_id, appareil_id) VALUES (?, ?)";
            try (PreparedStatement linkAppareil = con.prepareStatement(queryLinkAppareil)) {
                linkAppareil.setLong(1, ordreId);
                linkAppareil.setLong(2, appId);
                linkAppareil.executeUpdate();
            }

            String queryLinkPiece = "INSERT INTO Ordre_Piece(ordre_id, piece_id) VALUES (?, ?)";
            if (ordre.getPieces() != null) {
                for (Piece p : ordre.getPieces()) {
                    try (PreparedStatement linkPiece = con.prepareStatement(queryLinkPiece)) {
                        linkPiece.setLong(1, ordreId);
                        linkPiece.setLong(2, p.getId());
                        linkPiece.executeUpdate();
                    }
                }
            }

            con.commit();
            System.out.println("Order added successfully with ID: " + ordreId);
            return true;

        } catch (SQLException ex) {
            System.err.println("SQLException: " + ex.getMessage());
            ex.printStackTrace();
            if (con != null) {
                try {
                    con.rollback();
                    System.out.println("Transaction rolled back");
                } catch (SQLException rollbackEx) {
                    System.err.println("Rollback failed: " + rollbackEx.getMessage());
                    rollbackEx.printStackTrace();
                }
            }
            return false;
        } catch (IllegalArgumentException e) {
            System.err.println("IllegalArgumentException: " + e.getMessage());
            e.printStackTrace();
            if (con != null) {
                try {
                    con.rollback();
                    System.out.println("Transaction rolled back");
                } catch (SQLException rollbackEx) {
                    System.err.println("Rollback failed: " + rollbackEx.getMessage());
                    rollbackEx.printStackTrace();
                }
            }
            return false;
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException e) {
                    System.err.println("Failed to close connection: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }*/
    
    public boolean ajouterOrdre(OrdreDeReparation ordre) {
        Connection con = null;
        try {
            con = Connexion.getCn();
            if (con == null) {
                throw new SQLException("Database connection is null");
            }
            System.out.println("Connection established successfully");
            con.setAutoCommit(false);

            Client client = ordre.getClient();
            if (client == null || client.getNom() == null || client.getNom().trim().isEmpty()) {
                throw new IllegalArgumentException("Client information is invalid or missing");
            }
            long clientId = -1;

            String queryCheckClient = "SELECT id FROM Client WHERE nom = ?";
            try (PreparedStatement checkClient = con.prepareStatement(queryCheckClient)) {
                System.out.println("Checking client: " + client.getNom());
                checkClient.setString(1, client.getNom());
                try (ResultSet rsClient = checkClient.executeQuery()) {
                    if (rsClient.next()) {
                        clientId = rsClient.getLong("id");
                        System.out.println("Client found with ID: " + clientId);
                    } else {
                        String queryInsertClient = "INSERT INTO Client(nom, adresse, tel) VALUES (?, ?, ?)";
                        try (PreparedStatement insertClient = con.prepareStatement(queryInsertClient,
                                PreparedStatement.RETURN_GENERATED_KEYS)) {
                            System.out.println("Inserting new client: " + client.getNom());
                            insertClient.setString(1, client.getNom());
                            insertClient.setString(2, client.getAdresse());
                            insertClient.setString(3, client.getTel());
                            int rowsAffected = insertClient.executeUpdate();
                            if (rowsAffected > 0) {
                                try (ResultSet rsNewClient = insertClient.getGeneratedKeys()) {
                                    if (rsNewClient.next()) {
                                        clientId = rsNewClient.getLong(1);
                                        System.out.println("New client ID generated: " + clientId);
                                    } else {
                                        throw new SQLException("Failed to retrieve client ID");
                                    }
                                }
                            } else {
                                throw new SQLException("Failed to insert client");
                            }
                        }
                    }
                }
            }

            if (ordre.getAppareils() == null || ordre.getAppareils().isEmpty()) {
                throw new IllegalArgumentException("No appareils provided");
            }
            Appareil app = ordre.getAppareils().get(0);
            if (app == null || app.getDescription() == null || app.getMarque() == null ||
                app.getDescription().trim().isEmpty() || app.getMarque().trim().isEmpty()) {
                throw new IllegalArgumentException("Appareil information is invalid or missing");
            }
            long appId = -1;
            String queryInsertApp = "INSERT INTO Appareil(client_id, categorie_id, description, marque) VALUES (?, ?, ?, ?)";
            try (PreparedStatement insertApp = con.prepareStatement(queryInsertApp,
                    PreparedStatement.RETURN_GENERATED_KEYS)) {
                System.out.println("Inserting appareil with client_id: " + clientId);
                insertApp.setLong(1, clientId);
                if (app.getCategorie() != null && app.getCategorie().getId() != 0) {
                    insertApp.setLong(2, app.getCategorie().getId());
                    System.out.println("Using categorie_id: " + app.getCategorie().getId());
                } else {
                    throw new IllegalArgumentException("Categorie is null or invalid");
                }
                insertApp.setString(3, app.getDescription());
                insertApp.setString(4, app.getMarque());
                int rowsAffected = insertApp.executeUpdate();
                if (rowsAffected > 0) {
                    try (ResultSet rsApp = insertApp.getGeneratedKeys()) {
                        if (rsApp.next()) {
                            appId = rsApp.getLong(1);
                            System.out.println("New appareil ID generated: " + appId);
                        } else {
                            throw new SQLException("Failed to retrieve appareil ID");
                        }
                    }
                } else {
                    throw new SQLException("Failed to insert appareil");
                }
            }

            long ordreId = -1;
            String queryInsertOrdre = "INSERT INTO OrdreDeReparation(nbHeureMo, client_id) VALUES (?, ?)";
            try (PreparedStatement insertOrdre = con.prepareStatement(queryInsertOrdre,
                    PreparedStatement.RETURN_GENERATED_KEYS)) {
                System.out.println("Inserting ordre with nbHeureMo: " + ordre.getNbHeureMo());
                insertOrdre.setInt(1, ordre.getNbHeureMo());
                insertOrdre.setLong(2, clientId);
                int rowsAffected = insertOrdre.executeUpdate();
                if (rowsAffected > 0) {
                    try (ResultSet rsOrdre = insertOrdre.getGeneratedKeys()) {
                        if (rsOrdre.next()) {
                            ordreId = rsOrdre.getLong(1);
                            System.out.println("New ordre ID generated: " + ordreId);
                        } else {
                            throw new SQLException("Failed to retrieve ordre ID");
                        }
                    }
                } else {
                    throw new SQLException("Failed to insert ordre");
                }
            }

            String queryLinkAppareil = "INSERT INTO Ordre_Appareil(ordre_id, appareil_id) VALUES (?, ?)";
            try (PreparedStatement linkAppareil = con.prepareStatement(queryLinkAppareil)) {
                System.out.println("Linking ordre " + ordreId + " with appareil " + appId);
                linkAppareil.setLong(1, ordreId);
                linkAppareil.setLong(2, appId);
                int rowsAffected = linkAppareil.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("Failed to link ordre with appareil");
                }
            }

            String queryLinkPiece = "INSERT INTO Ordre_Piece(ordre_id, piece_id, quantite) VALUES (?, ?, ?)";
            if (ordre.getPieces() != null) {
                for (Piece p : ordre.getPieces()) {
                    try (PreparedStatement linkPiece = con.prepareStatement(queryLinkPiece)) {
                        System.out.println("Linking piece " + p.getId() + " with ordre " + ordreId + " and quantite " + p.getQuantite());
                        linkPiece.setLong(1, ordreId);
                        linkPiece.setLong(2, p.getId());
                        linkPiece.setInt(3, p.getQuantite());
                        int rowsAffected = linkPiece.executeUpdate();
                        if (rowsAffected == 0) {
                            throw new SQLException("Failed to link piece " + p.getId());
                        }
                    }
                }
            }

            con.commit();
            System.out.println("Order added successfully with ID: " + ordreId);
            return true;

        } catch (SQLException ex) {
            System.err.println("SQLException at " + new java.util.Date() + ": " + ex.getMessage());
            ex.printStackTrace();
            if (con != null) {
                try {
                    con.rollback();
                    System.out.println("Transaction rolled back at " + new java.util.Date());
                } catch (SQLException rollbackEx) {
                    System.err.println("Rollback failed at " + new java.util.Date() + ": " + rollbackEx.getMessage());
                    rollbackEx.printStackTrace();
                }
            }
            return false;
        } catch (IllegalArgumentException e) {
            System.err.println("IllegalArgumentException at " + new java.util.Date() + ": " + e.getMessage());
            e.printStackTrace();
            if (con != null) {
                try {
                    con.rollback();
                    System.out.println("Transaction rolled back at " + new java.util.Date());
                } catch (SQLException rollbackEx) {
                    System.err.println("Rollback failed at " + new java.util.Date() + ": " + rollbackEx.getMessage());
                    rollbackEx.printStackTrace();
                }
            }
            return false;
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                    System.out.println("Connection closed at " + new java.util.Date());
                } catch (SQLException e) {
                    System.err.println("Failed to close connection at " + new java.util.Date() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
    public boolean updateOrdre(OrdreDeReparation ordre) {
        try (Connection con = Connexion.getCn()) {
            con.setAutoCommit(false);

            long ordreId = ordre.getId();

            String queryUpdateOrdre = "UPDATE OrdreDeReparation SET nbHeureMo = ? WHERE id = ?";
            try (PreparedStatement updateOrdre = con.prepareStatement(queryUpdateOrdre)) {
                updateOrdre.setInt(1, ordre.getNbHeureMo());
                updateOrdre.setLong(2, ordreId);
                updateOrdre.executeUpdate();
            }

            if (ordre.getAppareils() == null || ordre.getAppareils().isEmpty()) {
                throw new IllegalArgumentException("No appareils provided");
            }
            Appareil app = ordre.getAppareils().get(0);
            if (app == null || app.getId() == 0 || app.getDescription() == null || app.getMarque() == null ||
                app.getDescription().trim().isEmpty() || app.getMarque().trim().isEmpty()) {
                throw new IllegalArgumentException("Appareil information is invalid or missing");
            }
            String queryUpdateApp = "UPDATE Appareil SET description = ?, marque = ?, categorie_id = ? WHERE id = ?";
            try (PreparedStatement updateApp = con.prepareStatement(queryUpdateApp)) {
                updateApp.setString(1, app.getDescription());
                updateApp.setString(2, app.getMarque());
                if (app.getCategorie() != null && app.getCategorie().getId() != 0) {
                    updateApp.setLong(3, app.getCategorie().getId());
                } else {
                    throw new IllegalArgumentException("Categorie is null or invalid");
                }
                updateApp.setLong(4, app.getId());
                updateApp.executeUpdate();
            }

            String queryDeletePieces = "DELETE FROM Ordre_Piece WHERE ordre_id = ?";
            try (PreparedStatement deletePieces = con.prepareStatement(queryDeletePieces)) {
                deletePieces.setLong(1, ordreId);
                deletePieces.executeUpdate();
            }

            String queryInsertPiece = "INSERT INTO Ordre_Piece(ordre_id, piece_id) VALUES (?, ?)";
            if (ordre.getPieces() != null) {
                for (Piece p : ordre.getPieces()) {
                    try (PreparedStatement insertPiece = con.prepareStatement(queryInsertPiece)) {
                        insertPiece.setLong(1, ordreId);
                        insertPiece.setLong(2, p.getId());
                        insertPiece.executeUpdate();
                    }
                }
            }

            con.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            try (Connection con = Connexion.getCn()) {
                con.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            return false;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            try (Connection con = Connexion.getCn()) {
                con.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            return false;
        }
    }
}
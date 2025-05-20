package model;

import java.util.ArrayList;
import java.util.List;

public class OrdreDeReparation {
	private long id;
			private List <Appareil> appareils=new ArrayList<> ();
			private int nbHeureMo;
			private  List <Piece> pieces=new ArrayList<> ();
			private Client client;
			public Client getClient() {
				return client;
			}
			public void setClient(Client client) {
				this.client = client;
			}
			public long getId() {
				return id;
			}
			public void setId(long id) {
				this.id = id;
			}
		
			public List<Appareil> getAppareils() {
				return appareils;
			}
			public void setAppareils(List<Appareil> appareils) {
				this.appareils = appareils;
			}
			public int getNbHeureMo() {
				return nbHeureMo;
			}
			public void setNbHeureMo(int nbHeureMo) {
				this.nbHeureMo = nbHeureMo;
			}
			public List<Piece> getPieces() {
				return pieces;
			}
			public void setPieces(List<Piece> pieces) {
				this.pieces = pieces;
			}
			
}

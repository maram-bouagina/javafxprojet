package application;

import java.util.List;

public class PiecesDetachee {
			private String designation;
			private double prixHT;
			private  List <PieceAChanger> piecesAChanger;
			public String getDesignation() {
				return designation;
			}
			public void setDesignation(String designation) {
				this.designation = designation;
			}
			public double getPrixHT() {
				return prixHT;
			}
			public void setPrixHT(double prixHT) {
				this.prixHT = prixHT;
			}
			public List<PieceAChanger> getPiecesAChanger() {
				return piecesAChanger;
			}
			public void setPiecesAChanger(List<PieceAChanger> piecesAChanger) {
				this.piecesAChanger = piecesAChanger;
			}
			
			
}

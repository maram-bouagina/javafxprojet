package application;

public class PieceAChanger {
			private PiecesDetachee pd;
			private int quantite;
			private OrdreDeReparation  ordre;
			public PiecesDetachee getPd() {
				return pd;
			}
			public void setPd(PiecesDetachee pd) {
				this.pd = pd;
			}
			public int getQuantite() {
				return quantite;
			}
			public void setQuantite(int quantite) {
				this.quantite = quantite;
			}
			public OrdreDeReparation getOrdre() {
				return ordre;
			}
			public void setOrdre(OrdreDeReparation ordre) {
				this.ordre = ordre;
			}
			
			
}

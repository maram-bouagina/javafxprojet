package application;

import java.util.List;

public class OrdreDeReparation {
			private Appareil appareil;
			private int nbHeureMo;
			private  List <PieceAChanger> piecesAChanger;
			public Appareil getAppareil() {
				return appareil;
			}
			public void setAppareil(Appareil appareil) {
				this.appareil = appareil;
			}
			public int getNbHeureMo() {
				return nbHeureMo;
			}
			public void setNbHeureMo(int nbHeureMo) {
				this.nbHeureMo = nbHeureMo;
			}
			public List<PieceAChanger> getPiecesAChanger() {
				return piecesAChanger;
			}
			public void setPiecesAChanger(List<PieceAChanger> piecesAChanger) {
				this.piecesAChanger = piecesAChanger;
			}
			
}

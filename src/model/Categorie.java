package application;

import java.util.List;

public class Categorie {
			private String libelle;
			private String tarif;
			private List<Appareil> apps;
			public String getLibelle() {
				return libelle;
			}
			public void setLibelle(String libelle) {
				this.libelle = libelle;
			}
			public String getTarif() {
				return tarif;
			}
			public void setTarif(String tarif) {
				this.tarif = tarif;
			}
			public List<Appareil> getApps() {
				return apps;
			}
			public void setApps(List<Appareil> apps) {
				this.apps = apps;
			}
}

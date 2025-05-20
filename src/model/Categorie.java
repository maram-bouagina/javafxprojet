package model;

import java.util.List;

public class Categorie {
	        private long id;
			private String libelle;
			private String tarif;
			private List<Appareil> apps;
			
			public long getId() {
				return id;
			}
			public void setId(long id) {
				this.id = id;
			}
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

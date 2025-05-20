package model;

import java.util.List;

public class Client {
			private long id;
			private String nom;
			private String adresse;
			private String tel;
			private List <Appareil> apps;
			public String getNom() {
				return nom;
			}
			public void setNom(String nom) {
				this.nom = nom;
			}
			public String getAdresse() {
				return adresse;
			}
			public void setAdresse(String adresse) {
				this.adresse = adresse;
			}
			public String getTel() {
				return tel;
			}
			public void setTel(String tel) {
				this.tel = tel;
			}
			public long getId() {
				return id;
			}
			public void setId(long id) {
				this.id = id;
			}
			public List<Appareil> getApps() {
				return apps;
			}
			public void setApps(List<Appareil> apps) {
				this.apps = apps;
			}
			
}

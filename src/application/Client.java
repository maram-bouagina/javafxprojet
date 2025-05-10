package application;

import java.util.List;

public class Client {
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
			
}

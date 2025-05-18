package application;

import java.util.List;

public class Appareil {
			private Client client;
			private Categorie categorie;
			private String description;
			private String marque;
			private List<OrdreDeReparation> ordrerep;
			public Client getClient() {
				return client;
			}
			public void setClient(Client client) {
				this.client = client;
			}
			public List<OrdreDeReparation> getOrdrerep() {
				return ordrerep;
			}
			public void setOrdrerep(List<OrdreDeReparation> ordrerep) {
				this.ordrerep = ordrerep;
			}
			public Categorie getCategorie() {
				return categorie;
			}
			public void setCategorie(Categorie categorie) {
				this.categorie = categorie;
			}
			public String getDescription() {
				return description;
			}
			public void setDescription(String description) {
				this.description = description;
			}
			public String getMarque() {
				return marque;
			}
			public void setMarque(String marque) {
				this.marque = marque;
			}     
}

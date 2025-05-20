package model;

public class Appareil {
    private long id;

			private Client client;
			private Categorie categorie;
			private String description;
			private String marque;
		
			public Client getClient() {
				return client;
			}
			public void setClient(Client client) {
				this.client = client;
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
			public long getId() {
				return id;
			}
			public void setId(long id) {
				this.id = id;
			}     
}

package entity;

public class ProfiloPersonale {
    private String biografia;
    private String immagine;

    public ProfiloPersonale(String biografia, String immagine) {
        this.biografia = biografia;
        this.immagine = immagine;
    }

    //riga 147 di controller.GestoreController
    public boolean aggiornaProfiloUtente(dto.ProfiloUtenteDTO profilo) {
        return new database.UtenteDAO().aggiornaProfiloUtente(profilo);
    }
}


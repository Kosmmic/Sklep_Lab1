package pl.sklep.skleplab.domain;

public class Uzytkownik{
    private String email;
    private String hasloHash;
    private Rola rola;

    public Uzytkownik(String email, String hasloHash, Rola rola){
        this.email = email;
        this.hasloHash = hasloHash;
        this.rola = rola;
    }

    public String getEmail() {return email;}
    public String getHasloHash() {return hasloHash;}
    public Rola getRola() {return rola;}

    public void setEmail(String email) {this.email = email;}
    public void setRola(Rola rola) {this.rola = rola;} 
}
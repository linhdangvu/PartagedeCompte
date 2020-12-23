package dangvulinh.tricount;

public class ListProjet {
    private String projet;
    private String description;
    private String date;
    private int id;
    private int imginto;
    private int imgchange;

    public ListProjet(String projet, String description, String date, int id, int imginto, int imgchange) {
        this.projet = projet;
        this.description = description;
        this.date = date;
        this.id = id;
        this.imginto = imginto;
        this.imgchange = imgchange;
    }

    public String getProjet() {
        return projet;
    }

    public void setProjet(String projet) {
        this.projet = projet;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getImginto() {
        return imginto;
    }

    public void setImginto(int imginto) {
        this.imginto = imginto;
    }

    public int getImgchange() {
        return imgchange;
    }

    public void setImgchange(int imgchange) {
        this.imgchange = imgchange;
    }
}

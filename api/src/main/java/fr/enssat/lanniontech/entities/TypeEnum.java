package fr.enssat.lanniontech.entities;

/**
 * Created by Romain on 26/09/2016.
 */
public enum  TypeEnum {

    ROUTE_SIMPLE(1),
    ROUTE_2X2(2),
    ROUTE_2X3(3),
    CARREFOUR_GIRATOIRE(4),
    FEU_ROUGE(5);

    private int id;

    TypeEnum(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}

package de.uniba.dsg.jaxrs.db;

import de.uniba.dsg.jaxrs.model.*;

import java.util.*;

class State {

    private final List<Bottle> bottles;
    private final List<Crate> crates;

    public State(List<Bottle> bottles, List<Crate> crates) {
        this.bottles = bottles;
        this.crates = crates;
    }

    public List<Bottle> getBottles() {
        return bottles;
    }

    public List<Crate> getCrates() {
        return crates;
    }

    State getInitState() {
        return new State(initBottles(), initCrates());
    }

    //region database seeding
    private List<Bottle> initBottles() {
        return new ArrayList<>(Arrays.asList(
                new Bottle(1, "Pils", 0.5, true, 4.8, 0.79, "Keesmann", 34),
                new Bottle(2, "Helles", 0.5, true, 4.9, 0.89, "Mahr", 17),
                new Bottle(3, "Boxbeutel", 0.75, true, 12.5, 5.79, "Divino", 11),
                new Bottle(4, "Tequila", 0.7, true, 40.0, 13.79, "Tequila Inc.", 5),
                new Bottle(5, "Gin", 0.5, true, 42.00, 11.79, "Hopfengarten", 3),
                new Bottle(6, "Export Edel", 0.5, true, 4.8, 0.59, "Oettinger", 66),
                new Bottle(7, "Premium Tafelwasser", 0.7, false, 0.0, 4.29, "Franken Brunnen", 12),
                new Bottle(8, "Wasser", 0.5, false, 0.0, 0.29, "Franken Brunnen", 57),
                new Bottle(9, "Spezi", 0.7, false, 0.0, 0.69, "Franken Brunnen", 42),
                new Bottle(10, "Grape Mix", 0.5, false, 0.0, 0.59, "Franken Brunnen", 12),
                new Bottle(11, "Still", 1.0, false, 0.0, 0.66, "Franken Brunnen", 34),
                new Bottle(12, "Cola", 1.5, false, 0.0, 1.79, "CCC", 69),
                new Bottle(13, "Cola Zero", 2.0, false, 0.0, 2.19, "CCC", 12),
                new Bottle(14, "Apple", 0.5, false, 0.0, 1.99, "Juice Factory", 25),
                new Bottle(15, "Orange", 0.5, false, 0.0, 1.99, "Juice Factory", 55),
                new Bottle(16, "Lime", 0.5, false, 0.0, 2.99, "Juice Factory", 8)
        ));
    }

    private List<Crate> initCrates() {
        List<Bottle> initBottles = initBottles();

        return new ArrayList<>(Arrays.asList(
                new Crate(1, initBottles.get(0), 20, 14.99, 3),
                new Crate(2, initBottles.get(1), 20, 15.99, 5),
                new Crate(3, initBottles.get(2), 6, 30.00, 7),
                new Crate(4, initBottles.get(7), 12, 1.99, 11),
                new Crate(5, initBottles.get(8), 20, 11.99, 13),
                new Crate(6, initBottles.get(11), 6, 10.99, 4),
                new Crate(7, initBottles.get(12), 6, 11.99, 5),
                new Crate(8, initBottles.get(13), 20, 35.00, 7),
                new Crate(9, initBottles.get(14), 12, 20.00, 9)
        ));
    }
    //endregion
}

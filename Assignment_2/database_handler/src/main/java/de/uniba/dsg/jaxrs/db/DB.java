package de.uniba.dsg.jaxrs.db;

import de.uniba.dsg.jaxrs.model.*;

import java.util.*;

public class DB {

    public static DB instance;

    private DB() {
    }

    public static synchronized DB getInstance() {
        if (instance == null) {
            instance = new DB();
        }

        return instance;
    }

    //region Create
    public synchronized Crate createCrate(Crate crate) {
        int crateId = getCrates()
                .stream()
                .max(Comparator.comparing(Crate::getId))
                .map(x -> x.getId() + 1)
                .orElse(0);

        crate.setId(crateId);

        State state = FileSystem.readState();
        state.getCrates().add(crate);
        FileSystem.writeState(state);

        return crate;
    }

    public synchronized Bottle createBottle(Bottle bottle) {
        int bottleId = getBottles()
                .stream()
                .max(Comparator.comparing(Bottle::getId))
                .map(x -> x.getId() + 1)
                .orElse(0);

        bottle.setId(bottleId);

        State state = FileSystem.readState();
        state.getBottles().add(bottle);
        FileSystem.writeState(state);

        return bottle;
    }
    //endregion

    //region Read
    public synchronized List<Bottle> getBottles() {
        return FileSystem.readState().getBottles();
    }

    public synchronized List<Crate> getCrates() {
        return FileSystem.readState().getCrates();
    }

    public synchronized Optional<Bottle> getBottleById(int bottleId) {
        return getBottles()
                .stream()
                .filter(x -> x.getId() == bottleId)
                .findFirst();
    }

    public synchronized Optional<Crate> getCrateById(int crateId) {
        return getCrates()
                .stream()
                .filter(x -> x.getId() == crateId)
                .findFirst();
    }
    //endregion

    //region Update
    public synchronized Optional<Bottle> updateBottleWithId(int bottleId, Bottle updatedBottle) {
        Optional<Bottle> oldBottle = getBottleById(bottleId);

        if(!oldBottle.isPresent()) return Optional.empty();

        updatedBottle.setId(oldBottle.get().getId());

        State state = FileSystem.readState();

        int bottleIndex = state.getBottles().indexOf(oldBottle.get());

        state.getBottles().remove(bottleIndex);
        state.getBottles().add(bottleIndex, updatedBottle);

        FileSystem.writeState(state);

        return Optional.of(updatedBottle);
    }

    public synchronized Optional<Crate> updateCrateWithId(int crateId, Crate updatedCrate) {
        Optional<Crate> oldCrate = getCrateById(crateId);

        if(!oldCrate.isPresent()) return Optional.empty();

        updatedCrate.setId(oldCrate.get().getId());

        State state = FileSystem.readState();

        int crateIndex = state.getCrates().indexOf(oldCrate.get());

        state.getCrates().remove(crateIndex);
        state.getCrates().add(crateIndex, updatedCrate);

        FileSystem.writeState(state);

        return Optional.of(updatedCrate);
    }
    //endregion

    //region Delete
    public synchronized boolean deleteBottleWithId(int bottleId){
        Optional<Bottle> bottleToDelete = getBottleById(bottleId);

        if(!bottleToDelete.isPresent()) {
            return false;
        }

        State state = FileSystem.readState();
        state.getBottles().remove(bottleToDelete.get());
        FileSystem.writeState(state);

        return true;
    }

    public synchronized boolean deleteCrateWithId(int crateId) {
        Optional<Crate> crateToDelete = getCrateById(crateId);

        if(!crateToDelete.isPresent()) {
            return false;
        }

        State state = FileSystem.readState();
        state.getCrates().remove(crateToDelete.get());
        FileSystem.writeState(state);

        return true;
    }
    //endregion
}

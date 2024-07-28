package me.dave.itempools.data.storage;

import me.dave.itempools.data.ItemPoolGoalData;

public interface Storage {

    ItemPoolGoalData loadPoolData(String poolId);

    void savePoolData(ItemPoolGoalData itemPoolData);
}

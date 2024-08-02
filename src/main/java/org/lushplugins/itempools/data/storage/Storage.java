package org.lushplugins.itempools.data.storage;

import org.lushplugins.itempools.data.ItemPoolGoalData;

public interface Storage {

    ItemPoolGoalData loadPoolData(String poolId);

    void savePoolData(ItemPoolGoalData itemPoolData);

    void deletePoolData(String poolId);
}

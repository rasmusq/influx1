package com.ralleq.influx.objects.modules.arrays;

import com.ralleq.influx.objects.modules.ModuleReturner;
import com.ralleq.influx.objects.modules.ModuleReturnerArray;
import com.ralleq.influx.objects.modules.effects.NothingEffect;

public class ModifierHandler extends ModuleReturnerArray {

    public ModifierHandler(int[] parentLocation, int newLocation) {
        super(parentLocation, newLocation);

        moduleReturners = new ModuleReturner[3];
        moduleReturners[0] = new ModifierArray(location, 0);
        moduleReturners[1] = new ModifierArray(location, 1);
        moduleReturners[2] = new ModifierArray(location, 2);

    }

}

package com.ralleq.influx.objects.modules.arrays;

import com.ralleq.influx.objects.modules.ModuleReturner;
import com.ralleq.influx.objects.modules.ModuleReturnerArray;

public class EffectHandler extends ModuleReturnerArray {

    public EffectHandler(int[] parentLocation, int newLocation) {
        super(parentLocation, newLocation);

        moduleReturners = new ModuleReturner[3];
        moduleReturners[0] = new EffectArray(location, 0);
        moduleReturners[1] = new EffectArray(location, 0);
        moduleReturners[2] = new EffectArray(location, 0);

    }

}

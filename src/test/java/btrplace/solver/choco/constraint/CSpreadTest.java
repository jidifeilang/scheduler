/*
 * Copyright (c) 2012 University of Nice Sophia-Antipolis
 *
 * This file is part of btrplace.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package btrplace.solver.choco.constraint;

import btrplace.model.*;
import btrplace.model.constraint.Fence;
import btrplace.model.constraint.Online;
import btrplace.model.constraint.Spread;
import btrplace.plan.ReconfigurationPlan;
import btrplace.solver.SolverException;
import btrplace.solver.choco.ChocoReconfigurationAlgorithm;
import btrplace.solver.choco.DefaultChocoReconfigurationAlgorithm;
import junit.framework.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Unit tests for {@link CSpread}.
 *
 * @author Fabien Hermenier
 */
public class CSpreadTest {

    private static UUID vm1 = UUID.randomUUID();
    private static UUID vm2 = UUID.randomUUID();

    private static UUID n1 = UUID.randomUUID();
    private static UUID n2 = UUID.randomUUID();
    private static UUID n3 = UUID.randomUUID();

    private static Model getModel() {
        Mapping map = new DefaultMapping();
        map.addOnlineNode(n1);
        map.addOnlineNode(n2);
        map.addOnlineNode(n3);
        map.addRunningVM(vm1, n1);
        map.addRunningVM(vm2, n2);
        return new DefaultModel(map);
    }

    @Test
    public void testDiscrete() throws SolverException {
        Model m = getModel();
        List<SatConstraint> cstr = new ArrayList<SatConstraint>();
        ChocoReconfigurationAlgorithm cra = new DefaultChocoReconfigurationAlgorithm();
        cra.labelVariables(true);
        Spread s = new Spread(m.getMapping().getAllVMs());
        s.setContinuous(false);
        cstr.add(s);
        cstr.add(new Online(m.getMapping().getAllNodes()));
        cstr.add(new Fence(Collections.singleton(vm1), Collections.singleton(n2)));
        ReconfigurationPlan p = cra.solve(m, cstr);
        Assert.assertNotNull(p);
        System.err.println(p);
        Mapping res = p.getResult().getMapping();
        Assert.assertEquals(2, p.getSize());
        Assert.assertNotSame(res.getVMLocation(vm1), res.getVMLocation(vm2));
    }

    @Test
    public void testContinuous() throws SolverException {
        Model m = getModel();
        List<SatConstraint> cstr = new ArrayList<SatConstraint>();
        ChocoReconfigurationAlgorithm cra = new DefaultChocoReconfigurationAlgorithm();
        cra.labelVariables(true);
        Spread s = new Spread(m.getMapping().getAllVMs());
        s.setContinuous(true);
        cstr.add(s);
        cstr.add(new Online(m.getMapping().getAllNodes()));
        cstr.add(new Fence(Collections.singleton(vm1), Collections.singleton(n2)));
        ReconfigurationPlan p = cra.solve(m, cstr);
        Assert.assertNotNull(p);
        System.err.println(p);
        Mapping res = p.getResult().getMapping();
        Assert.assertEquals(2, p.getSize());
        Assert.assertNotSame(res.getVMLocation(vm1), res.getVMLocation(vm2));
    }
}

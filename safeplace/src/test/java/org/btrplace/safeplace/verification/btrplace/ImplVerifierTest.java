/*
 * Copyright (c) 2014 University Nice Sophia Antipolis
 *
 * This file is part of btrplace.
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.btrplace.safeplace.verification.btrplace;

import org.btrplace.model.DefaultModel;
import org.btrplace.model.Model;
import org.btrplace.model.Node;
import org.btrplace.model.VM;
import org.btrplace.plan.DefaultReconfigurationPlan;
import org.btrplace.plan.ReconfigurationPlan;
import org.btrplace.plan.event.BootVM;
import org.btrplace.plan.event.KillVM;
import org.btrplace.plan.event.SuspendVM;
import org.btrplace.safeplace.Constraint;
import org.btrplace.safeplace.Specification;
import org.btrplace.safeplace.spec.SpecExtractor;
import org.btrplace.safeplace.spec.term.Constant;
import org.btrplace.safeplace.spec.type.NodeType;
import org.btrplace.safeplace.spec.type.SetType;
import org.btrplace.safeplace.spec.type.VMType;
import org.btrplace.safeplace.verification.CheckerResult;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * @author Fabien Hermenier
 */
public class ImplVerifierTest {

    private static SpecExtractor ex = new SpecExtractor();

    private static Specification getSpecification() throws Exception {
        return ex.extract();
    }

    @Test
    public void testAmong() throws Exception {
        Model mo = new DefaultModel();
        Node n0 = mo.newNode();
        Node n1 = mo.newNode();
        VM vm0 = mo.newVM();
        VM vm1 = mo.newVM();
        mo.getMapping().addOnlineNode(n0);
        mo.getMapping().addOnlineNode(n1);
        mo.getMapping().addReadyVM(vm0);
        mo.getMapping().addReadyVM(vm1);

        ReconfigurationPlan p = new DefaultReconfigurationPlan(mo);
        p.add(new BootVM(vm0, n1, 0, 1));
        p.add(new BootVM(vm1, n1, 0, 1));
        /*Model dst = mo.clone();
        dst.getMapping().addRunningVM(vm0, n1);
        dst.getMapping().addRunningVM(vm1, n1);*/
        Specification spec = getSpecification();
        Constraint c = spec.get("among");
        ImplVerifier iv = new ImplVerifier();
        //iv.continuous(false);
        List<Constant> args = Arrays.asList(
                new Constant(Collections.singletonList(vm1), new SetType(VMType.getInstance())),
                new Constant(Collections.singleton(new HashSet<>(Arrays.asList(n0))), new SetType(new SetType(NodeType.getInstance()))));

        /*TestCase tc = new TestCase("", c, Arrays.asList(
                new Constant(Collections.singletonList(vm1), new SetType(VMType.getInstance())),
                new Constant(Collections.singleton(new HashSet<>(Arrays.asList(n0))), new SetType(new SetType(NodeType.getInstance())))
        ), p, false);
        */
        CheckerResult res = iv.verify(c, args, p.getOrigin(), p.getResult());
        Assert.assertEquals(res.getStatus(), Boolean.TRUE);

    }

    @Test
    public void testLonely() throws Exception {
        Model mo = new DefaultModel();
        Node n0 = mo.newNode();
        Node n1 = mo.newNode();
        VM v0 = mo.newVM();
        VM v1 = mo.newVM();
        mo.getMapping().addOnlineNode(n0);
        mo.getMapping().addOnlineNode(n1);
        mo.getMapping().addRunningVM(v0, n1);
        mo.getMapping().addReadyVM(v1);
        ReconfigurationPlan p = new DefaultReconfigurationPlan(mo);
        p.add(new SuspendVM(v0, n1, n1, 0, 3));
        p.add(new BootVM(v1, n1, 0, 3));
        ImplVerifier v = new ImplVerifier();
        //v.continuous(true);
        Constraint c = getSpecification().get("lonely");
        List<Constant> args = Arrays.asList(new Constant(new HashSet(Arrays.asList(v0, v1)), new SetType(VMType.getInstance())));
        System.out.println(v.verify(c, args, p.getOrigin(), p.getResult()));
        //TestCase tc = new TestCase(v, c, p, args, true);
        //System.out.println(tc.pretty(true));
        Assert.fail();
    }

    @Test
    public void testKill() throws Exception {
        Model mo = new DefaultModel();
        VM v = mo.newVM();
        Node n = mo.newNode();
        mo.getMapping().addOnlineNode(n);
        mo.getMapping().addRunningVM(v, n);
        ReconfigurationPlan p = new DefaultReconfigurationPlan(mo);
        p.add(new KillVM(v, n, 1, 4));
        ImplVerifier i = new ImplVerifier();
        Constraint c = getSpecification().get("noVMsOnOfflineNodes");
        CheckerResult res = i.verify(c, Collections.<Constant>emptyList(), p);
        Assert.assertTrue(res.getStatus());
    }
}

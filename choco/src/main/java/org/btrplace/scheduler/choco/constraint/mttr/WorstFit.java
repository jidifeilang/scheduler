/*
 * Copyright (c) 2017 University Nice Sophia Antipolis
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

package org.btrplace.scheduler.choco.constraint.mttr;

import org.btrplace.model.Mapping;
import org.btrplace.model.VM;
import org.btrplace.scheduler.choco.ReconfigurationProblem;
import org.btrplace.scheduler.choco.constraint.mttr.load.GlobalLoadEstimator;
import org.btrplace.scheduler.choco.view.CShareableResource;
import org.btrplace.scheduler.choco.view.ChocoView;
import org.btrplace.scheduler.choco.view.Packing;
import org.btrplace.scheduler.choco.view.VectorPacking;
import org.chocosolver.memory.IStateInt;
import org.chocosolver.solver.search.strategy.selectors.values.IntValueSelector;
import org.chocosolver.solver.variables.IntVar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Placement heuristic implementing a worst fit.
 * @author Fabien Hermenier
 */
public class WorstFit implements IntValueSelector {

    private boolean stayFirst;

    private Map<IntVar, VM> vmMap;

    private ReconfigurationProblem rp;

    private GlobalLoadEstimator globalLoad;

    private List<CShareableResource> rcs;

    private VectorPacking packing;

    private Map<Integer, int[]> usages;

    private Map<Integer, int[]> capacities;

    /**
     * New heuristic.
     * Will try to make the VM stay on their current node in prior if possible
     *
     * @param vmMap the VM -> placement variable mapping
     * @param rp    the CSP to solve
     * @param load  the load estimator for the node.
     */
    public WorstFit(Map<IntVar, VM> vmMap, ReconfigurationProblem rp, GlobalLoadEstimator load) {
        this(vmMap, rp, load, true);
    }

    /**
     * New heuristic.
     * @param vmMap the VM -> placement variable mapping
     * @param rp the CSP to solve
     * @param load the load estimator for the node.
     * @param stayFirst {@code true} to try to let the VM on place first if possible
     */
    public WorstFit(Map<IntVar, VM> vmMap, ReconfigurationProblem rp, GlobalLoadEstimator load, boolean stayFirst) {
        this.stayFirst = stayFirst;
        this.vmMap = vmMap;
        globalLoad = load;
        packing = (VectorPacking) rp.getView(Packing.VIEW_ID);
        this.rp = rp;
        rcs = new ArrayList<>();
        for (String s : rp.getViews()) {
            ChocoView cv = rp.getView(s);
            if (cv instanceof CShareableResource) {
                rcs.add((CShareableResource) cv);
            }
        }

        usages = new HashMap<>();
        capacities = new HashMap<>();
    }

    @Override
    public int selectValue(IntVar v) {
        VM vm = vmMap.get(v);
        if (stayFirst && canStay(vm)) {
            return rp.getNode(rp.getSourceModel().getMapping().getVMLocation(vm));
        }

        //Get the load
        int leastId = v.getLB();
        double minLoad = 2;
        IStateInt[] loads = new IStateInt[rcs.size()];
        for (int nId = v.getLB(); nId <= v.getUB(); nId = v.nextValue(nId)) {
            for (int d = 0; d < rcs.size(); d++) {
                loads[d] = packing.assignedLoad()[d][nId];
            }
            double global = loadWith(nId, loads, vm);

            if (global < minLoad) {
                leastId = nId;
                minLoad = global;
            }
        }
        return leastId;
    }

    private IStateInt[] load(int nId) {
        IStateInt[] loads = new IStateInt[rcs.size()];
        for (int d = 0; d < rcs.size(); d++) {
            loads[d] = packing.assignedLoad()[d][nId];
        }
        return loads;
    }

    private int[] capacities(int nIdx) {
        int[] capa = capacities.get(nIdx);
        if (capa == null) {
            capa = new int[rcs.size()];
            for (int i = 0; i < rcs.size(); i++) {
                capa[i] += rcs.get(i).getVirtualUsage().get(nIdx).getUB() * rcs.get(i).getOverbookRatio(nIdx);
            }
            capacities.put(nIdx, capa);
        }
        return capa;
    }

    private int[] usage(int vId) {
        int[] usage = usages.get(vId);
        if (usage == null) {
            usage = new int[rcs.size()];
            for (int i = 0; i < rcs.size(); i++) {
                usage[i] += rcs.get(i).getVMAllocation(vId);
            }
            usages.put(vId, usage);
        }
        return usage;

    }

    private double loadWith(int nId, IStateInt[] loads, VM vm) {
        int[] capas = capacities(nId);
        double[] normalised = new double[capas.length];
        int[] usage = usage(rp.getVM(vm));
        for (int i = 0; i < capas.length; i++) {
            normalised[i] = (1.0d * loads[i].get() + usage[i]) / capas[i];
        }
        return globalLoad.getLoad(normalised);
    }

    /**
     * Check if a VM can stay on its current node.
     *
     * @param vm the VM
     * @return {@code true} iff the VM can stay
     */
    private boolean canStay(VM vm) {
        Mapping m = rp.getSourceModel().getMapping();
        if (m.isRunning(vm)) {
            int curPos = rp.getNode(m.getVMLocation(vm));
            if (!rp.getVMAction(vm).getDSlice().getHoster().contains(curPos)) {
                return false;
            }
            IStateInt[] loads = load(curPos);
            return loadWith(curPos, loads, vm) <= 1.0;
        }
        return false;
    }
}

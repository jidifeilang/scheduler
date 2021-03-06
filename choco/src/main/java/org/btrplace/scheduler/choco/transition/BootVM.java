/*
 * Copyright (c) 2016 University Nice Sophia Antipolis
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

package org.btrplace.scheduler.choco.transition;

import org.btrplace.model.Node;
import org.btrplace.model.VM;
import org.btrplace.model.VMState;
import org.btrplace.plan.ReconfigurationPlan;
import org.btrplace.scheduler.SchedulerException;
import org.btrplace.scheduler.choco.ReconfigurationProblem;
import org.btrplace.scheduler.choco.Slice;
import org.btrplace.scheduler.choco.SliceBuilder;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;

import java.util.EnumSet;


/**
 * Model a transition that allows a ready VP to be booted on a node.
 * An estimation of the action duration must be provided through a
 * {@link org.btrplace.scheduler.choco.duration.ActionDurationEvaluator} accessible from
 * {@link org.btrplace.scheduler.choco.ReconfigurationProblem#getDurationEvaluators()} with the key {@code BootVM.class}
 * <p>
 * If the reconfiguration problem has a solution, a {@link org.btrplace.plan.event.BootVM} action
 * is inserted into the resulting reconfiguration plan.
 *
 * @author Fabien Hermenier
 */
public class BootVM implements VMTransition {

    /**
     * The prefix to use for the variables
     */
    public static final String VAR_PREFIX = "bootVM";

    private Slice dSlice;

    private IntVar end;

    private IntVar start;

    private IntVar duration;

    private VM vm;

    private ReconfigurationProblem rp;

    private BoolVar state;

    /**
     * Make a new model.
     *
     * @param p the RP to use as a basis.
     * @param e the VM managed by the action
     * @throws org.btrplace.scheduler.SchedulerException if an error occurred
     */
    public BootVM(ReconfigurationProblem p, VM e) throws SchedulerException {
        vm = e;

        Model csp = p.getModel();
        int d = p.getDurationEvaluators().evaluate(p.getSourceModel(), org.btrplace.plan.event.BootVM.class, e);
        this.rp = p;
        start = p.makeDuration(p.getEnd().getUB() - d, 0, VAR_PREFIX, "(", e, ").start");
        end = rp.getModel().intOffsetView(start, d);
        duration = p.makeDuration(d, d, VAR_PREFIX, "(", e, ").duration");
        dSlice = new SliceBuilder(p, e, VAR_PREFIX, "(", e, ").dSlice").setStart(start)
                .setDuration(p.makeDuration(p.getEnd().getUB(), d, VAR_PREFIX, "(", e, ").dSlice_duration"))
                .build();

        csp.post(csp.arithm(end, "<=", p.getEnd()));
        csp.post(csp.arithm(duration, "<=", p.getEnd()));

        state = csp.boolVar(true);
    }

    @Override
    public boolean isManaged() {
        return true;
    }

    @Override
    public boolean insertActions(Solution s, ReconfigurationPlan plan) {
        Node node = rp.getNode(s.getIntVal(dSlice.getHoster()));
        org.btrplace.plan.event.BootVM a = new org.btrplace.plan.event.BootVM(vm, node, s.getIntVal(start), s.getIntVal(end));
        plan.add(a);
        return true;
    }

    @Override
    public IntVar getStart() {
        return start;
    }

    @Override
    public IntVar getEnd() {
        return end;
    }

    @Override
    public IntVar getDuration() {
        return duration;
    }

    @Override
    public Slice getCSlice() {
        return null;
    }

    @Override
    public Slice getDSlice() {
        return dSlice;
    }

    @Override
    public BoolVar getState() {
        return state;
    }

    @Override
    public VM getVM() {
        return vm;
    }

    @Override
    public VMState getSourceState() {
        return VMState.READY;
    }

    @Override
    public VMState getFutureState() {
        return VMState.RUNNING;
    }

    /**
     * The builder devoted to a ready->running transition.
     */
    public static class Builder extends VMTransitionBuilder {

        /**
         * New builder
         */
        public Builder() {
            super("boot", EnumSet.of(VMState.READY), VMState.RUNNING);
        }

        @Override
        public VMTransition build(ReconfigurationProblem r, VM v) throws SchedulerException {
            return new BootVM(r, v);
        }
    }
}

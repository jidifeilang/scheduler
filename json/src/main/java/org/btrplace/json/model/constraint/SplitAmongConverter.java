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

package org.btrplace.json.model.constraint;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.btrplace.json.JSONConverterException;
import org.btrplace.model.Model;
import org.btrplace.model.Node;
import org.btrplace.model.VM;
import org.btrplace.model.constraint.SplitAmong;

import java.util.Collection;

import static org.btrplace.json.JSONs.*;

/**
 * JSON converter for the {@link SplitAmong} constraint.
 *
 * @author Fabien Hermenier
 */
public class SplitAmongConverter implements ConstraintConverter<SplitAmong> {

    @Override
    public Class<SplitAmong> getSupportedConstraint() {
        return SplitAmong.class;
    }

    @Override
    public String getJSONId() {
        return "splitAmong";
    }

    @Override
    public SplitAmong fromJSON(Model mo, JSONObject o) throws JSONConverterException {
        checkId(o);
        return new SplitAmong(requiredVMPart(mo, o, "vParts"), requiredNodePart(mo, o, "pParts"), requiredBoolean(o, "continuous"));
    }

    @Override
    public JSONObject toJSON(SplitAmong o) {
        JSONObject c = new JSONObject();
        c.put("id", getJSONId());

        JSONArray vGroups = new JSONArray();
        for (Collection<VM> grp : o.getGroupsOfVMs()) {
            vGroups.add(vmsToJSON(grp));
        }

        JSONArray pGroups = new JSONArray();
        for (Collection<Node> grp : o.getGroupsOfNodes()) {
            pGroups.add(nodesToJSON(grp));
        }

        c.put("vParts", vGroups);
        c.put("pParts", pGroups);
        c.put("continuous", o.isContinuous());
        return c;
    }
}

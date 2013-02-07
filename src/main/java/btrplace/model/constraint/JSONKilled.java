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

package btrplace.model.constraint;

import btrplace.Utils;
import btrplace.model.JSONSatConstraintConverter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * JSON Converter for the constraint {@link btrplace.model.constraint.Killed}.
 *
 * @author Fabien Hermenier
 */
public class JSONKilled implements JSONSatConstraintConverter<Killed> {


    @Override
    public Class<Killed> getSupportedConstraint() {
        return Killed.class;
    }

    @Override
    public String getJSONId() {
        return "killed";
    }


    @Override
    public Killed fromJSON(JSONObject o) {
        String id = o.get("id").toString();
        if (!id.equals(getJSONId())) {
            return null;
        }
        return new Killed(Utils.fromJSON((JSONArray) o.get("vms")));
    }

    @Override
    public JSONObject toJSON(Killed o) {
        JSONObject c = new JSONObject();
        c.put("id", getJSONId());
        c.put("vms", Utils.toJSON(o.getInvolvedNodes()));
        return c;
    }
}

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

package org.btrplace.json.model.constraint.migration;

import org.btrplace.json.JSONConverterException;
import org.btrplace.json.model.constraint.ConstraintsConverter;
import org.btrplace.model.DefaultModel;
import org.btrplace.model.Model;
import org.btrplace.model.constraint.migration.Serialize;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Unit tests for {@link org.btrplace.json.model.constraint.migration.SerializeConverter}.
 *
 * @author Vincent Kherbache
 * @see org.btrplace.json.model.constraint.migration.SerializeConverter
 */
public class SerializeConverterTest {

    @Test
    public void testViables() throws JSONConverterException {
        Model mo = new DefaultModel();
        ConstraintsConverter conv = new ConstraintsConverter();
        conv.register(new SerializeConverter());


        Serialize serial = new Serialize(mo.newVM(), mo.newVM());
        Assert.assertEquals(conv.fromJSON(mo, conv.toJSON(serial)).toString(), serial.toString());
        System.out.println(conv.toJSON(serial));
    }

    @Test
    public void testBundle() {
        Assert.assertTrue(ConstraintsConverter.newBundle().getSupportedJavaConstraints().contains(Serialize.class));
        Assert.assertTrue(ConstraintsConverter.newBundle().getSupportedJSONConstraints().contains(new SerializeConverter().getJSONId()));
    }

}

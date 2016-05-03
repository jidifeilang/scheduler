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

package org.btrplace.json.model.view;

import net.minidev.json.JSONObject;
import org.btrplace.json.JSONConverterException;
import org.btrplace.model.Model;
import org.btrplace.model.view.ModelView;

/**
 * Specify a JSON converter for a {@link org.btrplace.model.view.ModelView}.
 *
 * @author Fabien Hermenier
 */
public abstract class ModelViewConverter<E extends ModelView> {

    /**
     * Get the className of the view that is supported by the converter.
     *
     * @return The view class
     */
    public abstract Class<E> getSupportedView();

    /**
     * Get the JSON identifier for the view.
     *
     * @return a non-empty string
     */
    public abstract String getJSONId();

    public abstract E fromJSON(Model mo, JSONObject o) throws JSONConverterException;

    public abstract JSONObject toJSON(E o) throws JSONConverterException;

}

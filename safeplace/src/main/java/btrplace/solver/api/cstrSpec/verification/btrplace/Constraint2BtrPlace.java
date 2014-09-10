package btrplace.solver.api.cstrSpec.verification.btrplace;

import btrplace.model.constraint.SatConstraint;
import btrplace.solver.api.cstrSpec.Constraint;
import btrplace.solver.api.cstrSpec.spec.term.Constant;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Fabien Hermenier
 */
public class Constraint2BtrPlace {

    public static SatConstraint build(Constraint cstr, List<Constant> params) throws ClassNotFoundException {
        return build("btrplace.model.constraint", cstr, params);
    }

    public static SatConstraint build(String pkg, Constraint cstr, List<Constant> params) throws ClassNotFoundException {
        String clName = cstr.id().substring(0, 1).toUpperCase() + cstr.id().substring(1);
        Class<SatConstraint> cl = (Class<SatConstraint>) Class.forName(pkg + "." + clName);
        List<Object> values = new ArrayList<>(params.size());
        for (Constant c : params) {
            values.add(c.eval(null));
        }
        for (Constructor c : cl.getConstructors()) {
            if (c.getParameterTypes().length == values.size()) {
                try {
                    return (SatConstraint) c.newInstance(values.toArray());
                } catch (Exception e) {
                    //We want ot try other constructors that may match
                }
            }
        }

        throw new IllegalArgumentException("No constructors compatible with values '" + values + "'");
    }
}
/*
 * Copyright (c) 2013 University of Nice Sophia-Antipolis
 *
 * This file is part of btrplace.
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

package btrplace.solver.choco.chocoUtil;


import org.testng.Assert;
import org.testng.annotations.Test;
import solver.Solver;
import solver.variables.IntVar;
import solver.variables.VF;

/**
 * @author Fabien Hermenier
 */
public class RoundedUpDivisionTest {

    @Test
    public void test1() {
        Solver s = new Solver();
        IntVar a = VF.bounded("a", 0, 5, s);
        IntVar b = VF.bounded("b", 0, 5, s);
        double q = 1;
        s.post(new RoundedUpDivision(a, b, q));
        Assert.assertEquals(6, s.findAllSolutions());
        //Assert.assertEquals(s.getNbSolutions(), 6);
    }

    @Test
    public void test2() {
        Solver s = new Solver();
        IntVar a = VF.bounded("a", 0, 32, s);
        IntVar b = VF.bounded("b", 0, 48, s);
        double q = 1.5;
        s.post(new RoundedUpDivision(a, b, q));
        Assert.assertEquals(33, s.findAllSolutions());
        //Assert.assertEquals(s.getNbSolutions(), 33);
    }
    /*
    private static void pretty(int a, int b, double q) {
        StringBuilder a1 = new StringBuilder();
        for (int i = 0; i < b; i++) {
            a1.append("(").append(i).append(",").append((int)Math.ceil(i / q)).append(") ");
        }
        System.err.println(a1.toString());
    } */
}

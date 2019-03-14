/*
 * HE_Mesh  Frederik Vanhoutte - www.wblut.com
 * 
 * https://github.com/wblut/HE_Mesh
 * A Processing/Java library for for creating and manipulating polygonal meshes.
 * 
 * Public Domain: http://creativecommons.org/publicdomain/zero/1.0/
 */

package wblut.math;

import java.security.InvalidParameterException;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import wblut.geom.WB_Coord;
import wblut.geom.WB_MutableCoordinate;

/**
 * The Class WB_ConstantParameter.
 *
 * @author Frederik Vanhoutte, W:Blut
 *
 *
 */
public class WB_ExpressionVectorParameter implements WB_VectorParameter {
	Expression expressionX;
	Expression expressionY;
	Expression expressionZ;
	String[] variables;

	/**
	 * 
	 *
	 * @param equationX
	 * @param equationY
	 * @param equationZ
	 * @param vars
	 */
	public WB_ExpressionVectorParameter(final String equationX, final String equationY, final String equationZ,
			final String... vars) {
		ExpressionBuilder expressionBuilder = new ExpressionBuilder(equationX);
		variables = new String[vars.length];
		for (int i = 0; i < vars.length; i++) {
			expressionBuilder.variable(vars[i]);
			variables[i] = vars[i];
		}
		try {
			expressionX = expressionBuilder.build();
		} catch (final Exception e) {
			throw new InvalidParameterException("Can't parse equation for X. Please check equation and parameters.");
		}
		expressionBuilder = new ExpressionBuilder(equationY);
		for (int i = 0; i < vars.length; i++) {
			expressionBuilder.variable(vars[i]);
		}
		try {
			expressionY = expressionBuilder.build();
		} catch (final Exception e) {
			throw new InvalidParameterException("Can't parse equation for Y. Please check equation and parameters.");
		}
		expressionBuilder = new ExpressionBuilder(equationZ);
		for (int i = 0; i < vars.length; i++) {
			expressionBuilder.variable(vars[i]);
		}
		try {
			expressionZ = expressionBuilder.build();
		} catch (final Exception e) {
			throw new InvalidParameterException("Can't parse equation for Z. Please check equation and parameters.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.math.WB_VectorParameter#evaluate(double[])
	 */
	@Override
	public WB_Coord evaluate(final double... value) {
		for (int i = 0; i < variables.length; i++) {
			expressionX.setVariable(variables[i], value[i]);
			expressionY.setVariable(variables[i], value[i]);
			expressionZ.setVariable(variables[i], value[i]);
		}
		final double x, y, z;
		try {
			x = expressionX.evaluate();
			y = expressionY.evaluate();
			z = expressionZ.evaluate();
		} catch (final ArithmeticException name) {
			return null;
		}
		return new WB_MutableCoordinate(x, y, z);
	}
}

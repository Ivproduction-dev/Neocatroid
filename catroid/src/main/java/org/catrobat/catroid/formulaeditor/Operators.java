/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.formulaeditor;

import org.catrobat.catroid.utils.EnumUtils;

public enum Operators {
	LOGICAL_OR(1, true), LOGICAL_AND(2, true), BITWISE_OR(3), BITWISE_XOR(4), BITWISE_NOR(3), BITWISE_XNOR(4),
	BITWISE_AND(5), BITWISE_NAND(5), EQUAL(6, true), NOT_EQUAL(6, true), SMALLER_OR_EQUAL(7, true),
	GREATER_OR_EQUAL(7, true), SMALLER_THAN(7, true), GREATER_THAN(7, true), SHIFT_LEFT(8), SHIFT_RIGHT(8),
	UNSIGNED_SHIFT_RIGHT(8), PLUS(9), MINUS(9), MULT(10), DIVIDE(10), MOD(10), POW(11),
	LOGICAL_NOT(7, true), BITWISE_NOT(12), CONCAT(9);

	private static final String TAG = Operators.class.getSimpleName();

	private final int priority;
	public final boolean isLogicalOperator;

	Operators(int priority) {
		this.priority = priority;
		isLogicalOperator = false;
	}

	Operators(int priority, boolean isLogical) {
		this.priority = priority;
		isLogicalOperator = isLogical;
	}

	public int compareOperatorTo(Operators operator) {
		return Integer.compare(priority, operator.priority);
	}

	public static Operators getOperatorByValue(String value) {
		return EnumUtils.getEnum(Operators.class, value);
	}

	public static boolean isOperator(String value) {
		return EnumUtils.isValidEnum(Operators.class, value);
	}
}

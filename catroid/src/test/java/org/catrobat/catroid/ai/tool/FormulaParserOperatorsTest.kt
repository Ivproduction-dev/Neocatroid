package org.catrobat.catroid.ai.tool

import org.catrobat.catroid.formulaeditor.Operators
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class FormulaParserOperatorsTest {

    private fun rootOperator(expression: String): String? =
        FormulaParser.parse(expression).formulaTree.value

    @Test
    fun comparisons_map_to_operator_enum_names() {
        assertEquals(Operators.EQUAL.name, rootOperator("1 = 1"))
        assertEquals(Operators.NOT_EQUAL.name, rootOperator("1 != 2"))
        assertEquals(Operators.SMALLER_THAN.name, rootOperator("1 < 2"))
        assertEquals(Operators.SMALLER_OR_EQUAL.name, rootOperator("1 <= 2"))
        assertEquals(Operators.GREATER_THAN.name, rootOperator("1 > 2"))
        assertEquals(Operators.GREATER_OR_EQUAL.name, rootOperator("1 >= 2"))
    }

    @Test
    fun not_equal_alias_is_normalized() {
        assertEquals(Operators.NOT_EQUAL.name, rootOperator("1 <> 2"))
    }

    @Test
    fun arithmetic_operators_still_map_to_enum_names() {
        assertEquals(Operators.PLUS.name, rootOperator("1 + 2"))
        assertEquals(Operators.MINUS.name, rootOperator("1 - 2"))
        assertEquals(Operators.MULT.name, rootOperator("3 * 4"))
        assertEquals(Operators.DIVIDE.name, rootOperator("3 / 4"))
    }

    @Test
    fun every_parsed_operator_is_a_known_enum_value() {
        for (expression in listOf(
            "1 = 1", "1 != 2", "1 <> 2", "1 < 2", "1 <= 2", "1 > 2", "1 >= 2",
            "1 + 2", "1 - 2", "3 * 4", "3 / 4", "5 mod 2", "2 ^ 3",
            "1 < 2 and 3 > 2", "1 < 2 or 3 > 2", "not 1 = 2"
        )) {
            assertNotNull(
                "operator of '$expression' is not a valid Operators value: ${rootOperator(expression)}",
                Operators.getOperatorByValue(rootOperator(expression))
            )
        }
    }
}

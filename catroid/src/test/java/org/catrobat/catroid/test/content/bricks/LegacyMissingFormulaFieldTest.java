/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://www.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.test.content.bricks;

import org.catrobat.catroid.content.Scope;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.SendToTcpServerBrick;
import org.catrobat.catroid.formulaeditor.Formula;
import org.catrobat.catroid.formulaeditor.InterpretationException;
import org.catrobat.catroid.io.XstreamSerializer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(MockitoJUnitRunner.class)
public class LegacyMissingFormulaFieldTest {

	private static final String TEXT_FORMULA_NODE = "(?s)<formula category=\"TEXT\">.*?</formula>";

	@Test
	public void testMissingTextFieldIsRestoredWithDefaultValue() throws InterpretationException {
		XstreamSerializer serializer = XstreamSerializer.getInstance();
		SendToTcpServerBrick brick = new SendToTcpServerBrick("server", "payload");

		String brickXml = serializer.getXstream().toXML(brick);
		assertTrue(brickXml.contains("category=\"TEXT\""));

		SendToTcpServerBrick restored = loadLegacyBrick(serializer, brickXml.replaceAll(TEXT_FORMULA_NODE, ""));
		Scope scope = new Scope(null, new Sprite("test"), null);

		Formula serverName = restored.getFormulaWithBrickField(Brick.BrickField.TEXT);
		assertEquals("", serverName.getFormulaTree().getValue());
		assertEquals("", serverName.interpretString(scope));
		assertEquals("payload", restored.getFormulaWithBrickField(Brick.BrickField.VALUE).interpretString(scope));
	}

	@Test
	public void testRestoredFieldIsStoredInFormulaMap() {
		XstreamSerializer serializer = XstreamSerializer.getInstance();
		String brickXml = serializer.getXstream().toXML(new SendToTcpServerBrick("server", "payload"));

		SendToTcpServerBrick restored = loadLegacyBrick(serializer, brickXml.replaceAll(TEXT_FORMULA_NODE, ""));
		restored.getFormulaWithBrickField(Brick.BrickField.TEXT);

		assertTrue(restored.getFormulaMap().containsKey(Brick.BrickField.TEXT));
		assertTrue(serializer.getXstream().toXML(restored).contains("category=\"TEXT\""));
	}

	@Test
	public void testUnknownBrickFieldStillThrows() {
		SendToTcpServerBrick brick = new SendToTcpServerBrick("server", "payload");

		try {
			brick.getFormulaWithBrickField(Brick.BrickField.X_POSITION);
			fail("Expected IllegalArgumentException for a brick field the brick does not have");
		} catch (IllegalArgumentException expected) {
			assertTrue(expected.getMessage().contains("Incompatible Brick Field"));
		}
	}

	private SendToTcpServerBrick loadLegacyBrick(XstreamSerializer serializer, String legacyBrickXml) {
		String spriteXml = "<org.catrobat.catroid.content.Sprite>"
				+ "  <scriptList>"
				+ "    <org.catrobat.catroid.content.StartScript>"
				+ "      <brickList>"
				+ legacyBrickXml
				+ "      </brickList>"
				+ "    </org.catrobat.catroid.content.StartScript>"
				+ "  </scriptList>"
				+ "  <lookList/>"
				+ "  <soundList/>"
				+ "</org.catrobat.catroid.content.Sprite>";

		Sprite sprite = (Sprite) serializer.getXstream().fromXML(spriteXml);
		return (SendToTcpServerBrick) sprite.getScriptList().get(0).getBrickList().get(0);
	}
}

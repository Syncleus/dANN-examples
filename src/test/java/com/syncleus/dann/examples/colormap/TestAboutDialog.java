/******************************************************************************
 *                                                                             *
 *  Copyright: (c) Syncleus, Inc.                                              *
 *                                                                             *
 *  You may redistribute and modify this source code under the terms and       *
 *  conditions of the Open Source Community License - Type C version 1.0       *
 *  or any later version as published by Syncleus, Inc. at www.syncleus.com.   *
 *  There should be a copy of the license included with this file. If a copy   *
 *  of the license is not included you are granted no right to distribute or   *
 *  otherwise use this file except through a legal and valid license. You      *
 *  should also contact Syncleus, Inc. at the information below if you cannot  *
 *  find a license:                                                            *
 *                                                                             *
 *  Syncleus, Inc.                                                             *
 *  2604 South 12th Street                                                     *
 *  Philadelphia, PA 19148                                                     *
 *                                                                             *
 ******************************************************************************/
package com.syncleus.dann.examples.colormap;

import org.fest.swing.edt.*;
import org.fest.swing.fixture.DialogFixture;
import org.junit.*;

import java.awt.*;

public class TestAboutDialog {
    private DialogFixture aboutFixture;

    @BeforeClass
    public static void setUpOnce() {
        Assume.assumeTrue(!GraphicsEnvironment.getLocalGraphicsEnvironment().isHeadlessInstance());

        FailOnThreadViolationRepaintManager.install();
    }


    @Before
    public void onSetUp() {
        Assume.assumeTrue(!GraphicsEnvironment.getLocalGraphicsEnvironment().isHeadlessInstance());

        AboutDialog aboutDialog = GuiActionRunner.execute(new GuiQuery<AboutDialog>() {
            @Override
            protected AboutDialog executeInEDT() {
                return new AboutDialog(null, false);
            }
        });

        this.aboutFixture = new DialogFixture(aboutDialog);
        this.aboutFixture.show();
    }

    @After
    public void tearDown() {
        Assume.assumeTrue(!GraphicsEnvironment.getLocalGraphicsEnvironment().isHeadlessInstance());

        this.aboutFixture.cleanUp();
    }

    @Test
    public void testDisplays() {
        Assume.assumeTrue(!GraphicsEnvironment.getLocalGraphicsEnvironment().isHeadlessInstance());

        this.aboutFixture.requireVisible();
        this.aboutFixture.button("ok button").click();
        this.aboutFixture.requireNotVisible();
    }
}

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
import org.fest.swing.exception.UnexpectedException;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.timing.Timeout;
import org.junit.*;

public class TestColorMapDemo {
    private FrameFixture colorMapDemoFixture;

    @BeforeClass
    public static void setUpOnce() {
        FailOnThreadViolationRepaintManager.install();
    }


    @Before
    public void onSetUp() {
        ColorMapDemo colorMapDemo = GuiActionRunner.execute(new GuiQuery<ColorMapDemo>() {
            @Override
            protected ColorMapDemo executeInEDT() {
                return new ColorMapDemo();
            }
        });

        this.colorMapDemoFixture = new FrameFixture(colorMapDemo);
        this.colorMapDemoFixture.show();
    }

    @After
    public void tearDown() {
        this.colorMapDemoFixture.cleanUp();
    }

    @Test
    public void testComponents() {
        this.colorMapDemoFixture.requireVisible();

        //test the spinner
        //spinners should take values of arbitrary granularity
        this.colorMapDemoFixture.spinner("iterationsSpinner").enterTextAndCommit("257");
        this.colorMapDemoFixture.spinner("iterationsSpinner").requireValue(257);
        this.colorMapDemoFixture.spinner("learningRateSpinner").enterTextAndCommit("0.16492");
        this.colorMapDemoFixture.spinner("learningRateSpinner").requireValue(0.16492);
        //lets try incrementing
        this.colorMapDemoFixture.spinner("iterationsSpinner").enterTextAndCommit("100");
        this.colorMapDemoFixture.spinner("iterationsSpinner").requireValue(100);
        this.colorMapDemoFixture.spinner("iterationsSpinner").increment(9);
        this.colorMapDemoFixture.spinner("iterationsSpinner").requireValue(1000);
        this.colorMapDemoFixture.spinner("iterationsSpinner").increment(100);
        this.colorMapDemoFixture.spinner("iterationsSpinner").requireValue(10000);
        this.colorMapDemoFixture.spinner("learningRateSpinner").enterTextAndCommit("0.01");
        this.colorMapDemoFixture.spinner("learningRateSpinner").increment(9);
        double currentValue = Double.valueOf(this.colorMapDemoFixture.spinner("learningRateSpinner").text());
        Assert.assertTrue("learning rate spinner did not increment properly", (currentValue - 0.1) < 0.00001);
        this.colorMapDemoFixture.spinner("learningRateSpinner").increment(100);
        currentValue = Double.valueOf(this.colorMapDemoFixture.spinner("learningRateSpinner").text());
        Assert.assertTrue("learning rate spinner did not increment properly", (currentValue - 1.0) < 0.001);
        //lets try decrementing
        this.colorMapDemoFixture.spinner("iterationsSpinner").enterTextAndCommit("10000");
        this.colorMapDemoFixture.spinner("iterationsSpinner").requireValue(10000);
        this.colorMapDemoFixture.spinner("iterationsSpinner").decrement(10);
        this.colorMapDemoFixture.spinner("iterationsSpinner").requireValue(9000);
        this.colorMapDemoFixture.spinner("iterationsSpinner").decrement(100);
        this.colorMapDemoFixture.spinner("iterationsSpinner").requireValue(100);
        this.colorMapDemoFixture.spinner("learningRateSpinner").enterTextAndCommit("1.0");
        this.colorMapDemoFixture.spinner("learningRateSpinner").decrement(10);
        currentValue = Double.valueOf(this.colorMapDemoFixture.spinner("learningRateSpinner").text());
        Assert.assertTrue("learning rate spinner did not increment properly", (currentValue - 0.9) < 0.00001);
        this.colorMapDemoFixture.spinner("learningRateSpinner").decrement(100);
        currentValue = Double.valueOf(this.colorMapDemoFixture.spinner("learningRateSpinner").text());
        Assert.assertTrue("learning rate spinner did not increment properly", (currentValue - 0.01) < 0.00001);
    }

    @Test(expected = UnexpectedException.class)
    public void testIterationsMinimum() {
        this.colorMapDemoFixture.spinner("iterationsSpinner").enterTextAndCommit("1000");
        this.colorMapDemoFixture.spinner("iterationsSpinner").requireValue(1000);
        this.colorMapDemoFixture.spinner("iterationsSpinner").enterTextAndCommit("0");
    }

    @Test(expected = UnexpectedException.class)
    public void testIterationsMaximum() {
        this.colorMapDemoFixture.spinner("iterationsSpinner").enterTextAndCommit("1000");
        this.colorMapDemoFixture.spinner("iterationsSpinner").requireValue(1000);
        this.colorMapDemoFixture.spinner("iterationsSpinner").enterTextAndCommit("100000");
    }

    @Test(expected = UnexpectedException.class)
    public void testLearningRateMinimum() {
        this.colorMapDemoFixture.spinner("learningRateSpinner").enterTextAndCommit("0.5");
        this.colorMapDemoFixture.spinner("learningRateSpinner").requireValue(0.5);
        this.colorMapDemoFixture.spinner("learningRateSpinner").enterTextAndCommit("0");
    }

    @Test(expected = UnexpectedException.class)
    public void testLearningRateMaximum() {
        this.colorMapDemoFixture.spinner("learningRateSpinner").enterTextAndCommit("0.5");
        this.colorMapDemoFixture.spinner("learningRateSpinner").requireValue(0.5);
        this.colorMapDemoFixture.spinner("learningRateSpinner").enterTextAndCommit("1.001");
    }

    @Test
    public void testTrainingDisplay() {
        this.colorMapDemoFixture.requireVisible();

        //train and display for various parameters
        this.colorMapDemoFixture.button("trainDisplayButton").requireEnabled();

        this.colorMapDemoFixture.spinner("iterationsSpinner").enterTextAndCommit("10000");
        this.colorMapDemoFixture.button("trainDisplayButton").click();
        this.colorMapDemoFixture.button("trainDisplayButton").requireDisabled();
        this.colorMapDemoFixture.button("trainDisplayButton").requireEnabled(Timeout.timeout(30000));

        this.colorMapDemoFixture.spinner("iterationsSpinner").enterTextAndCommit("5000");
        this.colorMapDemoFixture.spinner("iterationsSpinner").requireValue(5000);
        this.colorMapDemoFixture.button("trainDisplayButton").click();
        this.colorMapDemoFixture.button("trainDisplayButton").requireDisabled();
        this.colorMapDemoFixture.button("trainDisplayButton").requireEnabled(Timeout.timeout(30000));

        this.colorMapDemoFixture.spinner("iterationsSpinner").enterTextAndCommit("100");
        this.colorMapDemoFixture.spinner("iterationsSpinner").requireValue(100);
        this.colorMapDemoFixture.spinner("iterationsSpinner").enterTextAndCommit("5000");
        this.colorMapDemoFixture.spinner("learningRateSpinner").enterTextAndCommit("0.1");
        this.colorMapDemoFixture.spinner("learningRateSpinner").requireValue(0.1);
        this.colorMapDemoFixture.comboBox("dimentionalityComboBox").selectItem("2D");
        this.colorMapDemoFixture.comboBox("dimentionalityComboBox").requireSelection(1);
        this.colorMapDemoFixture.button("trainDisplayButton").click();
        this.colorMapDemoFixture.button("trainDisplayButton").requireDisabled();
        this.colorMapDemoFixture.button("trainDisplayButton").requireEnabled(Timeout.timeout(30000));

        this.colorMapDemoFixture.spinner("iterationsSpinner").enterTextAndCommit("250");
        this.colorMapDemoFixture.spinner("iterationsSpinner").requireValue(250);
        this.colorMapDemoFixture.spinner("iterationsSpinner").enterTextAndCommit("5000");
        this.colorMapDemoFixture.spinner("learningRateSpinner").enterTextAndCommit("1.0");
        this.colorMapDemoFixture.spinner("learningRateSpinner").requireValue(1.0);
        this.colorMapDemoFixture.comboBox("dimentionalityComboBox").selectItem("1D");
        this.colorMapDemoFixture.comboBox("dimentionalityComboBox").requireSelection(0);
        this.colorMapDemoFixture.button("trainDisplayButton").click();
        this.colorMapDemoFixture.button("trainDisplayButton").requireDisabled();
        this.colorMapDemoFixture.button("trainDisplayButton").requireEnabled(Timeout.timeout(30000));
    }
}

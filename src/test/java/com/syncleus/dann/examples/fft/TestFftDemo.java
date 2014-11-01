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
package com.syncleus.dann.examples.fft;

import org.fest.swing.edt.*;
import org.fest.swing.fixture.FrameFixture;
import org.fest.swing.timing.Timeout;
import org.junit.*;

import java.awt.*;
import java.io.IOException;

public class TestFftDemo {
    private FrameFixture fftDemoFixture;

    @BeforeClass
    public static void setUpOnce() {
        Assume.assumeTrue(!GraphicsEnvironment.getLocalGraphicsEnvironment().isHeadlessInstance());

        FailOnThreadViolationRepaintManager.install();
    }


    @Before
    public void onSetUp() {
        Assume.assumeTrue(!GraphicsEnvironment.getLocalGraphicsEnvironment().isHeadlessInstance());

        FftDemo fftDemo = GuiActionRunner.execute(new GuiQuery<FftDemo>() {
            @Override
            protected FftDemo executeInEDT() {
                return new FftDemo();
            }
        });

        this.fftDemoFixture = new FrameFixture(fftDemo);
        this.fftDemoFixture.show();
    }

    @After
    public void tearDown() {
        Assume.assumeTrue(!GraphicsEnvironment.getLocalGraphicsEnvironment().isHeadlessInstance());

        this.fftDemoFixture.cleanUp();
    }

    @Test
    public void testComponents() {
        Assume.assumeTrue(!GraphicsEnvironment.getLocalGraphicsEnvironment().isHeadlessInstance());

        this.fftDemoFixture.requireVisible();

        this.fftDemoFixture.button("listenButton").requireText("Listen");
        this.fftDemoFixture.button("listenButton").requireEnabled(Timeout.timeout(30000));
        this.fftDemoFixture.requireEnabled(Timeout.timeout(30000));


        try {
            Thread.sleep(500);
        }
        catch(final InterruptedException caughtException) {
            throw new IllegalStateException("Sleep unexpectidly interrupted", caughtException);
        }


        //start listening
        this.fftDemoFixture.button("listenButton").click();

        //check that its listening
        this.fftDemoFixture.button("listenButton").requireText("Stop");

        //stop listening
        this.fftDemoFixture.button("listenButton").click();

        //check if stopped
        this.fftDemoFixture.button("listenButton").requireText("Listen");
    }
}

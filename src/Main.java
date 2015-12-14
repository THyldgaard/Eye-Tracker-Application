import com.theeyetribe.client.GazeManager;
import com.theeyetribe.client.IGazeListener;
import com.theeyetribe.client.data.GazeData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;

public class Main  {

    private static Robot robot;

    public static void main(String[] args) {
        System.out.println("Hello World!");
        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }
        gazeMangement();

    }

    public static void gazeMangement() {
        final GazeManager gm = GazeManager.getInstance();
        boolean success = gm.activate(GazeManager.ApiVersion.VERSION_1_0, GazeManager.ClientMode.PUSH);

        final GazeListener gazeListener = new GazeListener();
        gm.addGazeListener(gazeListener);

        //TODO: Do awesome gaze control wizardry

        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                gm.removeGazeListener(gazeListener);
                gm.deactivate();
            }
        });
    }

    private static class GazeListener implements IGazeListener {
        @Override
        public void onGazeUpdate(GazeData gazeData)
        {

            System.out.println("Left Eye: "+gazeData.leftEye.pupilSize + " Right Eye: " + gazeData.rightEye.pupilSize);
            System.out.println("Left Eye: " + gazeData.leftEye.pupilCenterCoordinates);
            System.out.println("Right Eye: " + gazeData.rightEye.pupilCenterCoordinates);
            System.out.println();
            robot.mouseMove((int) gazeData.smoothedCoordinates.x, (int) gazeData.smoothedCoordinates.y);

            if (leftEyeBlink(gazeData.leftEye.pupilSize) && !rightEyeBlink(gazeData.rightEye.pupilSize)) {
                robot.mousePress(InputEvent.BUTTON1_MASK);
                robot.delay(500); // second
                robot.mouseRelease(InputEvent.BUTTON1_MASK);
            }

            if (leftEyeBlink(gazeData.leftEye.pupilSize) && rightEyeBlink(gazeData.rightEye.pupilSize)) {
                Timer timer = new Timer(500, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.exit(0);

                    }
                });
                

                timer.setRepeats(false);
                timer.start();
            }



        }
    }

    public static boolean rightEyeBlink(double rightEyePubilSize) {
        return rightEyePubilSize == 0;
    }

    public static boolean leftEyeBlink(double leftEyePubilSize) {
        return leftEyePubilSize == 0;
    }


}

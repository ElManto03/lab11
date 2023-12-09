package it.unibo.oop.reactivegui03;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Third experiment with reactive gui.
 */
@SuppressWarnings("PMD.AvoidPrintStackTrace")
public final class AnotherConcurrentGUI extends JFrame {

    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private final JLabel display = new JLabel();
    private final JButton stop = new JButton("stop");
    private final JButton up = new JButton("up");
    private final JButton down = new JButton("down");
    private Counter counter = new Counter();

    public AnotherConcurrentGUI() {
        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final JPanel panel = new JPanel();
        panel.add(display);
        panel.add(up);
        panel.add(down);
        panel.add(stop);
        this.getContentPane().add(panel);
        this.setVisible(true);
        new Thread(counter).start();
        new Thread(new Stopper()).start();
        /*
         *  Listeners
         */
        up.addActionListener(e -> counter.goUp());
        down.addActionListener(e -> counter.goDown());
        stop.addActionListener(e -> {
            counter.stopCounting();
            disableButtons();
        });

    }

    private class Counter implements Runnable {

        private volatile boolean stop;
        private volatile boolean isDecreasing;
        private int counter;

        @Override
        public void run() {
            while (!this.stop) {
                try {
                    // The EDT doesn't access `counter` anymore, it doesn't need to be volatile
                    final var nextText = Integer.toString(this.counter);
                    SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.display.setText(nextText));
                    this.counter += isDecreasing() ? -1 : 1;
                    Thread.sleep(100);
                } catch (InvocationTargetException | InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }

        /**
         * External command to stop counting.
         */
        public void stopCounting() {
            this.stop = true;

        }

        public boolean isDecreasing() {
            return isDecreasing;
        }

        public void goUp() {
            this.isDecreasing = false;
        }

        public void goDown() {
            this.isDecreasing = true;
        }

    }

    private void disableButtons() {
        up.setEnabled(false);
        down.setEnabled(false);
        stop.setEnabled(false);
    }

    private class Stopper implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(10));
                AnotherConcurrentGUI.this.counter.stopCounting();
                AnotherConcurrentGUI.this.disableButtons();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
        
    } 

}

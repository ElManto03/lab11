package it.unibo.oop.reactivegui03;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.Serializable;
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

    private static final long serialVersionUID = 3L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private static final long WAITING_TIME = TimeUnit.SECONDS.toMillis(10);

    private final JLabel display = new JLabel();
    private final JButton stop = new JButton("stop");
    private final JButton up = new JButton("up");
    private final JButton down = new JButton("down");
    private final CounterAgent counter = new CounterAgent();

    /**
     * Builds a new CGUI.
     */
    @SuppressWarnings("CPD-START")
    public AnotherConcurrentGUI() {
        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC),
            (int) (screenSize.getHeight() * HEIGHT_PERC));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final JPanel panel = new JPanel();
        panel.add(display);
        panel.add(up);
        panel.add(down);
        panel.add(stop);
        this.getContentPane().add(panel);
        this.setVisible(true);
        /*
         * Launch threads
         */
        new Thread(counter).start();
        new Thread(new Stopper()).start();
        /*
         * Listeners
         */
        up.addActionListener(e -> counter.goUp());
        down.addActionListener(e -> counter.goDown());
        stop.addActionListener(e -> {
            counter.stopCounting();
            disableButtons();
        });
    }

    @SuppressWarnings("CPD-END")

    private final class CounterAgent implements Runnable, Serializable {

        private static final long serialVersionUID = 31L;
        private volatile boolean stop;
        private volatile boolean isDecreasing;
        private int counter;

        @Override
        public void run() {
            while (!this.stop) {
                try {
                    final var nextText = Integer.toString(this.counter);
                    SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.display.setText(nextText));
                    this.counter += isDecreasing() ? -1 : 1;
                    Thread.sleep(100);
                } catch (InvocationTargetException | InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }

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

    private final class Stopper implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(WAITING_TIME);
                AnotherConcurrentGUI.this.counter.stopCounting();
                AnotherConcurrentGUI.this.disableButtons();
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

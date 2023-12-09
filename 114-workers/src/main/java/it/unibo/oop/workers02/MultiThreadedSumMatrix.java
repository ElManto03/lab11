package it.unibo.oop.workers02;

import java.util.ArrayList;
import java.util.List;

public class MultiThreadedSumMatrix implements SumMatrix {

    private final int nthreads;

    public MultiThreadedSumMatrix(final int nthreads) {
        this.nthreads = nthreads;
    }

    private static class Worker extends Thread {
        private final double[][] matrix;
        private final int startArray;
        private final int narrays;
        private double res;

        public Worker(final double[][] matrix, final int startArray, final int narrays) {
            super();
            this.matrix = matrix;
            this.startArray = startArray;
            this.narrays = narrays;
        }

        @Override
        public void run() {
            log();
            for (int i = startArray; i < matrix.length && i < startArray + narrays - 1; i++) {
                for (final double n : this.matrix[i]) {
                    this.res += n;
                }
            }
        }

        public double getRes() {
            return this.res;
        }

        private void log() {
            System.out.println("Working from position " + startArray + " to position " + (startArray + narrays - 1));
        }

    }

    @Override
    public double sum(final double[][] matrix) {

        final int size = matrix.length / nthreads +
            matrix.length % nthreads == 0 ? 0 : 1;

        final List<Worker> workers = new ArrayList<>(nthreads);
        for (int start = 0; start < matrix.length; start += size) {
            workers.add(new Worker(matrix, start, size));
        }

        for (final Worker w: workers) {
            w.start();
        }

        double sum = 0;
        for (final Worker w: workers) {
            try {
                w.join();
                sum += w.getRes();
            } catch (final InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
        return sum;

    }

}

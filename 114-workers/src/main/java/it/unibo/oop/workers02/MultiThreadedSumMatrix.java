package it.unibo.oop.workers02;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a standard implementation of a matrix sum calculation.
 * 
 */
public final class MultiThreadedSumMatrix implements SumMatrix {

    private final int nthreads;

    /**
     * 
     * @param nthreads
     *                 no. of thread performing the sum.
     */
    MultiThreadedSumMatrix(final int nthreads) {
        this.nthreads = nthreads;
    }

    private static class Worker extends Thread {
        private final double[][] matrix;
        private final int startArray;
        private final int narrays;
        private double res;

        private Worker(final double[][] matrix, final int startArray, final int narrays) {
            super();
            this.matrix = matrix;
            this.startArray = startArray;
            this.narrays = narrays;
        }

        @Override
        public void run() {
            for (int i = startArray; i < matrix.length && i < startArray + narrays; i++) {
                for (final double n : this.matrix[i]) {
                    this.res += n;
                }
            }
        }

        public double getRes() {
            return this.res;
        }

    }

    @Override
    public double sum(final double[][] matrix) {

        final int size = matrix.length / nthreads + matrix.length % nthreads;
        final List<Worker> workers = new ArrayList<>(nthreads);
        for (int start = 0; start < matrix.length; start += size) {
            workers.add(new Worker(matrix, start, size));
        }

        for (final Worker w : workers) {
            w.start();
        }

        double sum = 0;
        for (final Worker w : workers) {
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

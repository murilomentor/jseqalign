package org.jseqalign.exceptions;

/**
 * Exceção lançada quando uma matriz de substituição for inválida.
 */
public class InvalidMatrixException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    public InvalidMatrixException() {
        super();
    }

    /**
     *
     * @param message
     */
    public InvalidMatrixException(String message) {
        super(message);
    }

    /**
     *
     * @param e
     */
    public InvalidMatrixException(Throwable e) {
        super(e);
    }

    /**
     *
     * @param message
     * @param e
     */
    public InvalidMatrixException(String message, Throwable e) {
        super(message, e);
    }
}

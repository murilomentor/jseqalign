package org.jseqalign.exceptions;

/**
 * Exceção lançada quando uma sequência for inválida.
 */
public class InvalidSequenceException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    public InvalidSequenceException() {
        super();
    }

    /**
     *
     * @param message
     */
    public InvalidSequenceException(String message) {
        super(message);
    }

    /**
     *
     * @param e
     */
    public InvalidSequenceException(Throwable e) {
        super(e);
    }

    /**
     *
     * @param message
     * @param e
     */
    public InvalidSequenceException(String message, Throwable e) {
        super(message, e);
    }
}

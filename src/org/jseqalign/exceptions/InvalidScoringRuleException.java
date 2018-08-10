package org.jseqalign.exceptions;

/**
 * Exceção lançada quando uma regra de pontuação for inválida.
 */
public class InvalidScoringRuleException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    public InvalidScoringRuleException() {
        super();
    }

    /**
     *
     * @param message
     */
    public InvalidScoringRuleException(String message) {
        super(message);
    }

    /**
     *
     * @param e
     */
    public InvalidScoringRuleException(Throwable e) {
        super(e);
    }

    /**
     *
     * @param message
     * @param e
     */
    public InvalidScoringRuleException(String message, Throwable e) {
        super(message, e);
    }
}

package org.jseqalign.structure;

import java.io.Serializable;

/**
 *
 * @author Murilo S. Farias
 */
public class SequenceAlignment implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Primeira sequência da comparação já com os gaps necessários para o
     * alinhamento.
     */
    protected String firstSequenceWithGaps;

    /**
     * Linha descritiva do resultado do alinhamento entre a sequência 1 e a
     * sequência 2.
     */
    protected String alignmentDescriptiveLine;

    /**
     * Segunda sequência da comparação já com os gaps necessários para o
     * alinhamento.
     */
    protected String secondSequenceWithGaps;

    /**
     * Pontuação apurada para o alinhamento entre as sequencias.
     */
    protected int score;

    /**
     * Constrói um objeto do tipo <CODE>AlinhamentoDeSequencias</CODE>
     * inicializando todos os atributos.
     *
     * @param firstSequenceWithGaps
     * @param secondSequenceWithGaps
     * @param alignmentDescriptiveLine
     * @param pontuacao
     */
    public SequenceAlignment(String firstSequenceWithGaps,
            String alignmentDescriptiveLine, String secondSequenceWithGaps,
            int pontuacao) {
        this.firstSequenceWithGaps = firstSequenceWithGaps;
        this.alignmentDescriptiveLine = alignmentDescriptiveLine;
        this.secondSequenceWithGaps = secondSequenceWithGaps;
        this.score = pontuacao;
    }

    /**
     * Retorna a sequência 1 com os gaps necessários para o alinhamento com a
     * sequência 2.
     *
     * @return
     */
    public String getFirstSequenceWithGaps() {
        return firstSequenceWithGaps;
    }

    /**
     * Retorna a linha descritiva do resultado do alinhamento entre as
     * sequências.
     *
     * @return
     */
    public String getAlignmentDescriptiveLine() {
        return alignmentDescriptiveLine;
    }

    /**
     * Retorna a sequência 2 com os gaps necessários para o alinhamento com a
     * sequência 1.
     *
     * @return
     */
    public String getSecondSequenceWithGaps() {
        return secondSequenceWithGaps;
    }

    /**
     * Retorna a pontuação apurada para o alinhamento entre as sequências.
     *
     * @return
     */
    public int getScore() {
        return score;
    }

    /**
     * Retorna uma representação textual do resultado do alinhamento.
     */
    @Override
    public String toString() {
        return firstSequenceWithGaps + "\n" + alignmentDescriptiveLine + "\n"
                + secondSequenceWithGaps + "\nComputed Score: " + score;
    }

    /**
     *
     * @return <CODE>true</CODE> caso a instância do alinhamento seja igual ao
     * objeto comparado.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SequenceAlignment)) {
            return false;
        }

        SequenceAlignment outroAlinhamento = (SequenceAlignment) obj;

        if (this.score != outroAlinhamento.score) {
            return false;
        }

        if (!this.firstSequenceWithGaps.equals(outroAlinhamento.firstSequenceWithGaps)) {
            return false;
        }

        if (!this.alignmentDescriptiveLine.equals(outroAlinhamento.alignmentDescriptiveLine)) {
            return false;
        }

        return this.secondSequenceWithGaps.equals(outroAlinhamento.secondSequenceWithGaps);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.firstSequenceWithGaps
                != null ? this.firstSequenceWithGaps.hashCode() : 0);
        hash = 53 * hash + (this.alignmentDescriptiveLine
                != null ? this.alignmentDescriptiveLine.hashCode() : 0);
        hash = 53 * hash + (this.secondSequenceWithGaps
                != null ? this.secondSequenceWithGaps.hashCode() : 0);
        hash = 53 * hash + this.score;
        return hash;
    }
}

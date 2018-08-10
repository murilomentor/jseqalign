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
     * First sequence of the alignment, with gaps needed for presentation.
     */
    protected String firstSequenceWithGaps;

    /**
     * The descriptive line of the alignment between the two sequences.
     */
    protected String alignmentDescriptiveLine;

    /**
     * Second sequence of the alignment, with gaps needed for presentation.
     */
    protected String secondSequenceWithGaps;

    /**
     * Computed score of the alignment between the sequences.
     */
    protected int score;

    /**
     * Builds an object of type <CODE>SequenceAlignment</CODE> initializing all
     * the fields.
     *
     * @param firstSequenceWithGaps
     * @param secondSequenceWithGaps
     * @param alignmentDescriptiveLine
     * @param score
     */
    public SequenceAlignment(String firstSequenceWithGaps,
            String alignmentDescriptiveLine, String secondSequenceWithGaps,
            int score) {
        this.firstSequenceWithGaps = firstSequenceWithGaps;
        this.alignmentDescriptiveLine = alignmentDescriptiveLine;
        this.secondSequenceWithGaps = secondSequenceWithGaps;
        this.score = score;
    }

    /**
     * Returns the first sequence already with gaps for alignment with the 
     * second sequence.
     *
     * @return
     */
    public String getFirstSequenceWithGaps() {
        return firstSequenceWithGaps;
    }

    /**
     * Returns the descriptive line of the alignment between the two sequences.
     *
     * @return
     */
    public String getAlignmentDescriptiveLine() {
        return alignmentDescriptiveLine;
    }

    /**
     * Returns the second sequence already with gaps for alignment with the 
     * second sequence.
     *
     * @return
     */
    public String getSecondSequenceWithGaps() {
        return secondSequenceWithGaps;
    }

    /**
     * Returns the computed score for the alignment.
     *
     * @return
     */
    public int getScore() {
        return score;
    }

    /**
     * Returns a string representing the sequence alignment result.
     * @return String representation of the sequence alignment.
     */
    @Override
    public String toString() {
        return firstSequenceWithGaps + "\n" + alignmentDescriptiveLine + "\n"
                + secondSequenceWithGaps + "\nComputed Score: " + score;
    }

    /**
     * 
     * @return <CODE>true</CODE> if the alignment instance is equal to the
     * compared object.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SequenceAlignment)) {
            return false;
        }

        SequenceAlignment otherAlignment = (SequenceAlignment) obj;

        if (this.score != otherAlignment.score) {
            return false;
        }

        if (!this.firstSequenceWithGaps.equals(otherAlignment.firstSequenceWithGaps)) {
            return false;
        }

        if (!this.alignmentDescriptiveLine.equals(otherAlignment.alignmentDescriptiveLine)) {
            return false;
        }

        return this.secondSequenceWithGaps.equals(otherAlignment.secondSequenceWithGaps);
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

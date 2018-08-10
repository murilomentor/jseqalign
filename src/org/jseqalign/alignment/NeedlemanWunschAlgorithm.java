package org.jseqalign.alignment;

import org.jseqalign.structure.SequenceAlignment;
import org.jseqalign.exceptions.InvalidScoringRuleException;
import org.jseqalign.exceptions.InvalidSequenceException;
import org.jseqalign.io.CharacterSequence;
import java.io.IOException;
import java.io.Reader;

/**
 * Implements the default algorithm for the global alignment of biological
 * sequences created by Needleman and Wunsch.
 *
 * <p>
 * The algorithm uses recursion as a technique of dynamic programming.
 * Considering 2 sequences A and B of sizes <i>n</i> and <i>m</i>, it creates a
 * matrix M of size (n+1, m+1) which contains the scores for the similarity of
 * the prefixes of A and B. Each position on the matrix M[i,j] have the score
 * for the alignment between the sequences A[1..i] and B[1..j], and the first
 * line and column represents the alignments with gaps.</p>
 *
 * <P>
 * Starting at M[0,0] the algorithm will compute each M[i,j] using the following
 * recurrence:</P>
 *
 * <CODE><BLOCKQUOTE><PRE>
 * M[0,0] = 0
 * M[i,j] = max {
 *              M[i,j-1]   + insertion(B[j]),
 *              M[i-1,j-1] + substitution(A[i], B[j]),
 *              M[i-1,j]   + deletion(A[i])
 *          }
 * </PRE></BLOCKQUOTE></CODE>
 *
 * <P>
 * At the end of the above recurrence, the intersection of the last line and the
 * last column will have the score for the similarity between the two
 * sequences.</P>
 * <p>
 * Quadratic complexity of space..: computationMatrix (n+1 x m+1) kept in memory.<br>
 * Quadratic complexity of time...: constant work for each cell</p>
 * <p>
 * {@link #computeScore computeScore}<br>
 * O(n) of space complexity..: keeps only the last line or column in memory.<br>
 * O(n<SUP>2</SUP>) of time complexity..: constant work for each cell.</p>
 *
 * @author Murilo S. Farias
 * @see SmithWatermanAlgorithm
 */
public class NeedlemanWunschAlgorithm extends SequenceAlignmentAlgorithm {

    /**
     * First sequence to be used in the alignment.
     */
    protected CharacterSequence firstSequence;
    /**
     * First sequence to be used in the alignment.
     */
    protected CharacterSequence secondSequence;
    /**
     * Dynamic programming matrix used by the algorithm. Each position M(i, j)
     * have the best score for the alignment of the subsequences A(1..i) and
     * B(1..j) in which i represents the current position on the
     * <CODE>firstSequence</CODE> and j represents the current position on the
     * <CODE>secondSequence</CODE>.
     */
    protected int[][] computationMatrix;

    /**
     * Loads the sequences on instances of the {@link CharacterSequence} class.
     *
     * @param firstFile The file that contains the first sequence
     * @param secondFile The file that contains the second sequence
     * @throws IOException If there is an error reading any of the sequence files
     * @throws InvalidSequenceException If at least one of the sequences is invalid
     * 
     * @see CharacterSequence
     */
    @Override
    protected void loadInternalSequences(Reader firstFile, Reader secondFile)
            throws IOException, InvalidSequenceException {
        // loads the files contents into objects of the type CharacterSequence
        this.firstSequence = new CharacterSequence(firstFile);
        this.secondSequence = new CharacterSequence(secondFile);
    }

    /**
     * Clean the references to the loaded matrix and sequences objects.
     */
    @Override
    protected void unloadInternalSequences() {
        this.firstSequence = null;
        this.secondSequence = null;
        this.computationMatrix = null;
    }

    /**
     * This method returns an optimal global alignment between the loaded
     * sequences after computing a dynamic programming matrix. The method makes
     * a call to <CODE>computeDynamicProgrammingMatrix</CODE> to create a
     * dynamic programming matrix, and then calls
     * <CODE>computeOptimalAlignment</CODE> to compute the alignment.
     *
     * @return An optimal global sequence alignment between the loaded sequences
     * @throws InvalidScoringRuleException If the defined scoring rule is not
     * compatible with the loaded sequences.
     *
     * @see #computeDynamicProgrammingMatrix
     * @see #computeOptimalAlignment
     * @see #SequenceAlignment
     */
    @Override
    protected SequenceAlignment computeSequenceAlignment()
            throws InvalidScoringRuleException {
        // creates and computes the dynamic programming matrix 
        computeDynamicProgrammingMatrix();

        // creates an object with the optimal global alignment between the sequences
        SequenceAlignment computedAlignment;
        computedAlignment = computeOptimalAlignment();

        // removes the object reference to the computationMatrix so the Garbage Collector can wipe it
        computationMatrix = null;

        return computedAlignment;
    }

    /**
     * Creates and computes the dynamic programing matrix for the algorithm.
     *
     * @throws InvalidScoringRuleException If the defined scoring rule is not
     * compatible with the loaded sequences.
     */
    protected void computeDynamicProgrammingMatrix()
            throws InvalidScoringRuleException {
        int line, column, lines, columns, ins, rem, sub;

        lines = firstSequence.length() + 1;
        columns = secondSequence.length() + 1;

        computationMatrix = new int[lines][columns];

        // fills the first line
        computationMatrix[0][0] = 0;
        for (column = 1; column < columns; column++) {
            computationMatrix[0][column] = computationMatrix[0][column - 1]
                    + insertionScore(secondSequence.charAt(column));
        }

        // computes the similarity matrix line by line
        for (line = 1; line < lines; line++) {
            // fills the first column
            computationMatrix[line][0] = computationMatrix[line - 1][0]
                    + removalScore(firstSequence.charAt(line));

            for (column = 1; column < columns; column++) {
                ins = computationMatrix[line][column - 1]
                        + insertionScore(secondSequence.charAt(column));
                sub = computationMatrix[line - 1][column - 1]
                        + substitutionScore(firstSequence.charAt(line), secondSequence.charAt(column));
                rem = computationMatrix[line - 1][column]
                        + removalScore(firstSequence.charAt(line));

                // chose the biggest of the computed scores for the alignment between A[1..i] e B[1..j] 
                computationMatrix[line][column] = max(ins, sub, rem);
            }
        }
    }

    /**
     * This method computes and returns an optimal global alignment between the
     * loaded sequences using the provided dynamic programming matrix. Before
     * calling this method it is necessary to create the dynamic programming
     * matrix using the method <CODE>computeDynamicProgrammingMatrix</CODE>.
     *
     * @return An optimal global sequence alignment between the loaded sequences
     * @throws InvalidScoringRuleException If the defined scoring rule is not
     * compatible with the loaded sequences.
     * @see #computeDynamicProgrammingMatrix
     */
    protected SequenceAlignment computeOptimalAlignment()
            throws InvalidScoringRuleException {
        StringBuffer firstSequenceWithGaps, alignmentDescriptiveLine, secondSequenceWithGaps;
        int line, column, sub, maxScore;

        firstSequenceWithGaps = new StringBuffer();
        alignmentDescriptiveLine = new StringBuffer();
        secondSequenceWithGaps = new StringBuffer();

        // starts from the last line and last column
        line = computationMatrix.length - 1;
        column = computationMatrix[line].length - 1;
        maxScore = computationMatrix[line][column];

        while ((line > 0) || (column > 0)) {
            if (column > 0) {
                if (computationMatrix[line][column] == computationMatrix[line][column - 1]
                        + insertionScore(secondSequence.charAt(column))) {
                    // insertion
                    firstSequenceWithGaps.insert(0, GAP_CHAR_SUBSTITUTION);
                    alignmentDescriptiveLine.insert(0, GAP_CHAR);
                    secondSequenceWithGaps.insert(0, secondSequence.charAt(column));
                    column = column - 1;

                    // continues to the next loop iteration
                    continue;
                }
            }

            if ((line > 0) && (column > 0)) {
                sub = substitutionScore(firstSequence.charAt(line), secondSequence.charAt(column));

                if (computationMatrix[line][column] == computationMatrix[line - 1][column - 1] + sub) {
                    // substitution
                    firstSequenceWithGaps.insert(0, firstSequence.charAt(line));
                    if (firstSequence.charAt(line) == secondSequence.charAt(column)) {
                        alignmentDescriptiveLine.insert(0, MATCH_CHAR);
                    } else if (sub > 0) {
                        alignmentDescriptiveLine.insert(0, MATCH_CHAR_APROXIMATED);
                    } else {
                        alignmentDescriptiveLine.insert(0, MISMATCH_CHAR);
                    }
                    secondSequenceWithGaps.insert(0, secondSequence.charAt(column));
                    line = line - 1;
                    column = column - 1;

                    // continues to the next loop iteration
                    continue;
                }
            }

            // if the previous conditions were not met, it must be a removal
            firstSequenceWithGaps.insert(0, firstSequence.charAt(line));
            alignmentDescriptiveLine.insert(0, GAP_CHAR);
            secondSequenceWithGaps.insert(0, GAP_CHAR_SUBSTITUTION);
            line = line - 1;
        }

        return new SequenceAlignment(
                firstSequenceWithGaps.toString(),
                alignmentDescriptiveLine.toString(), 
                secondSequenceWithGaps.toString(),
                maxScore);
    }

    /**
     * Computes the score for the optimal alignment between the loaded sequences
     * according to the defined scoring rule. The method computes only the
     * similarity score, without creating the alignment matrix.
     *
     * Space complexity: O(n).
     *
     * @return the score for the optimal sequence alignment between the loaded
     * sequences.
     * @throws InvalidScoringRuleException If the defined scoring rule is not
     * compatible with the loaded sequences.
     */
    @Override
    protected int computeScore() throws InvalidScoringRuleException {
        int[] array;
        int line, column, lines, columns, temp, ins, rem, sub;

        lines = firstSequence.length() + 1;
        columns = secondSequence.length() + 1;

        if (lines <= columns) {
            // a matriz será percorrida a partir das colunas
            array = new int[lines];

            // percorre a primeira coluna
            array[0] = 0;
            for (line = 1; line < lines; line++) {
                array[line] = array[line - 1] + removalScore(firstSequence.charAt(line));
            }

            // processa a computationMatrix de similaridades coluna a coluna
            // o array contém apenas uma coluna em cada iteração
            for (column = 1; column < columns; column++) {
                // processa a primeira linha, a variável tmp irá armazenar os valores que deverão ser movidos mais tarde pro array
                temp = array[0] + insertionScore(secondSequence.charAt(column));

                for (line = 1; line < lines; line++) {
                    ins = array[line] + insertionScore(secondSequence.charAt(column));
                    sub = array[line - 1]
                            + substitutionScore(firstSequence.charAt(line),
                                    secondSequence.charAt(column));
                    rem = temp + removalScore(firstSequence.charAt(line));

                    // copia o valor de tmp para o array
                    array[line - 1] = temp;

                    // passa a maior pontuação entre inserção, substituição e remoção para tmp
                    temp = max(ins, sub, rem);
                }

                // passa o valor de tmp para o array
                array[lines - 1] = temp;
            }

            return array[lines - 1];
        } else {
            // a matrix será percorrida com referência nas linhas
            array = new int[columns];

            // preenche a primeira linha
            array[0] = 0;
            for (column = 1; column < columns; column++) {
                array[column] = array[column - 1]
                        + insertionScore(secondSequence.charAt(column));
            }

            // processa a matriz de similaridades linha a linha
            // o array contém apenas uma linha em cada iteração
            for (line = 1; line < lines; line++) {
                // processa a primeira linha, a variável tmp irá armazenar os valores que deverão ser movidos mais tarde pro array
                temp = array[0] + removalScore(firstSequence.charAt(line));

                for (column = 1; column < columns; column++) {
                    ins = temp + insertionScore(secondSequence.charAt(column));
                    sub = array[column - 1]
                            + substitutionScore(firstSequence.charAt(line),
                                    secondSequence.charAt(column));
                    rem = array[column] + removalScore(firstSequence.charAt(line));

                    // copia o valor de tmp para o array
                    array[column - 1] = temp;

                    // passa a maior pontuação entre inserção, substituição e remoção para tmp
                    temp = max(ins, sub, rem);
                }

                // passa o valor de tmp para o array
                array[columns - 1] = temp;
            }

            return array[columns - 1];
        }
    }
}

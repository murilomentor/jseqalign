package org.jseqalign.alignment;

import org.jseqalign.structure.SequenceAlignment;
import org.jseqalign.exceptions.InvalidScoringRuleException;
import org.jseqalign.exceptions.InvalidSequenceException;
import org.jseqalign.io.CharacterSequence;
import java.io.IOException;
import java.io.Reader;

/**
 * Classe que implementa o algoritmo padrão para alignment local de sequências
 * biológicas criado em 1981 por Smith e Waterman.
 * <p>
 * O algoritmo utiliza recursividade como técnica de programação dinâmica.
 * Considerando duas sequências A e B de tamanhos n e m, é criada uma
 * computationMatrix M com tamanho (n+1, m+1) que contém a pontuação para a
 * similaridade dos sufixos de A e B. Cada posição na computationMatrix M[i,j]
 * contém a pontuação para o alignment entre as sequências A[1..i] e B[1..j],
 * com exceção da primeira linha e primeira coluna, que sempre recebem o valor
 * zero.</p>
 * penalidade linear de gap
 *
 *
 * <CODE><BLOCKQUOTE><PRE>
 * M[0,0] = M[0,j] = M[i,0] = 0
 * M[i,j] = max {
 *              0,
 *              M[i,j-1]   + insercao(B[j]),
 *              M[i-1,j-1] + substituicao(A[i], B[j]),
 *              M[i-1,j]   + remocao(A[i])
 *          }
 * </PRE></BLOCKQUOTE></CODE>
 *
 *
 * @author Murilo S. Farias
 * @see AlgoritmoNeedlemanWunsch
 */
public class SmithWatermanAlgorithm extends SequenceAlignmentAlgorithm {

    /**
     * Primeira sequência a ser utilizada no alignment.
     */
    protected CharacterSequence firstSequence;

    /**
     * Segunda sequência a ser utilizada no alignment.
     */
    protected CharacterSequence secondSequence;

    /**
     * Matriz de programação dinâmica usada pelo algoritmo. Cada M(i, j) tem a
     * melhor pontuação para os caracteres alinhados das subsequências A(1..i) e
     * B(1..j), sendo que i é a posição atual da <CODE>firstSequence</CODE> e j
     * é a posição atual da <CODE>secondSequence</CODE>.
     */
    protected int[][] computationMatrix;

    /**
     *
     */
    protected int maxLine;

    /**
     *
     */
    protected int maxColumn;

    /**
     * Carrega as sequências em instâncias da classe {@link CharacterSequence}.
     *
     * @param firstFile Arquivo da primeira sequência do alignment
     * @param secondFile Arquivo da segunda sequência do alignment
     * @throws IOException Se houver um erro na leitura de um dos arquivos de
     * sequências
     * @throws InvalidSequenceException Se pelo menos uma das sequências lidas é
     * inválida
     * @see CharacterSequence
     */
    @Override
    protected void loadInternalSequences(Reader firstFile, Reader secondFile)
            throws IOException, InvalidSequenceException {

        this.firstSequence = new CharacterSequence(firstFile);
        this.secondSequence = new CharacterSequence(secondFile);
    }

    /**
     * Limpa as referências para os objetos das sequências e matrizes
     * carregadas.
     */
    @Override
    protected void unloadInternalSequences() {
        this.firstSequence = null;
        this.secondSequence = null;
        this.computationMatrix = null;
    }

    /**
     * Este método retrorna um alignment local ótimo entre as sequências
     * carregadas após calcular uma computationMatrix de programação dinâmica. O
     * método faz uma chamada <CODE>computeDynamicProgrammingMatrix</CODE> para
     * criar a computationMatrix de programação dinâmica, e depois chama
     * <CODE>computeOptimalAlignment</CODE> para processar o alignment.
     *
     * @return um alignment local ótimo entre as sequências carregadas
     * @throws InvalidScoringRuleException Se a regra de pontuação definida não
     * é compatível cm as sequências carregadas.
     * @see #computeDynamicProgrammingMatrix
     * @see #computeOptimalAlignment
     */
    @Override
    protected SequenceAlignment computeSequenceAlignment()
            throws InvalidScoringRuleException {

        // cria e processa a matriz de programação dinâmica 
        computeDynamicProgrammingMatrix();

        // cria um objeto com o alinhamento local ótimo entre as sequências
        SequenceAlignment computedAlignment;
        computedAlignment = computeOptimalAlignment();

        // remove a referência ao objeto da matriz para que possa ser limpo 
        // pelo Garbage Collector
        computationMatrix = null;

        return computedAlignment;
    }

    /**
     * Cria e processa a computationMatrix de programação dinâmica do algoritmo.
     *
     * @throws InvalidScoringRuleException Se a regra de pontuação definida não
     * é compatível com as sequências carregadas.
     */
    protected void computeDynamicProgrammingMatrix()
            throws InvalidScoringRuleException {
        int line, column, lines, columns, ins, sub, rem, maxScore;

        lines = firstSequence.length() + 1;
        columns = secondSequence.length() + 1;

        computationMatrix = new int[lines][columns];

        // preenche a primeira linha com zeros
        for (column = 0; column < columns; column++) {
            computationMatrix[0][column] = 0;
        }

        // define o valor zero para maxLine e maxColumn
        this.maxLine = this.maxColumn = maxScore = 0;

        // calcula a computationMatrix de similaridade linha por linha
        for (line = 1; line < lines; line++) {
            // primeira coluna também recebe valor zero
            computationMatrix[line][0] = 0;

            // calcula os valores de cada célula (da esquerda para a direita) para a linha atual
            for (column = 1; column < columns; column++) {
                ins = computationMatrix[line][column - 1] + insertionScore(secondSequence.charAt(column));
                sub = computationMatrix[line - 1][column - 1]
                        + substitutionScore(firstSequence.charAt(line),
                                secondSequence.charAt(column));
                rem = computationMatrix[line - 1][column] + removalScore(firstSequence.charAt(line));

                // escolhe o maior dos valores apurados para o alinhamento entre A[1..i] e B[1..j] 
                computationMatrix[line][column] = max(ins, sub, rem, 0);

                if (computationMatrix[line][column] > maxScore) {
                    maxScore = computationMatrix[line][column];
                    this.maxLine = line;
                    this.maxColumn = column;
                }
            }
        }
    }

    /**
     * Este método processa e retorna um alignment global ótimo entre as
     * sequências carregadas utilizando a computationMatrix de programação
     * dinâmica que deve ser criada previamente. Antes da chamada desse método é
     * necessário criar a computationMatrix de programação dinâmica utilizando o
     * método <CODE>computeDynamicProgrammingMatrix</CODE>.
     *
     * @return um alignment global ótimo entre as sequências carregadas
     * @throws InvalidScoringRuleException Se a regra de pontuação selecionada
     * não é compatível com as sequências carregadas.
     * @see #computeDynamicProgrammingMatrix
     */
    protected SequenceAlignment computeOptimalAlignment() throws
            InvalidScoringRuleException {
        StringBuffer firstSequenceWithGaps, alignmentDescriptiveLine, secondSequenceWithGaps;
        int line, column, maxScore, sub;

        line = this.maxLine;
        column = this.maxColumn;

        maxScore = computationMatrix[line][column];

        firstSequenceWithGaps = new StringBuffer();
        alignmentDescriptiveLine = new StringBuffer();
        secondSequenceWithGaps = new StringBuffer();

        while ((line > 0 || column > 0) && (computationMatrix[line][column] > 0)) {
            if (column > 0) {
                if (computationMatrix[line][column] == computationMatrix[line][column - 1]
                        + insertionScore(secondSequence.charAt(column))) {
                    firstSequenceWithGaps.insert(0, GAP_CHAR_SUBSTITUTION);
                    alignmentDescriptiveLine.insert(0, GAP_CHAR);
                    secondSequenceWithGaps.insert(0, secondSequence.charAt(column));

                    column = column - 1;

                    continue;
                }
            }

            if ((line > 0) && (column > 0)) {
                sub = substitutionScore(firstSequence.charAt(line), secondSequence.charAt(column));

                if (computationMatrix[line][column] == computationMatrix[line - 1][column - 1] + sub) {
                    firstSequenceWithGaps.insert(0, firstSequence.charAt(line));
                    if (firstSequence.charAt(line) == secondSequence.charAt(column)) {
                        //if (signalMatch()) {
                        alignmentDescriptiveLine.insert(0, MATCH_CHAR);
                        //} else {
                        //  linhaDescritivaDeAlinhamento.insert(0, firstSequence.charAt(linha));
                        //}
                    } else if (sub > 0) {
                        alignmentDescriptiveLine.insert(0, MATCH_CHAR_APROXIMATED);
                    } else {
                        alignmentDescriptiveLine.insert(0, MISMATCH_CHAR);
                    }
                    secondSequenceWithGaps.insert(0, secondSequence.charAt(column));

                    line = line - 1;
                    column = column - 1;

                    continue;
                }
            }

            firstSequenceWithGaps.insert(0, firstSequence.charAt(line));
            alignmentDescriptiveLine.insert(0, GAP_CHAR);
            secondSequenceWithGaps.insert(0, GAP_CHAR_SUBSTITUTION);

            line = line - 1;
        }

        return new SequenceAlignment(firstSequenceWithGaps.toString(),
                alignmentDescriptiveLine.toString(), secondSequenceWithGaps.toString(),
                maxScore);
    }

    /**
     * Processa a pontuação do alignment ótimo entre as sequências carregadas de
     * acordo com a regra de pontuação definida. O método calcula apenas a
     * pontuação de similaridade, sem criar a computationMatrix de alignment.
     * Complexidade de espaço: O(n).
     *
     * @return score do alignment global ótimo entre as sequências carregadas
     * @throws InvalidScoringRuleException se a regra de pontuação definida não
     * é compatível com as sequências carregadas.
     */
    @Override
    protected int computeScore() throws InvalidScoringRuleException {
        int[] array;
        int linhas = firstSequence.length() + 1, colunas = secondSequence.length() + 1;
        int line, column, temp, ins, rem, sub, maxScore;

        maxScore = 0;

        if (linhas <= colunas) {
            array = new int[linhas];

            for (line = 0; line < linhas; line++) {
                array[line] = 0;
            }

            for (column = 1; column < colunas; column++) {
                temp = 0;

                for (line = 1; line < linhas; line++) {
                    ins = array[line] + insertionScore(secondSequence.charAt(column));
                    sub = array[line - 1] + substitutionScore(firstSequence.charAt(line),
                            secondSequence.charAt(column));
                    rem = temp + removalScore(firstSequence.charAt(line));

                    array[line - 1] = temp;

                    temp = max(ins, sub, rem, 0);

                    if (temp > maxScore) {
                        maxScore = temp;
                    }
                }

                array[linhas - 1] = temp;
            }
        } else {
            array = new int[colunas];

            for (column = 0; column < colunas; column++) {
                array[column] = 0;
            }

            for (line = 1; line < linhas; line++) {
                temp = 0;

                for (column = 1; column < colunas; column++) {
                    ins = temp + insertionScore(secondSequence.charAt(column));
                    sub = array[column - 1]
                            + substitutionScore(firstSequence.charAt(line),
                                    secondSequence.charAt(column));
                    rem = array[column] + removalScore(firstSequence.charAt(line));

                    array[column - 1] = temp;

                    temp = max(ins, sub, rem, 0);

                    if (temp > maxScore) {
                        maxScore = temp;
                    }
                }

                array[colunas - 1] = temp;
            }
        }

        return maxScore;
    }
}

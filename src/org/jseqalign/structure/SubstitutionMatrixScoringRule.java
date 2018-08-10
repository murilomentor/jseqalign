package org.jseqalign.structure;

import org.jseqalign.exceptions.InvalidMatrixException;
import org.jseqalign.exceptions.InvalidScoringRuleException;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;

/**
 *
 * @author Murilo S. Farias
 */
public class SubstitutionMatrixScoringRule extends ScoringRule {

    /**
     * Caractere que indica as penalidades de remoção e inserção na matrix
     */
    protected static final char INSERTION_AND_REMOVAL_CHAR = '*';

    /**
     * Caractere que inicia uma linha de comentário no arquivo da matrix de
     * substiruição.
     */
    protected static final char COMMENT_CHAR = '#';

    /**
     * Contém os caracteres do cabeçalho das colunas da matrix de substituição.
     */
    protected String columnHeaders;

    /**
     * Contém os caracteres do cabeçalho das linhas da matrix de substituição.
     */
    protected String lineHeaders;

    /**
     * Contém os valores para as substituições, inserções e remoções indicados
     * nas interseções de linhas e colunas.
     */
    protected int[][] matrix;

    /**
     * Tamanho da matrix de substituição, só é necessário uma dimensão já que a
     * matrix sempre tem a mesma quantidade de linahs e colunas.
     */
    protected int matrixSize;

    /**
     * A pontuação máxima que pode ser retornada pela mariz de substituição.
     */
    protected int maximumScore;

    /**
     * Utilizado para criar uma nova instância de uma matrix de substituição que
     * deverá ser carredgada a partir de um arquivo.
     *
     * @param input
     * @throws IOException
     * @throws InvalidMatrixException
     */
    public SubstitutionMatrixScoringRule(Reader input)
            throws IOException, InvalidMatrixException {
        this(input, true);
    }

    /**
     * Utilizado para cria uma nova instância de uma matrix de substituição que
     * deverá ser carregada a partir de um arquivo, indicando com o parâmetro
     * <CODE>caseSensitive </CODE> se o algoritmo deve diferencias maiúsculas e
     * minúsculas.
     *
     * @param input
     * @param caseSensitive
     * @throws java.io.IOException
     * @throws org.jseqalign.exceptions.InvalidMatrixException
     */
    @SuppressWarnings("empty-statement")
    public SubstitutionMatrixScoringRule(Reader input, boolean caseSensitive)
            throws IOException, InvalidMatrixException {
        super(caseSensitive);

        StreamTokenizer st;
        StringBuilder sb = new StringBuilder();
        int line, column, absoluteMaximumScore = 0;
        char c;

        st = new StreamTokenizer(input);
        st.commentChar(COMMENT_CHAR);

        st.eolIsSignificant(true);

        for (st.nextToken(); st.ttype == StreamTokenizer.TT_EOL; st.nextToken());

        while ((st.ttype != StreamTokenizer.TT_EOF)
                && (st.ttype != StreamTokenizer.TT_EOL)) {
            if (st.ttype == StreamTokenizer.TT_WORD) {
                if (st.sval.length() > 1) {
                    throw new InvalidMatrixException("Columns headers must have only one "
                            + "character.");
                }

                sb.append(st.sval.charAt(0));
            } else if (st.ttype == INSERTION_AND_REMOVAL_CHAR) {
                sb.append(INSERTION_AND_REMOVAL_CHAR);
            } else {
                throw new InvalidMatrixException("Columns headers must have a letter "
                        + "or the special character: "
                        + INSERTION_AND_REMOVAL_CHAR + "'.");
            }

            st.nextToken();
        }

        if (caseSensitive) {
            columnHeaders = sb.toString();
        } else {
            columnHeaders = sb.toString().toUpperCase();
        }

        matrixSize = columnHeaders.length();

        // Confere se alguma coluna deve se encaixa na regra de penalidade por remoção
        if (columnHeaders.indexOf(INSERTION_AND_REMOVAL_CHAR) == -1) {
            throw new InvalidMatrixException("Substitution matrix does not have a "
                    + "removal penalty.");
        }

        // Confere se a matrix tem pelo menos um caractere
        if (matrixSize < 2) {
            throw new InvalidMatrixException("Substitution matrix must have at least "
                    + "one column with a character.");
        }

        // Confere se existem códigos de coluna repetidos
        for (int i = 0; i < matrixSize; i++) {
            if (columnHeaders.indexOf(columnHeaders.charAt(i), i + 1) > i) {
                throw new InvalidMatrixException("It must have distinct symbols for headers.");
            }
        }

        // Faz a alocação da matrix
        matrix = new int[matrixSize][matrixSize];

        // Limpa o buffer
        sb.delete(0, matrixSize);

        // Começa a ignorar EOL
        st.eolIsSignificant(false);
        if (st.ttype == StreamTokenizer.TT_EOL) {
            st.nextToken();
        }

        // Faz a leitura da matrix, uma letra para cada linha da matrix
        for (line = 0; line < matrixSize && st.ttype != StreamTokenizer.TT_EOF; line++) {
            // Leitura do cabeçalho.
            // Faz a leitura da primeira linha, deve conter os cabeçalhos das colunas
            if (st.ttype == StreamTokenizer.TT_WORD) {
                if (st.sval.length() > 1) {
                    throw new InvalidMatrixException("Substitution matrix headers must have "
                            + "only one character.");
                }

                sb.append(st.sval.charAt(0));
            } else if (st.ttype == INSERTION_AND_REMOVAL_CHAR) {
                sb.append(INSERTION_AND_REMOVAL_CHAR);
            } else {
                throw new InvalidMatrixException("Cabeçalhos de linhas da matriz de "
                        + "substituição precisam ser um caractere ou o caractere especial: "
                        + INSERTION_AND_REMOVAL_CHAR + "'.");
            }

            // Leitura dos valores
            for (column = 0; column < matrixSize; column++) {
                // Inicia a leitura dos valores
                if (st.nextToken() != StreamTokenizer.TT_NUMBER) {
                    throw new InvalidMatrixException("Valor inválido na linha "
                            + (line + 1) + ", coluna " + (column + 1) + ".");
                }

                matrix[line][column] = (int) st.nval;

                if (Math.abs(matrix[line][column]) > absoluteMaximumScore) {
                    absoluteMaximumScore = Math.abs(matrix[line][column]);
                }
            }

            st.nextToken();
        }

        // Caso nao seja case sensitive, converte todos os caracteres para maiúsculo
        if (caseSensitive) {
            lineHeaders = sb.toString();
        } else {
            lineHeaders = sb.toString().toUpperCase();
        }

        // Confere se foi lida a mesma quantidade de linhas e colunas
        if (lineHeaders.length() != matrixSize) {
            throw new InvalidMatrixException("A matriz de substituição precisa ter "
                    + "a mesma qtde de linhas e colunas.");
        }

        // Confere se existe uma linha relattiva a penalidade por inserção 
        if (lineHeaders.indexOf(INSERTION_AND_REMOVAL_CHAR) == -1) {
            throw new InvalidMatrixException("A matriz de substituição não tem "
                    + "linha para penalidade de inserção.");
        }

        // Confere se não existem códigos repetidos nas linhas
        for (int i = 0; i < matrixSize; i++) {
            if (lineHeaders.indexOf(lineHeaders.charAt(i), i + 1) > i) {
                throw new InvalidMatrixException("Cabeçalhos de linhas da matriz de "
                        + "substituição precisam ter códigos distintos.");
            }
        }

        // Confere se cada linha tem a sua coluna correspondente
        for (int i = 0; i < matrixSize; i++) {
            if (columnHeaders.indexOf(c = lineHeaders.charAt(i)) == -1) {
                throw new InvalidMatrixException("Não há coluna correspondente para "
                        + "o caractere da linha: '" + c + "' na matriz de substituição.");
            }
        }

        // Guarda a pontuação máxima encontrada
        this.maximumScore = absoluteMaximumScore;
    }

    /**
     * Retorna a pontuação para a substituição do caractere no parâmetro
     * <CODE>a</CODE> pelo caractere no parâmetro <CODE>b</CODE> de acordo com a
     * pontuação deinida na matrix de substituição.
     *
     * @return a pontuação da substituição do caractere em <CODE>a</CODE> pelo
     * caractere em <CODE>b</CODE>
     * @throws org.jseqalign.exceptions.InvalidScoringRuleException
     */
    @Override
    public int substitutionScore(char a, char b)
            throws InvalidScoringRuleException {
        int linha, coluna;

        if (caseSensitive) {
            linha = lineHeaders.indexOf(a);
            coluna = columnHeaders.indexOf(b);
        } else {
            linha = lineHeaders.indexOf(Character.toUpperCase(a));
            coluna = columnHeaders.indexOf(Character.toUpperCase(b));
        }

        if (linha < 0 || coluna < 0) {
            throw new InvalidScoringRuleException("Substitutição do caractere "
                    + a + " por " + b + " não está prevista na matriz de "
                    + "substituição selecionada.");
        }

        return matrix[linha][coluna];
    }

    /**
     * Retorna a pontuação para a inserção do caractere do parâmetro
     * <CODE>a</CODE> de acordo com o que está definido na matrix de
     * substituição.
     *
     * @return a pontuação de inserção do caractere do parâmetro <CODE>a</CODE>
     * de acordo com a pontuação definida na matrix de substituição.
     * @throws org.jseqalign.exceptions.InvalidScoringRuleException
     */
    @Override
    public int insertionScore(char a) throws InvalidScoringRuleException {
        return substitutionScore(INSERTION_AND_REMOVAL_CHAR, a);
    }

    /**
     * Retorna a pontuação para a remoção do caractere do parametro
     * <CODE>a</CODE> de acordo com o que está definido na matrix de
     * substituição.
     *
     * @return @throws org.jseqalign.exceptions.InvalidScoringRuleException
     */
    @Override
    public int removalScore(char a) throws InvalidScoringRuleException {
        return substitutionScore(a, INSERTION_AND_REMOVAL_CHAR);
    }

    /**
     * Indica se essa regra de pontuação suporta match parcial, no caso desta
     * matrix de substituição o match parcial é suportado.
     *
     * @return
     */
    @Override
    public boolean isPartialMatchPossible() {
        return true;
    }

    /**
     * retorna a pontuação máxima que pode ser atribuída por essa matrix de
     * substituição.
     *
     * @return
     */
    @Override
    public int maximumScore() {
        return maximumScore;
    }

    /**
     * Retorna a representação em String dessa instância de matrix de
     * substituição.
     *
     * @return
     */
    @Override
    public String toString() {
        int line, column;

        StringBuilder sb = new StringBuilder();

        // Números das colunas
        sb.append("Scoring matrix:\n\t");
        for (column = 0; column < matrixSize; column++) {
            sb.append("\t").append(column);
        }
        sb.append("\n\t");

        // Cabeçalhos (códigos) das colunas
        for (column = 0; column < matrixSize; column++) {
            sb.append('\t');
            sb.append(columnHeaders.charAt(column));
        }

        // Valores da matrix de pontuação
        for (line = 0; line < matrixSize; line++) {
            // Números das linhas e cabeçalhos/códigos
            sb.append("\n").append(line).append("\t").append(lineHeaders.charAt(line));

            for (column = 0; column < matrixSize; column++) {
                sb.append('\t');
                sb.append(matrix[line][column]);
            }
        }

        return sb.toString();
    }
}

package org.jseqalign.structure;

/**
 *
 * @author Murilo S. Farias
 */
public class GenericScoringRule extends ScoringRule {

    /**
     * Define a pontuação para recompensar um match encontrado no alinhamento.
     */
    protected int matchRewardScore;

    /**
     * Define a pontuação para penalizar um mismatch encontrado no alinhamento.
     */
    protected int mismatchPenaltyScore;

    /**
     * Define a pontuação para o custo de um gap encontrado no alinhamento.
     */
    protected int gapCostScore;

    /**
     * A pontuação máxima que pode ser retornada pela regra de pontuação.
     */
    protected int maximumScore;

    /**
     * Construtor utilizado para criar uma instância de uma regra de pontuação
     * genérica, que utiliza os valores recebidor para definir as pontuações de
     * match, mismatch e gap.
     *
     * @param matchRewardScore a recompensa para um match no alinhamento
     * @param mismatchPenaltyScore a penalidade para um mismatch no alinhamento
     * @param gapCostScore o custo de um gap no alinhamento
     */
    public GenericScoringRule(int matchRewardScore, int mismatchPenaltyScore, int gapCostScore) {
        this(matchRewardScore, mismatchPenaltyScore, gapCostScore, true);
    }

    /**
     * Construtor urilizado para criar uma instância de uma regra de pontuação
     * genérica, que utiliza os valores recebidos para definir as pontuações de
     * match, mismatch, e gap, e também se a regra de pontuação irá diferenciar
     * caracteres maiúsculos e minúsculos.
     *
     * @param matchRewardScore
     * @parammismatchPenaltyScorepenalidadeMismatch
     * @param gapCostScore
     * @param caseSensitive
     */
    public GenericScoringRule(int matchRewardScore, int mismatchPenaltyScore, int gapCostScore,
            boolean caseSensitive) {
        super(caseSensitive);

        this.matchRewardScore = matchRewardScore;
        this.mismatchPenaltyScore = mismatchPenaltyScore;
        this.gapCostScore = gapCostScore;

        if (Math.abs(matchRewardScore) >= Math.abs(mismatchPenaltyScore)) {
            if (Math.abs(matchRewardScore) >= Math.abs(gapCostScore)) {
                this.maximumScore = Math.abs(matchRewardScore);
            } else {
                this.maximumScore = Math.abs(gapCostScore);
            }
        } else if (Math.abs(mismatchPenaltyScore) >= Math.abs(gapCostScore)) {
            this.maximumScore = Math.abs(mismatchPenaltyScore);
        } else {
            this.maximumScore = Math.abs(gapCostScore);
        }
    }

    /**
     * Método que retorna a pontuação para a substituição do caractere em
     * <CODE>a</CODE> pelo caractere em <CODE>b</CODE>.
     *
     * @return a pontuação para a substituiçao de <CODE>a</CODE> por
     * <CODE>b</CODE>
     */
    @Override
    public int substitutionScore(char a, char b) {
        if (isCaseSensitive()) {
            if (a == b) {
                return matchRewardScore;
            } else {
                return mismatchPenaltyScore;
            }
        } else if (Character.toLowerCase(a) == Character.toLowerCase(b)) {
            return matchRewardScore;
        } else {
            return mismatchPenaltyScore;
        }
    }

    /**
     * Método que retorna a pontuação para a inserção do caractere em
     * <CODE>a</CODE>.
     *
     * @return a pontuação para a inserção do caractere em <CODE>a</CODE>.
     */
    @Override
    public int insertionScore(char a) {
        return gapCostScore;
    }

    /**
     * Método que retorna a pontuação para a remoção do caractere em
     * <CODE>a</CODE>.
     *
     * @return a pontuação para a remoção do caractere em <CODE>a</CODE>.
     */
    @Override
    public int removalScore(char a) {
        return gapCostScore;
    }

    /**
     * Método que retorna a pontuação máxima que pode ser atribuída por essa
     * regra de pontuação.
     *
     * @return a pontuação máima que pode ser atribuída
     */
    @Override
    public int maximumScore() {
        return maximumScore;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isPartialMatchPossible() {
        return false;
    }

    /**
     * Retorna <CODE>true</CODE> caso essa regra de pontuação aceite match
     * parcial, e <CODE>false</CODE> caso não suporte.
     *
     * @return <CODE>true</CODE> ou <CODE>false</CODE> indicando se aceita match
     * parcial ou não
     */
    @Override
    public String toString() {
        return "Regra de pontuação básica: recompensa para match = " + matchRewardScore
                + ", penalidade para mismatch = " + mismatchPenaltyScore
                + ", custo para gap = " + gapCostScore;
    }
}

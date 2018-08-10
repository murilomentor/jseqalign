package org.jseqalign.structure;

import org.jseqalign.exceptions.InvalidScoringRuleException;

/**
 * Classe abstrata que deve ser a superclasse de todas as regras de pontuação da
 * biblioteca SASBio. Ela especifica os atributos e métodos essenciais que
 * precisam ser implementados pelas subclasses. As regras de pontuaçao são
 * utilizadas pelos algoritmos de alinhamentos de sequências para calcular a
 * pontuação dos alinhamentos.
 *
 * @author Murilo S. Farias
 */
public abstract class ScoringRule {

    /**
     * Indica se essa regra de pontuação irá diferenciar caracteres maiúsculos e
     * minúsculos
     */
    protected boolean caseSensitive;

    /**
     * Construtor utilizado para criar uma nova instância de uma regra de
     * pontuação. O objeto criado pelo construtor padrão irá diferenciar
     * caracteres maiúsculos e minúsculos.
     */
    public ScoringRule() {
        this(true);
    }

    /**
     * Construtor utilizado para criar uma nova instância de uma regra de
     * pontuação, indicando através do parâmetro <CODE>caseSensitive</CODE> se o
     * algoritmo irá iferenciar caracteres maiúsculos e minúsculos.
     *
     * @param caseSensitive
     */
    public ScoringRule(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    /**
     * Indica se essa regra de pontuação irá diferenciar caracteres maiúsculos e
     * minúsculos
     *
     * @return
     */
    public boolean isCaseSensitive() {
        return this.caseSensitive;
    }

    /**
     * Retorna a pontuação para a substituição do caractere em <CODE>a</CODE>
     * pelo caractere em <CODE>b</CODE>.
     *
     * @param a primeiro caractere
     * @param b segundo caractere
     * @return a pontuação para a substituição do caractere em <CODE>a</CODE>
     * pelo caractere em <CODE>b</CODE>.
     * @throws org.jseqalign.exceptions.InvalidScoringRuleException
     */
    public abstract int substitutionScore(char a, char b)
            throws InvalidScoringRuleException;

    /**
     * Retorna a pontuação para a inserção do caractere em <CODE>a</CODE>.
     *
     * @param a o caractere a ser inserido
     * @return a pontuação para a inserção do caractere em <CODE>a</CODE>.
     * @throws org.jseqalign.exceptions.InvalidScoringRuleException
     */
    public abstract int insertionScore(char a)
            throws InvalidScoringRuleException;

    /**
     * Retorna a pontuação para a remoção do caractere em <CODE>a</CODE>.
     *
     * @param a o caractere a ser removido
     * @return a pontuação para a remoção do caractere em <CODE>a</CODE>
     * @throws InvalidScoringRuleException
     */
    public abstract int removalScore(char a)
            throws InvalidScoringRuleException;

    /**
     * Retorna a pontuação máima que pode ser atribuída por essa regra de
     * pontuação.
     *
     * @return a pontuação máima que pode ser atribuída
     */
    public abstract int maximumScore();

    /**
     * Retorna <CODE>true</CODE> caso essa regra de pontuação aceite match
     * parcial, e <CODE>false</CODE> caso não suporte.
     *
     * @return <CODE>true</CODE> ou <CODE>false</CODE> indicando se aceita match
     * parcial ou não
     */
    public abstract boolean isPartialMatchPossible();
}

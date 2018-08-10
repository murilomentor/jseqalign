package org.jseqalign.alignment;

import org.jseqalign.structure.SequenceAlignment;
import org.jseqalign.structure.ScoringRule;
import org.jseqalign.exceptions.InvalidScoringRuleException;
import org.jseqalign.exceptions.InvalidSequenceException;
import java.io.IOException;
import java.io.Reader;

/**
 * Classe abstrata que deve ser implementada por qualquer classe de algoritmos
 * de alignment de sequências. As clases de implementação devem realizar um
 * alignment entre duas sequências, considerando uma dada regra de
 * pontuação.
 *
 * <P>
 * Clientes dessa API devem carregar duas sequências e setar uma regra de
 * pontuação antes de chamar o método de alignment das sequências. A seguir está
 * um exemplo da sequencia de chamadas de métodos para a execução de um
 * alignment de sequências:</P>
 *
 * <CODE><BLOCKQUOTE><PRE>
 * // Criação e inicialização dos objetos
 * SequenceAlignmentAlgorithm algoritmo = new AlgumAlgoritmoDeAlinhamentoDeSequencias();
 * algoritmo.setRegraDePontuacao(algumaRegraDePontuacao);
 * algoritmo.loadSequences(sequencia1, sequencia2);
 *
 * // Execução do alignment propriamente dito
 * SequenceAlignment alignment = algoritmo.getSequenceAlignment();
 * int score = algoritmo.getPontuacao();
 * </PRE></BLOCKQUOTE></CODE>
 *
 * @author Murilo S. Farias
 * @see SequenceAlignment
 */
public abstract class SequenceAlignmentAlgorithm {

    /**
     * Caractere utilizado para representar um MATCH no alignment. O uso desse
     * caractere depende da flag <CODE>signalMatch</CODE>.
     *
     * @see #signalMatch
     * @see #signalMatch
     */
    protected static final char MATCH_CHAR = ':';
    /**
     * Caractere utilizado para representar um MATCH APROXIMADO no alignment.
     */
    protected static final char MATCH_CHAR_APROXIMATED = '.';
    /**
     * Caractere utilizado para representar um MISMATCH no alignment.
     */
    protected static final char MISMATCH_CHAR = ' ';
    /**
     * Caractere utilizado para representar um GAP no alignment.
     */
    protected static final char GAP_CHAR = ' ';
    /**
     * Caractere utilizado para representar um GAP na sequência.
     */
    protected static final char GAP_CHAR_SUBSTITUTION = '-';
    /**
     * Flag utilizada para indicar se o <CODE>MATCH_CHAR</CODE> deve ser
     * utilizado ou não. Se estiver setado como <CODE>true</CODE>, o algoritmo
     * deve usar o <CODE>MATCH_CHAR</CODE> na linha de alignment quando houver
     * um MATCH entre os caraceres das sequências que estão sendo comparadas. Se
     * estiver setado como <CODE>false</CODE>, o mesmo caractere que representa
     * o nucleotídeo ou o peptídeo que resultou num MATCH deve ser utilizado. A
     * flag é alerada quanto a regra de pontuação é informada para o
     * <CODE>SequenceAlignmentAlgorithm</CODE> utilizando o método
     * <CODE>setRegraDePontuacao</CODE>.
     *
     * @see #MATCH_CHAR
     * @see #signalMatch
     * @see #setScoringRule
     */
    protected boolean signalMatch;
    /**
     * Regra de pontuação usada para realizar o alignment de sequências. Este
     * atributo precisa ser configurado antes da execução do método de
     * alignment. Caso uma nova regra de pontuação seja setada, informações de
     * pontuação ou alignment que já tenham sido calculadas são perdidas.
     */
    protected ScoringRule scoringRule;
    /**
     * Armazena o resultado do último alignment realizado com essa classe. O
     * valor armazenado é uma strring que representa um alignment ótimo para
     * duas sequências, de acordo com uma regra de pontuação. Este atributo é
     * setado após a execução com sucesso do método
     * <CODE>computeSequenceAlignment</CODE> que as subclasses precisam
     * implementar. Caso novas sequências sejam carregadas ou uma nova regra de
     * pontuação seja informada, este atributo recebe <CODE>null</CODE>.
     */
    protected SequenceAlignment alignment;
    /**
     * Quando a flag <CODE>scoreComputed</CODE> está com o valor
     * <CODE>true</CODE>, este atributo armazena a pontuação do último alignment
     * de sequências realizado com essa classe. Este atributo é util quando
     * apenas a pontuação é necessária e os outros detalhes do alignment são
     * irrelevantes. O valor do atributo é setado após a execução com sucesso
     * dos métodos <CODE>computeSequenceAlignment</CODE> ou
     * <CODE>computeScore</CODE>, que são métodos que as subclasses precisam
     * implementar. Caso novas sequências sejam carregadas ou uma nova regra de
     * pontuação seja informada, este atributo recebe <CODE>null</CODE> e a flag
     * scoreComputed recebe o valor <CODE>false</CODE>.
     */
    protected int score;
    /**
     * Este atributo é uma flag que indica se a pontuação do alignment das duas
     * últimas sequências que foram carregadas já foi calculada. Essa flag é
     * setada para <CODE>true</CODE> após a execução com sucesso dos métodos
     * <CODE>computeSequenceAlignment</CODE> ou <CODE>computeScore</CODE>, que
     * são métodos que as subclasses precisam implementar. Caso novas sequências
     * sejam carregadas ou uma nova regra de pontuação seja informada, este
     * atributo recebe o valor <CODE>false</CODE>.
     */
    protected boolean scoreComputed = false;
    /**
     * Este aributo é uma flag que indica se as sequências foram carregadas.
     * Essa flag é setada para <CODE>true</CODE> após a execução com sucesso do
     * método <CODE>loadSequences</CODE>, que as subclasses devem implementar.
     */
    protected boolean loadedFiles = false;

    /**
     * Configura a regra de pontuação que deve ser utilizada no próximo
     * alignment. Este método apag qualquer alignment ou pontuação que já tenha
     * sido processada. Se a regra de pontuação informada suporta MATCH parcial,
     * o <CODE>SequenceAlignmentAlgorithm</CODE> é configurado para não utilizar
     * o caractere <CODE>MATCH_CHAR</CODE>. Se a regra de pontuação informada
     * NÃO suporta MATCH parcial, o <CODE>SequenceAlignmentAlgorithm</CODE> é
     * configurado para utilizar o caractere <CODE>MATCH_CHAR</CODE>.
     *
     * @param scoringRule Regra a ser usada para apurar a pontuação do alignment
     * @see #MATCH_CHAR
     * @see ScoringRule#isPartialMatchPossible
     */
    public void setScoringRule(ScoringRule scoringRule) {
        if (scoringRule == null) {
            throw new IllegalArgumentException("Null scoring scheme object.");
        }

        this.scoringRule = scoringRule;
        this.signalMatch = !scoringRule.isPartialMatchPossible();

        // quando uma nova regra de pontuação é informada, o alignment precisa 
        // ser processado novamente
        this.alignment = null;
        this.scoreComputed = false;
    }

    /**
     * Indica se o caractere <CODE>MATCH_CHAR</CODE> deve ser utilizado ou não.
     * Este método retorna <CODE>true</CODE> para indicar que o algoritmo de
     * alignment deve inserir o caractere <CODE>MATCH_CHAR</CODE> na linha
     * descritiva do alignment quando houver um MATCH entre os caracteres das
     * duas sequências, e retorna <CODE>false</CODE> para indicar que o
     * algoritmo de alignment deve inserir o próprio caractere que causou o
     * MATCH na linha descritiva do alignment.
     *
     * @return <CODE>true</CODE> se o caractere <CODE>MATCH_CHAR</CODE> deve ser
     * usado, do contrário, <CODE>false</CODE>.
     * @see #MATCH_CHAR
     * @see #signalMatch
     * @see #setScoringRule
     */
    protected boolean signalMatch() {
        return signalMatch;
    }

    /**
     * Carrega as sequências de acordo com as características de cada algoritmo.
     * Este método chama o método <CODE>loadInternalSequences</CODE> que deve
     * ser implementado pelas subclasses, enquanto realiza as alterações
     * necessárias nas flags de controle.
     *
     * @param firstFile Sequência 1
     * @param secondFile Sequência 2
     * @throws IOException Caso um erro aconteça durante a leitura das
     * sequências.
     * @throws InvalidSequenceException Se a sequência no arquivo lido é
     * incompatível com o algoritmo.
     */
    public void loadSequences(Reader firstFile, Reader secondFile)
            throws IOException, InvalidSequenceException {
        // quando novas sequencias são carregadas o alignment e a 
        // pontuação são apagados
        this.alignment = null;
        this.scoreComputed = false;

        // a flag loadedFiles é inicializada com false antes que uma 
        // possível exceção aconteça
        this.loadedFiles = false;

        // chama o método qua as subclasses implementam para carregar as sequências
        loadInternalSequences(firstFile, secondFile);

        // caso não haja exceção a flag loadedFiles recebe true
        this.loadedFiles = true;
    }

    /**
     * Apaga as referências dos atributos para os objetos carregados na memória.
     * Pra facilitar o trabalho do Garbage Collector.
     */
    public void unloadSequences() {
        // apaga as referências para os objetos de alignment e pontuação
        this.alignment = null;
        this.scoreComputed = false;

        // chama o método implementado pelas subclasses que apaga as referências
        // para as sequências carregadas
        unloadInternalSequences();

        this.loadedFiles = false;
    }

    /**
     * Retorna o último alignment processado ou pede que o método implementado
     * da subclasse processe um novo alignment utilizando o método
     * <CODE>computeSequenceAlignment</CODE> caso não exista nenhum. Antes da
     * chamada do método as sequências e a regra de pontuação já tem que ter
     * sido carregadas.
     *
     * @return um alignment entre as sequências carregadas
     * @throws InvalidScoringRuleException se a regra de pontuação carregada é
     * incompatível com as sequências carregadas
     * @see #computeSequenceAlignment
     */
    public SequenceAlignment getSequenceAlignment()
            throws InvalidScoringRuleException {
        if (!loadedFiles) {
            throw new IllegalStateException("As sequências que devem ser comparadas "
                    + "ainda não foram carregadas.");
        }

        if (scoringRule == null) {
            throw new IllegalStateException("A regra de pontuação para o alinhamento "
                    + "ainda não foi definido.");
        }

        if (this.alignment == null) {
            // synchronized garante que a regra de pontuação não vai mudar no meio do 
            // processamento do alignment
            synchronized (scoringRule) {
                // processa um novo alignment caso o objeto ainda não tenha 
                // processado nenhum
                this.alignment = computeSequenceAlignment();
            }

            // guarda o valor da pontuação apurada para o alignment
            this.score = this.alignment.getScore();
            this.scoreComputed = true;
        }

        return this.alignment;
    }

    /**
     * Retorna a pontuação apurada para o último alignment processado ou pede
     * para o método de implementação das subclasses <CODE>computeScore</CODE>
     * processar uma nova pontuação caso ainda não exista nenhuma. Antes da
     * chamada do método as sequências e a regra de pontuação já tem que ter
     * sido carregadas.
     *
     * @return a pontuação apurada para o alignment das sequências carregadas
     * @throws InvalidScoringRuleException se a regra de pontuação não é
     * compatível com as sequências que foram carregadas
     * @see #computeScore
     */
    public int getScore() throws InvalidScoringRuleException {
        if (!loadedFiles) {
            throw new IllegalStateException("As sequências que devem ser comparadas "
                    + "ainda não foram carregadas.");
        }

        if (scoringRule == null) {
            throw new IllegalStateException("A regra de pontuação para o alinhamento "
                    + "ainda não foi definido.");
        }

        if (!scoreComputed) {
            // synchronized garante que a regra de pontuação não vai mudar no meio do 
            // processamento do alinhamentos
            synchronized (scoringRule) {
                // processa um novo alignment caso o objeto ainda não tenha 
                // processado nenhum
                this.score = computeScore();
            }

            this.scoreComputed = true;
        }

        return this.score;
    }

    /**
     * As subclasses precisam implementar esse método para carregar as
     * sequências que serão utilizadas no alignment de acordo com as
     * características de cada algoritmo.
     *
     * @param firstFile Sequência 1
     * @param secondFile Sequência 2
     * @throws IOException se houver um erro na leitura das sequências
     * @throws InvalidSequenceException se qualquer das sequências é inválida
     * @see #loadSequences
     * @see SequenciaDeCaracteres
     * @see SequenciaDeFatores
     */
    protected abstract void loadInternalSequences(Reader firstFile, Reader secondFile)
            throws IOException, InvalidSequenceException;

    /**
     * As subclasses precisam implementar esse método para descarregar as
     * sequências utilizadas no último alignment de acordo com as
     * carasterísticas de cada algoritmo. As referências dos objetos devem ser
     * liberadas para que o Garbage Collector faça a limpeza da memória. O
     * método implementado pela subclasse é chamado pelo método público
     * <CODE>unloadSequences</CODE>
     *
     * @see #unloadSequences
     */
    protected abstract void unloadInternalSequences();

    /**
     * As subclasses precisam implementar esse método para processar o alignment
     * entre as sequências carregadas usando a regra de pontuação definida. O
     * método implementado pelas subclasses é chamado pelo método público
     * <CODE>getSequenceAlignment</CODE>.
     *
     * @return um alignment entre as sequências carregadas
     * @throws InvalidScoringRuleException se a regra de pontuação não é
     * compativel com as sequências carregadas
     * @see #getSequenceAlignment
     */
    protected abstract SequenceAlignment computeSequenceAlignment()
            throws InvalidScoringRuleException;

    /**
     * As subclasses precisam implementar esse método para processar a pontuação
     * do alignment entre as sequências carregadas usando a regra de pontuação
     * previamente definida. O método implementado pela subclasse é chamado pelo
     * método público <CODE>getPontuacao</CODE>.
     *
     * @return a pontuação do alignment entre as sequências carregadas
     * @throws InvalidScoringRuleException se a regra de pontuação definida não
     * é compatível com as sequências carregadas
     * @see #getScore
     */
    protected abstract int computeScore()
            throws InvalidScoringRuleException;

    /**
     * Este é um método auxiliar para a chamada do método
     * <CODE>substitutionScore</CODE> da instância de um objeto da classe
     * <CODE>ScoringRule</CODE> que foi definida para o algoritmo. O método
     * encapsulado retorna a pontuação que deve ser considerada para a
     * substituição do caractere A pelo caractere B de acordo com a regra de
     * pontuação selecionada.
     *
     * @param a - o caractere substituido
     * @param b - o caractere substituto
     * @return a pontuação para a substituição do caractere <CODE>a</CODE> pelo
     * caractere <CODE>b</CODE>
     * @throws InvalidScoringRuleException se a regra de pontuação selecionada
     * não é compatível com as sequências carregadas
     * @see ScoringRule#substitutionScore
     */
    protected final int substitutionScore(char a, char b)
            throws InvalidScoringRuleException {
        return scoringRule.substitutionScore(a, b);
    }

    /**
     * Este é um método auxiliar para a chamada do método
     * <CODE>insertionScore</CODE> da instância de um objeto da classe
     * <CODE>ScoringRule</CODE> que foi definida para o algoritmo. O método
     * encapsulado retorna a pontuação que deve ser considerada para a inserção
     * do caractere A de acordo com a regra de pontuação selecionada.
     *
     * @param a - o caractere cuja inserção deve ser considerada
     * @return a pontuação para a inserção de <CODE>a</CODE>
     * @throws InvalidScoringRuleException se a regra de pontuação selecionada
     * não é compatível com as sequências carregadas
     * @see ScoringRule#insertionScore
     */
    protected final int insertionScore(char a)
            throws InvalidScoringRuleException {
        return scoringRule.insertionScore(a);
    }

    /**
     * Este é um método auxiliar para a chamada do método
     * <CODE>removalScore</CODE> da instância de um objeto da classe
     * <CODE>ScoringRule</CODE> que foi definida para o algoritmo. O método
     * encapsulado retorna a pontuação que deve ser considerada para a remoção
     * do caractere A de acordo com a regra de pontuação selecionada.
     *
     * @param a o caractere cuja remoção deve ser considerada
     * @return a pontuação para a remoção de <CODE>a</CODE>
     * @throws InvalidScoringRuleException se a regra de pontuação selecionada
     * não é compatível com as sequências carregadas
     * @see ScoringRule#removalScore
     */
    protected final int removalScore(char a)
            throws InvalidScoringRuleException {
        return scoringRule.removalScore(a);
    }

    /**
     * Returns the greatest of the values received as parameters.
     *
     * @param v1 first value to be compared
     * @param vn remaining values to be compared
     * @return the greatest value in <CODE>v1</CODE> or <CODE>vn</CODE>
     */
    protected final int max(int v1, int... vn) {
        int max = v1;
        for (int v : vn) {
            max = (v >= max) ? v : max;
        }
        return max;
    }

}

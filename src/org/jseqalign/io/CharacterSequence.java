package org.jseqalign.io;

import org.jseqalign.exceptions.InvalidSequenceException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 *
 * @author Murilo S. Farias
 * @see SmithWaterman
 * @see NeedlemanWunsch
 */
public class CharacterSequence {

    /**
     * O caractere que denota uma linha de comentário no arquivo que contém a
     * sequência. O conteúdo de uma linha iniciada por esse caractere será
     * ignorado na leitura.
     */
    protected static final char COMMENT_CHAR = '>';
    /**
     *
     */
    protected char[] characterSequence;

    /**
     * Constrói um objeto do tipo SequenciaDeCaracteres a partir de um arquivo.
     *
     * @param reader
     * @throws java.io.IOException
     * @throws org.jseqalign.exceptions.InvalidSequenceException
     */
    public CharacterSequence(Reader reader) throws IOException, InvalidSequenceException {
        int charInt;
        char charChar;

        BufferedReader inputFile = new BufferedReader(reader);

        StringBuilder sb = new StringBuilder();

        while ((charInt = inputFile.read()) != -1) {
            charChar = (char) charInt;
            if (charChar == COMMENT_CHAR) {
                inputFile.readLine();
            } else if (Character.isLetter(charChar)) {
                sb.append(charChar);
            } else if (!Character.isWhitespace(charChar)) {
                throw new InvalidSequenceException("Spaces are not allowed on the sequences.");
            }
        }

        if (sb.length() > 0) {
            characterSequence = new char[sb.length()];
        } else {
            throw new InvalidSequenceException("The sequence is empty.");
        }

        sb.getChars(0, sb.length(), characterSequence, 0);
    }

    /**
     * Retorna o tamanho da sequência.
     *
     * @return
     */
    public int length() {
        return characterSequence.length;
    }

    /**
     * Retorna o caractere da sequência na posição informada.
     *
     * @param position
     * @return
     */
    public char charAt(int position) {
        return characterSequence[position - 1];
    }

    /**
     * Retorna a representação do objeto em uma String.
     *
     * @return
     */
    @Override
    public String toString() {
        return new String(characterSequence);
    }
}

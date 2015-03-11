package ch.heigvd.res.lab01.impl.filters;

import ch.heigvd.res.lab01.impl.Utils;
import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Logger;

/**
 * This class transforms the streams of character sent to the decorated writer.
 * When filter encounters a line separator, it sends it to the decorated writer.
 * It then sends the line number and a tab character, before resuming the write
 * process.
 *
 * Hello\n\World -> 1\Hello\n2\tWorld
 *
 * @author Olivier Liechti
 */
public class FileNumberingFilterWriter extends FilterWriter {

    private static final Logger LOG = Logger.getLogger(FileNumberingFilterWriter.class.getName());
    private int lineNumber = 0;
    private boolean firstLine = true;

    public FileNumberingFilterWriter(Writer out) {
        super(out);
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
        if (firstLine) {
            writeLineNumber();
            firstLine = false;
        }
        String nextLine[] = Utils.getNextLine(str.substring(off, off + len));
        if (nextLine[0].isEmpty()) {
            super.write(nextLine[1], 0, nextLine[1].length());
            return;
        }
        while (!nextLine[0].isEmpty()) {
            super.write(nextLine[0], 0, nextLine[0].length());
            writeLineNumber();
            nextLine = Utils.getNextLine(nextLine[1]);
        }
        if (!nextLine[1].isEmpty()) {
            super.write(nextLine[1], 0, nextLine[1].length());
        }
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        write(new String(cbuf), off, len);
        //throw new UnsupportedOperationException("The student has not implemented this method yet.");
    }

    private boolean lastCharWasNewLine = false;
    @Override
    public void write(int c) throws IOException {
        if (firstLine) {
            writeLineNumber();
            firstLine = false;
        }
        if (c == '\n' || c == '\r') {
            lastCharWasNewLine = true;
        } else if (lastCharWasNewLine) {
            writeLineNumber();
            lastCharWasNewLine = false;
        }
        super.write(c);
    }
    
    private void writeLineNumber() throws IOException {
        String str = ++lineNumber + "\t";
        super.write(str, 0, str.length());
    }

}

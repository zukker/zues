package com.amper.io;

import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;
import java.io.*;


/**
 * <p>Title: </p>
 * <p>Description: Расширение класса Properties. Добавлена возможность выбора
 * кодировки при загрузки из файла (метод load)</p>
 * <p>Copyright: Copyright (c) Ural-Geo 2010</p>
 * @author de-nos
 * @version 1.0
 */
public class AProperties extends Properties{
    String charsetName;;
    public AProperties() {
    }

    public void load(InputStream inStream, String charsetName) throws IOException {
        this.charsetName = charsetName;
        load_(inStream);
    }

    public void load(InputStream inStream) throws IOException {
        this.charsetName = "ISO8859_5";
        load_(inStream);
    }

    public synchronized void load_(InputStream inStream) throws IOException {
        char[] convtBuf = new char[1024];
        LineReader lr = new LineReader(inStream);

        int limit;
        int keyLen;
        int valueStart;
        char c;
        boolean hasSep;
        boolean precedingBackslash;

        while ((limit = lr.readLine()) >= 0) {
            c = 0;
            keyLen = 0;
            valueStart = limit;
            hasSep = false;

            //System.out.println("line=<" + new String(lineBuf, 0, limit) + ">");
            precedingBackslash = false;
            while (keyLen < limit) {
                c = lr.lineBuf[keyLen];
                //need check if escaped.
                if ((c == '=' ||  c == ':') && !precedingBackslash) {
                    valueStart = keyLen + 1;
                    hasSep = true;
                    break;
                } else if ((c == ' ' || c == '\t' ||  c == '\f') && !precedingBackslash) {
                    valueStart = keyLen + 1;
                    break;
                }
                if (c == '\\') {
                    precedingBackslash = !precedingBackslash;
                } else {
                    precedingBackslash = false;
                }
                keyLen++;
            }
            while (valueStart < limit) {
                c = lr.lineBuf[valueStart];
                if (c != ' ' && c != '\t' &&  c != '\f') {
                    if (!hasSep && (c == '=' ||  c == ':')) {
                        hasSep = true;
                    } else {
                        break;
                    }
                }
                valueStart++;
            }
            String key = loadConvert(lr.lineBuf, 0, keyLen, convtBuf);
            String value = loadConvert(lr.lineBuf, valueStart, limit - valueStart, convtBuf);
            put(key, value);
        }
    }
    class LineReader {
        public LineReader(InputStream inStream) {
            this.inStream = inStream;
        }
        byte[] inBuf = new byte[8192];
        char[] lineBuf = new char[1024];
        int inLimit = 0;
        int inOff = 0;
        InputStream inStream;

        int readLine() throws IOException {
            int len = 0;
            char c = 0;

            boolean skipWhiteSpace = true;
            boolean isCommentLine = false;
            boolean isNewLine = true;
            boolean appendedLineBegin = false;
            boolean precedingBackslash = false;
            boolean skipLF = false;

            while (true) {
                if (inOff >= inLimit) {
                    inLimit = inStream.read(inBuf);
                    inOff = 0;
                    if (inLimit <= 0) {
                        if (len == 0 || isCommentLine) {
                            return -1;
                        }
                        return len;
                    }
                }
                //The line below is equivalent to calling a
                //ISO8859-1 decoder.
                c = (char) (0xff & inBuf[inOff++]);
                if (skipLF) {
                    skipLF = false;
                    if (c == '\n') {
                        continue;
                    }
                }
                if (skipWhiteSpace) {
                    if (c == ' ' || c == '\t' || c == '\f') {
                        continue;
                    }
                    if (!appendedLineBegin && (c == '\r' || c == '\n')) {
                        continue;
                    }
                    skipWhiteSpace = false;
                    appendedLineBegin = false;
                }
                if (isNewLine) {
                    isNewLine = false;
                    if (c == '#' || c == '!') {
                        isCommentLine = true;
                        continue;
                    }
                }

                if (c != '\n' && c != '\r') {
                    lineBuf[len++] = c;
                    if (len == lineBuf.length) {
                        int newLength = lineBuf.length * 2;
                        if (newLength < 0) {
                            newLength = Integer.MAX_VALUE;
                        }
                        char[] buf = new char[newLength];
                        System.arraycopy(lineBuf, 0, buf, 0, lineBuf.length);
                        lineBuf = buf;
                    }
                    //flip the preceding backslash flag
                    if (c == '\\') {
                        precedingBackslash = !precedingBackslash;
                    } else {
                        precedingBackslash = false;
                    }
                }
                else {
                    // reached EOL
                    if (isCommentLine || len == 0) {
                        isCommentLine = false;
                        isNewLine = true;
                        skipWhiteSpace = true;
                        len = 0;
                        continue;
                    }
                    if (inOff >= inLimit) {
                        inLimit = inStream.read(inBuf);
                        inOff = 0;
                        if (inLimit <= 0) {
                            return len;
                        }
                    }
                    if (precedingBackslash) {
                        len -= 1;
                        //skip the leading whitespace characters in following line
                        skipWhiteSpace = true;
                        appendedLineBegin = true;
                        precedingBackslash = false;
                        if (c == '\r') {
                            skipLF = true;
                        }
                    } else {
                        return len;
                    }
                }
            }
        }
    }

    private String loadConvert (char[] in, int off, int len, char[] convtBuf) {
        if ( convtBuf.length < len ) {
            int newLen = len * 2;
            if ( newLen < 0 ) {
                newLen = Integer.MAX_VALUE;
            }
            convtBuf = new char[newLen];
        }
      if (charsetName.equals("ISO8859_5")) {
      char aChar;
      char[] out = convtBuf;
      int outLen = 0;
      int end = off + len;

          while (off < end) {
              aChar = in[off++];
              if (aChar == '\\') {
                  aChar = in[off++];
                  if(aChar == 'u') {
                      // Read the xxxx
                      int value=0;
          for (int i=0; i<4; i++) {
              aChar = in[off++];
              switch (aChar) {
                case '0': case '1': case '2': case '3': case '4':
                case '5': case '6': case '7': case '8': case '9':
                   value = (value << 4) + aChar - '0';
             break;
          case 'a': case 'b': case 'c':
                            case 'd': case 'e': case 'f':
             value = (value << 4) + 10 + aChar - 'a';
             break;
          case 'A': case 'B': case 'C':
                            case 'D': case 'E': case 'F':
             value = (value << 4) + 10 + aChar - 'A';
             break;
          default:
                                throw new IllegalArgumentException(
                                             "Malformed \\uxxxx encoding.");
                          }
                       }
                      out[outLen++] = (char)value;
                  } else {
                      if (aChar == 't') aChar = '\t';
                      else if (aChar == 'r') aChar = '\r';
                      else if (aChar == 'n') aChar = '\n';
                      else if (aChar == 'f') aChar = '\f';
                      out[outLen++] = aChar;
                  }
              } else {
            out[outLen++] = (char)aChar;
              }
          }
          return new String (out, 0, outLen);
      } else {
          byte[] bb = new byte[len];
          for (int i = 0; i < len; i++) {
              bb[i] = (byte)in[off + i];
          }
          try {
              return new String( bb, charsetName );
          } catch ( UnsupportedEncodingException ex ) {
              return new String( bb );
          }
      }
    }


}

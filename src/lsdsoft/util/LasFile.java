package lsdsoft.util;

import java.util.*;
import java.io.*;


/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: Ural-Geo</p>
 *
 * @author lsdsoft
 * @version 1.0
 */
public class LasFile {
    class LasSection {
        public String name;
        public Vector lines = new Vector();
        public LasSection(String name) {
            this.name = name;
        }
    };
    public Vector sections = new Vector();

    public LasFile() {
    }

    public LasSection getSection(int num) {
        return (LasSection)sections.get(num);
    }
    public LasSection getLastSection() {
        return (LasSection)sections.get(sections.size() - 1);
    }

    public void load(String fileName) throws Exception {
        load(new File(fileName));
    }
    public void load(File file) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = null;
        LasSection section = null;
        while ((line = br.readLine()) != null) {
            // new section
            if(line.startsWith("~")) {
                section = new LasSection(line.substring(1));
                sections.add(section);
                System.out.println("Loading section: " + section.name);
            } else { //else add line to current section
                if( section != null) {
                    section.lines.add( line );
                }
            }

        }
        br.close();

    }
}

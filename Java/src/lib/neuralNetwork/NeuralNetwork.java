/*
 * Copyright (c) 2017 Jonathan Crockett.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Jonathan Crockett - initial API and implementation and/or initial documentation
 */

package lib.neuralNetwork;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import javax.swing.JFileChooser;

/**
 *
 * @author Jonathan Crockett
 */
public class NeuralNetwork {
    public static void main(String[] args) {
        HMMNetwork<String> ai = null;
        JFileChooser jfc = new JFileChooser();
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        if(jfc.showOpenDialog(null) != JFileChooser.APPROVE_OPTION){
            System.exit(0);
        }
        
        try{
            ai = new HMMNetwork(processFile(jfc.getSelectedFile(), "[]"), "[]");
            
        }catch(FileNotFoundException e) {
            System.out.println("Error: File not found. " + e);
            System.exit(0);    
        }
        
        Scanner in = new Scanner(System.in);
        boolean running = true;
        while(running) {
            System.out.println("Type part of a sentence and press enter and the network to try to finish your sentence:");
            String line = in.nextLine();
            
            if(line.equals("quit"))
                running = false;
            
            else{
                Scanner lineScan = new Scanner(line);
                String lastWord = "";

                while(lineScan.hasNext()) {
                    lastWord = lineScan.next();
                    System.out.print(lastWord + " ");
                }

                Iterator thought = ai.getThought(lastWord);

                thought.next();
                while(thought.hasNext()) {
                    System.out.print(thought.next() + " ");
                }
                System.out.println();
            }
        }
    }
    
    public static List<String> processFile(File file, String delimiter) throws FileNotFoundException {
        LinkedList<String> wordList = new LinkedList<>();
        
        Scanner scn = new Scanner(file);
        
        while(scn.hasNext()){
            String word = scn.next().replaceAll("[\\p{Punct}&&[^0-9]&&[^.]&&[^?]&&[^!]]", "").toLowerCase();
            
            if(word.endsWith(".") || word.endsWith("?") || word.endsWith("!")){
                wordList.add(word);
                word = delimiter;
            }
                
            wordList.add(word);
        }
        
        return wordList;
    }
}
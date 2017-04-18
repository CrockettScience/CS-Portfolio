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

import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 *
 * @author Jonathan Crockett
 */
public class HiddenMerkovModel<T> implements ArtificialNeuralNetwork<T> {
    private static final int DEFAULT_TABLE_SIZE = 101;
    
    private int currentSize = 0;
    private Neuron<T>[] array;  
    
    
    public HiddenMerkovModel(List<T> knowledgeSource, String thoughtDelimiter) throws FileNotFoundException {
        allocateArray(DEFAULT_TABLE_SIZE);
        
        currentSize = 0;
        for( int i = 0; i < array.length; i++ )
            array[ i ] = null;
        
        learn(knowledgeSource, thoughtDelimiter);
    }
    
    public final void learn(List<T> knowledgeSource, String thoughtDelimiter) throws FileNotFoundException {
        
        Iterator<T> itr = knowledgeSource.iterator();
        
        if(!itr.hasNext())
            return;
        
        Neuron<T> last = getNeuron(itr.next());
        add(last);
        
        if(!itr.hasNext())
            return;
        
        while(itr.hasNext()) {
            Neuron current = getNeuron(itr.next());
            
            if(!current.data.equals(thoughtDelimiter)) {
                last.addConnection(current);
                add(current);
                last = current;
            }
            else {
                last.addConnection(null);
                if(itr.hasNext()) {
                    last = getNeuron(itr.next());
                    add(last);
                }
            }
        }
    }
    
    public Iterator getThought(T initial) {
        Neuron<T> neuron = new Neuron<>(initial);
        int pos = findPos(neuron);
        return (Iterator) new Thought(array[pos]);
    }
    
    private Neuron<T> getNeuron(T data) {
        Neuron<T> neuron = new Neuron<>(data);
        int pos = findPos(neuron);
        return array[pos] != null ? array[pos] : neuron;
    }
    
    private void add(Neuron<T> neuron) {
        int currentPos = findPos(neuron);
        
        if(array[currentPos] != null)
            return;//no dupes, don't add
        
        array[ currentPos ] = neuron;
        currentSize++;
        
        if( currentSize > array.length / 2 )        
            rehash( );                
    }
    
    private class Thought<T> implements Iterator<T> {
        Neuron<T> current;
        Random rand = new Random();

        public Thought(Neuron<T> startThought) {
            current = startThought;
        }

        @Override
        public boolean hasNext() {
            return current != null;
        }

        public T next() {
            if(current == null)
                throw new NoSuchElementException();

            T data = current.data;
            current = current.AdvanceThought(rand);
            return data;
        }

    }
    
    private int findPos(Neuron<T> neuron){
        int offset = 1;
        int currentPos = Math.abs(neuron.hashCode() % array.length);

        while(array[currentPos] != null){
            if(neuron.equals(array[currentPos]))   
                break;
            
            currentPos += offset;                  // Compute ith probe
            offset += 2;
            
            if(currentPos >= array.length)       // Implement the mod
                currentPos -= array.length;
        }
        return currentPos;
    }
    
    private void rehash(){
        Neuron<T>[] oldArray = array;
        
        allocateArray(nextPrime(4 * currentSize ));
        currentSize = 0;
        
        for( int i = 0; i < oldArray.length; i++ )
            if(oldArray[i] != null)
                add(oldArray[i]);
    }
    
    private static int nextPrime(int n){
        if(n % 2 == 0)
            n++;

        for( ;!isPrime(n) ;)
            n += 2;

        return n;
    }
    
    private static boolean isPrime( int n ){
        if(n == 2 || n == 3)
            return true;

        if(n == 1 || n % 2 == 0)
            return false;

        for(int i = 3; i * i <= n; i += 2)
            if(n % i == 0)
                return false;

        return true;
    }
    
    private void allocateArray(int arraySize){
        array = new Neuron[ nextPrime(arraySize)];
    }
}

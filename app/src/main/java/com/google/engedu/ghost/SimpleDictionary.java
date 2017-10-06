/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.ghost;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

public  class SimpleDictionary implements GhostDictionary {
    private ArrayList<String> words;

    public SimpleDictionary(InputStream wordListStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(wordListStream));
        words = new ArrayList<>();
        String line = null;
        while((line = in.readLine()) != null) {
            String word = line.trim();
            if (word.length() >= MIN_WORD_LENGTH)
                words.add(line.trim());
        }
    }

    @Override
    public boolean isWord(String word) {
        return words.contains(word);
    }

    @Override
    public String getAnyWordStartingWith(String prefix) {
        int possibleWordIndex;
        String possibleWord;
        if(prefix == ""){
            Random random = new Random();
            int randomIndex = random.nextInt(words.size());
            return words.get(randomIndex);
        }
        else{
            possibleWordIndex = searchPossibleWords(prefix);
            if(possibleWordIndex == -1){
                return "noWord";
            }else{
                possibleWord = words.get(possibleWordIndex);
                if(possibleWord.equals(prefix)){
                    return "sameAsPrefix";
                }else{
                    return possibleWord;
                }
            }
        }
    }

    private int searchPossibleWords(String prefix) {
        int lowerIndex = 0;
        int higherIndex = words.size()-1;
        int middleIndex,checkList;
        String checkWord;
        while(lowerIndex <= higherIndex){
            middleIndex = (lowerIndex+higherIndex)/2;
            checkWord = words.get(middleIndex);
            checkList = checkWord.startsWith(prefix)? 0 : prefix.compareTo(checkWord);
            if(checkList == 0){
                return middleIndex;
            }
            else if(checkList > 0){
                lowerIndex = middleIndex+1;
            }
            else{
                higherIndex = middleIndex-1;
            }
        }
        return -1;
    }

    @Override
    public String getGoodWordStartingWith(String prefix,int whoEndFirst) {
        String selected = null;
        int  possibleWordIndex,upIndex,downIndex,t;
        String possibleWord,checkWord;
        ArrayList<String> shortListedWord = new ArrayList<String>();
        Random randomWordIndex = new Random();


        if(prefix == ""){
            Random random = new Random();
            int randomIndex = random.nextInt(words.size());
            return words.get(randomIndex);
        }
        else{
            possibleWordIndex = searchPossibleWords(prefix);
            upIndex = downIndex = possibleWordIndex;
            Log.d("test", "getGoodWordStartingWith: "+possibleWordIndex);
            if(possibleWordIndex == -1){
                return "noWord";
            }
            possibleWord = words.get(possibleWordIndex);
            shortListedWord.add(possibleWord);
            while(true){
                upIndex++;
                if(upIndex == words.size()){
                    break;
                }
                checkWord = words.get(upIndex);
                t = checkWord.startsWith(prefix)? 0 : prefix.compareTo(checkWord);
                if(t != 0){
                    break;
                }
                if(checkWord.length()%2 == whoEndFirst){
                    shortListedWord.add(checkWord);
                }
            }
            while(true){
                downIndex--;
                if(downIndex < 0){
                    break;
                }
                checkWord = words.get(downIndex);
                t = checkWord.startsWith(prefix)? 0 : prefix.compareTo(checkWord);
                if(t != 0){
                    break;
                }
                if(checkWord.length()%2 == whoEndFirst){
                    shortListedWord.add(checkWord);
                }
            }
        }
        Log.d("test", "getGoodWordStartingWith: "+shortListedWord);
        if(shortListedWord.size() == 0){
            return "noWord";
        }else{
            int selectedWordIndex = randomWordIndex.nextInt(shortListedWord.size());
            selected = shortListedWord.get(selectedWordIndex);
            if(selected.equals(prefix)){
                return "sameAsPrefix";
            }
        }

        return selected;
    }
}
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

import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;


public class GhostActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String COMPUTER_TURN = "Computer's turn";
    private static final String USER_TURN = "Your turn";
    private GhostDictionary dictionary;
    private boolean userTurn = false;
    private Random random = new Random();

    private String computerWordNew = null;
    private String wordFragment = null;
    private String computerWord = null;
    private String yourWord = null;
    TextView ghostText;
    TextView gameStatus;
    RadioButton hardbutton;
    RadioButton easybutton;
    Button challengeButton;
    Button resetButton;
     int mode=0;
    int whoEndFirst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);
        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open("words.txt");
            dictionary = new SimpleDictionary(inputStream);
        } catch (IOException e) {
            Toast toast = Toast.makeText(this, "Could not load dictionary", Toast.LENGTH_LONG);
            toast.show();
        }

        Toast.makeText(getApplicationContext(),"please select mode",Toast.LENGTH_LONG).show();
        challengeButton = (Button) findViewById(R.id.challenge);
        resetButton = (Button) findViewById(R.id.restart);
        challengeButton.setEnabled(false);
        resetButton.setEnabled(false);
        challengeButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);
        easybutton=(RadioButton)findViewById(R.id.easy);
        hardbutton=(RadioButton)findViewById(R.id.hard);
        easybutton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                onStart(null);
                mode=1;
                challengeButton.setEnabled(true);
                resetButton.setEnabled(true);
            }
        });
         hardbutton.setOnClickListener(new View.OnClickListener(){

             @Override
             public void onClick(View view) {
              onStart(null);
                 mode=0;
                 challengeButton.setEnabled(true);
                 resetButton.setEnabled(true);
             }
         });

    }

    public boolean onKeyUp (int keyCode, KeyEvent event){
        char pressedKey = (char) event.getUnicodeChar();

        pressedKey = Character.toLowerCase(pressedKey);
        if(pressedKey >= 'a' && pressedKey <= 'z'){
            wordFragment = String.valueOf(ghostText.getText());
            wordFragment += pressedKey;
            ghostText.setText(wordFragment);
            computerTurn();
        }
        else{
            createToast("Invalid Input! Try Again",1000);
        }
        return false;
    }

    private void createToast(String msg, int time) {
        final Toast toast = Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG);
        toast.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                toast.cancel();;
            }
        },time);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ghost, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Handler for the "Reset" button.
     * Randomly determines whether the game starts with a user turn or a computer turn.
     * @param view
     * @return true
     */
    public boolean onStart(View view) {
        userTurn = random.nextBoolean();
        ghostText = (TextView) findViewById(R.id.ghostText);
        wordFragment = "";
        ghostText.setText(wordFragment);
        gameStatus = (TextView) findViewById(R.id.gameStatus);
        whoEndFirst = userTurn ? 1:0;
        if (userTurn) {
            gameStatus.setText(USER_TURN);
        } else {
            gameStatus.setText(COMPUTER_TURN);
            computerTurn();
        }
        return true;
    }

    private void computerTurn() {
        gameStatus.setText(COMPUTER_TURN);
        // Do computer turn stuff then make it the user's turn again
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mode==1) {
                    computerWord = dictionary.getAnyWordStartingWith(wordFragment);
                }
                else
                    computerWord = dictionary.getGoodWordStartingWith(wordFragment,whoEndFirst);
                System.out.println(computerWord);
                if(computerWord == "noWord"){
                    createToast("Computer Wins! No such Word",1000);
                    onStart(null);
                }
                else if(computerWord == "sameAsPrefix"){
                    createToast("Computer Wins! You Ended the word",1000);
                    onStart(null);
                }
                else{
                    if(wordFragment.equals("")){
                        wordFragment = computerWord.substring(0,1);
                    }else{
                        System.out.println(computerWord);
                        wordFragment = computerWord.substring(0,wordFragment.length()+1);
                    }
                    ghostText.setText(wordFragment);
                    createToast(USER_TURN,1000);
                }
                userTurn = true;
                gameStatus.setText(USER_TURN);
            }
        },1000);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.challenge:
                if(wordFragment.length() >= 4 ){
                    yourWord = dictionary.getAnyWordStartingWith(wordFragment);
                    if(yourWord == "noWord"){
                        createToast("You Wins! No such Word",1000);
                        onStart(null);
                    }
                    else if(yourWord == "sameAsPrefix"){
                        createToast("You Wins! Computer Ended the word",1000);
                        onStart(null);
                    }
                    else{
                        System.out.println(yourWord);
                        createToast("Computer Wins! Word Exist \n"+yourWord,1500);
                        onStart(null);
                    }
                }
                else{
                    createToast("Computer Wins! \nWord is Still Less then 4 Character",1000);
                    onStart(null);
                }
                break;
            case R.id.restart:

                onStart(null);
                break;
        }
    }
}

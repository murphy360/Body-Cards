package com.andrios.bodycards;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DeckOfCardsWorkoutActivity extends Activity {

	AdView adView;
	AdRequest request;
	
	int numPeople, currentUser, sets, decksize;
	ImageView card;
	TextView userTXT, remainingTXT;
	ArrayList<Profile> unusedProfiles, selectedProfiles;
	ArrayList<Exercise> exercises;
	Workout[] workouts;
	String workoutName;

	int[] cards = { R.drawable.c2, R.drawable.c3, R.drawable.c4, R.drawable.c5,
			R.drawable.c6, R.drawable.c7, R.drawable.c8, R.drawable.c9,
			R.drawable.c10, R.drawable.ca, R.drawable.cj, R.drawable.cq,
			R.drawable.ck, R.drawable.d2, R.drawable.d3, R.drawable.d4,
			R.drawable.d5, R.drawable.d6, R.drawable.d7, R.drawable.d8,
			R.drawable.d9, R.drawable.d10, R.drawable.da, R.drawable.dj,
			R.drawable.dq, R.drawable.dk, R.drawable.s2, R.drawable.s3,
			R.drawable.s4, R.drawable.s5, R.drawable.s6, R.drawable.s7,
			R.drawable.s8, R.drawable.s9, R.drawable.s10, R.drawable.sa,
			R.drawable.sj, R.drawable.sq, R.drawable.sk, R.drawable.h2,
			R.drawable.h3, R.drawable.h4, R.drawable.h5, R.drawable.h6,
			R.drawable.h7, R.drawable.h8, R.drawable.h9, R.drawable.h10,
			R.drawable.ha, R.drawable.hj, R.drawable.hq, R.drawable.hk,
			R.drawable.jb, R.drawable.jr };

	int[] backs = { R.drawable.back, R.drawable.back2, R.drawable.back3,
			R.drawable.back4 };

	int cardNum = 0;

	int cardBack;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.deckofcardsworkoutactivity);

		
		getExtras();
		
		chooseBack();
		shuffleDeck();
		setConnections();
		createWorkouts();
	}

	private void createWorkouts() {
		workouts = new Workout[numPeople];

		ArrayList<Exercise> exercises = new ArrayList<Exercise>();
		Exercise quickWorkout = new Exercise("Deck of Cards");
		exercises.add(quickWorkout);
		for (int i = 0; i < numPeople; i++) {
			//TODO Check Math on this one... 
			//Compensation commented out in line 202. if math doesn't work.   
			workouts[i] = new Workout(numPeople, decksize / numPeople, 14, 2, exercises, workoutName);
		}
	}

	@SuppressWarnings("unchecked")
	private void getExtras() {
	
		
		Intent intent = this.getIntent();
		numPeople = intent.getIntExtra("peeps", -1);
		
		decksize = intent.getIntExtra("decksize", 0);
		
		if(decksize == 0){
			decksize = 54;
		}else if(decksize == 1){
			decksize = 54/4;
		}else{
			decksize = 54/2;
		}
		
		while(decksize % numPeople != 0) {
			decksize++;
		}
		
		workoutName = intent.getStringExtra("workoutName");
		
		selectedProfiles = (ArrayList<Profile>) intent
				.getSerializableExtra("profiles");
		unusedProfiles = (ArrayList<Profile>) intent
				.getSerializableExtra("profilesU");
	}

	private void chooseBack() {
		Random number = new Random();
		cardBack = backs[number.nextInt(backs.length)];
	}

	private void shuffleDeck() {
		for (int i = 0; i < 10; i++) {
			shuffle();
		}
	}

	private void shuffle() {
		Random number = new Random();
		for (int i = 0; i < cards.length; i++) {
			int s = number.nextInt(cards.length);
			int x = cards[s];
			cards[s] = cards[i];
			cards[i] = x;
		}

	}

	private void getNewCard() {
		
		
		if(currentUser != -1){
			workouts[currentUser].stop();
			workouts[currentUser].setFinSets(sets);
			workouts[currentUser].incrementCount("Deck of Cards", 1);
			System.out.println("sets: "+ sets);
	
			
		}else{
			sets = 1;
			long startTime = SystemClock.elapsedRealtime();
			for(int i = 0; i < workouts.length; i++){
				
				workouts[i].startTotal(startTime);
			}
		}
		card.setImageResource(cardBack);

		dealCard();

		
		currentUser++;
		
		//TODO Clean up code (use modulos) or not doesn't matter...
		//		not sure if modulo is more efficient, but maybe something
		//		to look up for future reference.
		if(currentUser > (numPeople-1)){
			currentUser = 0;
			sets++;
		}
		
		//Guests turn to do exercise
		if(currentUser >= selectedProfiles.size() ){
			userTXT.setText("Guest User " + (currentUser - selectedProfiles.size() + 1));
			
		}
		
		//User With Profile's turn to do exercise
		else{
			userTXT.setText(selectedProfiles.get(currentUser).getFirstName());
		}
		
		remainingTXT.setText("Cards Remaining: " + (decksize - cardNum - 1));
		workouts[currentUser].start();
	}

	private void dealCard() {
		Timer clock = new Timer();
		clock.schedule(new myTask(), 150);
	}

	public class myTask extends TimerTask {
		final Handler handler = new Handler();

		public void run() {
			handler.post(new Runnable() {
				public void run() {
					card.setImageResource(cards[cardNum++]);
				}

			});
		};
	}

	private void setConnections() {
		
		userTXT = (TextView) findViewById(R.id.quickWorkoutActivityUserNameTXT);
		remainingTXT = (TextView) findViewById(R.id.cardCount);
		
		card = (ImageView) findViewById(R.id.card);
		
		currentUser = -1;
		remainingTXT.setText("Cards Remaining: " + (decksize - cardNum));
		
	    // Add the adView to it
		//TODO Change this name. 
	     AdView adView = (AdView)this.findViewById(R.id.deckOfCardsAdView);
	      
	    // Initiate a generic request to load it with an ad
		request = new AdRequest();
		request.setTesting(true);
		adView.loadAd(request);
		
		setOnClickListeners();
		
	}

	private void setOnClickListeners() {
		card.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (cardNum < decksize) {
					getNewCard();
				} else {
					workouts[currentUser].stop();
					workouts[currentUser].setFinSets(sets);
					workouts[currentUser].incrementCount("Deck of Cards", 1);
					System.out.println("sets: "+ sets);
	
					long endTime = SystemClock.elapsedRealtime();
					
					for(int i = 0; i < workouts.length; i++){
						/*
						if(workouts[i].getNumSets() != sets){
							workouts[i].setNumSets(sets);
						}
						*/
						workouts[i].stopTotal(endTime);
						workouts[i].setWorkoutTime(workouts[i].getTotalFormattedTime());
					}
					
					setWorkoutsToProfile();
					Intent el_fin = new Intent(v.getContext(), FinishedActivity.class);
					
					startActivityForResult(el_fin, 34222);
					

				}
			}

		});
		
	}
	
	public void setWorkoutsToProfile() {
		ArrayList<Profile> profiles = new ArrayList<Profile>();
		for (int k = 0; k < selectedProfiles.size(); k++) {

			selectedProfiles.get(k).addWorkout(workouts[k]);
			profiles.add(selectedProfiles.get(k));
		}
		for (int l = 0; l < unusedProfiles.size(); l++) {
			profiles.add(unusedProfiles.get(l));
		}

		try {
			FileOutputStream fos = openFileOutput("profiles",
					Context.MODE_PRIVATE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);

			oos.writeObject(profiles);

			oos.close();
			fos.close();
		} catch (IOException e) {

			Toast.makeText(DeckOfCardsWorkoutActivity.this, "Error: Writing to file",
					Toast.LENGTH_SHORT).show();
		}
	}
	public void onActivityResult(int requestCode, int returnCode, Intent intent) {
		if (returnCode == RESULT_OK) {
			setResult(RESULT_OK);
			DeckOfCardsWorkoutActivity.this.finish();
		}
	}
	
}
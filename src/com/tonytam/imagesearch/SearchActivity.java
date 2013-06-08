package com.tonytam.imagesearch;

import java.util.ArrayList;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;


/*
 * The main screen allowing users to search for an image
 */
public class SearchActivity extends Activity {
	EditText etQuery;
	GridView gvImageResult;
	Button btSearch;
	ArrayList<ImageResult> imageResults = new ArrayList<ImageResult>();
	ImageResultArrayAdapter imageAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		setupViews();
		imageAdapter = new ImageResultArrayAdapter(this,  imageResults);
		gvImageResult.setAdapter(imageAdapter);
		
		
		/*
		 * Click on a thumbnail brings up the full image Activity
		 */
		gvImageResult.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adaptor, View parent, int position,
					long rowId) {
					Intent i = new Intent(getApplicationContext(),
										  ImageDisplayActivity.class	
								);
					ImageResult imageResult = imageResults.get(position);
					
					i.putExtra("result", imageResult);
					// TODO, wanted to test this 
					// i.putExtra("result", "hey");

					startActivity(i);
			}
			
		});

		// Load preferences
		Map <String,String> prefs = ImageSearchSettings.loadPreferences((Activity) this);
		Log.d("DEBUG", prefs.toString());
	}

	public void setupViews() {
		etQuery = (EditText) findViewById(R.id.etQuery);
		gvImageResult = (GridView)findViewById(R.id.gvImageResult);
		btSearch = (Button ) findViewById(R.id.btSearch);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}


	/* 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Go to our preferences activity
		Intent i = new Intent(getApplicationContext(),
				  			SettingsActivity.class	
					);

		startActivity(i);
		return true;
	}

	/*
	 * Clicking on an image jumps to the ImageDisplayActivyt
	 */
	public void onImageSearch (View v) {
		String query = etQuery.getText().toString();
		Toast.makeText(this, "Searching for " + query,  Toast.LENGTH_SHORT)
			.show();
		
		// asyn load the data from Google API
		AsyncHttpClient client = new AsyncHttpClient();
		
		// Google Image Search
		// https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=android
		// TODO: Handle no data connection
		// TODO: Handle timeout
		// TOOD: Handle empty results
		// TODO: use YQL?
		client.get("https://ajax.googleapis.com/ajax/services/search/images?v=1.0&rsz=8" + 
					"&start=" + 0 + 
					"&v=1.0" + 
					"&q=" + Uri.encode(query),
					new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject response) {
				JSONArray imageJsonResults = null;

				try {
					imageJsonResults = response.getJSONObject(
							"responseData").getJSONArray("results");
					imageResults.clear();
					imageAdapter.addAll(ImageResult
							.fromJSONArray(imageJsonResults));
				
					Log.d("DEBUG", imageResults.toString());
				} catch (JSONException e) {
					
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(Throwable arg0, JSONObject arg1) {
				Log.d("DEBUG", arg1.toString());
			}			
		}
		
		);
	}
}

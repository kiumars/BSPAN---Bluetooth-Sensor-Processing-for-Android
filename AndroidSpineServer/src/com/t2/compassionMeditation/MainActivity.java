package com.t2.compassionMeditation;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

//Need the following import to get access to the app resources, since this
//class is in a sub-package.

import com.t2.AndroidSpineServerMainActivity;
import com.t2.R;
import com.t2.biomap.BioDetailActivity;
import com.t2.biomap.SharedPref;
import com.t2.filechooser.FileChooser;


public class MainActivity extends ListActivity {
	private static final String TAG = "MainActivity";
	private static final String mActivityVersion = "1.0";
	private static boolean firstTime = true;
	
	boolean mAllowMultipleUsers;
	int mUserMode;
	
	
	/**
	 * Application version info determined by the package manager
	 */
	private String mApplicationVersion = "";	
	
	private String[] mStrings = {
            "New Session", 
            "View EEG Activity", 
            "View Previous Session", 
            };	
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        mAllowMultipleUsers = SharedPref.getBoolean(this, 
        		com.t2.compassionMeditation.Constants.PREF_MULTIPLE_USERS, 
        		com.t2.compassionMeditation.Constants.PREF_MULTIPLE_USERS_DEFAULT);        
        
        mUserMode = SharedPref.getInt(this, 
        		com.t2.compassionMeditation.Constants.PREF_USER_MODE, 
        		com.t2.compassionMeditation.Constants.PREF_USER_MODE_DEFAULT);        
        
        
		try {
			PackageManager packageManager = this.getPackageManager();
			PackageInfo info = packageManager.getPackageInfo(this.getPackageName(), 0);			
			mApplicationVersion = info.versionName;
			Log.i(TAG, "Compassion Meditation Application Version: " + mApplicationVersion + ", Activity Version: " + mActivityVersion);
		} 
		catch (NameNotFoundException e) {
			   	Log.e(TAG, e.toString());
		}
        
		if (mUserMode == Constants.PREF_USER_MODE_DEFAULT) {
			Intent intent2 = new Intent(this, UserModeActivity.class);
			this.startActivityForResult(intent2, com.t2.compassionMeditation.Constants.USER_MODE_ACTIVITY);		
			
		}
		else {
			
			GoAhead();
	        
		}
		

        
    }

    void GoAhead() {
		if (mAllowMultipleUsers) {
			Intent intent2 = new Intent(this, SelectUserActivity.class);
			this.startActivityForResult(intent2, com.t2.compassionMeditation.Constants.SELECT_USER_ACTIVITY);		
			
		} else {
	    	SharedPref.putString(this, "SelectedUser", 	"");

		}        

        View header = getLayoutInflater().inflate(R.layout.layout_header, null);
        ListView listView = getListView();
        listView.addHeaderView(header);
        
        // Use an existing ListAdapter that will map an array
        // of strings to TextViews
        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mStrings));
        
        this.setListAdapter(new ArrayAdapter<String>(this, R.layout.main1,R.id.label, mStrings));        
        getListView().setTextFilterEnabled(true);
    }
    
    @Override
	protected void onStart() {
		super.onStart();
		
	}


	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
    	Intent intent;
    	
    	
		super.onListItemClick(l, v, position, id);
		
		
		
		// Get the item that was clicked
		Object o = this.getListAdapter().getItem(position - 1);
		String keyword = o.toString();
		if (keyword.equalsIgnoreCase("new session")) {
			
			
			boolean instructionsOnStart = SharedPref.getBoolean(this, 
					com.t2.compassionMeditation.Constants.PREF_INSTRUCTIONS_ON_START, 
					com.t2.compassionMeditation.Constants.PREF_INSTRUCTIONS_ON_START_DEFAULT);        

			if (instructionsOnStart) {
				Intent intent1 = new Intent(this, InstructionsActivity.class);
				this.startActivityForResult(intent1, Constants.INSTRUCTIONS_USER_ACTIVITY);		
			} else {
				intent = new Intent(this, MeditationActivity.class);
				this.startActivity(intent);		

			}
			
			
			
		}
		if (keyword.equalsIgnoreCase("view eeg activity")) {
			intent = new Intent(this, CompassionActivity.class);
			this.startActivity(intent);		
			
		}
		if (keyword.equalsIgnoreCase("View Previous Session")) {
//			intent = new Intent(this, FileChooser.class);
//			this.startActivityForResult(intent, Constants.FILECHOOSER_USER_ACTIVITY);

			intent = new Intent(this, ViewSessionsActivity.class);
			
			this.startActivityForResult(intent, Constants.VIEW_SESSIONS_ACTIVITY);

			
		}
		
		
	}


	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		
		switch(requestCode) {
			case Constants.FILECHOOSER_USER_ACTIVITY:
				if (data == null)
					return;
				String sessionName = data.getStringExtra(Constants.FILECHOOSER_USER_ACTIVITY_RESULT);
		    	Toast.makeText(this, "File Clicked: " + sessionName, Toast.LENGTH_SHORT).show();
		    	
		    	
				Intent intent = new Intent(this, ViewHistoryActivity.class);
				Bundle bundle = new Bundle();
	
				bundle.putString(Constants.EXTRA_SESSION_NAME,sessionName);
	
				//Add this bundle to the intent
				intent.putExtras(bundle);				
				
		    	
				this.startActivity(intent);			    	

				break;
				
		    case (com.t2.compassionMeditation.Constants.SELECT_USER_ACTIVITY) :  
			      if (resultCode == RESULT_OK) {
			  		if (data == null)
						return;
			    	  

			    	// We can't write the note yet because we may not have been re-initialized
			    	// since the not dialog put us into pause.
			    	// We'll save the note and write it at restore
			    	String userName = data.getStringExtra(com.t2.compassionMeditation.Constants.SELECT_USER_ACTIVITY_RESULT);

			    	if (userName == null) {
			    		userName = "";
			    	}

			    	SharedPref.putString(this, "SelectedUser", 	userName);
			    	  
			      } 
			      break; 	
			      
		    case (Constants.INSTRUCTIONS_USER_ACTIVITY):
				intent = new Intent(this, MeditationActivity.class);
				this.startActivity(intent);		
		    	break;
		    	
		    case (Constants.USER_MODE_ACTIVITY):
		    	GoAhead();
		    	break;
		    	
				
		}
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.getMenuInflater().inflate(R.menu.main1, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.settings:
			startActivity(new Intent("com.t2.biofeedback.MANAGER"));
			return true;
			
		case R.id.preferences:
			Intent intent = new Intent(this, PreferenceActivity.class);
			this.startActivity(intent);	
			return true;
			
						
		case R.id.about:
			String content = "National Center for Telehealth and Technology (T2)\n\n";
			content += "Compassion Meditation Application\n";
			content += "Application Version: " + mApplicationVersion + "\n";
			content += TAG + " Version: " + mActivityVersion;
			
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			
			alert.setTitle("About");
			alert.setMessage(content);	
			alert.show();			
			return true;
			
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	
	
	
	
}

package com.collectirdata.src;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	EditText tablename;
	DatabaseHandler dbhandle;
	String tableSelected;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tablename = (EditText) findViewById(R.id.tablename);
		dbhandle = new DatabaseHandler(this);
	}
	
	public void makeNewDB(View v){
		if(tablename.getText().toString().equals("")){
			Toast.makeText(this, "Please enter a remote name", Toast.LENGTH_SHORT).show();
			return;
		}
		Intent intent = new Intent(this, LearnNewRemote.class);
		intent.putExtra("tablename", tablename.getText().toString());
		startActivity(intent);
	}
	
	public void useExisting(View v){
		
		Intent intent = new Intent(this, ExistingRemote.class);
		startActivity(intent);
	}
	
	public void editExisting(View v){
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Choose remote");
		
		Spinner existingRemotes = new Spinner(this);
		ArrayList<String> spinnerContent = dbhandle.getExistingRemotes();
		tableSelected = spinnerContent.get(0);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,spinnerContent);
		existingRemotes.setAdapter(adapter);
		existingRemotes.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				TextView temp = (TextView) arg1;
				tableSelected = temp.getText().toString();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
			
		});
		
		alert.setView(existingRemotes);
		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Intent intent = new Intent(MainActivity.this,EditRemote.class);
				intent.putExtra("tablename", tableSelected);
				startActivity(intent);
			  }
			});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int whichButton) {
		    	
		    }
		});
			alert.show();
	}

	

}

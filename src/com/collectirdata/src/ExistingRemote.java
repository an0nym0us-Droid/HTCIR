package com.collectirdata.src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.htc.circontrol.CIRControl;
import com.htc.htcircontrol.HtcIrData;

public class ExistingRemote extends Activity {
	CIRControl mControl;
	HtcIrData mLearntKey;
	
	String tablename;
	int ini;
	Integer[] rids = {0x7f080007,0x7f080006,0x7f080008,0x7f080009,
				  0x7f08000a,0x7f08000b,0x7f08000c,0x7f08000d,0x7f08000e,
				  0x7f08000f,0x7f080010,0x7f080011,0x7f080012,0x7f080013,0x7f080014};
	
	String[] func_names = {"Power","CH+","CH-","Vol+","Vol-",
							"1","2","3","4","5",
							"6","7","8","9","0"};
	
	HashMap<Integer,Integer> map;
	DatabaseHandler dbhandle;
	
	Spinner existingRemotes;
	
	boolean clicked=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.existing_remote);
		ini = rids[0];
		mControl = new CIRControl (getApplicationContext(), mHandler);
		mControl.start();
		map = new HashMap<Integer, Integer>();
		
		dbhandle = new DatabaseHandler(this);
		existingRemotes = (Spinner) findViewById(R.id.existingRemotes);
		ArrayList<String> spinnerContent = dbhandle.getExistingRemotes();
		if(spinnerContent.size()==0){
			finish();
		}else{
			tablename = spinnerContent.get(0);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,spinnerContent);
			existingRemotes.setAdapter(adapter);
			existingRemotes.setOnItemSelectedListener(new OnItemSelectedListener() {
	
				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					TextView temp = (TextView) arg1;
					tablename = temp.getText().toString();
				}
	
				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
					
				}
				
			});
			for(int i=0;i<func_names.length;i++){
				Button button = (Button) findViewById(rids[i]);
				button.setText(func_names[i]);
				map.put(rids[i], i+1);
			}
			
		}
	}
	
	Handler mHandler = new Handler() { 
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what) {
			case CIRControl.MSG_RET_LEARN_IR:
				//CIR APP developer can check UUID to see if the reply message is what he/she is interesting. 
				
				//CIR APP developer must check learning IR data.
				//The learning IR data is in HtcIrData object.
				//If data is null, the learning is not successful, so developer can check error type.
				if(mLearntKey!=null) {
				
				}
				else {
					switch(msg.arg1) {
					case CIRControl.ERR_LEARNING_TIMEOUT:
						//TODO: timeout error because of CIR do not receive IR data from plastic remote. 

						break;
					case CIRControl.ERR_PULSE_ERROR:
						//TODO:
						//CIR receives IR data from plastic remote, but data is inappropriate.
						//The common error is caused by user he/she does not align the phone's CIR receiver
						// with CIR transmitter of plastic remote.		

						break;
					case CIRControl.ERR_OUT_OF_FREQ:
						//TODO:
						//This error is to warn user that the plastic remote is not supported or
						// the phone's CIR receiver does not align with CIR transmitter of plastic remote.

						break;
					case CIRControl.ERR_IO_ERROR:
						//TODO:
						//CIR hardware component is busy in doing early CIR activity.

						break;
					default:
						//TODO: other errors

						break;
					}
				}
				break;
			case CIRControl.MSG_RET_TRANSMIT_IR:
				switch(msg.arg1) {
				case CIRControl.ERR_IO_ERROR:
					//TODO:
					//CIR hardware component is busy in doing early CIR command.
					break;
				case CIRControl.ERR_INVALID_VALUE:
					//TODO:
					//The developer might use wrong arguments.
					break;
				case CIRControl.ERR_CMD_DROPPED:
					//TODO:
					//SDK might be too busy to send IR key, developer can try later, or send IR key with non-droppable setting  
					break;
				default:
					//TODO: other errors
					break;
				}
				break;
			case CIRControl.MSG_RET_CANCEL:
				switch(msg.arg1) {
				case CIRControl.ERR_IO_ERROR:
					//TODO:
					//CIR hardware component is busy in doing early CIR command.
					break;
				case CIRControl.ERR_CANCEL_FAIL:
					//TODO:
					//CIR hardware component is busy in doing early CIR command.
					break;
				default:
					//TODO: other errors
					break;
				}
				break;
			default:
				super.handleMessage(msg);
			}
	    }
	};
	
	int run = 0;
	
	
	public void sendIR(View v) {
		
		final int ffunc_id = map.get(v.getId());
		Log.e("sendIR","vid: "+v.getId());
		Log.e("sendIR","vid: "+ffunc_id);
		//TODO: to demonstrate how learning IR data is re-used and sent for target machine!
    	if(run==0) {
    		run = 1;
        	new Thread(new Runnable() {
        		public void run() {
        			UUID rid;
        				HtcIrData mykey = dbhandle.returnHTCIR(tablename, ffunc_id);
        				if(mykey!=null)
        				rid = mControl.transmitIRCmd (mykey, false);
        			
        			run=0;
            }}).start();
    	}
	}
	
	
	

}

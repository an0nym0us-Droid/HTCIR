package com.collectirdata.src;

import java.util.UUID;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.htc.circontrol.CIRControl;
import com.htc.htcircontrol.HtcIrData;

public class EditRemote extends Activity {
	CIRControl mControl;
	HtcIrData mLearntKey;
	
	TextView function;
	TextView status;
	TextView finished;
	
	String tablename;
	Integer func_id=1;
	
	String[] func_names = {"Power","CH+","CH-","Vol+","Vol-",
							"Dig 1","Dig 2","Dig 3","Dig 4","Dig 5",
							"Dig 6","Dig 7","Dig 8","Dig 9","Dig 0"};
	
	
	DatabaseHandler dbhandle;
	
	boolean clicked=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.learn_remote);
		function = (TextView) findViewById(R.id.function);
		function.setText(func_names[func_id-1]);
		status = (TextView) findViewById(R.id.status);
		mControl = new CIRControl (getApplicationContext(), mHandler);
		mControl.start();
		
		Intent intent = getIntent();
		tablename = intent.getStringExtra("tablename");
		
		dbhandle = new DatabaseHandler(this);
		
		finished = (TextView) findViewById(R.id.finished);
		finished.setVisibility(View.GONE);
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
				mLearntKey = (HtcIrData) msg.getData().getSerializable(CIRControl.KEY_CMD_RESULT);
				if(mLearntKey!=null) {
					status.setText("Success !! ");
					StringBuilder data = new StringBuilder();
					for(int item : mLearntKey.getFrame()){
						if(data.length() == 0)
							data.append(item);
						else
							data.append(","+item);
					}
					dbhandle.insertIntoTable(tablename, func_id, mLearntKey.getRepeatCount(), mLearntKey.getFrequency(), data.toString());
					clicked=false;
				}
				else {
					status.setText("Learn IR error=" + msg.arg1);
					clicked=false;
					switch(msg.arg1) {
					case CIRControl.ERR_LEARNING_TIMEOUT:
						//TODO: timeout error because of CIR do not receive IR data from plastic remote. 
						status.setText("Learn IR error= ERR_LEARNING_TIMEOUT");

						break;
					case CIRControl.ERR_PULSE_ERROR:
						//TODO:
						//CIR receives IR data from plastic remote, but data is inappropriate.
						//The common error is caused by user he/she does not align the phone's CIR receiver
						// with CIR transmitter of plastic remote.		
						status.setText("Learn IR error= ERR_PULSE_ERROR");

						break;
					case CIRControl.ERR_OUT_OF_FREQ:
						//TODO:
						//This error is to warn user that the plastic remote is not supported or
						// the phone's CIR receiver does not align with CIR transmitter of plastic remote.
						status.setText("Learn IR error= ERR_OUT_OF_FREQ");

						break;
					case CIRControl.ERR_IO_ERROR:
						//TODO:
						//CIR hardware component is busy in doing early CIR activity.
						status.setText("Learn IR error= ERR_IO_ERROR");

						break;
					default:
						//TODO: other errors
						status.setText("Learn IR error= OTHERS");

						break;
					}
				}
				break;
			case CIRControl.MSG_RET_TRANSMIT_IR:
				status.setText("Send IR error=" + msg.arg1);
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
	
	public void learn(View v){
		if(!clicked){
			mControl.learnIRCmd(10);
			status.setText("Learning in 10sec");
			clicked=true;
		}
	}

	public void test(View v) {
		
		//TODO: to demonstrate how learning IR data is re-used and sent for target machine!
    	if(run==0) {
    		run = 1;
        	new Thread(new Runnable() {
        		public void run() {
        			UUID rid;
        			if(mLearntKey != null) {
        				rid = mControl.transmitIRCmd (mLearntKey, false);
        			}
        			run=0;
            }}).start();
    	}
	}
	
	public void skip(View v){
		mLearntKey = null;
		if(func_id==15){
			finished.setVisibility(View.VISIBLE);
			return;
		}
		func_id++;
		function.setText(func_names[func_id-1]);
		status.setText("");
	}
	
	

	

}

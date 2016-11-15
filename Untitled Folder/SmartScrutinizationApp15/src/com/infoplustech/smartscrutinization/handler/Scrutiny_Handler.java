package com.infoplustech.smartscrutinization.handler;

import android.os.Handler;
import android.os.Message;

import com.infoplustech.smartscrutinization.callback.Scrutiny_NetworkCallback;
import com.infoplustech.smartscrutinization.utils.SSConstants;

public class Scrutiny_Handler extends Handler {

	private Scrutiny_NetworkCallback<Object> callback;

	public Scrutiny_Handler(Scrutiny_NetworkCallback<Object> callback) {
		this.callback = callback;
	}

	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		if (msg.what == SSConstants.SUCCESS) {
			callback.onSuccess(msg.obj.toString());
		} else if (msg.what == SSConstants.FAILURE) {
			callback.onFailure(msg.obj.toString());
		}else if (msg.what == SSConstants.SUCCESS1) {
			callback.onSuccess(msg.obj.toString());
		}else if (msg.what == SSConstants.FAILURE1) {
			callback.onFailure(msg.obj.toString());
		}
	}
}

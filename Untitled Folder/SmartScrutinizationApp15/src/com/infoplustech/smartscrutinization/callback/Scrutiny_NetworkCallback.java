package com.infoplustech.smartscrutinization.callback;

@SuppressWarnings("hiding")
public interface Scrutiny_NetworkCallback<Object> {
	void onSuccess(Object object);
	void onFailure(String errorMessge);
}

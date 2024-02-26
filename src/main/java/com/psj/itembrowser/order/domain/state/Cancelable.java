package com.psj.itembrowser.order.domain.state;

@FunctionalInterface
public interface Cancelable {
	public boolean isNotCancelable();
}
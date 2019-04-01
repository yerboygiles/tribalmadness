package com.oddlabs.util;

public abstract strictfp class ListElementImpl<T> implements ListElement<T> {
	private LinkedList<T> parent;

	private ListElement<T> next = null;
	private ListElement<T> prior = null;

        @Override
	public final void setListOwner(LinkedList<T> owner) {
		parent = owner;
	}

    @Override
	public final LinkedList<T> getListOwner() {
		return parent;
	}

    @Override
	public final void setPrior(ListElement<T> prior) {
		this.prior = prior;
	}

    @Override
	public final void setNext(ListElement<T> next) {
		this.next = next;
	}

    @Override
	public final ListElement<T> getPrior() {
		return prior;
	}

    @Override
	public final ListElement<T> getNext() {
		return next;
	}
}

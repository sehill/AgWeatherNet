package edu.wsu.weather.agweathernet.helpers;

public class SpinnerModel<T> {
	private T id;
	private String value;

	public SpinnerModel() {
		super();
	}

	public SpinnerModel(T id, String value) {
		this.id = id;
		this.value = value;
	}

	public void setId(T id) {
		this.id = id;
	}

	public T getId() {
		return id;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return value;
	}
}

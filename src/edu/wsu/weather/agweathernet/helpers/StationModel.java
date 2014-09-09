package edu.wsu.weather.agweathernet.helpers;

import java.io.Serializable;

public class StationModel implements Serializable {
	private static final long serialVersionUID = 1L;
	private String unitId;
	private String name;
	private String county;
	private String installationDate;
	private boolean isFavourite;

	public StationModel() {

	}

	public StationModel(String unitId, String name, String county,
			String installationDate) {
		this.setUnitId(unitId);
		this.setName(name);
		this.setCounty(county);
		this.setInstallationDate(installationDate);
	}

	public String getUnitId() {
		return unitId;
	}

	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCounty() {
		return county;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public String getInstallationDate() {
		return installationDate;
	}

	public void setInstallationDate(String installationDate) {
		this.installationDate = installationDate;
	}

	@Override
	public String toString() {
		return this.name + " " + this.county;
	}

	public boolean isFavourite() {
		return isFavourite;
	}

	public void setFavourite(boolean isFavourite) {
		this.isFavourite = isFavourite;
	}
}

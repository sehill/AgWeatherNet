package edu.wsu.weather.agweathernet.helpers.models;

import edu.wsu.weather.agweathernet.CommonUtility;

public class CurrentWeather {
	private String unitId;
	private String stationName;
	private String airTemp;
	private String soilTemp;
	private String relHumidity;
	private String dewPoint;
	private String leafWetness;
	private String soilMoist;
	private String solarRad;
	private String precip;
	private String windSpeed;
	private String stationLat;
	private String stationLong;

	public String getUnitId() {
		return unitId;
	}

	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}

	public String getStationName() {
		return stationName;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

	public String getAirTemp() {
		return CommonUtility.DECIMAL_FORMAT_SINGLE.format(Double
				.parseDouble(airTemp));
	}

	public void setAirTemp(String airTemp) {
		this.airTemp = airTemp;
	}

	public String getSoilTemp() {
		return CommonUtility.DECIMAL_FORMAT_SINGLE.format(Double
				.parseDouble(soilTemp));
	}

	public void setSoilTemp(String soilTemp) {
		this.soilTemp = soilTemp;
	}

	public String getRelHumidity() {
		return CommonUtility.DECIMAL_FORMAT_SINGLE.format(Double
				.parseDouble(relHumidity));
	}

	public void setRelHumidity(String relHumidity) {
		this.relHumidity = relHumidity;
	}

	public String getDewPoint() {
		return CommonUtility.DECIMAL_FORMAT_SINGLE.format(Double
				.parseDouble(dewPoint));
	}

	public void setDewPoint(String dewPoint) {
		this.dewPoint = dewPoint;
	}

	public String getLeafWetness() {
		return CommonUtility.DECIMAL_FORMAT_SINGLE.format(Double
				.parseDouble(leafWetness));
	}

	public void setLeafWetness(String leafWetness) {
		this.leafWetness = leafWetness;
	}

	public String getSoilMoist() {
		return CommonUtility.DECIMAL_FORMAT_SINGLE.format(Double
				.parseDouble(soilMoist));
	}

	public void setSoilMoist(String soilMoist) {
		this.soilMoist = soilMoist;
	}

	public String getSolarRad() {
		return CommonUtility.DECIMAL_FORMAT_SINGLE.format(Double
				.parseDouble(solarRad));
	}

	public void setSolarRad(String solarRad) {
		this.solarRad = solarRad;
	}

	public String getPrecip() {
		return CommonUtility.DECIMAL_FORMAT_SINGLE.format(Double
				.parseDouble(precip));
	}

	public void setPrecip(String precip) {
		this.precip = precip;
	}

	public String getWindSpeed() {
		return CommonUtility.DECIMAL_FORMAT_SINGLE.format(Double
				.parseDouble(windSpeed));
	}

	public void setWindSpeed(String windSpeed) {
		this.windSpeed = windSpeed;
	}

	public String getStationLat() {
		return stationLat;
	}

	public void setStationLat(String stationLat) {
		this.stationLat = stationLat;
	}

	public String getStationLong() {
		return stationLong;
	}

	public void setStationLong(String stationLong) {
		this.stationLong = stationLong;
	}

}

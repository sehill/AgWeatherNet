package edu.wsu.weather.agweathernet;

import java.io.Serializable;

public class AlertsModel implements Serializable {

	private static final long serialVersionUID = 1L;

	public String Id;
	public String reportName;
	public String stationId;
	public String station;
	public String alertEvent;
	public String unit;
	public String tresholdValue;
	public String startTime;
	public String address;
	public String objid;
	public String alertMethod;
	public String deliveryStatus;
	public String serviceProvider;

	public AlertsModel() {

	}

	public AlertsModel(String reportName, String stationId, String station, String alertEvent,
			String unit, String tresholdValue, String startTime,
			String address, String objid, String alertMethod,
			String deliveryStatus, String serviceProvider) {

		this.reportName = reportName;
		this.stationId = stationId;
		this.station = station;
		this.alertEvent = alertEvent;
		this.unit = unit;
		this.tresholdValue = tresholdValue;
		this.startTime = startTime;
		this.address = address;
		this.objid = objid;
		this.alertMethod = alertMethod;
		this.deliveryStatus = deliveryStatus;
		this.serviceProvider = serviceProvider;
	}

	@Override
	public String toString() {

		return "Id: " + this.Id + " report name: " + this.reportName
				+ " station: " + this.station + " alert event"
				+ this.alertEvent + " threshold value" + this.tresholdValue;

	}
}
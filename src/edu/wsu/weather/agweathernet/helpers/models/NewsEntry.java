package edu.wsu.weather.agweathernet.helpers.models;

import java.io.Serializable;

public class NewsEntry implements Serializable {
	private static final long serialVersionUID = -6332963545849617745L;

	private String pubDate;
	private String title;
	private String link;
	private String permaLink;
	private String description;

	public String getPubDate() {
		return pubDate;
	}

	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getPermaLink() {
		return permaLink;
	}

	public void setPermaLink(String permaLink) {
		this.permaLink = permaLink;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = extractCData(description);
	}

	private String extractCData(String data) {
		data = data.replaceAll("<!\\[CDATA\\[", "");
		data = data.replaceAll("\\]\\]>", "");
		return data;
	}
}
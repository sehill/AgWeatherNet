package edu.wsu.weather.agweathernet.helpers;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import edu.wsu.weather.agweathernet.helpers.models.NewsEntry;

public class NewRSSHandler extends DefaultHandler {
	private NewsEntry currentEntry = new NewsEntry();
	private List<NewsEntry> newsList = new ArrayList<NewsEntry>();

	private StringBuffer chars = new StringBuffer();

	private int entriesMaxQuantity = 1000;
	private int entriesCount;

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		chars = new StringBuffer();
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (localName.equalsIgnoreCase("title")) {
			currentEntry.setTitle(chars.toString());

		} else if (localName.equalsIgnoreCase("pubDate")) {
			currentEntry.setPubDate(chars.toString());

		} else if (localName.equalsIgnoreCase("link")) {
			currentEntry.setLink(chars.toString());

		} else if (localName.equalsIgnoreCase("description")) {
			currentEntry.setDescription(chars.toString());

		} else if (localName.equalsIgnoreCase("guid")) {
			currentEntry.setPermaLink(chars.toString());

		} else if (localName.equalsIgnoreCase("item")) {
			newsList.add(currentEntry);
			currentEntry = new NewsEntry();
			entriesCount++;
			if (entriesCount >= entriesMaxQuantity) {
				return;
			}
		}
	}

	public void characters(char ch[], int start, int length) {
		chars.append(new String(ch, start, length));
	}

	public int getEntriesMaxQuantity() {
		return entriesMaxQuantity;
	}

	public void setEntriesMaxQuantity(int entriesMaxQuantity) {
		this.entriesMaxQuantity = entriesMaxQuantity;
	}

	public List<NewsEntry> getList() {
		return newsList;
	}
}

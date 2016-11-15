package com.infoplustech.smartscrutinization.utils;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;

public class BundleMappingHandler extends DefaultHandler{
	BundleModel bundleModel;
	List<BundleModel> listBundleMapping;
	private StringBuffer buffer;
	private boolean inTag;
	
	//<?xml version="1.0" encoding="UTF-8"?>
//	<response>
//	<bundle_mapping></bundle_mapping>
//	<regulation></regulation>
//	</response>
	
	public BundleMappingHandler(Context context) {
		buffer = new StringBuffer();
		listBundleMapping = new ArrayList<BundleModel>();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		// TODO Auto-generated method stub
		super.startElement(uri, localName, qName, attributes);
		if(localName.equalsIgnoreCase("response")) {
			bundleModel = new BundleModel();
		}
		else if(localName.equalsIgnoreCase("bundle_mapping")) {
			inTag = true;
		} else if(localName.equalsIgnoreCase("regulation")) {
			inTag = true;
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
		super.characters(ch, start, length);
		if(inTag == true) {
			buffer.append(ch, start, length);
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		// TODO Auto-generated method stub
		super.endElement(uri, localName, qName);
		if(localName.equalsIgnoreCase("response")) {
			listBundleMapping.add(bundleModel);
		}
		else if(localName.equalsIgnoreCase("bundle_mapping")) {
			inTag = false;
			bundleModel.setBundleMappingResponse(buffer.toString());
			buffer.setLength(0);
		} else if(localName.equalsIgnoreCase("regulation")) {
			inTag = false;
			bundleModel.setRegulation(buffer.toString());
			buffer.setLength(0);
		}
	}
	
	public List<BundleModel> getBundleMappingList() {
		return listBundleMapping;
	}
}

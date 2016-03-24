/*
 * Copyright (C) 2016. JRummy Apps, Inc. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 *
 * File created on 3/17/16 5:52 AM by Jared Rummler.
 */

package com.jrummyapps.fonts.googlewebfonts;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.gson.Gson;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class GoogleFonts {

  public static final String API_KEY = "AIzaSyDp-f-3fnrZNFP62hgCqJHj0t1HAixzLh4";

  public static final String WEBFONTS_URL = "https://www.googleapis.com/webfonts/v1/webfonts?key=" + API_KEY;

  private static final SimpleDateFormat GOOGLE_WEBFONT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

  /**
   * The list of families is returned in no particular order by default. It is possible however to sort the list using
   * the sort parameter:
   *
   * <p>https://www.googleapis.com/webfonts/v1/webfonts?sort=popularity</p>
   * <p>The possible sorting values are:</p>
   *
   * <ul>
   * <li>alpha: Sort the list alphabetically</li>
   * <li>date: Sort the list by date added (most recent font added or updated first)</li>
   * <li>popularity: Sort the list by popularity (most popular family first)</li>
   * <li>style: Sort the list by number of styles available (family with most styles first)</li>
   * <li>trending: Sort the list by families seeing growth in usage (family seeing the most growth first)</li>
   * </ul>
   */
  public static final class Sorting {

    /**
     * Sort the list alphabetically
     */
    public static final String ALPHA = "alpha";

    /**
     * Sort the list by date added (most recent font added or updated first)
     */
    public static final String DATE = "date";

    /**
     * Sort the list by popularity (most popular family first)
     */
    public static final String POPULARITY = "popularity";

    /**
     * Sort the list by number of styles available (family with most styles first)
     */
    public static final String STYLE = "style";

    /**
     * Sort the list by families seeing growth in usage (family seeing the most growth first)
     */
    public static final String TRENDING = "trending";

  }

  public static GoogleWebFonts getGoogleWebFonts(Gson gson, String sort) {
    String url = WEBFONTS_URL;
    if (sort != null) {
      switch (sort) {
        case Sorting.ALPHA:
          url += "&sort=" + sort;
          break;
        case Sorting.DATE:
          url += "&sort=" + sort;
          break;
        case Sorting.POPULARITY:
          url += "&sort=" + sort;
          break;
        case Sorting.STYLE:
          url += "&sort=" + sort;
          break;
        case Sorting.TRENDING:
          url += "&sort=" + sort;
          break;
      }
    }
    HttpRequest request = HttpRequest.get(url);
    String json = request.body();
    return gson.fromJson(json, GoogleWebFonts.class);
  }

  public static Specimen getSpecimen(GoogleWebFont font) {
    try {
      String url = "https://www.google.com/fonts/specimen/" + URLEncoder.encode(font.family, "UTF-8");
      Document document = Jsoup.connect(url).get();
      String creator = document.select("h2[itemprop=creator]").first().text();
      String description = document.select("div.description").first().text();
      return new Specimen(creator, description);
    } catch (Exception e) {
      return null;
    }
  }

  public static String getDescription(GoogleWebFont font) {
    try {
      String url = "https://www.google.com/fonts/specimen/" + URLEncoder.encode(font.family, "UTF-8");
      Document document = Jsoup.connect(url).get();
      return document.select("div.description").first().text();
    } catch (Exception e) {
      return null;
    }
  }

  public static Long getTimestamp(GoogleWebFont font) {
    try {
      return GOOGLE_WEBFONT_DATE_FORMAT.parse(font.lastModified).getTime();
    } catch (ParseException e) {
      return null;
    }
  }

  public static class Specimen {

    public final String creator;
    public final String description;

    Specimen(String creator, String description) {
      this.creator = creator;
      this.description = description;
    }
  }

}

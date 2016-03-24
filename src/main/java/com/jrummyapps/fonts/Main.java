/*
 * Copyright (C) 2016. JRummy Apps, Inc. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 *
 * File created on 3/16/16 2:34 AM by Jared Rummler.
 */

package com.jrummyapps.fonts;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.typography.font.sfntly.Font;
import com.jrummyapps.fonts.googlewebfonts.GoogleFonts;
import com.jrummyapps.fonts.googlewebfonts.GoogleWebFont;
import com.jrummyapps.fonts.googlewebfonts.GoogleWebFonts;
import com.jrummyapps.fonts.models.FontInfoHolder;
import com.jrummyapps.fonts.models.FontInformation;
import com.jrummyapps.fonts.utils.Utils;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.io.FileUtils;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {

  public static void main(String[] args) throws IOException {
    printReadMe();
  }

  private static void printReadMe() {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    GoogleWebFonts fonts = GoogleFonts.getGoogleWebFonts(gson, GoogleFonts.Sorting.POPULARITY);

    System.out.println("Hundreds of free fonts. Here are just a few of them:");

    int x = 0;
    for (GoogleWebFont item : fonts.items) {

      if (!item.files.containsKey("regular")) {
        continue;
      }

      String folder = item.family.toLowerCase().replaceAll(" ", "_");

      System.out.println("# <a href=\"fonts/" + folder + "/regular\">" +
          "<img src=\"fonts/" + folder + "/regular/preview1.png\"></a>");

      if (x == 15) {
        break;
      }
      x++;

    }
  }

  private static void createJsonFiles() throws IOException {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    GoogleWebFonts fonts = GoogleFonts.getGoogleWebFonts(gson, GoogleFonts.Sorting.POPULARITY);
    List<FontInfoHolder> list = new ArrayList<>();
    for (GoogleWebFont item : fonts.items) {
      FontInfoHolder data = new FontInfoHolder();
      data.categories = new String[]{item.category};
      data.variants = item.variants;
      data.lastModified = GoogleFonts.getTimestamp(item);
      data.subsets = item.subsets;
      data.name = item.family;
      data.folder = item.family.toLowerCase().replaceAll(" ", "_");
      list.add(data);
    }
    String json = gson.toJson(list);
    Files.write(Paths.get("fonts/googlewebfonts.json"), json.getBytes());

    List<FontInfoHolder> infos = new ArrayList<>();
    int pageNumber = 1;
    for (int i = 0; i < list.size(); i++) {
      infos.add(list.get(i));
      if (i > 0 && i % 50 == 0) {
        Files.write(Paths.get("fonts/page-" + pageNumber + ".json"), gson.toJson(infos).getBytes());
        infos.clear();
        pageNumber++;
      }
    }
    Files.write(Paths.get("fonts/page-" + pageNumber + ".json"), gson.toJson(infos).getBytes());
  }

  private static void downloadAllFonts(boolean redownload) {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    GoogleWebFonts fonts = GoogleFonts.getGoogleWebFonts(gson, GoogleFonts.Sorting.POPULARITY);

    File fontsDir = new File("fonts");
    fontsDir.mkdirs();

    for (GoogleWebFont item : fonts.items) {

      String dirName = item.family.toLowerCase().replaceAll(" ", "_");
      File fontSubdirectory = new File(fontsDir, dirName);
      fontSubdirectory.mkdirs();

      System.out.println(item.family);

      for (Map.Entry<String, String> entry : item.files.entrySet()) {
        try {
          String varient = entry.getKey();
          String url = entry.getValue();

          System.out.println(" ->> " + varient);

          File varientDir = new File(fontSubdirectory, varient);
          File fontFile = new File(varientDir, "font.ttf");
          File infoFile = new File(varientDir, "info.json");
          File subsetFile = new File(varientDir, "preview.ttf");
          File preview1Png = new File(varientDir, "preview1.png");
          File preview2Png = new File(varientDir, "preview2.png");
          File preview3Png = new File(varientDir, "preview3.png");
          File pangram1Png = new File(varientDir, "pangram1.png");
          File pangram2Png = new File(varientDir, "pangram2.png");
          File flipfontApk = new File(varientDir, "flipfont.apk");

          if (!redownload && fontFile.exists() && infoFile.exists() && subsetFile.exists() && preview1Png.exists() &&
              preview2Png.exists() && preview3Png.exists() && pangram1Png.exists() && pangram2Png.exists() &&
              flipfontApk.exists()) {
            continue;
          }

          varientDir.mkdirs();

          Font font = Utils.loadFont(url);
          FontInformation info = Utils.getFontInformation(font, item, varient);

          Files.write(Paths.get(fontFile.getAbsolutePath()), font.digest());
          Files.write(Paths.get(infoFile.getAbsolutePath()), gson.toJson(info).getBytes());
          Utils.subset(font).text(info.getFullFontName() + "Aa").strip(true).create(subsetFile);
          Utils.fontToPng(font, preview1Png, info.getFullFontName(), 50f, Color.BLACK);
          Utils.fontToPng(font, preview2Png, "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz 0123456789", 50f,
              Color.BLACK);
          Utils.fontToPng(font, preview3Png, "Aa", 50f, Color.BLACK);
          Utils.fontToPng(font, pangram1Png, "The quick brown fox jumps over the lazy dog.", 50f, Color.BLACK);
          Utils.fontToPng(font, pangram2Png, "Grumpy wizards make toxic brew for the evil Queen and Jack.", 50f,
              Color.BLACK);

          File apk = createApk(fontFile, info.getFullFontName(), info.getPostScriptName());
          apk.renameTo(flipfontApk);
          FileUtils.deleteDirectory(new File("FlipFont"));
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }

  }

  private static File createApk(File fontFile, String fontName, String filename) throws IOException {
    return createApk(fontFile, fontName, filename, null);
  }

  private static File createApk(File fontFile, String fontName, String filename, String suffix) throws IOException {
    String cmd = "bash flipfont.sh \"" + fontFile.getAbsolutePath() + "\" \"" + fontName + "\" \"" + filename + "\"";
    if (suffix != null) cmd += " " + suffix;
    CommandLine commandLine = CommandLine.parse(cmd);
    DefaultExecutor defaultExecutor = new DefaultExecutor();
    defaultExecutor.setExitValue(0);
    defaultExecutor.execute(commandLine);
    return new File("FlipFont/app/build/outputs/apk/app-release.apk");
  }

}

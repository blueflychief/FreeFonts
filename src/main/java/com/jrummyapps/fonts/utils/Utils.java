/*
 * Copyright (C) 2016. Jared Rummler <me@jaredrummler.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.jrummyapps.fonts.utils;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.typography.font.sfntly.Font;
import com.google.typography.font.sfntly.FontFactory;
import com.google.typography.font.sfntly.Tag;
import com.google.typography.font.sfntly.table.core.CMapTable;
import com.google.typography.font.sfntly.table.core.FontHeaderTable;
import com.google.typography.font.sfntly.table.core.NameTable;
import com.google.typography.font.sfntly.table.core.OS2Table;
import com.google.typography.font.sfntly.table.core.PostScriptTable;
import com.google.typography.font.tools.sfnttool.GlyphCoverage;
import com.google.typography.font.tools.subsetter.HintStripper;
import com.google.typography.font.tools.subsetter.RenumberingSubsetter;
import com.google.typography.font.tools.subsetter.Subsetter;
import com.jrummyapps.fonts.googlewebfonts.GoogleFonts;
import com.jrummyapps.fonts.googlewebfonts.GoogleWebFont;
import com.jrummyapps.fonts.models.FontInformation;

import java.awt.Color;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import static com.google.typography.font.sfntly.Font.PlatformId.Windows;
import static com.google.typography.font.sfntly.Font.WindowsEncodingId.UnicodeUCS2;
import static com.google.typography.font.sfntly.table.core.NameTable.WindowsLanguageId.English_UnitedStates;

public class Utils {

  /**
   * Get a font from a local file
   *
   * @param file
   *     a local valid font file
   * @return the font
   * @throws IOException
   *     if an error occurs while loading the font
   */
  public static Font loadFont(File file) throws IOException {
    FileInputStream fis = null;
    try {
      fis = new FileInputStream(file);
      byte[] bytes = new byte[(int) file.length()];
      fis.read(bytes);
      return loadFont(bytes);
    } finally {
      if (fis != null) {
        try {
          fis.close();
        } catch (IOException ignored) {
        }
      }
    }
  }

  /**
   * Downloads a font from the given URL
   *
   * @param url
   *     the URL
   * @return the font
   * @throws IOException
   *     if an error occurs while loading the font
   */
  public static Font loadFont(String url) throws IOException {
    return loadFont(HttpRequest.get(url).bytes());
  }

  /**
   * Load a font from a byte array
   *
   * @param bytes
   *     font bytes
   * @return the font
   * @throws IOException
   *     if an error occurs while loading the font
   */
  public static Font loadFont(byte[] bytes) throws IOException {
    Font.Builder[] builders = FontFactory.getInstance().loadFontsForBuilding(bytes);
    builders[0].setDigest(bytes);
    return builders[0].build();
  }

  /**
   * Create a preview image for a font
   *
   * @param fontFile
   *     the local font file
   * @param string
   *     the preview text
   * @param fontSize
   *     the font size
   * @return the generated image
   * @throws IOException
   *     if an error occurs while loading the font
   * @throws FontFormatException
   *     if an error occurs while loading the font
   */
  public static BufferedImage convertStringToImage(File fontFile, String string, float fontSize)
      throws IOException, FontFormatException {
    return convertStringToImage(fontFile, string, fontSize, Color.BLACK);
  }

  /**
   * Create a preview image for a font
   *
   * @param sfntly
   *     the font
   * @param string
   *     the preview text
   * @param fontSize
   *     the font size
   * @param color
   *     the color for the preview
   * @return the generated image
   * @throws IOException
   *     if an error occurs while loading the font
   * @throws FontFormatException
   *     if an error occurs while loading the font
   */
  public static BufferedImage convertFontToImage(Font sfntly, String string, float fontSize, Color color)
      throws IOException, FontFormatException {
    BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
    Graphics2D graphics = bufferedImage.createGraphics();

    ByteArrayInputStream is = new ByteArrayInputStream(sfntly.digest());
    java.awt.Font font = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, is).deriveFont(fontSize);
    graphics.setFont(font);

    if (string == null) {
      string = getFullFontName(sfntly);
    }

    FontRenderContext fontRenderContext = graphics.getFontMetrics().getFontRenderContext();
    Rectangle2D rectangle = font.getStringBounds(string, fontRenderContext);
    graphics.dispose();

    int width = (int) Math.ceil(rectangle.getWidth());
    int height = (int) Math.ceil(rectangle.getHeight());

    bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);

    graphics = bufferedImage.createGraphics();
    graphics.setColor(color);
    graphics.setFont(font);

    FontMetrics fontMetrics = graphics.getFontMetrics();
    int x = 0;
    int y = fontMetrics.getAscent();

    graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    graphics.drawString(string, x, y);
    graphics.dispose();

    return bufferedImage;
  }

  /**
   * Create a preview image for a font
   *
   * @param fontFile
   *     the local font file
   * @param string
   *     the preview text
   * @param fontSize
   *     the font size
   * @param color
   *     the color for the text
   * @return the generated image
   * @throws IOException
   *     if an error occurs while loading the font
   * @throws FontFormatException
   *     if an error occurs while loading the font
   */
  public static BufferedImage convertStringToImage(File fontFile, String string, float fontSize, Color color)
      throws IOException, FontFormatException {
    BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
    Graphics2D graphics = bufferedImage.createGraphics();

    InputStream fontStream = new BufferedInputStream(new FileInputStream(fontFile));
    java.awt.Font font = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, fontStream).deriveFont(fontSize);
    graphics.setFont(font);

    if (string == null) {
      string = getFullFontName(loadFont(fontFile));
    }

    FontRenderContext fontRenderContext = graphics.getFontMetrics().getFontRenderContext();
    Rectangle2D rectangle = font.getStringBounds(string, fontRenderContext);
    graphics.dispose();

    int width = (int) Math.ceil(rectangle.getWidth());
    int height = (int) Math.ceil(rectangle.getHeight());

    bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);

    graphics = bufferedImage.createGraphics();
    graphics.setColor(color);
    graphics.setFont(font);

    FontMetrics fontMetrics = graphics.getFontMetrics();
    int x = 0;
    int y = fontMetrics.getAscent();

    graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    graphics.drawString(string, x, y);
    graphics.dispose();

    return bufferedImage;
  }

  /**
   * Create a font preview and save it as a PNG file
   *
   * @param font
   *     the font
   * @param string
   *     the preview text
   * @param fontSize
   *     the font size
   * @param color
   *     the color for the text
   * @return the generated image
   * @throws IOException
   *     if an error occurs while loading the font
   * @throws FontFormatException
   *     if an error occurs while loading the font
   */
  public static boolean fontToPng(Font font, File destination, String string, float fontSize, Color color) {
    try {
      ImageIO.write(convertFontToImage(font, string, fontSize, color), "PNG", destination);
      return true;
    } catch (IOException | FontFormatException e) {
      return false;
    }
  }

  /**
   * Create a font preview and save it as a PNG file
   *
   * @param font
   *     the font
   * @param destination
   *     the destination PNG
   * @param string
   *     the preview text
   * @param fontSize
   *     the font size
   * @param color
   *     the font color
   * @return {@code true} if the font preview was successfully saved as a PNG
   */
  public static boolean fontToPng(File font, File destination, String string, float fontSize, Color color) {
    try {
      ImageIO.write(convertStringToImage(font, string, fontSize, color), "PNG", destination);
      return true;
    } catch (IOException | FontFormatException e) {
      return false;
    }
  }

  public static String getFullFontName(Font font) {
    return getNameFromTable(font, NameTable.NameId.FullFontName);
  }

  public static String getFontFamilyName(Font font) {
    return getNameFromTable(font, NameTable.NameId.FontFamilyName);
  }

  public static String getFontSubfamilyName(Font font) {
    return getNameFromTable(font, NameTable.NameId.FontSubfamilyName);
  }

  public static String getPostscriptName(Font font) {
    return getNameFromTable(font, NameTable.NameId.PostscriptName);
  }

  public static String getCopyright(Font font) {
    return getNameFromTable(font, NameTable.NameId.CopyrightNotice);
  }

  public static String getTrademark(Font font) {
    return getNameFromTable(font, NameTable.NameId.Trademark);
  }

  public static String getLicenseUrl(Font font) {
    return getNameFromTable(font, NameTable.NameId.LicenseInfoURL);
  }

  public static String getLicenseDescription(Font font) {
    return getNameFromTable(font, NameTable.NameId.LicenseDescription);
  }

  public static String getDesigner(Font font) {
    return getNameFromTable(font, NameTable.NameId.Designer);
  }

  public static String getDesignerUrl(Font font) {
    return getNameFromTable(font, NameTable.NameId.DesignerURL);
  }

  public static String getVendorUrl(Font font) {
    return getNameFromTable(font, NameTable.NameId.VendorURL);
  }

  public static String getManufacturerName(Font font) {
    return getNameFromTable(font, NameTable.NameId.ManufacturerName);
  }

  public static String getVersionName(Font font) {
    return getNameFromTable(font, NameTable.NameId.VersionString);
  }

  /**
   * Get the name as a String for the specified name. If there is no entry for the requested name then
   * <code>null</code>
   * is returned. If there is no encoding conversion available for the name then a best attempt String will be
   * returned.
   *
   * @param font
   *     the font
   * @param ids
   *     the {@link NameTable.NameId}(s)
   * @return the name
   */
  public static String getNameFromTable(Font font, NameTable.NameId... ids) {
    NameTable nameTable = font.getTable(Tag.name);
    String value = null;
    for (NameTable.NameId nameId : ids) {
      value = nameTable.name(Windows.value(), UnicodeUCS2.value(), English_UnitedStates.value(), nameId.value());
      if (value != null) {
        break;
      }
    }
    return value;
  }

  /**
   * Get the font's weight class
   *
   * @param font
   *     the font
   * @return the font's weight class
   */
  public static int getWeightClass(Font font) {
    OS2Table table = font.getTable(Tag.OS_2);
    if (table == null) {
      return -1;
    }
    return table.usWeightClass();
  }

  /**
   * Check if a font is italic
   *
   * @param font
   *     the font
   * @return {@code true} if the font is italic
   */
  public static boolean isItalic(Font font) {
    FontHeaderTable head = font.getTable(Tag.head);
    if (head != null) {
      int macStyle = head.macStyleAsInt();
      if ((macStyle & FontHeaderTable.MacStyle.Italic.mask()) == FontHeaderTable.MacStyle.Italic.mask()) {
        return true;
      }
    }
    PostScriptTable postTable = font.getTable(Tag.post);
    if (postTable.italicAngle() != 0) {
      return true;
    }
    NameTable name = font.getTable(Tag.name);
    for (NameTable.NameEntry entry : name.names()) {
      if (entry.name().toLowerCase().contains("italic")) {
        return true;
      }
    }
    return false;
  }

  /**
   * Check if a font is bold
   *
   * @param font
   *     the font
   * @return {@code true} if the font is bold
   */
  public boolean isBold(Font font) {
    FontHeaderTable head = font.getTable(Tag.head);
    if (head != null) {
      int macStyle = head.macStyleAsInt();
      if ((macStyle & FontHeaderTable.MacStyle.Bold.mask()) == FontHeaderTable.MacStyle.Bold.mask()) {
        return true;
      }
    }
    NameTable name = font.getTable(Tag.name);
    for (NameTable.NameEntry entry : name.names()) {
      if (entry.name().toLowerCase().contains("bold")) {
        return true;
      }
    }
    return false;
  }

  /**
   * Create a subset font
   *
   * @param font
   *     the original font
   * @return a builder class to generate the subset font
   */
  public static Subset subset(Font font) {
    return new Subset(font);
  }

  public static final class Subset {

    private final Font font;

    private String text;
    private boolean strip = true;

    private Subset(Font font) {
      this.font = font;
    }

    /**
     * @param text
     *     the characters for the subset font
     * @return this builder object
     */
    public Subset text(String text) {
      this.text = text;
      return this;
    }

    /**
     * @param strip
     *     {@code true} to strip tables from the font
     * @return this builder object
     */
    public Subset strip(boolean strip) {
      this.strip = strip;
      return this;
    }

    /**
     * Create the subset font
     *
     * @param path
     *     the destination path
     * @throws IOException
     *     if an error occurs while creating the font file
     */
    public void create(String path) throws IOException {
      create(new File(path));
    }

    /**
     * Create the subset font
     *
     * @param destination
     *     the destination file
     * @throws IOException
     *     if an error occurs while creating the font file
     */
    public void create(File destination) throws IOException {
      FontFactory fontFactory = FontFactory.getInstance();

      List<CMapTable.CMapId> cmapIds = new ArrayList<>();
      cmapIds.add(CMapTable.CMapId.WINDOWS_BMP);

      if (text == null) {
        text = getFullFontName(font);
      }

      Font newFont = font;
      if (text != null) {
        Subsetter subsetter = new RenumberingSubsetter(newFont, fontFactory);
        subsetter.setCMaps(cmapIds, 1);
        List<Integer> glyphs = GlyphCoverage.getGlyphCoverage(font, text);
        subsetter.setGlyphs(glyphs);
        Set<Integer> removeTables = new HashSet<>();
        // Most of the following are valid tables, but we don't renumber them yet, so strip
        removeTables.add(Tag.GDEF);
        removeTables.add(Tag.GPOS);
        removeTables.add(Tag.GSUB);
        removeTables.add(Tag.kern);
        removeTables.add(Tag.hdmx);
        removeTables.add(Tag.vmtx);
        removeTables.add(Tag.VDMX);
        removeTables.add(Tag.LTSH);
        removeTables.add(Tag.DSIG);
        // AAT tables, not yet defined in sfntly Tag class
        removeTables.add(Tag.intValue(new byte[]{'m', 'o', 'r', 't'}));
        removeTables.add(Tag.intValue(new byte[]{'m', 'o', 'r', 'x'}));
        subsetter.setRemoveTables(removeTables);
        newFont = subsetter.subset().build();
      }

      if (strip) {
        Subsetter hintStripper = new HintStripper(newFont, fontFactory);
        Set<Integer> removeTables = new HashSet<>();
        removeTables.add(Tag.fpgm);
        removeTables.add(Tag.prep);
        removeTables.add(Tag.cvt);
        removeTables.add(Tag.hdmx);
        removeTables.add(Tag.VDMX);
        removeTables.add(Tag.LTSH);
        removeTables.add(Tag.DSIG);
        hintStripper.setRemoveTables(removeTables);
        newFont = hintStripper.subset().build();
      }

      FileOutputStream fos = new FileOutputStream(destination);
      fontFactory.serializeFont(newFont, fos);

    }

  }

  public static FontInformation getFontInformation(Font font, GoogleWebFont webFont, String varient) {
    FontInformation info = new FontInformation();
    GoogleFonts.Specimen specimen = GoogleFonts.getSpecimen(webFont);
    info.setCategories(new String[]{webFont.category});
    info.setCreator(specimen.creator);
    info.setDescription(specimen.description);
    info.setDesigner(Utils.getDesigner(font));
    info.setDesignerUrl(Utils.getDesignerUrl(font));
    info.setFamily(webFont.family);
    info.setFontFamilyName(Utils.getFontFamilyName(font));
    info.setFontSubfamilyName(Utils.getFontSubfamilyName(font));
    info.setFullFontName(Utils.getFullFontName(font));
    info.setKind(webFont.kind);
    info.setLastModified(GoogleFonts.getTimestamp(webFont));
    info.setLicenseDescription(Utils.getLicenseDescription(font));
    info.setLicenseUrl(Utils.getLicenseUrl(font));
    info.setManufacturer(Utils.getManufacturerName(font));
    info.setPostScriptName(Utils.getPostscriptName(font));
    info.setSize(font.digest().length);
    info.setSubsets(webFont.subsets);
    info.setTrademark(Utils.getTrademark(font));
    info.setVarient(varient);
    info.setVendorUrl(Utils.getVendorUrl(font));
    info.setVersionString(Utils.getVersionName(font));
    info.setWeightClass(Utils.getWeightClass(font));
    return info;
  }

}

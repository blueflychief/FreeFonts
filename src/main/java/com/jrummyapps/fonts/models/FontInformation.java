/*
 * Copyright (C) 2016. JRummy Apps, Inc. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 *
 * File created on 3/23/16 1:35 PM by Jared Rummler.
 */

package com.jrummyapps.fonts.models;

import com.google.gson.annotations.SerializedName;

public class FontInformation {

  private String[] subsets;

  private String[] categories;

  private String creator;

  private String varient;

  private String kind;

  private String family;

  @SerializedName("font_family_name")
  private String fontFamilyName;

  @SerializedName("full_font_name")
  private String fullFontName;

  @SerializedName("font_subfamily_name")
  private String fontSubfamilyName;

  @SerializedName("postscript_name")
  private String postScriptName;

  private String trademark;

  private String manufacturer;

  private String designer;

  @SerializedName("designer_url")
  private String designerUrl;

  @SerializedName("vendor_url")
  private String vendorUrl;

  @SerializedName("license_desciption")
  private String licenseDescription;

  @SerializedName("license_url")
  private String licenseUrl;

  @SerializedName("version_string")
  private String versionString;

  private String description;

  @SerializedName("last_modified")
  private long lastModified;

  private long size;

  @SerializedName("weight_class")
  private int weightClass;



  public int getWeightClass() {
    return weightClass;
  }

  public void setWeightClass(int weightClass) {
    this.weightClass = weightClass;
  }

  public String[] getSubsets() {
    return subsets;
  }

  public void setSubsets(String[] subsets) {
    this.subsets = subsets;
  }

  public String[] getCategories() {
    return categories;
  }

  public void setCategories(String[] categories) {
    this.categories = categories;
  }

  public String getCreator() {
    return creator;
  }

  public void setCreator(String creator) {
    this.creator = creator;
  }

  public String getVarient() {
    return varient;
  }

  public void setVarient(String varient) {
    this.varient = varient;
  }

  public String getKind() {
    return kind;
  }

  public void setKind(String kind) {
    this.kind = kind;
  }

  public String getFamily() {
    return family;
  }

  public void setFamily(String family) {
    this.family = family;
  }

  public String getFontFamilyName() {
    return fontFamilyName;
  }

  public void setFontFamilyName(String fontFamilyName) {
    this.fontFamilyName = fontFamilyName;
  }

  public String getFullFontName() {
    return fullFontName;
  }

  public void setFullFontName(String fullFontName) {
    this.fullFontName = fullFontName;
  }

  public String getFontSubfamilyName() {
    return fontSubfamilyName;
  }

  public void setFontSubfamilyName(String fontSubfamilyName) {
    this.fontSubfamilyName = fontSubfamilyName;
  }

  public String getPostScriptName() {
    return postScriptName;
  }

  public void setPostScriptName(String postScriptName) {
    this.postScriptName = postScriptName;
  }

  public String getTrademark() {
    return trademark;
  }

  public void setTrademark(String trademark) {
    this.trademark = trademark;
  }

  public String getManufacturer() {
    return manufacturer;
  }

  public void setManufacturer(String manufacturer) {
    this.manufacturer = manufacturer;
  }

  public String getDesigner() {
    return designer;
  }

  public void setDesigner(String designer) {
    this.designer = designer;
  }

  public String getDesignerUrl() {
    return designerUrl;
  }

  public void setDesignerUrl(String designerUrl) {
    this.designerUrl = designerUrl;
  }

  public String getVendorUrl() {
    return vendorUrl;
  }

  public void setVendorUrl(String vendorUrl) {
    this.vendorUrl = vendorUrl;
  }

  public String getLicenseDescription() {
    return licenseDescription;
  }

  public void setLicenseDescription(String licenseDescription) {
    this.licenseDescription = licenseDescription;
  }

  public String getLicenseUrl() {
    return licenseUrl;
  }

  public void setLicenseUrl(String licenseUrl) {
    this.licenseUrl = licenseUrl;
  }

  public String getVersionString() {
    return versionString;
  }

  public void setVersionString(String versionString) {
    this.versionString = versionString;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public long getSize() {
    return size;
  }

  public void setSize(long size) {
    this.size = size;
  }

  public long getLastModified() {
    return lastModified;
  }

  public void setLastModified(long lastModified) {
    this.lastModified = lastModified;
  }

}

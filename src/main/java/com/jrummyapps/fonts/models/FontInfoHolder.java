/*
 * Copyright (C) 2016. JRummy Apps, Inc. - All Rights Reserved
 *
 * Unauthorized copying or redistribution of this file in source and binary forms via any medium
 * is strictly prohibited.
 *
 * File created on 3/24/16 6:13 AM by Jared Rummler.
 */

package com.jrummyapps.fonts.models;

import com.google.gson.annotations.SerializedName;

public class FontInfoHolder {

  public String name;

  public String folder;

  public String[] variants;

  public String[] subsets;

  public String[] categories;

  @SerializedName("last_modified")
  public Long lastModified;

}

package com.amentrix.evilbook.nametag;

class TeamInfo
{
  private String name;
  private String prefix;
  private String suffix;

  TeamInfo(String name)
  {
    this.name = name;
  }

  void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  void setSuffix(String suffix) {
    this.suffix = suffix;
  }

  String getPrefix() {
    return this.prefix;
  }

  String getSuffix() {
    return this.suffix;
  }

  String getName() {
    return this.name;
  }
}
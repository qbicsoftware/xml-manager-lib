package life.qbic.xml.loci;

import java.util.List;

public class GeneLocus {

  private String label;
  private List<String> alleles;

  public GeneLocus(String name, List<String> alleles) {
    this.label = name;
    this.alleles = alleles;
  }

  public List<String> getAlleles() {
    return alleles;
  }

  public String getLabel() {
    return label;
  }

}

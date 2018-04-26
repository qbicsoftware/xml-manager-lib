package life.qbic.xml.properties;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "unit")
@XmlEnum
public enum Unit {
  @XmlEnumValue("s")
  Second("s"),
  @XmlEnumValue("min")
  Minute("min"),
  @XmlEnumValue("g")
  Hour("h"),
  @XmlEnumValue("d")
  Day("d"),
  @XmlEnumValue("pg")
  Picogram("pg"),
  @XmlEnumValue("ng")
  Nanogram("ng"),
  @XmlEnumValue("\u00B5" + "g")
  Microgram("\u00B5" + "g"),
  @XmlEnumValue("mg")
  Milligram("mg"),
  @XmlEnumValue("g")
  Gram("g"),
  @XmlEnumValue("kg")
  Kilogram("kg"),
  @XmlEnumValue("m")
  Meter("m"),
  @XmlEnumValue("A")
  Ampere("A"),
  @XmlEnumValue("K")
  Kelvin("K"),
  @XmlEnumValue("mol")
  Mole("mol"),
  @XmlEnumValue("cd")
  Candela("cd"),
  @XmlEnumValue("Pa")
  Pascal("Pa"),
  @XmlEnumValue("J")
  Joule("J"),
  @XmlEnumValue("W")
  Watt("W"),
  @XmlEnumValue("N")
  Newton("N"),
  @XmlEnumValue("T")
  Tesla("T"),
  @XmlEnumValue("H")
  Henry("H"),
  @XmlEnumValue("C")
  Coulomb("C"),
  @XmlEnumValue("V")
  Volt("V"),
  @XmlEnumValue("F")
  Farad("F"),
  @XmlEnumValue("S")
  Siemens("S"),
  @XmlEnumValue("wb")
  Weber("Wb"),
  @XmlEnumValue("\u2126")
  Ohm("\u2126"),
  @XmlEnumValue("Hz")
  Hertz("Hz"),
  @XmlEnumValue("lx")
  Lux("lx"),
  @XmlEnumValue("lm")
  Lumen("lm"),
  @XmlEnumValue("Bq")
  Becquerel("Bq"),
  @XmlEnumValue("Gy")
  Gray("Gy"),
  @XmlEnumValue("Sv")
  Sievert("Sv"),
  @XmlEnumValue("kat")
  Katal("kat"),
  @XmlEnumValue("\u00B5" + "l")
  Microliter("\u00B5" + "l"),
  @XmlEnumValue("ml")
  Milliliter("ml"),
  @XmlEnumValue("l")
  Liter("l"),
  @XmlEnumValue("pg/l")
  Picogram_Per_Liter("pg/l"),
  @XmlEnumValue("ng/l")
  Nanogram_Per_Liter("ng/l"),
  @XmlEnumValue("\u00B5" + "g/l")
  Microgram_Per_Liter("\u00B5" + "g/l"),
  @XmlEnumValue("mg/l")
  Milligram_Per_Liter("mg/l"),
  @XmlEnumValue("g/l")
  Gram_Per_Liter("g/l"),
  @XmlEnumValue("pmol/l")
  Picomol_Per_Liter("pmol/l"),
  @XmlEnumValue("nmol/l")
  Nanomol_Per_Liter("nmol/l"),
  @XmlEnumValue("\u00B5"+ "mol/l")
  Micromol_Per_Liter("\u00B5"+ "mol/l"),
  @XmlEnumValue("mmol/l")
  Millimol_Per_Liter("mmol/l"),
  @XmlEnumValue("mol/l")
  Mol_Per_Liter("mol/l"),
  @XmlEnumValue("arb.unit")
  Arbitrary_Unit("arb.unit");
  
  private String value;

  private Unit(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

}
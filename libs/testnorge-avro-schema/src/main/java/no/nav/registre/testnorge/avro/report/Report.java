/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package no.nav.registre.testnorge.avro.report;

import org.apache.avro.generic.GenericArray;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.util.Utf8;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

@org.apache.avro.specific.AvroGenerated
public class Report extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = 2025866384125327381L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"Report\",\"namespace\":\"no.nav.registre.testnorge.avro.report\",\"fields\":[{\"name\":\"applicationName\",\"type\":\"string\"},{\"name\":\"name\",\"type\":\"string\"},{\"name\":\"start\",\"type\":\"string\"},{\"name\":\"end\",\"type\":\"string\"},{\"name\":\"entries\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"Entry\",\"fields\":[{\"name\":\"status\",\"type\":\"string\"},{\"name\":\"description\",\"type\":\"string\"}]}}}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<Report> ENCODER =
      new BinaryMessageEncoder<Report>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<Report> DECODER =
      new BinaryMessageDecoder<Report>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<Report> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<Report> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<Report> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<Report>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this Report to a ByteBuffer.
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a Report from a ByteBuffer.
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a Report instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
   */
  public static Report fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

   private java.lang.CharSequence applicationName;
   private java.lang.CharSequence name;
   private java.lang.CharSequence start;
   private java.lang.CharSequence end;
   private java.util.List<no.nav.registre.testnorge.avro.report.Entry> entries;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public Report() {}

  /**
   * All-args constructor.
   * @param applicationName The new value for applicationName
   * @param name The new value for name
   * @param start The new value for start
   * @param end The new value for end
   * @param entries The new value for entries
   */
  public Report(java.lang.CharSequence applicationName, java.lang.CharSequence name, java.lang.CharSequence start, java.lang.CharSequence end, java.util.List<no.nav.registre.testnorge.avro.report.Entry> entries) {
    this.applicationName = applicationName;
    this.name = name;
    this.start = start;
    this.end = end;
    this.entries = entries;
  }

  public org.apache.avro.specific.SpecificData getSpecificData() { return MODEL$; }
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return applicationName;
    case 1: return name;
    case 2: return start;
    case 3: return end;
    case 4: return entries;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: applicationName = (java.lang.CharSequence)value$; break;
    case 1: name = (java.lang.CharSequence)value$; break;
    case 2: start = (java.lang.CharSequence)value$; break;
    case 3: end = (java.lang.CharSequence)value$; break;
    case 4: entries = (java.util.List<no.nav.registre.testnorge.avro.report.Entry>)value$; break;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  /**
   * Gets the value of the 'applicationName' field.
   * @return The value of the 'applicationName' field.
   */
  public java.lang.CharSequence getApplicationName() {
    return applicationName;
  }


  /**
   * Sets the value of the 'applicationName' field.
   * @param value the value to set.
   */
  public void setApplicationName(java.lang.CharSequence value) {
    this.applicationName = value;
  }

  /**
   * Gets the value of the 'name' field.
   * @return The value of the 'name' field.
   */
  public java.lang.CharSequence getName() {
    return name;
  }


  /**
   * Sets the value of the 'name' field.
   * @param value the value to set.
   */
  public void setName(java.lang.CharSequence value) {
    this.name = value;
  }

  /**
   * Gets the value of the 'start' field.
   * @return The value of the 'start' field.
   */
  public java.lang.CharSequence getStart() {
    return start;
  }


  /**
   * Sets the value of the 'start' field.
   * @param value the value to set.
   */
  public void setStart(java.lang.CharSequence value) {
    this.start = value;
  }

  /**
   * Gets the value of the 'end' field.
   * @return The value of the 'end' field.
   */
  public java.lang.CharSequence getEnd() {
    return end;
  }


  /**
   * Sets the value of the 'end' field.
   * @param value the value to set.
   */
  public void setEnd(java.lang.CharSequence value) {
    this.end = value;
  }

  /**
   * Gets the value of the 'entries' field.
   * @return The value of the 'entries' field.
   */
  public java.util.List<no.nav.registre.testnorge.avro.report.Entry> getEntries() {
    return entries;
  }


  /**
   * Sets the value of the 'entries' field.
   * @param value the value to set.
   */
  public void setEntries(java.util.List<no.nav.registre.testnorge.avro.report.Entry> value) {
    this.entries = value;
  }

  /**
   * Creates a new Report RecordBuilder.
   * @return A new Report RecordBuilder
   */
  public static no.nav.registre.testnorge.avro.report.Report.Builder newBuilder() {
    return new no.nav.registre.testnorge.avro.report.Report.Builder();
  }

  /**
   * Creates a new Report RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new Report RecordBuilder
   */
  public static no.nav.registre.testnorge.avro.report.Report.Builder newBuilder(no.nav.registre.testnorge.avro.report.Report.Builder other) {
    if (other == null) {
      return new no.nav.registre.testnorge.avro.report.Report.Builder();
    } else {
      return new no.nav.registre.testnorge.avro.report.Report.Builder(other);
    }
  }

  /**
   * Creates a new Report RecordBuilder by copying an existing Report instance.
   * @param other The existing instance to copy.
   * @return A new Report RecordBuilder
   */
  public static no.nav.registre.testnorge.avro.report.Report.Builder newBuilder(no.nav.registre.testnorge.avro.report.Report other) {
    if (other == null) {
      return new no.nav.registre.testnorge.avro.report.Report.Builder();
    } else {
      return new no.nav.registre.testnorge.avro.report.Report.Builder(other);
    }
  }

  /**
   * RecordBuilder for Report instances.
   */
  @org.apache.avro.specific.AvroGenerated
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<Report>
    implements org.apache.avro.data.RecordBuilder<Report> {

    private java.lang.CharSequence applicationName;
    private java.lang.CharSequence name;
    private java.lang.CharSequence start;
    private java.lang.CharSequence end;
    private java.util.List<no.nav.registre.testnorge.avro.report.Entry> entries;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(no.nav.registre.testnorge.avro.report.Report.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.applicationName)) {
        this.applicationName = data().deepCopy(fields()[0].schema(), other.applicationName);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
      if (isValidValue(fields()[1], other.name)) {
        this.name = data().deepCopy(fields()[1].schema(), other.name);
        fieldSetFlags()[1] = other.fieldSetFlags()[1];
      }
      if (isValidValue(fields()[2], other.start)) {
        this.start = data().deepCopy(fields()[2].schema(), other.start);
        fieldSetFlags()[2] = other.fieldSetFlags()[2];
      }
      if (isValidValue(fields()[3], other.end)) {
        this.end = data().deepCopy(fields()[3].schema(), other.end);
        fieldSetFlags()[3] = other.fieldSetFlags()[3];
      }
      if (isValidValue(fields()[4], other.entries)) {
        this.entries = data().deepCopy(fields()[4].schema(), other.entries);
        fieldSetFlags()[4] = other.fieldSetFlags()[4];
      }
    }

    /**
     * Creates a Builder by copying an existing Report instance
     * @param other The existing instance to copy.
     */
    private Builder(no.nav.registre.testnorge.avro.report.Report other) {
      super(SCHEMA$);
      if (isValidValue(fields()[0], other.applicationName)) {
        this.applicationName = data().deepCopy(fields()[0].schema(), other.applicationName);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.name)) {
        this.name = data().deepCopy(fields()[1].schema(), other.name);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.start)) {
        this.start = data().deepCopy(fields()[2].schema(), other.start);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.end)) {
        this.end = data().deepCopy(fields()[3].schema(), other.end);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.entries)) {
        this.entries = data().deepCopy(fields()[4].schema(), other.entries);
        fieldSetFlags()[4] = true;
      }
    }

    /**
      * Gets the value of the 'applicationName' field.
      * @return The value.
      */
    public java.lang.CharSequence getApplicationName() {
      return applicationName;
    }


    /**
      * Sets the value of the 'applicationName' field.
      * @param value The value of 'applicationName'.
      * @return This builder.
      */
    public no.nav.registre.testnorge.avro.report.Report.Builder setApplicationName(java.lang.CharSequence value) {
      validate(fields()[0], value);
      this.applicationName = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'applicationName' field has been set.
      * @return True if the 'applicationName' field has been set, false otherwise.
      */
    public boolean hasApplicationName() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'applicationName' field.
      * @return This builder.
      */
    public no.nav.registre.testnorge.avro.report.Report.Builder clearApplicationName() {
      applicationName = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'name' field.
      * @return The value.
      */
    public java.lang.CharSequence getName() {
      return name;
    }


    /**
      * Sets the value of the 'name' field.
      * @param value The value of 'name'.
      * @return This builder.
      */
    public no.nav.registre.testnorge.avro.report.Report.Builder setName(java.lang.CharSequence value) {
      validate(fields()[1], value);
      this.name = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'name' field has been set.
      * @return True if the 'name' field has been set, false otherwise.
      */
    public boolean hasName() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'name' field.
      * @return This builder.
      */
    public no.nav.registre.testnorge.avro.report.Report.Builder clearName() {
      name = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /**
      * Gets the value of the 'start' field.
      * @return The value.
      */
    public java.lang.CharSequence getStart() {
      return start;
    }


    /**
      * Sets the value of the 'start' field.
      * @param value The value of 'start'.
      * @return This builder.
      */
    public no.nav.registre.testnorge.avro.report.Report.Builder setStart(java.lang.CharSequence value) {
      validate(fields()[2], value);
      this.start = value;
      fieldSetFlags()[2] = true;
      return this;
    }

    /**
      * Checks whether the 'start' field has been set.
      * @return True if the 'start' field has been set, false otherwise.
      */
    public boolean hasStart() {
      return fieldSetFlags()[2];
    }


    /**
      * Clears the value of the 'start' field.
      * @return This builder.
      */
    public no.nav.registre.testnorge.avro.report.Report.Builder clearStart() {
      start = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    /**
      * Gets the value of the 'end' field.
      * @return The value.
      */
    public java.lang.CharSequence getEnd() {
      return end;
    }


    /**
      * Sets the value of the 'end' field.
      * @param value The value of 'end'.
      * @return This builder.
      */
    public no.nav.registre.testnorge.avro.report.Report.Builder setEnd(java.lang.CharSequence value) {
      validate(fields()[3], value);
      this.end = value;
      fieldSetFlags()[3] = true;
      return this;
    }

    /**
      * Checks whether the 'end' field has been set.
      * @return True if the 'end' field has been set, false otherwise.
      */
    public boolean hasEnd() {
      return fieldSetFlags()[3];
    }


    /**
      * Clears the value of the 'end' field.
      * @return This builder.
      */
    public no.nav.registre.testnorge.avro.report.Report.Builder clearEnd() {
      end = null;
      fieldSetFlags()[3] = false;
      return this;
    }

    /**
      * Gets the value of the 'entries' field.
      * @return The value.
      */
    public java.util.List<no.nav.registre.testnorge.avro.report.Entry> getEntries() {
      return entries;
    }


    /**
      * Sets the value of the 'entries' field.
      * @param value The value of 'entries'.
      * @return This builder.
      */
    public no.nav.registre.testnorge.avro.report.Report.Builder setEntries(java.util.List<no.nav.registre.testnorge.avro.report.Entry> value) {
      validate(fields()[4], value);
      this.entries = value;
      fieldSetFlags()[4] = true;
      return this;
    }

    /**
      * Checks whether the 'entries' field has been set.
      * @return True if the 'entries' field has been set, false otherwise.
      */
    public boolean hasEntries() {
      return fieldSetFlags()[4];
    }


    /**
      * Clears the value of the 'entries' field.
      * @return This builder.
      */
    public no.nav.registre.testnorge.avro.report.Report.Builder clearEntries() {
      entries = null;
      fieldSetFlags()[4] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Report build() {
      try {
        Report record = new Report();
        record.applicationName = fieldSetFlags()[0] ? this.applicationName : (java.lang.CharSequence) defaultValue(fields()[0]);
        record.name = fieldSetFlags()[1] ? this.name : (java.lang.CharSequence) defaultValue(fields()[1]);
        record.start = fieldSetFlags()[2] ? this.start : (java.lang.CharSequence) defaultValue(fields()[2]);
        record.end = fieldSetFlags()[3] ? this.end : (java.lang.CharSequence) defaultValue(fields()[3]);
        record.entries = fieldSetFlags()[4] ? this.entries : (java.util.List<no.nav.registre.testnorge.avro.report.Entry>) defaultValue(fields()[4]);
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<Report>
    WRITER$ = (org.apache.avro.io.DatumWriter<Report>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<Report>
    READER$ = (org.apache.avro.io.DatumReader<Report>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

  @Override protected boolean hasCustomCoders() { return true; }

  @Override public void customEncode(org.apache.avro.io.Encoder out)
    throws java.io.IOException
  {
    out.writeString(this.applicationName);

    out.writeString(this.name);

    out.writeString(this.start);

    out.writeString(this.end);

    long size0 = this.entries.size();
    out.writeArrayStart();
    out.setItemCount(size0);
    long actualSize0 = 0;
    for (no.nav.registre.testnorge.avro.report.Entry e0: this.entries) {
      actualSize0++;
      out.startItem();
      e0.customEncode(out);
    }
    out.writeArrayEnd();
    if (actualSize0 != size0)
      throw new java.util.ConcurrentModificationException("Array-size written was " + size0 + ", but element count was " + actualSize0 + ".");

  }

  @Override public void customDecode(org.apache.avro.io.ResolvingDecoder in)
    throws java.io.IOException
  {
    org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
    if (fieldOrder == null) {
      this.applicationName = in.readString(this.applicationName instanceof Utf8 ? (Utf8)this.applicationName : null);

      this.name = in.readString(this.name instanceof Utf8 ? (Utf8)this.name : null);

      this.start = in.readString(this.start instanceof Utf8 ? (Utf8)this.start : null);

      this.end = in.readString(this.end instanceof Utf8 ? (Utf8)this.end : null);

      long size0 = in.readArrayStart();
      java.util.List<no.nav.registre.testnorge.avro.report.Entry> a0 = this.entries;
      if (a0 == null) {
        a0 = new SpecificData.Array<no.nav.registre.testnorge.avro.report.Entry>((int)size0, SCHEMA$.getField("entries").schema());
        this.entries = a0;
      } else a0.clear();
      SpecificData.Array<no.nav.registre.testnorge.avro.report.Entry> ga0 = (a0 instanceof SpecificData.Array ? (SpecificData.Array<no.nav.registre.testnorge.avro.report.Entry>)a0 : null);
      for ( ; 0 < size0; size0 = in.arrayNext()) {
        for ( ; size0 != 0; size0--) {
          no.nav.registre.testnorge.avro.report.Entry e0 = (ga0 != null ? ga0.peek() : null);
          if (e0 == null) {
            e0 = new no.nav.registre.testnorge.avro.report.Entry();
          }
          e0.customDecode(in);
          a0.add(e0);
        }
      }

    } else {
      for (int i = 0; i < 5; i++) {
        switch (fieldOrder[i].pos()) {
        case 0:
          this.applicationName = in.readString(this.applicationName instanceof Utf8 ? (Utf8)this.applicationName : null);
          break;

        case 1:
          this.name = in.readString(this.name instanceof Utf8 ? (Utf8)this.name : null);
          break;

        case 2:
          this.start = in.readString(this.start instanceof Utf8 ? (Utf8)this.start : null);
          break;

        case 3:
          this.end = in.readString(this.end instanceof Utf8 ? (Utf8)this.end : null);
          break;

        case 4:
          long size0 = in.readArrayStart();
          java.util.List<no.nav.registre.testnorge.avro.report.Entry> a0 = this.entries;
          if (a0 == null) {
            a0 = new SpecificData.Array<no.nav.registre.testnorge.avro.report.Entry>((int)size0, SCHEMA$.getField("entries").schema());
            this.entries = a0;
          } else a0.clear();
          SpecificData.Array<no.nav.registre.testnorge.avro.report.Entry> ga0 = (a0 instanceof SpecificData.Array ? (SpecificData.Array<no.nav.registre.testnorge.avro.report.Entry>)a0 : null);
          for ( ; 0 < size0; size0 = in.arrayNext()) {
            for ( ; size0 != 0; size0--) {
              no.nav.registre.testnorge.avro.report.Entry e0 = (ga0 != null ? ga0.peek() : null);
              if (e0 == null) {
                e0 = new no.nav.registre.testnorge.avro.report.Entry();
              }
              e0.customDecode(in);
              a0.add(e0);
            }
          }
          break;

        default:
          throw new java.io.IOException("Corrupt ResolvingDecoder.");
        }
      }
    }
  }
}











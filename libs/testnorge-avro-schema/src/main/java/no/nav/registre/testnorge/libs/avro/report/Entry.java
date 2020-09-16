/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package no.nav.registre.testnorge.libs.avro.report;

import org.apache.avro.generic.GenericArray;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.util.Utf8;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

@org.apache.avro.specific.AvroGenerated
public class Entry extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = 128894877207162283L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"Entry\",\"namespace\":\"no.nav.registre.testnorge.libs.avro.report\",\"fields\":[{\"name\":\"status\",\"type\":\"string\"},{\"name\":\"description\",\"type\":\"string\"},{\"name\":\"time\",\"type\":\"string\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<Entry> ENCODER =
      new BinaryMessageEncoder<Entry>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<Entry> DECODER =
      new BinaryMessageDecoder<Entry>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<Entry> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<Entry> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<Entry> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<Entry>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this Entry to a ByteBuffer.
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a Entry from a ByteBuffer.
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a Entry instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
   */
  public static Entry fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

   private java.lang.CharSequence status;
   private java.lang.CharSequence description;
   private java.lang.CharSequence time;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public Entry() {}

  /**
   * All-args constructor.
   * @param status The new value for status
   * @param description The new value for description
   * @param time The new value for time
   */
  public Entry(java.lang.CharSequence status, java.lang.CharSequence description, java.lang.CharSequence time) {
    this.status = status;
    this.description = description;
    this.time = time;
  }

  public org.apache.avro.specific.SpecificData getSpecificData() { return MODEL$; }
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return status;
    case 1: return description;
    case 2: return time;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: status = (java.lang.CharSequence)value$; break;
    case 1: description = (java.lang.CharSequence)value$; break;
    case 2: time = (java.lang.CharSequence)value$; break;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  /**
   * Gets the value of the 'status' field.
   * @return The value of the 'status' field.
   */
  public java.lang.CharSequence getStatus() {
    return status;
  }


  /**
   * Sets the value of the 'status' field.
   * @param value the value to set.
   */
  public void setStatus(java.lang.CharSequence value) {
    this.status = value;
  }

  /**
   * Gets the value of the 'description' field.
   * @return The value of the 'description' field.
   */
  public java.lang.CharSequence getDescription() {
    return description;
  }


  /**
   * Sets the value of the 'description' field.
   * @param value the value to set.
   */
  public void setDescription(java.lang.CharSequence value) {
    this.description = value;
  }

  /**
   * Gets the value of the 'time' field.
   * @return The value of the 'time' field.
   */
  public java.lang.CharSequence getTime() {
    return time;
  }


  /**
   * Sets the value of the 'time' field.
   * @param value the value to set.
   */
  public void setTime(java.lang.CharSequence value) {
    this.time = value;
  }

  /**
   * Creates a new Entry RecordBuilder.
   * @return A new Entry RecordBuilder
   */
  public static no.nav.registre.testnorge.libs.avro.report.Entry.Builder newBuilder() {
    return new no.nav.registre.testnorge.libs.avro.report.Entry.Builder();
  }

  /**
   * Creates a new Entry RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new Entry RecordBuilder
   */
  public static no.nav.registre.testnorge.libs.avro.report.Entry.Builder newBuilder(no.nav.registre.testnorge.libs.avro.report.Entry.Builder other) {
    if (other == null) {
      return new no.nav.registre.testnorge.libs.avro.report.Entry.Builder();
    } else {
      return new no.nav.registre.testnorge.libs.avro.report.Entry.Builder(other);
    }
  }

  /**
   * Creates a new Entry RecordBuilder by copying an existing Entry instance.
   * @param other The existing instance to copy.
   * @return A new Entry RecordBuilder
   */
  public static no.nav.registre.testnorge.libs.avro.report.Entry.Builder newBuilder(no.nav.registre.testnorge.libs.avro.report.Entry other) {
    if (other == null) {
      return new no.nav.registre.testnorge.libs.avro.report.Entry.Builder();
    } else {
      return new no.nav.registre.testnorge.libs.avro.report.Entry.Builder(other);
    }
  }

  /**
   * RecordBuilder for Entry instances.
   */
  @org.apache.avro.specific.AvroGenerated
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<Entry>
    implements org.apache.avro.data.RecordBuilder<Entry> {

    private java.lang.CharSequence status;
    private java.lang.CharSequence description;
    private java.lang.CharSequence time;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(no.nav.registre.testnorge.libs.avro.report.Entry.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.status)) {
        this.status = data().deepCopy(fields()[0].schema(), other.status);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
      if (isValidValue(fields()[1], other.description)) {
        this.description = data().deepCopy(fields()[1].schema(), other.description);
        fieldSetFlags()[1] = other.fieldSetFlags()[1];
      }
      if (isValidValue(fields()[2], other.time)) {
        this.time = data().deepCopy(fields()[2].schema(), other.time);
        fieldSetFlags()[2] = other.fieldSetFlags()[2];
      }
    }

    /**
     * Creates a Builder by copying an existing Entry instance
     * @param other The existing instance to copy.
     */
    private Builder(no.nav.registre.testnorge.libs.avro.report.Entry other) {
      super(SCHEMA$);
      if (isValidValue(fields()[0], other.status)) {
        this.status = data().deepCopy(fields()[0].schema(), other.status);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.description)) {
        this.description = data().deepCopy(fields()[1].schema(), other.description);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.time)) {
        this.time = data().deepCopy(fields()[2].schema(), other.time);
        fieldSetFlags()[2] = true;
      }
    }

    /**
      * Gets the value of the 'status' field.
      * @return The value.
      */
    public java.lang.CharSequence getStatus() {
      return status;
    }


    /**
      * Sets the value of the 'status' field.
      * @param value The value of 'status'.
      * @return This builder.
      */
    public no.nav.registre.testnorge.libs.avro.report.Entry.Builder setStatus(java.lang.CharSequence value) {
      validate(fields()[0], value);
      this.status = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'status' field has been set.
      * @return True if the 'status' field has been set, false otherwise.
      */
    public boolean hasStatus() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'status' field.
      * @return This builder.
      */
    public no.nav.registre.testnorge.libs.avro.report.Entry.Builder clearStatus() {
      status = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'description' field.
      * @return The value.
      */
    public java.lang.CharSequence getDescription() {
      return description;
    }


    /**
      * Sets the value of the 'description' field.
      * @param value The value of 'description'.
      * @return This builder.
      */
    public no.nav.registre.testnorge.libs.avro.report.Entry.Builder setDescription(java.lang.CharSequence value) {
      validate(fields()[1], value);
      this.description = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'description' field has been set.
      * @return True if the 'description' field has been set, false otherwise.
      */
    public boolean hasDescription() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'description' field.
      * @return This builder.
      */
    public no.nav.registre.testnorge.libs.avro.report.Entry.Builder clearDescription() {
      description = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /**
      * Gets the value of the 'time' field.
      * @return The value.
      */
    public java.lang.CharSequence getTime() {
      return time;
    }


    /**
      * Sets the value of the 'time' field.
      * @param value The value of 'time'.
      * @return This builder.
      */
    public no.nav.registre.testnorge.libs.avro.report.Entry.Builder setTime(java.lang.CharSequence value) {
      validate(fields()[2], value);
      this.time = value;
      fieldSetFlags()[2] = true;
      return this;
    }

    /**
      * Checks whether the 'time' field has been set.
      * @return True if the 'time' field has been set, false otherwise.
      */
    public boolean hasTime() {
      return fieldSetFlags()[2];
    }


    /**
      * Clears the value of the 'time' field.
      * @return This builder.
      */
    public no.nav.registre.testnorge.libs.avro.report.Entry.Builder clearTime() {
      time = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Entry build() {
      try {
        Entry record = new Entry();
        record.status = fieldSetFlags()[0] ? this.status : (java.lang.CharSequence) defaultValue(fields()[0]);
        record.description = fieldSetFlags()[1] ? this.description : (java.lang.CharSequence) defaultValue(fields()[1]);
        record.time = fieldSetFlags()[2] ? this.time : (java.lang.CharSequence) defaultValue(fields()[2]);
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<Entry>
    WRITER$ = (org.apache.avro.io.DatumWriter<Entry>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<Entry>
    READER$ = (org.apache.avro.io.DatumReader<Entry>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

  @Override protected boolean hasCustomCoders() { return true; }

  @Override public void customEncode(org.apache.avro.io.Encoder out)
    throws java.io.IOException
  {
    out.writeString(this.status);

    out.writeString(this.description);

    out.writeString(this.time);

  }

  @Override public void customDecode(org.apache.avro.io.ResolvingDecoder in)
    throws java.io.IOException
  {
    org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
    if (fieldOrder == null) {
      this.status = in.readString(this.status instanceof Utf8 ? (Utf8)this.status : null);

      this.description = in.readString(this.description instanceof Utf8 ? (Utf8)this.description : null);

      this.time = in.readString(this.time instanceof Utf8 ? (Utf8)this.time : null);

    } else {
      for (int i = 0; i < 3; i++) {
        switch (fieldOrder[i].pos()) {
        case 0:
          this.status = in.readString(this.status instanceof Utf8 ? (Utf8)this.status : null);
          break;

        case 1:
          this.description = in.readString(this.description instanceof Utf8 ? (Utf8)this.description : null);
          break;

        case 2:
          this.time = in.readString(this.time instanceof Utf8 ? (Utf8)this.time : null);
          break;

        default:
          throw new java.io.IOException("Corrupt ResolvingDecoder.");
        }
      }
    }
  }
}











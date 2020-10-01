/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package no.nav.registre.testnorge.libs.avro.personinfo;

import org.apache.avro.generic.GenericArray;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.util.Utf8;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

@org.apache.avro.specific.AvroGenerated
public class PersonInfo extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = 2209788691648612559L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"PersonInfo\",\"namespace\":\"no.nav.registre.testnorge.libs.avro.personinfo\",\"fields\":[{\"name\":\"objectId\",\"type\":\"string\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<PersonInfo> ENCODER =
      new BinaryMessageEncoder<PersonInfo>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<PersonInfo> DECODER =
      new BinaryMessageDecoder<PersonInfo>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<PersonInfo> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<PersonInfo> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<PersonInfo> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<PersonInfo>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this PersonInfo to a ByteBuffer.
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a PersonInfo from a ByteBuffer.
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a PersonInfo instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
   */
  public static PersonInfo fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

   private java.lang.CharSequence objectId;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public PersonInfo() {}

  /**
   * All-args constructor.
   * @param objectId The new value for objectId
   */
  public PersonInfo(java.lang.CharSequence objectId) {
    this.objectId = objectId;
  }

  public org.apache.avro.specific.SpecificData getSpecificData() { return MODEL$; }
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return objectId;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: objectId = (java.lang.CharSequence)value$; break;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  /**
   * Gets the value of the 'objectId' field.
   * @return The value of the 'objectId' field.
   */
  public java.lang.CharSequence getObjectId() {
    return objectId;
  }


  /**
   * Sets the value of the 'objectId' field.
   * @param value the value to set.
   */
  public void setObjectId(java.lang.CharSequence value) {
    this.objectId = value;
  }

  /**
   * Creates a new PersonInfo RecordBuilder.
   * @return A new PersonInfo RecordBuilder
   */
  public static no.nav.registre.testnorge.libs.avro.personinfo.PersonInfo.Builder newBuilder() {
    return new no.nav.registre.testnorge.libs.avro.personinfo.PersonInfo.Builder();
  }

  /**
   * Creates a new PersonInfo RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new PersonInfo RecordBuilder
   */
  public static no.nav.registre.testnorge.libs.avro.personinfo.PersonInfo.Builder newBuilder(no.nav.registre.testnorge.libs.avro.personinfo.PersonInfo.Builder other) {
    if (other == null) {
      return new no.nav.registre.testnorge.libs.avro.personinfo.PersonInfo.Builder();
    } else {
      return new no.nav.registre.testnorge.libs.avro.personinfo.PersonInfo.Builder(other);
    }
  }

  /**
   * Creates a new PersonInfo RecordBuilder by copying an existing PersonInfo instance.
   * @param other The existing instance to copy.
   * @return A new PersonInfo RecordBuilder
   */
  public static no.nav.registre.testnorge.libs.avro.personinfo.PersonInfo.Builder newBuilder(no.nav.registre.testnorge.libs.avro.personinfo.PersonInfo other) {
    if (other == null) {
      return new no.nav.registre.testnorge.libs.avro.personinfo.PersonInfo.Builder();
    } else {
      return new no.nav.registre.testnorge.libs.avro.personinfo.PersonInfo.Builder(other);
    }
  }

  /**
   * RecordBuilder for PersonInfo instances.
   */
  @org.apache.avro.specific.AvroGenerated
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<PersonInfo>
    implements org.apache.avro.data.RecordBuilder<PersonInfo> {

    private java.lang.CharSequence objectId;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(no.nav.registre.testnorge.libs.avro.personinfo.PersonInfo.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.objectId)) {
        this.objectId = data().deepCopy(fields()[0].schema(), other.objectId);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
    }

    /**
     * Creates a Builder by copying an existing PersonInfo instance
     * @param other The existing instance to copy.
     */
    private Builder(no.nav.registre.testnorge.libs.avro.personinfo.PersonInfo other) {
      super(SCHEMA$);
      if (isValidValue(fields()[0], other.objectId)) {
        this.objectId = data().deepCopy(fields()[0].schema(), other.objectId);
        fieldSetFlags()[0] = true;
      }
    }

    /**
      * Gets the value of the 'objectId' field.
      * @return The value.
      */
    public java.lang.CharSequence getObjectId() {
      return objectId;
    }


    /**
      * Sets the value of the 'objectId' field.
      * @param value The value of 'objectId'.
      * @return This builder.
      */
    public no.nav.registre.testnorge.libs.avro.personinfo.PersonInfo.Builder setObjectId(java.lang.CharSequence value) {
      validate(fields()[0], value);
      this.objectId = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'objectId' field has been set.
      * @return True if the 'objectId' field has been set, false otherwise.
      */
    public boolean hasObjectId() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'objectId' field.
      * @return This builder.
      */
    public no.nav.registre.testnorge.libs.avro.personinfo.PersonInfo.Builder clearObjectId() {
      objectId = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public PersonInfo build() {
      try {
        PersonInfo record = new PersonInfo();
        record.objectId = fieldSetFlags()[0] ? this.objectId : (java.lang.CharSequence) defaultValue(fields()[0]);
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<PersonInfo>
    WRITER$ = (org.apache.avro.io.DatumWriter<PersonInfo>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<PersonInfo>
    READER$ = (org.apache.avro.io.DatumReader<PersonInfo>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

  @Override protected boolean hasCustomCoders() { return true; }

  @Override public void customEncode(org.apache.avro.io.Encoder out)
    throws java.io.IOException
  {
    out.writeString(this.objectId);

  }

  @Override public void customDecode(org.apache.avro.io.ResolvingDecoder in)
    throws java.io.IOException
  {
    org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
    if (fieldOrder == null) {
      this.objectId = in.readString(this.objectId instanceof Utf8 ? (Utf8)this.objectId : null);

    } else {
      for (int i = 0; i < 1; i++) {
        switch (fieldOrder[i].pos()) {
        case 0:
          this.objectId = in.readString(this.objectId instanceof Utf8 ? (Utf8)this.objectId : null);
          break;

        default:
          throw new java.io.IOException("Corrupt ResolvingDecoder.");
        }
      }
    }
  }
}











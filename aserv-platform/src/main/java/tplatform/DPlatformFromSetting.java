/**
 * Autogenerated by Thrift Compiler (0.9.3)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package tplatform;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;
import org.apache.thrift.scheme.TupleScheme;
import javax.annotation.Generated;
import java.util.*;

@SuppressWarnings({ "cast", "rawtypes", "serial", "unchecked" })
@Generated(value = "Autogenerated by Thrift Compiler (0.9.3)", date = "2017-01-11")
public class DPlatformFromSetting implements org.apache.thrift.TBase<DPlatformFromSetting, DPlatformFromSetting._Fields>, java.io.Serializable, Cloneable, Comparable<DPlatformFromSetting> {

    private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("DPlatformFromSetting");

    private static final org.apache.thrift.protocol.TField FROM_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("fromId", org.apache.thrift.protocol.TType.I32, (short) 1);

    private static final org.apache.thrift.protocol.TField REVIEW_FIELD_DESC = new org.apache.thrift.protocol.TField("review", org.apache.thrift.protocol.TType.BOOL, (short) 2);

    private static final org.apache.thrift.protocol.TField SETTING_FIELD_DESC = new org.apache.thrift.protocol.TField("setting", org.apache.thrift.protocol.TType.STRUCT, (short) 3);

    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();

    // required
    protected int fromId;

    // required
    protected boolean review;

    // optional
    protected DFromSetting setting;

    // isset id assignments
    private static final int __FROMID_ISSET_ID = 0;

    private static final int __REVIEW_ISSET_ID = 1;

    protected byte __isset_bitfield = 0;

    private static final _Fields optionals[] = { _Fields.SETTING };

    public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;

    static {
        schemes.put(StandardScheme.class, new DPlatformFromSettingStandardSchemeFactory());
        schemes.put(TupleScheme.class, new DPlatformFromSettingTupleSchemeFactory());
    }

    /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
    public enum _Fields implements org.apache.thrift.TFieldIdEnum {

        FROM_ID((short) 1, "fromId"), REVIEW((short) 2, "review"), SETTING((short) 3, "setting");

        private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

        static {
            for (_Fields field : EnumSet.allOf(_Fields.class)) {
                byName.put(field.getFieldName(), field);
            }
        }

        /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
        public static _Fields findByThriftId(int fieldId) {
            switch(fieldId) {
                case // FROM_ID
                1:
                    return FROM_ID;
                case // REVIEW
                2:
                    return REVIEW;
                case // SETTING
                3:
                    return SETTING;
                default:
                    return null;
            }
        }

        /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
        public static _Fields findByThriftIdOrThrow(int fieldId) {
            _Fields fields = findByThriftId(fieldId);
            if (fields == null)
                throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
            return fields;
        }

        /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
        public static _Fields findByName(String name) {
            return byName.get(name);
        }

        private final short _thriftId;

        private final String _fieldName;

        _Fields(short thriftId, String fieldName) {
            _thriftId = thriftId;
            _fieldName = fieldName;
        }

        public short getThriftFieldId() {
            return _thriftId;
        }

        public String getFieldName() {
            return _fieldName;
        }
    }

    public DPlatformFromSetting() {
    }

    public DPlatformFromSetting(int fromId, boolean review) {
        this();
        this.fromId = fromId;
        setFromIdIsSet(true);
        this.review = review;
        setReviewIsSet(true);
    }

    /**
   * Performs a deep copy on <i>other</i>.
   */
    public DPlatformFromSetting(DPlatformFromSetting other) {
        __isset_bitfield = other.__isset_bitfield;
        this.fromId = other.fromId;
        this.review = other.review;
        if (other.isSetSetting()) {
            this.setting = other.setting;
        }
    }

    public DPlatformFromSetting deepCopy() {
        return new DPlatformFromSetting(this);
    }

    @Override
    public void clear() {
        setFromIdIsSet(false);
        this.fromId = 0;
        setReviewIsSet(false);
        this.review = false;
        this.setting = null;
    }

    public int getFromId() {
        return this.fromId;
    }

    public DPlatformFromSetting setFromId(int fromId) {
        this.fromId = fromId;
        setFromIdIsSet(true);
        return this;
    }

    public void unsetFromId() {
        __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __FROMID_ISSET_ID);
    }

    /** Returns true if field fromId is set (has been assigned a value) and false otherwise */
    @JsonIgnore
    public boolean isSetFromId() {
        return EncodingUtils.testBit(__isset_bitfield, __FROMID_ISSET_ID);
    }

    public void setFromIdIsSet(boolean value) {
        __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __FROMID_ISSET_ID, value);
    }

    public boolean isReview() {
        return this.review;
    }

    public DPlatformFromSetting setReview(boolean review) {
        this.review = review;
        setReviewIsSet(true);
        return this;
    }

    public void unsetReview() {
        __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __REVIEW_ISSET_ID);
    }

    /** Returns true if field review is set (has been assigned a value) and false otherwise */
    @JsonIgnore
    public boolean isSetReview() {
        return EncodingUtils.testBit(__isset_bitfield, __REVIEW_ISSET_ID);
    }

    public void setReviewIsSet(boolean value) {
        __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __REVIEW_ISSET_ID, value);
    }

    public DFromSetting getSetting() {
        return this.setting;
    }

    public DPlatformFromSetting setSetting(DFromSetting setting) {
        this.setting = setting;
        return this;
    }

    public void unsetSetting() {
        this.setting = null;
    }

    /** Returns true if field setting is set (has been assigned a value) and false otherwise */
    @JsonIgnore
    public boolean isSetSetting() {
        return this.setting != null;
    }

    public void setSettingIsSet(boolean value) {
        if (!value) {
            this.setting = null;
        }
    }

    public void setFieldValue(_Fields field, Object value) {
        switch(field) {
            case FROM_ID:
                if (value == null) {
                    unsetFromId();
                } else {
                    setFromId((Integer) value);
                }
                break;
            case REVIEW:
                if (value == null) {
                    unsetReview();
                } else {
                    setReview((Boolean) value);
                }
                break;
            case SETTING:
                if (value == null) {
                    unsetSetting();
                } else {
                    setSetting((DFromSetting) value);
                }
                break;
        }
    }

    public Object getFieldValue(_Fields field) {
        switch(field) {
            case FROM_ID:
                return getFromId();
            case REVIEW:
                return isReview();
            case SETTING:
                return getSetting();
        }
        throw new IllegalStateException();
    }

    /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
    public boolean isSet(_Fields field) {
        if (field == null) {
            throw new IllegalArgumentException();
        }
        switch(field) {
            case FROM_ID:
                return isSetFromId();
            case REVIEW:
                return isSetReview();
            case SETTING:
                return isSetSetting();
        }
        throw new IllegalStateException();
    }

    @Override
    public boolean equals(Object that) {
        if (that == null)
            return false;
        if (that instanceof DPlatformFromSetting)
            return this.equals((DPlatformFromSetting) that);
        return false;
    }

    public boolean equals(DPlatformFromSetting that) {
        if (that == null)
            return false;
        boolean this_present_fromId = true;
        boolean that_present_fromId = true;
        if (this_present_fromId || that_present_fromId) {
            if (!(this_present_fromId && that_present_fromId))
                return false;
            if (this.fromId != that.fromId)
                return false;
        }
        boolean this_present_review = true;
        boolean that_present_review = true;
        if (this_present_review || that_present_review) {
            if (!(this_present_review && that_present_review))
                return false;
            if (this.review != that.review)
                return false;
        }
        boolean this_present_setting = true && this.isSetSetting();
        boolean that_present_setting = true && that.isSetSetting();
        if (this_present_setting || that_present_setting) {
            if (!(this_present_setting && that_present_setting))
                return false;
            if (!this.setting.equals(that.setting))
                return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        List<Object> list = new ArrayList<Object>();
        boolean present_fromId = true;
        list.add(present_fromId);
        if (present_fromId)
            list.add(fromId);
        boolean present_review = true;
        list.add(present_review);
        if (present_review)
            list.add(review);
        boolean present_setting = true && (isSetSetting());
        list.add(present_setting);
        if (present_setting)
            list.add(setting);
        return list.hashCode();
    }

    @Override
    public int compareTo(DPlatformFromSetting other) {
        if (!getClass().equals(other.getClass())) {
            return getClass().getName().compareTo(other.getClass().getName());
        }
        int lastComparison = 0;
        lastComparison = Boolean.valueOf(isSetFromId()).compareTo(other.isSetFromId());
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (isSetFromId()) {
            lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.fromId, other.fromId);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(isSetReview()).compareTo(other.isSetReview());
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (isSetReview()) {
            lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.review, other.review);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        lastComparison = Boolean.valueOf(isSetSetting()).compareTo(other.isSetSetting());
        if (lastComparison != 0) {
            return lastComparison;
        }
        if (isSetSetting()) {
            lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.setting, other.setting);
            if (lastComparison != 0) {
                return lastComparison;
            }
        }
        return 0;
    }

    public _Fields fieldForId(int fieldId) {
        return _Fields.findByThriftId(fieldId);
    }

    public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
        schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
        schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("DPlatformFromSetting(");
        boolean first = true;
        sb.append("fromId:");
        sb.append(this.fromId);
        first = false;
        if (!first)
            sb.append(", ");
        sb.append("review:");
        sb.append(this.review);
        first = false;
        if (isSetSetting()) {
            if (!first)
                sb.append(", ");
            sb.append("setting:");
            if (this.setting == null) {
                sb.append("null");
            } else {
                sb.append(this.setting);
            }
            first = false;
        }
        sb.append(")");
        return sb.toString();
    }

    public void validate() throws org.apache.thrift.TException {
    }

    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
        try {
            write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
        } catch (org.apache.thrift.TException te) {
            throw new java.io.IOException(te);
        }
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        try {
            // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
            __isset_bitfield = 0;
            read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
        } catch (org.apache.thrift.TException te) {
            throw new java.io.IOException(te);
        }
    }

    private static class DPlatformFromSettingStandardSchemeFactory implements SchemeFactory {

        public DPlatformFromSettingStandardScheme getScheme() {
            return new DPlatformFromSettingStandardScheme();
        }
    }

    private static class DPlatformFromSettingStandardScheme extends StandardScheme<DPlatformFromSetting> {

        public void read(org.apache.thrift.protocol.TProtocol iprot, DPlatformFromSetting struct) throws org.apache.thrift.TException {
            org.apache.thrift.protocol.TField schemeField;
            iprot.readStructBegin();
            while (true) {
                schemeField = iprot.readFieldBegin();
                if (schemeField.type == org.apache.thrift.protocol.TType.STOP) {
                    break;
                }
                switch(schemeField.id) {
                    case // FROM_ID
                    1:
                        if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
                            struct.fromId = iprot.readI32();
                            struct.setFromIdIsSet(true);
                        } else {
                            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
                        }
                        break;
                    case // REVIEW
                    2:
                        if (schemeField.type == org.apache.thrift.protocol.TType.BOOL) {
                            struct.review = iprot.readBool();
                            struct.setReviewIsSet(true);
                        } else {
                            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
                        }
                        break;
                    case // SETTING
                    3:
                        if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
                            struct.setting = new DFromSetting();
                            struct.setting.read(iprot);
                            struct.setSettingIsSet(true);
                        } else {
                            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
                        }
                        break;
                    default:
                        org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
                }
                iprot.readFieldEnd();
            }
            iprot.readStructEnd();
            // check for required fields of primitive type, which can't be checked in the validate method
            struct.validate();
        }

        public void write(org.apache.thrift.protocol.TProtocol oprot, DPlatformFromSetting struct) throws org.apache.thrift.TException {
            struct.validate();
            oprot.writeStructBegin(STRUCT_DESC);
            oprot.writeFieldBegin(FROM_ID_FIELD_DESC);
            oprot.writeI32(struct.fromId);
            oprot.writeFieldEnd();
            oprot.writeFieldBegin(REVIEW_FIELD_DESC);
            oprot.writeBool(struct.review);
            oprot.writeFieldEnd();
            if (struct.setting != null) {
                if (struct.isSetSetting()) {
                    oprot.writeFieldBegin(SETTING_FIELD_DESC);
                    struct.setting.write(oprot);
                    oprot.writeFieldEnd();
                }
            }
            oprot.writeFieldStop();
            oprot.writeStructEnd();
        }
    }

    private static class DPlatformFromSettingTupleSchemeFactory implements SchemeFactory {

        public DPlatformFromSettingTupleScheme getScheme() {
            return new DPlatformFromSettingTupleScheme();
        }
    }

    private static class DPlatformFromSettingTupleScheme extends TupleScheme<DPlatformFromSetting> {

        @Override
        public void write(org.apache.thrift.protocol.TProtocol prot, DPlatformFromSetting struct) throws org.apache.thrift.TException {
            TTupleProtocol oprot = (TTupleProtocol) prot;
            BitSet optionals = new BitSet();
            if (struct.isSetFromId()) {
                optionals.set(0);
            }
            if (struct.isSetReview()) {
                optionals.set(1);
            }
            if (struct.isSetSetting()) {
                optionals.set(2);
            }
            oprot.writeBitSet(optionals, 3);
            if (struct.isSetFromId()) {
                oprot.writeI32(struct.fromId);
            }
            if (struct.isSetReview()) {
                oprot.writeBool(struct.review);
            }
            if (struct.isSetSetting()) {
                struct.setting.write(oprot);
            }
        }

        @Override
        public void read(org.apache.thrift.protocol.TProtocol prot, DPlatformFromSetting struct) throws org.apache.thrift.TException {
            TTupleProtocol iprot = (TTupleProtocol) prot;
            BitSet incoming = iprot.readBitSet(3);
            if (incoming.get(0)) {
                struct.fromId = iprot.readI32();
                struct.setFromIdIsSet(true);
            }
            if (incoming.get(1)) {
                struct.review = iprot.readBool();
                struct.setReviewIsSet(true);
            }
            if (incoming.get(2)) {
                struct.setting = new DFromSetting();
                struct.setting.read(iprot);
                struct.setSettingIsSet(true);
            }
        }
    }

    static {
        Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
        tmpMap.put(_Fields.FROM_ID, new org.apache.thrift.meta_data.FieldMetaData("fromId", org.apache.thrift.TFieldRequirementType.DEFAULT, new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
        tmpMap.put(_Fields.REVIEW, new org.apache.thrift.meta_data.FieldMetaData("review", org.apache.thrift.TFieldRequirementType.DEFAULT, new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.BOOL)));
        tmpMap.put(_Fields.SETTING, new org.apache.thrift.meta_data.FieldMetaData("setting", org.apache.thrift.TFieldRequirementType.OPTIONAL, new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRUCT, "DFromSetting")));
        metaDataMap = Collections.unmodifiableMap(tmpMap);
        org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(DPlatformFromSetting.class, metaDataMap);
    }

    public DPlatformFromSetting create() {
        return new DPlatformFromSetting();
    }

    public DPlatformFromSetting clone() {
        return cloneDepth(0);
    }

    public DPlatformFromSetting cloneDepth(int _depth) {
        DPlatformFromSetting _clone = create();
        cloneMore(_clone, _depth);
        return _clone;
    }

    public void cloneMore(DPlatformFromSetting _clone, int _depth) {
    }
}

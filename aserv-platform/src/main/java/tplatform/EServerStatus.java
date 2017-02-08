/**
 * Autogenerated by Thrift Compiler (0.9.3)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package tplatform;

import com.absir.aserv.system.bean.value.JaLang;

public enum EServerStatus implements org.apache.thrift.TEnum {

    @JaLang("待开")
    wait(0), @JaLang("开启")
    open(1), @JaLang("维护")
    maintain(2), @JaLang("爆满")
    full(3), @JaLang("审核")
    review(4), @JaLang("测试")
    test(5);

    private final int value;

    private EServerStatus(int value) {
        this.value = value;
    }

    /**
   * Get the integer value of this enum value, as defined in the Thrift IDL.
   */
    public int getValue() {
        return value;
    }

    /**
   * Find a the enum type by its integer value, as defined in the Thrift IDL.
   * @return null if the value is not found.
   */
    public static EServerStatus findByValue(int value) {
        switch(value) {
            case 0:
                return wait;
            case 1:
                return open;
            case 2:
                return maintain;
            case 3:
                return full;
            case 4:
                return review;
            case 5:
                return test;
            default:
                return null;
        }
    }
}

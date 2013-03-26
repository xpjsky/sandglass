package net.xpjsky.sandglass.common.prop;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;

/**
 * Description Here
 *
 * @author Paddy
 * @version 12-8-24 下午9:47
 */
public class Property {

    public String namespace;

    public String key;

    public String value;

    public String comment;

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(Object value) {
        if (value instanceof String) {
            this.value = (String)value;
        } else if(value instanceof List) {

        } else if(value instanceof Map) {

        } else if(value instanceof Array){

        } else {
            this.value = value.toString();
        }
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}

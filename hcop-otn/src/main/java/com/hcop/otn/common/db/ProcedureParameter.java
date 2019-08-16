/**
 * FileName: ProcedureParameter
 * Author:   Administrator
 * Date:     2018/11/6 23:25
 * Description:
 */
package com.hcop.otn.common.db;

public class ProcedureParameter implements Comparable{

    /**
     * if this parameter is output parameter
     * the result will store in a map, and
     * the key is this key
     */
    private Object key;

    /**
     * the order of this parameter display parameter list of  procedure
     *
     */
    private int index;
    /**
     * determine this parameter is input type or not
     */
    private boolean input;
    /**
     * sql type this parameter will bind.
     * the value  reference java.sql.Types,please
     *
     *
     */
    private int  sqlType;

    public void setSqlType(int sqlType) {
        this.sqlType = sqlType;
    }
    public int getSqlType() {
        return sqlType;
    }
    /**
     * the value of this parameter
     */
    private Object value;

    public Object getValue() {
        return value;
    }
    public void setValue(Object value) {
        this.value = value;
    }
    public Object getKey() {
        return key;
    }
    public boolean isInput() {
        return input;
    }
    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }

    public void setKey(Object key) {
        this.key = key;
    }
    public void setInput(boolean input) {
        this.input = input;
    }

    @Override
    public int compareTo(Object o) {
        // TODO Auto-generated method stub
        if(o instanceof ProcedureParameter){
            ProcedureParameter other = (ProcedureParameter)o;
            int index = this.getIndex();
            int otherIndex = other.getIndex();

            if(index < otherIndex){
                return -1;
            }else if(index == otherIndex){
                return 0;
            }else{
                return 1;
            }
        }
        return -1;
    }


}

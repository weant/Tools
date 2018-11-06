
package com.otn.tool.common.internal.xos.response.extractors;

import java.util.LinkedList;
import java.util.List;

import com.alu.tools.basic.collection.CollectionUtil;
import com.alu.tools.basic.collection.Container;
import com.lucent.oms.xml.naInterface.Message_T;

import com.otn.tool.common.internal.xos.response.IResponseExtractor;
import  com.otn.tool.common.internal.xos.response.IllegalResponseException;

public abstract class AbstractListDataExtractor<T, L> implements
        IResponseExtractor<List<T>>
{
    public void extractResponse( Message_T response, Container<List<T>> data )
    {
        L list = getList( response );
        if( list == null )
            throw IllegalResponseException.create( "Invalid data in response",
                response );
        if( data.isEmpty() )
            data.set( new LinkedList<T>() );
        CollectionUtil.addAll( data.get(), getDataArray( list ) );
    }

    protected abstract L getList( Message_T response );

    protected abstract T[] getDataArray( L list );
}
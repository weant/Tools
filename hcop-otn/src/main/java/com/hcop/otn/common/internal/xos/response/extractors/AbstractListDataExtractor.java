
package com.hcop.otn.common.internal.xos.response.extractors;

import com.alu.tools.basic.collection.CollectionUtil;
import com.alu.tools.basic.collection.Container;
import com.hcop.otn.common.internal.xos.response.IResponseExtractor;
import com.hcop.otn.common.internal.xos.response.IllegalResponseException;
import com.lucent.oms.xml.naInterface.Message_T;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractListDataExtractor<T, L> implements
        IResponseExtractor<List<T>>
{
    @Override
    public void extractResponse(Message_T response, Container<List<T>> data )
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
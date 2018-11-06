package com.otn.tool.common.internal.xos.tl1.response;

import java.util.regex.Pattern;

import com.otn.tool.common.internal.xos.tl1.util.TL1Constants;
import com.alu.tools.basic.StringUtil;
import com.alu.tools.basic.collection.Pair;

public class TL1MessageDataParser
{
    private static final String ESCAPE_STR = "\\\"";

    private static final int AID_PART_INDEX = 0;

    private static final int POSITIONAL_PARAM_PART_INDEX = 1;

    private static final int KEYWORD_PARAM_PART_INDEX = 2;

    private static final int STATE_PARAM_PART = 3;

    private static final Pattern PART_DELIMITER_REGEX = Pattern
            .compile( TL1Constants.MsgDelimiter.PART_DELIMITER_REGEX );

    private static final Pattern COMMA_REGEX = Pattern
            .compile( TL1Constants.MsgDelimiter.COMMA );

    private static final String TWO_DELIMITER = StringUtil.repeat(
        TL1Constants.MsgDelimiter.PART_DELIMITER, 2 );

    public TL1MessageData toMessageData( String messageLine )
    {
        String[] parts = PART_DELIMITER_REGEX
                .split( getLineContent( messageLine ) );
        if( parts.length == 0 )
            return null;
        TL1MessageData messageData = createData();
        for( int i = 0; i < parts.length; i++ )
        {
            parseDataPart( messageData, parts[i], i );
        }
        return messageData;
    }

    protected TL1MessageData createData()
    {
        return new TL1MessageData();
    }

    protected void parseDataPart( TL1MessageData messageData, String partStr,
            int index )
    {
        if( StringUtil.isBlank( partStr ) )
            return;
        switch( index )
        {
            case AID_PART_INDEX:
                parseAidPart( messageData, partStr );
                break;
            case POSITIONAL_PARAM_PART_INDEX:
                parsePositionalParamPart( messageData, partStr );
                break;
            case KEYWORD_PARAM_PART_INDEX:
                parseKeywordParamPart( messageData, partStr );
                break;
            case STATE_PARAM_PART:
                parseStateParamPart( messageData, partStr );
                break;
            default:
        }
    }

    protected void parseAidPart( TL1MessageData messageData, String aidStr )
    {
        messageData.addAid( COMMA_REGEX.split( aidStr ) );
    }

    protected void parsePositionalParamPart( TL1MessageData messageData,
            String positionalParamStr )
    {
        messageData
                .addPositionalParam( COMMA_REGEX.split( positionalParamStr ) );
    }

    protected void parseKeywordParamPart( TL1MessageData messageData,
            String keywordParamStr )
    {
        for( String keywordParam : COMMA_REGEX.split( keywordParamStr ) )
        {
            Pair<String, String> keyValuePair = StringUtil.cut( keywordParam,
                TL1Constants.MsgDelimiter.EQUAL );
            if( keyValuePair == null )
                continue;
            messageData.addKeywordParam( keyValuePair.getFirst(),
                removeEscape(keyValuePair.getSecond()) );
        }
    }

    protected void parseStateParamPart( TL1MessageData messageData,
            String stateParamPart )
    {
        Pair<String, String> pstSst = StringUtil.cut( stateParamPart,
            TL1Constants.MsgDelimiter.STATE_PARAM_DELIMITER );
        if( pstSst == null )
            messageData.setPST( stateParamPart );
        else
            messageData.setState( pstSst.getFirst(), pstSst.getSecond() );
    }

    private String getLineContent( String messageLine )
    {
        String lineData = StringUtil.cut( messageLine,
            TL1Constants.MsgDelimiter.DATA_START.length(),
            TL1Constants.MsgDelimiter.DATA_END.length() );
        if( lineData.startsWith( TWO_DELIMITER ) )
        {
            int thirdColonIndex = lineData.indexOf(
                TL1Constants.MsgDelimiter.PART_DELIMITER,
                TWO_DELIMITER.length() );
            if( thirdColonIndex < 0 )
                thirdColonIndex = lineData.length();
            if( !lineData.substring( TWO_DELIMITER.length(), thirdColonIndex )
                    .contains( TL1Constants.MsgDelimiter.EQUAL ) )
            {
                lineData = lineData.substring( 1 );
            }
        }
        return lineData;
    }

    private String removeEscape( String str )
    {
        if( str.startsWith( ESCAPE_STR ) && str.endsWith( ESCAPE_STR ) )
            return str.substring( 2, str.length() - 2 ).trim();
        return str;
    }
}

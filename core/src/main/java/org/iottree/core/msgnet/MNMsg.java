package org.iottree.core.msgnet;

import java.util.HashMap;

import org.iottree.core.util.IdCreator;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 
 * @author jason.zhu
 *
 */
public class MNMsg
{
	private String _id ;
	
	private HashMap<String,Object> heads = null;//new HashMap<>() ;
	
	private String topic = null ;
	
	private Object payload = null ; 
	
	public MNMsg()
	{
		_id = IdCreator.newSeqId() ;
	}
	
	private MNMsg(String id)
	{
		this._id = id ;
	}
	
	public String getMsgId()
	{
		return this._id ;
	}
	
	public long getMsgDT()
	{
		return IdCreator.extractTimeInMillInSeqId(_id);
	}
	
	public MNMsg asTopic(String topic)
	{
		this.topic = topic ;
		return this ;
	}
	
	public String getTopic()
	{
		return this.topic ;
	}
	
	synchronized public MNMsg asHead(String name,Object val)
	{
		if(val==null)
		{
			if(heads==null) return this ;
			heads.remove(name) ;
			return this ;
		}
		
		if(!val.getClass().isPrimitive() && !(val instanceof String))
			throw new IllegalArgumentException("head value must primitive and string") ;
		
		if(heads==null)
		{
			heads = new HashMap<>() ;
		}
		heads.put(name, val) ;
		return this;
	}
	
	public Object getHeadVal(String name)
	{
		if(heads==null)
			return null ;
		return heads.get(name) ;
	}
	
	public MNMsg asPayload(Object payload)
	{
		if(payload==null)
		{
			this.payload = null ;
			return this;
		}
		
//		if(!payload.getClass().isPrimitive() && !(payload instanceof String)
//				&& !(payload instanceof JSONObject) && !(payload instanceof JSONArray))
//			throw new IllegalArgumentException("invalid payload type,it must be primitive string,JSONObject,JSONArray") ;
		
		this.payload = payload ;
		return this ;
	}
	
	public Object getPayload()
	{
		return payload ;
	}
	
	public Number getPayloadNumber()
	{
		return (Number)payload ;
	}
	
	public boolean getPayloadBool(boolean def_v) {
        if(payload==null)
        	return def_v;
        if (payload instanceof Boolean){
            return ((Boolean) payload).booleanValue();
        }
//        try {
//            // we'll use the get anyway because it does string conversion.
//            return this.getBoolean(key);
//        } catch (Exception e) {
//            return def_v;
//        }
        
        return def_v ;
    }
	
	public JSONObject getPayloadJO(JSONObject def_v)
	{
		 if(payload==null)
	        	return def_v;
		 if(payload instanceof JSONObject)
			 return (JSONObject)payload ;
		 
		 if(payload instanceof String)
		 {
			 String pstr = (String)payload ;
			 JSONObject tmpjo = new JSONObject(pstr) ;
			 return tmpjo ;
		 }
		 
		 throw new RuntimeException("not JSONObject payload") ;
	}
	
	public int getPayloadInt32()
	{
		if(!(payload instanceof Number))
			throw new RuntimeException("payload is not number") ;
		return ((Number)payload).intValue() ;
	}
	
	public String getPayloadStr()
	{
		if(this.payload==null)
			return "" ;
		return this.payload.toString() ;
	}
	
	public JSONObject toJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("id", _id) ;
		jo.put("time_ms", this.getMsgDT()) ;
		jo.putOpt("topic", this.topic) ;
		if(heads!=null)
		{
			jo.put("heads", new JSONObject(this.heads)) ;
		}
		
		jo.putOpt("payload", payload) ;
		return jo ;
	}
	
	public static MNMsg fromJO(JSONObject jo)
	{
		MNMsg ret = new MNMsg(jo.getString("id")) ;
		ret.topic = jo.optString("topic") ;
		JSONObject hdjo = jo.optJSONObject("heads") ;
		if(hdjo!=null)
		{
			ret.heads = new HashMap<>() ;
			for(String n:hdjo.keySet())
			{
				ret.heads.put(n, hdjo.get(n)) ;
			}
		}
		ret.payload = jo.opt("payload") ;
		return ret;
	}
}

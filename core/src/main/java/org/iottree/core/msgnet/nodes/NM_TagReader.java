package org.iottree.core.msgnet.nodes;

import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.util.ILang;
import org.iottree.core.util.jt.JSONTemp;
import org.json.JSONObject;

/**
 * 选择特定标签，然后配置特定名称进行对应。生成简单的JSON数据
 * 
 * @author jason.zhu
 *
 */
public class NM_TagReader extends MNNodeMid implements ILang
{
	@Override
	public String getColor()
	{
		return "#a1cbde";
	}
	
	@Override
	public String getIcon()
	{
		return "\\uf02c";
	}

	@Override
	public JSONTemp getInJT()
	{
		return null;
	}

	@Override
	public JSONTemp getOutJT()
	{
		return null;
	}

	@Override
	public int getOutNum()
	{
		return 1;
	}

	@Override
	public String getTP()
	{
		return "tag_reader";
	}

	@Override
	public String getTPTitle()
	{
		return g("tag_reader");
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		return null;
	}

	@Override
	protected void setParamJO(JSONObject jo, long up_dt)
	{
		
	}
	
	// --------------

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg)
	{
		return RTOut.createOutAll(msg) ;
	}
}

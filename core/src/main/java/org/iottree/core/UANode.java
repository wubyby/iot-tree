package org.iottree.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.iottree.core.util.CompressUUID;
import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.core.basic.PropGroup;
import org.iottree.core.basic.PropItem;
import org.iottree.core.basic.PropNode;
import org.iottree.core.res.IResNode;
import org.iottree.core.basic.PropItem.PValTP;
import org.iottree.core.util.xmldata.*;
import org.json.JSONObject;

@data_class
public abstract class UANode extends PropNode implements IOCBox,DataTranserXml.ITranser //IOC
{
	static ILogger log = LoggerManager.getLogger("UANode") ;
	
	public static enum State
	{
		
	}
	
	@data_val
	String id = null ;
	
	@data_val
	private String name = "" ;
	
	@data_val
	private String title = "" ;
	
	@data_val
	private String desc="" ;
	
	@data_val(param_name = "ext_attr_str")
	private String extAttrStr = "" ;
	
	transient JSONObject extAttrJO = null ;
	
	transient UANode parentNode = null ;
	
	//transient State nodeState = State.st_not_setup; 
	//private ScriptEngine scriptEng = null ;
	
	public UANode()
	{
		id = CompressUUID.createNewId();
	}
	
	public UANode(String name,String title,String desc)
	{
		id = CompressUUID.createNewId(); //UUID.randomUUID().toString();
		setNameTitle(name,title,desc);
	}
	
	public abstract String getNodeTp() ;
//	protected boolean setNameTitle(String name,String title)
//	{
//		Convert.checkVarName(name);
//		if(name.startsWith("_"))
//			throw new IllegalArgumentException("name cannot start with _") ;
//		boolean b = false;
//		if(!name.equals(this.name))
//		{
//			this.name = name ;
//			b = true ;
//		}
//		if(title!=null&&!title.equals(this.title))
//		{
//			this.title = title ;
//			b = true ;
//		}
//		
//		return b;
//	}
	
	boolean setNameTitle(String name,String title,String desc)
	{
		boolean b = false;
		if(name!=null)
		{
			Convert.checkVarName(name);
			if(name.startsWith("_"))
				throw new IllegalArgumentException("name cannot start with _") ;
			if(!name.equals(this.name))
			{
				this.name = name ;
				b = true ;
			}
		}
		
		if(title!=null&&!title.equals(this.title))
		{
			this.title = title ;
			b = true ;
		}
		if(desc==null)
			desc = "" ;
		if(!desc.equals(this.desc))
		{
			this.desc = desc ;
			b = true ;
		}
		return b;
	}
	
	void setNameTitleSys(String name,String title,String desc)
	{
		Convert.checkVarName(name,false);
		//if(name.startsWith("_"))
		//	throw new IllegalArgumentException("name cannot start with _") ;
		this.name = name ;
		this.title = title ;
		this.desc = desc ;
	}
	/**
	 * 
	 * @param new_self create by copySelfWithNewId
	 */
	protected void copyTreeWithNewSelf(IRoot root,UANode new_self,String ownerid,boolean copy_id,boolean root_subnode_id,
			HashMap<IRelatedFile,IRelatedFile> rf2new)
	{
		if(Convert.isNotNullEmpty(ownerid))
			ownerid+="-" ;
		else
			ownerid="" ;
		
		if(copy_id)
			new_self.id = ownerid+this.id ;
		else
			new_self.id = ownerid+new_self.id ;
		
		new_self.name = this.name ;
		new_self.title = this.title ;
		new_self.desc = this.desc ;
	}

	public String getId()
	{
		return id ;
	}
	
	public String getNextIdByRoot()
	{
		IRoot r = getRoot() ;
		if(r==null)
			throw new RuntimeException("no root found") ;
		return r.getRootNextId() ;
	}
	
	public int getRelatedFiles(List<File> fs)
	{
		//ArrayList<File> rets = new ArrayList<>() ;
		if(this instanceof IRelatedFile)
			fs.add(((IRelatedFile)this).getRelatedFile()) ;
		if(this instanceof IResNode)
		{
			File fdir = ((IResNode)this).getResNodeDir();
			if(fdir!=null)
				fs.add(fdir) ;
		}
		List<UANode> subns = this.getSubNodes() ;
		if(subns==null)
			return fs.size() ;
		for(UANode n:subns)
			n.getRelatedFiles(fs);
		return fs.size() ;
	}
	
	private IRoot getRoot()
	{
		UANode tn = this ;
		do
		{
			if(tn instanceof IRoot)
				return (IRoot)tn ;
			tn = tn.getParentNode() ;
		}while(tn!=null) ;
		return null ;
	}
	/**
	 * 
	 * @return
	 */
	public String getRefOwnerId()
	{
		int k = id.indexOf('-') ;
		if(k<=0)
			return null ;//no owner
		return id.substring(0,k) ;
	}
	
	
	
	public String getRefBranchId()
	{
		int k = id.indexOf('-') ;
		if(k<=0)
			return null ;//no owner
		return id.substring(k+1) ;
	}
	
	public boolean isRefOwner()
	{
		return this instanceof IRefOwner ;
	}
	
	public boolean isRefedNode()
	{
		return isRefOwner() || Convert.isNotNullEmpty(getRefBranchId());
	}
	
	/**
	 * tree node which is copied by branch will has owner tree node id and branch treenode ref id
	 * 
	 * @return
	 */
	public IRefOwner getRefOwner()
	{
		String idowner = this.getRefOwnerId() ;
		if(Convert.isNullOrEmpty(idowner))
			return null ;
		UANode tn = this ;
		do
		{
			if(tn.getId().equals(idowner))
			{
				return (IRefOwner)tn ;
			}
			tn = tn.getParentNode() ;
		}
		while(tn!=null) ;
		return null ;
	}
	
	/**
	 * if this node is refered by a branch. node will be copy from branch.
	 * so this node has a branch node related to it.
	 * @return
	 */
	public UANode getRefBranchNode()
	{
		String bid = getRefBranchId() ;
		if(Convert.isNullOrEmpty(bid))
			return null ;
		IRefOwner ref = getRefOwner() ;
		if(ref==null)
			return null;
		IRefBranch rb = ref.getRefBranch() ;
		if(rb==null)
			return null ;
		return rb.findNodeById(bid) ;
	}
	
	public boolean equals(Object o)
	{//extends map obj that may make error
		if(!(o instanceof UANode))
			return false;
		return this.id.equals(((UANode)o).id) ;
	}
	
	public String getName()
	{
		if(name==null)
			return "" ;
		return name ;
	}
	
	public String getTitle()
	{
		if(title==null)
			return "" ;
		return title ;
	}
	
	public String getDesc()
	{
		return desc;
	}
	
	
	public String getExtAttrStr()
	{
		return this.extAttrStr ;
	}
	
	public void setExtAttrStr(String astr)
	{
		this.extAttrStr = astr ;
		this.extAttrJO = null ;
	}
	
	public JSONObject getExtAttrJO()
	{
		if(extAttrJO!=null)
			return extAttrJO;
		
		if(Convert.isNullOrEmpty(this.extAttrStr))
			return null ;
		
		try
		{
			extAttrJO = new JSONObject(extAttrStr) ;
			return extAttrJO;
		}
		catch(Exception e)
		{
			if(log.isDebugEnabled())
				log.debug("", e);
			return null ;
		}
	}
	
	/**
	 * 
	 * @param classn
	 * @return string or [string]
	 */
	public Object getExtAttrValue(String classn)
	{
		JSONObject jo = getExtAttrJO() ;
		if(jo==null)
			return null ;
		return jo.opt(classn) ;
	}
	
	public UANode getParentNode()
	{
		return parentNode ;
	}
	
	public String getNodePath()
	{
		UANode p = this.getParentNode() ;
		if(p==null)
		{
			return "/"+this.name ;
		}
		String ppn = p.getNodePath() ;
		return ppn+"/"+this.name;
	}
	
	public String getNodePathCxt()
	{
		return getNodePathCxt(".");
	}
	
	public String getNodePathCxt(String spliter)
	{
		UANode p = this.getParentNode() ;
		if(p==null)
		{
			return this.getName() ;
		}
		String ppn = p.getNodePathCxt(spliter) ;
		return ppn+spliter+this.getName();
	}
	
	public String getNodeCxtPathIn(UANode tn)
	{
		return getNodeCxtPathIn(tn,".") ;
	}
	
	public String getNodeCxtPathIn(UANode tn,String spliter)
	{
		String pnp = tn.getNodePathCxt(spliter) ;
		String np = this.getNodePathCxt(spliter) ;
		//System.out.println(this.getClass().getName()+" np="+np+"   "+tn.getClass().getName()+"  pnp="+pnp);
    	return np.substring(pnp.length()+1) ;
	}
	
	public String getNodeCxtPathTitleIn(UANode tn)
	{
		String pnp = tn.getNodePathTitle() ;
		String np = this.getNodePathTitle() ;
		return np.substring(pnp.length()) ;
	}
	
	public String getNodePathName()
	{
		UANode p = this.getParentNode() ;
		if(p==null)
		{
			//return this.name ;
			return "" ;
		}
		String ppn = p.getNodePathName() ;
		if(Convert.isNullOrEmpty(ppn))
			return this.name ;
		else
			return ppn+"."+this.name;
	}

	public String getNodePathTitle()
	{
		UANode p = this.getParentNode() ;
		if(p==null)
		{
			return this.getTitle() ;
			//return "" ;
		}
		String ppt = p.getNodePathTitle() ;
		if(Convert.isNullOrEmpty(ppt))
			return this.getTitle() ;
		else
			return ppt+"/"+this.getTitle();
	}
	
	
	
	void constructNodeTree()
	{
		List<UANode> ns = getSubNodes() ;
		if(ns==null||ns.size()<=0)
			return ;
		for(UANode n:ns)
		{
			n.parentNode = this ;
			n.constructNodeTree();
		}
	}
	
	/**
	 * find descendants node by id in tree
	 * @param id
	 * @return
	 */
	public UANode findNodeById(String id)
	{
		if(id.contentEquals(this.id))
			return this;
		List<UANode> subns = getSubNodes() ;
		if(subns==null)
			return null ;
		for(UANode subn:subns)
		{
			if(id.contentEquals(subn.getId()))
				return subn ;
			UANode n = subn.findNodeById(id);
			if(n!=null)
				return n ;
		}
		return null;
	}
	
	public UANode getSubNodeByName(String n)
	{
		List<UANode> subns = getSubNodes() ;
		if(subns==null)
			return null ;
		for(UANode subn:subns)
		{
			if(n.contentEquals(subn.getName()))
				return subn ;
		}
		return null;
	}
	
	/**
	 * when do copy paste operation,name may auto created.
	 * to avoid confliction,node can use this method to create next name
	 * @param newname
	 * @return
	 */
	protected String calNextSubNameAuto(String newname)
	{
		UANode subn = this.getSubNodeByName(newname);
		if(subn==null)
			return newname ;
		int k = newname.lastIndexOf("_");
		String leftn=newname;
		int cc = 0 ;
		if(k>0)
		{
			String rstr = newname.substring(k+1);
			try
			{
				cc  =Integer.parseInt(rstr);
				leftn = newname.substring(0,k);
			}
			catch(Exception ee)
			{
				
			}
		}
		
		String tmpn = null;
		do
		{
			cc ++;
			tmpn=leftn+"_"+cc;
			subn = this.getSubNodeByName(tmpn);
		}
		while(subn!=null);
		return tmpn;
	}
	
	
	public final UANode getTopNode()
	{
		UANode pn = this.getParentNode() ;
		if(pn==null)
			return this ;
		return pn.getTopNode() ;
	}
	
	public final UADev getOwnerUADev()
	{
		UANode pn = this;
		while(pn!=null)
		{
			pn = pn.getParentNode() ;
			if(pn==null)
				return null ;
			if(pn instanceof UADev)
				return (UADev)pn;
		}
		return null ;
	}
	/**
	 * no include self
	 * @param path
	 * @return
	 */
	public final UANode getDescendantNodeByPath(List<String> path)
	{
		return getDescendantNodeByPath(path,0) ;
	}
	
	private final UANode getDescendantNodeByPath(List<String> path,int idx)
	{
		if(path.size()<=idx)
			return this ;
		String n = path.get(idx) ;
		UANode nn = this.getSubNodeByName(n) ;
		if(nn==null)
			return null ;
		
		idx ++ ;
		if(path.size()<=idx)
			return nn ;
		return nn.getDescendantNodeByPath(path,idx) ;
	}
	
	public UANode getDescendantNodeByPath(String pathstr)
	{
		LinkedList<String> ps = Convert.splitStrWithLinkedList(pathstr, ".") ;
		return getDescendantNodeByPath(ps) ;
	}
	
	public abstract List<UANode> getSubNodes();
	

	
	
	
//	public final UANode copyMeTreeWithNewId()
//	{
//		UANode nn = copySelfWithNewId() ;
//		//cannot use getSubNodes
//		copySubNodesWithNewSelf(nn) ;
//		copyMembersWithNewSelf(nn);
//		return nn ;
//	}
	
	
	public boolean isNodeValid()
	{
		if(Convert.isNullOrEmpty(id))
			return false;
		if(Convert.isNullOrEmpty(this.name))
			return false;
		return chkValid() ;
	}
	
	protected abstract boolean chkValid();
	
	
	protected void fireNodeChanged()
	{
		this.onNodeChanged();
		
		//
		
	}
	
	protected void onNodeChanged()
	{
		
	}
	
	static ArrayList<PropGroup> basicPGS = new ArrayList<>();
	static
	{
		PropGroup pg = new PropGroup("basic","Basic") ;
		pg.addPropItem(new PropItem("id","Id","Object's id",PValTP.vt_str,true,null,null,""));
		pg.addPropItem(new PropItem("name","Name","Object's name",PValTP.vt_str,false,null,null,""));
		pg.addPropItem(new PropItem("title","Title","Object's title",PValTP.vt_str,false,null,null,""));
		pg.addPropItem(new PropItem("desc","Description","Object's Description",PValTP.vt_str,false,null,null,""));
		
		basicPGS.add(pg) ;
	}
	
	@Override
	public List<PropGroup> listPropGroups()
	{
		return basicPGS;
	}
	
	public Object getPropValue(String groupn,String itemn)
	{
		if("basic".contentEquals(groupn))
		{
			switch(itemn)
			{
			case "id":
				return this.getId();
			case "name":
				return this.getName();
			case "title":
				return this.getTitle() ;
			case "desc":
				return this.getDesc();
			}
		}
		return super.getPropValue(groupn, itemn);
	}
	
	public boolean setPropValue(String groupn,String itemn,String strv)
	{
		if("basic".contentEquals(groupn))
		{
			switch(itemn)
			{
			case "name":
				this.name =strv;
				return true;
			case "title":
				this.title =strv;
				return true;
			case "desc":
				this.desc =strv;
				return true;
			default://
				return false;
			}
		}
		return super.setPropValue(groupn, itemn,strv);
	}
	
	public String toString()
	{
		return "id:\""+id+"\",name:\""+name+"\",title:\""+title+"\",nvalid:"+isNodeValid() ;
	}
	
//	public XmlData toUAXmlData() throws Exception
//	{
//		XmlData xd = DataXmlTranser.extractXmlDataFromObj(this) ;
//		
//		return xd ;
//	}
	
//	public void fromUAXmlData(XmlData xd) throws Exception
//	{
//		DataXmlTranser.injectXmDataToObj(this, xd);
//		XmlData pnxd = xd.getSubDataSingle("_prop_node") ;
//		if(pnxd!=null)
//			this.fromPropNodeXmlData(pnxd);
//	}
	
	
	public void afterXmlDataExtract(XmlData xd)
	{
		xd.setSubDataSingle("_prop_node", this.toPropNodeValXmlData());
	}
	
	public void afterXmlDataInject(XmlData xd)
	{
		XmlData pnxd = xd.getSubDataSingle("_prop_node") ;
		if(pnxd!=null)
			this.fromPropNodeValXmlData(pnxd);
	}
	
	public Object JS_get(String  key)
	{
		switch(key)
		{
		case "_id":
			return this.id ;
		case "_name":
			return this.name ;
		case "_title":
			return this.title ;
		case "_desc":
			return this.desc ;
		}
		return this.getSubNodeByName(key) ;
	}
	
	public List<Object> JS_names()
	{
		ArrayList<Object> ss = new ArrayList<>() ;
		ss.add("_id") ;
		ss.add("_name") ;
		ss.add("_title") ;
		ss.add("_desc") ;
		List<UANode> subns = this.getSubNodes() ;
		if(subns!=null)
		{
			for(UANode n:subns)
				ss.add(n.getName()) ;
		}
		return ss ;
	}
	
}







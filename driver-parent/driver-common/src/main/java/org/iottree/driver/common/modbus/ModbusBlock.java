package org.iottree.driver.common.modbus;

import java.util.*;

import org.iottree.driver.common.modbus.ModbusCmd;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.core.util.xmldata.DataUtil;
import org.iottree.driver.common.modbus.*;
import org.iottree.core.DevAddr;
import org.iottree.core.UAVal;
import org.iottree.core.UAVal.ValTP;
import org.iottree.core.basic.IConnEndPoint;
import org.iottree.core.basic.MemSeg8;
import org.iottree.core.basic.MemTable;
import org.iottree.driver.common.*;

public class ModbusBlock
{
	static ILogger log = LoggerManager.getLogger("Modbus_Lib");
	
	int devId = 1 ;
	short addrTp = -1 ;
	
	List<ModbusAddr> addrs = null ;
	
	int blockSize = 32 ;
	long scanInterMS = 100 ;
	
	MemTable<MemSeg8> memTb = new MemTable<>(8,65536) ;
	
	transient HashMap<ModbusCmd,List<ModbusAddr>> cmd2addr = new HashMap<>() ;
	
	
	private int failedSuccessive = 3 ;
	
	private long reqTO = 1000 ;
	
	private long recvTO = 100 ;
	
	private long interReqMs = 0 ;
	
	transient int failedCount = 0 ;
	
	transient ModbusCmd.Protocol modbusProtocal = ModbusCmd.Protocol.rtu;
	
	public ModbusBlock(int devid,short addrtp,List<ModbusAddr> addrs,
			int block_size,long scan_inter_ms,int failed_successive)
	{
		devId = devid ;
		if(addrs==null||addrs.size()<=0)
			throw new IllegalArgumentException("addr cannot be emtpy");
		if(block_size<=0)
			throw new IllegalArgumentException("block cannot <=0 ");
		this.addrTp = addrtp ;
		this.addrs = addrs ;
		this.blockSize = block_size ;
		this.scanInterMS = scan_inter_ms;
		this.failedSuccessive = failed_successive;
	}
	
	public void setTimingParam(long req_to,long recv_to,long inter_reqms)
	{
		this.reqTO = req_to ;
		this.recvTO = recv_to ;
		this.interReqMs = inter_reqms ;
	}
		
	public short getAddrTp()
	{
		return addrTp ;
	}
	
	private boolean isBitCmd()
	{
		switch(addrTp)
		{
		case ModbusAddr.COIL_INPUT:
		case ModbusAddr.COIL_OUTPUT:
			return true;
		default:
			return false;
		}
	}
	
	private short getFC()
	{
		switch(addrTp)
		{
		case ModbusAddr.COIL_INPUT:
			return ModbusCmd.MODBUS_FC_READ_DISCRETE_INPUT;
		case ModbusAddr.COIL_OUTPUT:
			return ModbusCmd.MODBUS_FC_READ_COILS ;//rw
		case ModbusAddr.REG_INPUT:
			return ModbusCmd.MODBUS_FC_READ_INPUT_REG;
		case ModbusAddr.REG_HOLD:
			return ModbusCmd.MODBUS_FC_READ_HOLD_REG;//rw
	
		default:
			return -1;
		}
	}
	
	public List<ModbusAddr> getAddrs()
	{
		return addrs ;
	}
	
	public boolean initReadCmds()
	{
		if(this.isBitCmd())
			return initReadCmdsBit();
		else
			return initReadCmdsWord();
	}

	private boolean initReadCmdsBit()
	{
		if(addrs==null||addrs.size()<=0)
			return false ;
		
		ModbusCmd curcmd = null ;
		int cur_reg = -1 ;
		ArrayList<ModbusAddr> curaddrs = null ;
		for(ModbusAddr ma:addrs)
		{
			int regp = ma.getRegPos();//ma.getBoolBitPos();
			if(cur_reg<0)
			{
				cur_reg = regp ;
				curaddrs = new ArrayList<>() ;
				curaddrs.add(ma) ;
				continue ;
			}
			
			if(ma.getRegPos()<=cur_reg+this.blockSize)//if(ma.getBoolBitPos()<=cur_reg+this.blockSize)
			{
				curaddrs.add(ma) ;
				continue;
			}
			
			ModbusAddr lastma = curaddrs.get(curaddrs.size()-1) ;
			curcmd =  new ModbusCmdReadBits(this.getFC(),this.scanInterMS,
						devId,cur_reg,lastma.getRegPos()-cur_reg+1) ;
			
			//curcmd.setRecvTimeout(reqTO);
			//curcmd.setScanIntervalMS(this.interReqMs);
			cmd2addr.put(curcmd, curaddrs);
			
			cur_reg = regp ;
			curaddrs = new ArrayList<>() ;
			curaddrs.add(ma) ;
			continue ;
		}
		
		if(curaddrs.size()>0)
		{
			ModbusAddr lastma = curaddrs.get(curaddrs.size()-1) ;
			
			curcmd =  new ModbusCmdReadBits(this.getFC(),this.scanInterMS,
						devId,cur_reg,lastma.getRegPos()-cur_reg+1) ;
			//curcmd.setRecvTimeout(reqTO);
			//curcmd.setRecvEndTimeout(recvTO);
			
			cmd2addr.put(curcmd, curaddrs);
		}
		
		for(ModbusCmd mc:cmd2addr.keySet())
		{
			mc.setRecvTimeout(reqTO);
			mc.setRecvEndTimeout(recvTO);
			if(log.isDebugEnabled())
				log.debug("init modbus cmd="+mc);
		}
		
		
		return true;
	}
	
	private boolean initReadCmdsWord()
	{
		if(addrs==null||addrs.size()<=0)
			return false ;

		ModbusCmd curcmd = null ;
		int cur_reg = -1 ;
		ArrayList<ModbusAddr> curaddrs = null ;
		for(ModbusAddr ma:addrs)
		{
			int regp = ma.getRegPos() ;
			if(cur_reg<0)
			{
				cur_reg = regp ;
				curaddrs = new ArrayList<>() ;
				curaddrs.add(ma) ;
				continue ;
			}
			
			if(ma.getRegEnd()<=cur_reg+this.blockSize)
			{
				curaddrs.add(ma) ;
				continue;
			}
			
			ModbusAddr lastma = curaddrs.get(curaddrs.size()-1) ;
			curcmd = new ModbusCmdReadWords(this.getFC(),this.scanInterMS,
						devId,cur_reg,(lastma.getRegEnd()-cur_reg)/2);
			//curcmd.setRecvTimeout(reqTO);
			//curcmd.setRecvEndTimeout(recvTO);
			
			cmd2addr.put(curcmd, curaddrs);
				
			cur_reg = regp ;
			curaddrs = new ArrayList<>() ;
			curaddrs.add(ma) ;
			continue ;
		}
		
		if(curaddrs.size()>0)
		{
			ModbusAddr lastma = curaddrs.get(curaddrs.size()-1) ;
			curcmd = new ModbusCmdReadWords(this.getFC(),this.scanInterMS,
						devId,cur_reg,(lastma.getRegEnd()-cur_reg)/2);
			//curcmd.setRecvTimeout(reqTO);
			//curcmd.setRecvEndTimeout(recvTO);
			cmd2addr.put(curcmd, curaddrs);
		}
		
		for(ModbusCmd mc:cmd2addr.keySet())
		{
			mc.setRecvTimeout(reqTO);
			mc.setRecvEndTimeout(recvTO);
			if(log.isDebugEnabled())
				log.debug("init modbus cmd="+mc);
		}
		
		return true;
	}
	
	public void setModbusProtocal(ModbusCmd.Protocol p)
	{
		modbusProtocal = p;
		
		for(ModbusCmd mc:cmd2addr.keySet())
		{
			mc.setProtocol(p);
		}
	}
	
	private void setAddrError(List<ModbusAddr> addrs)
	{
		if(addrs==null)
			return ;
		for(ModbusAddr ma:addrs)
			ma.RT_setVal(null); 
	}
	

	private Object getValByAddr(ModbusAddr da)
	{
		UAVal.ValTP vt = da.getValTP();
		if(vt==null)
			return null ;
		if(vt==UAVal.ValTP.vt_bool)
		{
			int regp = da.getRegPos() ;
			return memTb.getValBool(regp/8,regp%8) ;
		}
		else if(vt.isNumberVT())
		{
			return memTb.getValNumber(vt,da.getRegPos()*2) ;
		}
		return null;
	}
	
//	private boolean setValByAddr(ModbusAddr da,Object v)
//	{
//		UAVal.ValTP vt = da.getValTP();
//		if(vt==null)
//			return false ;
//		if(vt==UAVal.ValTP.vt_bool)
//		{
//			boolean bv = false;
//			if(v instanceof Boolean)
//				bv = (Boolean)v ;
//			else if(v instanceof Number)
//				bv = ((Number)v).doubleValue()>0 ;
//			else
//				return false;
//			memTb.setValBool(da.getRegPos(),da.getBitPos(),bv) ;
//			return true;
//		}
//		else if(vt.isNumberVT())
//		{
//			if(!(v instanceof Number))
//				return false;
//			memTb.setValNumber(vt,da.getRegPos(),(Number)v) ;
//			return true;
//		}
//		return false;
//	}
	
	
	
	public boolean runCmds(IConnEndPoint ep) throws Exception
	{
		this.runWriteCmdAndClear(ep);
		return runReadCmds(ep) ;
		
	}
	
	public void runCmdsErr()
	{
		runReadCmdsErr() ;
	}
	
	private void transMem2Addrs(List<ModbusAddr> addrs)
	{
		//System.out.println("transMem2Addrs "+this.recvTO+" fix=");
		//mem to addrs
		for(ModbusAddr ma:addrs)
		{
			Object ov = getValByAddr(ma) ;
			if(ov!=null)
				ma.RT_setVal(ov);
		}
	}
	
	private boolean chkSuccessiveFailed(boolean bfailed)
	{
		if(!bfailed)
		{
			failedCount = 0 ;
			return false;
		}
		
		failedCount ++;
		if(failedCount>=this.failedSuccessive)
		{
			failedCount = this.failedSuccessive;
			return true;
		}
		return false;
	}
	
	private boolean runReadCmds(IConnEndPoint ep) throws Exception
	{
		//ArrayList<DevAddr> okaddrs = new ArrayList<>() ;
		boolean ret = true;
		for(ModbusCmd mc:cmd2addr.keySet())
		{
			if(!mc.tickCanRun())
				continue ;
			
			Thread.sleep(this.interReqMs);
			
			List<ModbusAddr> addrs = cmd2addr.get(mc) ;
			mc.doCmd(ep.getOutputStream(), ep.getInputStream());
			if(mc instanceof ModbusCmdReadBits)
			{
				ModbusCmdReadBits mcb = (ModbusCmdReadBits)mc ;
				boolean[] bvs = mcb.getRetVals() ;
				if(bvs==null)
				{//err set address invalid
					if(chkSuccessiveFailed(true))
					{
						setAddrError(addrs);
						ret = false;
					}
					continue ;
				}
				int regpos = mcb.getRegAddr() ;
				for(int i = 0 ; i < bvs.length ; i ++)
				{
					boolean bv = bvs[i] ;
					memTb.setValBool((regpos+i)/8, (regpos+i)%8, bv);
				}
				transMem2Addrs(addrs);
				
				chkSuccessiveFailed(false) ;
			}
			else if(mc instanceof ModbusCmdReadWords)
			{
				ModbusCmdReadWords mcw = (ModbusCmdReadWords)mc ;
				int[] rvs = mcw.getRetVals() ;
				if(rvs==null)
				{//err set address invalid
					if(chkSuccessiveFailed(true))
					{
						setAddrError(addrs);
						ret = false;
					}
					continue ;
				}
				int regpos = mcw.getRegAddr() ;
				for(int i = 0 ; i < rvs.length ; i ++)
				{
					int rv = rvs[i] ;
					memTb.setValNumber(ValTP.vt_int16, (regpos+i)*2, rv);
				}
				transMem2Addrs(addrs);
				chkSuccessiveFailed(false) ;
			}
		}
		return ret ;
	}
	
	private boolean runReadCmdsErr() //throws Exception
	{
		//ArrayList<DevAddr> okaddrs = new ArrayList<>() ;
		boolean ret = true;
		for(ModbusCmd mc:cmd2addr.keySet())
		{
			
			List<ModbusAddr> addrs = cmd2addr.get(mc) ;
			setAddrError(addrs);
			//transMem2Addrs(addrs);
		}
		return ret ;
	}
	
	private LinkedList<ModbusCmd> writeCmds = new LinkedList<>() ;
	
	private void runWriteCmdAndClear(IConnEndPoint ep) throws Exception
	{
		int s = writeCmds.size();
		if(s<=0)
			return ;
		
		ModbusCmd[] cmds = new ModbusCmd[s] ;
		synchronized(writeCmds)
		{
			for(int i = 0 ; i < s ; i ++)
				cmds[i] = writeCmds.removeFirst() ;
		}
		
		for(ModbusCmd mc:cmds)
		{
			if(!mc.tickCanRun())
				continue ;
			
			Thread.sleep(this.interReqMs);
			
			mc.doCmd(ep.getOutputStream(), ep.getInputStream());
		}
	}
	
	public boolean setWriteCmdAsyn(ModbusAddr ma, Object v)
	{
		//System.out.println("set write asyn") ;
		ModbusCmd mc=null;
		switch(ma.getAddrTp())
		{
		case ModbusAddr.COIL_OUTPUT:
			boolean[] bvs = new boolean[1] ;
			bvs[0] = (Boolean)v;
			//mc = new ModbusCmdWriteBits(scanInterMS,this.devId,ma.getRegPos(),bvs) ;
			mc = new ModbusCmdWriteBit(scanInterMS,this.devId,ma.getRegPos(), (Boolean)v) ;
			mc.setRecvTimeout(reqTO);
			mc.setRecvEndTimeout(recvTO);
			//mc.setProtocol(modbusProtocal);
			break ;
		case ModbusAddr.REG_HOLD:
			if(!(v instanceof Number))
				return false;
			Number nv = (Number)v ;
			ValTP vt = ma.getValTP() ;
			int dlen = vt.getValByteLen()/2 ;
			if(dlen<1)
				return false;//not support
			int[] vals = null ;
			switch(vt)
			{
			case vt_int16:
			case vt_uint16:
				vals = new int[1] ;
				vals[0] = nv.shortValue() ;
				break ;
			case vt_int32:
			case vt_uint32:
				vals = new int[2] ;
				int intv = nv.intValue() ;
				vals[0] = (intv>>16) & 0xFFFF ;
				vals[1] = intv & 0xFFFF ;
				break ;
			case vt_int64:
			case vt_uint64:
				vals = new int[4] ;
				long longv = nv.longValue() ;
				vals[0] = (int)((longv>>48) & 0xFFFF) ;
				vals[1] = (int)((longv>>32) & 0xFFFF) ;
				vals[2] = (int)((longv>>16) & 0xFFFF) ;
				vals[3] = (int)(longv & 0xFFFF) ;
				break ;
			case vt_float:
				vals = new int[2] ;
				intv = Float.floatToIntBits(nv.floatValue()) ;
				vals[0] = (intv>>16) & 0xFFFF ;
				vals[1] = intv & 0xFFFF ;
				break ;
			case vt_double:
				vals = new int[4] ;
				longv = Double.doubleToLongBits(nv.doubleValue()) ;
				vals[0] = (int)((longv>>48) & 0xFFFF) ;
				vals[1] = (int)((longv>>32) & 0xFFFF) ;
				vals[2] = (int)((longv>>16) & 0xFFFF) ;
				vals[3] = (int)(longv & 0xFFFF) ;
				break ;
			default:
				return false;
			}
			
			if(vals.length==1)
			{
				mc = new ModbusCmdWriteWord(this.scanInterMS,
						devId,ma.getRegPos(),vals[0]);
			}
			else
			{
				mc = new ModbusCmdWriteWords(this.scanInterMS,
						devId,ma.getRegPos(),vals);
			}
			 mc.setRecvTimeout(reqTO);
			mc.setRecvEndTimeout(recvTO);
			 break;
		default:
			return false;
		}
		mc.setProtocol(modbusProtocal);

		synchronized(writeCmds)
		{
			writeCmds.addLast(mc);
		}
		return true;
		
	}
}

<%@ page contentType="text/html;charset=UTF-8" isELIgnored="false"%>
<%@ page
	import="java.util.*,
				java.io.*,
				java.util.*,
				org.iottree.core.*,
	org.iottree.core.util.*,
	org.iottree.core.comp.*,
				java.net.*"%>
<%
	if(!Convert.checkReqEmpty(request, out, "tabid","path"))
		return ;
	//String op = request.getParameter("op");
	String tabid = request.getParameter("tabid");
	String path = request.getParameter("path");
	//String id = request.getParameter("id");
	//UARep rep = UAManager.getInstance().getRepById(repid);
	//if(rep==null)
	//{
	//	out.print("no rep found!");
	//	return;
	//}
	UAHmi uahmi = (UAHmi)UAUtil.findNodeByPath(path);//.findHmiById(id) ;
	if(uahmi==null)
	{
		out.print("no hmi node found") ;
	}
	if(uahmi.getRefBranchNode()!=null)
	{
		response.sendRedirect(path);
		return ;
	}
	UANodeOCTagsCxt node = uahmi.getBelongTo() ;
	String cxtnodeid = node.getId() ;
%><!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>hmi context editor</title>
<script src="/_js/jquery-1.12.0.min.js"></script>
<script src="/_js/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript" src="/_js/ajax.js"></script>
<link rel="stylesheet" type="text/css" href="/_js/layui/css/layui.css" />
<script src="/_js/dlg_layer.js"></script>
<script src="/_js/layui/layui.all.js"></script>
<script src="/_js/dlg_layer.js"></script>
<link  href="/_js/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css" >
<script src="/opencharts/dist/oc.js"></script>
<link type="text/css" href="/opencharts/src/css/oc.css" rel="stylesheet" />
<link  href="/_js/font4.7.0/css/font-awesome.css"  rel="stylesheet" type="text/css" >
	
<style>
body {
	margin: 0px;
	padding: 0px;
	font-size: 12px;
	text-align: center;
-moz-user-select : none;
-webkit-user-select: none;
}
.top {
	position: fixed;
	
	left: 0;
	top: 0;
	bottom: 0;
	z-index: 999;
	height: 45px;
	width:100%;
	text-align: left;
	margin:0px;
	padding:0px;
	overflow: hidden
}


.left {
	position: fixed;
	float: left;
	left: 0;
	top: 0px;
	bottom: 0;
	z-index: 999;
	width: 45px;
	overflow-x: hidden
	background-color: #aaaaaa;
}


.left_pan {
	position: fixed;
	float: left;
	left: 45px;
	top: 45px;
	bottom: 0;
	z-index: 999;
	width: 145px;
	overflow-x: hidden
	
}

.right {
	position: fixed;
	float: right;
	right: 0;
	top: 0px;
	bottom: 0;
	z-index: 999;
	width: 45px;
	height: 100%;
	overflow-x: hidden
	background-color: #aaaaaa;
}

.mid {
	position: absolute;
	left: 45px;
	right: 0px;
	top: 0px;
	bottom: 0;
	z-index: 998;
	width: auto;
	margin:0px;
	overflow: hidden;
	box-sizing: border-box
}

.top_btn
{
	color:#009999;
	margin-top: 5px;
	margin-left:20px;
	cursor: pointer;
}

.top i:hover
{
color: #fdd000;
}

.lr_btn
{
	margin-top: 10px;
	color:#009999;
	cursor: pointer;
}

.lr_btn_div
{
	margin-top: 0px;
	color:#858585;
	background-color:#eeeeee;
	cursor: pointer;
}

.lr_btn_btm
{
	margin-bottom: 20px;
	position:absolute;
	left:5px;
	bottom:20px;
	color:#858585;
	
	cursor: pointer;
}

.left i:hover{
color: #fdd000;
}

.lr_btn i:hover
{
color: #fdd000;
}

.right i:hover{
color: #ffffff;
}

.props_panel_edit
{
	position0: absolute;
	left: 0px;
	right: 0px;
	top: 18px;
	bottom0: 50px;
	height:80%
	z-index: 998;

	overflow-y: auto;
	vertical-align:top;
	box-sizing: border-box
}

.props_panel_pos
{
	position: absolute;
	bottom: 50px;
	
	z-index: 998;
	box-sizing: border-box
}

.top_menu_close {
    font-family: Tahoma;
    border: solid 2px #ccc;
    padding: 0px 5px;
    text-align: center;
    font-size: 12px;
    color: blue;
    position: absolute;
    top: 2px;
    line-height: 14px;
    height: 14px;
    width: 26px;
    border-radius: 14px;
    -moz-border-radius: 14px;
    background-color: white;
}

.top_menu_left{
	position:absolute;z-index: 50000;width: 25;height:25;TOP:100px;right:0px;
	text-align: center;
	font-size: 12px;
 font-weight: bold;
 background-color:#4770a1;
 color: #eeeeee;
 line-height: 35px;
 border:2px solid;
border-radius:5px;
//box-shadow: 5px 5px 2px #888888;
}

.top_win_left
{
border:solid 3px gray;		
background-color:silver;
top:0;
left:30;
height:230;
width:830;
padding:1px;
line-height:21px;
border-radius:15px;
-moz-border-radius:15px;
box-shadow:0 5px 27px rgba(0,0,0,0.3);
-webkit-box-shadow:0 5px 27px rgba(0,0,0,0.3);
-moz-box-shadow:0 5px 27px rgba(0,0,0,0.3);
_position:absolute;
_display:block;
z-index:10000;
}

.left_panel_win
{
position:absolute;display:none;z-index:1000;left:45px;
background-color: #eeeeee;
top:0px;height:100%;
}

.right_panel_win
{
position:absolute;z-index:1000;right:0px;
top:0px;height:100%;
border: 1;
 font: 15;
  width: 245px;
  background-color: #f2f2f2;

}


.left_panel_bar
{
height:30px;
}

.layui-tab {
    margin: 0px;
    padding:0px;
    text-align: left!important;
    height:35px;
}
.layui-tab-content {
    padding: 0px;
}

</style>
</head>
<body class="layout-body">

		<div class="left " style="overflow: hidden;">
			<div id="leftcat_cxt_sub_hmi" onclick="leftcat_sel('cxt_sub_hmis','Context Sub HMI',150)" title="Context Sub-HMI"><i class="fa fa-cube fa-3x lr_btn"></i><br>&nbsp;</div>
			<div id="leftcat_basic_di" onclick="leftcat_sel('basic_di','Basic',230)" title="Basic"><i class="fa fa-circle-o fa-3x lr_btn" ></i><br>&nbsp;</div>
			<div id="leftcat_divcomp" onclick="leftcat_sel('divcomp','Components',330)" title="Controller"><i class="fa fa-cog fa-3x lr_btn"></i><br> &nbsp;</div>
			<div id="leftcat_basic_icon" onclick="leftcat_sel('basic_icon','Basic Icons')" title="Icon"><i class="fa fa-picture-o fa-3x lr_btn"></i><br>&nbsp;</div>
			<div id="leftcat_pic" onclick="leftcat_sel('pic','Pictures Lib',500)" title="Pic Library"><i class="fa fa-cubes fa-3x lr_btn"></i><br>&nbsp;</div>
			<div id="leftcat_comp" onclick="leftcat_sel('comp','HMI Components',500)" title="Components"><i class="fa fa-cogs fa-3x lr_btn"></i><br>&nbsp; </div>
		</div>
		<div id="left_panel" class="left_panel_win" pop_width="300px" >
			<div class="left_panel_bar" >
				<span id="left_panel_title" style="font-size: 20px;">Basic Shape</span><div onclick="leftcat_close()" class="top_menu_close"  style="position:absolute;top:1px;right:10px;top:2px;">X</div>
			</div>
			<iframe id="left_pan_iframe" src="" style="width:100%;height:90%;overflow:hidden;margin: 0px;border:0px;padding: 0px" ></iframe>
		</div>
		<div class="mid">
			<div id="main_panel" style="border: 0px solid #000;margin:0px; width: 100%; height: 100%; background-color: #1e1e1e" ondrop0="drop(event)" ondragover0="allowDrop(event)">
				<div id="win_act_store" style="position: absolute; display: none; background-color: #cccccc;z-index:1">
					<div class="layui-btn-group">
					  <button type="button" class="layui-btn layui-btn-primary layui-btn-sm" title="新增数据库"  onclick="store_add_db()">
					    <i class="layui-icon">&#xe654;</i>
					  </button>
					  <button type="button" class="layui-btn layui-btn-primary layui-btn-sm">
					    <i class="layui-icon">&#xe642;</i>
					  </button>
					  <button type="button" class="layui-btn layui-btn-primary layui-btn-sm">
					    <i class="layui-icon">&#xe640;</i>
					  </button>
					</div>
				</div>
				
				<div id="win_act_conn" style="position: absolute; display: none; background-color: #cccccc;z-index:1">
					<div class="layui-btn-group" style="width:40px">
					  <button type="button" class="layui-btn layui-btn-primary layui-btn-sm" title="新增接入"  onclick="conn_add()">
					    <i class="layui-icon">&#xe654;</i>
					  </button>
					  <button type="button" class="layui-btn layui-btn-primary layui-btn-sm">
					    <i class="layui-icon">&#xe642;</i>
					  </button>
					  <button type="button" class="layui-btn layui-btn-primary layui-btn-sm">
					    <i class="layui-icon">&#xe640;</i>
					  </button>
					</div>
				</div>

		</div>
		

<div style="position:absolute;right:5px;top:5px;z-index:1001;color:#1e1e1e" title="show or hide properties panel" id="btn_prop_showhidden">&nbsp;&nbsp;<i class="fa fa-bars fa-lg"></i>&nbsp;&nbsp;</div>

<div id='edit_panel'  class="right_panel_win" >
	<div id="p_info" style="height: 20" class0="props_panel_pos">&nbsp;</div>
		<div class="layui-tab">
	  <ul class="layui-tab-title">
	    <li class="layui-this">Properties</li>
	    <li>Events</li>
	  </ul>
	  <div class="layui-tab-content">
	    <div class="layui-tab-item layui-show">
	    	<div  id='edit_props'  style="width:100%;height:500px;"></div>
		</div>
	    <div class="layui-tab-item">
	      <div  id='edit_events'  style="width:100%;height:500px;"></div>
		</div>
	   
	  </div>
	</div>
</div>

 
		</div>





<script>


var tab_id = "<%=tabid%>" ;

var layuiEle ;
var path="<%=path%>";

var cxtnodeid = "<%=cxtnodeid%>" ;
var editorname = "<%=uahmi.getTitle()%>" ;

layui.use('element', function(){
	layuiEle = layui.element;
  
  //…
});

function add_tab()
{
	
}
var panel = null;
var editor = null ;

var loadLayer = null ;
var intedit =null;

var hmiModel=null;
var hmiView=null;

function on_panel_mousemv(p,d)
{
	$("#p_info").html("["+p.x+","+p.y+"] - ("+Math.round(d.x*10)/10+","+Math.round(d.y*10)/10+")");
}

function init_iottpanel()
{
	hmiModel = new oc.hmi.HMIModel({
		temp_url:"hmi_editor_ajax.jsp?op=load&path="+path,
		comp_url:"comp_ajax.jsp?op=comp_load",
		hmi_path:path
	});
	
	panel = new oc.hmi.HMIPanel("main_panel",{
		on_mouse_mv:on_panel_mousemv,
		on_model_chg:on_model_chg
	});
	editor = new oc.DrawEditor("edit_props","edit_events",panel,{
		plug_cb:editor_plugcb
	}) ;
	hmiView = new oc.hmi.HMIView(hmiModel,panel,editor,{
		copy_paste_url:"util/copy_paste_ajax.jsp"
	});
	
	hmiView.init();
	
	loadLayer = hmiView.getLayer();
	intedit = hmiView.getInteract();
}


var editor_plugcb_pm=null ;

function editor_plugcb(jq_ele,tp,di,name,val)
{
	editor_plugcb_pm = {editor:editorname,editor_id:cxtnodeid,path:path,di:di,name:name,val:val,cxtnodeid:cxtnodeid} ;

	if(tp.indexOf("event_")==0)
	{
		dlg.open("../util/di_editplug_"+tp+".jsp",
				{title:"Edit Event",w:'500px',h:'400px'},
				['Ok','Cancel'],
				[
					function(dlgw)
					{
						var ret = dlgw.editplug_get() ;
						 var cjs = ret.clientjs ;
						 var sjs = ret.serverjs;
						 if(cjs==null)
							 cjs = "" ;
						 if(sjs==null)
							 sjs = "" ;
						 di.setEventBinder(name,cjs,sjs) ;
						 editor.refreshEventEditor();
						 dlg.close();
					},
					function(dlgw)
					{
						dlg.close();
					}
				]);
	}
	else
	{
		var tt = "Edit Properties" ;
		if(tp=="prop_bind")
			tt = "Bind Properties" ;
		dlg.open("../util/di_editplug_"+tp+".jsp",
				{title:tt,w:'500px',h:'400px'},
				['Ok','Cancel'],
				[
					function(dlgw)
					{
						if(tp=="prop_bind")
						{
							var ret = dlgw.editplug_get() ;
							console.log(ret) ;
							di.setPropBinder(name,ret.jstxt,ret.bexp) ;
							editor.refreshPropBindEditor();
						}
						else
						{
							var ret = dlgw.editplug_get() ;
							var v = ret.v ;
							jq_ele.val(v) ;
							editor.applyUI2SelectedItem();
						}
						dlg.close();
					},
					function(dlgw)
					{
						dlg.close();
					}
				]);
	}
	
}


function do_apply()
{
	if(!editor.applyUI2SelectedItem())
	{
		dlg.msg("apply failed");
		return ;
	}
	//panel.on_draw();
}

function do_add_di(dicn,opts)
{
	if(intedit==null)
		return;
	if(!intedit.setOperAddItem(dicn,opts))
	{
		dlg.msg("set oper error");
		return;
	}
	
}

function do_add_pts(tp,opts)
{
	if(intedit==null)
		return;
	if(!intedit.setOperAddPts(tp,opts))
	{
		dlg.msg("set oper error");
		return;
	}
	
}

function do_add_unit_ins(unitid)
{
	if(intedit==null)
		return;
	if(!intedit.setOperAddUnitIns(unitid))
	{
		dlg.msg("set oper add unit ins error");
		return;
	}
	
}

function on_model_chg()
{
	tab_notify();
}

function tab_save()
{
	var pm = {} ;
	pm.op="save" ;
	pm.path=path;
	//pm.hmiid=hmiid;
	pm.txt = JSON.stringify(loadLayer.extract(null)) ;
	oc.util.doAjax("hmi_editor_ajax.jsp",pm,(bsucc,ret)=>{
        dlg.msg(ret);
        if("save ok"==ret)
        {
        	panel.setModelDirty(false) ;
        	tab_notify();
        }
    });
}

function tab_notify()
{
	if(parent==window || !(parent.tab_notify))
		return;
	parent.tab_notify(tab_id);
}

function tab_st()
{
	return {tabid:tab_id,dirty:panel.isModelDirty()} ;
}

function draw_fit()
{
	if(loadLayer==null)
		return ;
	loadLayer.ajustDrawFit();
}

var bInRefresh=false;
var lastRefreshDT = -1 ;

function refresh_dyn()
{
	if(bInRefresh)
		return ;
	if(new Date().getTime()-lastRefreshDT<2000)
		return ;
	try
	{
		bInRefresh = true;
		hmiModel.refreshDyn(function(){
			lastRefreshDT = new Date().getTime();
			bInRefresh = false;
		});
	}
	finally
	{
		
	}
}

//setInterval("hmiModel.refreshDyn();",5000);
//setInterval("refresh_dyn()",2000);

function btn_load_unit()
{
	send_ajax("t_ajax.jsp","id=u_u1",function(bsucc,ret){
		//alert(ret);
		oc.DrawUnit.addUnitByJSON(ret);
	}) ;
}

layui.use('form', function(){

});


//////////edit panel
$(document).ready(function()
{
	$('#edit_panel_btn').click(function()
	{
		$('#edit_panel').slideToggle();
		$(this).toggleClass("cerrar");
   	});
 		
	$('#lr_btn_fitwin').click(function()
	{
		draw_fit();
   	});
	
	//init_prop_evt_tab();

 	init_iottpanel();
 	
 	//init_top_menu();
});

function slide_toggle(obj,w)
{
	if(obj.attr('topm_show')=='1')
	{
		obj.animate({width: '0px', opacity: 'hide'}, 'normal',function(){ obj.hide();});
		obj.attr('topm_show',"0") ;
		return 0 ;
	}
	else
	{
		obj.animate({width: w, opacity: 'show'}, 'normal',function(){ obj.show();});
		obj.attr('topm_show',"1") ;
		return 1 ;
	}
}

function hide_toggle(obj)
{
	obj.hide();
	obj.attr('topm_show',"0") ;
}



var left_cur = null ;

function leftcat_sel(n,t,w)
{
	if(w==undefined)
		w = "300px" ;
	else
		w = w+"px" ;
	if(left_cur!=null)
	{
		//slide_toggle($('#left_panel'));
		hide_toggle($('#left_panel'))
		if(left_cur==n)
		{//close only		
			$('.lr_btn_div').removeClass("lr_btn_div");
			left_cur=null ;
			return ;
		}
	}
	
	//if()
	left_cur=n;
	$('.lr_btn_div').removeClass("lr_btn_div");
	$("#leftcat_"+n).addClass("lr_btn_div") ;
	$("#left_panel_title").html(t) ;
	if("basic_icon"==n)
		document.getElementById("left_pan_iframe").src="../pic/icon_fa.jsp" ;
	else
		document.getElementById("left_pan_iframe").src="hmi_left_"+n+".jsp?path="+path ;
	
	//top_menu_hide_other('filter');
	//$('#left_panel').hide();
	//$('#topm_filter_panel').slideToggle();
	var r = slide_toggle($('#left_panel'),w);
	//$(this).toggleClass("top_menu_tog");
}

function leftcat_close()
{
	$('.lr_btn_div').removeClass("lr_btn_div");
	left_cur=null ;
	slide_toggle($('#left_panel'));
}

var resize_cc = 0 ;
$(window).resize(function(){
	panel.updatePixelSize() ;
	resize_cc ++ ;
	if(resize_cc<=1)
		draw_fit();
	});
	
var b_prop_show=true;
$('#btn_prop_showhidden').click(function(){
	if(b_prop_show)
	{
		$("#edit_panel").css("display","none");
		$("#btn_prop_showhidden").css("color","#ebeef3");
		
		b_prop_show=false;
	}
	else
	{
		$("#edit_panel").css("display","");
		$("#btn_prop_showhidden").css("color","#1e1e1e");
		b_prop_show=true;
	}
});

</script>
</body>
</html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="
	org.iottree.core.*,
	org.iottree.core.conn.*,
				org.iottree.core.util.*,
				org.iottree.core.basic.*,
				org.eclipse.milo.opcua.stack.core.types.structured.*,
				org.eclipse.milo.opcua.stack.core.types.builtin.*,
				org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.*,
				org.eclipse.milo.opcua.sdk.client.nodes.*,
	java.io.*,
	java.util.*,
	java.net.*,
	java.util.*
	"%><%!
	
	%><%
	String path = request.getParameter("path") ;
	String vtstr = request.getParameter("vt") ;
	String title = request.getParameter("title") ;
	if(path==null)
		path = "" ;
	if(vtstr==null)
		vtstr= "int16" ;
	if(title==null)
		title = "" ;
%>
<html>
<head>
<title>Simple Tag Edit</title>
<jsp:include page="../head.jsp"></jsp:include>
<script>
dlg.resize_to(500,500);
</script>
</head>
<body>
<form class="layui-form" action="">
  <div class="layui-form-item">
    <label class="layui-form-label">Path</label>
    <div class="layui-input-block">
      <input type="text" id="path" name="path" value="<%=path%>"  class="layui-input">
    </div>

  </div>
  <div class="layui-form-item">
    <label class="layui-form-label">Value Tp</label>
    <div class="layui-input-inline" style="width:80px">
          	<select id="vt" lay-filter="vt" >
<%

for(UAVal.ValTP vtp:UAVal.ValTP.values())
{
	String vttp = vtp.getStr();
%><option value="<%=vttp%>"><%=vttp %></option>
<%
}
%>
    	</select>
    </div>
	<label class="layui-form-label">Title</label>
    <div class="layui-input-inline">
      <input type="text" id="title" name="title" value="<%=title %>"  class="layui-input">
    </div>
  </div>

 </form>
</body>
<script type="text/javascript">
var vtstr='<%=vtstr%>';
var form = null;
layui.use('form', function(){
	  form = layui.form;
	  form.on("select(vt)",function(obj){
		  setDirty();
		  });
	  $("#path").on("input",function(e){
		  setDirty();
		  });
	  $("#title").on("input",function(e){
		  setDirty();
		  });
	  $("#vt").val(vtstr) ;
	  form.render(); 
});

var _tmpid = 0 ;

var bdirty=false;


function isDirty()
{
	return bdirty;
}
function setDirty()
{
	bdirty= true;
	dlg.btn_set_enable(1,true);
}

	
function win_close()
{
	dlg.close(0);
}

function get_input_val(id,defv,bnum)
{
	var n = $('#'+id).val();
	if(n==null||n=='')
	{
		return defv ;
	}
	if(bnum)
		return parseInt(n);
	return n;
}

function get_val(id,title,cb,bnum)
{
	var v = $('#'+id).val();
	if(v==null||v=='')
	{
		cb(false,'Please input '+title) ;
		throw "no "+title+" input" ;
	}
	if(bnum)
	{
		v = parseInt(v);
		if(v==NaN)
		{
			cb(false,'Please input valid '+title) ;
			throw "invalid "+title+" input" ;
		}
	}
	
	return v ;
}

function do_submit(cb)
{
	var path = $("#path").val() ;
	var title =  $("#title").val() ;
	var vtstr = $("#vt").val() ;
	if(path==null||path=='')
	{
		dlg.msg("no path input") ;
		return ;
	}
	if(vtstr==null||vtstr=='')
	{
		dlg.msg("no Value Tp Selected") ;
		return ;
	}
	
	if(!chk_var_path(path,true))
	{
		dlg.msg("path must be combined by var name,which must use a-z A-Z 0-9 _ and a-z A-Z first");
		return;
	}

	cb(true,{path:path,vt:vtstr,title:title});
}

</script>
</html>
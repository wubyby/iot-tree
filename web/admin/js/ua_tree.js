function UAJoinPt(id,title,x,y,d)
{
	this.id = id ;
	this.title = title ;
	this.x = x ;
	this.y = y ;
	this.d = d ;

	this.check_in=function(x,y)
	{
		return x>=this.x && x <=this.x+this.d && y>=this.y && y<=this.y+this.d ;
	}

	this.is_conn_pt=function()
	{
		return this.id.indexOf('conn_')==0;
	}
	this.is_tree_pt=function()
	{
		return this.id.indexOf('ch_')==0;
	}

	this.get_center_x=function()
	{
		return this.x + this.d/2 ;
	}
	this.get_center_y=function()
	{
		return this.y + this.d/2 ;
	}
}

function UATree_CXTMENU_ACT(data)
{
	var inst = $.jstree.reference(data.reference);
    var node = inst.get_node(data.reference).original;
	//console.log(data.item) ;
    //console.log(obj) ;
    
	console.log(data.item.op_name,node) ;
	var act = data.item.op_action
	if(act!=undefined&&act!=null&&act!="")
		act(node,data.item.op_name) ;
}

function UATree(tree_opt)
{
	//this.id = id ;
	//this.opt = opt ;
	this.cxt_menu={} ;
	this.data_url = tree_opt.data_url ;
	this.on_selected=tree_opt.on_selected;
	//his.on_tree_chged = tree_opt.on_tree_chged;
	this.on_tree_scrolled = tree_opt.on_tree_scrolled;
	this.id = tree_opt.eleid ;
	//this.tree_data_url = tree_opt.data_url ;
	//this.tree_cxt_menu = tree_opt.cxt_menu;

	this.jsTree = null ;
	this.belongTo = null ;
	
	this.get_cxt_menu=function(tp,tn)
	{
		if(!tree_opt||!tree_opt.cxt_menu)
			return ;
		var tmp = tree_opt.cxt_menu[tp] ;
		if(tmp==undefined||tmp==null)
			return ;
		var r = null;// this.cxt_menu[tp] ;
		if(r!=undefined&&r!=null)
			return r ;
		r={} ;
		for(var op of tmp)
		{//{op_name:"new_dev",op_title:"xxx",op_icon:"fa fa-tasks fa-lg",action:act_ch_new_dev}
			var op_name = op["op_name"] ;
			if(op_name==undefined||op_name==null)
				continue ;
			if(op.op_chk)
			{
				if(!op.op_chk(tn))
					continue ;
			}
			r[op_name]={op_name:op_name,op_action:op.op_action,label:op.op_title,icon:op.op_icon, "separator_after": true,action:UATree_CXTMENU_ACT} ;
		}
		//this.cxt_menu[tp] =r ;
		return r ;
	}
	
	this.get_node_by_id=function(id)
	{
		var node = this.jsTree.jstree("get_node", id);
		if(node==null)
			return null ;
		return node.original ;
	}
	
	this.init=function()
	{
		$.jstree.destroy();
		this.jsTree = $('#'+this.id).jstree(
				{
					'core' : {
						'data' : {
							'url' : this.data_url,
							"dataType" : "json"
						},
						'check_callback' : function(o, n, p, i, m) {
							if(m && m.dnd && m.pos !== 'i') { return false; }
							if(o === "move_node" || o === "copy_node") {
								if(this.get_node(n).parent === this.get_node(p).id) { return false; }
							}
							return true;
						},
						'themes' : {
							'responsive' : false,
							//'variant' : 'small',
							'stripes' : true
						}
					},
					'sort0' : function(a, b) {
						return this.get_type(a) === this.get_type(b) ? (this.get_text(a) > this.get_text(b) ? 1 : -1) : (this.get_type(a) >= this.get_type(b) ? 1 : -1);
					},
					'contextmenu' : { //
						
						'items' :(node)=>{
							//this.get_type(node)==='ch''
							console.log(node)
							var tp = node.original.type
							console.log(tp) ;
							return this.get_cxt_menu(tp,node.original) ;
		                }
					},
					'types' : {
						'default' : { 'icon' : 'folder' },
						'file' : { 'valid_children' : [], 'icon' : 'file' }
					},
					'unique' : {
						'duplicate' : function (name, counter) {
							return name + ' ' + counter;
						}
					},
					'plugins' : ['state','dnd','types','contextmenu','unique']
				}
		);

		$("#"+this.id).parent().scroll(()=>{
			if(this.on_tree_scrolled)
				this.on_tree_scrolled() ; 
		});

		this.jsTree.on('activate_node.jstree',(e,data)=>{
			if(this.on_selected!=undefined&&this.on_selected!=null)
				this.on_selected(data.node.original)
		})
		.on('delete_node.jstree', function (e, data) {
			$.get('?operation=delete_node', { 'id' : data.node.id })
				.fail(function () {
					data.instance.refresh();
				});
		})
		.on('create_node.jstree', function (e, data) {
			$.get('?operation=create_node', { 'type' : data.node.type, 'id' : data.node.parent, 'text' : data.node.text })
				.done(function (d) {
					data.instance.set_id(data.node, d.id);
				})
				.fail(function () {
					data.instance.refresh();
				});
		})
		.on('rename_node.jstree', function (e, data) {
			$.get('?operation=rename_node', { 'id' : data.node.id, 'text' : data.text })
				.done(function (d) {
					data.instance.set_id(data.node, d.id);
				})
				.fail(function () {
					data.instance.refresh();
				});
		})
		.on('move_node.jstree', function (e, data) {
			$.get('?operation=move_node', { 'id' : data.node.id, 'parent' : data.parent })
				.done(function (d) {
					//data.instance.load_node(data.parent);
					data.instance.refresh();
				})
				.fail(function () {
					data.instance.refresh();
				});
		})
		.on('copy_node.jstree', function (e, data) {
			$.get('?operation=copy_node', { 'id' : data.original.id, 'parent' : data.parent })
				.done(function (d) {
					//data.instance.load_node(data.parent);
					data.instance.refresh();
				})
				.fail(function () {
					data.instance.refresh();
				});
		})
		.on('changed.jstree', function (e, data) {
			//console.log(data);


		});
		return this.jsTree;
	}
	
	this.get_join_pts=function()
	{
		var r = [] ;
		
		var x = this.belongTo.canvas[0].width-12 ;
		$("#"+this.id+" img").each((index, element)=>{
			var id = $(element).attr("id");
			if(id==null)
				return ;
			if(id.indexOf("ch_")!=0)
				return ;
			var yp = this.get_ypos($(element));
			r.push(new UAJoinPt(id,"",x,yp,10)) ;
		}) ;
		//console.log(idstr) ;
		//
		return r ;
	}

	this.get_ypos=function(ele)
	{
		//var p_t1 = ele.offset().top;
		
		//console.log(p_t1,) ;
		return ele.offset().top - $("#"+this.id).parent().offset().top ;//- c_t1 ;
		//return ele[0].clientTop-$("#"+this.id)[0].clientTop;
	//	return  ele.offset().top - $("#"+this.id).offset().top-ele.scrollTop();
	}
}

function UAConn(conn_opt)
{
	this.conn_eleid = conn_opt.eleid ;
	this.conn_data_url = conn_opt.data_url;
	this.on_conn_chged=conn_opt.on_conn_chged;
	this.conn_data_loaded = conn_opt.data_loaded ;
	this.conn_ui_showed = conn_opt.on_ui_showed ;

	this.joins = [] ;
	
	this.connvue = new Vue({
		el: '#'+this.conn_eleid,
		data:{
			connectors:[
				
			]
		}
	});

	
	
	this.init=function()
	{
		$("#"+this.conn_eleid).scroll(()=>{
			if(this.on_conn_chged)
				this.on_conn_chged() ; 
		});
		this.refresh_ui() ;
	}

	this.refresh_ui=function()
	{
		var pm = {
				type : 'post',
				url :this.conn_data_url,
				data :{}
			};
		$.ajax(pm).done((ret)=>{
			this.connvue.connectors=ret.conn_pts ;
			if(this.conn_ui_showed)
				this.connvue.$nextTick(this.conn_ui_showed)
			this.joins = ret.joins;
			this.conn_data_loaded() ;
		}).fail((req, st, err)=>{
			dlg.msg(err);
		});
	}

	this.get_join_pts=function()
	{
		var r = [] ;
		
		$("#"+this.conn_eleid+" .subitem_li").each((index, element)=>{
			var id = $(element).attr("id");
			var yp = this.get_ypos($(element));
			r.push(new UAJoinPt(id,"",0,yp,10)) ;
		}) ;
		//console.log(idstr) ;
		//
		return r ;
	}

	this.get_ypos=function(ele)
	{
		//return ele.offset().top - $("#"+this.conn_eleid).offset().top ;
		return ele.offset().top - $("#"+this.conn_eleid).parent().offset().top ;
	}


	this.check_has_join=function(pt)
	{
		if(this.joins==null)
			return null;
		var id = pt.id ;
		if(id.indexOf('conn_')==0)
		{
			var cid = id.substring(5) ;
			for(var jo of this.joins)
			{
				if(jo.connid==cid)
					return jo ;
			}
		}
		if(id.indexOf("ch_")==0)
		{
			var chid = id.substring(3) ;
			for(var jo of this.joins)
			{
				if(jo.chid==chid)
					return jo;
			}
		}

		return null;
	}

}

//ua connections web control
function UAPanel(conn_opt,connch_opt,tree_opt)
{
	this.connch_eleid = connch_opt.eleid ;
	this.join_url = connch_opt.join_url ;
	//this.url_data = opt.url_data ;
	//this.url_st = opt.url_st ;
	this.conn_cxt = null ;
	this.canvas = null ;

	this.ua_conn = new UAConn({
		eleid:conn_opt.eleid,
		data_url:conn_opt.data_url,
		data_loaded :()=>{
			setTimeout(()=>{
				this.redraw(false,true,false);
			},300);
		},
		on_conn_chged:()=>{
			this.redraw(false,true,false) ;
		},
		on_ui_showed:conn_opt.ui_showed
	});

	this.getConnVue=function()
	{
		return this.ua_conn.connvue ;
	}
			
	this.ua_tree = new UATree({
		eleid:tree_opt.eleid,
		data_url:tree_opt.data_url,
		cxt_menu:tree_opt.cxt_menu,
		on_selected:(tn)=>{
			if(tree_opt.on_selected)
				tree_opt.on_selected(tn);
			},
		on_tree_scrolled:()=>{
			this.redraw(false,true,false);
			}
		}) ;
	this.ua_tree.belongTo=this;

	this.init_tree=function()
	{
		
		this.ua_tree.init().on("loaded.jstree",(e,data)=>{
			//console.log("loaded...",data);
			this.redraw(false,true,false) ;
		})
		.on("redraw.jstree",(e,data)=>{
			console.log("redraw tree");
		})
		.on("after_open.jstree",(e,data)=>{
			//console.log("after_open.jstree");
			this.redraw(false,true,false);
		})
		.on("after_close.jstree",(e,data)=>{
			//console.log("after_close.jstree");
			this.redraw(false,true,false);
		}) ;
	}
	
	this.init=function()
	{
		this.ua_conn.init();
		this.init_tree() ;
		
		this.conn_cxt = document.createElement('canvas').getContext('2d');
		var can = $(this.conn_cxt.canvas);
		this.canvas =can ;
		can.css("position", "relative");
		can.css("left", "0px");
		can.css("top", "0px");
		can.css("display","");
		var cch= $("#"+this.connch_eleid);
		can.attr('width', cch[0].offsetWidth) ;
		can.attr('height', cch[0].offsetHeight-5) ;
		//can.attr('height', "100%") ;
		cch.append(can);
		cch.resize(()=>{
			var w = cch[0].offsetWidth;
			var h = cch[0].offsetHeight;
			console.log(w,h)
			can.attr('width', w) ;
			can.attr('height', h-5) ;
			this.redraw(false,true,false);
		});

		this.init_mouse_evt() ;
	}

	this.join_pt_on = null ;
	this.join_pt_down = null ;
	this.cur_join=null;

	this.conn_pts = null ;
	this.tree_pts = null ;
	this.mouse_pos=null;

	this.get_conn_pt=function(id)
	{
		if(this.conn_pts==null)
			return null ;
		for(var pt of this.conn_pts)
			if(pt.id==id)
				return pt ;
		return null ;
	}

	this.get_tree_pt=function(id)
	{
		if(this.tree_pts==null)
			return null ;
		for(var pt of this.tree_pts)
			if(pt.id==id)
				return pt ;
		return null ;
	}

	
	this.get_join_pts=function(jo)
	{
		var cpt = this.get_conn_pt("conn_"+jo.connid) ;
		var tpt = this.get_tree_pt("ch_"+jo.chid) ;
		return {from:cpt,to:tpt} ;
	}

	this.st = 0 ;//0-normal 1-adding 2-conn del 3-tn del

	this.init_mouse_evt=function()
	{
		this.canvas.mousedown((e)=>{
			var x = e.offsetX ;
			var y = e.offsetY ;
			var pt = this.find_move_on(x,y) ;
			if(pt==null)
			{
				if(this.join_pt_down!=null)
				{
					this.join_pt_down = null ;
					this.st = 0 ;
					this.redraw(false,false,false) ;
				}
				return ;
			}

			if(this.join_pt_down != pt)
			{
				if(pt.is_conn_pt())
				{
					var jo = this.ua_conn.check_has_join(pt) ;
					if(jo!=null)
					{
						var jpts = this.get_join_pts(jo) ;
						this.join_pt_down = jpts.to;//pt ;
						this.cur_join = jo;
						this.st = 2 ;
					}
					else
					{
						this.join_pt_down = pt ;
						this.st = 1 ;
					}
				}
				else if(pt.is_tree_pt())
				{
					var jo = this.ua_conn.check_has_join(pt) ;
					if(jo!=null)
					{
						var jpts = this.get_join_pts(jo) ;
						this.join_pt_down = jpts.from;//pt ;
						this.cur_join = jo;
						this.st = 3 ;
					}
				}

				this.redraw(false,false,false) ;
			}
			
		});

		this.canvas.mouseup((e)=>{
			var x = e.offsetX ;
			var y = e.offsetY ;
			switch(this.st)
			{
				case 1:
					if(this.join_pt_on!=null)
					{
						//dlg.msg(this.join_pt_down.id+" "+this.join_pt_on.id) ;
						this.set_join("join_add",this.join_pt_down.id,this.join_pt_on.id);
					}
					this.join_pt_down = null ;
					this.mouse_pos=null ;
					this.st = 0 ;
					this.redraw(false,false,false) ;
					break;
				case 2:
				case 3:
					if(this.join_pt_on!=this.join_pt_down)
						this.set_join("join_del","conn_"+this.cur_join.connid,"ch_"+this.cur_join.chid);
					this.join_pt_down = null ;
					this.mouse_pos=null ;
					this.st = 0 ;
					this.redraw(false,false,false) ;
					break;
				default:
					break;
			}
		});

		this.canvas.mousemove((e)=>{
			var x = e.offsetX ;
			var y = e.offsetY ;

			var b = false;
			var pt = this.find_move_on(x,y) ;

			switch(this.st)
			{
				case 1:
					this.mouse_pos={x:x,y:y} ;
					if(pt!=this.join_pt_on && (pt==null || (pt.is_tree_pt() && this.ua_conn.check_has_join(pt)==null )))
					{
						this.join_pt_on = pt ;
					}
					this.redraw(false,false,false) ;
					break ;
				case 2:
				case 3:
					this.mouse_pos={x:x,y:y} ;
					if(pt!=this.join_pt_on)
					{
						this.join_pt_on = pt ;
					}
					this.redraw(false,false,false) ;
					break;
				default:
					if(pt!=this.join_pt_on)
					{
						if(pt==null||pt.is_conn_pt())
						{
							this.join_pt_on = pt ;
							this.redraw(false,false,false) ;
						}
					}
					break ;
			}
			
		});
	}

	this.find_move_on=function(x,y)
	{
		if(this.conn_pts==null)
			return null;
		for(var pt of this.conn_pts)
			if(pt.check_in(x,y))
			{
				return pt ;
			}
		for(var pt of this.tree_pts)
			if(pt.check_in(x,y))
			{
				return pt ;
			}
		return null ;
	}

	this.refresh_ui=function()
	{
		this.redraw(true,true,true);
	}
	
	this.redraw=function(conn_reload,join_reget,tree_reload)
	{
		var cxt = this.conn_cxt ;
		if(cxt==null)return ;

		if(conn_reload)
		{
			this.ua_conn.refresh_ui();
		}

		if(tree_reload)
		{
			//this.ua_tree.reload() ;
			this.init_tree() ;
		}

		if(conn_reload || tree_reload || join_reget || this.conn_pts==null)
		{
			this.conn_pts = this.ua_conn.get_join_pts() ;
			//console.log(conn_pts) ;
			this.tree_pts = this.ua_tree.get_join_pts() ;
		}
		
		this.canvas[0].width = this.canvas[0].width
		this.draw_conn_ch();
	}

	this.draw_join=function(cxt,jo)
	{
		var st = this.get_conn_pt('conn_'+jo.connid) ;
		var et = this.get_tree_pt('ch_'+jo.chid);
		if(st==null||et==null)
			return ;
		cxt.beginPath() ;
		cxt.moveTo(st.get_center_x(),st.get_center_y());
		cxt.lineTo(et.get_center_x(),et.get_center_y());
		cxt.stroke() ;
	}
	
	this.draw_conn_ch=function()
	{
		var cxt = this.conn_cxt ;
		if(cxt==null)
			return ;
		cxt.fillStyle="#0000ff"
		for(var pt of this.conn_pts)
		{
			if(pt==this.join_pt_on || this.ua_conn.check_has_join(pt))
				cxt.fillRect(pt.x,pt.y,pt.d,pt.d);
			else
				cxt.rect(pt.x,pt.y,pt.d,pt.d);
			cxt.stroke();
		}

		for(var pt of this.tree_pts)
		{
			if(pt==this.join_pt_on || this.ua_conn.check_has_join(pt))
				cxt.fillRect(pt.x,pt.y,pt.d,pt.d);
			else
				cxt.rect(pt.x,pt.y,pt.d,pt.d);
			cxt.stroke();
		}

		for(var jo of this.ua_conn.joins)
		{
			if(this.st==2||this.st==3)
			{
				if(jo==this.cur_join)
					continue ;
			}
			this.draw_join(cxt,jo) ;
		}
		// for(var i = 0 ; i < this.conn_pts.length && i<this.tree_pts.length ; i ++)
		// {
		// 	var st = this.conn_pts[i] ;
		// 	var et = this.tree_pts[i] ;
		// 	cxt.beginPath() ;
		// 	cxt.moveTo(st.get_center_x(),st.get_center_y());
		// 	cxt.lineTo(et.get_center_x(),et.get_center_y());
		// 	cxt.stroke() ;
		// }

		if(this.join_pt_down!=null&&this.mouse_pos!=null)
		{
			cxt.beginPath() ;
			cxt.moveTo(this.join_pt_down.get_center_x(),this.join_pt_down.get_center_y());
			cxt.lineTo(this.mouse_pos.x,this.mouse_pos.y);
			cxt.stroke() ;
		}
	}

	
	this.set_join=function(op,connid,chid)
	{
		var pm = {
				type : 'post',
				url :this.join_url,
				data :{op:op,connid:connid,chid:chid}
			};
		$.ajax(pm).done((ret)=>{
			if(ret.res)
				this.redraw(true,true,false) ;
			else
				dlg.msg(ret.err) ;
		}).fail((req, st, err)=>{
			dlg.msg(err);
		});
		
	}
}

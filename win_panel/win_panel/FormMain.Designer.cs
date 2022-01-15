﻿namespace win_panel
{
    partial class FormMain
    {
        /// <summary>
        /// 必需的设计器变量。
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// 清理所有正在使用的资源。
        /// </summary>
        /// <param name="disposing">如果应释放托管资源，为 true；否则为 false。</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows 窗体设计器生成的代码

        /// <summary>
        /// 设计器支持所需的方法 - 不要修改
        /// 使用代码编辑器修改此方法的内容。
        /// </summary>
        private void InitializeComponent()
        {
            this.components = new System.ComponentModel.Container();
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FormMain));
            this.lbTitle = new System.Windows.Forms.Label();
            this.btnStart = new System.Windows.Forms.Button();
            this.btnStop = new System.Windows.Forms.Button();
            this.linkLabel1 = new System.Windows.Forms.LinkLabel();
            this.btnRegService = new System.Windows.Forms.Button();
            this.btnUnregService = new System.Windows.Forms.Button();
            this.notifyIcon = new System.Windows.Forms.NotifyIcon(this.components);
            this.cxtMenuNotify = new System.Windows.Forms.ContextMenuStrip(this.components);
            this.openPanelMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.menuExit = new System.Windows.Forms.ToolStripMenuItem();
            this.cxtMenuNotify.SuspendLayout();
            this.SuspendLayout();
            // 
            // lbTitle
            // 
            this.lbTitle.AutoSize = true;
            this.lbTitle.Font = new System.Drawing.Font("黑体", 14.25F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(134)));
            this.lbTitle.ForeColor = System.Drawing.Color.Brown;
            this.lbTitle.Location = new System.Drawing.Point(19, 25);
            this.lbTitle.Name = "lbTitle";
            this.lbTitle.Size = new System.Drawing.Size(130, 19);
            this.lbTitle.TabIndex = 0;
            this.lbTitle.Text = "Not Running";
            // 
            // btnStart
            // 
            this.btnStart.Location = new System.Drawing.Point(12, 74);
            this.btnStart.Name = "btnStart";
            this.btnStart.Size = new System.Drawing.Size(124, 35);
            this.btnStart.TabIndex = 1;
            this.btnStart.Text = "Start";
            this.btnStart.UseVisualStyleBackColor = true;
            this.btnStart.Click += new System.EventHandler(this.btnStart_Click);
            // 
            // btnStop
            // 
            this.btnStop.Location = new System.Drawing.Point(154, 74);
            this.btnStop.Name = "btnStop";
            this.btnStop.Size = new System.Drawing.Size(125, 35);
            this.btnStop.TabIndex = 1;
            this.btnStop.Text = "Stop";
            this.btnStop.UseVisualStyleBackColor = true;
            this.btnStop.Click += new System.EventHandler(this.btnStop_Click);
            // 
            // linkLabel1
            // 
            this.linkLabel1.AutoSize = true;
            this.linkLabel1.Location = new System.Drawing.Point(199, 31);
            this.linkLabel1.Name = "linkLabel1";
            this.linkLabel1.Size = new System.Drawing.Size(59, 12);
            this.linkLabel1.TabIndex = 2;
            this.linkLabel1.TabStop = true;
            this.linkLabel1.Text = "Check Log";
            // 
            // btnRegService
            // 
            this.btnRegService.Location = new System.Drawing.Point(12, 163);
            this.btnRegService.Name = "btnRegService";
            this.btnRegService.Size = new System.Drawing.Size(124, 35);
            this.btnRegService.TabIndex = 1;
            this.btnRegService.Text = "Register Service";
            this.btnRegService.UseVisualStyleBackColor = true;
            this.btnRegService.Click += new System.EventHandler(this.btnRegService_Click);
            // 
            // btnUnregService
            // 
            this.btnUnregService.Location = new System.Drawing.Point(154, 163);
            this.btnUnregService.Name = "btnUnregService";
            this.btnUnregService.Size = new System.Drawing.Size(125, 35);
            this.btnUnregService.TabIndex = 1;
            this.btnUnregService.Text = "Unregister Service";
            this.btnUnregService.UseVisualStyleBackColor = true;
            this.btnUnregService.Click += new System.EventHandler(this.btnUnregService_Click);
            // 
            // notifyIcon
            // 
            this.notifyIcon.ContextMenuStrip = this.cxtMenuNotify;
            this.notifyIcon.Icon = ((System.Drawing.Icon)(resources.GetObject("notifyIcon.Icon")));
            this.notifyIcon.Text = "IOTTree Control Panel";
            this.notifyIcon.Visible = true;
            this.notifyIcon.MouseDoubleClick += new System.Windows.Forms.MouseEventHandler(this.notifyIcon_MouseDoubleClick);
            // 
            // cxtMenuNotify
            // 
            this.cxtMenuNotify.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.openPanelMenuItem,
            this.menuExit});
            this.cxtMenuNotify.Name = "cxtMenuNotify";
            this.cxtMenuNotify.Size = new System.Drawing.Size(144, 48);
            // 
            // openPanelMenuItem
            // 
            this.openPanelMenuItem.Name = "openPanelMenuItem";
            this.openPanelMenuItem.Size = new System.Drawing.Size(143, 22);
            this.openPanelMenuItem.Text = "Open Panel";
            this.openPanelMenuItem.Click += new System.EventHandler(this.openPanelMenuItem_Click);
            // 
            // menuExit
            // 
            this.menuExit.Name = "menuExit";
            this.menuExit.Size = new System.Drawing.Size(143, 22);
            this.menuExit.Text = "Exit";
            this.menuExit.Click += new System.EventHandler(this.menuExit_Click);
            // 
            // FormMain
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 12F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(291, 249);
            this.Controls.Add(this.linkLabel1);
            this.Controls.Add(this.btnStop);
            this.Controls.Add(this.btnUnregService);
            this.Controls.Add(this.btnRegService);
            this.Controls.Add(this.btnStart);
            this.Controls.Add(this.lbTitle);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedSingle;
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.MaximizeBox = false;
            this.Name = "FormMain";
            this.Text = "IOTTree Control Panel";
            this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.FormMain_FormClosing);
            this.FormClosed += new System.Windows.Forms.FormClosedEventHandler(this.FormMain_FormClosed);
            this.Load += new System.EventHandler(this.FormMain_Load);
            this.SizeChanged += new System.EventHandler(this.FormMain_SizeChanged);
            this.cxtMenuNotify.ResumeLayout(false);
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.Label lbTitle;
        private System.Windows.Forms.Button btnStart;
        private System.Windows.Forms.Button btnStop;
        private System.Windows.Forms.LinkLabel linkLabel1;
        private System.Windows.Forms.Button btnRegService;
        private System.Windows.Forms.Button btnUnregService;
        private System.Windows.Forms.NotifyIcon notifyIcon;
        private System.Windows.Forms.ContextMenuStrip cxtMenuNotify;
        private System.Windows.Forms.ToolStripMenuItem openPanelMenuItem;
        private System.Windows.Forms.ToolStripMenuItem menuExit;
    }
}


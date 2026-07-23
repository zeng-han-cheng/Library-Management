import fs from 'node:fs/promises';
import { Presentation, PresentationFile } from '@oai/artifact-tool';

const OUT = 'E:/LibraryManagement/LibraryManagement-答辩PPT.pptx';
const PREVIEW = 'E:/LibraryManagement/ppt_work/preview';
const W = 1280, H = 720;
const C = {
  navy: '#0B1F3A', ink: '#10243E', blue: '#2F7CF6', cyan: '#27C3D8',
  sky: '#EAF4FF', pale: '#F6F9FC', white: '#FFFFFF', muted: '#65758B',
  line: '#DCE6F1', green: '#1C9B72', amber: '#E8A02B', red: '#D85B62',
};
const FONT = 'Microsoft YaHei';

function addBox(slide, {x,y,w,h,fill='none',stroke='none',radius=0,name,shadow='shadow-none'}) {
  return slide.shapes.add({ geometry: radius ? 'roundRect' : 'rect', name,
    position:{left:x,top:y,width:w,height:h}, fill,
    line:{style:'solid',fill:stroke,width:stroke==='none'?0:1},
    borderRadius: radius || undefined, shadow });
}
function addText(slide, text, {x,y,w,h,size=20,color=C.ink,bold=false,align='left',valign='top',name,fill='none',stroke='none',radius=0}) {
  const box = addBox(slide,{x,y,w,h,fill,stroke,radius,name});
  box.text = text;
  box.text.style = {fontSize:size,bold,color,alignment:align,verticalAlignment:valign,typeface:FONT};
  return box;
}
function addLine(slide, x1,y1,x2,y2,color=C.line,width=2) {
  return slide.shapes.add({geometry:'line',position:{left:Math.min(x1,x2),top:Math.min(y1,y2),width:Math.abs(x2-x1),height:Math.abs(y2-y1)},fill:'none',line:{style:'solid',fill:color,width}});
}
function addCircle(slide, x,y,d,fill,stroke='none') { return addBox(slide,{x,y,w:d,h:d,fill,stroke,radius:d/2}); }
function addHeader(slide, kicker, title, page) {
  addText(slide,kicker.toUpperCase(),{x:72,y:42,w:270,h:22,size:13,color:C.blue,bold:true});
  addText(slide,title,{x:72,y:72,w:1000,h:52,size:34,color:C.navy,bold:true});
  addLine(slide,72,140,1208,140,C.line,1);
  addText(slide,String(page).padStart(2,'0'),{x:1150,y:42,w:58,h:22,size:13,color:C.muted,bold:true,align:'right'});
}
function addFooter(slide, label='LibraryManagement · 实训项目答辩') {
  addLine(slide,72,672,1208,672,C.line,1);
  addText(slide,label,{x:72,y:684,w:520,h:18,size:11,color:C.muted});
}
function note(slide, text) { slide.speakerNotes.textFrame.setText(text); slide.speakerNotes.setVisible(true); }
function pill(slide, text, x,y,w,fill=C.sky,color=C.blue) {
  addText(slide,text,{x,y,w,h:30,size:13,color,bold:true,align:'center',valign:'center',fill,radius:15});
}
function bullet(slide, text, x,y,w, color=C.ink, mark=C.blue) {
  addCircle(slide,x,y+8,8,mark);
  addText(slide,text,{x:x+22,y,w,h:34,size:18,color});
}
function arrow(slide,x1,y1,x2,y2,color=C.blue) {
  addLine(slide,x1,y1,x2,y2,color,3);
  addText(slide,'›',{x:x2-14,y:y2-17,w:24,h:34,size:30,color,bold:true,align:'center',valign:'center'});
}

function slideCover(p) {
  const s=p.slides.add(); s.background.fill=C.navy;
  // subtle right-side data/library motif
  addCircle(s,930,40,420,'#123A66'); addCircle(s,1030,125,260,'#174C7C');
  for (let i=0;i<5;i++) { addLine(s,780+i*55,115+i*55,1185,125+i*35,'#2E78AE',1); }
  for (const [x,y,c] of [[840,175,C.cyan],[950,260,C.blue],[1050,155,C.cyan],[1130,335,C.blue],[1000,430,C.cyan]]) addCircle(s,x,y,14,c);
  addText(s,'实训项目答辩',{x:84,y:84,w:260,h:24,size:16,color:'#9EC9FF',bold:true});
  addText(s,'Library\nManagement',{x:80,y:170,w:560,h:150,size:58,color:C.white,bold:true});
  addText(s,'图书管理系统', {x:85,y:345,w:430,h:50,size:30,color:'#CDE8FF',bold:true});
  addText(s,'让图书、读者与借阅流程，在一个可追踪的系统里高效协同。',{x:85,y:425,w:600,h:62,size:21,color:'#B7C8DA'});
  pill(s,'Vue 3',85,560,98,'#153B63','#9EDBFF'); pill(s,'Spring Boot',195,560,142,'#153B63','#9EDBFF'); pill(s,'MySQL',347,560,108,'#153B63','#9EDBFF'); pill(s,'AI',465,560,75,'#153B63','#9EDBFF');
  addText(s,'三人小组 · 数据专业 · 2026',{x:85,y:626,w:430,h:22,size:14,color:'#8FA8C2'});
  note(s,'成员 A 开场：用一句话说明作品不是单纯的 CRUD，而是把图书、读者、借阅和 AI 服务串成一条完整业务链。随后介绍今天的汇报结构：问题、设计、实现、演示与分工。');
}

function slideProblem(p) {
  const s=p.slides.add(); s.background.fill=C.white; addHeader(s,'01 · Why this project','我们要解决的不是“录入图书”，而是管理一条完整借阅链路',2);
  addText(s,'传统管理中，书目、读者和借阅记录容易分散在表格、纸质登记和口头沟通里。系统的价值，是把每一步变成可查询、可校验、可追踪的业务数据。',{x:72,y:177,w:690,h:90,size:23,color:C.ink});
  const cards=[['信息分散','书目、读者、公告与借阅记录缺少统一入口',C.blue],['规则难追踪','库存、借阅上限、归还状态需要系统自动校验',C.green],['服务不智能','读者找书和管理员分析运营，缺少辅助决策',C.amber]];
  cards.forEach(([t,b,c],i)=>{const x=72+i*368; addBox(s,{x,y:330,w:330,h:190,fill:C.pale,stroke:C.line,radius:18}); addCircle(s,x+28,360,44,c); addText(s,String(i+1),{x:x+28,y:360,w:44,h:44,size:20,color:C.white,bold:true,align:'center',valign:'center'}); addText(s,t,{x:x+28,y:425,w:270,h:28,size:22,color:C.navy,bold:true}); addText(s,b,{x:x+28,y:464,w:270,h:48,size:16,color:C.muted});});
  addText(s,'项目目标：做一个能真实跑通“登录 → 查书 → 借阅 → 归还 → 统计 → AI 辅助”的前后端分离系统。',{x:72,y:585,w:950,h:40,size:22,color:C.blue,bold:true});
  addFooter(s); note(s,'成员 A：先讲背景，再提出答辩主线。强调系统解决的是流程协同和数据可追踪，而不只是界面展示。');
}

function slideValue(p) {
  const s=p.slides.add(); s.background.fill=C.pale; addHeader(s,'02 · What we built','从业务对象到可运行产品，系统形成了四个闭环',3);
  const items=[['统一身份','管理员与读者登录后进入不同权限视图','ADMIN / READER',C.blue],['资源管理','图书、分类、读者、公告集中管理','CRUD + 分页查询',C.green],['借阅流转','库存变化与借阅记录同步更新','借出 / 归还 / 逾期状态',C.amber],['智能辅助','读者推荐与管理员运营建议','DeepSeek API',C.cyan]];
  items.forEach(([t,b,tag,c],i)=>{const x=72+(i%2)*560,y=185+Math.floor(i/2)*205; addBox(s,{x,y,w:520,h:170,fill:C.white,stroke:C.line,radius:18,shadow:'shadow-sm'}); addCircle(s,x+28,y+28,44,c); addText(s,String(i+1),{x:x+28,y:y+28,w:44,h:44,size:19,color:C.white,bold:true,align:'center',valign:'center'}); addText(s,t,{x:x+92,y:y+29,w:330,h:28,size:22,color:C.navy,bold:true}); addText(s,b,{x:x+92,y:y+68,w:380,h:45,size:16,color:C.muted}); pill(s,tag,x+92,y+121,190,C.sky,C.blue);});
  addFooter(s); note(s,'成员 A：概括产品闭环。每个闭环都对应后面的一个演示动作，让评委知道我们展示的是一套完整系统。');
}

function slideModules(p) {
  const s=p.slides.add(); s.background.fill=C.white; addHeader(s,'03 · Product map','一个入口，覆盖管理员、读者和借阅业务的主要动作',4);
  addBox(s,{x:72,y:182,w:330,h:378,fill:C.navy,stroke:C.navy,radius:22}); addText(s,'用户入口',{x:106,y:218,w:180,h:30,size:23,color:C.white,bold:true}); addText(s,'登录后按角色加载可见功能',{x:106,y:260,w:220,h:45,size:17,color:'#B8CBE0'}); addText(s,'ADMIN',{x:106,y:375,w:160,h:26,size:16,color:'#9EDBFF',bold:true}); addText(s,'READER',{x:106,y:470,w:160,h:26,size:16,color:'#8FE1C2',bold:true});
  const mods=[['首页仪表盘','总量与运营概览'],['图书管理','检索、分页、新增、编辑'],['分类 / 读者','基础数据维护'],['公告管理','发布与查看通知'],['借阅管理','借出、归还、记录'],['统计分析','按分类汇总库存'],['AI 咨询','推荐 / 运营建议']];
  mods.forEach(([t,b],i)=>{const col=i%2,row=Math.floor(i/2),x=470+col*330,y=182+row*105; addBox(s,{x,y,w:290,h:78,fill:C.pale,stroke:C.line,radius:14}); addText(s,t,{x:x+18,y:y+15,w:250,h:24,size:18,color:C.navy,bold:true}); addText(s,b,{x:x+18,y:y+45,w:250,h:20,size:13,color:C.muted});});
  addFooter(s); note(s,'成员 A：展示产品地图，明确管理员和读者共同使用一套系统，但通过角色权限看到不同动作。');
}

function slideArchitecture(p) {
  const s=p.slides.add(); s.background.fill=C.pale; addHeader(s,'04 · Architecture','前后端分离，让页面、业务规则和数据访问各自清晰',5);
  const layers=[['Frontend','Vue 3 · Element Plus · Axios · Vue Router',C.blue],['API / Security','Spring Boot · JWT · AuthInterceptor · RequireRole',C.green],['Business','Controller · Service · Transactional workflow',C.amber],['Data','MyBatis · MySQL 8 · 6 core tables',C.cyan]];
  layers.forEach(([t,b,c],i)=>{const y=180+i*105; addBox(s,{x:112,y,w:720,h:78,fill:C.white,stroke:C.line,radius:16}); addBox(s,{x:112,y,w:170,h:78,fill:c,radius:16}); addText(s,t,{x:112,y:y+21,w:170,h:30,size:20,color:C.white,bold:true,align:'center',valign:'center'}); addText(s,b,{x:315,y:y+21,w:470,h:30,size:19,color:C.ink}); if(i<layers.length-1) arrow(s,820,y+78,820,y+105,C.blue);});
  addBox(s,{x:920,y:190,w:260,h:270,fill:C.navy,stroke:C.navy,radius:20}); addText(s,'Maven 多模块',{x:955,y:222,w:190,h:30,size:22,color:C.white,bold:true,align:'center'}); ['common','pojo','server'].forEach((t,i)=>{addBox(s,{x:963,y:288+i*48,w:175,h:32,fill:'#173B62',radius:16}); addText(s,t,{x:963,y:288+i*48,w:175,h:32,size:15,color:'#B9E8FF',bold:true,align:'center',valign:'center'});}); addText(s,'职责拆分，便于协作与维护',{x:942,y:423,w:215,h:24,size:15,color:'#B8CBE0',align:'center'});
  addFooter(s); note(s,'成员 B：讲技术架构。重点说明三层后端结构和 Maven 多模块拆分如何支持三人协作；不逐行读代码，突出职责边界。');
}

function slideData(p) {
  const s=p.slides.add(); s.background.fill=C.white; addHeader(s,'05 · Data model','数据库用核心实体串起“谁借了什么、什么时候还”',6);
  const nodes=[['admin','管理员'],['reader','读者'],['category','分类'],['book','图书'],['borrow_record','借阅记录'],['notice','公告']];
  const pos=[[90,210],[90,395],[390,210],[390,395],[730,300],[1010,210]];
  nodes.forEach(([t,b],i)=>{const [x,y]=pos[i]; addBox(s,{x,y,w:200,h:74,fill:i===4?C.navy:C.pale,stroke:i===4?C.navy:C.line,radius:16}); addText(s,t,{x:x+18,y:y+13,w:164,h:22,size:17,color:i===4?C.white:C.navy,bold:true,align:'center'}); addText(s,b,{x:x+18,y:y+42,w:164,h:18,size:13,color:i===4?'#B8CBE0':C.muted,align:'center'});});
  arrow(s,290,247,390,247,C.line); arrow(s,590,247,730,334,C.line); arrow(s,290,432,390,432,C.line); arrow(s,590,432,730,360,C.line); arrow(s,930,334,1010,247,C.line); arrow(s,590,247,830,300,C.line);
  addText(s,'借阅记录是业务核心：关联 reader_id、book_id，并在事务中同步库存与归还状态。',{x:92,y:575,w:1050,h:40,size:21,color:C.blue,bold:true}); addFooter(s); note(s,'成员 C：讲数据库。先指出六张核心表，再强调 borrow_record 是业务核心；借书和还书不是单表写入，而是记录与库存同步变化。');
}

function slideFlow(p) {
  const s=p.slides.add(); s.background.fill=C.pale; addHeader(s,'06 · Core flow','借阅流程把业务规则落在服务层，并通过事务保证一致性',7);
  const steps=[['1','提交借阅','校验当前用户与目标读者'],['2','检查规则','读者有效、未超过上限、库存 > 0'],['3','更新库存','decreaseStock(bookId)'],['4','写入记录','设置 dueTime、status、operatorId'],['5','归还闭环','returnBook + increaseStock']];
  steps.forEach(([n,t,b],i)=>{const x=72+i*227; addCircle(s,x+74,195,64,i===4?C.green:C.blue); addText(s,n,{x:x+74,y:195,w:64,h:64,size:25,color:C.white,bold:true,align:'center',valign:'center'}); if(i<steps.length-1) arrow(s,x+141,227,x+214,227,C.line); addText(s,t,{x,y:300,w:210,h:28,size:20,color:C.navy,bold:true,align:'center'}); addText(s,b,{x,y:340,w:210,h:54,size:15,color:C.muted,align:'center'});});
  addBox(s,{x:140,y:470,w:1000,h:104,fill:C.white,stroke:C.line,radius:18}); addText(s,'@Transactional',{x:180,y:500,w:180,h:30,size:24,color:C.red,bold:true}); addText(s,'借阅与库存更新在同一个事务中执行，任一步失败都会回滚，避免“有记录但库存没减”或“库存变了但没有记录”。',{x:400,y:490,w:660,h:54,size:19,color:C.ink}); addFooter(s); note(s,'成员 B：演示或讲解借阅流程。重点回答评委可能追问的并发与一致性：服务层使用事务，库存更新失败会抛出业务异常。');
}

function slideSecurity(p) {
  const s=p.slides.add(); s.background.fill=C.white; addHeader(s,'07 · Security','权限控制贯穿登录、接口和业务动作三层',8);
  const cols=[['登录认证','AuthController\nJWT 签发令牌',C.blue],['请求拦截','AuthInterceptor\n解析用户上下文',C.green],['角色授权','@RequireRole\nADMIN / READER',C.amber]];
  cols.forEach(([t,b,c],i)=>{const x=120+i*365; addBox(s,{x,y:205,w:300,h:180,fill:C.pale,stroke:C.line,radius:20}); addCircle(s,x+112,230,76,c); addText(s,String(i+1),{x:x+112,y:230,w:76,h:76,size:28,color:C.white,bold:true,align:'center',valign:'center'}); addText(s,t,{x:x+30,y:326,w:240,h:26,size:21,color:C.navy,bold:true,align:'center'}); addText(s,b,{x:x+30,y:365,w:240,h:50,size:16,color:C.muted,align:'center'}); if(i<2) arrow(s,x+308,295,x+355,295,C.line);});
  addText(s,'典型差异',{x:120,y:485,w:150,h:28,size:20,color:C.navy,bold:true}); pill(s,'管理员：维护图书 / 读者 / 公告',280,480,300,'#EAF8F2',C.green); pill(s,'读者：查看资源 / 借阅 / 推荐',610,480,290,'#FFF6E5',C.amber); pill(s,'共同：登录 / 查书 / 归还',930,480,220,C.sky,C.blue); addText(s,'密码使用 BCrypt；前端 Axios 自动携带 Bearer Token。',{x:120,y:565,w:800,h:32,size:20,color:C.blue,bold:true}); addFooter(s); note(s,'成员 B：讲安全。用三层模型说明不是只在前端隐藏按钮，后端接口也有角色注解和拦截器保护。');
}

function slideAI(p) {
  const s=p.slides.add(); s.background.fill=C.pale; addHeader(s,'08 · AI feature','AI 被放进真实业务场景：推荐图书，也辅助运营判断',9);
  addBox(s,{x:72,y:190,w:500,h:330,fill:C.navy,stroke:C.navy,radius:22}); addText(s,'AI 咨询',{x:110,y:228,w:220,h:36,size:28,color:C.white,bold:true}); addText(s,'同一个页面，根据角色进入两条不同的业务提示链。',{x:110,y:285,w:350,h:52,size:18,color:'#B8CBE0'}); pill(s,'DeepSeekClient',110,395,210,'#173B62','#9EDBFF'); addText(s,'超时 / 空响应 / 未配置密钥\n统一转为可控业务提示', {x:110,y:445,w:300,h:48,size:16,color:'#B8CBE0'});
  const flows=[['读者','/api/ai/recommend','输入偏好 → 推荐书目 + 理由',C.green],['管理员','/api/ai/advice','输入运营问题 → 库存与运营建议',C.amber]];
  flows.forEach(([role,path,desc,c],i)=>{const y=205+i*165; addBox(s,{x:650,y,w:500,h:125,fill:C.white,stroke:C.line,radius:18}); pill(s,role,680,y+22,90,c===C.green?'#EAF8F2':'#FFF6E5',c); addText(s,path,{x:790,y:y+25,w:300,h:24,size:16,color:C.navy,bold:true}); addText(s,desc,{x:680,y:y+70,w:420,h:24,size:17,color:C.muted});});
  addText(s,'亮点：AI 不是独立聊天框，而是嵌入用户角色与图书馆业务语境。',{x:650,y:555,w:520,h:34,size:19,color:C.blue,bold:true}); addFooter(s); note(s,'成员 C：展示 AI。读者账号演示推荐，管理员账号演示运营建议，并说明后端统一封装 DeepSeek 调用，失败时不会把异常直接暴露给用户。');
}

function slideStack(p) {
  const s=p.slides.add(); s.background.fill=C.white; addHeader(s,'09 · Technology & practice','技术选型对应实训方案：从基础知识到项目实战形成闭环',10);
  const rows=[['前端','HTML / CSS / JavaScript 基础延伸','Vue 3 · Element Plus · Axios · Vite'],['后端','Java / Servlet / Web 服务思想','Java 17 · Spring Boot 3.2.5 · MyBatis'],['数据库','MySQL / SQL / DML / DQL','MySQL 8 · 外键 · 索引 · 分页查询'],['工程与 AI','Maven 项目管理 + AI 模型调用','多模块 Maven · JWT · BCrypt · DeepSeek API']];
  rows.forEach(([a,b,c],i)=>{const y=185+i*100; addBox(s,{x:72,y,w:1136,h:72,fill:i%2?C.pale:C.white,stroke:C.line,radius:12}); addText(s,a,{x:98,y:y+20,w:125,h:25,size:20,color:C.blue,bold:true}); addText(s,b,{x:255,y:y+20,w:365,h:25,size:17,color:C.muted}); addLine(s,640,y+16,640,y+56,C.line,1); addText(s,c,{x:690,y:y+20,w:480,h:25,size:18,color:C.navy,bold:true});});
  addText(s,'项目不是把课程知识点“并列摆放”，而是把它们组合成可运行、可演示、可迭代的应用。',{x:72,y:610,w:1050,h:34,size:21,color:C.blue,bold:true}); addFooter(s); note(s,'成员 C：回应实训方案。把课程里的前端、MySQL、Java、AI、Maven 和项目实战，逐项映射到作品中的具体实现。');
}

function slideDemo(p) {
  const s=p.slides.add(); s.background.fill=C.pale; addHeader(s,'10 · Live demo','现场演示按一条业务主线推进，控制节奏也控制风险',11);
  const demo=[['A','登录与首页','展示角色入口、菜单差异、数据概览'],['A','图书检索','按书名 / ISBN 查询，展示分页结果'],['B','借阅与归还','展示库存变化、借阅记录、归还状态'],['C','统计与 AI','按分类汇总，再完成推荐或运营建议']];
  demo.forEach(([who,t,b],i)=>{const y=190+i*90; addCircle(s,82,y+10,48,who==='A'?C.blue:who==='B'?C.green:C.amber); addText(s,who,{x:82,y:y+10,w:48,h:48,size:19,color:C.white,bold:true,align:'center',valign:'center'}); addText(s,t,{x:160,y,w:240,h:28,size:21,color:C.navy,bold:true}); addText(s,b,{x:420,y:y+3,w:560,h:28,size:18,color:C.muted}); if(i<3) addLine(s,106,y+58,106,y+90,C.line,2);});
  addBox(s,{x:1030,y:208,w:160,h:280,fill:C.navy,stroke:C.navy,radius:18}); addText(s,'演示原则',{x:1050,y:240,w:120,h:26,size:19,color:C.white,bold:true,align:'center'}); ['先讲场景','再点操作','最后说实现'].forEach((t,i)=>addText(s,t,{x:1050,y:305+i*48,w:120,h:24,size:16,color:'#B8CBE0',align:'center'})); addFooter(s); note(s,'成员 A 负责开场和登录，成员 B 负责借阅主链路，成员 C 负责统计与 AI。每个人操作前先说场景，操作后用一句话解释背后的实现。');
}

function slideRoles(p) {
  const s=p.slides.add(); s.background.fill=C.white; addHeader(s,'11 · Team roles','三个人不是轮流念 PPT，而是分别守住一条完整责任链',12);
  const roles=[['成员 A','产品与前端','需求梳理\nVue 页面与路由\n登录 / 首页 / 图书模块\n现场开场演示',C.blue],['成员 B','后端与安全','Controller / Service\n借阅事务与库存规则\nJWT / BCrypt / 权限拦截\n现场核心流程演示',C.green],['成员 C','数据库与 AI','表结构与 MyBatis\n统计查询与数据模型\nDeepSeek 接入与容错\n现场统计 / AI 演示',C.amber]];
  roles.forEach(([name,title,body,c],i)=>{const x=72+i*380; addBox(s,{x,y:190,w:330,h:370,fill:C.pale,stroke:C.line,radius:22}); addCircle(s,x+32,222,62,c); addText(s,name.replace('成员 ',''),{x:x+32,y:222,w:62,h:62,size:23,color:C.white,bold:true,align:'center',valign:'center'}); addText(s,name,{x:x+112,y:230,w:170,h:25,size:20,color:C.navy,bold:true}); addText(s,title,{x:x+112,y:266,w:170,h:22,size:15,color:c,bold:true}); addLine(s,x+32,320,x+298,320,C.line,1); addText(s,body,{x:x+32,y:350,w:255,h:150,size:17,color:C.muted});});
  addFooter(s); note(s,'三人共同讲：每位成员负责一条从设计到实现再到演示的完整链路。答辩时避免重复讲同一个模块，交接时用一句话说明下一位要回答的问题。');
}

function slideClose(p) {
  const s=p.slides.add(); s.background.fill=C.navy;
  addText(s,'我们交付的，不只是一个页面集合。',{x:80,y:115,w:780,h:58,size:38,color:C.white,bold:true});
  addText(s,'它是一套可以登录、查询、借阅、归还、统计，并且能够借助 AI 提供服务的图书管理系统。',{x:82,y:205,w:860,h:70,size:25,color:'#CDE8FF'});
  const facts=[['业务闭环','图书 → 借阅 → 归还 → 库存'],['工程实践','前后端分离 + Maven 多模块'],['可扩展方向','预约、逾期提醒、更多数据分析']];
  facts.forEach(([t,b],i)=>{const x=82+i*365; addBox(s,{x,y:385,w:315,h:130,fill:'#12385E',stroke:'#285A83',radius:18}); addText(s,t,{x:x+24,y:412,w:265,h:25,size:20,color:'#9EDBFF',bold:true}); addText(s,b,{x:x+24,y:455,w:265,h:35,size:16,color:'#B8CBE0'});});
  addText(s,'谢谢各位老师，欢迎提问。',{x:82,y:605,w:560,h:34,size:24,color:C.white,bold:true});
  note(s,'成员 C 收束：回到开场的目标，说明系统已经跑通主要业务闭环，并给出自然的扩展方向。然后三位成员共同进入提问环节。');
}

async function main() {
  await fs.mkdir(PREVIEW,{recursive:true});
  const p = Presentation.create({slideSize:{width:W,height:H}});
  slideCover(p); slideProblem(p); slideValue(p); slideModules(p); slideArchitecture(p); slideData(p); slideFlow(p); slideSecurity(p); slideAI(p); slideStack(p); slideDemo(p); slideRoles(p); slideClose(p);
  for (const [i,s] of p.slides.items.entries()) {
    const png = await p.export({slide:s,format:'png',scale:1});
    await fs.writeFile(`${PREVIEW}/slide-${String(i+1).padStart(2,'0')}.png`, new Uint8Array(await png.arrayBuffer()));
  }
  const montage = await p.export({format:'webp',montage:true,scale:1});
  await fs.writeFile(`${PREVIEW}/montage.webp`, new Uint8Array(await montage.arrayBuffer()));
  const pptx = await PresentationFile.exportPptx(p); await pptx.save(OUT);
  console.log(OUT);
}
main().catch(e=>{console.error(e);process.exitCode=1});

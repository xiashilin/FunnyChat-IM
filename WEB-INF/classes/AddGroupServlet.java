package Servlet;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import Tookit.OperDataBase;
import Tookit.PostData;

public class AddGroupServlet extends HttpServlet{
    PrintWriter out = null;
    String url ="https://api.cn.ronghub.com/group/join.json";
    List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
    PostData postData = new PostData();
    public void doPost(HttpServletRequest request,HttpServletResponse response)throws IOException,ServletException
    {
        out = response.getWriter();
        /*
         * 获取参数
         * */
        String userId = request.getParameter("userId");
        String groupId = request.getParameter("groupId");
        /*
         * 查询数据库
         * */
        String sql = "select * from Groups where groupId=?";
        OperDataBase.init("FunnyChat",sql);
        OperDataBase.addSQLData(1,groupId);
        ResultSet rs =OperDataBase.exeGetDataSQL();
        /*
         * 查询该群是否存在,已经存在则返回错误代码 
         * */
        try{
            if(rs.next()==false){
            //返回错误代码 return;
            out.print("{\"code\":404}");return;
            }
        }catch(Exception e){
            out.print("{\"code\":404}");return;
        }finally{
            OperDataBase.closeConn(); //关闭数据库
        }
	/*
	* 查询是否已经添加,已经添加则返回错误代码
	*/
	sql = "select * from GroupRS where userId=? and groupId=?";
        OperDataBase.init("FunnyChat",sql);
	OperDataBase.addSQLData(1,userId);
        OperDataBase.addSQLData(2,groupId);
        rs =OperDataBase.exeGetDataSQL();
	try{
            if(rs.next()==true){
            //返回错误代码 return;
            out.print("{\"code\":404}");return;
            }
        }catch(Exception e){
            out.print("{\"code\":404}");return;
        }finally{
            OperDataBase.closeConn(); //关闭数据库
        }
        /*
         * 更新数据库
         * */
        sql ="insert into GroupRS(userId,groupId,type)values(?,?,\"member\")";
        OperDataBase.init("FunnyChat",sql);
        OperDataBase.addSQLData(1,userId);
        OperDataBase.addSQLData(2,groupId);
        try{
            if(OperDataBase.exeSQL() == -2){
                out.print("{\"code\":404}");return;
            }
        }catch(Exception e){
            out.print("{\"code\":404}");return;
        }finally{
            OperDataBase.closeConn();
        }
        /*
         * 向融云服务器发送请求
         * */
        nameValuePair.add(new BasicNameValuePair("userId",userId));
        nameValuePair.add(new BasicNameValuePair("groupId",groupId));
        String res = postData.getData(url,nameValuePair);
        out.print(res);
    }
    public void doGet(HttpServletRequest request,HttpServletResponse response)throws IOException,ServletException{
        doPost(request,response);
    }
}

package com.whereim.clearapps.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.whereim.clearapps.bean.PackageBean;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class CmdUtils {
	public static void doCmd(final List<String> cmds,final Handler handler){
		new Thread(){
			public void run() {
				Message msg=handler.obtainMessage();
				try {
					Process process=Runtime.getRuntime().exec("su");
					BufferedReader info=new BufferedReader(new InputStreamReader(process.getInputStream()));
					BufferedReader error=new BufferedReader(new InputStreamReader(process.getErrorStream()));
					DataOutputStream os = new DataOutputStream(process.getOutputStream());
					for (int i = 0; i < cmds.size(); i++) {
						String temp=cmds.get(i);
						if(temp!=null&&!"".equals(temp)&&!"null".equals(temp)){
							os.writeBytes(temp+" \n");
							Log.d("dddd", "����:"+temp);
						}
					}
					os.flush();
					os.close();
					
					String line="";
					String errorLine="";
					do{
						errorLine=error.readLine();
						Log.d("dddd", "����:"+errorLine+"��β:"+"null".equals(errorLine.trim()));
						if(!"".equals(errorLine)&&!"null".equals(errorLine)&&!"NULL".equals(errorLine)){
							throw new IOException();
						}
					}while(errorLine!=null);
					do{
						line=info.readLine();
						Log.d("dddd", "��ȡinfo:"+line);
					}while(line!=null);
					
					Log.d("dddd", "waitFor");
					int i=process.waitFor();
					Log.d("dddd", "ִ�н��:"+i);
					msg.what=0;
				} catch (Exception e) {
					msg.what=1;
					Log.e("dddd", ""+e.getLocalizedMessage());
					e.printStackTrace();
				}
				handler.sendMessage(msg);
				return;
			};
		}.start();
	}
	
	
	
	
	public static Map<String, PackageBean> getWhiteApps(Context ctx){
		Map<String, PackageBean> map=new HashMap<String, PackageBean>();
		List<PackageBean> list=DbFactory.getDb(ctx).findAll(PackageBean.class);
		for (int i = 0; i < list.size(); i++) {
			PackageBean bean=list.get(i);
			map.put(bean.getPackageName(), bean);
		}
		return map;
	}
}
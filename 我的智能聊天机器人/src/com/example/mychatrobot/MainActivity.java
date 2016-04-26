package com.example.mychatrobot;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mychatrobot.JsonBean.WS;
import com.google.gson.Gson;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

public class MainActivity extends Activity {
	
	private StringBuffer mysb=new StringBuffer();
	
	private ArrayList<TalkBean> talkbeanlist=new ArrayList<TalkBean>();
	
	private ListView lvlist;
	
	private int[] pics=new int[]{
		R.drawable.p1,R.drawable.p2,R.drawable.p3,R.drawable.p4
	};

	private String[] answers=new String[]{
			"Լ��","����Ư��Ư��","������"
		};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// ����12345678���滻��������� APPID�������ַ��http://www.xfyun.cn
		// �����ڡ�=���� appid ֮�����������ַ�����ת���
		SpeechUtility.createUtility(this, SpeechConstant.APPID +"=571aebc0");
		
		
		lvlist=(ListView) findViewById(R.id.lvlist);
	}
	
	
	public void listen(View v){
		//1.����RecognizerDialog����
		RecognizerDialog mDialog = new RecognizerDialog(this, null);
		//2.����accent��language�Ȳ���
		mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
		mDialog.setParameter(SpeechConstant.ACCENT, "mandarin");
		//��Ҫ��UI�ؼ�����������⣬����������²������ã�����֮��onResult�ص����ؽ����������
		//���
		// mDialog.setParameter("asr_sch", "1");
		// mDialog.setParameter("nlp_version", "2.0");
		//3.���ûص��ӿ�
		mDialog.setListener(new RecognizerDialogListener() {
			
			@Override
			public void onResult(RecognizerResult results, boolean islast) {
				
				String resultString=results.getResultString();
				
				System.out.println("��д������"+resultString);
				
				System.out.println("�Ƿ����"+islast);
				
				String parseData = parseData(resultString);
				
				mysb.append(parseData);
				
				if(islast){
					String askcontent=mysb.toString();
					//��ʼ����������
					
					TalkBean askbean=new TalkBean(askcontent, "", true, -1);
					
					System.out.println(askcontent+"++++++++++++++++++++++++++++++");
					
					talkbeanlist.add(askbean);
					
					//��ʼ���ش���Ϣ
					
					String answercontent="û����";
					int imageid=-1;
					
					if(askcontent.contains("���")){
						answercontent="���ѽ��";
					}else if(askcontent.contains("����˭")){
						answercontent="�������С���ְ���";
					}else if(askcontent.contains("��Ů")){
						answercontent=answers[(int) (Math.random()*answers.length)];
						imageid=pics[(int) (Math.random()*pics.length)];
						
					}else if(askcontent.contains("�����ǵػ�")){
						answercontent="С����Ģ��";
						imageid=R.drawable.m;
					}
					
					speak(answercontent);
					
					TalkBean answerbBean=new TalkBean("", answercontent, false, imageid);
					
					talkbeanlist.add(answerbBean);
					
					MyAdapter adapter=new MyAdapter();
					
					lvlist.setAdapter(adapter);
					
					adapter.notifyDataSetChanged();
					
					lvlist.setSelection(talkbeanlist.size()-1);
					
				}
				
				
				
				
			}
			
			@Override
			public void onError(SpeechError arg0) {
				
			}
		});
		//4.��ʾdialog��������������
		mDialog.show();

		
	}
	
	public void speak(String answercontent){
		//1.���� SpeechSynthesizer ����, �ڶ������������غϳ�ʱ�� InitListener
		SpeechSynthesizer mTts= SpeechSynthesizer.createSynthesizer(this, null);
		//2.�ϳɲ������ã�������ƴ�Ѷ��MSC API�ֲ�(Android)��SpeechSynthesizer ��
		//���÷����ˣ��������߷����ˣ��û��ɲμ� ��¼12.2
		mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan"); //���÷�����
		mTts.setParameter(SpeechConstant.SPEED, "50");//��������
		mTts.setParameter(SpeechConstant.VOLUME, "80");//������������Χ 0~100
		mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //�����ƶ�
		//���úϳ���Ƶ����λ�ã����Զ��屣��λ�ã��������ڡ�./sdcard/iflytek.pcm��
		//������ SD ����Ҫ�� AndroidManifest.xml ���д SD ��Ȩ��
		//��֧�ֱ���Ϊ pcm �� wav ��ʽ���������Ҫ����ϳ���Ƶ��ע�͸��д���
		mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, "./sdcard/iflytek.pcm");
		//3.��ʼ�ϳ�
		mTts.startSpeaking(answercontent, null);

	}
	protected String parseData(String resultString) {
		
		StringBuffer sb=new StringBuffer();
		
		Gson gson=new Gson();
		JsonBean jsonBean = gson.fromJson(resultString, JsonBean.class);
		
		ArrayList<WS> ws = jsonBean.ws;
		
		for (WS w : ws) {
			String string = w.cw.get(0).w;
			
			sb.append(string);
		}
		
		return sb.toString();
	}
	

	class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return talkbeanlist.size();
		}

		@Override
		public TalkBean getItem(int position) {
			// TODO Auto-generated method stub
			return talkbeanlist.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			
			ViewHodler viewhodler;
			
			TalkBean item=getItem(position);
			
			if(convertView==null){
				
				convertView=View.inflate(getApplicationContext(), R.layout.list_item, null);
				
				viewhodler=new ViewHodler();
				viewhodler.tvask=(TextView) convertView.findViewById(R.id.tv_ask);
				viewhodler.tvanswer=(TextView) convertView.findViewById(R.id.tv_answer);
				viewhodler.llanswer=(LinearLayout) convertView.findViewById(R.id.ll_answer);
				viewhodler.iv_pic=(ImageView) convertView.findViewById(R.id.iv_pic);
				
				convertView.setTag(viewhodler);
						
			}else{
				
				viewhodler = (ViewHodler)convertView.getTag();
			}
			if(item.isask){
				//��ʾ������
				viewhodler.tvask.setVisibility(View.VISIBLE);
				viewhodler.llanswer.setVisibility(View.GONE);
				
				viewhodler.tvask.setText(item.askcontent);
				
				
			}else{
				//��ʾ�ǻش�
				
				viewhodler.tvask.setVisibility(View.GONE);
				viewhodler.llanswer.setVisibility(View.VISIBLE);
				
				viewhodler.tvanswer.setText(item.answercontent);
				
				if(item.imageId>0){
					viewhodler.iv_pic.setVisibility(View.VISIBLE);
					viewhodler.iv_pic.setImageResource(item.imageId);
				}else{
					viewhodler.iv_pic.setVisibility(View.GONE);
				}
			}
			
			
			
			return convertView;
		}
		
		class ViewHodler{
			public TextView tvask;
			public TextView tvanswer;
			public LinearLayout llanswer;
			public ImageView iv_pic;
		}
	}
	
}

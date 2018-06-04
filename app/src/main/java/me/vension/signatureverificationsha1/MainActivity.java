package me.vension.signatureverificationsha1;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.security.MessageDigest;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
   // Used to load the 'native-lib' library on application startup.
	static {
		System.loadLibrary("native-lib");
	}

	protected TextView appSignaturesTv;
	protected TextView jniSignaturesTv;
	protected Button checkBtn;
	protected Button tokenBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initView();
	}

	private void initView() {
		appSignaturesTv = findViewById(R.id.app_signatures_tv);
		jniSignaturesTv = findViewById(R.id.jni_signatures_tv);
		checkBtn = findViewById(R.id.btn_check_sha1);
		tokenBtn = findViewById(R.id.btn_get_token);

		appSignaturesTv.setText("APP签名信息:\n" + getSha1Value(MainActivity.this));
		jniSignaturesTv.setText("jni配置的签名信息:\n" + getSignaturesSha1(MainActivity.this));
		checkBtn.setOnClickListener(this);
		tokenBtn.setOnClickListener(this);
	}


	@Override
	public void onClick(View view) {
		switch (view.getId()){
			case R.id.btn_check_sha1:
				//校验签名
				boolean isCheck = checkSha1(MainActivity.this);
				if(isCheck){
					Toast.makeText(getApplicationContext(),"验证通过",Toast.LENGTH_LONG).show();
				}else{
					Toast.makeText(getApplicationContext(),"验证不通过，请检查valid.cpp文件配置的sha1值",Toast.LENGTH_LONG).show();
				}
				break;
			case R.id.btn_get_token:
				//获取Token
				String result = getToken(MainActivity.this,"12345");
				Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
				break;
		}
	}


    /**
	 * 获取本Sha1值
	 */
	public String getSha1Value(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), PackageManager.GET_SIGNATURES);
			byte[] cert = info.signatures[0].toByteArray();
			MessageDigest md = MessageDigest.getInstance("SHA1");
			byte[] publicKey = md.digest(cert);
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < publicKey.length; i++) {
				String appendString = Integer.toHexString(0xFF & publicKey[i])
						.toUpperCase(Locale.US);
				if (appendString.length() == 1)
					hexString.append("0");
				hexString.append(appendString);
			}
			String result = hexString.toString();
			return result.substring(0, result.length());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * A native method that is implemented by the 'native-lib' native library,
	 * which is packaged with this application.
	 */
	public native String getSignaturesSha1(Context context);
	public native boolean checkSha1(Context context);
	public native String getToken(Context context,String userId);


}

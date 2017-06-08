package cn.ucai.live.ui.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.User;
import com.hyphenate.exceptions.HyphenateException;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.live.I;
import cn.ucai.live.R;
import cn.ucai.live.data.restapi.LiveException;
import cn.ucai.live.data.restapi.LiveManager;
import cn.ucai.live.utils.MD5;

public class RegisterActivity extends BaseActivity {

    private static final int REQUESTCODE_PICK = 1;
    private static final int REQUESTCODE_CUTTING = 2;
    @BindView(R.id.email)
    EditText username;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.register)
    Button register;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nick)
    EditText nick;
    @BindView(R.id.confirmpassword)
    EditText confirmpassword;
    @BindView(R.id.ivAvatar)
    ImageView ivAvatar;
    File file;
    private String avatarName;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void registers() {
        try {
            EMClient.getInstance().createAccount(username.getText().toString(), MD5.getMessageDigest(password.getText().toString()));
                runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dissDialog();
                                    showToast("注册成功");
                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                    finish();
                                }
                            });
        } catch (final HyphenateException e) {
            e.printStackTrace();
                runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dissDialog();
                                    showLongToast("注册失败：" + e.getMessage());
                                    removeAppRegister();
                                }
                });
        }

    }

    private void removeAppRegister() {
        try {
            LiveManager.getInstance().unRegister(username.getText().toString());
        } catch (LiveException e) {
            e.printStackTrace();
        }
    }

    private void showDialog() {
        pd = new ProgressDialog(RegisterActivity.this);
        pd.setMessage("正在注册...");
        pd.setCanceledOnTouchOutside(false);
        pd.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode!=RESULT_OK){
            return;
        }
        switch (requestCode){
            case REQUESTCODE_PICK:
                if (data == null || data.getData() == null) {
                    return;
                }
                startPhotoZoom(data.getData());
                break;
            case REQUESTCODE_CUTTING:
                if (data != null) {
                    setPicToView(data);
                }
                break;
            default:
                break;
        }
    }
    private void dissDialog(){
        if(pd!=null&&pd.isShowing()){
            pd.dismiss();
        }
    }
    @OnClick(R.id.register)
    public void register(View view){
        showDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (checkInput()) return;
                try {
                    if(LiveManager.getInstance().register(
                            username.getText().toString()
                            , nick.getText().toString()
                            , MD5.getMessageDigest(password.getText().toString())
                            , file)){
                    registers();
                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dissDialog();
                                showLongToast("注册失败：");
                            }
                        });
                    }
                } catch (final Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dissDialog();
                            showLongToast("注册失败："+e.getMessage());
                        }
                    });
                }
            }
        }).start();

    }

    private boolean checkInput() {
        if (       TextUtils.isEmpty(username.getText())
                || TextUtils.isEmpty(password.getText())
                || TextUtils.isEmpty(confirmpassword.getText())
                || TextUtils.isEmpty(nick.getText())
                ) {
            showToast("昵称、用户名、密码不能为空");

            return true;
        }
        if (!password.getText().toString().equals(confirmpassword.getText().toString())){
            showToast("密码不一致");
            return true;
        }
        return false;
    }

    @OnClick(R.id.ivAvatar)
    public void onViewClicked() {
        uploadHeadPhoto();
    }
    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            ivAvatar.setImageBitmap(photo);
            saveBitmapFile(photo);
        }

    }
    private void saveBitmapFile(Bitmap bitmap) {
        if (bitmap != null) {
            String imagePath = getAvatarPath(RegisterActivity.this, I.AVATAR_TYPE)+"/"+getAvatarName()+".jpg";
            file = new File(imagePath);//将要保存图片的路径
            try {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                bos.flush();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 返回头像保存在sd卡的位置:
     * Android/data/cn.ucai.superwechat/files/pictures/user_avatar
     * @param context
     * @param path
     * @return
     */
    public static String getAvatarPath(Context context, String path){
        File dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File folder = new File(dir,path);
        if(!folder.exists()){
            folder.mkdir();
        }
        return folder.getAbsolutePath();
    }
    private String getAvatarName() {
        avatarName = String.valueOf(System.currentTimeMillis());
        return avatarName;
    }
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, REQUESTCODE_CUTTING);
    }
    private void uploadHeadPhoto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dl_title_upload_photo);
        builder.setItems(new String[]{getString(R.string.dl_msg_take_photo), getString(R.string.dl_msg_local_upload)},
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        switch (which) {
                            case 0:
                                Toast.makeText(RegisterActivity.this, getString(R.string.toast_no_support),
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                                pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                                startActivityForResult(pickIntent, REQUESTCODE_PICK);
                                break;
                            default:
                                break;
                        }
                    }
                });
        builder.create().show();
    }
}

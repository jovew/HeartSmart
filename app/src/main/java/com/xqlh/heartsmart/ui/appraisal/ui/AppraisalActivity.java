package com.xqlh.heartsmart.ui.appraisal.ui;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.vondear.rxtools.view.dialog.RxDialogSureCancel;
import com.xqlh.heartsmart.R;
import com.xqlh.heartsmart.api.RetrofitHelper;
import com.xqlh.heartsmart.api.base.BaseObserval;
import com.xqlh.heartsmart.base.BaseActivity;
import com.xqlh.heartsmart.bean.EntityAppraisalAnswer;
import com.xqlh.heartsmart.bean.EntityAppraisalTopic;
import com.xqlh.heartsmart.bean.EntityReportAnswer;
import com.xqlh.heartsmart.ui.appraisal.adapter.AdapterAnswerApprisal;
import com.xqlh.heartsmart.ui.mine.ui.UndoneAppraisalActivity;
import com.xqlh.heartsmart.utils.Constants;
import com.xqlh.heartsmart.utils.ContextUtils;
import com.xqlh.heartsmart.utils.ProgressUtils;
import com.xqlh.heartsmart.utils.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import es.dmoral.toasty.Toasty;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class AppraisalActivity extends BaseActivity {

    @BindView(R.id.tv_topic)
    TextView tv_topic;

    @BindView(R.id.rv_appraisal_answer)
    RecyclerView rv_appraisal_answer;
    private String psyID;
    private String token;
    private String testRecordId;
    private String topicid;

    private List<EntityAppraisalTopic.ResultBean> lisTopic;
    private List<String> listAnswer = new ArrayList<>();

    private AdapterAnswerApprisal adapterAnswerApprisal;

    private int topicIndex = 0;
    SharedPreferencesHelper sp;


    @Override
    public int setContent() {
        return R.layout.activity_appraisal;
    }

    @Override
    public boolean setFullScreen() {
        return false;
    }

    @Override
    public void init() {
        Intent intent = getIntent();
        psyID = intent.getStringExtra("PsyID");//测评的id
        testRecordId = intent.getStringExtra("TestRecordId");


        Log.i(TAG, "测评记录的id       " + testRecordId);

        rv_appraisal_answer.setLayoutManager(new LinearLayoutManager(this));

        sp = new SharedPreferencesHelper(ContextUtils.getContext(), Constants.CHECKINFOR);

        topicIndex = (int) sp.getSharedPreference(Constants.TOPIC_INDEX, 0);

        token = sp.getSharedPreference(Constants.LOGIN_TOKEN, "").toString();

        initTopic(psyID);

    }

    //根据测评id获取题目的信息
    public void initTopic(String id) {
        RetrofitHelper.getApiService()
                .getAppraisalTopic(id)
                .subscribeOn(Schedulers.io())
                .compose(ProgressUtils.<EntityAppraisalTopic>applyProgressBar(this))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserval<EntityAppraisalTopic>() {
                    @Override
                    public void onSuccess(final EntityAppraisalTopic response) {
                        if (response.getCode() == 1) {

                            lisTopic = response.getResult();
                            Log.i(TAG, "集合大小" + lisTopic.size() + "........");
                            if (topicIndex < lisTopic.size()) {

                                Log.i(TAG, "题目id: " + lisTopic.get(topicIndex).getID());

                                //设置题目
                                tv_topic.setText(lisTopic.get(topicIndex).getTopicNumber() + ".  " + lisTopic.get(topicIndex).getContent());

                                topicid = lisTopic.get(topicIndex).getID();//获取题目的id

                                initAnswer(topicid);

                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //跳转到测试报告界面
                                        Intent intent = new Intent(AppraisalActivity.this, AppraisalReportActivity.class);
                                        intent.putExtra("TestRecordId", testRecordId);
                                        startActivity(intent);
                                        Log.i(TAG, "测评答案的集合" + listAnswer.size());
                                    }
                                });
                            }
                        } else {
                            Toasty.warning(ContextUtils.getContext(), "服务器异常", Toast.LENGTH_SHORT, true).show();
                        }
                        return;
                    }
                });
    }

    public void initAnswer(final String topicid) {
        RetrofitHelper.getApiService()
                .getAppraisalAnswer(topicid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserval<EntityAppraisalAnswer>() {
                    @Override
                    public void onSuccess(final EntityAppraisalAnswer response) {
                        if (response.getCode() == 1) {
                            adapterAnswerApprisal
                                    = new AdapterAnswerApprisal(
                                    R.layout.item_appraisal_answer,
                                    ContextUtils.getContext(),
                                    response.getResult());

                            rv_appraisal_answer.setAdapter(adapterAnswerApprisal);

                            adapterAnswerApprisal.openLoadAnimation();

                            adapterAnswerApprisal.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                                    topicIndex++;
                                    //提交答案
                                    reportAnswer(testRecordId, response.getResult().get(position).getOptionNumber(), topicid);

                                    listAnswer.add(response.getResult().get(position).getOptionNumber() + "");
                                }
                            });
                        } else {
                            Toasty.warning(ContextUtils.getContext(), "服务器异常", Toast.LENGTH_SHORT, true).show();
                        }
                        return;
                    }
                });
    }

//    提交测评答案【api/psychtest/savepuo】
//
//    POST 登录状态
//
//    参数	类型	说明
//    ptestUserid	string	测评记录ID
//    optionNumber	string	选项编号。如A，B，C，1,2,3等
//    topicID	string	题目ID

    public void reportAnswer(String ptestUserid, String optionNumber, String topicID) {
        RetrofitHelper.getApiService()
                .reportAnswer(token, ptestUserid, optionNumber, topicID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserval<EntityReportAnswer>() {
                    @Override
                    public void onSuccess(final EntityReportAnswer response) {
                        if (response.getCode() == 1) {
                            //提交成功刷新下一题
                            initTopic(psyID);//刷新下一题
                        } else {
                            Toasty.warning(ContextUtils.getContext(), "服务器异常", Toast.LENGTH_SHORT, true).show();
                        }
                    }
                });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showDoalog();
        }
        return true;
    }

    public void showDoalog() {

        final RxDialogSureCancel rxDialogSureCancel = new RxDialogSureCancel(mContext);//提示弹窗
        rxDialogSureCancel.getContentView().setText("是否退出测试");
        rxDialogSureCancel.getSureView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.put(Constants.TOPIC_INDEX, topicIndex);
                Intent intent = new Intent(AppraisalActivity.this, UndoneAppraisalActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });
        rxDialogSureCancel.getCancelView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rxDialogSureCancel.cancel();
            }
        });
        rxDialogSureCancel.show();
    }

}

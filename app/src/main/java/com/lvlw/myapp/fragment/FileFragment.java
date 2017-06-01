package com.lvlw.myapp.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.VideoInfo.entity.FileFolder;
import com.VideoInfo.entity.VideoInfo;
import com.lvlw.myapp.R;
import com.lvlw.myapp.adapter.MyAdapter;
import com.lvlw.myapp.entity.QFileInfo;
import com.lvlw.myapp.eventmessage.GoToFragment;
import com.lvlw.myapp.eventmessage.ScanResultEvent;
import com.lvlw.myapp.fileutils.QFileScaner;
import com.lvlw.myapp.greendao.CommonUtils;
import com.lvlw.myapp.utils.MyComparator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Wantrer on 2017/4/22 0022.
 */

public class FileFragment extends Fragment {
    @BindView(R.id.file)
    ListView file;
    @BindView(R.id.ibtn_right_menu2)
    LinearLayout ibtnRightMenu2;

    public LinearLayout getIbtnRightMenu2() {
        return ibtnRightMenu2;
    }


    public ListView getFile() {
        return file;
    }


    private List<QFileInfo> mDatas;
    private List<VideoInfo> videoInfos;
    private TextView cancel;
    private TextView seltall;
    private TextView reseltall;


    public CommonUtils getDbUtils() {
        return dbUtils;
    }

    public void setDbUtils(CommonUtils dbUtils) {
        this.dbUtils = dbUtils;
    }

    private CommonUtils dbUtils;
    private final String dbName = "videoinfo.db";

    private MyAdapter mAdapter;
    private MediaMetadataRetriever mMetadataRetriever;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.file_fragment, null);
        ButterKnife.bind(this, rootView);
        cancel = (TextView) getActivity().findViewById(R.id.edit);
        seltall = (TextView) getActivity().findViewById(R.id.seltall);
        reseltall = (TextView) getActivity().findViewById(R.id.reseltall);
        ibtnRightMenu2.setOnClickListener(new DeletedFileonClickListener());
        initData();
        return rootView;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEventMainThread(ScanResultEvent addSuccessful) {
        String s = addSuccessful.getAddSuccessful();
        mDatas = new ArrayList<>();
        dbUtils = new CommonUtils(getActivity(), dbName);
        videoInfos = dbUtils.listAll();
        dbUtils.getDaoManager().closeDaoSession();
        converstViedeoInfoToQFileInfo(videoInfos, mDatas);
        MyAdapter adapter = new MyAdapter(getActivity(), mDatas, null, getActivity());
        file.setAdapter(adapter);
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }

    private List<QFileInfo> converstViedeoInfoToQFileInfo(List<VideoInfo> videoInfos, List<QFileInfo> mDatas) {
        for (VideoInfo videoInfo : videoInfos) {
            QFileInfo fileInfo = new QFileInfo();
            fileInfo.set_fileId(videoInfo.getId());
            fileInfo.set_fileIcon(R.mipmap.mp4);
            fileInfo.set_fileName(videoInfo.getFile_Name());
            fileInfo.set_filePath(videoInfo.getFile_Path());
            fileInfo.set_fileDesc(videoInfo.getFile_Size() + "    " + videoInfo.getFile_Duration());
            fileInfo.set_fileExt(videoInfo.getFile_Extention());
            mDatas.add(fileInfo);
        }
        return mDatas;
    }

    private List<VideoInfo> converstQFileInfoToViedeoInfo(List<QFileInfo> mDatas, List<VideoInfo> videoInfos) {
        for (QFileInfo data : mDatas) {
            VideoInfo videoInfo = new VideoInfo();
            String[] tokens = data.get_fileDesc().split("\\s{4,}", 2);
            videoInfo.setId(data.get_fileId());
            videoInfo.setFile_Name(data.get_fileName());
            videoInfo.setFile_Size(tokens[0]);
            videoInfo.setFile_Duration(tokens[1]);
            videoInfo.setFile_Path(data.get_filePath());
            videoInfo.setFile_Extention(data.get_fileExt());
            videoInfos.add(videoInfo);
        }
        return videoInfos;
    }

    public void onKeyDown(int keyCode, KeyEvent event) {
        if (cancel.getText().equals("取消")) {
            cancel.performClick();
        }
        getActivity().finish();
    }

    private class DeletedFileonClickListener implements View.OnClickListener {

        @Override
        public void onClick(final View v) {
            if (cancel.getText().equals("取消")) {
                videoInfos = new ArrayList<>();
                mDatas = ((MyAdapter) file.getAdapter()).getmDatas();
                dbUtils = new CommonUtils(getActivity(), dbName);
                new AsyncTask<Context, Integer, List<QFileInfo>>() {
                    private ProgressDialog dialog;
                    private boolean haveSelecte = true;
                    private boolean flag = true;

                    @Override
                    protected void onPreExecute() {
                        dialog = ProgressDialog.show(getActivity(), "",
                                "正在删除,请稍候....");
                        super.onPreExecute();
                    }

                    @Override
                    protected List<QFileInfo> doInBackground(Context... params) {
                        List<QFileInfo> deleteInfos = new ArrayList<>();
                        for (int i = 0; i < mDatas.size(); i++) {
                            if (mDatas.get(i).isCheck()) {
                                deleteInfos.add(mDatas.get(i));
                            }
                        }
                        for (QFileInfo deleteInfo : deleteInfos) {
                            mDatas.remove(deleteInfo);
                        }
                        if (deleteInfos.size() > 0) {
                            converstQFileInfoToViedeoInfo(deleteInfos, videoInfos);
                            for (VideoInfo videoInfo : videoInfos) {
                                if (flag) {
                                    flag = dbUtils.deleteStudent(videoInfo);
                                } else {
                                    break;
                                }
                            }
                            dbUtils.getDaoManager().closeDaoSession();
                        } else {
                            haveSelecte = false;
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(List<QFileInfo> qFileInfos) {
                        dialog.dismiss();
                        if (haveSelecte) {
                            mAdapter = ((MyAdapter) file.getAdapter());
                            if (flag) {
                                Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
                                mAdapter.notifyDataSetChanged();
                                if (mAdapter.flage) {
                                    cancel.setText("编辑");
                                    mAdapter.flage = !mAdapter.flage;
                                }
                            } else {
                                Toast.makeText(getActivity(), "删除失败", Toast.LENGTH_SHORT).show();
                            }
                            ibtnRightMenu2.setVisibility(View.GONE);
                            seltall.setVisibility(View.GONE);
                            reseltall.setVisibility(View.GONE);
                            GoToFragment menuVisiable=new GoToFragment(2);
                            EventBus.getDefault().post(menuVisiable);
                            mAdapter.isAdd = false;
                        } else {
                            Toast.makeText(getActivity(), "请选择视频文件", Toast.LENGTH_SHORT).show();
                        }
                        super.onPostExecute(qFileInfos);
                    }
                }.execute();
            }
        }
    }

    public void scanSDCard(){
        videoInfos = new ArrayList<VideoInfo>();
        mDatas = ((MyAdapter) getFile().getAdapter()).getmDatas();
        mMetadataRetriever = new MediaMetadataRetriever();
        dbUtils = new CommonUtils(getActivity(), dbName);
        new AsyncTask<Context, Integer, List<QFileInfo>>() {
            private ProgressDialog dialog;
            boolean addtoSQLite = false;

            //                CommonUtils dbquery=new CommonUtils(getActivity(),"filefolder.db");
            @Override
            protected void onPreExecute() {
                dialog = ProgressDialog.show(getActivity(), "",
                        "正在扫描SD卡,请稍候....");
                super.onPreExecute();
            }

            @Override
            protected List<QFileInfo> doInBackground(Context... params) {
                List<FileFolder> queryFileFolder = dbUtils.listAllFileFolder();
                List<String> queryFilePath = new ArrayList<String>();
                for (FileFolder fileFolder : queryFileFolder) {
                    queryFilePath.add(fileFolder.getFile_Path());
                }
                QFileScaner scaner = new QFileScaner(queryFilePath);
                scaner.Start(Environment.getExternalStorageDirectory(), mMetadataRetriever);
                List<QFileInfo> fileInfos = scaner.get_resultFiles();
                if (fileInfos.size() > 0) {

                    for (QFileInfo data : fileInfos) {
                        if (!dbUtils.queryBuilder(data.get_filePath())) {
                            VideoInfo videoInfo = new VideoInfo();
                            videoInfo.setFile_Name(data.get_fileName());
                            videoInfo.setFile_Size(data.get_fileSize());
                            videoInfo.setFile_Path(data.get_filePath());
                            videoInfo.setFile_Duration(data.get_fileDesc());
                            videoInfo.setFile_Extention(data.get_fileExt());
                            videoInfos.add(videoInfo);
                        }
                    }
                    if (videoInfos.size() > 0) {
                        if (dbUtils.inserMultStudent(videoInfos)) {
                            addtoSQLite = true;
                            converstViedeoInfoToQFileInfo(videoInfos, mDatas);
                        }
                    }
                    String[] sortBy = new String[]{"_fileName"};
                    int orderBy = 1;//1:升序，-1：降序
                    MyComparator myCmp = new MyComparator(sortBy, orderBy);
                    Collections.sort(mDatas, myCmp);
                    //                        dbquery.getDaoManager().closeDaoSession();
                    dbUtils.getDaoManager().closeDaoSession();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<QFileInfo> qFileInfos) {
                dialog.dismiss();
                // TODO: create test data
                if (videoInfos.size() >= 0) {
                    if (videoInfos.size() == 0) {
                        Toast.makeText(getActivity(), "没有找到新的视频文件", Toast.LENGTH_SHORT).show();
                    } else {
                        if (addtoSQLite) {
                            Toast.makeText(getActivity(), "添加成功", Toast.LENGTH_SHORT).show();
                            ((MyAdapter) file.getAdapter()).notifyDataSetChanged();
                        } else {
                            Toast.makeText(getActivity(), "添加失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                    //                        myvideo.setVisibility(View.VISIBLE);
                    //                        scanSDCard.setVisibility(View.VISIBLE);
                }
                super.onPostExecute(qFileInfos);
            }
        }.execute();
    }
    private void initData(){
        new AsyncTask<String,String,List<QFileInfo>>(){
            private ProgressDialog dialog;
            @Override
            protected void onPreExecute() {
                dialog = ProgressDialog.show(getActivity(), "",
                        "加载中....");
                super.onPreExecute();
            }

            @Override
            protected List<QFileInfo> doInBackground(String... params) {
                dbUtils = new CommonUtils(getActivity(), dbName);
                videoInfos = dbUtils.listAll();
                dbUtils.getDaoManager().closeDaoSession();
                mDatas = new ArrayList<>();
                converstViedeoInfoToQFileInfo(videoInfos, mDatas);
                return null;
            }

            @Override
            protected void onPostExecute(List<QFileInfo> qFileInfos) {
                dialog.dismiss();
                MyAdapter adapter = new MyAdapter(getActivity(), mDatas, null, getActivity());
                file.setAdapter(adapter);
                super.onPostExecute(qFileInfos);
            }
        }.execute();
    }
}

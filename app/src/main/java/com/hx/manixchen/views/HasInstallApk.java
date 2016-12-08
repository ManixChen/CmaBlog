package com.hx.manixchen.views;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.hx.manixchen.cmablog.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HasInstallApk extends AppCompatActivity {
    private ImageButton finishThisActivity;
    public int app_icon;// 获得该资源图片在R文件中的值 (对应于android:icon属性)
    public int labelRes;// 获得该label在R文件中的值(对应于android:label属性)
    public String name;// 获得该节点的name值 (对应于android:name属性)
    public String packagename;// 获得该应用程序的包名 (对应于android：packagename属性)

    private ListView lv;
    private MyAdapter adapter;
    ArrayList<HashMap<String, Object>> items = new ArrayList<HashMap<String, Object>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        setContentView(R.layout.activity_has_install_apk);

        lv = (ListView) findViewById(R.id.lv);
        finishThisActivity=(ImageButton) findViewById(R.id.backBtn);
        finishThisActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(HasInstallApk.this,"返回上一级",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        //得到PackageManager对象
        PackageManager pm = getPackageManager();

        //得到系统安装的所有程序包的PackageInfo对象
        //List<ApplicationInfo> packs = pm.getInstalledApplications(0);
        List<PackageInfo> packs = pm.getInstalledPackages(0);

        for (PackageInfo pi : packs) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            //显示用户安装的应用程序，而不显示系统程序


            //显示所有应用程序
            map.put("app_icon", pi.applicationInfo.loadIcon(pm));//图标
            map.put("appName", pi.applicationInfo.loadLabel(pm));//应用程序名称
            map.put("packageName", pi.applicationInfo.packageName);//应用程序包名
            //循环读取并存到HashMap中，再增加到ArrayList上，一个HashMap就是一项
            items.add(map);
        }

        /**
         * 参数：Context
         * ArrayList(item的集合)
         * item的layout
         * 包含ArrayList中的HashMap的key的数组
         * key所对应的值的相应的控件id
         */
        adapter = new MyAdapter(this, items, R.layout.systemapp_list,
                new String[]{"app_icon", "appName", "packageName"},
                new int[]{R.id.app_icon, R.id.appName, R.id.packageName});

        lv=(ListView)findViewById(R.id.lv);
        lv.setAdapter(adapter);

    }


}
class MyAdapter extends SimpleAdapter
{
    private int[] appTo;
    private String[] appFrom;
    private SimpleAdapter.ViewBinder appViewBinder;
    private List<? extends Map<String, ?>>  appData;
    private int appResource;
    private LayoutInflater appInflater;

    /**
     * 构造器
     * @param context
     * @param data
     * @param resource
     * @param from
     * @param to
     */
    public MyAdapter(Context context, List<? extends Map<String, ?>> data,
                     int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        appData = data;
        appResource = resource;
        appFrom = from;
        appTo = to;
        appInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int position, View convertView, ViewGroup parent){
        return createViewFromResource(position, convertView, parent, appResource);
    }

    private View createViewFromResource(int position, View convertView, ViewGroup parent, int resource){
        View v;

        if(convertView == null){
            v = appInflater.inflate(resource, parent,false);
            final int[] to = appTo;
            final int count = to.length;
            final View[] holder = new View[count];

            for(int i = 0; i < count; i++){
                holder[i] = v.findViewById(to[i]);
            }

            v.setTag(holder);
        }else {
            v = convertView;
        }

        bindView(position, v);
        return v;
    }

    private void bindView(int position, View view){
        final Map dataSet = appData.get(position);

        if(dataSet == null){
            return;
        }

        final ViewBinder binder = appViewBinder;
        final View[] holder = (View[])view.getTag();
        final String[] from = appFrom;
        final int[] to = appTo;
        final int count = to.length;

        for(int i = 0; i < count; i++){
            final View v = holder[i];

            if(v != null){
                final Object data = dataSet.get(from[i]);
                String text = data == null ? "":data.toString();

                if(text == null){
                    text = "";
                }

                boolean bound = false;

                if(binder != null){
                    bound = binder.setViewValue(v, data, text);
                }

                if(!bound){
                    /**
                     * 自定义适配器，关在在这里，根据传递过来的控件以及值的数据类型，
                     * 执行相应的方法，可以根据自己需要自行添加if语句。另外，CheckBox等
                     * 集成自TextView的控件也会被识别成TextView，这就需要判断值的类型
                     */
                    if(v instanceof TextView){
                        //如果是TextView控件，则调用SimpleAdapter自带的方法，设置文本
                        setViewText((TextView)v, text);
                    }else if(v instanceof ImageView){
                        //如果是ImageView控件，调用自己写的方法，设置图片
                        setViewImage((ImageView)v, (Drawable)data);
                    }else {
                        throw new IllegalStateException(v.getClass().getName() + " is not a " +
                                "view that can be bounds by this SimpleAdapter");
                    }
                }
            }
        }
    }

    public void setViewImage(ImageView v, Drawable value)
    {
        v.setImageDrawable(value);
    }

}
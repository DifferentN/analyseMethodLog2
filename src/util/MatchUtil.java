package util;

import event.MyEvent;
import event.ViewInfo;

import java.util.ArrayList;
import java.util.List;

public class MatchUtil {
    public static float SAME_STATE = 1.0f;
    public static float NOT_SAME_STATE = 0f;
    //assign weight of each part
    private static float p1 = 1/4f,p2 = p1 ,p3 = p1, p4 = p1;
    //the threshold of viewInfo
    private static float viewInfoThreshold = 0.6f;
    //the threshold of targetView(touch or setText view)
    private static float targetThreshold = viewInfoThreshold;
    //the threshold of structure
    private static float structureThreshold = 0.6f;
    /**
     * reckon the similarity between viewnInfo1 and viewInfo2
     * @param viewInfo1
     * @param viewInfo2
     * @return
     */
    public static float obtainViewSimilarity(ViewInfo viewInfo1,ViewInfo viewInfo2){
        if(!viewInfo1.getViewPath().equals(viewInfo2.getViewPath())){
            return 0;
        }
        float res = p1 * Math.min( viewInfo1.getX(), viewInfo2.getX() ) / Math.max( viewInfo1.getX(), viewInfo2.getX() ) +
                p2 * Math.min( viewInfo1.getY(), viewInfo2.getY() ) / Math.max( viewInfo1.getY(), viewInfo2.getY() ) +
                p3 * Math.min( viewInfo1.getWidth(), viewInfo2.getWidth()) / Math.max( viewInfo1.getWidth(), viewInfo2.getWidth()) +
                p4 * Math.min( viewInfo1.getHeight(), viewInfo2.getHeight()) / Math.max( viewInfo1.getHeight(), viewInfo2.getHeight());
        return res;
    }

    /**
     * reckon the similarity between MyEvent
     * 计算2个任务之间的相似度
     * @param myEvent1
     * @param myEvent2
     * @return
     */
    public static float obtainMyEventSimilarity(MyEvent myEvent1,MyEvent myEvent2){
        float activitySimilarity = myEvent1.getActivityId().equals( myEvent2.getActivityId() )?1:0;
        float userActionTypeSimilarity = myEvent1.getMethodName().equals( myEvent2.getMethodName() )?1:0;
        float targetViewSimilarity = 0;
        if(myEvent1.getPath().equals( myEvent2.getPath() )){
            targetViewSimilarity = p1 * Math.min( myEvent1.getViewX(), myEvent2.getViewX() ) / Math.max( myEvent1.getViewX(), myEvent2.getViewX() ) +
                    p2 * Math.min( myEvent1.getViewY(), myEvent2.getViewY() ) / Math.max( myEvent1.getViewY(), myEvent2.getViewY() ) +
                    p3 * Math.min( myEvent1.getWidth(), myEvent2.getWidth()) / Math.max( myEvent1.getWidth(), myEvent2.getWidth()) +
                    p4 * Math.min( myEvent1.getHeight(), myEvent2.getHeight()) / Math.max( myEvent1.getHeight(), myEvent2.getHeight());
        }

        float structureSimilarity = obtainStructureSimilarity(myEvent1.getStructure(),myEvent2.getStructure());
        if(activitySimilarity==1 && userActionTypeSimilarity==1 &&
            targetViewSimilarity>targetThreshold && structureSimilarity>structureThreshold){
            return SAME_STATE;
        }
        return NOT_SAME_STATE;
    }

    public static float obtainStructureSimilarity(List<ViewInfo> structure1,List<ViewInfo> structure2){
        float layer1 = getStructureLayer(structure1);
        float layer2 = getStructureLayer(structure2);
        float maxLayer = Math.max(layer1, layer2 );

        float viewNum1 = getViewNum(structure1);
        float viewNum2 = getViewNum(structure2);
        float maxNum = Math.max(viewNum1,viewNum2);
        int sameNum = reckonViewTreeSimilarity( structure1, structure2 );
        float part1 = Math.min(layer1,layer2)/maxLayer*p1;
        float part2 = Math.min(viewNum1,viewNum2)/maxNum*p1;
        float part3 = sameNum/maxNum*p3;
        float res = part1 + part2 + part3 + p4;
        float temp = sameNum/maxNum;
        System.out.println(temp+" "+sameNum+" "+viewNum1+" "+viewNum2+" "+res);
        return res;

    }
    public static int reckonViewTreeSimilarity(List<ViewInfo> viewInfos1,List<ViewInfo> viewInfos2){
        //the threshold of viewInfo similarity
        float w = viewInfoThreshold;
        int res = 0;
        if(viewInfos1==null){
            return res;
        }
        //reckon the number of viewInfos which similarity bigger than w
        //获取 相似度大于阈值w的 元素（视图）对的数量
        for(ViewInfo viewInfo:viewInfos1){

            ViewInfo matcherViewInfo = searchMatchedViewInfo(viewInfo,viewInfos2);
            //不是同一个View，相似度为0
            if(matcherViewInfo==null){
                continue;
            }
            float similarity = obtainViewSimilarity(viewInfo,matcherViewInfo);
            //view对的相似度大于阈值，res加一
            if(similarity>=w){
                res++;
            }
            //计算子视图中 相似度大于阈值的元素对的个数
            res += reckonViewTreeSimilarity(viewInfo.getChilds(), matcherViewInfo.getChilds());
        }
        return res;
    }

    /**
     * 在childs中搜索与childViewInfo相匹配的ViewInfo
     * 判断依据：名称和序号
     * @param childViewInfo
     * @param childs
     * @return
     */
    private static ViewInfo searchMatchedViewInfo(ViewInfo childViewInfo,List<ViewInfo> childs){
        if(childs==null){
            return null;
        }
        for(ViewInfo viewInfo:childs){
            if( viewInfo.getViewName().equals(childViewInfo.getViewName()) &&
                viewInfo.getViewIndex()==childViewInfo.getViewIndex()){
                return viewInfo;
            }
        }
        return null;
    }
    private static int getViewNum(List<ViewInfo> structure){
        List<ViewInfo> queue = new ArrayList<>();
        queue.addAll(structure);
        int num = 0;
        while(!queue.isEmpty()){
            ViewInfo viewInfo = queue.remove(0);
            num++;
            List<ViewInfo> childViewInfos = viewInfo.getChilds();
            if(childViewInfos != null){
                queue.addAll(childViewInfos);
            }

        }
        return num;
    }

    /**
     * obtain the height of the window structure
     * @param structure
     * @return
     */
    private static int getStructureLayer(List<ViewInfo> structure){
        int res = 0;
        for(ViewInfo viewInfo:structure){
            res = Math.max(res,getViewTreeLayer(viewInfo));
        }
        return res;
    }

    /**
     * obtain the height of the view
     * @param viewInfo represent a view
     * @return
     */
    private static int getViewTreeLayer(ViewInfo viewInfo){
        if(viewInfo.getChilds()==null){
            return 1;
        }
        int res = 1;
        List<ViewInfo> childs = viewInfo.getChilds();
        for(ViewInfo child:childs){
            res = Math.max( res, getViewTreeLayer(child) + 1 );
        }
        return res;
    }
}

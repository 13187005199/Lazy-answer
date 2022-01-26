package com.lazy.vm.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.alibaba.fastjson.JSONObject;
import com.lazy.common.utils.DateUtils;
import com.lazy.common.utils.DelTagsUtil;
import com.lazy.common.utils.SecurityUtils;
import com.lazy.common.utils.uuid.SnowflakeIdWorker;
import com.lazy.vm.domain.Achievement;
import com.lazy.vm.domain.AnswerOptions;
import com.lazy.vm.domain.TestPaper;
import com.lazy.vm.domain.vo.AnswerOptionsVo;
import com.lazy.vm.domain.vo.AnswerTypeVo;
import com.lazy.vm.domain.vo.AnswerVo;
import com.lazy.vm.mapper.AchievementMapper;
import com.lazy.vm.mapper.AnswerOptionsMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import com.lazy.vm.mapper.AnswerMapper;
import com.lazy.vm.domain.Answer;
import com.lazy.vm.service.IAnswerService;

/**
 * 作业题目Service业务层处理
 *
 * @author fgq
 * @date 2021-10-26
 */
@Service
public class AnswerServiceImpl implements IAnswerService {
    @Autowired
    private AnswerMapper answerMapper;

    @Autowired
    private AchievementMapper achievementMapper;

    @Autowired
    private AnswerOptionsMapper answerOptionsMapper;

    /**
     * 查询作业题目
     *
     * @param id 作业题目ID
     * @return 作业题目
     */
    @Override
    public AnswerVo selectAnswerById(String id) {
        Answer answer = answerMapper.selectAnswerById(id);
        AnswerVo answerVo = new AnswerVo();
        BeanUtils.copyProperties(answer, answerVo);
        String answerId = answer.getId();
        List<AnswerOptions> answerOptionList = answerOptionsMapper.selectAnswerOptionsByAnswerId(answerId);
        List<AnswerOptionsVo> list = new ArrayList<>();
        for (AnswerOptions answerOptions : answerOptionList) {
            AnswerOptionsVo answerOptionsVo = new AnswerOptionsVo();
            BeanUtils.copyProperties(answerOptions, answerOptionsVo);
            list.add(answerOptionsVo);
        }
        answerVo.setAnswerList(list);
        return answerVo;
    }

    /**
     * 查询作业题目列表
     *
     * @param answer 作业题目
     * @return 作业题目
     */
    @Override
    public List<Answer> selectAnswerList(Answer answer) {
        return answerMapper.selectAnswerList(answer);
    }

    /**
     * 新增作业题目
     *
     * @param answerVo 作业题目
     * @return 结果
     */
    @Override
    public int insertAnswer(AnswerVo answerVo) {
        Answer answer = new Answer();
        String id = SnowflakeIdWorker.getId();
        BeanUtils.copyProperties(answerVo, answer);
        answer.setId(id);
        answer.setContentText(DelTagsUtil.getTextFromHtml(answerVo.getContent()));
        for (AnswerOptionsVo answerOptionsVo : answerVo.getAnswerList()) {
            AnswerOptions answerOptions = new AnswerOptions();
            BeanUtils.copyProperties(answerOptionsVo, answerOptions);
            answerOptions.setQuId(id);
            answerOptionsMapper.insertAnswerOptions(answerOptions);
        }
        return answerMapper.insertAnswer(answer);
    }

    /**
     * 修改作业题目
     *
     * @param answerVo 作业题目
     * @return 结果
     */
    @Override
    public int updateAnswer(AnswerVo answerVo) {
        Answer answer = new Answer();
        BeanUtils.copyProperties(answerVo, answer);
        answer.setUpdateTime(DateUtils.getNowDate());
        answer.setContentText(DelTagsUtil.getTextFromHtml(answerVo.getContent()));
        //删除所有题目选项然后重新增加
        answerOptionsMapper.deleteAnswerOptionsByQuId(answer.getId());
        for (AnswerOptionsVo answerOptionsVo : answerVo.getAnswerList()) {
            AnswerOptions answerOptions = new AnswerOptions();
            BeanUtils.copyProperties(answerOptionsVo, answerOptions);
            answerOptions.setQuId(answer.getId());
            answerOptionsMapper.insertAnswerOptions(answerOptions);
        }
        return answerMapper.updateAnswer(answer);
    }

    /**
     * 批量删除作业题目
     *
     * @param ids 需要删除的作业题目ID
     * @return 结果
     */
    @Override
    public int deleteAnswerByIds(Long[] ids) {
        //删除作业前把选项删除
        for (Long id : ids) {
            answerOptionsMapper.deleteAnswerOptionsByQuId(id.toString());
        }
        return answerMapper.deleteAnswerByIds(ids);
    }

    /**
     * 删除作业题目信息
     *
     * @param id 作业题目ID
     * @return 结果
     */
    @Override
    public int deleteAnswerById(Long id) {
        //删除作业前把选项删除
        answerOptionsMapper.deleteAnswerOptionsByQuId(id.toString());
        return answerMapper.deleteAnswerById(id);
    }


    /**
     * 考试分数
     *
     * @param answerVo
     * @return
     */
    @Override
    public int testScore(List<AnswerVo> answerVo) {
        int fs = 0;
        if (answerVo.size() > 0) {
            int count = 0;
            for (AnswerVo an : answerVo) {
                List<AnswerTypeVo> answerTypeVo = (List<AnswerTypeVo>) JSONObject.parse(an.getOptions());
                if (answerTypeVo.get(an.getUserAnswer()).getIsRight() == "true") {
                    count++;
                }
            }
            fs = count * 100 / answerVo.size();
            Achievement achievement = new Achievement();
            achievement.setId(UUID.randomUUID().toString().replaceAll("-", "").toString());
            achievement.setChapterId(answerVo.get(0).getTestPaperId());
            achievement.setUesrId(String.valueOf(SecurityUtils.getLoginUser().getUser().getUserId()));
            achievement.setAchievement(String.valueOf(fs));
            achievementMapper.insertAchievement(achievement);
            return fs;
        }
        return fs;
    }


    @Override
    public List<Answer> getTestPaperAnswer(String testPaperId) {
        if (!"".equals(testPaperId)) {
            List<Answer> testPaperAnswer = answerMapper.getTestPaperAnswer(testPaperId);
            return testPaperAnswer;
        }
        return null;
    }

    @Override
    public List<AnswerVo> pagingWithAnswer(AnswerVo answerVo) {
        List<AnswerVo> answerVoList= answerMapper.selectExcludes(answerVo);
        return answerVoList;
    }
}

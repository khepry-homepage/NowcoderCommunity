package com.nowcoder.community.config;

import com.nowcoder.community.quartz.PostJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

@Configuration
public class QuartzConfig {
    @Bean
    public JobDetailFactoryBean postJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(PostJob.class);
        factoryBean.setName("postJob");
        factoryBean.setGroup("postJobGroup");
        factoryBean.setDurability(true);
        factoryBean.setRequestsRecovery(true);  //  当job运行失败时由下一scheduler接手运行，false表示等下一个定时周期再运行
        return factoryBean;
    }
    @Bean
    public SimpleTriggerFactoryBean postTrigger(JobDetail postJobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(postJobDetail);
        factoryBean.setName("postTrigger");
        factoryBean.setGroup("postTriggerGroup");
        factoryBean.setRepeatInterval(3000);
        factoryBean.setJobDataMap(new JobDataMap());    //  设定quartz底层存放job状态的对象
        return factoryBean;
    }
}

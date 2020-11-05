package com.sunshineftg.kbase.plugin;

import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * mybatisPlus code generator
 * 自动生成代码插件
 * t_account_rec,t_collection,t_collection_detail,t_deliver,t_deliver_detail,t_factoring_pool_record,t_invoice,t_invoice_detail,t_pool_white_list,t_receivables,t_receivables_detail
 * ,t_sales_order,t_sales_order_detail,t_task_record
 */
public class CodeGenerator {

    private static String url = "jdbc:mysql://10.66.11.130:3306/eam?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=false";
    private static String userName = "eam";
    private static String password = "56cbO4ZZZRnXzt2c";
    private static String driverClassName = "com.mysql.jdbc.Driver";

        /**
         * <p>
         * 读取控制台内容
         * </p>
         */
        public static String scanner(String tip) {
            Scanner scanner = new Scanner(System.in);
            StringBuilder help = new StringBuilder();
            help.append("请输入" + tip + "：");
            System.out.println(help.toString());
            if (scanner.hasNext()) {
                String ipt = scanner.next();
                if (StringUtils.isNotEmpty(ipt)) {
                    return ipt;
                }
            }
            throw new MybatisPlusException("请输入正确的" + tip + "！");
        }

        public static void main(String[] args) {
            // 代码生成器
            AutoGenerator mpg = new AutoGenerator();
            // 全局配置
            GlobalConfig gc = new GlobalConfig();
            final String projectPath = System.getProperty("user.dir");
            gc.setOutputDir(projectPath + "/src/main/java");
            gc.setAuthor("system");
            gc.setSwagger2(true);
            gc.setOpen(false);
            mpg.setGlobalConfig(gc);
            // 数据源配置
            DataSourceConfig dsc = new DataSourceConfig();
            dsc.setUrl(url);
            dsc.setDriverName(driverClassName);
            dsc.setUsername(userName);
            dsc.setPassword(password);
/*
            dsc.setTypeConvert((globalConfig, fieldType) -> {
                String t = fieldType.toLowerCase();
                // 实体中时间采用String类型
                if(t.contains("datetime")){
                    return DbColumnType.STRING;
                }
                //其它字段采用默认转换（非mysql数据库可以使用其它默认的数据库转换器）
                return new MySqlTypeConvert().processTypeConvert(globalConfig,fieldType);
            });*/
            mpg.setDataSource(dsc);

            // 包配置
            final PackageConfig pc = new PackageConfig();
            pc.setModuleName(scanner("模块名"));
            pc.setParent("com.sunjinke.puhui.eam");
            mpg.setPackageInfo(pc);

            // 自定义配置
            InjectionConfig cfg = new InjectionConfig() {
                @Override
                public void initMap() {
                    // to do nothing
                }
            };

            // 如果模板引擎是 freemarker
            String templatePath = "/templates/mapper.xml.ftl";
            // 如果模板引擎是 velocity
            // String templatePath = "/templates/mapper.xml.vm";
            // 自定义输出配置
            List<FileOutConfig> focList = new ArrayList<>();
            // 自定义配置会被优先输出
            focList.add(new FileOutConfig(templatePath) {
                @Override
                public String outputFile(TableInfo tableInfo) {
                    // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                    return projectPath + "/src/main/resources/mapper/" + pc.getModuleName()
                            + "/" + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
                }
            });
            cfg.setFileOutConfigList(focList);
            mpg.setCfg(cfg);
            // 配置模板
            TemplateConfig templateConfig = new TemplateConfig();
            // 配置自定义输出模板
            //指定自定义模板路径，注意不要带上.ftl/.vm, 会根据使用的模板引擎自动识别
            // templateConfig.setEntity("templates/entity2.java");
            // templateConfig.setService();
            // templateConfig.setController();
            templateConfig.setXml(null);
            mpg.setTemplate(templateConfig);
            // 策略配置
            StrategyConfig strategy = new StrategyConfig();
            // 是否生成实体时，生成字段注解
            strategy.setEntityTableFieldAnnotationEnable(true);
            // 表前缀
            strategy.setTablePrefix("t_");
            // 数据库表映射到实体的命名策略
            strategy.setNaming(NamingStrategy.underline_to_camel);
            // 数据库表字段映射到实体的命名策略, 未指定按照 naming 执行
            strategy.setColumnNaming(NamingStrategy.underline_to_camel);
            // lombok
            strategy.setEntityLombokModel(true);
            // @RestController
            strategy.setRestControllerStyle(true);
            strategy.setInclude(scanner("表名，多个英文逗号分割").split(","));
            strategy.setControllerMappingHyphenStyle(true);
            // strategy.setSuperEntityClass("你自己的父类实体,没有就不用设置!");
            // 公共父类
            // strategy.setSuperControllerClass("你自己的父类控制器,没有就不用设置!");
            // 写于父类中的公共字段
            // strategy.setSuperEntityColumns("id");
            mpg.setStrategy(strategy);
            mpg.setTemplateEngine(new FreemarkerTemplateEngine());
            mpg.execute();
        }
}


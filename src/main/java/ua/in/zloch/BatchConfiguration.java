package ua.in.zloch;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.HibernateItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.orm.hibernate3.HibernateOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import ua.in.zloch.entity.Region;

import java.util.LinkedList;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    // tag::readerwriterprocessor[]
    @Bean
    public FlatFileItemReader<Region> reader() {
        FlatFileItemReader<Region> reader = new FlatFileItemReader<Region>();
        reader.setResource(new ClassPathResource("data/regions/list.txt"));
        reader.setLineMapper(new DefaultLineMapper<Region>() {{
            setLineTokenizer(new DelimitedLineTokenizer() {{
                setNames(new String[] { "name", "koatuu" });
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<Region>() {{
                setTargetType(Region.class);
            }});
        }});
        return reader;
    }

    @Bean
    public RegionItemProcessor processor() {
        return new RegionItemProcessor();
    }

    @Bean
    public HibernateItemWriter<Region> writer() {
        HibernateItemWriter<Region> writer = new HibernateItemWriter<Region>();
        writer.setSessionFactory(new SessionFactory() {
        });
        return writer;
    }
    // end::readerwriterprocessor[]

    // tag::listener[]

    @Bean
    public JobExecutionListener listener() {
        return new JobCompletionNotificationListener(new JdbcTemplate(dataSource));
    }

    // end::listener[]

    // tag::jobstep[]
    @Bean
    public Job importRegionsJob() {
        return jobBuilderFactory.get("importRegionJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener())
                .flow(step1())
                .end()
                .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .<Region, Region> chunk(10)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }
    // end::jobstep[]
}
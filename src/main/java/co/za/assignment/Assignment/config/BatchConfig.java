package co.za.assignment.Assignment.config;

import co.za.assignment.Assignment.writer.ConsoleItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.xstream.XStreamMarshaller;

import java.util.Map;

/**
 * @author : Mpholo Leboea
 * @Created : 2022/04/05
 **/

@EnableBatchProcessing
@Configuration
public class BatchConfig {

    private final JobBuilderFactory jobs;
    private final StepBuilderFactory steps;

    public BatchConfig(JobBuilderFactory jobs, StepBuilderFactory steps) {
        this.jobs = jobs;
        this.steps = steps;
    }

   @Bean
   public FlatFileItemReader<String> flatFileItemReader() {
       return new FlatFileItemReaderBuilder<String>()
               .name("fileReader")
               .resource(new ClassPathResource("billing.txt"))
               .lineMapper(new LineMapper<String>() {
                   @Override
                   public String mapLine(String s, int i) throws Exception {
                       return s;
                   }
               })
               .build();
   }

   @Bean
   public Tasklet tasklet() {
        return (new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {

                System.out.println("\n\n\n***************************Count of words in the file********************************************");
                final Map<String, Integer> words = ConsoleItemWriter.words;
                words.forEach((k,v)->{
                    System.out.println(k+"="+v);
                });
                System.out.println("***********************************************************************\n\n\n");
                return RepeatStatus.FINISHED;
            }
        });

   }

//   @Bean
//   public StaxEventItemWriter xmlWriter() {
//       XStreamMarshaller marshaller = new XStreamMarshaller();
//        StaxEventItemWriter staxEventItemWriter = new StaxEventItemWriter();
//
//        staxEventItemWriter.setResource(new ClassPathResource("ExecutionExport_Mortgage Life--R000.001.002.xml"));
//        staxEventItemWriter.setMarshaller(marshaller);
//        staxEventItemWriter.setRootTagName("prod:PublishMarketableProductsRequest");
//
//
//   }
   @Bean
    public Step step1() {
        return steps.get("step1")
                .chunk(10)
                .reader(flatFileItemReader())
                .writer(new ConsoleItemWriter())
                .build();
   }

    @Bean
    public Step step2() {
        return steps.get("step2")
                .tasklet(tasklet())
                .build();
    }

   @Bean
    public Job Job1() {
        return jobs.get("job1")
                .incrementer(new RunIdIncrementer())
                .start(step1())
                .next(step2())
                .build();
   }
}

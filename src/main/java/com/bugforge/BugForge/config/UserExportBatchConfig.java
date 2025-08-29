package com.bugforge.BugForge.config;

import com.bugforge.BugForge.data.Users;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class UserExportBatchConfig {

    private final DataSource dataSource;

    public UserExportBatchConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // ✅ Reader: reads Users from DB
    @Bean
    public JdbcCursorItemReader<Users> reader() {
        JdbcCursorItemReader<Users> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql("SELECT id, username, mail, password FROM users"); // adjust if table/column names differ
        reader.setRowMapper((rs, rowNum) -> {
            Users user = new Users();
            user.setId(rs.getInt("id"));
            user.setUsername(rs.getString("username"));
            user.setMail(rs.getString("mail"));
            user.setPassword(rs.getString("password"));
            return user;
        });
        return reader;
    }

    // ✅ Writer: writes Users to CSV
    @Bean
    public FlatFileItemWriter<Users> writer() {
        FlatFileItemWriter<Users> writer = new FlatFileItemWriter<>();
        writer.setResource(new FileSystemResource("output/users.csv"));

        // ✅ Add header row
        writer.setHeaderCallback(writer1 -> writer1.write("id,username,mail"));

        DelimitedLineAggregator<Users> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(",");

        BeanWrapperFieldExtractor<Users> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[]{"id", "username", "mail"});
        // ⚠️ excluding password for security reasons
        lineAggregator.setFieldExtractor(fieldExtractor);

        writer.setLineAggregator(lineAggregator);
        return writer;
    }


    // ✅ Step
    @Bean
    public Step exportStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("exportStep", jobRepository)
                .<Users, Users>chunk(10, transactionManager)
                .reader(reader())
                .writer(writer())
                .build();
    }

    // ✅ Job
    @Bean
    public Job exportJob(JobRepository jobRepository, Step exportStep) {
        return new JobBuilder("exportJob", jobRepository)
                .start(exportStep)
                .build();
    }

    @Bean
    public CommandLineRunner runExportJob(JobLauncher jobLauncher, Job exportJob) {
        return args -> {
            System.out.println("EXECUTING BATCH JOB ON STARTUP");
            // Spring Batch requires unique parameters for each job run.
            // Using the current time is a common way to ensure uniqueness.
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("startAt", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(exportJob, jobParameters);
        };
    }
}

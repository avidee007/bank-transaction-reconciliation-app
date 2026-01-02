package com.avi.learning.reconciliation.reader;

import com.avi.learning.reconciliation.domain.GatewayTransaction;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Configuration
public class CsvFileReader {

  /**
   * FlatFileReader an implementation of {@link ItemReader } for reading transaction file in CSV
   * file format stored in resource folder for this demo. Files can be accessed from S3 bucket in
   * cloud native project, Server path for server deployment, multipart file as API input.
   *
   * @return Bean of {@link FlatFileItemReader}
   */
  @Bean
  public FlatFileItemReader<GatewayTransaction> gatewayTransactionReader() {
    return new FlatFileItemReaderBuilder<GatewayTransaction>()
        .name("gatewayTransactionReader")
        .resource(new FileSystemResource("src/main/resources/transactions.csv"))
        .delimited()
        .names("txn_id", "account_no", "amount", "txn_date")
        .fieldSetMapper(
            fs ->
                new GatewayTransaction(
                    fs.readString("txn_id"),
                    fs.readString("account_no"),
                    fs.readBigDecimal("amount"),
                    fs.readDate("txn_date", "yyyy-MM-dd")))
        .linesToSkip(1)
        .build();
  }
}

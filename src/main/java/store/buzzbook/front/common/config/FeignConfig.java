package store.buzzbook.front.common.config;

import org.springframework.cloud.openfeign.support.JsonFormWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import feign.codec.Encoder;
import feign.form.ContentType;
import feign.form.MultipartFormContentProcessor;
import feign.form.spring.SpringFormEncoder;
import feign.form.spring.SpringManyMultipartFilesWriter;
import feign.form.spring.SpringSingleMultipartFileWriter;
import lombok.RequiredArgsConstructor;
import store.buzzbook.front.common.interceptor.FeignInterceptor;
import store.buzzbook.front.common.util.CookieUtils;

@Configuration
@RequiredArgsConstructor
@Import(JsonFormWriter.class)
public class FeignConfig {

	public FeignInterceptor feignInterceptor() {
		return new FeignInterceptor(new CookieUtils());
	}

	@Bean
	Encoder feignEncoder(JsonFormWriter jsonFormWriter) {
		SpringFormEncoder encoder = new SpringFormEncoder();
		MultipartFormContentProcessor processor = (MultipartFormContentProcessor) encoder.getContentProcessor(ContentType.MULTIPART);
		processor.addFirstWriter(jsonFormWriter);
		processor.addFirstWriter(new SpringSingleMultipartFileWriter());
		processor.addFirstWriter(new SpringManyMultipartFilesWriter());
		return encoder;
	}
}

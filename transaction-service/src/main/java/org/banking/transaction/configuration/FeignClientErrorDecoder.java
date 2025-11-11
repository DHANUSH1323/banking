package org.banking.transaction.configuration;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.banking.transaction.exception.GlobalException;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FeignClientErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String s, Response response) {

        GlobalException globalException = extractGlobalException(response);

        switch (response.status()) {
            case 404, 400 -> {
                return globalException;
            }
            default -> {
                return new Exception("common feign exception");
            }
        }
    }

    private GlobalException extractGlobalException(Response response) {

        GlobalException globalException = null;
        Reader reader = null;

        try {
            reader = response.body().asReader(StandardCharsets.UTF_8);
            String result = IOUtils.toString(reader);
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            globalException = mapper.readValue(result, GlobalException.class);
        } catch (IOException e) {
            log.error("IO exception on reading exception message", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    log.error("IO exception on reading exception message", e);
                }
            }
        }
        return globalException;
    }
}

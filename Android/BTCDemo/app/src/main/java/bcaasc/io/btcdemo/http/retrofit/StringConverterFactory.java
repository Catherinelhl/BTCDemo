package bcaasc.io.btcdemo.http.retrofit;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Converter.Factory;
import retrofit2.Retrofit;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;


/**
 * Http：用於網絡請求返回數據可轉化成String的格式支持
 */
public class StringConverterFactory extends Factory {
    private static final MediaType MEDIA_TYPE = MediaType.parse("text/plain");

    public StringConverterFactory() {
    }

    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return String.class.equals(type) ? new Converter<ResponseBody, String>() {
            public String convert(ResponseBody value) throws IOException {
                return value.string();
            }
        } : null;
    }

    public Converter<?, String> stringConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return super.stringConverter(type, annotations, retrofit);
    }

    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        return String.class.equals(type) ? new Converter<String, RequestBody>() {
            public RequestBody convert(String value) throws IOException {
                return RequestBody.create(StringConverterFactory.MEDIA_TYPE, value);
            }
        } : null;
    }
}
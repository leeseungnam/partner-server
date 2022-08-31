package kr.wrightbrothers.framework.util;

import org.apache.commons.io.IOUtils;
import org.springframework.lang.NonNull;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ReadableRequestBodyWrapper extends HttpServletRequestWrapper {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private final byte[] bytes;
    private final String requestBody;

    public ReadableRequestBodyWrapper(HttpServletRequest request) throws IOException {
        super(request);

        InputStream inputStream = super.getInputStream();
        bytes = IOUtils.toByteArray(inputStream);
        requestBody = new String(bytes, DEFAULT_CHARSET);
    }

    @Override
    public ServletInputStream getInputStream() {
        final ByteArrayInputStream bis = new ByteArrayInputStream(bytes);

        return new ServletImpl(bis);
    }

    public String getRequestBody() {
        return this.requestBody;
    }

    static class ServletImpl extends ServletInputStream {
        private final InputStream is;

        ServletImpl(InputStream bis) {
            is = bis;
        }

        @Override
        public int read() throws IOException {
            return is.read();
        }

        @Override
        public int read(@NonNull byte[] b) throws IOException {
            return is.read(b);
        }

        @Override
        public boolean isFinished() {
            return false;
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setReadListener(ReadListener listener) {
            // do nothing
        }
    }
}
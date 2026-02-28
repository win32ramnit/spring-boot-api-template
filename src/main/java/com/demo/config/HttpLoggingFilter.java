package com.demo.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.output.TeeOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class HttpLoggingFilter implements Filter {

  private static final Logger LOG = LoggerFactory.getLogger(HttpLoggingFilter.class);

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    try {
      HttpServletRequest httpServletRequest = (HttpServletRequest) request;
      HttpServletResponse httpServletResponse = (HttpServletResponse) response;

      Map<String, String> requestMap = this.getTypesafeRequestMap(httpServletRequest);
      BufferedRequestWrapper bufferedRequestWrapper = new BufferedRequestWrapper(httpServletRequest);
      BufferedResponseWrapper bufferedResponseWrapper = new BufferedResponseWrapper(httpServletResponse);

      // @formatter:off
      final StringBuilder logMessage = new StringBuilder("REST Request - ")
          .append("[HTTP METHOD: ").append(httpServletRequest.getMethod())
          .append("] [PATH INFO: ").append(httpServletRequest.getServletPath())
          .append("] [REQUEST PARAMETERS: ").append(requestMap)
          .append("] [REQUEST BODY: ").append(bufferedRequestWrapper.getRequestBody())
          .append("] [REMOTE ADDRESS: ").append(httpServletRequest.getRemoteAddr())
          .append("]");
      // @formatter:on

      chain.doFilter(bufferedRequestWrapper, bufferedResponseWrapper);
      if (!bufferedResponseWrapper.getHeader("Content-Type").equals("application/vnd.ms-excel"))
        logMessage.append(" [RESPONSE: ").append(bufferedResponseWrapper.getContent()).append("]");

      LOG.debug(logMessage.toString());
    } catch (Throwable t) {
      LOG.error(t.getMessage());
    }
  }

  private Map<String, String> getTypesafeRequestMap(HttpServletRequest httpServletRequest) {
    Map<String, String> typeSafeRequestMap = new HashMap<>();
    Enumeration<?> requestParamNames = httpServletRequest.getParameterNames();
    while (requestParamNames.hasMoreElements()) {
      String requestParamName = (String) requestParamNames.nextElement();
      String requestParamValue;
      if ("password".equalsIgnoreCase(requestParamName)) {
        requestParamValue = "********";
      } else {
        requestParamValue = httpServletRequest.getParameter(requestParamName);
      }
      typeSafeRequestMap.put(requestParamName, requestParamValue);
    }

    return typeSafeRequestMap;
  }

  @Override
  public void destroy() {
  }

  private static final class BufferedRequestWrapper extends HttpServletRequestWrapper {

    private ByteArrayInputStream bais = null;
    private ByteArrayOutputStream baos = null;
    private BufferedServletInputStream bsis = null;
    private byte[] buffer = null;

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request The request to wrap
     * @throws IllegalArgumentException if the request is null
     */
    public BufferedRequestWrapper(HttpServletRequest request) throws IOException {
      super(request);
      // read InputStream and store its content in a buffer
      InputStream is = request.getInputStream();
      this.baos = new ByteArrayOutputStream();
      byte[] buf = new byte[1024];
      int read;
      while ((read = is.read(buf)) > 0) {
        this.baos.write(buf, 0, read);
      }
      this.buffer = this.baos.toByteArray();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
      this.bais = new ByteArrayInputStream(this.buffer);
      this.bsis = new BufferedServletInputStream(this.bais);
      return this.bsis;
    }

    String getRequestBody() throws IOException {
      BufferedReader reader = new BufferedReader(new InputStreamReader(this.getInputStream()));
      String line = null;
      StringBuilder inputBuffer = new StringBuilder();
      do {
        line = reader.readLine();
        if (null != line) {
          inputBuffer.append(line.trim());
        }
      } while (null != null);
      reader.close();

      return inputBuffer.toString().trim();
    }

  }

  private static final class BufferedServletInputStream extends ServletInputStream {
    private ByteArrayInputStream bais;

    private BufferedServletInputStream(ByteArrayInputStream bais) {
      this.bais = bais;
    }

    @Override
    public int available() {
      return this.bais.available();
    }

    @Override
    public boolean isFinished() {
      return false;
    }

    @Override
    public boolean isReady() {
      return true;
    }

    @Override
    public void setReadListener(ReadListener listener) {
    }

    @Override
    public int read() throws IOException {
      return this.bais.read();
    }

    @Override
    public int read(byte[] buf, int off, int len) {
      return this.bais.read(buf, off, len);
    }
  }

  public class TreeServletOutputStrem extends ServletOutputStream {

    private final TeeOutputStream targetStream;

    public TreeServletOutputStrem(OutputStream one, OutputStream two) {
      this.targetStream = new TeeOutputStream(one, two);
    }

    @Override
    public boolean isReady() {
      return false;
    }

    @Override
    public void setWriteListener(WriteListener listener) {
    }

    @Override
    public void write(int b) throws IOException {
      this.targetStream.write(b);
    }

    public void flush() throws IOException {
      super.flush();
      this.targetStream.flush();
    }

    public void close() throws IOException {
      super.close();
      this.targetStream.close();
    }
  }

  public class BufferedResponseWrapper implements HttpServletResponse {
    HttpServletResponse original;
    TreeServletOutputStrem tee;
    ByteArrayOutputStream baos;

    public BufferedResponseWrapper(HttpServletResponse response) {
      original = response;
    }

    public String getContent() {
      return baos.toString();
    }

    @Override
    public String getCharacterEncoding() {
      return original.getCharacterEncoding();
    }

    @Override
    public String getContentType() {
      return original.getContentType();
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
      if (tee == null) {
        baos = new ByteArrayOutputStream();
        tee = new TreeServletOutputStrem(original.getOutputStream(), baos);
      }
      return tee;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
      return original.getWriter();
    }

    @Override
    public void setCharacterEncoding(String charset) {
      original.setCharacterEncoding(charset);
    }

    @Override
    public void setContentLength(int len) {
      original.setContentLength(len);
    }

    @Override
    public void setContentLengthLong(long length) {
      original.setContentLengthLong(length);
    }

    @Override
    public void setContentType(String type) {
      original.setContentType(type);
    }

    @Override
    public void setBufferSize(int size) {
      original.setBufferSize(size);
    }

    @Override
    public int getBufferSize() {
      return original.getBufferSize();
    }

    @Override
    public void flushBuffer() throws IOException {
      tee.flush();
    }

    @Override
    public void resetBuffer() {
      original.resetBuffer();
    }

    @Override
    public boolean isCommitted() {
      return original.isCommitted();
    }

    @Override
    public void reset() {
      original.reset();
    }

    @Override
    public void setLocale(Locale loc) {
      original.setLocale(loc);
    }

    @Override
    public Locale getLocale() {
      return original.getLocale();
    }

    @Override
    public void addCookie(Cookie cookie) {
      original.addCookie(cookie);
    }

    @Override
    public boolean containsHeader(String name) {
      return original.containsHeader(name);
    }

    @Override
    public String encodeURL(String url) {
      return original.encodeURL(url);
    }

    @Override
    public String encodeRedirectURL(String url) {
      return "";
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {

    }

    @Override
    public void sendError(int sc) throws IOException {

    }

    @Override
    public void sendRedirect(String location) throws IOException {

    }

    @Override
    public void sendRedirect(String s, int i, boolean b) throws IOException {

    }

    @Override
    public void setDateHeader(String name, long date) {

    }

    @Override
    public void addDateHeader(String name, long date) {

    }

    @Override
    public void setHeader(String name, String value) {

    }

    @Override
    public void addHeader(String name, String value) {

    }

    @Override
    public void setIntHeader(String name, int value) {

    }

    @Override
    public void addIntHeader(String name, int value) {

    }

    @Override
    public void setStatus(int sc) {

    }

    @Override
    public int getStatus() {
      return 0;
    }

    @Override
    public String getHeader(String name) {
      return "";
    }

    @Override
    public Collection<String> getHeaders(String name) {
      return List.of();
    }

    @Override
    public Collection<String> getHeaderNames() {
      return List.of();
    }
  }
}

package com.gumdom.boot.infrastructure;

import org.apache.http.protocol.HttpService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AbstJavaStep extends AbstServiceStep implements IBasicExtension,IBasicServiceController,IBridgeAcrossProxyService {

    @Autowired
    HttpServletRequest servletRequest;

    @Autowired
    HttpServletResponse servletResponse;

    public AbstJavaStep() {
        super();
    }

    public AbstJavaStep(BridgeAcrossService bridgeAcrossService) {
        this(bridgeAcrossService,null);
    }

    public AbstJavaStep(BridgeAcrossService bridgeAcrossService, HttpServletRequest httpServletRequest) {
        super(bridgeAcrossService);
        this.servletRequest = httpServletRequest;
    }

    public void setServletResponse(HttpServletResponse servletResponse) {
        this.servletResponse = servletResponse;
    }

    public HttpServletRequest getServletRequest() {
        return servletRequest;
    }

    public void setServletRequest(HttpServletRequest servletRequest) {
        this.servletRequest = servletRequest;
    }

    public HttpServletResponse getServletResponse() {
        return servletResponse;
    }

    public void writeJsonToResponse(String text) throws IOException {
        this.servletResponse.reset();
        this.servletResponse.setContentType("application/json");
        this.servletResponse.setCharacterEncoding(this.servletRequest.getCharacterEncoding());

        if (this.isNotNullAndNotEmpty(text)) {
            this.servletResponse.getWriter().print(text);
        }else{
            this.servletResponse.getWriter().print("");
        }
        this.servletResponse.getWriter().flush();
    }

    public void outPutJsonToResponse(String text) throws IOException {
        this.servletResponse.reset();
        this.servletResponse.setContentType("application/json");
        this.servletResponse.setCharacterEncoding(this.servletRequest.getCharacterEncoding());

        if (this.isNotNullAndNotEmpty(text)) {
            this.servletResponse.getOutputStream().write(text.getBytes(this.servletResponse.getCharacterEncoding()));
        }else{
            this.servletResponse.getOutputStream().write(new byte[0]);
        }
        this.servletResponse.getOutputStream().flush();
    }

}

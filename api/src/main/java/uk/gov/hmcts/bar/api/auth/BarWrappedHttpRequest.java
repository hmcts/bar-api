package uk.gov.hmcts.bar.api.auth;

import lombok.Getter;
import uk.gov.hmcts.bar.api.data.model.BarUser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class BarWrappedHttpRequest extends HttpServletRequestWrapper {

    @Getter
    private BarUser barUser;

    /**
     * Constructs a request object wrapping the given request.
     *
     * @param request The request to wrap
     * @throws IllegalArgumentException if the request is null
     */
    public BarWrappedHttpRequest(HttpServletRequest request, BarUser barUser) {
        super(request);
        this.barUser = barUser;
    }


}

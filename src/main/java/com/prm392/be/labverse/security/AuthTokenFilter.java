package com.prm392.be.labverse.security;

import com.prm392.be.labverse.config.ApiEndpoint;
import com.prm392.be.labverse.dto.ErrorResponse;
import com.prm392.be.labverse.exception.AppException;
import com.prm392.be.labverse.exception.AuthErrorCode;
import com.prm392.be.labverse.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;


/**
 * Authentication filter
 */
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthTokenFilter extends OncePerRequestFilter {
    JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws
            ServletException, IOException {

        String uri = request.getRequestURI();
        log.info("{} go to AuthTokenFilter", uri);

        String contextPath = request.getContextPath();
        String path = uri.substring(contextPath.length());


        AntPathMatcher pathMatcher = new AntPathMatcher();
        boolean skipAuthenticate = Arrays.stream(ApiEndpoint.PUBLIC_API)
                .anyMatch(endpoint -> pathMatcher.match(endpoint, path));
        if (skipAuthenticate) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            //get access token from header
            String accessToken = request.getHeader("Authorization");
            if (accessToken == null || !accessToken.startsWith("Bearer ")) {
                throw new AppException(AuthErrorCode.MISSING_ACCESS_TOKEN);
            }

            accessToken = accessToken.substring("Bearer ".length());
            //validate accessToken
            jwtUtil.validateJwtAccessToken(accessToken);

//            //todo: làm lại chỗ này sau khi làm logout
//            //access token still valid -> check whether it invalidated (by logout or refresh)
////            if(tokenService.isAccessTokenInvalidated(accessToken)){
////                //access token is invalidated
////                log.info("The access token is invalidated, {}", accessToken);
////                throw new AppException(ErrorCode.UNAUTHENTICATED);
////            }


            //INFO: chỗ này đang tin tưởng hoàn toàn vào jwt mà ko check lại db
            // trong trường hợp lỡ như tk bị delete đi rồi, mà tk vẫn còn hiệu lực thì nó vẫn qua được filter này
            String email = jwtUtil.getUserEmailFromAccessToken(accessToken);
            String authorities = jwtUtil.getUserRoleFromAccessToken(accessToken);

            log.info("Authorities: {}", authorities);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    email, null, AuthorityUtils.commaSeparatedStringToAuthorityList(authorities));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("Authenticate user successfully! email: {}", email);
        } catch (Exception e) {
            String exceptionMessage = e.getMessage();
            if (e instanceof AppException appException) {
                ErrorResponse errorResponse = new ErrorResponse(appException.getCode(), appException.getMessage());
                request.setAttribute("AUTH_RESPONSE", errorResponse);
            } else {
                log.error("A strange error happen in jwt filter: {}", exceptionMessage);
            }
        }

        //continue filter chain
        filterChain.doFilter(request, response);
    }

}

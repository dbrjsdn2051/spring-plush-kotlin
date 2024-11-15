package org.example.exportkotlin.config.security.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.example.exportkotlin.exception.CustomApiException
import org.springframework.http.MediaType
import org.springframework.web.filter.OncePerRequestFilter

class GlobalExceptionHandlerFilter(
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            filterChain.doFilter(request, response)
        } catch (e: CustomApiException) {
            extracted(response, e)
        } catch (e: Exception) {
            extracted(response, e)
        }
    }

    private fun extracted(
        response: HttpServletResponse,
        e: Exception
    ) {
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.status = HttpServletResponse.SC_BAD_REQUEST
        response.characterEncoding = "UTF-8"
        response.writer.write(e.message.toString())
    }
}
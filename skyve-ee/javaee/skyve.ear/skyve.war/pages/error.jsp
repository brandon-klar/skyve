<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page isErrorPage="true"%>
<%@page import="java.security.Principal"%>
<%@page import="java.util.Locale"%>
<%@page import="org.skyve.metadata.user.User"%>
<%@page import="org.skyve.util.Util"%>
<%@page import="org.skyve.impl.web.UserAgent"%>
<%@page import="org.skyve.impl.web.WebUtil"%>
<%
	String basePath = Util.getSkyveContextUrl() + "/";
	boolean mobile = UserAgent.getType(request).isMobile();
	String referer = WebUtil.getRefererHeader(request);
	Principal p = request.getUserPrincipal();
	User user = WebUtil.processUserPrincipalForRequest(request, (p == null) ? null : p.getName(), true);
	Locale locale = (user == null) ? request.getLocale() : user.getLocale();
%>
<!DOCTYPE html>
<html dir="<%=Util.isRTL(locale) ? "rtl" : "ltr"%>">
	<head>
		<!-- Standard Meta -->
	    <meta charset="utf-8" />
	    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
		<meta http-equiv="pragma" content="no-cache" />
		<meta http-equiv="cache-control" content="private,no-cache,no-store" />
		<meta http-equiv="expires" content="0" />
	    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0">

		<!-- Site Properties -->	    
		<title><%=Util.i18n("page.error.title", locale)%></title>
		<base href="<%=basePath%>" />

		<% if (mobile) { %>
			<meta name="format-detection" content="telephone=no" />
			<meta name="format-detection" content="email=no">
		<% } %>

		<link rel="icon" type="image/png" href="images/window/skyve_fav.png" />
		<link rel="apple-touch-icon" href="images/window/skyve_fav.png">
		<link rel="stylesheet" href="semantic24/semantic.min.css">
		
		<%@include file="fragments/styles.html" %>
	</head>
	<body>
		<div class="ui middle aligned center aligned grid">
		    <div class="column">
		    	<%@include file="fragments/logo.html" %>
		    	
		        <form class="ui large form">
		            <div class="ui segment">
		            	<div class="ui red message">
		            		<div class="ui header">
		            			<%=Util.i18n("page.error.banner", locale)%>
		            		</div>
		            		<div class="field">
		            			<%=Util.i18n("page.error.explanation", locale)%>
		            		</div>
		            	</div>
						<div class="field">
							<a href="<%=request.getContextPath()%>" class="ui fluid large blue submit button"><%=Util.i18n("page.loginError.retry", locale)%></a>
						</div>
						<div class="field">
							<a href="mailto:<%=org.skyve.util.Util.getSupportEmailAddress()%>?subject=Exception Report&body=<%=(exception == null) ? Util.i18n("page.loginError.noMessage", locale) : exception.getLocalizedMessage()%> 
							for <%=(request.getUserPrincipal() != null) ? request.getUserPrincipal().getName() : Util.i18n("page.loginError.notLoggedIn", locale)%> 
						 	<%=new java.util.Date()%>" class="ui fluid large blue basic button"><i class="envelope icon"></i><%=Util.i18n("page.loginError.report", locale)%></a>
					 	</div>
		            </div>
		        </form>
		    </div>
		</div>
	</body>
</html>

package io.github.marcperez06.java_parser.resources.objects.swagger;

import java.util.HashMap;
import java.util.Map;

public class SwaggerEndpoint {
	
	private SwaggerRequestInfo get;
	private SwaggerRequestInfo post;
	private SwaggerRequestInfo put;
	private SwaggerRequestInfo delete;
	private SwaggerRequestInfo patch;
	
	public SwaggerEndpoint() {
		
	}

	public SwaggerRequestInfo getGet() {
		return this.get;
	}

	public void setGet(SwaggerRequestInfo get) {
		this.get = get;
	}

	public SwaggerRequestInfo getPost() {
		return this.post;
	}

	public void setPost(SwaggerRequestInfo post) {
		this.post = post;
	}

	public SwaggerRequestInfo getPut() {
		return this.put;
	}

	public void setPut(SwaggerRequestInfo put) {
		this.put = put;
	}

	public SwaggerRequestInfo getDelete() {
		return this.delete;
	}

	public void setDelete(SwaggerRequestInfo delete) {
		this.delete = delete;
	}
	
	public SwaggerRequestInfo getPatch() {
		return this.patch;
	}

	public void setPatch(SwaggerRequestInfo patch) {
		this.patch = patch;
	}
	
	public boolean haveAnyRequestInfo() {
		boolean haveAnyRequestInfo = (this.get != null);
		haveAnyRequestInfo |= (this.post != null);
		haveAnyRequestInfo |= (this.put != null);
		haveAnyRequestInfo |= (this.delete != null);
		haveAnyRequestInfo |= (this.patch != null);
		return haveAnyRequestInfo;
	}
	
	public SwaggerRequestInfo getRequestInfo() {
		SwaggerRequestInfo requestInfo = null;
		boolean notRequestInfoAssigned = true;

		if (this.haveAnyRequestInfo()) {
			
			if (this.get != null && notRequestInfoAssigned) {
				requestInfo = this.get;
				notRequestInfoAssigned = false;
			}
			
			if (this.post != null && notRequestInfoAssigned) {
				requestInfo = this.post;
				notRequestInfoAssigned = false;
			}
			
			if (this.put != null && notRequestInfoAssigned) {
				requestInfo = this.put;
				notRequestInfoAssigned = false;
			}
			
			if (this.delete != null && notRequestInfoAssigned) {
				requestInfo = this.delete;
				notRequestInfoAssigned = false;
			}
			
			if (this.patch != null && notRequestInfoAssigned) {
				requestInfo = this.patch;
				notRequestInfoAssigned = false;
			}

		}
		return requestInfo;
	}
	
	public Map<String, SwaggerRequestInfo> getAllRequestInfo() {
		Map<String, SwaggerRequestInfo> allRequestInfo = new HashMap<String, SwaggerRequestInfo>();
		
		if (this.get != null) {
			allRequestInfo.put("GET", this.get);
		}
		
		if (this.post != null) {
			allRequestInfo.put("POST", this.post);
		}
		
		if (this.put != null) {
			allRequestInfo.put("PUT", this.put);
		}
		
		if (this.delete != null) {
			allRequestInfo.put("DELETE", this.delete);
		}
		
		if (this.patch != null) {
			allRequestInfo.put("PATCH", this.patch);
		}

		return allRequestInfo;
	}

}
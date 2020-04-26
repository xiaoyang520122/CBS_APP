package com.cninsure.cp.entity.yjx;

import java.io.Serializable;

public class GeoCoderEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**状态
	 * 返回码	英文描述	定义							常见原因
	 * 0	ok		正常						服务请求正常召回
	 * 1			服务器内部错误
	 * 2	Parameter Invalid	请求参数非法	必要参数拼写错误或漏传（如query和tag请求中均未传入）
	 * 3	Verify Failure	权限校验失败	
	 * 4	Quota Failure	配额校验失败		服务当日调用次数已超限，请前往API控制台提升（请优先进行开发者认证）
	 * 5	AK Failure	ak不存在或者非法			未传入ak参数；ak已被删除（可前往回收站恢复）；
	 * 101				 服务禁用
	 * 102	 			不通过白名单或者安全码不对
	 * 2xx 	 			无权限
	 * 3xx	  			配额错误
	 * */
	public int status; //状态
	public Location location;
	
	public static class Location{
		/**纬度值*/
		public int lat;	
		/**经度值*/
		public int lng;	
		/** 位置的附加信息，是否精确查找。1为精确查找，即准确打点；0为不精确，即模糊打点。*/
		public int precise;
	}

}

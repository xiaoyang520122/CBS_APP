package com.cninsure.cp.entity.fc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FCCaseTypeEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 根据险种id和险种 */
	public static Integer getCaseTypeId(int value, String caseType) {
		String id = getTypeMap(value).get(caseType);
		return Integer.valueOf(id);
	}

	public static List<String> getTypeList(int value) {
		switch (value) {
		case 0:
			return getListByMap(getTypeMap(0));

		case 1:
			return getListByMap(getTypeMap(1));

		case 3:
			return getListByMap(getTypeMap(3));

		case 19:
			return getListByMap(getTypeMap(19));

		case 25:
			return getListByMap(getTypeMap(25));

		case 27:
			return getListByMap(getTypeMap(27));

		case 62:
			return getListByMap(getTypeMap(62));

		case 66:
			return getListByMap(getTypeMap(66));

		case 70:
			return getListByMap(getTypeMap(70));

		case 77:
			return getListByMap(getTypeMap(77));

		case 80:
			return getListByMap(getTypeMap(80));

		case 87:
			return getListByMap(getTypeMap(87));

		case 99:
			return getListByMap(getTypeMap(99));

		case 106:
			return getListByMap(getTypeMap(106));

		case 107:
			return getListByMap(getTypeMap(107));

		case 108:
			return getListByMap(getTypeMap(108));

		default:
			return getListByMap(getTypeMap(0));
		}
	}

	private static List<String> getListByMap(Map<String, String> map) {
		List<String> tempList = new ArrayList<String>();
		for (String str : map.keySet()) {
			tempList.add(str);
		}
		return tempList;
	}

	public static Map<String, String> getTypeMap(int value) {
		switch (value) {
		case 1:
			return getMap1();

		case 3:
			return getMap3();

		case 19:
			return getMap19();

		case 25:
			return getMap25();

		case 27:
			return getMap27();

		case 62:
			return getMap62();

		case 66:
			return getMap66();

		case 70:
			return getMap70();

		case 77:
			return getMap77();

		case 80:
			return getMap80();

		case 87:
			return getMap87();

		case 99:
			return getMap99();

		case 106:
			return getMap106();

		case 107:
			return getMap107();

		case 108:
			return getMap108();

		default:
			Map<String, String> tempMap = new HashMap<String, String>();
			tempMap.put("请选择","");
			return tempMap;
		}
	}

	private static Map<String, String> getMap1() {
		Map<String, String> tempMap = new HashMap<String, String>();
		tempMap.put("机动车商业保险条款", "2");
		return tempMap;
	}

	private static Map<String, String> getMap3() {
		Map<String, String> tempMap = new HashMap<String, String>();
		tempMap.put("船东保障和赔偿责任保险", "4");
		tempMap.put("沿海船舶燃油污染责任保险", "5");
		tempMap.put("沿海河内船舶保险", "6");
		tempMap.put("沿海河内船舶保险附加拖轮拖带责任保险", "7");
		tempMap.put("沿海河内船舶保险附加船主对旅客责任保险", "8");
		tempMap.put("沿海河内船舶保险附加船东对船员责任保险", "9");
		tempMap.put("沿海河内船舶保险附加四分之一碰撞，触碰责任保险", "10");
		tempMap.put("沿海内河船舶保险附加螺旋桨、舵、锚、锚链及子船单独损失保险条款", "11");
		tempMap.put("沿海河内船舶保险附加四分之三碰撞，触碰责任保险", "12");
		tempMap.put("沿海河内船舶保险附加油污责任险条款", "13");
		tempMap.put("沿海河内船舶保险附加货物运输承运人保险", "14");
		tempMap.put("沿海河内船舶保险附加第三者人身伤亡责任险", "15");
		tempMap.put("船舶建造保险", "16");
		tempMap.put("沿海河内船舶建造保险", "17");
		tempMap.put("修船责任保险协议书", "18");
		return tempMap;
	}

	private static Map<String, String> getMap19() {
		Map<String, String> tempMap = new HashMap<String, String>();
		tempMap.put("建筑安装工程保险", "20");
		tempMap.put("公路综合险", "21");
		tempMap.put("安装工程一切险", "22");
		tempMap.put("道路建筑工程一切险", "23");
		tempMap.put("地铁安装工程一切险", "24");
		tempMap.put("地铁建筑工程一切险", "25");
		tempMap.put("风电企业建筑安装工程一切险", "26");
		return tempMap;
	}

	private static Map<String, String> getMap25() {
		Map<String, String> tempMap = new HashMap<String, String>();
		tempMap.put("施工机械综合保险", "65");
		return tempMap;
	}


	private static Map<String, String> getMap27() {
		Map<String, String> tempMap = new HashMap<String, String>();
		tempMap.put("包装破裂险", "28");
		tempMap.put("舱面货物条款", "29");
		tempMap.put("串味险", "30");
		tempMap.put("出口货物香港（包括九龙）或澳门存仓火险责任扩展保险", "31");
		tempMap.put("淡水，雨林险", "32");
		tempMap.put("短量险", "33");
		tempMap.put("公路货物运输保险", "34");
		tempMap.put("公路货物运输定额保险", "35");
		tempMap.put("钩损险", "36");
		tempMap.put("国内航空货物运输保险", "37");
		tempMap.put("国内航空旅客行李保险", "38");
		tempMap.put("国内水路，陆路货物运输保险", "39");
		tempMap.put("海关检验条款", "40");
		tempMap.put("海洋运输货物保险", "41");
		tempMap.put("海运进口货物国内转运期间保险", "42");
		tempMap.put("航空运输货物保险", "43");
		tempMap.put("黄曲霉素险", "44");
		tempMap.put("混杂，污染险", "45");
		tempMap.put("渗漏险", "46");
		tempMap.put("受潮受热险", "47");
		tempMap.put("水路货物运输保险", "48");
		tempMap.put("铁路货物运输保险", "49");
		tempMap.put("偷窃，提货不着险", "50");
		tempMap.put("物流货物保险", "51");
		tempMap.put("锈损险", "52");
		tempMap.put("易腐货物条款", "53");
		tempMap.put("交货不到条款", "54");
		tempMap.put("进口关税条款", "55");
		tempMap.put("进口集装箱货物运输特别条款", "56");
		tempMap.put("拒收险条款", "57");
		tempMap.put("路上运输货物险条款", "58");
		tempMap.put("码头检验条款", "59");
		tempMap.put("卖方利益保险", "60");
		tempMap.put("碰损，破损险条款", "61");
		return tempMap;
	}

	private static Map<String, String> getMap62() {
		Map<String, String> tempMap = new HashMap<String, String>();
		tempMap.put("电厂机器损坏保险", "63");
		tempMap.put("机器损坏保险", "64");
		tempMap.put("施工机械综合保险", "65");
		return tempMap;
	}

	private static Map<String, String> getMap66() {
		Map<String, String> tempMap = new HashMap<String, String>();
		tempMap.put("个人贷款抵押房屋保险", "67");
		tempMap.put("个人贷款抵押房屋综合险", "68");
		tempMap.put("家庭财产基本险（房屋）", "69");
		tempMap.put("家庭财产基本险（室内财产）", "70");
		tempMap.put("家庭财产盗抢保险", "71");
		tempMap.put("家庭财产管道破裂及水渍保险", "72");
		tempMap.put("家庭财产火灾爆炸险", "73");
		tempMap.put("家庭财产综合保险", "74");
		tempMap.put("家庭住房装修工程保险", "75");
		tempMap.put("家用电器用电安全保险", "76");
		return tempMap;
	}

	private static Map<String, String> getMap70() {
		Map<String, String> tempMap = new HashMap<String, String>();
		tempMap.put("风险查勘", "156");
		tempMap.put("风险评估", "83");
		return tempMap;
	}

	private static Map<String, String> getMap77() {
		Map<String, String> tempMap = new HashMap<String, String>();
		tempMap.put("电厂营业中断保险", "78");
		tempMap.put("风电企业营业中断险", "79");
		return tempMap;
	}

	private static Map<String, String> getMap80() {
		Map<String, String> tempMap = new HashMap<String, String>();
		tempMap.put("计算机保险", "81");
		tempMap.put("现金保险", "82");
		tempMap.put("风险评估", "83");
		tempMap.put("标的评估", "84");
		tempMap.put("个体工商户财产保险", "85");
		tempMap.put("保赔险", "86");
		return tempMap;
	}

	private static Map<String, String> getMap87() {
		Map<String, String> tempMap = new HashMap<String, String>();
		tempMap.put("财产一切险", "88");
		tempMap.put("财产综合险", "89");
		tempMap.put("商业楼宇财产基本险", "90");
		tempMap.put("商业楼宇财产综合险", "91");
		tempMap.put("商业楼宇财产一切险", "92");
		tempMap.put("珠宝商综合保险", "93");
		tempMap.put("电厂财产基本险", "94");
		tempMap.put("电厂财产一切险", "95");
		tempMap.put("电厂财产综合险", "96");
		tempMap.put("风电企业运营期一切险", "97");
		tempMap.put("其他财产保险", "98");
		return tempMap;
	}

	private static Map<String, String> getMap99() {
		Map<String, String> tempMap = new HashMap<String, String>();
		tempMap.put("意外伤害身故保险", "100");
		tempMap.put("意外伤害保险", "101");
		tempMap.put("团体意外伤害身故保险", "102");
		tempMap.put("团体意外伤害保险", "103");
		tempMap.put("意外伤害医疗费用保险", "104");
		tempMap.put("意外伤害生活津贴保险", "105");
		return tempMap;
	}

	private static Map<String, String> getMap106() {
		Map<String, String> tempMap = new HashMap<String, String>();
		tempMap.put("公众责任保险", "107");
		tempMap.put("餐饮场所责任保险", "108");
		tempMap.put("供电责任保险", "109");
		tempMap.put("产品责任保险", "110");
		tempMap.put("雇主责任保险", "111");
		tempMap.put("建设工程设计责任保险", "112");
		tempMap.put("物业管理责任保险", "113");
		tempMap.put("校园方责任保险", "114");
		tempMap.put("旅行社责任保险", "115");
		tempMap.put("单项工程设计责任保险", "116");
		tempMap.put("监护人责任保险", "117");
		tempMap.put("工程监理责任保险", "118");
		tempMap.put("医疗责任保险", "119");
		tempMap.put("机动车辆停车场责任保险", "120");
		tempMap.put("董事，监事及高级管理人员职业责任保险", "121");
		tempMap.put("建筑工程勘察责任保险", "122");
		tempMap.put("船舶检验师职业责任保险", "123");
		tempMap.put("特种设备第三者责任保险", "124");
		tempMap.put("电梯责任保险", "125");
		tempMap.put("个人责任保险", "126");
		tempMap.put("承运人旅客责任保险", "127");
		tempMap.put("物流责任保险", "128");
		tempMap.put("个人综合责任保险", "129");
		tempMap.put("家庭雇佣责任保险", "130");
		tempMap.put("工伤责任保险", "131");
		tempMap.put("道路客运承运人责任保险", "132");
		tempMap.put("吊装责任保险", "133");
		tempMap.put("雇主忠诚责任保险", "134");
		tempMap.put("产品质量保证险", "135");
		tempMap.put("旅游场所公众责任保险", "136");
		tempMap.put("烟花爆竹企业安全生产责任险", "137");
		tempMap.put("危险化学品企业安全生产责任保险", "138");
		tempMap.put("煤矿企业安全生产责任保险", "139");
		tempMap.put("非煤矿安全生产责任保险", "140");
		tempMap.put("国际货运代理人责任保险", "141");
		tempMap.put("建筑工程质量责任保险", "142");
		tempMap.put("验光师职业责任保险", "143");
		tempMap.put("旅行社责任保险", "144");
		tempMap.put("道路承运人责任保险", "145");
		tempMap.put("援外人员责任保险", "146");
		tempMap.put("电动自行车第三者责任保险", "147");
		tempMap.put("免疫接种责任保险", "148");
		tempMap.put("保险公估机构职业责任保险", "149");
		tempMap.put("火灾公众责任保险", "150");
		tempMap.put("保安公司责任保险条款", "151");
		tempMap.put("特种设备检验责任保险", "152");
		tempMap.put("道路客运承运人责任保险", "153");
		tempMap.put("道路危险货物承运人责任保险", "154");
		tempMap.put("雇主责任保险条款", "155");
		return tempMap;
	}

	private static Map<String, String> getMap107() {
		Map<String, String> tempMap = new HashMap<String, String>();
		tempMap.put("农业险", "156");
		return tempMap;
	}
	private static Map<String, String> getMap108() {
		Map<String, String> tempMap = new HashMap<String, String>();
//		tempMap.put("请选择","");
		tempMap.put("碎屏险", "1081");
		tempMap.put("手机信用保险", "1082");
		tempMap.put("疫苗险", "1083");
		tempMap.put("合家欢", "1084");
		tempMap.put("其他", "1085");
		return tempMap;
	}

	/** 根据水险险种分类 获取业务类型 **/
	public static List<String> getWaterTypeList(int value) {
		switch (value) {
		case 1:
			return getListByMap(getWaterMap(1));

		case 2:
			return getListByMap(getWaterMap(2));

		case 3:
			return getListByMap(getWaterMap(3));

		case 4:
			return getListByMap(getWaterMap(4));

		case 5:
			return getListByMap(getWaterMap(5));

		case 6:
			return getListByMap(getWaterMap(6));

		case 7:
			return getListByMap(getWaterMap(7));

		default:
			return getListByMap(getWaterMap(0));
		}
	}

	private static Map<String, String> getWaterMap(int value) {
		switch (value) {
		case 1:
			return getWMap1();

		case 2:
			return getWMap2();

		case 3:
			return getWMap3();

		case 4:
			return getWMap4();

		case 5:
			return getWMap5();

		case 6:
			return getWMap6();

		default:
			Map<String, String> tempMap = new HashMap<String, String>();
			tempMap.put("请选择","");
			return tempMap;
		}
	}

	private static Map<String, String> getWMap1() {
		Map<String, String> tempMap = new HashMap<String, String>();
		tempMap.put("船舶碰撞或损坏", "1");
		tempMap.put("承保状况检验", "2");
		tempMap.put("买卖状况检验", "3");
		tempMap.put("船舶建造", "4");
		tempMap.put("船舶价值评估", "5");
		return tempMap;
	}
	private static Map<String, String> getWMap2() {
		Map<String, String> tempMap = new HashMap<String, String>();
		tempMap.put("公估案件（中）","6"); 
		tempMap.put("公估案件（英）","7"); 
		tempMap.put("代查勘案件（中）","8"); 
		tempMap.put("代查勘案件（英）","9"); 
		return tempMap;
	}

	private static Map<String, String> getWMap3() {
		Map<String, String> tempMap = new HashMap<String, String>();
		tempMap.put("入会检验","10"); 
		tempMap.put("船舶碰撞或损坏","11"); 
		tempMap.put("货物损坏","12"); 
		tempMap.put("人员伤亡","13"); 
		tempMap.put("油污污染","14"); 
		tempMap.put("船期损失","15"); 
		tempMap.put("机损检验","16"); 
		tempMap.put("人员偷渡","17"); 
		return tempMap;
	}

	private static Map<String, String> getWMap4() {
		Map<String, String> tempMap = new HashMap<String, String>();
		tempMap.put("水尺计重","18"); 
		tempMap.put("起退租","19"); 
		tempMap.put("起退租及船况检验","20"); 
		tempMap.put("监装监卸","21"); 
		tempMap.put("集装箱监装","22"); 
		tempMap.put("封舱/开封","23"); 
		tempMap.put("取样","24"); 
		tempMap.put("液体货物计量","25"); 
		tempMap.put("大件货物绑扎","26"); 
		tempMap.put("货物丈量","27"); 
		tempMap.put("货物监管","28"); 
		tempMap.put("清洁状况","29"); 
		tempMap.put("水密测试","30"); 
		tempMap.put("港口船长服务","31"); 
		tempMap.put("集装箱拆箱","32"); 
		return tempMap;
	}
	
	private static Map<String, String> getWMap5() {
		Map<String, String> tempMap = new HashMap<String, String>();
		tempMap.put("存货检验","33"); 
		tempMap.put("DPS钢材监装","34"); 
		tempMap.put("DPS钢材监卸","35"); 
		tempMap.put("DPS货损检验","36"); 
		tempMap.put("DPS船舶适货检验","37"); 
		tempMap.put("DPS仓库适货检验","38"); 
		tempMap.put("DPS水果检验","39"); 
		tempMap.put("Cargill仓库检验","40"); 
		tempMap.put("订购商品查验","41");
		return tempMap;
	}
	
	private static Map<String, String> getWMap6() {
		Map<String, String> tempMap = new HashMap<String, String>();
		tempMap.put(" Lloyd's report","42"); 
		tempMap.put("其他","43"); 
		tempMap.put("资料翻译","44"); 
		return tempMap;
	}

}

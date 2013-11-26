package restaurant_cafe.gui;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

public class TableList {
	@SuppressWarnings("serial")
	private static final Map<Integer, Point> tableMap = new HashMap<Integer, Point>(){
		{
			put(1, new Point(178, 64));
			put(2, new Point(92, 218));
			put(3, new Point(174, 218));
			put(4, new Point(244, 218));
		}
	};
	
	public static Map<Integer, Point> getTableMap(){
		return tableMap;
	}
	public Point getTablePoint(int n) {
		return tableMap.get(n);
	}
}


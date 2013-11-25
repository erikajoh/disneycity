package restaurant_cafe.gui;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

public class TableList {
	@SuppressWarnings("serial")
	private static final Map<Integer, Point> tableMap = new HashMap<Integer, Point>(){
		{
			put(1, new Point(110, 300));
			put(2, new Point(360, 100));
			put(3, new Point(360, 300));
		}
	};
	
	public static Map<Integer, Point> getTableMap(){
		return tableMap;
	}
	public Point getTablePoint(int n) {
		return tableMap.get(n);
	}
}


package market.interfaces;

import market.CustomerAgent.State;

public interface Customer {
	public void msgLineMoved();
	
	public void msgHereIsItemAndBill(int num, double amt);
	
	public void msgOutOfItem();
	
	public void msgHereIsChange(double amt);
	
	public void msgHereIsMoney(double amt);
	
	public void msgAnimationMoveUpFinished();
	
	public void msgAnimationFinished();
}

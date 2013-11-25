package restaurant_haus.interfaces;


public interface Market {
	public abstract void msgOrderPayment(double money, boolean paidFull);
	
	public abstract void msgFinalPayment();
	public abstract void msgIncrementalPayment(double money);
}
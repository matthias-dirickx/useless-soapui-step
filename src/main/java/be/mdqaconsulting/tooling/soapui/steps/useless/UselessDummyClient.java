package be.mdqaconsulting.tooling.soapui.steps.useless;

public class UselessDummyClient {
	private String ip;
	private String port;
	private String inputMessage;
	private byte[] outputMessage;
	private boolean isCompleted;
	
	UselessDummyClient(String ip, String port) {
		this.ip = ip;
		this.port = port;
	}
	
	public void executeRaw(String inputMessage) {
		this.inputMessage = inputMessage;
		this.outputMessage = String.format("No, you %s!", inputMessage).getBytes();
		//Set random failure - sure, why not.
		double failChance = Math.random();
		this.isCompleted = failChance > 0.8;
	}
	
	public byte[] getRawResponse() {
		return this.outputMessage;
	}
	
	public boolean isCompleted() {
		return this.isCompleted;
	}
}
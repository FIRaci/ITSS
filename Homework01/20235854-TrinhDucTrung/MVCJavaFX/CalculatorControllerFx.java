public class CalculatorControllerFx {
	private CalculatorViewFx theView;
	private CalculatorModelFx theModel;
	
	public CalculatorControllerFx(CalculatorViewFx theView, CalculatorModelFx theModel) {
		this.theView = theView;
		this.theModel = theModel;
		
		this.theView.getCalculateButton().setOnAction(e -> {
			try{
				int firstNumber = theView.getFirstNumber();
				int secondNumber = theView.getSecondNumber();
				
				theModel.addTwoNumbers(firstNumber, secondNumber);
				theView.setCalcSolution(theModel.getCalculationValue());
			}
			catch(NumberFormatException ex){
				System.out.println(ex);
				theView.displayErrorMessage("Vui lòng điền số nguyên");
			}
		});
	}
}

public class CalculatorControllerFx {public class CalculatorControllerFx {

	private CalculatorViewFx theView;	private CalculatorViewFx theView;

	private CalculatorModelFx theModel;	private CalculatorModelFx theModel;

		

	public CalculatorControllerFx(CalculatorViewFx theView, CalculatorModelFx theModel) {	public CalculatorControllerFx(CalculatorViewFx theView, CalculatorModelFx theModel) {

		this.theView = theView;		this.theView = theView;

		this.theModel = theModel;		this.theModel = theModel;

				

		this.theView.getCalculateButton().setOnAction(e -> {		this.theView.getCalculateButton().setOnAction(e -> {

			try{			try{

				int firstNumber = theView.getFirstNumber();				int firstNumber = theView.getFirstNumber();

				int secondNumber = theView.getSecondNumber();				int secondNumber = theView.getSecondNumber();

								

				theModel.addTwoNumbers(firstNumber, secondNumber);				theModel.addTwoNumbers(firstNumber, secondNumber);

				theView.setCalcSolution(theModel.getCalculationValue());				theView.setCalcSolution(theModel.getCalculationValue());

			}			}

			catch(NumberFormatException ex){			catch(NumberFormatException ex){

				System.out.println(ex);				System.out.println(ex);

				theView.displayErrorMessage("Vui lòng điền số nguyên");				theView.displayErrorMessage("Vui lòng điền số nguyên");

			}			}

		});		});

	}	}

}}


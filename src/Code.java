import java.util.ArrayList;

public class Code {
	public final static int SLOTLENGTH = 4;
	private Color[] slots;
	Code(Color[] inColor){
		slots = inColor;
	}
	Code(){
		slots = new Color[SLOTLENGTH];
		for(int i = 0; i<SLOTLENGTH;i++){
			slots[i] = Color.EMPTY;
		}
	}
	public boolean equals(Code inCode){
		boolean isEqual = true;
		for(int i = 0; i<SLOTLENGTH;i++){
			if(this.slots[i]!=inCode.getSlots()[i]){
				isEqual = false;
			}
		}

		return isEqual;
	}
	/**
	 * 
	 * @param inCode Code to count pegs on.
	 * @return Aggregate int of number of black and white pegs.
	 */
	public int pegs(Code inCode){
		ArrayList<Color> slotColors = new ArrayList<Color>();
		ArrayList<Color> inCodeColors = new ArrayList<Color>();
		{
			for(int i = 0; i<SLOTLENGTH; i++){
				inCodeColors.add(inCode.getSlots()[i]);
			}
			for(int i = 0; i<SLOTLENGTH; i++){
				slotColors.add(this.getSlots()[i]);
			}
		}
		int blackPegs = 0;
		int whitePegs = 0;


		{
			for(int i = 0; i<inCodeColors.size(); i++){
				if(inCodeColors.get(i)==slotColors.get(i)){
					blackPegs++;
					slotColors.remove(inCodeColors.get(i));
					inCodeColors.remove(inCodeColors.get(i));
					//					System.out.println(slotColors);
					//					System.out.println(inCodeColors);
					//					System.out.println();
					i--;
				}
			}
			//			System.out.println();
			//			System.out.println(slotColors);
			//			System.out.println(inCodeColors);
			//			System.out.println();
			for(int i = 0; i<inCodeColors.size(); i++){
				if(slotColors.contains(inCodeColors.get(i))){
					whitePegs++;
					slotColors.remove(inCodeColors.get(i));
					inCodeColors.remove(inCodeColors.get(i));
					//					System.out.println(slotColors);
					//					System.out.println(inCodeColors);
					//					System.out.println();
					i--;

				}
			}
		}
		int outPegs = Integer.parseInt(Integer.toString(blackPegs)+Integer.toString(whitePegs));
		//int[] outPegs = {blackPegs, whitePegs};
		return outPegs;
	}

	@Override
	public String toString(){
		String outString = "[ ";
		for(int i = 0; i<SLOTLENGTH;i++){
			outString += this.slots[i] + " ";
		}
		outString+="]";
		return outString;

	}
	public Color[] getSlots() {
		return slots;
	}


	public void setSlots(Color[] slots) {
		this.slots = slots;
	}


}

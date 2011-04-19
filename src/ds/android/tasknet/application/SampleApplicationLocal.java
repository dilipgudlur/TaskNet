/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ds.android.tasknet.application;

import java.util.ArrayList;
import java.util.Vector;

/* * @author Divya_PKV
 */
public class SampleApplicationLocal {

    public ArrayList<Double> method1(int a, int b) {
        //System.out.println("Method1: a = " + a + " b = " + b);
        //return a + b;
        
        int nnumberofFilters = a;	//earlier value was 24, now set to a
        int nlifteringCoefficient = 22;
        boolean oisLifteringEnabled = true;
        boolean oisZeroThCepstralCoefficientCalculated = false;
        int nnumberOfMFCCParameters = b; //earlier value was 12, now set to b//without considering 0-th
        double dsamplingFrequency = 8000.0;
        int nFFTLength = 512;
        //Vector[] mfcc_parameters = new Vector[15];
        
        ArrayList<Double> mfcc_parameters = new ArrayList<Double>();
        
        //Double d;
        
        
        if (oisZeroThCepstralCoefficientCalculated) {
          //take in account the zero-th MFCC
          nnumberOfMFCCParameters = nnumberOfMFCCParameters + 1;
        }
        else {
          nnumberOfMFCCParameters = nnumberOfMFCCParameters;
        }

        MFCC mfcc = new MFCC(nnumberOfMFCCParameters,
                             dsamplingFrequency,
                             nnumberofFilters,
                             nFFTLength,
                             oisLifteringEnabled,
                             nlifteringCoefficient,
                             oisZeroThCepstralCoefficientCalculated);

        //System.out.println(mfcc.toString());

        //simulate a frame of speech
        double[] x = new double[160];
        x[2]=10; x[4]=14;
        double[] dparameters = mfcc.getParameters(x);
        //System.out.println("MFCC parameters:");
        //mfcc_parameters.length = dparameters.length ;
        for (int i = 0; i < dparameters.length; i++) 
        	mfcc_parameters.add(dparameters[i]);
        	     	 
        return mfcc_parameters;
      
    }

    public void method2() {
        System.out.println("Method2");
    }

    public void method3() {
        System.out.println("Method3");
    }

    public void method4() {
        System.out.println("Method4");
    }
}

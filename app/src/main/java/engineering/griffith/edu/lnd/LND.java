package engineering.griffith.edu.lnd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
public class LND {

    /*
     * Input parameters
     */
    private int current_month;
    private double LF;
    private double NF;
    private double HF;
    private double LNDLev;
    private double WTPIntake;

    /*
     * Output values
     */
    public double[] LND_Level;
    public double[] LNDFIN;
    public double[] SPILL_RISK;
    public double AverageWTP;

    public LND(){
        LND_Level = new double[3];
        SPILL_RISK = new double[3];
        AverageWTP = 0;
    }

    public void execute_LND(int current_month, double LF,
                            double NF, double HF, double LNDLev, double WTPIntake) {
        this.current_month = current_month;
        this.LF = LF;
        this.NF = NF;
        this.HF = HF;
        this.LNDLev = LNDLev;
        this.WTPIntake = WTPIntake;

        int[] range = {123, 169};
        double[] Av_Flow = {1480.54, 1092.66, 772.17, 614.37, 529.07, 426.38, 389.92,
                362.15, 413.34, 655.59, 1149.92, 1512.81};
        //SSF uses reference to median, but exponential distribution needs average;
        //however we extract random numbers based on median and convert to inflow
        //using the equation with average
        double meanfl = Av_Flow[current_month];

        //specify month by month where the median value sits in the inflow exp CDF
        double[] median = {0.27, 0.32, 0.33, 0.28, 0.24, 0.22,
                0.20, 0.21, 0.19, 0.17, 0.16, 0.18};

        //RANDOM NUMBER GENERATOR FOR INFLOW
        double sim=10000;
        double weeksahead=6;

        //following lines to avoid error if sum is not 100%. It will change the 3
        //numbers proportionally to obtain 100
        double LFNFHF = LF + NF + HF;
        if (LFNFHF > 100) {
            HF = Math.round(HF * 100 / (LFNFHF));
            NF = Math.round(NF * 100 / (LFNFHF));
            LF = Math.round(LF * 100 / (LFNFHF));
        } else if (LFNFHF < 100) {
            HF = Math.round(HF * 100 / (LFNFHF));
            NF = Math.round(NF * 100 / (LFNFHF));
            LF = Math.round(LF * 100 / (LFNFHF));
        }

        //below, in case if rounding leads to 99 or 101
        if(LF+NF+HF<100)
            HF=100-NF-LF;
        else if(LF+NF+HF > 100)
            HF=100-NF-LF;

        // fix threshold of what it is above/below median. With exponential distribution, mean sits at 0.63 of CDF
        double xLFl=0.001;
        double xLFh=0.25;
        double xNFl=0.25;
        double xNFh=0.75;
        double xHFl=0.75;
        double xHFh=0.999;

        //Convert level to volume based on storage curve
        double LNDVOL;
        if(LNDLev < 160){
            // TODO, if we use 3.84, there will be a lot of nah
            //LNDVOL= 0.0033*Math.pow((LNDLev-123), 3.8452);
            LNDVOL= 0.0033*Math.pow((LNDLev-123), 3);
        }
        else{
            LNDVOL=7.3588*Math.pow(LNDLev,2) - 2026.1*(LNDLev) + 139468;
        }

        double LNDmax= 6900;
        double Envflow=3;           //ML/day

        // calculation random vector
        LF = LF / 100;
        NF = NF / 100;
        HF = HF / 100;

        long NotWOrking1= Math.round(sim*LF);
        List<Double> x1 = new ArrayList<Double>();
        for(int i=0; i < NotWOrking1; i++){
            x1.add((new Random()).nextDouble());
        }

        long NotWOrking2= Math.round(sim*NF);
        List<Double> x2 = new ArrayList<Double>();
        for(int i=0; i < NotWOrking2; i++){
            x2.add((new Random()).nextDouble());
        }

        long NotWOrking3= Math.round(sim*HF);
        List<Double> x3 = new ArrayList<Double>();
        for(int i=0; i < NotWOrking3; i++){
            x3.add((new Random()).nextDouble());
        }

        // nornalising random numbers within the limits of LF, NF and HF
        for (int i = 0; i < NotWOrking1; i++) {
            x1.set(i, x1.get(i) * (xLFh - xLFl) + xLFl);
        }
        for (int i = 0; i < NotWOrking2; i++) {
            x2.set(i, x2.get(i) * (xNFh - xNFl) + xNFl);
        }
        for (int i = 0; i < NotWOrking3; i++) {
            x3.set(i, x3.get(i) * (xHFh - xHFl) + xHFl);
        }

        double[] X = new double[(int)(sim*LF+sim*NF+sim*HF)];

        for(int i = 0; i < NotWOrking1; i++){
            X[i] = x1.get(i);
        }
        for(int i = 0; i < NotWOrking2; i++){
            X[i + (int)(sim*LF)] = x2.get(i);
        }
        for(int i = 0; i < NotWOrking3; i++){
            X[i + (int)(sim*LF) + (int)(sim*NF)] = x3.get(i);
        }


        double[][] XfinalInflow = new double[(int)(sim*LF+sim*NF+sim*HF)][(int)weeksahead];
        List<Integer> randperm = new ArrayList<>();
        for(int i = 0; i < sim*LF+sim*NF+sim*HF; i++){
            randperm.add(i);
        }

        for(int i=0; i<sim*LF+sim*NF+sim*HF; i++){
            for(int j=0; j<weeksahead; j++){
                XfinalInflow[i][j] = 1000;
            }
        }
        for(int j = 0; j < weeksahead; j++){
            Collections.shuffle(randperm);
            for(int i=0; i < randperm.size(); i++){
                XfinalInflow[i][j] = X[randperm.get(i)];
            }
        }

        //Calculation random Inflow vectors through quantile (inverse CDF) of
        //exponential distribution
        double[][] Inflows = new double[(int)(sim*LF+sim*NF+sim*HF)][(int) weeksahead];
        for(int i=0; i<sim*LF+sim*NF+sim*HF; i++){
            for(int j=0; j<weeksahead; j++){
                Inflows[i][j] = 0;
            }
        }
        for(int i = 0; i < sim*LF+sim*NF+sim*HF; i++){
            for(int j = 0; j < weeksahead; j++){
                Inflows[i][j] = (0 - meanfl) * Math.log(1 - XfinalInflow[i][j]);
            }
        }

        // calculate deltaVOL. From historical data, Total VOL=
        // 0.00006*Inflow.^2-0.1113*x+185.69+err   then take off outflows
        double[][] FlowingVOL = new double[(int)(sim*LF+sim*NF+sim*HF)][(int)weeksahead];
        double[][] LNDvol = new double[(int)(sim*LF+sim*NF+sim*HF)][(int)weeksahead];
        for(int i=0; i<sim*LF+sim*NF+sim*HF; i++){
            for(int j=0; j<weeksahead; j++){
                FlowingVOL[i][j] = 0;
                LNDvol[i][j] = 0;
            }
        }

        //parameters of the logistic distribution of error COMB2000
        double mu=-79.3988;
        double s=589.0406;
        for(int i=0; i<sim*LF+sim*NF+sim*HF; i++){
            for(int j=0; j<weeksahead; j++){
                double q = (new Random()).nextDouble() * (0.70-0.30)+0.30;
                double err = mu + s*Math.log(q/(1-q));
                // TODO, not understand here in the matlab Inflows > 2000
                if(Inflows[i][j]>2000){
                    FlowingVOL[i][j]=0.00006*(Math.pow(Inflows[i][j],2))-0.1113*Inflows[i][j]+185.69+err;
                }
                else{
                    FlowingVOL[i][j]=Math.exp(0.4494*Math.log(Inflows[i][j])+2.4128)+err;
                }
            }
        }

        double ENVflow=Envflow*7;

        double[][] Spill=new double[(int)(sim*LF+sim*NF+sim*HF)][(int)weeksahead];
        for(int i=0; i<sim*LF+sim*NF+sim*HF; i++){
            for(int j=0; j<weeksahead; j++){
                Spill[i][j] = 0;
            }
        }
        if(WTPIntake>61.2)
            WTPIntake=61.2;

        double[][] WTPmax=new double[(int)(sim*LF+sim*NF+sim*HF)][(int)weeksahead + 1];
        for(int i=0; i<sim*LF+sim*NF+sim*HF; i++){
            for(int j=0; j<weeksahead; j++){
                WTPmax[i][j] = 1 * 7 * WTPIntake;
            }
        }



        // Adjust the maximum draw off flows in order to maintain a minimum residual
        // head of 3 metres at the WTP Inlet Structure and 10 metres at the most
        // disadvantaged air valve based on Geoff Hamilton hydraulic calculations
        if (LNDLev > 162.03) {
            if (WTPIntake > 61.2)
                WTPIntake = 61.2;
        } else if (LNDLev > 156.06) {
            if (WTPIntake > 50.4)
                WTPIntake = 50.4;
        } else if (LNDLev > 149.8) {
            if (WTPIntake > 43.2)
                WTPIntake = 43.2;
        } else if (LNDLev > 143.56) {
            if (WTPIntake > 28.8)
                WTPIntake = 28.8;
        } else if (LNDLev > 137.16) {
            if (WTPIntake > 21.6)
                WTPIntake = 21.6;
        } else {
            WTPIntake = 0;
        }

        // for the first week the maximum draw off flow is based on what calculated
        // above
        double MudgeeWTP=WTPIntake*7;

        for(int i = 0; i < sim*LF+sim*NF+sim*HF; i++){
            WTPmax[i][0] = MudgeeWTP;
        }

        for (int i = 0; i < sim * LF + sim * NF + sim * HF; i++) {
            LNDvol[i][0] = LNDVOL + FlowingVOL[i][0] - ENVflow - WTPmax[i][0];
            if (LNDvol[i][0] > LNDmax) {
                Spill[i][0] = LNDvol[i][0] - LNDmax;
                LNDvol[i][0] = LNDmax;
            }
            if (LNDvol[i][0] < 0)
                LNDvol[i][0] = 0;
            if (LNDvol[i][0] > 4205) {
                // from here we estimate the maximum draw
                // off flow for second week based on the
                // gate used, according to G. Hamilton's
                // calculation
                if (WTPmax[i][1] >= 61.2 * 7) {
                    WTPmax[i][1] = 61.2 * 7;
                }
            } else if (LNDvol[i][0] > 2359 && LNDvol[i][0] <= 4205) {
                if (WTPmax[i][1] >= 50.4 * 7) {
                    WTPmax[i][1] = 50.4 * 7;
                }
            } else if (LNDvol[i][0] > 1182 && LNDvol[i][0] <= 2359) {
                if (WTPmax[i][1] >= 43.2 * 7) {
                    WTPmax[i][1] = 43.2 * 7;
                }
            } else if (LNDvol[i][0] > 465 && LNDvol[i][0] <= 1182) {
                if (WTPmax[i][1] >= 28.8 * 7) {
                    WTPmax[i][1] = 28.8 * 7;
                }
            } else if (LNDvol[i][0] > 110 && LNDvol[i][0] <= 465) {
                if (WTPmax[i][1] >= 21.6 * 7) {
                    WTPmax[i][1] = 21.6 * 7;
                }
            } else if (LNDvol[i][0] < 110) {
                WTPmax[i][1] = 0;
            }
        }
        for(int j = 1; j < weeksahead; j++){
            for (int i = 0; i < sim * LF + sim * NF + sim * HF; i++) {
                LNDvol[i][j] = LNDvol[i][j-1] + FlowingVOL[i][j]-ENVflow-WTPmax[i][j];
                if(LNDvol[i][j]>LNDmax){
                    Spill[i][j]=LNDvol[i][j]-LNDmax;
                    LNDvol[i][j]=LNDmax;
                }
                if(LNDvol[i][j]<0){
                    LNDvol[i][j]=0;
                }
                if(LNDvol[i][j]>4205){
                    //from here we estimate the maximum draw off flow for next week based on the gate used, according to G. Hamilton's calculation
                    if(WTPmax[i][j+1]>=61.2*7){
                        WTPmax[i][j+1]=(61.2*7);
                    }
                }
                else if (LNDvol[i][j]>2359 && LNDvol[i][1]<= 4205){
                    if(WTPmax[i][j+1]>=50.4*7){
                        WTPmax[i][j+1]=50.4*7;
                    }
                }
                else if (LNDvol[i][j]> 1182 && LNDvol[i][1]<= 2359){
                    if(WTPmax[i][j+1]>=43.2*7){
                        WTPmax[i][j+1]=43.2*7;
                    }
                }
                else if (LNDvol[i][j]> 465 && LNDvol[i][1]<= 1182){
                    if(WTPmax[i][j+1]>=28.8*7){
                        WTPmax[i][j+1]=28.8*7;
                    }
                }
                else if (LNDvol[i][j]>110 && LNDvol[i][1]<= 465){
                    if(WTPmax[i][j+1]>=21.6*7){
                        WTPmax[i][j+1]=21.6*7;
                    }
                }
                else if (LNDvol[i][j]<110){
                    WTPmax[i][j+1]=0;
                }
            }
        }

        double[] SPILL = new double[(int) (sim * LF + sim * NF + sim * HF)];
        for (int i = 0; i < sim * LF + sim * NF + sim * HF; i++) {
            SPILL[i] = 0;
            for(int j = 1; j < weeksahead; j++){
                SPILL[i] += Spill[i][j];
            }
        }
        double[] avWTPmax = new double[(int) (sim * LF + sim * NF + sim * HF)];
        for (int i = 0; i < sim * LF + sim * NF + sim * HF; i++) {
            avWTPmax[i] = (WTPmax[i][0] + WTPmax[i][1] + WTPmax[i][2] + WTPmax[i][3] + WTPmax[i][4] + WTPmax[i][5]) / 6;
            avWTPmax[i] = avWTPmax[i]/7;
        }

        double avWTPmax_mean = 0;
        for (int i = 0; i < sim * LF + sim * NF + sim * HF; i++) {
            avWTPmax_mean += avWTPmax[i];
        }
        avWTPmax_mean = avWTPmax_mean / (sim * LF + sim * NF + sim * HF);

        //@output
        AverageWTP= Math.round(avWTPmax_mean);

        //Output charts calculation
        double[] full = new double[(int)sim];
        double[] medium = new double[(int)sim];
        double[] low = new double[(int)sim];
        LNDFIN = new double[(int)sim];
        //@output
        for(int i = 0; i < sim; i++){
            full[i] = 0;
            medium[i] = 0;
            low[i] = 0;
            LNDFIN[i] = 1;
        }

        for (int i = 0; i < sim; i++) {
            if (LNDvol[i][(int) weeksahead - 1] < 4000) {
                LNDFIN[i] = 123 + 4.5296 * Math.pow(LNDvol[i][(int) weeksahead - 1], 0.2569);
            } else {
                LNDFIN[i] = 123 + 0.0024 * LNDvol[i][(int) weeksahead - 1] + 28.856;
            }
        }
        for (int i = 0; i < sim; i++) {
            if (LNDvol[i][(int) weeksahead - 1] > 5764) {
                full[i]=1;
            }else if(LNDvol[i][(int) weeksahead - 1] > 2910){
                medium[i] = 1;
            } else{
                low[i] = 1;
            }
        }


        double Full = 0;
        double Medium = 0;
        double Low = 0;
        for (int i = 0; i < sim; i++) {
            Full += full[i];
            Medium += medium[i];
            Low += low[i];
        }
        Full = 100*Full/sim;
        Medium = 100*Medium/sim;
        Low = 100*Low/sim;
        if(Full<0.001)
            Full=0.001;
        if(Medium<0.001)
            Medium=0.001;
        if(Low<0.001)
            Low=0.001;

        //@output
        LND_Level[0] = Low;
        LND_Level[1] = Medium;
        LND_Level[2] = Full;


        double[] nospill = new double[(int)sim];
        double[] mediumspill = new double[(int)sim];
        double[] highspill = new double[(int)sim];
        for(int i = 0; i < sim; i++){
            nospill[i] = 0;
            mediumspill[i] = 0;
            highspill[i] = 0;
        }
        for(int i = 0; i < sim; i++){
            if(SPILL[i]>1000)
                highspill[i]=1;
            else if(SPILL[i] > 10)
                mediumspill[i]=1;
            else
                nospill[i]=1;
        }

        double NO_Spill = 0;
        double MEDIUM_Spill = 0;
        double HIGH_Spill = 0;
        for (int i = 0; i < sim; i++) {
            NO_Spill += nospill[i];
            MEDIUM_Spill += mediumspill[i];
            HIGH_Spill += highspill[i];
        }
        NO_Spill = 100*NO_Spill/sim;
        MEDIUM_Spill = 100*MEDIUM_Spill/sim;
        HIGH_Spill = 100*HIGH_Spill/sim;
        if(NO_Spill<0.001)
            NO_Spill=0.001;
        if(MEDIUM_Spill<0.001)
            MEDIUM_Spill=0.001;
        if(HIGH_Spill<0.001)
            HIGH_Spill=0.001;

        //@output
        SPILL_RISK[0] = NO_Spill;
        SPILL_RISK[1] = MEDIUM_Spill;
        SPILL_RISK[2] = HIGH_Spill;

    }// END For Execuyte_LND

}
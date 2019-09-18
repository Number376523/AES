import java.util.*;
import java.lang.*;



class AttackOnFiveRounds {
	
	public static final int NUMERO = 65536;
	public static final int COLONNA = 1;                      			 //colonna scelta per il sottospazio D
	public static final int[] INSIEME = new int[]{1,0,1,1};   			 //insieme I scelto per il sottospazio M
	
	public static int[][] serieIniziale = new int[NUMERO][16];
	public static int[][] serieCifrata = new int[NUMERO][16];
	public static int[] guessedKey = new int[4];
	
	public int[][] randomizza() {
		int i, j;
		int[][] temp = new int[4][4];
		for (i=0; i<4; i++) {
			for (j=0; j<4; j++) {
				temp[i][j] = ((int)(Math.random()*1321))%256;
			}
		}
		return temp;
	}
	
	public void creaSerie() {
		Aes aes = new Aes();
		int indice1, indice2, indice3, indice4, i, j, k;
		int[][] testoIniziale = new int[4][4];
		int[][] testoCifrato = new int[4][4];
		testoIniziale = randomizza();
		i=0;
		for (indice1=0; indice1<256; indice1++) {
			for (indice2=0; indice2<256; indice2++) {
				for (indice3=0; indice3<256; indice3++) {
					for (indice4=0; indice4<256; indice4++) {
						testoIniziale[0][(COLONNA+0)%4] = indice1;  
						testoIniziale[1][(COLONNA+1)%4] = indice2;
						testoIniziale[2][(COLONNA+2)%4] = indice3;
						testoIniziale[3][(COLONNA+3)%4] = indice4;
						for (j=0; j<4; j++) {
							for (k=0; k<4; k++) {
								serieIniziale[i][j*4+k] = testoIniziale[k][j];
							}
						}
						testoCifrato = testoIniziale.clone();
						testoCifrato = aes.addRoundKey(testoCifrato, 0);
						for (j=1; j<6; j++) {
							testoCifrato = aes.encryption(testoCifrato, j);
						}						
						for (j=0; j<4; j++) {
							for (k=0; k<4; k++) {
								serieCifrata[i][j*4+k] = testoCifrato[k][j];
							}
						}
						i++;
					}
				}
			}
		}	
	}
	
	public boolean controlloCollisioni() {
		Aes aes = new Aes();
		int i, j, k, l, m;
		boolean flag = false;
		int[][] temp0 = new int[4][4];
		int[][] temp1 = new int[4][4];
		int[][] temp2 = new int[4][4];
		for (i=0; i<NUMERO; i++) {
			for (j=i+1; j<NUMERO; j++) {
				for (k=0; k<4; k++) {
					for (l=0; l<4; l++) {
						temp0[l][k] = serieCifrata[i][k*4+l] ^ serieCifrata[j][k*4+l];
					}
				}
				if(controlloSpazio(temp0) == true) {
					for (k=0; k<4; k++) {
						for (l=0; l<4; l++) {
							temp1[l][k] = serieIniziale[i][k*4+l];
							temp2[l][k] = serieIniziale[j][k*4+l];
						}
					}
					flag = guessKeyFunction(temp1, temp2);
				}
				if (flag == true) {
					return flag;
				}
			}
		}
		return flag;
	}
						
	public boolean guessKeyFunction(int[][] testo1, int[][] testo2) {
		int i, j, indice1, indice2, indice3, indice4;
		boolean flag = false;
		Aes aes = new Aes();
		int[][] temp0 = new int[4][4];
		int[][] tempMatrix1 = new int[4][4];
		int[][] tempMatrix2 = new int[4][4];
		int[][] nuovaColonna1 = new int[4][4];
		int[][] nuovaColonna2 = new int[4][4];
		for (indice1=0; indice1<256; indice1++) {
			for (indice2=0; indice2<256; indice2++) {
				for (indice3=0; indice3<256; indice3++) {
					for (indice4=0; indice4<256; indice4++) {
						tempMatrix1 = testo1.clone();
						tempMatrix2 = testo2.clone();
						guessedKey[0] = indice1;
						guessedKey[1] = indice2;
						guessedKey[2] = indice3;
						guessedKey[3] = indice4;
						for (i=0; i<4; i++) {
							tempMatrix1[i][(COLONNA+i)%4] = tempMatrix1[i][(COLONNA+i)%4] ^ guessedKey[i];
							tempMatrix2[i][(COLONNA+i)%4] = tempMatrix2[i][(COLONNA+i)%4] ^ guessedKey[i];
						}
						tempMatrix1 = aes.encryption(tempMatrix1, 1);
						tempMatrix2 = aes.encryption(tempMatrix2, 1);
						if (flag == false) {
							nuovaColonna1 = tempMatrix1.clone();
							nuovaColonna2 = tempMatrix2.clone();
							nuovaColonna1[0][COLONNA] = tempMatrix2[0][COLONNA];
							nuovaColonna1[1][COLONNA] = tempMatrix1[1][COLONNA];
							nuovaColonna1[2][COLONNA] = tempMatrix1[2][COLONNA];
							nuovaColonna1[3][COLONNA] = tempMatrix1[3][COLONNA];
							nuovaColonna2[0][COLONNA] = tempMatrix1[0][COLONNA];
							nuovaColonna2[1][COLONNA] = tempMatrix2[1][COLONNA];
							nuovaColonna2[2][COLONNA] = tempMatrix2[2][COLONNA];
							nuovaColonna2[3][COLONNA] = tempMatrix2[3][COLONNA];
							for (i=2; i<6; i++) {
								nuovaColonna1 = aes.encryption(nuovaColonna1, i);
								nuovaColonna2 = aes.encryption(nuovaColonna2, i);
							}
							for (i=0; i<4; i++) {
								for (j=0; j<4; j++) {
									temp0[i][j] = nuovaColonna1[i][j] ^ nuovaColonna2[i][j];
								}
							}
							flag = controlloSpazio(temp0);
						}
						if (flag == true) {
							nuovaColonna1 = tempMatrix1.clone();
							nuovaColonna2 = tempMatrix2.clone();
							nuovaColonna1[0][COLONNA] = tempMatrix1[0][COLONNA];
							nuovaColonna1[1][COLONNA] = tempMatrix2[1][COLONNA];
							nuovaColonna1[2][COLONNA] = tempMatrix1[2][COLONNA];
							nuovaColonna1[3][COLONNA] = tempMatrix1[3][COLONNA];
							nuovaColonna2[0][COLONNA] = tempMatrix2[0][COLONNA];
							nuovaColonna2[1][COLONNA] = tempMatrix1[1][COLONNA];
							nuovaColonna2[2][COLONNA] = tempMatrix2[2][COLONNA];
							nuovaColonna2[3][COLONNA] = tempMatrix2[3][COLONNA];
							for (i=2; i<6; i++) {
								nuovaColonna1 = aes.encryption(nuovaColonna1, i);
								nuovaColonna2 = aes.encryption(nuovaColonna2, i);
							}
							for (i=0; i<4; i++) {
								for (j=0; j<4; j++) {
									temp0[i][j] = nuovaColonna1[i][j] ^ nuovaColonna2[i][j];
								}
							}
							flag = controlloSpazio(temp0);
						}
						if (flag == true) {
							nuovaColonna1 = tempMatrix1.clone();
							nuovaColonna2 = tempMatrix2.clone();
							nuovaColonna1[0][COLONNA] = tempMatrix1[0][COLONNA];
							nuovaColonna1[1][COLONNA] = tempMatrix1[1][COLONNA];
							nuovaColonna1[2][COLONNA] = tempMatrix2[2][COLONNA];
							nuovaColonna1[3][COLONNA] = tempMatrix1[3][COLONNA];
							nuovaColonna2[0][COLONNA] = tempMatrix2[0][COLONNA];
							nuovaColonna2[1][COLONNA] = tempMatrix2[1][COLONNA];
							nuovaColonna2[2][COLONNA] = tempMatrix1[2][COLONNA];
							nuovaColonna2[3][COLONNA] = tempMatrix2[3][COLONNA];
							for (i=2; i<6; i++) {
								nuovaColonna1 = aes.encryption(nuovaColonna1, i);
								nuovaColonna2 = aes.encryption(nuovaColonna2, i);
							}
							for (i=0; i<4; i++) {
								for (j=0; j<4; j++) {
									temp0[i][j] = nuovaColonna1[i][j] ^ nuovaColonna2[i][j];
								}
							}
							flag = controlloSpazio(temp0);
						}
						if (flag == true) {
							nuovaColonna1 = tempMatrix1.clone();
							nuovaColonna2 = tempMatrix2.clone();
							nuovaColonna1[0][COLONNA] = tempMatrix1[0][COLONNA];
							nuovaColonna1[1][COLONNA] = tempMatrix1[1][COLONNA];
							nuovaColonna1[2][COLONNA] = tempMatrix1[2][COLONNA];
							nuovaColonna1[3][COLONNA] = tempMatrix2[3][COLONNA];
							nuovaColonna2[0][COLONNA] = tempMatrix2[0][COLONNA];
							nuovaColonna2[1][COLONNA] = tempMatrix2[1][COLONNA];
							nuovaColonna2[2][COLONNA] = tempMatrix2[2][COLONNA];
							nuovaColonna2[3][COLONNA] = tempMatrix1[3][COLONNA];
							for (i=2; i<6; i++) {
								nuovaColonna1 = aes.encryption(nuovaColonna1, i);
								nuovaColonna2 = aes.encryption(nuovaColonna2, i);
							}
							for (i=0; i<4; i++) {
								for (j=0; j<4; j++) {
									temp0[i][j] = nuovaColonna1[i][j] ^ nuovaColonna2[i][j];
								}
							}
							flag = controlloSpazio(temp0);
						}
						if (flag == true) {
							nuovaColonna1 = tempMatrix1.clone();
							nuovaColonna2 = tempMatrix2.clone();
							nuovaColonna1[0][COLONNA] = tempMatrix1[0][COLONNA];
							nuovaColonna1[1][COLONNA] = tempMatrix1[1][COLONNA];
							nuovaColonna1[2][COLONNA] = tempMatrix2[2][COLONNA];
							nuovaColonna1[3][COLONNA] = tempMatrix2[3][COLONNA];
							nuovaColonna2[0][COLONNA] = tempMatrix2[0][COLONNA];
							nuovaColonna2[1][COLONNA] = tempMatrix2[1][COLONNA];
							nuovaColonna2[2][COLONNA] = tempMatrix1[2][COLONNA];
							nuovaColonna2[3][COLONNA] = tempMatrix1[3][COLONNA];
							for (i=2; i<6; i++) {
								nuovaColonna1 = aes.encryption(nuovaColonna1, i);
								nuovaColonna2 = aes.encryption(nuovaColonna2, i);
							}
							for (i=0; i<4; i++) {
								for (j=0; j<4; j++) {
									temp0[i][j] = nuovaColonna1[i][j] ^ nuovaColonna2[i][j];
								}
							}
							flag = controlloSpazio(temp0);
						}
						if (flag == true) {
							nuovaColonna1 = tempMatrix1.clone();
							nuovaColonna2 = tempMatrix2.clone();
							nuovaColonna1[0][COLONNA] = tempMatrix1[0][COLONNA];
							nuovaColonna1[1][COLONNA] = tempMatrix2[1][COLONNA];
							nuovaColonna1[2][COLONNA] = tempMatrix1[2][COLONNA];
							nuovaColonna1[3][COLONNA] = tempMatrix2[3][COLONNA];
							nuovaColonna2[0][COLONNA] = tempMatrix2[0][COLONNA];
							nuovaColonna2[1][COLONNA] = tempMatrix1[1][COLONNA];
							nuovaColonna2[2][COLONNA] = tempMatrix2[2][COLONNA];
							nuovaColonna2[3][COLONNA] = tempMatrix1[3][COLONNA];
							for (i=2; i<6; i++) {
								nuovaColonna1 = aes.encryption(nuovaColonna1, i);
								nuovaColonna2 = aes.encryption(nuovaColonna2, i);
							}
							for (i=0; i<4; i++) {
								for (j=0; j<4; j++) {
									temp0[i][j] = nuovaColonna1[i][j] ^ nuovaColonna2[i][j];
								}
							}
							flag = controlloSpazio(temp0);
						}
						if (flag == true) {
							nuovaColonna1 = tempMatrix1.clone();
							nuovaColonna2 = tempMatrix2.clone();
							nuovaColonna1[0][COLONNA] = tempMatrix1[0][COLONNA];
							nuovaColonna1[1][COLONNA] = tempMatrix2[1][COLONNA];
							nuovaColonna1[2][COLONNA] = tempMatrix2[2][COLONNA];
							nuovaColonna1[3][COLONNA] = tempMatrix1[3][COLONNA];
							nuovaColonna2[0][COLONNA] = tempMatrix2[0][COLONNA];
							nuovaColonna2[1][COLONNA] = tempMatrix1[1][COLONNA];
							nuovaColonna2[2][COLONNA] = tempMatrix1[2][COLONNA];
							nuovaColonna2[3][COLONNA] = tempMatrix2[3][COLONNA];
							for (i=2; i<6; i++) {
								nuovaColonna1 = aes.encryption(nuovaColonna1, i);
								nuovaColonna2 = aes.encryption(nuovaColonna2, i);
							}
							for (i=0; i<4; i++) {
								for (j=0; j<4; j++) {
									temp0[i][j] = nuovaColonna1[i][j] ^ nuovaColonna2[i][j];
								}
							}
							flag = controlloSpazio(temp0);
						}
						if (flag == true) {
							return flag;
						}
					}
				}
			}
		}
		return flag;
	}
	
	public boolean controlloSpazio(int[][] testo) {
		int[][] testoProva = testo.clone();
		Aes aes = new Aes();
		testoProva = aes.mixColumnsInverse(testoProva);
		if (INSIEME[0] == 0) {
			if ((testoProva[0][0] == 0) && (testoProva[1][3] == 0) && (testoProva[2][2] == 0) && (testoProva[3][1] == 0)) {
				return true;
			}
		} else if (INSIEME[1] == 0) {
			if ((testoProva[0][1] == 0) && (testoProva[1][0] == 0) && (testoProva[2][3] == 0) && (testoProva[3][2] == 0)) {
				return true;
			}
		} else if (INSIEME[2] == 0) {
			if ((testoProva[0][2] == 0) && (testoProva[1][1] == 0) && (testoProva[2][0] == 0) && (testoProva[3][3] == 0)) {
				return true;
			}
		} else if (INSIEME[3] == 0) {
			if ((testoProva[0][3] == 0) && (testoProva[1][2] == 0) && (testoProva[2][1] == 0) && (testoProva[3][0] == 0)) {
				return true;
			}
		}
		
	return false;
	}
		
	public static void main(String[] args) {
		boolean flag;
		AttackOnFiveRounds attack = new AttackOnFiveRounds();
		attack.creaSerie();
		flag = attack.controlloCollisioni();
		if(flag == true) {
			System.out.println(guessedKey[0]+" "+guessedKey[1]+" "+guessedKey[2]+" "+guessedKey[3]);
		} else {
			System.out.println("It didn't work...");
		}		
	}
}

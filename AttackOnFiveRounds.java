import java.util.*;
import java.lang.*;



class AttackOnFiveRounds {
	
	public static final int NUMERO = 65536;
	public static final int COLONNA = 1;                      			 //colonna scelta per il sottospazio D
	public static final int[] INSIEME = {1,0,1,1};   					 //insieme I scelto per il sottospazio M
	
	public static int[][][] serieIniziale = new int[NUMERO][NUMERO][16];
	public static int[][][] serieCifrata = new int[NUMERO][NUMERO][16];
	public static int[] guessedKey = new int[4];
	public static Aes aes = new Aes();
	
	public static int[][] randomizza() {
		int i, j;
		int[][] temp = new int[4][4];
		for (i=0; i<4; i++) {
			for (j=0; j<4; j++) {
				temp[i][j] = ((int)(Math.random()*1321))%256;
			}
		}
		return temp;
	}
	
	public static void creaSerie() {
		int indice1, indice2, indice3, indice4, i, j, k;
		int[][] testoIniziale = new int[4][4];
		int[][] testoCifrato = new int[4][4];
		testoIniziale = randomizza();
		i=0;
		for (indice1=0; indice1<256; indice1++) {
			for (indice2=0; indice2<256; indice2++) {
				for (indice3=0; indice3<256; indice3++) {
					for (indice4=0; indice4<256; indice4++) {
						testoIniziale[0][COLONNA] = indice1; 
						testoIniziale[1][(COLONNA+1)%4] = indice2;
						testoIniziale[2][(COLONNA+2)%4] = indice3;
						testoIniziale[3][(COLONNA+3)%4] = indice4;
						for (j=0; j<4; j++) {
							for (k=0; k<4; k++) {
								serieIniziale[i/NUMERO][i%NUMERO][j*4+k] = testoIniziale[k][j];
							}
						}
						testoCifrato = clona(testoIniziale);
						testoCifrato = aes.addRoundKey(testoCifrato, 0);
						for (j=1; j<6; j++) {
							testoCifrato = aes.encryption(testoCifrato, j);
						}						
						for (j=0; j<4; j++) {
							for (k=0; k<4; k++) {
								serieCifrata[i/NUMERO][i%NUMERO][j*4+k] = testoCifrato[k][j];
							}
						}
						i++;
					}
				}
			}
		}	
	}
	
	public static boolean controlloCollisioni() {
		int i, j, k, l, m, n;
		boolean flag = false;
		int[][] temp0 = new int[4][4];
		int[][] temp1 = new int[4][4];
		int[][] temp2 = new int[4][4];
		for (i=0; i<NUMERO; i++) {
			for (j=0; j<NUMERO; j++) {
				for (k=0; k<NUMERO; k++) {
					for (l=0; l<NUMERO; l++) {
						if (i!=k & j!=l) {
							for (m=0; m<4; m++) {
								for (n=0; n<4; n++) {
									temp0[n][m] = serieCifrata[i][j][m*4+n] ^ serieCifrata[k][l][m*4+n];
								}
							}
							if(controlloSpazio(temp0) == true) {
								for (m=0; m<4; m++) {
									for (n=0; n<4; n++) {
										temp1[n][m] = serieIniziale[i][j][m*4+n];
										temp2[n][m] = serieIniziale[k][l][m*4+n];
									}
								}
								flag = guessKeyFunction(temp1, temp2);
							}
							if (flag == true) {
								return flag;
							}
						}
					}
				}
			}
		}
		return flag;
	}
						
	public static boolean guessKeyFunction(int[][] testo1, int[][] testo2) {
		int i, j, indice1, indice2, indice3, indice4;
		boolean flag = false;
		int[][] temp0 = new int[4][4];
		int[][] tempMatrix1 = new int[4][4];
		int[][] tempMatrix2 = new int[4][4];
		int[][] nuovaColonna1 = new int[4][4];
		int[][] nuovaColonna2 = new int[4][4];
		for (indice1=0; indice1<256; indice1++) {
			for (indice2=0; indice2<256; indice2++) {
				for (indice3=0; indice3<256; indice3++) {
					for (indice4=0; indice4<256; indice4++) {
						tempMatrix1 = clona(testo1);
						tempMatrix2 = clona(testo2);
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
							nuovaColonna1 = clona(tempMatrix1);
							nuovaColonna2 = clona(tempMatrix2);
							nuovaColonna1[0][COLONNA] = tempMatrix2[0][COLONNA];
							nuovaColonna1[1][COLONNA] = tempMatrix1[1][COLONNA];
							nuovaColonna1[2][COLONNA] = tempMatrix1[2][COLONNA];
							nuovaColonna1[3][COLONNA] = tempMatrix1[3][COLONNA];
							nuovaColonna2[0][COLONNA] = tempMatrix1[0][COLONNA];
							nuovaColonna2[1][COLONNA] = tempMatrix2[1][COLONNA];
							nuovaColonna2[2][COLONNA] = tempMatrix2[2][COLONNA];
							nuovaColonna2[3][COLONNA] = tempMatrix2[3][COLONNA];
							nuovaColonna1 = aes.decryption(nuovaColonna1, 1);
							nuovaColonna2 = aes.decryption(nuovaColonna2, 1);
							for (i=0; i<4; i++) {
								nuovaColonna1[i][(COLONNA+i)%4] = nuovaColonna1[i][(COLONNA+i)%4] ^ guessedKey[i];
								nuovaColonna2[i][(COLONNA+i)%4] = nuovaColonna2[i][(COLONNA+i)%4] ^ guessedKey[i];
							}
							nuovaColonna1 = aes.addRoundKey(nuovaColonna1, 0);
							nuovaColonna2 = aes.addRoundKey(nuovaColonna2, 0);
							for (i=1; i<6; i++) {
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
							nuovaColonna1 = clona(tempMatrix1);
							nuovaColonna2 = clona(tempMatrix2);
							nuovaColonna1[0][COLONNA] = tempMatrix1[0][COLONNA];
							nuovaColonna1[1][COLONNA] = tempMatrix2[1][COLONNA];
							nuovaColonna1[2][COLONNA] = tempMatrix1[2][COLONNA];
							nuovaColonna1[3][COLONNA] = tempMatrix1[3][COLONNA];
							nuovaColonna2[0][COLONNA] = tempMatrix2[0][COLONNA];
							nuovaColonna2[1][COLONNA] = tempMatrix1[1][COLONNA];
							nuovaColonna2[2][COLONNA] = tempMatrix2[2][COLONNA];
							nuovaColonna2[3][COLONNA] = tempMatrix2[3][COLONNA];
							nuovaColonna1 = aes.decryption(nuovaColonna1, 1);
							nuovaColonna2 = aes.decryption(nuovaColonna2, 1);
							for (i=0; i<4; i++) {
								nuovaColonna1[i][(COLONNA+i)%4] = nuovaColonna1[i][(COLONNA+i)%4] ^ guessedKey[i];
								nuovaColonna2[i][(COLONNA+i)%4] = nuovaColonna2[i][(COLONNA+i)%4] ^ guessedKey[i];
							}
							nuovaColonna1 = aes.addRoundKey(nuovaColonna1, 0);
							nuovaColonna2 = aes.addRoundKey(nuovaColonna2, 0);
							for (i=1; i<6; i++) {
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
							nuovaColonna1 = clona(tempMatrix1);
							nuovaColonna2 = clona(tempMatrix2);
							nuovaColonna1[0][COLONNA] = tempMatrix1[0][COLONNA];
							nuovaColonna1[1][COLONNA] = tempMatrix1[1][COLONNA];
							nuovaColonna1[2][COLONNA] = tempMatrix2[2][COLONNA];
							nuovaColonna1[3][COLONNA] = tempMatrix1[3][COLONNA];
							nuovaColonna2[0][COLONNA] = tempMatrix2[0][COLONNA];
							nuovaColonna2[1][COLONNA] = tempMatrix2[1][COLONNA];
							nuovaColonna2[2][COLONNA] = tempMatrix1[2][COLONNA];
							nuovaColonna2[3][COLONNA] = tempMatrix2[3][COLONNA];
							nuovaColonna1 = aes.decryption(nuovaColonna1, 1);
							nuovaColonna2 = aes.decryption(nuovaColonna2, 1);
							for (i=0; i<4; i++) {
								nuovaColonna1[i][(COLONNA+i)%4] = nuovaColonna1[i][(COLONNA+i)%4] ^ guessedKey[i];
								nuovaColonna2[i][(COLONNA+i)%4] = nuovaColonna2[i][(COLONNA+i)%4] ^ guessedKey[i];
							}
							nuovaColonna1 = aes.addRoundKey(nuovaColonna1, 0);
							nuovaColonna2 = aes.addRoundKey(nuovaColonna2, 0);
							for (i=1; i<6; i++) {
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
							nuovaColonna1 = clona(tempMatrix1);
							nuovaColonna2 = clona(tempMatrix2);
							nuovaColonna1[0][COLONNA] = tempMatrix1[0][COLONNA];
							nuovaColonna1[1][COLONNA] = tempMatrix1[1][COLONNA];
							nuovaColonna1[2][COLONNA] = tempMatrix1[2][COLONNA];
							nuovaColonna1[3][COLONNA] = tempMatrix2[3][COLONNA];
							nuovaColonna2[0][COLONNA] = tempMatrix2[0][COLONNA];
							nuovaColonna2[1][COLONNA] = tempMatrix2[1][COLONNA];
							nuovaColonna2[2][COLONNA] = tempMatrix2[2][COLONNA];
							nuovaColonna2[3][COLONNA] = tempMatrix1[3][COLONNA];
							nuovaColonna1 = aes.decryption(nuovaColonna1, 1);
							nuovaColonna2 = aes.decryption(nuovaColonna2, 1);
							for (i=0; i<4; i++) {
								nuovaColonna1[i][(COLONNA+i)%4] = nuovaColonna1[i][(COLONNA+i)%4] ^ guessedKey[i];
								nuovaColonna2[i][(COLONNA+i)%4] = nuovaColonna2[i][(COLONNA+i)%4] ^ guessedKey[i];
							}
							nuovaColonna1 = aes.addRoundKey(nuovaColonna1, 0);
							nuovaColonna2 = aes.addRoundKey(nuovaColonna2, 0);
							for (i=1; i<6; i++) {
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
							nuovaColonna1 = clona(tempMatrix1);
							nuovaColonna2 = clona(tempMatrix2);
							nuovaColonna1[0][COLONNA] = tempMatrix1[0][COLONNA];
							nuovaColonna1[1][COLONNA] = tempMatrix1[1][COLONNA];
							nuovaColonna1[2][COLONNA] = tempMatrix2[2][COLONNA];
							nuovaColonna1[3][COLONNA] = tempMatrix2[3][COLONNA];
							nuovaColonna2[0][COLONNA] = tempMatrix2[0][COLONNA];
							nuovaColonna2[1][COLONNA] = tempMatrix2[1][COLONNA];
							nuovaColonna2[2][COLONNA] = tempMatrix1[2][COLONNA];
							nuovaColonna2[3][COLONNA] = tempMatrix1[3][COLONNA];
							nuovaColonna1 = aes.decryption(nuovaColonna1, 1);
							nuovaColonna2 = aes.decryption(nuovaColonna2, 1);
							for (i=0; i<4; i++) {
								nuovaColonna1[i][(COLONNA+i)%4] = nuovaColonna1[i][(COLONNA+i)%4] ^ guessedKey[i];
								nuovaColonna2[i][(COLONNA+i)%4] = nuovaColonna2[i][(COLONNA+i)%4] ^ guessedKey[i];
							}
							nuovaColonna1 = aes.addRoundKey(nuovaColonna1, 0);
							nuovaColonna2 = aes.addRoundKey(nuovaColonna2, 0);
							for (i=1; i<6; i++) {
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
							nuovaColonna1 = clona(tempMatrix1);
							nuovaColonna2 = clona(tempMatrix2);
							nuovaColonna1[0][COLONNA] = tempMatrix1[0][COLONNA];
							nuovaColonna1[1][COLONNA] = tempMatrix2[1][COLONNA];
							nuovaColonna1[2][COLONNA] = tempMatrix1[2][COLONNA];
							nuovaColonna1[3][COLONNA] = tempMatrix2[3][COLONNA];
							nuovaColonna2[0][COLONNA] = tempMatrix2[0][COLONNA];
							nuovaColonna2[1][COLONNA] = tempMatrix1[1][COLONNA];
							nuovaColonna2[2][COLONNA] = tempMatrix2[2][COLONNA];
							nuovaColonna2[3][COLONNA] = tempMatrix1[3][COLONNA];
							nuovaColonna1 = aes.decryption(nuovaColonna1, 1);
							nuovaColonna2 = aes.decryption(nuovaColonna2, 1);
							for (i=0; i<4; i++) {
								nuovaColonna1[i][(COLONNA+i)%4] = nuovaColonna1[i][(COLONNA+i)%4] ^ guessedKey[i];
								nuovaColonna2[i][(COLONNA+i)%4] = nuovaColonna2[i][(COLONNA+i)%4] ^ guessedKey[i];
							}
							nuovaColonna1 = aes.addRoundKey(nuovaColonna1, 0);
							nuovaColonna2 = aes.addRoundKey(nuovaColonna2, 0);
							for (i=1; i<6; i++) {
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
							nuovaColonna1 = clona(tempMatrix1);
							nuovaColonna2 = clona(tempMatrix2);
							nuovaColonna1[0][COLONNA] = tempMatrix1[0][COLONNA];
							nuovaColonna1[1][COLONNA] = tempMatrix2[1][COLONNA];
							nuovaColonna1[2][COLONNA] = tempMatrix2[2][COLONNA];
							nuovaColonna1[3][COLONNA] = tempMatrix1[3][COLONNA];
							nuovaColonna2[0][COLONNA] = tempMatrix2[0][COLONNA];
							nuovaColonna2[1][COLONNA] = tempMatrix1[1][COLONNA];
							nuovaColonna2[2][COLONNA] = tempMatrix1[2][COLONNA];
							nuovaColonna2[3][COLONNA] = tempMatrix2[3][COLONNA];
							nuovaColonna1 = aes.decryption(nuovaColonna1, 1);
							nuovaColonna2 = aes.decryption(nuovaColonna2, 1);
							for (i=0; i<4; i++) {
								nuovaColonna1[i][(COLONNA+i)%4] = nuovaColonna1[i][(COLONNA+i)%4] ^ guessedKey[i];
								nuovaColonna2[i][(COLONNA+i)%4] = nuovaColonna2[i][(COLONNA+i)%4] ^ guessedKey[i];
							}
							nuovaColonna1 = aes.addRoundKey(nuovaColonna1, 0);
							nuovaColonna2 = aes.addRoundKey(nuovaColonna2, 0);
							for (i=1; i<6; i++) {
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
	
	public static boolean controlloSpazio(int[][] testo) {
		int[][] testoProva = clona(testo);
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
	
	public static int[][] clona(int[][] state) {
		int i, j;
		int[][] temp = new int[4][4];
		for (i=0; i<4; i++) {
			for (j=0; j<4; j++) {
				temp[i][j] = state[i][j];
			}
		}
		return temp;
	}
		
	public static void main(String[] args) {
		boolean flag;
		creaSerie();
		flag = controlloCollisioni();
		if(flag == true) {
			System.out.println(guessedKey[0]+" "+guessedKey[1]+" "+guessedKey[2]+" "+guessedKey[3]);
		} else {
			System.out.println("It didn't work...");
		}		
	}
}

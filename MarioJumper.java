/**
*
* 
**************************************************************************
**************************************************************************
* Questa è una versione semplificata del gioco "super mario". A differenza
* del gioco originale il livello non scorre con il player ma resta fermo
* ed è il player a muoversi.
* I comandi vengono visualizzati nel tutorial
**************************************************************************
**************************************************************************
* versione 2.0
* @author Julian Cummaudo
* @author Nadia Fasani
* @version 05.06.2020
*/
import java.io.*;
public class MarioJumper{

//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	static int score = 0;
	static int bestScore = 0;

	public static void main(String[] args){
		clearScreen();
		printMenu();
		char[] skin = {'\u2687','\u263A','\u269B','\u2600','\u2618'};
		int option = 0;
		int skinOption = 0;
		while(option != 'g' && option != 'q' && option != 't'){
			option = 0;
			option = inputTast();
			wait(50);
			System.out.print("Digita uno dei due caratteri \r");
		}
		switch(option){
			case 'g':
				option = 0;
				clearScreen();
				printMenuN1(skin);
				while(option != '1' && option != '2' && option != '3' && option != '4' && option != '5'){
					option = 0;
					option = inputTast();
					wait(50);
					System.out.print("Digita uno dei caratteri \r");
				}//se l'utente non ha scritto niente ripete
				switch(option){
					case '1':
						skinOption = skinSet(1);
						break;
					case '2':
						skinOption = skinSet(2);
						break;
					case '3':
						skinOption = skinSet(3);
						break;
					case '4':
						skinOption = skinSet(4);
						break;
					case '5':
						skinOption = skinSet(5);
						break;
				}
				char[][] campo = new char[23][78];
				int times = 14;
				int[] coord = {3,2,0,0,0,3,0};
				/*array necessario per passare dei valori al metodo
					e per riceverli modificati.
					Primi due valori: coordinate iniziali del player
					Terzo valore: frame corrente
					Quarto valore: variabile isJumping
					Quinto valore: k
					Sesto valore: lives
					Settimo valore: isDying
				*/
				int fps = 40;
				int time = 1000 / fps;
				int frame = 1; //contatore di frame
				int k = 0; //contatore di caselle "saltate" in verticale

				int isJumping = 0;
				/*variabile int che ha la funzione di una variabile boolean
					(ci serviva int per poterla ricevere dal return di "move"
					 che ritorna solo un array int)
				*/ 

				fillThatMatrixDuhLevel1(campo);

				campo[coord[0]][coord[1]] = skin[skinOption];//carattere del player

				
				frame(campo); //cornice
				
				printFrames(campo, campo[coord[0]][coord[1]]);
				wait(time);
				clearScreen();

				do{
					coord = move(campo, coord[0], coord[1], frame, coord[3], coord[4], coord[5],coord[6]);
					
					printFrames(campo,campo[coord[0]][coord[1]]);
					for(int ik = 0; ik <= coord[5]; ik++){
						System.out.print("\u001b[31m"); //rosso
						System.out.print("\u2764 "); //carattere del cuore
						System.out.print("\u001B[0m"); //reset
					}
					System.out.print("	score: " + score + "	best score: " + bestScore);
					wait(time);
					clearScreen();
					frame++;

				}while(coord[5] >= -1);

				gameOver();
			
				break;
			// q serve per lasciare il gioco
			case 'q':
				clearScreen();
				System.out.print("\r \n");
				System.out.print("Alla prossima \u263a");
				System.out.print("\r \n");
				System.exit(0);
				break;
			// t per il tutorial
			case 't':
				option = 0;
				clearScreen();
				System.out.print(" d per avanzare in avanti di una casella" + "\r \n");
				System.out.print("a per avanzare indietro di una casella" + "\r \n");
				System.out.print("D per avanzare in avanti di due caselle" + "\r \n");
				System.out.print("A per avanzare indietro di due caselle" + "\r \n");
				System.out.print("'spazio' per saltare" + "\r \n");
				System.out.print("q per fermare il gioco in qualsiasi momento" + "\r \n");
				System.out.print("\r \n");
				System.out.print("l'obiettivo è di arrivare accanto al 'portale' infondo a sinistra");
				System.out.print("\r \n");
				System.out.print("\r \n");
				System.out.print("Premi qualsiasi tasto adesso per tornare al menu" + "\r \n");
				System.out.print("\r \n");
				try{
					while(true){
						if(System.in.available() != 0){
							main(new String[0]);
						}
						wait(100);
					}
				}catch(IOException e){
					System.out.print("ERRORE nell'Input");
					return;
				}
		}
	}

//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	public static int skinSet(int num){
		clearScreen();
		System.out.print("scelta " + num);
		wait(2000);
		return num-1;
	}
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	// stampa il campo di gioco
	public static void printFrames(char[][] campo, char scelta) {
		// stampa la matrice
		for (int k = 0; k < campo.length; k++) {
			for (int g = 0; g < campo[k].length; g++) {
				if(campo[k][g] == scelta){
					System.out.print("\u001b[33;1m"); //COLORE
					System.out.print(scelta);
					System.out.print("\u001B[0m");
				}else{
					System.out.print(campo[k][g]);
				}
			}
			System.out.print("\r \n");
		}
	}

//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	// gestisce il programma in base agli input della tastiera dell'utente
	public static int[] move(char[][] campo, int i, int j, int frame, int isJumping, int k, int lives, int isDying){
		int b = 0;
		boolean isFalling = false;
		
		if(isJumping == 0){
			isFalling = campo[i+1][j] == ' ';
			if(isFalling && k == 0 && (frame % 10) == 0){
				swapMatrice(campo, i, j, i + 1, j);
				i++;
			}
		}else if(isJumping == 1){
			isFalling = false;
		}
		boolean hasJump = false;
		int direzione = inputTast();

		// gestisce la partita in base agli input dell'utente
		switch(direzione){
			// a serve per spostarsi verso sinistra
			case 'a':
				if(j > 2){
					if(campo[i][j-1] == '\u25b2'){
						lives -= 1;
					}else if(campo[i][j-1] == ' '){
						swapMatrice(campo, i, j, i, j-1);
						j--;
					}
				}
				break;
			// d serve per spostarsi verso destra
			case 'd':
				if(j < campo[0].length-2){
					if(campo[i][j+1] == '\u25b2'){
						lives -= 1;
					}else if(campo[i][j+1] == ' '){
						swapMatrice(campo, i, j, i, j+1);
						j++;
					}
				}
				break;
			// lo spazio serve per saltare
			case ' ':
				if(!isFalling && isJumping == 0 && campo[i-1][j] == ' '){
					isJumping = 1;
					k = 1;
					hasJump = true;
				}
				break;
			// A per spostarsi verso sinistra ma più velocemente
			case 'A': 
				if(j >= 4){
					if(campo[i][j-2] == '\u25b2' || campo[i][j-1] == '\u25b2'){
						lives -= 1;
					}else if(campo[i][j-2] == ' ' && campo[i][j-1] == ' '){
						swapMatrice(campo, i, j, i, j-2);
						j -= 2;
					}
				}
				break;
			// D per spostarsi verso destra ma più velocemente
			case 'D':
				if(j < campo[0].length-3){
					if(campo[i][j+2] == '\u25b2' || campo[i][j+1] == '\u25b2'){
						lives -= 1;
					}else if(campo[i][j+2] == ' ' && campo[i][j+1] == ' '){
						swapMatrice(campo, i, j, i, j+2);
						j += 2;
					}
				}
				break;
			default:
				
				break;
			// q per abbandonare la partita
			case 'q':
				clearScreen();
				System.out.print("\r \n");
				System.out.print("Alla prossima \u263A");
				System.out.print("\r \n");
				System.exit(0);
				break;
		}
		//PORTALE
		if(campo[i+1][j] == '\u25ef' ||
			campo[i-1][j] == '\u25ef' ||
			campo[i][j+1] == '\u25ef' ||
			campo[i][j-1] == '\u25ef' ||
			campo[i+1][j] == '\u25ef'
			){
			score += 1;
			swapMatrice(campo, i, j, 2, 3);
			i = 2;
			j = 3;
			if(score > bestScore){
				bestScore = score;
			} 
		}
		//check morte
		if(campo[i+1][j] == '\u25b2' && isDying == 0){
			lives -= 1;
			isDying = 1;
		}else if(isDying == 1 && frame % 30 == 0){
			lives -= 1;
		}else if(campo[i+1][j] != '\u25b2' && isDying == 1){
			isDying = 0;
		}

		//condizioni per il salto
		if(k != 0 && !hasJump && (frame % 10) == 0){
			if(campo[i-1][j] == ' '){
				swapMatrice(campo, i, j, i-1, j);
				i--;
			}
				k++;
		}
		if(k == 4){
			k = 0;
			isJumping = 0;
		}
		
		if(isFalling){
			b = 1;
		}
		int c = isJumping;
		return new int[]{i,j,b,c,k,lives,isDying};
	}

//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	// Attende i millisecondi <ms> forniti come parametri
	public static void wait(int ms){
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e){
			return;
		}
	}

//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	// Esegue uno scambio tra i valori i, j ed i2, j2 nella matrice a
	public static void swapMatrice(char[][] campo, int i, int j, int i2, int j2){
		char carattere = campo[i][j];
		campo[i][j] = campo[i2][j2];
		campo[i2][j2] = carattere;
	}
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	// cattura l'input della tastiera
	public static int inputTast(){
		try{
			if(System.in.available() > 0){
				return System.in.read();
			}else{
				return 0;
			}
		}catch(IOException e){
			System.out.println("ERRORE NELLA TASTIERA");
			System.exit(0);
			return 0;
		}
	}
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	// va a capo per dare l'illusione di movimento
	public static void clearScreen(){
		System.out.print("\033[H\033[2J");
		System.out.flush();
	}
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	// riempie il campo di gioco con livelli e ostacoli (versione base)
	public static char[][] fillThatMatrixDuh(char[][] campo){
		for (int l = 1; l < campo.length; l++) {
			for (int m = 1; m < campo[l].length-1; m++) {
				campo[l][m] = ' ';
				if(l == campo.length - 2 ||  l == 11 || l == 16 || l == 6){
					campo[l][m] = '-';
				}
			}
		}

		campo[6][76] = ' ';
		campo[11][2] = ' ';
		campo[16][75] = ' ';
		campo[campo.length - 2][2] = ' ';

		return campo;
	}
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	// riempie il campo di gioco con livelli e ostacoli (livello 1)
	public static char[][] fillThatMatrixDuhLevel1(char[][] campo){

		for (int l = 1; l < campo.length; l++) {
			for (int m = 1; m < campo[l].length-1; m++) {
				campo[l][m] = ' ';
				if(l == campo.length - 4 ||  l == 11 || l == 16 || l == 6){
					campo[l][m] = '-';
				}
			}
		}
		campo[18][2] = '\u25ef';
		int n = 0;
		// spazi per cadere da un piano all'altro
		campo[6][76] = ' ';
		campo[11][2] = ' ';
		campo[16][76] = ' ';
		campo[campo.length - 2][2] = ' ';

		// PRIMO PIANO
		// ostacolo 1.1
		campo[5][5] = '|';
		campo[5][6] = '*';
		campo[5][7] = '*';
		campo[5][8] = '|';

		// ostacolo 1.2
		for(n = 19; n <= 22; n++){
			campo[4][n] = '-';
		}

		// piramidi 1.1
		campo[5][14] = '\u25b2';
		campo[5][15] = '\u25b2';

		// ostacolo 1.4
		campo[5][35] = '|';
		campo[4][35] = '|';
		campo[4][36] = '*';
		campo[4][37] = '*';
		campo[4][38] = '|';
		campo[5][38] = '|';

		// ostacolo 1.5
		for(int n1 = 3; n1 <= 5; n1 += 2){
			for(n = 45; n <= 50; n++){
				campo[n1][n] = '-';
			}
		}

		// piramidi 1.2
		campo[5][55] = '\u25b2';
		campo[5][56] = '\u25b2';

		// ostacolo 1.6
		campo[5][60] = '|';
		campo[5][61] = '*';
		campo[5][62] = '*';
		campo[5][63] = '|';

		// SECONDO PIANO
		// piramidi 2.1
		campo[10][75] = '\u25b2';
		campo[10][74] = '\u25b2';

		// ostacolo 2.1
		for(n = 66; n <= 71; n++){
			campo[10][n] = '-';
		}
		for(n = 64; n <= 67; n++){
			campo[8][n] = '-';
		}

		// ostacolo 2.2
		campo[10][50] = '|';
		campo[9][50] = '|';
		campo[9][49] = '*';
		campo[9][48] = '*';
		campo[9][47] = '*';
		campo[9][46] = '|';
		campo[10][46] = '|';

		// ostacolo 2.3
		for(n = 20; n <= 30; n++){
			if(n != 21 && n!=22 && n!=26 && n!=27){
				campo[9][n] = '-';
			}
		}

		// piramidi 2.2
		for(n = 25; n <= 28;n++){
			campo[10][n] = '\u25b2';
		}

		// piramidi 2.3
		for(n=20; n <= 23; n++){
			campo[10][n] = '\u25b2';
		}

		// TERZO PIANO
		// piramidi 3.1
		campo[15][5] = '\u25b2';
		campo[15][6] = '\u25b2';
		// ostacolo 3.1
		campo[15][19] = '|';
		campo[15][20] = '*';
		campo[15][21] = '*';
		campo[15][22] = '|';

		// ostacolo 3.2
		for(n=6;n<=9;n++){
			campo[14][n] = '-';
		}
		// piramidi 3.2
		campo[15][23] = '\u25b2';
		campo[15][24] = '\u25b2';

		// ostacolo 3.4
		campo[15][30] = '|';
		campo[14][30] = '|';
		campo[14][31] = '*';
		campo[14][32] = '*';
		campo[14][33] = '|';
		campo[15][33] = '|';

		// ostacolo 3.5
		for(n = 50; n<= 54; n++){
			campo[15][n] = '-';
		}
		for(n = 50; n<= 56; n++){
			campo[13][n] = '-';
		}

		// piramidi 3.3
		campo[15][55] = '\u25b2';
		campo[15][56] = '\u25b2';

		// ostacolo 3.6
		campo[15][65] = '|';
		campo[15][66] = '*';
		campo[15][67] = '*';
		campo[15][68] = '|';


		// QUARTO PIANO
		// piramidi 4.1
		for (int i = 2;i < 77; i++) {
			campo[21][i] = '\u25b2';
		}

		for (int i = 6;i < 10; i++) {
			campo[19][i] = ' ';
		}

		for (int i = 70;i <= 76; i++) {
			campo[19][i] = ' ';
		}

		for (int i = 57;i <= 63; i++) {
			campo[19][i] = '\u25b2';
		}
		for (int i = 57;i <= 63; i++) {
			campo[17][i] = ' ';
		}

		for (int i = 12;i < 16; i++) {
			campo[19][i] = ' ';
		}

		for (int i = 22;i < 36; i++) {
			campo[19][i] = ' ';
		}

		for (int i = 27; i < 32; i++) {
			campo[18][i] = '-';
		}

		return campo;
	}
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	// crea la cornice attorno al campo di gioco
	public static void frame(char[][] campo){
		for(int i = 0; i < campo.length; i++){
			for(int j = 0; j < campo[i].length; j++){
				if(i == 1 && j == 0){
					campo[i][j] = '\u250F';
				}else if(i == 1 && j == campo[i].length - 2){
					campo[i][j] = '\u2513';
				}else if(i == campo.length -1 && j == 0){
					campo[i][j] = '\u2517';	
				}else if(i == campo.length -1 && j == campo[i].length -2){
					campo[i][j] = '\u251b';
				}else if(j > 0 && j < campo[i].length -1 && (i == 1 || i == campo.length -1)){
					campo[i][j] = '\u2501';
				}else if(j == 1 || (j == campo[i].length-1)){
					campo[i][j] = '\u2503';
				}
			}
		}
	}
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	// stampa il menu
	public static void printMenu(){
	System.out.print("  /$$      /$$ /$$$$$$$$ /$$   /$$ /$$   /$$" + "\r \n");
	System.out.print("| $$$    /$$$| $$_____/| $$$ | $$| $$  | $$" + "\r \n");
	System.out.print("| $$$$  /$$$$| $$      | $$$$| $$| $$  | $$" + "\r \n");
	System.out.print("| $$ $$/$$ $$| $$$$$   | $$ $$ $$| $$  | $$" + "\r \n");
	System.out.print("| $$  $$$| $$| $$__/   | $$  $$$$| $$  | $$" + "\r \n");
	System.out.print("| $$\\  $ | $$| $$      | $$\\  $$$| $$  | $$" + "\r \n");
	System.out.print("| $$ \\/  | $$| $$$$$$$$| $$ \\  $$|  $$$$$$/" + "\r \n");
	System.out.print("|__/     |__/|________/|__/  \\__/ \\______/ " + "\r \n");
   
   	System.out.print("\r \n");
	System.out.print("\r \n");
	System.out.print("\r \n");
	System.out.print("0-----------------0" + "\r \n");
	System.out.print("| t per i comandi |" + "\r \n");
	System.out.print("|-----------------|" + "\r \n");
	System.out.print("|  g per giocare  |" + "\r \n");
	System.out.print("|-----------------|" + "\r \n");
	System.out.print("|  q per uscire   |" + "\r \n");
	System.out.print("0-----------------0" + "\r \n");
	}
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	// controlla se l'utente deve perdere una vita
	public static void gameOver(){
		clearScreen();
		System.out.print("\u001b[34m");
		System.out.print("\r"+"    _____              __  __   ______      ____   __      __  ______   _____    " + "\r \n");
		wait(100);
		System.out.print("\r"+"   / ____|     /\\     |  \\/  | |  ____|    / __ \\  \\ \\    / / |  ____| |  __ \\   " + "\r \n");
		wait(100);
		System.out.print("\r"+"  | |  __     /  \\    | \\  / | | |__      | |  | |  \\ \\  / /  | |__    | |__) |  " + "\r \n");
		wait(100);
		System.out.print("\r"+"  | | |_ |   / /\\ \\   | |\\/| | |  __|     | |  | |   \\ \\/ /   |  __|   |  _  /   " + "\r \n");
		wait(100);
		System.out.print("\r"+"  | |__| |  / ____ \\  | |  | | | |____    | |__| |    \\  /    | |____  | | \\ \\   " + "\r \n");
		wait(100);
		System.out.print("\r"+"   \\_____| /_/    \\_\\ |_|  |_| |______|    \\____/      \\/     |______| |_|  \\_\\  " + "\r \n");
		System.out.print("\r"+"                                                                                ");
		System.out.print("\r \n");
		wait(1000);
		System.out.print("\r" + " score: " + score);
		if(score > bestScore){
			bestScore = score;
		}
		wait(1000);
		System.out.print("\r \n");
		System.out.print("\r" + " high score: " + bestScore);

		wait(3000);
		System.out.print("\u001b[0m");
        score = 0;                                                              
		main(new String[0]);
	}
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	public static void printMenuN1(char[] skin){
		System.out.print("\r \n");
		System.out.print(skin[0] + "	" + skin[1] + "	" + skin[2] + "	" + skin[3] + "	" + skin[4]);
		System.out.print("\r \n");
		System.out.print("1" + "	" + "2" + "	" + "3" + "	" + "4" + "	" + "5");
		System.out.print("\r \n");
	}
}

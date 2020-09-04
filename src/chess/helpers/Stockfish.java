package chess.helpers;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

public class Stockfish {
	private Process engineProcess; 
	private BufferedReader processReader; 
	private OutputStreamWriter processWriter; 
	//	private static final String PATH = "C:\\Program Files\\stockfish-8-win\\Windows\\stockfish_8_x64"; 



	public boolean startEngine(String version) { 
		try { 
			String fileName = "";
			if(version.equalsIgnoreCase("x32"))
			{

				fileName = "stockfish_8_x32.exe";			  
			}
			else
			{
				fileName = "stockfish_8_x64.exe";		
			}
			URL url = this.getClass().getResource("../engine/" + fileName);
			//System.out.println("url is " + url.toString());
			String execPath= url.getPath() ;
			// System.out.println(execPath);

			engineProcess = Runtime.getRuntime().exec (execPath);
			processReader = new BufferedReader(new InputStreamReader( 
					engineProcess.getInputStream())); 
			processWriter = new OutputStreamWriter( 
					engineProcess.getOutputStream()); 

		} catch (Exception e) { 
			return false; 
		} 
		return true; 
	} 


	//to send any command
	public void sendCommand(String command) { 
		try { 
			processWriter.write(command + "\n"); 
			processWriter.flush(); 
		} catch (IOException e) { 
			e.printStackTrace(); 
		} 
	} 

	//to stop the engine
	public void stopEngine() { 
		try { 
			sendCommand("quit"); 
			processReader.close(); 
			processWriter.close(); 
		} catch (IOException e) { 
		} 
	} 

	//redrawing the board
	public String drawBoard() {  
		String fen="";
		try{
			sendCommand("d"); 
			String output=getOutput(20);
			if(output.contains("\n")){
				String[] rows = output.split("\n"); 

				for (int i = 1; i < rows.length; i++) { 
					//System.out.println(rows[i]); 
					if(rows[i].contains("Fen:")){
						fen=rows[i].split("Fen: ")[1];
					}
				} 
			}
		}
		catch (Exception ex)
		{
			System.out.println("Failed in getting FEN" +fen);  

		}
		return fen;

	} //END OF DRAWBOARD

	//SEnding the command isready to read from the engine
	public String getOutput(int waitTime) { 
		StringBuffer buffer = new StringBuffer(); 
		try { 
			Thread.sleep(waitTime); 
			sendCommand("isready"); 
			while (true) { 
				String text = processReader.readLine(); 
				if (text.equals("readyok")) 
					break; 
				else 
					buffer.append(text + "\n"); 
			} 
		} catch (Exception e) { 
			e.printStackTrace(); 
		} 
		return buffer.toString(); 
	} 


	//Find the best move by using the command go movetime
	public String getBestMove(int waitTime) { 
		String bestMove="";
		String alternateBestMove ="";
		sendCommand("go movetime " + waitTime); 
		String output=getOutput(20);
		try{
			if(output.contains("\n")){
				String[] rows = output.split("\n"); 

				for (int i = 1; i < rows.length; i++) { 
					//System.out.println(rows[i]); 
					String firstLine = rows[0];
					alternateBestMove = (firstLine.substring(firstLine.length()-5)).substring(0, 5);
					if(rows[i].contains("bestmove ")){
						bestMove=rows[i].split(" ")[1];
					}
				} 
			}
		}
		catch (Exception ex)
		{
			System.out.println("Failed in getting best move" +bestMove);  
		}

		if(bestMove.equalsIgnoreCase("") && alternateBestMove!=null)
			bestMove = alternateBestMove.trim();
		return bestMove;
	} //END OF GET BEST MOVE

	//Getting the move the engine thinks is my response (pondermove)
	public boolean getPonderMove(int waitTime) { 
		boolean ponderMove=false;
		sendCommand("go movetime " + waitTime); 
		String output=getOutput(20);
		try{
			if(output.contains("\n")){
				String[] rows = output.split("\n"); 

				for (int i = 1; i < rows.length; i++) { 
					System.out.println(rows[i]); 
					if(rows[i].contains("ponder ")){
						ponderMove=true;

					}
				} 
			}
		}
		catch (Exception ex)
		{
			System.out.println("Failed in getting pondermove" +ponderMove);  
		}
		return ponderMove;
	} 
}

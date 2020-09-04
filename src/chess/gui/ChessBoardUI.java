package chess.gui;

//imports
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

//my imports
import chess.helpers.Stockfish;


public class ChessBoardUI extends JFrame implements ActionListener  {	
	
	//Global Variables
	JButton squares[][]=new JButton[8][8];	
	JButton placeHolderBtn=null;
	static Stockfish engine=new Stockfish();
	static String currentFen;
	boolean engineToMove=false;
	StringBuffer moves = new StringBuffer();
	JTextArea ta = new JTextArea();
	boolean readySetGo = false;
	
	//Getting the images
	ImageIcon whiteKing=new ImageIcon(this.getClass().getResource("../images/wk.png"));
	ImageIcon blackKing=new ImageIcon(this.getClass().getResource("../images/bk.png"));
	ImageIcon whiteQueen=new ImageIcon(this.getClass().getResource("../images/wq.png"));
	ImageIcon blackQueen=new ImageIcon(this.getClass().getResource("../images/bq.png"));
	ImageIcon whiteRook=new ImageIcon(this.getClass().getResource("../images/wr.png"));
	ImageIcon blackRook=new ImageIcon(this.getClass().getResource("../images/br.png"));
	ImageIcon whiteBishop=new ImageIcon(this.getClass().getResource("../images/wb.png"));
	ImageIcon blackBishop=new ImageIcon(this.getClass().getResource("../images/bb.png"));
	ImageIcon whiteKnight=new ImageIcon(this.getClass().getResource("../images/wn.png"));
	ImageIcon blackKnight=new ImageIcon(this.getClass().getResource("../images/bn.png"));
	ImageIcon whitePawn=new ImageIcon(this.getClass().getResource("../images/wp.png"));
	ImageIcon blackPawn=new ImageIcon(this.getClass().getResource("../images/bp.png"));	

	
	public static void main(String[] args) {
		
		//Requesting for machine version
		String version = "x64";
		if (args.length > 0) {
		    try {
		        String arg1 = args[0];
		        if(arg1.equalsIgnoreCase("x64") || arg1.equalsIgnoreCase("x32"))
		        {
		        	version = arg1;
		        }
		    } catch (Exception e) {
		        //ignore : use the defined value on top
		    }
		}
		
		
		//Starting the Engine
		boolean engineStart = engine.startEngine(version);
		if(!engineStart)
		{
			System.err.println("Engine failed to start");
			return;
		}
		
		//set Stockfish  ready
		engine.sendCommand("uci");
		engine.sendCommand("setoption name Threads value 4");
		engine.sendCommand("setoption name Debug Log File value debug.txt");
		
		//starting the program
		new ChessBoardUI();			
	}
	
	public ChessBoardUI(){
		//Setting the Title of the Window
		super("ChessBoard");				
		setSize(1200,700);
		setResizable(true);
		
		//Panel which board and side panel are placed on
		JPanel mainPanel=new JPanel();
		mainPanel.setLayout(new BorderLayout());
		
		//Side panel for controls
		JPanel sidePanel = new JPanel();		
		GridBagLayout gbl_panel = new GridBagLayout();
	    sidePanel.setLayout(gbl_panel);
        sidePanel.setBackground(new Color (55, 221, 158));
        
        //Color-picker drop-down
        JLabel player1Label = new JLabel("Player 1 - Choose Color: ");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        sidePanel.add(player1Label,gbc);
        String[] choices = { "White","Black"};
        JComboBox comboBox = new JComboBox<String>(choices);
        gbc.gridx = 1;
        gbc.gridy = 0;
        sidePanel.add(comboBox, gbc);
        
        //Skill level drop-down
        JLabel skillLabel = new JLabel("Skill Level: ");
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 1;
        sidePanel.add(skillLabel,gbc);
        String[] skillChoices = { "Novice", "Intermediate", "Advanced", "Impossible"};
        JComboBox skillsComboBox = new JComboBox<String>(skillChoices);
        gbc.gridx = 1;
        gbc.gridy = 1;
        sidePanel.add(skillsComboBox, gbc);
        
        //start game button
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JButton startBtn = new JButton("Start Game");
        sidePanel.add(startBtn,gbc);
        
        //Action Listener for the Start Button
        startBtn.addActionListener(new ActionListener()
        {  //defining the action performed right here to avoid separating out in the actionperformed method
          public void actionPerformed(ActionEvent e)
          {	  //Now to make the Actual Chessboard
        	  JPanel chessBoardPanel;
        	  //Get the selected skill level and setting the engine's skill level accordingly
        	  String skillLvl = (String)skillsComboBox.getSelectedItem();
        	  if(skillLvl.equalsIgnoreCase("Novice"))
        	  {
        		  engine.sendCommand("setoption name Skill Level value 0");	 
        	  }
        	  else if(skillLvl.equalsIgnoreCase("Intermediate"))
        	  {
        		  engine.sendCommand("setoption name Skill Level value 5");	 
        	  }
        	  if(skillLvl.equalsIgnoreCase("Advanced"))
        	  {
        		  engine.sendCommand("setoption name Skill Level value 10");	 
        	  }
        	  if(skillLvl.equalsIgnoreCase("Impossible"))
        	  {
        		  engine.sendCommand("setoption name Skill Level value 20");	 
        	  }
        	  
        	  //Get the selected player color choice and setting the board accordingly
        	  String playerColor = (String)comboBox.getSelectedItem();
        	  if(playerColor.equalsIgnoreCase("White"))
        	  {
        		  chessBoardPanel= setupBoard("White");
        	  }
        	  else
        	  {
        		  chessBoardPanel=setupBoard("Black");
        	  }    	  
        	  
        	  //Adding the Chess Board to the main panel and actuallly starting the game
        	  mainPanel.add(chessBoardPanel, BorderLayout.WEST);
        	  currentFen=	engine.drawBoard();
        	  readySetGo = true;
        	  if(playerColor.equalsIgnoreCase("Black"))
        	  {
        		  engineToMove = true;
        	  }        	  
        	  startBtn.setEnabled(false);        	
        	  pack();
          }
        });//END OF START BUTTON ACTION LISTENER
        
        //Add The label for moves
        JLabel movesLabel = new JLabel("Game Moves ");
	    gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 4;
        sidePanel.add(movesLabel,gbc);
        
        //Add Text Area to Panel
        ta.setBackground(Color.LIGHT_GRAY);
	    ta.setLineWrap(true); 	 
	    ta.setEditable(false);   
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.gridheight = 4;
        sidePanel.add(new JScrollPane(ta) ,gbc);
           		 
		mainPanel.add(sidePanel, BorderLayout.CENTER);
		add(mainPanel);		
		setVisible(true);		
		startGame();
	}//END OF CHESSBOARDUI CONSTRUCTOR

	//Sets the board and coordinates based on player color
	public JPanel setupBoard(String color)
	{
		JPanel chessPanel = new JPanel();
		chessPanel.setLayout(new GridLayout(8,8));
		for (int j=0;j<8;j++){
			for(int i=0;i<8;i++){
				String letter;
				int number=0;
				
				if(color.equalsIgnoreCase("White")){
					//Getting the files of the chess Board i.e. a,b,c,etc.
					if(i==0){letter="a"; } 
					else if (i==1){letter="b";}
					else if (i==2){letter="c";}
					else if (i==3){letter="d";}
					else if (i==4){letter="e";}
					else if (i==5){letter="f";}
					else if (i==6){letter="g";}
					else {letter="h";}
					
					//Getting the ranks of the chess board i.e. 1,2,3,etc.
					if (j==7){number=1;}
					else if (j==6){number=2;}
					else if (j==5){number=3;}
					else if (j==4){number=4;}
					else if (j==3){number=5;}
					else if (j==2){number=6;}
					else if (j==1){number=7;}
					else{number=8;}
				}
				else
				{
					if(i==0){letter="h"; } 
					else if (i==1){letter="g";}
					else if (i==2){letter="f";}
					else if (i==3){letter="e";}
					else if (i==4){letter="d";}
					else if (i==5){letter="c";}
					else if (i==6){letter="b";}
					else {letter="a";}
					
					//Getting the ranks of the chess board i.e. 1,2,3,etc.
					if (j==7){number=8;}
					else if (j==6){number=7;}
					else if (j==5){number=6;}
					else if (j==4){number=5;}
					else if (j==3){number=4;}
					else if (j==2){number=3;}
					else if (j==1){number=2;}
					else{number=1;}
				}
			    //Creation of button and name
				JButton myButton=new JButton();
				myButton.setFont(new Font("Arial", Font.PLAIN, 1));
				String buttonName=""+letter+number;
					//System.out.println(""+i+","+j);
				myButton.setForeground(new Color (173, 87, 41));
				myButton.setText(""+i+","+j);
				myButton.putClientProperty("position", buttonName);
				
				//Placing Start Position White Pieces
				if(buttonName.equalsIgnoreCase("a1")||buttonName.equalsIgnoreCase("h1")){
					myButton.setIcon(whiteRook);
					myButton.setName("Rook");					
				}
				else if(buttonName.equalsIgnoreCase("b1")||buttonName.equalsIgnoreCase("g1")){
					myButton.setIcon(whiteKnight);
					myButton.setName("Knight");
				}
				else if(buttonName.equalsIgnoreCase("c1")||buttonName.equalsIgnoreCase("f1")){
					myButton.setIcon(whiteBishop);
					myButton.setName("Bishop");
				}
				else if(buttonName.equalsIgnoreCase("d1")){
					myButton.setIcon(whiteQueen);
					myButton.setName("Queen");
				}
				else if(buttonName.equalsIgnoreCase("e1")){
					myButton.setIcon(whiteKing);
					myButton.setName("King");
				}
				else if(number==2){
					myButton.setIcon(whitePawn);
					myButton.setName("Pawn");
				}
				//PLacing Start Position Black Pieces
				if(buttonName.equalsIgnoreCase("a8")||buttonName.equalsIgnoreCase("h8")){
					myButton.setIcon(blackRook);
					myButton.setName("Rook");
				}
				else if(buttonName.equalsIgnoreCase("b8")||buttonName.equalsIgnoreCase("g8")){
					myButton.setIcon(blackKnight);
					myButton.setName("Knight");
				}
				else if(buttonName.equalsIgnoreCase("c8")||buttonName.equalsIgnoreCase("f8")){
					myButton.setIcon(blackBishop);
					myButton.setName("Bishop");
				}
				else if(buttonName.equalsIgnoreCase("d8")){
					myButton.setIcon(blackQueen);
					myButton.setName("Queen");
				}
				else if(buttonName.equalsIgnoreCase("e8")){
					myButton.setIcon(blackKing);
					myButton.setName("King");
				}
				else if(number==7){
					myButton.setIcon(blackPawn);
					myButton.setName("Pawn");
				}
				
				//Chess Board buttons Listener
				myButton.addActionListener(this);
				
				//Placing the buttons on the squares
				squares[i][j]=myButton;
				//Square Color Determination
				if((i+j)%2!=0){
					squares[i][j].setBackground(new Color (173, 87, 41));
				}
				
				chessPanel.add(squares[i][j]);
			}
		}
		return chessPanel;
	}//END OF BOARD SETUP
	
	//Starting the game
	public void startGame()
	{	    
	    while(true){
			if(readySetGo && engineToMove){				    
			    ta.setText(moves.toString());			    
				computerMove();
				ta.setText(moves.toString());
				engineToMove=false;
			}
			else
			{
				 try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}
	    }
	}
	
	//ACTION PERFORMED_____ACTION PERFORMED_______ACTION PERFORMED_____ACTION PERFORMED
	
	public void actionPerformed(ActionEvent e) {
		//Checking where this action is coming from
		Object source=e.getSource(); 
		if (source instanceof JButton) {
		    JButton button = (JButton) source;
		    		    
		    //Putting the piece on the new square
		    if (placeHolderBtn!=null){
		    	//Getting the move Played and new Position
		    	String initialPosition=(String) placeHolderBtn.getClientProperty("position");
		    	String endPosition=(String) button.getClientProperty("position");
		    	String movePlayed=initialPosition+endPosition;
		    	engine.sendCommand ("position fen " +currentFen+" moves "+movePlayed);
		    	String newFen=engine.drawBoard();
		    		//System.out.println("Old fen is: "+currentFen);
		    		//System.out.println("u played: "+movePlayed);
		    		//System.out.println("New fen is: "+newFen);
		    	//Checking if the move is valid
		    	if(newFen.equalsIgnoreCase(currentFen)){
		    			//System.out.println("INVALID MOVE!!!!!!!!!");
		    			//JOptionPane.showMessageDialog(null, "Invalid Move");
		    		//Resetting the place holder button to null
		    		String oldCoordinates=placeHolderBtn.getText();
			    	String[] oldParts = oldCoordinates.split(",");
			    	String oldX=oldParts[0];
			    	String oldY=oldParts[1];
			    	int oldxCoordinate=Integer.parseInt(oldX);
			    	int oldyCoordinate=Integer.parseInt(oldY);
			    	if((oldxCoordinate+oldyCoordinate)%2!=0){
						squares[oldxCoordinate][oldyCoordinate].setBackground(new Color (173, 87, 41));
			    	}
			    	else{
						squares[oldxCoordinate][oldyCoordinate].setBackground(null);
		    		
			    	}
			    	placeHolderBtn=null;
		    	}//END OF INVALID MOVE/UNSELECTING A PIECE
		    	else
		    	{//VALID MOVE
		    		
		    	//Pasting the piece on the new square
		    	currentFen=newFen;
		    	moves.append(movePlayed + " ");
		    	String destination = button.getText();
		    	String[] parts = destination.split(",");
		    	String x=parts[0];
		    	String y=parts[1];
		    	int xCoordinate=Integer.parseInt(x);
		    	int yCoordinate=Integer.parseInt(y);
		    	System.out.println(xCoordinate);
		    	System.out.println(yCoordinate);
		    	squares[xCoordinate][yCoordinate].setIcon(placeHolderBtn.getIcon());
		    	squares[xCoordinate][yCoordinate].setName(placeHolderBtn.getName());
		    	
		    	//Removing the Piece from the old square
		    	String oldCoordinates=placeHolderBtn.getText();
		    	String[] oldParts = oldCoordinates.split(",");
		    	String oldX=oldParts[0];
		    	String oldY=oldParts[1];
		    	int oldxCoordinate=Integer.parseInt(oldX);
		    	int oldyCoordinate=Integer.parseInt(oldY);
		    	
		    	//R U Castling????????
				if(placeHolderBtn.getName().equalsIgnoreCase("King")&& Math.abs(oldxCoordinate-xCoordinate)==2){
					String newPosition = (String) button.getClientProperty("position");
				    if (newPosition.equalsIgnoreCase("g1")){
				    		
				    		JButton rook=squares[7][7];
				    		squares[5][7].setName(rook.getName());
				    		squares [5][7].setIcon(rook.getIcon());

				    		squares [7][7].setName(null);
				    		squares [7][7].setIcon(null);
				    }
				    else if (newPosition.equalsIgnoreCase("c1")){
			    		
			    		JButton rook=squares[0][7];
			    		squares[3][7].setName(rook.getName());
			    		squares [3][7].setIcon(rook.getIcon());

			    		squares [0][7].setName(null);
			    		squares [0][7].setIcon(null);
				    }    
				    else if (newPosition.equalsIgnoreCase("g8")){
			    		
			    		JButton rook=squares[7][0];
			    		squares[5][0].setName(rook.getName());
			    		squares [5][0].setIcon(rook.getIcon());

			    		squares [7][0].setName(null);
			    		squares [7][0].setIcon(null);
				    }
				    else if (newPosition.equalsIgnoreCase("c8")){
		    		
				    	JButton rook=squares[0][0];
				    	squares[3][0].setName(rook.getName());
				    	squares [3][0].setIcon(rook.getIcon());

				    	squares [0][0].setName(null);
				    	squares [0][0].setIcon(null);
				    }
				    
				}//END OF R U CASTLING????
				    
				//Removing the Piece from the Old Square (cont) and Resetting background Colors    
		    	squares[oldxCoordinate][oldyCoordinate].setIcon(null);
		    	squares[oldxCoordinate][oldyCoordinate].setName(null);
			    	if((oldxCoordinate+oldyCoordinate)%2!=0){
						squares[oldxCoordinate][oldyCoordinate].setBackground(new Color (173, 87, 41));
			    	}
			    	else{
						squares[oldxCoordinate][oldyCoordinate].setBackground(null);
		
			    	}
			   
			    	placeHolderBtn=null;
			    	engineToMove = true;
			    
		    		}//END OF VALID MOVE
		    	}//END OF DESTINATION SQUARE CLICK
		    	else
		    	{//SELECTING PIECE TO MOVE
		    		//Making sure there is a piece there
		    		if(button.getIcon()!=null )
				    {   button.setBackground(Color.CYAN); 
		    			placeHolderBtn=button;
				    	//System.out.println(placeHolderBtn.getName());
				    }	
		    		else{
		    			System.out.println("Free Square - Please select the piece");
		    		}
		    	}//END OF SELECTING THE PIECE TO MOVE
	    	}//END OF WHERE IS THE ACTION PERFORMED COMING FROM
	}//END OF ACTION PERFORMED
	
	
	public void computerMove (){

		while(true){
			//Getting the best move and opponent move from the engine
			String bestMove=engine.getBestMove(400);
			boolean ponderMove=engine.getPonderMove(400);
			
			//Have u Checkmated??
			if(bestMove.contains("none"))
			{
				JOptionPane.showMessageDialog(null, "Congrats! U have Checkmate Me!");
				engine.stopEngine();
				break;
			}

			//Making the Suggested Engine Move
			engine.sendCommand("position fen "+currentFen+" moves "+bestMove);
			String newFen=engine.drawBoard();


			if(newFen.equalsIgnoreCase(currentFen)){
				System.out.println("INVALID MOVE!!!!!!!!!");
				continue;
			}
			else{
				//Updating the position
				currentFen=newFen;
				moves.append(bestMove + " ");
				String startpos=bestMove.substring(0, 2);
				String endpos=bestMove.substring(2, 4);

				Icon picture=null;
				String piece="";
				int startX=0;
				int startY=0;
				int endX=0;
				int endY=0;
				boolean isKing=false;
				//Finding the Starting Position
				for(int i=0;i<8;i++){
					for (int j=0; j<8; j++){
						String position=(String) squares[i][j].getClientProperty("position");
						if(startpos.equalsIgnoreCase(position)){
							//Getting the position of the Moving Piece
							JButton srcButton=squares[i][j];
							picture=srcButton.getIcon();
							piece=srcButton.getName();
							if (piece.equalsIgnoreCase("King")){
								startX=i;
								startY=j;
								isKing=true;
							}
							squares[i][j].setIcon(null);
							squares[i][j].setName(null);
							break;
						}
					}
				}

				//Finding the ending Position
				for(int i=0;i<8;i++){
					for (int j=0; j<8; j++){
						String position=(String) squares[i][j].getClientProperty("position");
						if(endpos.equalsIgnoreCase(position)){
							//Getting the position of the Moving Piece
							squares[i][j].setIcon(picture);
							squares[i][j].setName(piece);
							endX=i;
							endY=j;
							break;
						}
					}
				}
				//IS THE ENGINE CASTLING????
				if (isKing==true&&Math.abs(startX-endX)==2){
					String newPosition = (String) squares[endX][endY].getClientProperty("position");
					if (newPosition.equalsIgnoreCase("g1")){

						JButton rook=squares[7][7];
						squares[5][7].setName(rook.getName());
						squares [5][7].setIcon(rook.getIcon());

						squares [7][7].setName(null);
						squares [7][7].setIcon(null);
					}
					else if (newPosition.equalsIgnoreCase("c1")){

						JButton rook=squares[0][7];
						squares[3][7].setName(rook.getName());
						squares [3][7].setIcon(rook.getIcon());

						squares [0][7].setName(null);
						squares [0][7].setIcon(null);
					}

					else if (newPosition.equalsIgnoreCase("g8")){

						JButton rook=squares[7][0];
						squares[5][0].setName(rook.getName());
						squares [5][0].setIcon(rook.getIcon());

						squares [7][0].setName(null);
						squares [7][0].setIcon(null);
					}
					else if (newPosition.equalsIgnoreCase("c8")){

						JButton rook=squares[0][0];
						squares[3][0].setName(rook.getName());
						squares [3][0].setIcon(rook.getIcon());

						squares [0][0].setName(null);
						squares [0][0].setIcon(null);
					}

				}//END OF IF CHECKING FOR CASTLING
			}
			if(ponderMove==false){
				ponderMove=engine.getPonderMove(400);
				if(ponderMove==false){
				JOptionPane.showMessageDialog(null, "HA HA HA Try Again After More Practice!");
				engine.stopEngine();
				}
			}
			break;
		}

	}//END OF COMPUTER MOVE 	
	
	}//END OF CLASS
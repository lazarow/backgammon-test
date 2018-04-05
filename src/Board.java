import java.util.ArrayList;

public class Board
{
	/**
	 * Reprezentuje stan planszy. Kolejne elementy tablicy oznaczają:
	 * indeks:		znaczenie:
	 * 0 - 23		plansza (pola kolejno od 1 do 24)
	 * 24			banda czarnych
	 * 25			banda czerwonych
	 * 26			dwór czarnych
	 * 27			dwór białych
	 * 
	 * Zakładam, że wartości dodatnie to piony czarne, ujemne to piony czerwone.
	 */
	private int[] board = new int[28];
	public Player black, red;
	
	public Board()
	{
		// ustawienie początkowe pionów
		board[0] = 2;
		board[5] = -5;
		board[7] = -3;
		board[11] = 5;
		board[12] = -5;
		board[16] = 3;
		board[18] = 5;
		board[23] = -2;
		// ustawienie obiektów graczy
		black = new BlackPlayer();
		red = new RedPlayer();
		black.opponent = red;
		red.opponent = black;
	}
	
	abstract protected class Player
	{
		protected int color 		= 0; 	// kolor gracza, 1 -> czarny, -1 -> czerwony
		protected int bar 			= 0; 	// banda (pozycja w `board`)
		protected int bearoff 		= 0; 	// dwór (pozycja w `board`)
		protected int homeBegin		= 0; 	// początek domu (pozycja w `board`)
		protected int homeEnd 		= 0; 	// koniec domu (pozycja w `board`)
		protected int bitChips1 	= 0; 	// binarna reperezentacja pozycji pionów gracza (co najmniej jeden)
		protected int bitChips2 	= 0; 	// binarna reperezentacja pozycji pionów gracza (co najmniej dwa piony na polu)
		protected Player opponent	= null;	// przeciwnik
		
		public Player()
		{
			// uzupełnienie binarki
			for (int i = 0; i < 24; ++i) {
				updateBitChips(i);
			}
		}
		
		private void updateBitChips(int position)
		{
			if (sign(board[position]) == color) {
				bitChips1 |= (1 << position);
				if (color * board[position] > 1) {
					bitChips2 |= (1 << position);
				} else {
					bitChips2 &= ~(1 << position);
				}
			} else {
				bitChips1 &= ~(1 << position);
				bitChips2 &= ~(1 << position);
			}
		}
		
		private void reEnterChecker(int position)
		{
			board[bar]--;
			board[position] += color;
			updateBitChips(position);
		}
		
		private void moveChecker(int from, int to)
		{
			// zbicie piona przeciwnika
			if (sign(board[to]) == -color) {
				board[opponent.bar]++;
				opponent.updateBitChips(to);
			}
			// normalny ruch
			board[from] -= color;
			board[to] += color;
			updateBitChips(from);
			updateBitChips(to);
		}
		
		private boolean areAllCheckersAtHome()
		{
			int pieces = 0;
			for (int i = homeBegin; i <= homeEnd; ++i)
				if (sign(board[i]) == color)
					pieces += board[i];
			return pieces == 15;
		}
		
		private int sign(int i)
		{
			if (i == 0) return 0;
		    if (i >> 31 != 0) return -1;
		    return 1;
		}
		
		
	}
	
	protected class BlackPlayer extends Player
	{
		protected int color = 1;
		protected int bar = 24;
		protected int bearoff = 26;
		protected int homeBegin = 18;
		protected int homeEnd = 23;
	}
	
	protected class RedPlayer extends Player
	{
		protected int color = -1;
		protected int bar = 25;
		protected int bearoff = 27;
		protected int homeBegin = 0;
		protected int homeEnd = 5;
	}
}

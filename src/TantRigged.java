import java.awt.Container;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class TantRigged extends JFrame {
	private static final long serialVersionUID = 1L;

	public Board board;
	
	private int waitStep = 0, waitSol = 1000;
	private boolean toFile = true;

	private final int[] leps = {-2,-1,1,2,1,-1}, lepo = {0,1,1,0,-1,-1};
	private final int[] lepszs = {0,0,0,2,1,-1}, lepszo = {0,0,0,0,-1,-1};
	private final int sts = 25, sto = 25;
	
	private int[][][] col;
	private int putCol;
	private int[] nextElem;
	private int left;
	private int writeCirc;
	private int[] curSeq;
	private ArrayList<int[]> solSeq;
	
	private int[][] elem;
	private int[] elCnt = {1,1,1,1,1,1,1,1,1,1};
	private int elemCnt;

	public TantRigged(PuzzleSet keszlet) {
		super("Tantrix");
		Container cp = getContentPane();
		board = new Board(50, 50, keszlet);
		cp.add(board);
		elem = keszlet.elem;
		col = new int[50][50][3];
		for(int i = 0; i < col.length; i++)
			for(int j = 0; j < col[i].length; j++)
			Arrays.fill(col[i][j], -1);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800,600);
		setLocationRelativeTo(this);
		setVisible(true);
	}
	
	private boolean align(int s, int o, int tip, int forg) {
		for(int i = 0; i < elem[0].length; i += 2) {
			int el = elem[tip][i];
			if(el == -1) continue;
			int ir = (el+forg)%6;
			int sz = col[s+lepszs[ir]][o+lepszo[ir]][ir%3];
			if(sz != -1 && sz != i/2+1)
				return false;
			ir = (elem[tip][i+1]+ir)%6;
			sz = col[s+lepszs[ir]][o+lepszo[ir]][ir%3];
			if(sz != -1 && sz != i/2+1)
				return false;
		}
		return true;
	}
	
	private void put(int s, int o, int tip, int forg) {
		board.uj(s, o, tip, forg);
		for(int i = 0; i < elem[0].length/2; i++) {
			int el = elem[tip][2*i];
			if(el == -1) continue;
			int ir = (el+forg)%6;
			col[s+lepszs[ir]][o+lepszo[ir]][ir%3] *= -i-1;
			ir = (elem[tip][2*i+1]+ir)%6;
			col[s+lepszs[ir]][o+lepszo[ir]][ir%3] *= -i-1;
		}
	}
	
	private void remove(int s, int o) {
		int tip = board.tab[s][o], forg = board.forg[s][o];
		board.torl(s, o);
		for(int i = 0; i < elem[0].length/2; i++) {
			int el = elem[tip][2*i];
			if(el == -1) continue;
			int ir = (el+forg)%6;
			col[s+lepszs[ir]][o+lepszo[ir]][ir%3] /= -i-1;
			ir = (elem[tip][2*i+1]+ir)%6;
			col[s+lepszs[ir]][o+lepszo[ir]][ir%3] /= -i-1;
		}
	}
	
	private boolean hasHole(int s, int o, int ir) {
		int i, el, ve, nov;
		for(i = 0; i < elemCnt; i++) {
			ve = board.tab[s][o];
			el = (elem[ve][2*putCol]+board.forg[s][o])%6;
			ve = (el+elem[ve][2*putCol+1])%6;
			nov = el==ir^writeCirc<0?5:1;
			ir = el==ir?ve:el;
			for(el = (el+nov)%6; el != ve; el = (el+nov)%6)
				if(board.tab[s+leps[el]][o+lepo[el]] < 0)
					return true;
			s += leps[ir]; o += lepo[ir];
			ir = (ir+3)%6;
		}
		return false;
	}
	
	public boolean newSol() {
		int i, j, k, nov, el, sgn, szekv[];
		for(i = 0; i < solSeq.size(); i++) {
			szekv = solSeq.get(i);
			for(nov = 1; nov < elemCnt; nov += elemCnt-2)
				for(sgn = -1; sgn <= 1; sgn += 2)
cikl:				for(j = 0; j < elemCnt; j++) {
						for(k = 0, el = j; k < elemCnt; k++, el=(el+nov)%elemCnt) {
							if(curSeq[k] != sgn*szekv[el] && (Math.abs(curSeq[k]) != 3 || Math.abs(szekv[el]) != 3))
								continue cikl;
						}
						return false;
					}
		}
		solSeq.add(curSeq);
		curSeq = curSeq.clone();
		return true;
	}

	public void recurse(int stelem, int kovs, int kovo, int kovforg) {
		if(board.tab[kovs][kovo] != -1) {
			if(stelem == elemCnt && !hasHole(sts-2, sto, 3) && newSol()) {
				board.surit();
				board.repaint();
				board.solCnt++;
				if(toFile) {
					BufferedImage bi = new BufferedImage(board.getWidth(), board.getHeight(), BufferedImage.TYPE_INT_RGB);
					board.paint(bi.getGraphics());
					File outf = new File(String.format("solution\\solution%02d-%02d.png", elemCnt, board.solCnt));
					try {
						ImageIO.write(bi, "png", outf);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if(waitSol > 0)
					try {
						Thread.sleep(waitSol);
					} catch(InterruptedException ex) {}
			} else
				return;
		}
		int okul = Math.abs(kovo-sto), skul = Math.abs(kovs-sts);
		skul -= okul;
		if(okul+((skul<0?0:skul)+1)/2 > left)
			return;
		int elelem = 0, i, j, forg;
		for(i = stelem; i < elemCnt; elelem = i, i = nextElem[i]) {
			for(j = 0; j < 2; j++) {
				int ir = elem[i][2*putCol], kany = elem[i][2*putCol+1];
				if(ir == -1) continue;
				if(j > 0) ir += kany;
				forg = (kovforg-ir+12)%6;
				ir = (kovforg+(1-2*j)*kany+6)%6;
				if(align(kovs, kovo, i, forg)) {
					put(kovs, kovo, i, forg);
					board.repaint();
					if(waitStep > 0)
						try {
							Thread.sleep(waitStep);
						} catch(InterruptedException ex) {}
					if(--elCnt[i] == 0)
						nextElem[elelem] = nextElem[i];
					curSeq[elemCnt-left] = (2*j-1)*kany;
					left--;
					writeCirc += (kany=(2*j-1)*(3-kany));
					recurse(elCnt[stelem]>0?stelem:nextElem[stelem], kovs+leps[ir], kovo+lepo[ir], (ir+3)%6);
					writeCirc -= kany;
					left++;
					if(elCnt[i]++ == 0)
						nextElem[elelem] = i;
					remove(kovs, kovo);
					board.repaint();
				}
			}
		}
	}
	
	public void solve(int elemCnt, int colour) {
		this.elemCnt = elemCnt;
		nextElem = new int[elemCnt];
		for(int i = 0; i < elemCnt; i++)
			nextElem[i] = i+1;
		this.putCol = colour;
		curSeq = new int[elemCnt];
		solSeq = new ArrayList<int[]>(16);
		left = elemCnt;
		put(sts, sto, 0, (6-elem[0][2*colour])%6);
		left--;
		writeCirc = 3-(curSeq[0]=elem[0][2*colour+1]);
		recurse(1, sts-2, sto, 3);
		remove(sts, sto);
	}
	
	public static void main(String args[]) {
		TantRigged tantrigged = new TantRigged(PuzzleSet.SET2);
		for(int j = 3; j <= 10; j++) {
			tantrigged.board.level = j;
			tantrigged.board.solCnt = 0;
			for(int i = 0; i < 3; i++)
				tantrigged.solve(j,i);
		}
	}
}

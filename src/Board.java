import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Arrays;

import javax.swing.JPanel;

public class Board extends JPanel {
	private static final long serialVersionUID = 1L;
	
	final int kerVas = 3, vonVas = 10, uresVas = 1;
	final int vizSzeg = 20, fugSzeg = 20, maxR = 60;
	final Color[] colors;
	final Color elemSzin = Color.black,//new Color(224,224,224),
			hatterSzin = Color.lightGray,//new Color(224,224,224),
			peremSzin = Color.gray,
			uresSzin = Color.gray;
	
	public int[][] elem, tab, forg;
	public int mins, maxs, mino, maxo;
	
	public int level, solCnt;

	private BasicStroke perStil, vonStil;//, uresStil;
	
	public Board(int s, int o, PuzzleSet keszlet) {
		tab = new int[s][o];
		for(int i = 0; i < s; i++)
			Arrays.fill(tab[i], -1);
		forg = new int[s][o];
		elem = keszlet.elem;
		colors = keszlet.colors;
		mins = s; maxs = -1; mino = o; maxo = -1;
		perStil = new BasicStroke(kerVas, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
		vonStil = new BasicStroke(vonVas, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
//		uresStil = new BasicStroke(uresVas, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
	}
	
	public void uj(int s, int o, int tip, int forg) {
		tab[s][o] = tip;
		if(s < mins) mins = s;
		if(s > maxs) maxs = s;
		if(o < mino) mino = o;
		if(o > maxo) maxo = o;
		this.forg[s][o] = forg;
	}
	
	public void torl(int s, int o) {
		tab[s][o] = -1;
	}
	
	public void surit() {
		int ss, oo, omins = mins, omaxs = maxs, omino = mino, omaxo = maxo;
		mins = tab.length; maxs = -1; mino = tab[0].length; maxo = -1;
		for(ss = omins; ss <= omaxs; ss++)
			for(oo = omino; oo <= omaxo; oo++)
				if(tab[ss][oo] > -1) {
					if(ss < mins) mins = ss;
					if(ss > maxs) maxs = ss;
					if(oo < mino) mino = oo;
					if(oo > maxo) maxo = oo;
				}
	}
	
	private int ii(double dd) {
		return (int)(dd+.5);
	}
	
//	private void uresRajz(Graphics g, double R, double ox, double oy) {		
//		double rp2 = R/2, gy3p2r = Math.sqrt(3)/2*R;
//		int x[] = {ii(ox+rp2),ii(ox+R),ii(ox+rp2),ii(ox-rp2),ii(ox-R),ii(ox-rp2)};
//		int y[] = {ii(oy-gy3p2r),ii(oy),ii(oy+gy3p2r),ii(oy+gy3p2r),ii(oy),ii(oy-gy3p2r)};
//		Graphics2D g2 = (Graphics2D)g;
//		g2.setStroke(uresStil);
//		g2.setColor(uresSzin);
//		g2.drawPolygon(x, y, 6);
//	}
	
	private void elemRajz(Graphics g, double R, int tip, int forg, double ox, double oy) {
		double rp2 = R/2, gy3p2r = Math.sqrt(3)/2*R;
		int x[] = {ii(ox+rp2),ii(ox+R),ii(ox+rp2),ii(ox-rp2),ii(ox-R),ii(ox-rp2)};
		int y[] = {ii(oy-gy3p2r),ii(oy),ii(oy+gy3p2r),ii(oy+gy3p2r),ii(oy),ii(oy-gy3p2r)};
		int i;
		Graphics2D g2 = (Graphics2D)g;
//		if(tip == 0) {
//			g2.setColor(Color.gray);
//		} else
		g2.setColor(elemSzin);
		g2.fillPolygon(x, y, 6);
		g2.setStroke(vonStil);
		for(i = 0; i < elem[0].length; i += 2) {
			g2.setColor(colors[i/2]);
			int el = elem[tip][i];
			if(el == -1) continue;
			int k = (el+forg)%6;
			switch(elem[tip][i+1]) {
				case 1:
					g2.drawArc(x[k]-ii(rp2), y[k]-ii(rp2), ii(R), ii(R), 180-k*60, 120);
					break;
				case 2:
					int kp1 = (k+1)%6;
					g2.drawArc(x[k]+x[kp1]-ii(ox+3*rp2), y[k]+y[kp1]-ii(oy+3*rp2), ii(3*R), ii(3*R), 180-k*60, 60);
					break;
				case 3:
					int km1 = (k+5)%6, kp2 = (k+2)%6, kp3 = (k+3)%6;
					g2.drawLine((x[km1]+x[k])/2, (y[km1]+y[k])/2, (x[kp2]+x[kp3])/2, (y[kp2]+y[kp3])/2);
			}
		}
		g2.setStroke(perStil);
		g2.setColor(peremSzin);
		g2.drawPolygon(x, y, 6);
	}
	
	public void paint(Graphics g) {
		int s = maxs-mins+1, o = maxo-mino+1;
		g.setColor(hatterSzin);
		g.fillRect(2, 2, getWidth()-5, getHeight()-5);
		double R = maxR, r, gy3p2 = Math.sqrt(3)/2;
		r = 2.0*(getWidth()-vizSzeg*2)/(3*o+1);
		if(r < R) R = r;
		r = (getHeight()-fugSzeg*2)/gy3p2/(s+1);
		if(r < R) R = r;
		gy3p2 *= R;
		double ox = (getWidth()-R*(3*o+1)/2)/2+R;
		double oy = (getHeight()-gy3p2*(s+1))/2+gy3p2;
		int ss, oo, tip;
//		for(ss = 0; ss < s; ss++)
//			for(oo = 1-ss%2; oo < o; oo += 2)
//				if((tip=tab[ss+mins][oo+mino]) <= -1)
//					uresRajz(g, R, ox+3.0/2*R*oo, oy+gy3p2*ss);
		for(ss = 0; ss < s; ss++)
			for(oo = 0; oo < o; oo++)
				if((tip=tab[ss+mins][oo+mino]) > -1)
					elemRajz(g, R, tip, forg[ss+mins][oo+mino], ox+3.0/2*R*oo, oy+gy3p2*ss);
		g.setColor(Color.black);
		g.drawString(level+". szint:  "+solCnt+". megoldás", 20, 20);
	}
}

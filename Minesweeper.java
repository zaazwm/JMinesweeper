import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashSet;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/*
The MIT License (MIT)

Copyright (c) 2016 Weimeng Zhu

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/

public class Minesweeper extends JPanel {
	
	private static final long serialVersionUID = -4137736904055280716L;
	
	private JButton[] mineButtons;
	
	private HashSet<Integer> mineSet;
	
	private Color defaultColor;
	
	private Random rnd;
	
	private Dimension preferredSize = new Dimension(50,50); 
	
	private int sizeX;
	private int sizeY;
	
	public Minesweeper() {
		this.setLayout(new GridBagLayout());
		
		mineSet = new HashSet<Integer>();
		rnd = new Random();
		sizeX=0;
		sizeY=0;
		
		this.initialize(10, 10, 10);
	}
	
	private void initialize(int x, int y, int mineNum) {
		this.removeAll();
		mineSet.clear();
		sizeX=x;
		sizeY=y;
		mineNum=Math.min(mineNum, x*y);
		while(mineSet.size()<mineNum) {
			mineSet.add(rnd.nextInt(x*y));
		}
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0,0,0,0);
		c.weightx = 0.05;
		c.weighty = 0.05;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth=1;
		c.gridheight=1;
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int swidth = (int)screenSize.getWidth();
		int sheight = (int)screenSize.getHeight()-100;
		
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		swidth = gd.getDisplayMode().getWidth();
		sheight = gd.getDisplayMode().getHeight()-100;
		
		mineButtons = new JButton[x*y];
//		int unitx = this.getWidth()/x;
//		int unity = this.getHeight()/y;
		int unitx = swidth/x;
		int unity = sheight/y;
		Dimension dim = new Dimension(Math.min(preferredSize.width, Math.min(unitx, unity)),Math.min(preferredSize.height,Math.min(unitx, unity)));
		for(int iy=0;iy<y;iy++) {
			for(int jx=0;jx<x;jx++) {
				mineButtons[iy*x+jx]=new JButton(" ");
				mineButtons[iy*x+jx].setOpaque(true);
				mineButtons[iy*x+jx].setPreferredSize(dim);
				mineButtons[iy*x+jx].setFocusable(false);
				mineButtons[iy*x+jx].setFont(mineButtons[iy*x+jx].getFont().deriveFont(Font.BOLD));
				c.gridx = jx;
				c.gridy = iy;
				mineButtons[iy*x+jx].addActionListener(new MineActionListener(jx,iy));
				mineButtons[iy*x+jx].addMouseListener(new MineMouseListener(jx,iy));
				this.add(mineButtons[iy*x+jx], c);
				defaultColor = mineButtons[iy*x+jx].getBackground();
			}
		}
		
		revalidate();
		JFrame frame = (JFrame)SwingUtilities.getWindowAncestor(this);
		if(frame!=null) {
			frame.pack();
			frame.setLocationRelativeTo(null);
		}
	}
	
	private void refresh() {
		initialize(sizeX, sizeY, mineSet.size());
	}
	
	private void checkWin() {
		int found = 0;
		int untouched = 0;
		for(int y=0;y<sizeY;y++) {
			for(int x=0;x<sizeX;x++) {
				if(mineButtons[y*sizeX+x].getBackground().equals(Color.RED) && mineSet.contains(y*sizeX+x))
					found++;
				else if(mineButtons[y*sizeX+x].getBackground().equals(defaultColor) && mineButtons[y*sizeX+x].getText().equals(" "))
					untouched++;
			}
		}
		if(found+untouched==mineSet.size()) {
			JOptionPane.showMessageDialog(Minesweeper.this, "Succeed!");
			refresh();
		}

	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		JFrame frame = new JFrame("Minesweeper");
		final Minesweeper mw = new Minesweeper();
		
		frame.setLayout(new BorderLayout());
		
		frame.add(mw, BorderLayout.CENTER);
		
		JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
		JLabel rowLabel = new JLabel("Row:");
		final JTextField rowField = new JTextField("10",5);
		JLabel colLabel = new JLabel("Column:");
		final JTextField colField = new JTextField("10",5);
		JLabel mineLabel = new JLabel("Mines:");
		final JTextField mineField = new JTextField("10",5);
		
		final JButton startButton = new JButton("(Re)Start");
		
		ActionListener restartActionListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				mw.initialize(Integer.parseInt(rowField.getText()), Integer.parseInt(colField.getText()), Integer.parseInt(mineField.getText()));
			}
			
		};
		
		startButton.addActionListener(restartActionListener);
		rowField.addActionListener(restartActionListener);
		colField.addActionListener(restartActionListener);
		mineField.addActionListener(restartActionListener);
		
		
		JLabel cprtLabel = new JLabel("by W.Zhu");
		
		controlPanel.add(rowLabel);
		controlPanel.add(rowField);
		controlPanel.add(colLabel);
		controlPanel.add(colField);
		controlPanel.add(mineLabel);
		controlPanel.add(mineField);
		controlPanel.add(startButton);
		controlPanel.add(cprtLabel);
		
		frame.add(controlPanel, BorderLayout.SOUTH);
		
		frame.setSize(530, 600);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		
		frame.setVisible(true);
	}
	
	private class MineActionListener implements ActionListener {
		
		public int x;
		public int y;
		
		public MineActionListener(int x, int y) {
			this.x=x;
			this.y=y;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if(x<0 || x>=sizeX || y<0 || y>=sizeY)
				return;
			
			if(!mineButtons[y*sizeX+x].getText().equals(" ")) {
				for(int ny = y-1;ny<=y+1;ny++) {
					for(int nx=x-1;nx<=x+1;nx++) {
						clickNear(nx,ny);
					}
				}
			}
			
			if(mineButtons[y*sizeX+x].getBackground().equals(Color.RED))
				return;
			
			if(mineSet.contains(y*sizeX+x)) {
				for(int m : mineSet)
					mineButtons[m].setBackground(Color.RED);
				JOptionPane.showMessageDialog(Minesweeper.this, "Boom!");
				refresh();
			}
			else {
				dfs(x,y);
			}
			
			checkWin();
		}
		
		private void clickNear(int x, int y) {
			if(x<0 || x>=sizeX || y<0 || y>=sizeY)
				return;
			
			if(mineButtons[y*sizeX+x].getBackground().equals(Color.RED))
				return;
			
			if(mineSet.contains(y*sizeX+x)) {
				for(int m : mineSet)
					mineButtons[m].setBackground(Color.RED);
				JOptionPane.showMessageDialog(Minesweeper.this, "Boom!");
				refresh();
			}
			else {
				dfs(x,y);
			}
		}
	}
	
	private class MineMouseListener implements MouseListener {
		public int x;
		public int y;
		
		public MineMouseListener(int x, int y) {
			this.x=x;
			this.y=y;
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if(SwingUtilities.isRightMouseButton(e)) {
				if(mineButtons[y*sizeX+x].getBackground().equals(Color.RED))
					mineButtons[y*sizeX+x].setBackground(defaultColor);
				else if(!mineButtons[y*sizeX+x].getBackground().equals(Color.GRAY) && mineButtons[y*sizeX+x].getText().equals(" ")) {
					mineButtons[y*sizeX+x].setBackground(Color.RED);
					checkWin();
				}
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			
		}
	}
	
	private void dfs(int x, int y) {
		if(x<0 || x>=sizeX || y<0 || y>=sizeY)
			return;
		if(mineButtons[y*sizeX+x].getBackground().equals(Color.GRAY)
				|| mineButtons[y*sizeX+x].getBackground().equals(Color.RED))
			return;
		if(!mineButtons[y*sizeX+x].getText().equals(" "))
			return;
		
		int near=0;
		for(int ny = y-1;ny<=y+1;ny++) {
			for(int nx=x-1;nx<=x+1;nx++) {
				if(nx<0 || nx>=sizeX || ny<0 || ny>=sizeY)
					continue;
				if(mineSet.contains(ny*sizeX+nx))
					near++;
			}
		}
		if(near>0)
			mineButtons[y*sizeX+x].setText(""+near);
		else {
			mineButtons[y*sizeX+x].setBackground(Color.GRAY);
			for(int ny = y-1;ny<=y+1;ny++) {
				for(int nx=x-1;nx<=x+1;nx++) {
					dfs(nx,ny);
				}
			}
		}
	}
}


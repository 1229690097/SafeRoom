package service;

import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.IconifyAction;

import com.googlecode.javacv.OpenCVFrameGrabber;
import com.googlecode.javacv.cpp.opencv_core.IplImage;

import javax.swing.UIManager;
import java.awt.Color;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.awt.event.ActionEvent;

public class ShowJFrame extends JFrame {

	public static boolean isMonitor = false; // 判断此时监控是否打开
	public static int num = 1;

	private JPanel contentPane;
	private final double maxtemperture = 100;// 最大温度差 -50---+50
	private final double maxCO = 100; // 最大CO浓度
	private static OpenCVFrameGrabber grabber = null;// 视频显示

	SpinnerModel model_temp = new SpinnerNumberModel(0, -50, 50, 1);
	SpinnerModel model_CO = new SpinnerNumberModel(0, 0, 50, 1);
	JSpinner spinner_CO = new JSpinner(model_CO); // 实现数字加减
	JSpinner spinner_temp = new JSpinner(model_temp);
	JProgressBar progressBar_temp = new JProgressBar();
	JProgressBar progressBar_CO = new JProgressBar();
	MyPanel panel = new MyPanel();// 自己定义的类，把视频图像放到panel
	public static ShowJFrame frame = new ShowJFrame();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ShowJFrame() {
		setTitle("安全屋");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 773, 554);
		contentPane = new JPanel();
		contentPane.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "", TitledBorder.LEADING,
				TitledBorder.TOP, null, new Color(0, 0, 0)));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblNewLabel = new JLabel(new ImageIcon(ShowJFrame.class.getResource("/img/home.png")));
		lblNewLabel.setBounds(10, 69, 331, 299);
		contentPane.add(lblNewLabel);

		panel.setBounds(361, 69, 386, 299);
		contentPane.add(panel);
		JLabel label_safe = new JLabel();

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(
				new TitledBorder(null, "\u95E8\u7A97\u76D1\u6D4B", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_1.setBounds(399, 417, 292, 88);
		contentPane.add(panel_1);
		panel_1.setLayout(null);

		JLabel label_1 = new JLabel("门");
		label_1.setBounds(24, 27, 35, 15);
		panel_1.add(label_1);

		JLabel label_door = new JLabel("New label");
		label_door.setIcon(new ImageIcon(ShowJFrame.class.getResource("/img/green.png")));
		label_door.setBackground(Color.RED);
		label_door.setBounds(73, 27, 14, 15);
		panel_1.add(label_door);

		JLabel label_2 = new JLabel("窗");
		label_2.setBounds(24, 59, 35, 15);
		panel_1.add(label_2);

		JLabel label_window = new JLabel();
		label_window.setIcon(new ImageIcon(ShowJFrame.class.getResource("/img/red.png")));
		label_window.setBackground(Color.RED);
		label_window.setBounds(73, 59, 14, 15);
		panel_1.add(label_window);

		JToggleButton toggleButton = new JToggleButton("关窗");
		JToggleButton tglbtnNewToggleButton = new JToggleButton("开门");

		// 门按钮控制
		tglbtnNewToggleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (tglbtnNewToggleButton.isSelected()) { // 当前门开着
					tglbtnNewToggleButton.setText("关门");
					label_door.setIcon(new ImageIcon(ShowJFrame.class.getResource("/img/red.png")));
				} else {
					tglbtnNewToggleButton.setText("开门");
					label_door.setIcon(new ImageIcon(ShowJFrame.class.getResource("/img/green.png")));
				}
				String state_door = tglbtnNewToggleButton.getText();
				String state_window = toggleButton.getText();
				if (state_door.equals("开门") && state_window.equals("开窗")) { // 窗户和门此时都关着
					label_safe.setIcon(new ImageIcon(ShowJFrame.class.getResource("/img/green.png")));
				} else { // 否则为红灯
					label_safe.setIcon(new ImageIcon(ShowJFrame.class.getResource("/img/red.png")));
				}
			}
		});
		tglbtnNewToggleButton.setBounds(157, 23, 125, 23);
		panel_1.add(tglbtnNewToggleButton);

		// 窗户按钮控制
		toggleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (toggleButton.isSelected()) { // 窗户开着为绿灯，关着为红灯
					toggleButton.setText("开窗");
					label_window.setIcon(new ImageIcon(ShowJFrame.class.getResource("/img/green.png")));
				} else {
					toggleButton.setText("关窗");
					label_window.setIcon(new ImageIcon(ShowJFrame.class.getResource("/img/red.png")));
				}
				String state_door = tglbtnNewToggleButton.getText();
				String state_window = toggleButton.getText();
				if (state_door.equals("开门") && state_window.equals("开窗")) { // 窗户和门此时都关着
					label_safe.setIcon(new ImageIcon(ShowJFrame.class.getResource("/img/green.png")));
				} else { // 否则为红灯
					label_safe.setIcon(new ImageIcon(ShowJFrame.class.getResource("/img/red.png")));
				}
			}
		});
		toggleButton.setBounds(157, 55, 125, 23);
		panel_1.add(toggleButton);

		JPanel panel_2 = new JPanel();
		panel_2.setBorder(
				new TitledBorder(null, "\u5B89\u5168\u76D1\u6D4B", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_2.setBounds(237, 10, 188, 49);
		contentPane.add(panel_2);
		panel_2.setLayout(null);

		JLabel lblNewLabel_2 = new JLabel("当前是否安全");
		lblNewLabel_2.setBounds(22, 24, 91, 15);
		panel_2.add(lblNewLabel_2);

		label_safe.setIcon(new ImageIcon(ShowJFrame.class.getResource("/img/red.png")));
		label_safe.setBackground(Color.RED);
		label_safe.setBounds(125, 24, 14, 15);
		panel_2.add(label_safe);

		JButton button_1 = new JButton("");
		button_1.setBackground(Color.WHITE);
		button_1.setIcon(new ImageIcon(ShowJFrame.class.getResource("/img/capture.png")));
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (isMonitor) {
					frame.closeMonitor();
					try {
						Thread.sleep(1000);
					} catch (Exception e2) {
						e2.printStackTrace();
					}

					new Thread() { // 此时监控开启
						public void run() {
							isMonitor = true;
							frame.monitor();
						}
					}.start();
				} else {
					new Thread() { // 此时监控开启
						public void run() {
							isMonitor = true;
							frame.monitor();
						}
					}.start();
				}
			}
		});
		button_1.setBounds(466, 378, 40, 29);
		contentPane.add(button_1);

		JPanel panel_3 = new JPanel();
		panel_3.setBorder(
				new TitledBorder(null, "\u4F20\u611F\u5668", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_3.setBounds(10, 417, 331, 88);
		contentPane.add(panel_3);
		panel_3.setLayout(null);

		JLabel label = new JLabel("CO浓度");
		label.setBounds(10, 55, 54, 15);
		panel_3.add(label);

		JLabel label_5 = new JLabel("温度：");
		label_5.setBounds(10, 22, 54, 15);
		panel_3.add(label_5);
		progressBar_temp.setString("当前室内温度：0℃");
		progressBar_temp.setStringPainted(true);

		progressBar_temp.setValue(50);
		progressBar_temp.setBounds(56, 22, 146, 23);
		panel_3.add(progressBar_temp);
		progressBar_CO.setString("当前CO浓度：0%");
		progressBar_CO.setStringPainted(true);
		progressBar_CO.setBounds(56, 55, 146, 23);
		panel_3.add(progressBar_CO);

		spinner_CO.setBounds(212, 52, 83, 22);
		panel_3.add(spinner_CO);

		spinner_temp.setBounds(212, 22, 83, 22);
		panel_3.add(spinner_temp);

		JLabel lblNewLabel_3 = new JLabel("室内俯视图");
		lblNewLabel_3.setBounds(95, 378, 140, 15);
		contentPane.add(lblNewLabel_3);

		JButton btnNewButton = new JButton(" 抓拍");
		btnNewButton.setIcon(new ImageIcon(ShowJFrame.class.getResource("/img/capture.png")));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!isMonitor) {
					javax.swing.JOptionPane.showMessageDialog(ShowJFrame.this, "监控未开启，无法抓拍！");
					return;
				}
				IplImage img;
				try {
					img = grabber.grab();
					BufferedImage image = img.getBufferedImage();
					File outputfile = new File("capture_pictures\\" + num + ".png");
					ImageIO.write(image, "png", outputfile);

					num++;
					javax.swing.JOptionPane.showMessageDialog(ShowJFrame.this, "抓拍成功！已存入当前项目文件夹capture_pictures中");
				} catch (com.googlecode.javacv.FrameGrabber.Exception | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});
		btnNewButton.setBounds(635, 378, 90, 29);
		contentPane.add(btnNewButton);

		JToggleButton toggleButton_1 = new JToggleButton("开启摄像头");
		toggleButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (toggleButton_1.isSelected()) {
					new Thread() { // 此时监控开启
						public void run() {
							isMonitor = true;
							frame.monitor();
						}
					}.start();
					toggleButton_1.setText("关闭摄像头");
				} else { // 此时监控关闭
					toggleButton_1.setText("开启摄像头");
					frame.closeMonitor();
				}
			}
		});
		toggleButton_1.setBounds(516, 378, 109, 29);
		contentPane.add(toggleButton_1);

		JButton button = new JButton("");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (isMonitor) {
					frame.closeMonitor();
					try {
						Thread.sleep(1000);
					} catch (Exception e2) {
						e2.printStackTrace();
					}
					new Thread() { // 此时监控开启
						public void run() {
							isMonitor = true;
							frame.monitor();
						}
					}.start();
				} else {
					new Thread() { // 此时监控开启
						public void run() {
							isMonitor = true;
							frame.monitor();
						}
					}.start();
				}
			}
		});
		button.setForeground(new Color(255, 0, 0));
		button.setBackground(Color.WHITE);
		button.setIcon(new ImageIcon(ShowJFrame.class.getResource("/img/capture.png")));
		button.setBounds(416, 378, 40, 29);
		contentPane.add(button);

		// JSpinner监听
		ChangeListener listener = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSpinner source = (JSpinner) e.getSource();
				updateProgressBar_CO();
				updateProgressBar_temperature();
			}
		};

		spinner_CO.addChangeListener(listener);
		spinner_temp.addChangeListener(listener);
	}

	// 更新进度条
	public void updateProgressBar_temperature() {
		int current = (int) spinner_temp.getValue();
		progressBar_temp.setString("当前室内温度：" + current + "℃");
		// 最低温度为-50，进度条计算比例 eg : (50+50)/100 此时正好填满
		progressBar_temp.setValue((current + 50));
		progressBar_temp.setMaximum((int) maxtemperture);
	}

	public void updateProgressBar_CO() {
		int current = (int) spinner_CO.getValue();
		progressBar_CO.setString("当前CO浓度：" + current + "%");
		progressBar_CO.setMaximum((int) maxCO);
		progressBar_CO.setValue((int) current);//
	}

	public void monitor() {

		grabber = new OpenCVFrameGrabber(0);
		try {
			grabber.start();
		} catch (com.googlecode.javacv.FrameGrabber.Exception e1) {
			e1.printStackTrace();
		}
		panel.setVisible(true);

		while (true) {
			if (!isMonitor) { // 监控关闭
				break;
			}
			IplImage img;
			try {
				img = grabber.grab();
				BufferedImage image = img.getBufferedImage();
				BufferedImage inputbig = new BufferedImage(256, 256, BufferedImage.TYPE_INT_BGR);
				inputbig.getGraphics().drawImage(image, 0, 0, 256, 256, null);
				frame.setImage(inputbig);

			} catch (com.googlecode.javacv.FrameGrabber.Exception e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(20);// 图像刷新速度 20ms刷新一次
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	// 开启监控
	public void startMonitor() {
		if (panel == null) {
			panel = new MyPanel();
		} else {
			panel.setVisible(true);
		}

		panel.setBounds(361, 69, 386, 299);
		contentPane.add(panel);

	}

	// 关闭监控
	public void closeMonitor() {
		try {
			isMonitor = false;
			grabber.stop();// 停止抓取图像
			panel.setVisible(false);
			// System.exit(2); // 退出
		} catch (com.googlecode.javacv.FrameGrabber.Exception e) {
			e.printStackTrace();
		}
	}

	public void setImage(BufferedImage image) {
		panel.setImage(image);
	}

	// 显示视频 自定义类
	class MyPanel extends JPanel {

		Image image = null;

		public Image getImage() {
			return image;
		}

		public void setImage(Image image) {
			this.image = image;
			this.repaint();
		}

		public void paint(Graphics g) {
			try {
				g.drawImage(image, 0, 0, 550, 400, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}

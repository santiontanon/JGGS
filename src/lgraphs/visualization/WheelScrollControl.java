package lgraphs.visualization;

import prefuse.Display;
import prefuse.controls.ControlAdapter;
import prefuse.visual.VisualItem;

import java.awt.event.MouseWheelEvent;

/**
 * ControlAdapter that enables vertical scrolling of a Prefuse Display
 * by moving the mouse wheel up and down.
 *
 * @author Arne Timmerman
 */
public class WheelScrollControl extends ControlAdapter {

	private static final int BLOCK_SCROLL_AMOUNT = 50;
	private double m_initialVerticalPosition;

	/**
	 * Create a WheelScrollControl object that accounts for the initial vertical pan while scrolling.
	 * The scrolling will never exceed the initial pan.
	 *
	 * @param initialVerticalPan the amount of vertical pan of the display where this control is enabled on
	 */
	public WheelScrollControl(double initialVerticalPan) {

		// Vertical panning of a Prefuse Display is negated compared to the vertical postion
		this.m_initialVerticalPosition = -initialVerticalPan;
	}

	/**
	 * Create a WheelScrollControl object. The initial vertical pan is set to zero.
	 */
	public WheelScrollControl() {

		this(0);
	}
	
	

	/**
	 * Event that is fired when the mouse wheel is moved above a VisualItem at
	 * the Display where this control is placed on.
	 *
	 * @param visualItem the visualitem where the mouse event was fired above
	 * @param mouseEvent details on the mouse event that is fired
	 * @see prefuse.controls.Control#itemWheelMoved(prefuse.visual.VisualItem, java.awt.event.MouseWheelEvent)
	 */
	@Override
	public void itemWheelMoved(VisualItem visualItem, MouseWheelEvent mouseEvent) {

		mouseWheelMoved(mouseEvent);
	}

	/**
	 * Event that is fired when the mouse wheel is moved above the Display
	 * where this control is placed on.
	 *
	 * @param mouseEvent details on the mouse event that is fired
	 * @see java.awt.event.MouseWheelListener#mouseWheelMoved(java.awt.event.MouseWheelEvent)
	 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent mouseEvent) {

		Display display = (Display) mouseEvent.getComponent();

		// The vertical movement that is calculated from a Mouse Wheel Event
		// is negated compared to normal scrolling behavior
		int negatedVerticalMovement = 0;
		int horizontalMovement = 0;
		
		if (mouseEvent.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
			if(mouseEvent.getModifiers() == 1) {
				horizontalMovement = mouseEvent.getUnitsToScroll();
			}else{
				negatedVerticalMovement = mouseEvent.getUnitsToScroll();
			}
		} else if (mouseEvent.getScrollType() == MouseWheelEvent.WHEEL_BLOCK_SCROLL) {
			int wheelRotation = mouseEvent.getWheelRotation();
			if(mouseEvent.getModifiers() == 1){
				horizontalMovement = (BLOCK_SCROLL_AMOUNT * wheelRotation);
			}else{
				negatedVerticalMovement = (BLOCK_SCROLL_AMOUNT * wheelRotation);
			}
		}

		// Negate the negated vertical movement back to stimulate normal behavior
		// of scrolling, this means: content up if scroll wheel is wheeled back
		int verticalMovement = -negatedVerticalMovement;

			double currentVerticalPosition = display.getDisplayY();
			double newVerticalPosition = currentVerticalPosition - verticalMovement;
		
		scrollDisplay(display, horizontalMovement, verticalMovement);
//		// Scroll if the new vertical position does not exceeds the initial position
//		if (newVerticalPosition >= m_initialVerticalPosition) {
//			scrollDisplay(display, verticalMovement);
//
//		// Scroll partial if the new vertical position exceeds the initial position
//		} else if ((verticalMovement > 0) && (currentVerticalPosition != m_initialVerticalPosition)) {
//	   		int partialVerticalMovement = (int) (currentVerticalPosition - m_initialVerticalPosition);
//			scrollDisplay(display, partialVerticalMovement);
//			
//		// Do not scroll
//		} else {
//		}
	}

	/**
	 * Scroll the given display with the given amount of pan in vertical direction.
	 * @param display the display that has te be panned
	 * @param verticalMovement the amount of vertical pan that has te be scrolled
	 */
	private void scrollDisplay(Display display, int horizontalMovement, int verticalMovement) {

		display.pan(horizontalMovement, -verticalMovement);
		display.repaint();
	}
	

}
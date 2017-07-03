/**
 *
 */
package eval;

import model.Employee;
import model.ScheduleInterface;

/**
 * Schedule Evaluator
 *
 * @author goulas
 */
public interface Evaluator {
	// Will be instanciated as new Evaluator(problem);

	/**
	 * Sets a whole schedule
	 */
	public void setSchedule(ScheduleInterface result);

	/**
	 * Gets the active schedule
	 *
	 * @return
	 */
	public ScheduleInterface getSchedule();

	/**
	 * Gets the cost of the current schedule
	 */
	public int getCost();

	/**
	 * Gets the cost of the current employee schedule
	 */
	public int evaluateCostFor(Employee emp);

	public int evaluateDayWorkCostFor(Employee emp, int assignments);

	public int evaluateDayWorkCostFor(Employee emp);

	public int evaluateShiftWorkCostFor(Employee emp);

	public int evaluateDayWorkCost();

	/**
	 * Returns the day assignment related cost when the weekly patterns are
	 * changed according to the stored values in w_cd. The schedule object
	 * should be unaffected on finish.
	 *
	 * @param emp
	 * @param w_cd
	 * @return
	 */
	public int evaluateDayWorkCost(int emp_id, int[] w_cd);

	/**
	 * Checks is a schedule is feasible
	 */
	public boolean isFeasible();

	public void evaluate();

	public String analyzeCost();

}

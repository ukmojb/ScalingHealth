/*
 * Scaling Health
 * Copyright (C) 2018 SilentChaos512
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 3
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.silentchaos512.scalinghealth.lib;

import net.minecraftforge.common.config.Configuration;
import net.silentchaos512.scalinghealth.ScalingHealth;
import net.silentchaos512.scalinghealth.config.Config;

import javax.annotation.Nullable;
import java.util.Calendar;

public enum EnumResetTime {
    NONE, SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, DAILY, MONTHLY;

    public boolean shouldReset(Calendar today, Calendar lastTimePlayed) {
        int todayDate = today.get(Calendar.DATE);
        int lastDate = lastTimePlayed.get(Calendar.DATE);
        int todayMonth = today.get(Calendar.MONTH);
        int lastMonth = today.get(Calendar.MONTH);
        int todayYear = today.get(Calendar.YEAR);
        int lastYear = today.get(Calendar.YEAR);
        boolean isSameDate = todayDate == lastDate && todayMonth == lastMonth && todayYear == lastYear;

        switch (this) {
            case NONE:
                return false;
            case DAILY:
                return !isSameDate;
            case MONTHLY:
                return todayMonth != lastMonth;
            case SUNDAY:
            case MONDAY:
            case TUESDAY:
            case WEDNESDAY:
            case THURSDAY:
            case FRIDAY:
            case SATURDAY:
                Calendar nextReset = getNextResetDay(lastTimePlayed);
                return !today.before(nextReset);
            default:
                ScalingHealth.logHelper.warn("Unknown EnumResetTime: " + this);
                return false;
        }
    }

    @Nullable
    private Calendar getNextResetDay(Calendar lastTimePlayed) {
        Calendar nextReset = Calendar.getInstance();
        nextReset.setTime(lastTimePlayed.getTime());
        nextReset.set(Calendar.HOUR, 0);
        nextReset.set(Calendar.MINUTE, 1);
        nextReset.set(Calendar.SECOND, 1);
        nextReset.set(Calendar.AM_PM, Calendar.AM);
        nextReset.add(Calendar.DATE, 1);
        int nextResetDayOfWeek = getNextResetDayOfWeek();
        if (nextResetDayOfWeek < 0)
            return null;
        while (nextReset.get(Calendar.DAY_OF_WEEK) != nextResetDayOfWeek) {
            nextReset.add(Calendar.DATE, 1);
        }
        return nextReset;
    }

    private int getNextResetDayOfWeek() {
        switch (this) {
            case SUNDAY:
                return Calendar.SUNDAY;
            case MONDAY:
                return Calendar.MONDAY;
            case TUESDAY:
                return Calendar.TUESDAY;
            case WEDNESDAY:
                return Calendar.WEDNESDAY;
            case THURSDAY:
                return Calendar.THURSDAY;
            case FRIDAY:
                return Calendar.FRIDAY;
            case SATURDAY:
                return Calendar.SATURDAY;
            default:
                return -1;
        }
    }

    public static EnumResetTime loadFromConfig(Configuration c, EnumResetTime defaultValue, String category) {
        String[] validValues = new String[values().length];
        for (int i = 0; i < values().length; ++i)
            validValues[i] = values()[i].name();

        String whichOne = category.equals(Config.CAT_DIFFICULTY) ? "difficulty" : "health";
        String str = c.getString("Reset Time", category,
                defaultValue.name(),
                "Allows players' " + whichOne + " to be reset at certain frequencies.\n"
                        + "  NONE - Do not do regular resets."
                        + "  Weekdays (SUNDAY, MONDAY, etc.) - Reset on this day of the week. If the player does not play on this day, they will be reset the next time they log in.\n"
                        + "  DAILY - Reset if the last time the player logged in was a different day."
                        + "  MONTHLY - Reset if the last time the player logged in was a different month (everyone resets on the first of the month).",
                validValues);

        for (EnumResetTime mode : values())
            if (mode.name().equalsIgnoreCase(str))
                return mode;
        return defaultValue;
    }
}

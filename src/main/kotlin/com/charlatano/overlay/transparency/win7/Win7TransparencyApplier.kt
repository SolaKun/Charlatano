/*
 * Charlatano: Free and open-source (FOSS) cheat for CS:GO/CS:CO
 * Copyright (C) 2017 - Thomas G. P. Nappo, Jonathan Beaudoin
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.charlatano.overlay.transparency.win7

import com.charlatano.overlay.transparency.TransparencyApplier
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.User32.INSTANCE as User32

object Win7TransparencyApplier : TransparencyApplier {

    override fun applyTransparency(hwnd: WinDef.HWND) = DWM_BLURBEHIND().run {
        dwFlags = WinDef.DWORD(DWM.DWM_BB_ENABLE)
        fEnable = true
        hRgnBlur = null
        DWM.DwmEnableBlurBehindWindow(hwnd, this).toInt() == 0
    }

}

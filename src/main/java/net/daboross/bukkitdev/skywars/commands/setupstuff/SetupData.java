/*
 * Copyright (C) 2013-2014 Dabo Ross <http://www.daboross.net/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.daboross.bukkitdev.skywars.commands.setupstuff;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.daboross.bukkitdev.skywars.api.arenaconfig.SkyArenaConfig;
import net.daboross.bukkitdev.skywars.api.arenaconfig.SkyBoundaries;
import net.daboross.bukkitdev.skywars.api.location.SkyBlockLocation;
import net.daboross.bukkitdev.skywars.api.location.SkyBlockLocationRange;
import net.daboross.bukkitdev.skywars.api.location.SkyPlayerLocation;

public class SetupData {

    private String arenaName;
    private Path saveFile;
    private SkyBlockLocation originPos1;
    private SkyBlockLocation originPos2;
    private SkyBlockLocation originMin;
    private SkyBlockLocation originMax;
    private final List<SkyPlayerLocation> spawns = new ArrayList<>();

    public void setOriginPos1(SkyBlockLocation originPos1) {
        this.originPos1 = originPos1;
        setOriginMin();
        setOriginMax();
    }

    public void setOriginPos2(SkyBlockLocation originPos2) {
        this.originPos2 = originPos2;
        setOriginMin();
        setOriginMax();
    }

    public void setOriginMin() {
        if (originPos1 != null && originPos2 != null) {
            originMin = SkyBlockLocation.min(originPos1, originPos2);
        }
    }

    public void setOriginMax() {
        if (originPos1 != null && originPos2 != null) {
            originMax = SkyBlockLocation.max(originPos1, originPos2);
        }
    }

    public SkyArenaConfig convertToArenaConfig() {
        if (originMin == null || originMax == null) {
            throw new IllegalStateException("Origin not defined.");
        }
        SkyArenaConfig config = new SkyArenaConfig();
        config.setArenaName(arenaName);
        config.setFile(saveFile);
        SkyBlockLocationRange origin = new SkyBlockLocationRange(originMin, originMax, originMin.world);
        SkyBlockLocationRange clearing = calculateClearing(origin);
        SkyBlockLocationRange building = calculateBuilding(clearing);
        SkyBoundaries boundaries = config.getBoundaries();
        boundaries.setOrigin(origin);
        boundaries.setClearing(clearing);
        boundaries.setBuilding(building);
        config.setPlacementY(20);
        config.setSpawns(spawns);
        config.setNumTeams(spawns.size());
        config.setTeamSize(1);
        return config;
    }

    private static SkyBlockLocationRange calculateClearing(SkyBlockLocationRange origin) {
        SkyBlockLocation min = new SkyBlockLocation(-1, -20, -1, null);
        SkyBlockLocation max = new SkyBlockLocation(origin.max.x - origin.min.x + 1,
                origin.max.y - origin.min.y + 1,
                origin.max.z - origin.min.z + 1, null);
        return new SkyBlockLocationRange(min, max, null);
    }

    private static SkyBlockLocationRange calculateBuilding(SkyBlockLocationRange clearing) {
        SkyBlockLocation min = clearing.min.add(-1, 0, -1);
        SkyBlockLocation max = clearing.max.add(1, 1, 1);
        return new SkyBlockLocationRange(min, max, null);
    }

    public String getArenaName() {
        return arenaName;
    }

    public Path getSaveFile() {
        return saveFile;
    }

    public SkyBlockLocation getOriginPos1() {
        return originPos1;
    }

    public SkyBlockLocation getOriginPos2() {
        return originPos2;
    }

    public SkyBlockLocation getOriginMin() {
        return originMin;
    }

    public SkyBlockLocation getOriginMax() {
        return originMax;
    }

    public List<SkyPlayerLocation> getSpawns() {
        return spawns;
    }

    public void setArenaName(final String arenaName) {
        this.arenaName = arenaName;
    }

    public void setSaveFile(final Path saveFile) {
        this.saveFile = saveFile;
    }

    public void setOriginMin(final SkyBlockLocation originMin) {
        this.originMin = originMin;
    }

    public void setOriginMax(final SkyBlockLocation originMax) {
        this.originMax = originMax;
    }

    @Override
    public String toString() {
        return "SetupData{" +
                "arenaName='" + arenaName + '\'' +
                ", saveFile=" + saveFile +
                ", originPos1=" + originPos1 +
                ", originPos2=" + originPos2 +
                ", originMin=" + originMin +
                ", originMax=" + originMax +
                ", spawns=" + spawns +
                '}';
    }

    @Override
    @SuppressWarnings("RedundantIfStatement")
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof SetupData)) return false;

        SetupData data = (SetupData) o;

        if (arenaName != null ? !arenaName.equals(data.arenaName) : data.arenaName != null) return false;
        if (originMax != null ? !originMax.equals(data.originMax) : data.originMax != null) return false;
        if (originMin != null ? !originMin.equals(data.originMin) : data.originMin != null) return false;
        if (originPos1 != null ? !originPos1.equals(data.originPos1) : data.originPos1 != null) return false;
        if (originPos2 != null ? !originPos2.equals(data.originPos2) : data.originPos2 != null) return false;
        if (saveFile != null ? !saveFile.equals(data.saveFile) : data.saveFile != null) return false;
        if (!spawns.equals(data.spawns)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = arenaName != null ? arenaName.hashCode() : 0;
        result = 31 * result + (saveFile != null ? saveFile.hashCode() : 0);
        result = 31 * result + (originPos1 != null ? originPos1.hashCode() : 0);
        result = 31 * result + (originPos2 != null ? originPos2.hashCode() : 0);
        result = 31 * result + (originMin != null ? originMin.hashCode() : 0);
        result = 31 * result + (originMax != null ? originMax.hashCode() : 0);
        result = 31 * result + spawns.hashCode();
        return result;
    }
}

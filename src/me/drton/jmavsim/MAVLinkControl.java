package me.drton.jmavsim;

import me.drton.jmavlib.mavlink.MAVLinkMessage;
import me.drton.jmavlib.mavlink.MAVLinkSchema;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: ton Date: 22.05.14 Time: 17:23
 */
public class MAVLinkControl extends MAVLinkSystem {
    private List<MAVLinkMissionItem> mission = new ArrayList<MAVLinkMissionItem>();
    private long missionSendTime = 0;
    private long lastActionTime = 0;
    private int targetSysId;
    private int targetComponentId;
    private boolean sending = false;
    private long timeout = 4000;

    public MAVLinkControl(MAVLinkSchema schema, int sysId, int componentId, int targetSysId, int targetComponentId) {
        super(schema, sysId, componentId);
        this.targetSysId = targetSysId;
        this.targetComponentId = targetComponentId;
    }

    @Override
    public void handleMessage(MAVLinkMessage msg) {
        super.handleMessage(msg);
        if ("MISSION_REQUEST".equals(msg.getMsgName())) {
            int target_system = msg.getInt("target_system");
            int target_component = msg.getInt("target_component");
            if (target_system == sysId && (target_component == componentId || target_component == 0)) {
                int itemID = msg.getInt("seq");
                if (itemID >= 0 && itemID < mission.size()) {
                    MAVLinkMissionItem item = mission.get(itemID);
                    System.out.println("Mission send: item " + itemID + " " + item);
                    MAVLinkMessage mission_item = new MAVLinkMessage(schema, "MISSION_ITEM", sysId, componentId);
                    mission_item.set("target_system", targetSysId);
                    mission_item.set("target_component", targetComponentId);
                    mission_item.set("seq", itemID);
                    mission_item.set("current", (itemID == 0) ? 1 : 0); // TODO
                    mission_item.set("command", item.command);
                    mission_item.set("frame", item.frame);
                    mission_item.set("autocontinue", item.autocontinue);
                    mission_item.set("param1", item.param1);
                    mission_item.set("param2", item.param2);
                    mission_item.set("param3", item.param3);
                    mission_item.set("param4", item.param4);
                    mission_item.set("x", item.x);
                    mission_item.set("y", item.y);
                    mission_item.set("z", item.z);
                    mission_item.set("autocontinue", item.autocontinue);
                    sendMessage(mission_item);
                }
            }
        } else if ("MISSION_ACK".equals(msg.getMsgName())) {
            int target_system = msg.getInt("target_system");
            int target_component = msg.getInt("target_component");
            if (target_system == sysId && (target_component == componentId || target_component == 0)) {
                System.out.println("Mission sent");
                missionSendTime = 0;
                sending = false;
            }
        }
    }

    @Override
    public void update(long t) {
        if (sending) {
            if (t - lastActionTime > timeout) {
                System.out.println("Mission sending timeout");
                missionSendTime = 0;
                sending = false;
            }
        } else if (missionSendTime != 0 && t > missionSendTime) {
            sending = true;
            System.out.printf("Mission sending started, %s items\n", mission.size());
            MAVLinkMessage mission_count = new MAVLinkMessage(schema, "MISSION_COUNT", sysId, componentId);
            mission_count.set("target_system", targetSysId);
            mission_count.set("target_component", targetComponentId);
            mission_count.set("count", mission.size());
            sendMessage(mission_count);
            lastActionTime = t;
        }
    }

    public void setMissionSendTime(long t) {
        missionSendTime = t;
    }

    public void resetMission() {
        mission.clear();
    }

    public void addMissionItem(MAVLinkMissionItem item) {
        mission.add(item);
    }

    public void loadMission(String fileName) throws IOException {
        FileReader fileReader = new FileReader(fileName);
        BufferedReader br = new BufferedReader(fileReader);
        String line;
        br.readLine();    // skip first line
        while ((line = br.readLine()) != null) {
            String[] a = line.split("[ \t]+");
            mission.add(new MAVLinkMissionItem(Integer.parseInt(a[2]), Integer.parseInt(a[3]), Float.parseFloat(a[4]),
                    Float.parseFloat(a[5]), Float.parseFloat(a[6]), Float.parseFloat(a[7]), Float.parseFloat(a[8]),
                    Float.parseFloat(a[9]), Float.parseFloat(a[10]), Integer.parseInt(a[11])));
        }
        fileReader.close();
    }
}

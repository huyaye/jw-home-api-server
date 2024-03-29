package com.jw.home.domain;

import com.jw.home.common.spec.HomeSecurityMode;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Document(collection = "home")
public class Home {
    @Getter
    @Setter
    public static class Room {
        private String roomName;
        private Set<String> deviceIds;

        public boolean containsDeviceId(String deviceId) {
            if (deviceIds != null) {
                return deviceIds.contains(deviceId);
            }
            return false;
        }
    }

    @Id
    private String id;

    private String homeName;

    private String timezone;

    private HomeSecurityMode securityMode;

    private List<Room> rooms;

    private Set<String> sharedMemberIds;

    private Set<String> invitedMemberIds;

    private Set<String> noRoomDeviceIds;

    public boolean hasNoRelatedMembers() {
        return (sharedMemberIds == null || sharedMemberIds.isEmpty()) &&
                (invitedMemberIds == null || invitedMemberIds.isEmpty());
    }

    public boolean hasMember(String memId) {
        return (sharedMemberIds != null && sharedMemberIds.contains(memId)) ||
                (invitedMemberIds != null && invitedMemberIds.contains(memId));
    }

    public boolean hasSharedMember(String memId) {
        return sharedMemberIds != null && sharedMemberIds.contains(memId);
    }

    public void addInvitedMemberId(String memberId) {
        if (invitedMemberIds == null) {
            invitedMemberIds = new HashSet<>();
        }
        invitedMemberIds.add(memberId);
    }

    public void addSharedMemberId(String memberId) {
        if (sharedMemberIds == null) {
            sharedMemberIds = new HashSet<>();
        }
        sharedMemberIds.add(memberId);
    }

    public void approveMember(String memId) {
        invitedMemberIds.remove(memId);
        if (sharedMemberIds == null) {
            sharedMemberIds = new HashSet<>();
        }
        sharedMemberIds.add(memId);
    }

    public void evictMember(String memId) {
        if (sharedMemberIds != null) {
            sharedMemberIds.remove(memId);
        }
        if (invitedMemberIds != null) {
            invitedMemberIds.remove(memId);
        }
    }

    public void addNoRoomDeviceId(String deviceId) {
        if (noRoomDeviceIds == null) {
            noRoomDeviceIds = new HashSet<>();
        }
        noRoomDeviceIds.add(deviceId);
    }

    public boolean removeDevice(String deviceId) {
        if (noRoomDeviceIds != null) {
            if (noRoomDeviceIds.remove(deviceId)) {
                return true;
            }
        }
        if (rooms != null) {
            for (Room room : rooms) {
                Set<String> deviceIds = room.getDeviceIds();
                if (deviceIds != null && deviceIds.remove(deviceId)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Set<String> getDeviceIds() {
        Set<String> allDeviceIds = new HashSet<>();
        if (noRoomDeviceIds != null) {
            allDeviceIds.addAll(noRoomDeviceIds);
        }
        if (rooms != null) {
            for (Room room : rooms) {
                Set<String> deviceIds = room.getDeviceIds();
                if (deviceIds != null) {
                    allDeviceIds.addAll(deviceIds);
                }
            }
        }
        return allDeviceIds;
    }
}
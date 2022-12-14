package science.atlarge.opencraft.opencraft.messaging.dyconits;

import com.flowpowered.network.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import science.atlarge.opencraft.dyconits.MessageQueue;
import science.atlarge.opencraft.opencraft.net.message.play.entity.EntityRotationMessage;
import science.atlarge.opencraft.opencraft.net.message.play.entity.EntityTeleportMessage;
import science.atlarge.opencraft.opencraft.net.message.play.entity.RelativeEntityPositionMessage;
import science.atlarge.opencraft.opencraft.net.message.play.entity.RelativeEntityPositionRotationMessage;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MergeMessageQueue implements MessageQueue<Message> {

    private final static int SHIFT = 1 << 12;

    private final Queue<Message> messageList = new ConcurrentLinkedQueue<>();
    private final Map<Integer, EntityPositionRotationUnionMessage> entityMessageMap = new ConcurrentHashMap<>();

    @Override
    public void add(Message message) {
        if (message instanceof RelativeEntityPositionRotationMessage) {
            RelativeEntityPositionRotationMessage newMessage = (RelativeEntityPositionRotationMessage) message;
            if (entityMessageMap.containsKey(newMessage.getId())) {
                EntityPositionRotationUnionMessage unionMessage = entityMessageMap.get(newMessage.getId());
                unionMessage.merge(newMessage);
            } else {
                EntityPositionRotationUnionMessage newUnionMessage = new EntityPositionRotationUnionMessage(newMessage);
                messageList.add(newUnionMessage);
                entityMessageMap.put(newMessage.getId(), newUnionMessage);
            }
        } else if (message instanceof RelativeEntityPositionMessage) {
            RelativeEntityPositionMessage newMessage = (RelativeEntityPositionMessage) message;
            if (entityMessageMap.containsKey(newMessage.getId())) {
                EntityPositionRotationUnionMessage unionMessage = entityMessageMap.get(newMessage.getId());
                unionMessage.merge(newMessage);
            } else {
                EntityPositionRotationUnionMessage newUnionMessage = new EntityPositionRotationUnionMessage(newMessage);
                messageList.add(newUnionMessage);
                entityMessageMap.put(newMessage.getId(), newUnionMessage);
            }
        } else if (message instanceof EntityRotationMessage) {
            EntityRotationMessage newMessage = (EntityRotationMessage) message;
            if (entityMessageMap.containsKey(newMessage.getId())) {
                EntityPositionRotationUnionMessage unionMessage = entityMessageMap.get(newMessage.getId());
                unionMessage.merge(newMessage);
            } else {
                EntityPositionRotationUnionMessage newUnionMessage = new EntityPositionRotationUnionMessage(newMessage);
                messageList.add(newUnionMessage);
                entityMessageMap.put(newMessage.getId(), newUnionMessage);
            }
        } else if (message instanceof EntityTeleportMessage) {
            EntityTeleportMessage newMessage = (EntityTeleportMessage) message;
            if (entityMessageMap.containsKey(newMessage.getId())) {
                EntityPositionRotationUnionMessage unionMessage = entityMessageMap.get(newMessage.getId());
                unionMessage.merge(newMessage);
            } else {
                EntityPositionRotationUnionMessage newUnionMessage = new EntityPositionRotationUnionMessage(newMessage);
                messageList.add(newUnionMessage);
                entityMessageMap.put(newMessage.getId(), newUnionMessage);
            }
        } else {
            messageList.add(message);
        }
    }

    @Override
    public boolean isEmpty() {
        return messageList.isEmpty();
    }

    @Override
    public Message remove() {
        Message msg = messageList.remove();
        if (msg instanceof EntityPositionRotationUnionMessage) {
            EntityPositionRotationUnionMessage unionMessage = (EntityPositionRotationUnionMessage) msg;
            entityMessageMap.remove(unionMessage.getId());
            return unionMessage.toRealMessage();
        }
        return msg;
    }

    @Data
    @AllArgsConstructor
    private static class EntityPositionRotationUnionMessage implements Message {
        private final int id;
        private double x;
        private double y;
        private double z;
        private int rotation;
        private int pitch;
        private boolean onGround;

        private boolean hasMoved;
        private boolean movementIsRelative;
        private boolean hasRotated;

        EntityPositionRotationUnionMessage(EntityTeleportMessage message) {
            id = message.getId();
            updateAbsolutePosition(message);
        }

        EntityPositionRotationUnionMessage(RelativeEntityPositionRotationMessage message) {
            id = message.getId();
            updateRelativePosition(message.getDeltaX(), message.getDeltaY(), message.getDeltaZ(), message.isOnGround());
            updateRotation(message.getRotation(), message.getPitch(), message.isOnGround());
            movementIsRelative = true;
        }

        EntityPositionRotationUnionMessage(RelativeEntityPositionMessage message) {
            id = message.getId();
            updateRelativePosition(message.getDeltaX(), message.getDeltaY(), message.getDeltaZ(), message.isOnGround());
            movementIsRelative = true;
        }

        EntityPositionRotationUnionMessage(EntityRotationMessage message) {
            id = message.getId();
            updateRotation(message.getRotation(), message.getPitch(), message.isOnGround());
        }

        void merge(EntityTeleportMessage message) {
            assert id == message.getId();
            updateAbsolutePosition(message);
        }

        void merge(RelativeEntityPositionRotationMessage message) {
            assert id == message.getId();
            updateRelativePosition(message.getDeltaX(), message.getDeltaY(), message.getDeltaZ(), message.isOnGround());
            updateRotation(message.getRotation(), message.getPitch(), message.isOnGround());
        }

        void merge(RelativeEntityPositionMessage message) {
            assert id == message.getId();
            updateRelativePosition(message.getDeltaX(), message.getDeltaY(), message.getDeltaZ(), message.isOnGround());
        }

        void merge(EntityRotationMessage message) {
            assert id == message.getId();
            updateRotation(message.getRotation(), message.getPitch(), message.isOnGround());
        }

        private void updateAbsolutePosition(EntityTeleportMessage message) {
            x = message.getX();
            y = message.getY();
            z = message.getZ();
            rotation = message.getRotation();
            pitch = message.getPitch();
            onGround = message.isOnGround();

            hasMoved = true;
            movementIsRelative = false;
            hasRotated = true;
        }

        private void updateRelativePosition(short deltaX, short deltaY, short deltaZ, boolean onGround) {
            double dx = (double) deltaX / SHIFT;
            double dy = (double) deltaY / SHIFT;
            double dz = (double) deltaZ / SHIFT;
            this.x += dx;
            this.y += dy;
            this.z += dz;
            this.onGround = onGround;

            this.hasMoved = true;
        }

        private void updateRotation(int rotation, int pitch, boolean onGround) {
            this.rotation = rotation;
            this.pitch = pitch;
            this.onGround = onGround;

            this.hasRotated = true;
        }

        Message toRealMessage() {

            double dx = x * SHIFT;
            double dy = y * SHIFT;
            double dz = z * SHIFT;

            boolean teleport = !movementIsRelative
                    || dx > Short.MAX_VALUE
                    || dy > Short.MAX_VALUE
                    || dz > Short.MAX_VALUE
                    || dx < Short.MIN_VALUE
                    || dy < Short.MIN_VALUE
                    || dz < Short.MIN_VALUE;

            if (!hasMoved) {
                return new EntityRotationMessage(id, rotation, pitch, onGround);
            } else {
                if (!teleport) {
                    if (hasRotated) {
                        return new RelativeEntityPositionRotationMessage(id, (short) dx, (short) dy, (short) dz, rotation, pitch, onGround);
                    } else {
                        return new RelativeEntityPositionMessage(id, (short) dx, (short) dy, (short) dz, onGround);
                    }
                } else {
                    return new EntityTeleportMessage(id, x, y, z, rotation, pitch, onGround);
                }
            }
        }
    }
}

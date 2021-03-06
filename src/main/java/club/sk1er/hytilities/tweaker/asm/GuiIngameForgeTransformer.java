/*
 * Hytilities - Hypixel focused Quality of Life mod.
 * Copyright (C) 2020  Sk1er LLC
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
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package club.sk1er.hytilities.tweaker.asm;

import club.sk1er.hytilities.tweaker.transformer.HytilitiesTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ListIterator;

public class GuiIngameForgeTransformer implements HytilitiesTransformer {
    @Override
    public String[] getClassName() {
        return new String[]{"net.minecraftforge.client.GuiIngameForge"};
    }

    @Override
    public void transform(ClassNode classNode, String name) {
        for (MethodNode method : classNode.methods) {
            if (method.name.equals("renderHealth")) {
                ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();

                while (iterator.hasNext()) {
                    AbstractInsnNode next = iterator.next();
                    if (next instanceof MethodInsnNode && next.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                        String methodName = mapMethodNameFromNode(next);

                        if (methodName.equals("isHardcoreModeEnabled") || methodName.equals("func_76093_s")) {
                            for (int i = 0; i < 7; i++) {
                                method.instructions.remove(next.getNext());
                            }

                            method.instructions.insert(next, forceHardcore());
                            break;
                        }
                    }
                }
            } else if (method.name.equals("renderTitle")) {
                method.instructions.insertBefore(method.instructions.getFirst(), checkTitle());
            }
        }
    }

    private InsnList checkTitle() {
        InsnList list = new InsnList();
        list.add(new VarInsnNode(Opcodes.ALOAD, 0));
        list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraftforge/client/GuiIngameForge", "field_175201_x", "Ljava/lang/String;"));
        list.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "net/minecraft/util/EnumChatFormatting", "func_110646_a", "(Ljava/lang/String;)Ljava/lang/String;", false));
        list.add(new VarInsnNode(Opcodes.ASTORE, 69));
        list.add(new VarInsnNode(Opcodes.ALOAD, 69));
        list.add(new LdcInsnNode("Your Mini Wither died!"));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false));
        LabelNode ifne = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFNE, ifne));
        list.add(new VarInsnNode(Opcodes.ALOAD, 69));
        list.add(new LdcInsnNode("Your Wither died!"));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false));
        list.add(new JumpInsnNode(Opcodes.IFNE, ifne));
        list.add(new VarInsnNode(Opcodes.ALOAD, 69));
        list.add(new LdcInsnNode("BED DESTROYED!"));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/String", "equals", "(Ljava/lang/Object;)Z", false));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(ifne);
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, "club/sk1er/hytilities/config/HytilitiesConfig", "hardcoreHearts", "Z"));
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, "club/sk1er/hytilities/Hytilities", "INSTANCE", "Lclub/sk1er/hytilities/Hytilities;"));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "club/sk1er/hytilities/Hytilities", "getHardcoreStatus", "()Lclub/sk1er/hytilities/handlers/game/hardcore/HardcoreStatus;", false));
        list.add(new InsnNode(Opcodes.ICONST_1));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "club/sk1er/hytilities/handlers/game/hardcore/HardcoreStatus", "setDanger", "(Z)V", false));
        list.add(ifeq);
        return list;
    }

    private InsnList forceHardcore() {
        InsnList list = new InsnList();
        LabelNode ifne = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFNE, ifne));
        list.add(new FieldInsnNode(Opcodes.GETSTATIC, "club/sk1er/hytilities/Hytilities", "INSTANCE", "Lclub/sk1er/hytilities/Hytilities;"));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "club/sk1er/hytilities/Hytilities", "getHardcoreStatus", "()Lclub/sk1er/hytilities/handlers/game/hardcore/HardcoreStatus;", false));
        list.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "club/sk1er/hytilities/handlers/game/hardcore/HardcoreStatus", "shouldChangeStyle", "()Z", false));
        LabelNode ifeq = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.IFEQ, ifeq));
        list.add(ifne);
        list.add(new InsnNode(Opcodes.ICONST_5));
        LabelNode gotoInsn = new LabelNode();
        list.add(new JumpInsnNode(Opcodes.GOTO, gotoInsn));
        list.add(ifeq);
        list.add(new InsnNode(Opcodes.ICONST_0));
        list.add(gotoInsn);
        return list;
    }
}

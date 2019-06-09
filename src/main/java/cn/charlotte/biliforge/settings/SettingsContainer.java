/*
 *  * Copyright © Wynntils - 2019.
 */

package cn.charlotte.biliforge.settings;

import cn.charlotte.biliforge.instance.containers.ModuleContainer;
import cn.charlotte.biliforge.settings.annotations.SettingsInfo;
import cn.charlotte.biliforge.settings.instances.SettingsHolder;
import cn.charlotte.biliforge.util.overlay.Overlay;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;

public class SettingsContainer {

    String displayPath;
    SettingsHolder holder;
    ArrayList<Field> fields = new ArrayList<>();
    ModuleContainer m;

    public SettingsContainer(ModuleContainer m, SettingsHolder holder) {
        this.holder = holder;
        this.m = m;
        this.displayPath = holder instanceof Overlay ? m.getInfo().displayName() + "/" + ((Overlay) holder).displayName : holder.getClass().getAnnotation(SettingsInfo.class).displayPath().replaceFirst("^Main", m.getInfo().displayName());

        for (Field f : holder.getClass().getDeclaredFields()) {
            if (!Modifier.isStatic(f.getModifiers())) {
                fields.add(f);
            }
        }
        if (holder instanceof Overlay) {
            for (Field f : holder.getClass().getSuperclass().getDeclaredFields()) {
                if (!Modifier.isStatic(f.getModifiers())) {
                    fields.add(f);
                }
            }
        }

        try {
            tryToLoad();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void tryToLoad() throws Exception {
        updateValues(SettingsManager.getSettings(m, holder, this));
    }

    public HashMap<Field, Object> getValues() throws Exception {
        HashMap<Field, Object> values = new HashMap<>();

        for (Field f : fields) {
            values.put(f, f.get(holder));
        }

        return values;
    }

    public void saveSettings() throws Exception {
        SettingsManager.saveSettings(m, holder);
    }

    public void setValue(Field f, Object value) throws Exception {
        setValue(f, value, true);
    }

    public void setValue(Field f, Object value, boolean save) throws Exception {
        f.set(holder, value);
        holder.onSettingChanged(f.getName());

        if (save) SettingsManager.saveSettings(m, holder);
    }

    public void updateValues(SettingsHolder newH) throws Exception {
        boolean save = false;

        ArrayList<String> fieldsName = new ArrayList<>();
        for (Field f2 : fields) {
            fieldsName.add(f2.getName());
        }

        for (Field f : newH.getClass().getDeclaredFields()) {
            if (!fieldsName.contains(f.getName())) {
                save = true;
                continue;
            }
            setValue(f, f.get(newH), false);
        }
        if (holder instanceof Overlay) {
            for (Field f : newH.getClass().getSuperclass().getDeclaredFields()) {
                if (!fieldsName.contains(f.getName())) {
                    save = true;
                    continue;
                }
                setValue(f, f.get(newH), false);
            }
        }

        if (save) {
            saveSettings();
        }
    }

    public void onCreateConfig() throws Exception {
        ArrayList<String> fieldsName = new ArrayList<>();
        for (Field f2 : fields) {
            fieldsName.add(f2.getName());
        }
    }

    public void resetValue(Field field) throws Exception {
        field.set(holder, field.get(holder.getClass().getConstructor().newInstance()));
    }

    public void resetValues() throws Exception {
        for (Field field : fields)
            field.set(holder, field.get(holder.getClass().getConstructor().newInstance()));
    }

    public String getDisplayPath() {
        return this.displayPath;
    }

    public SettingsHolder getHolder() {
        return holder;
    }

}

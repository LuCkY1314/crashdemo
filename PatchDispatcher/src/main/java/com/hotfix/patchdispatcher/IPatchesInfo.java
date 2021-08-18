package com.hotfix.patchdispatcher;

import java.util.List;

/**
 * Created by chan on 2017/12/28.
 * Interface to get the list of patches.
 */

public interface IPatchesInfo<T extends PatchClassInfo> {
    List<T> getPatches();
}
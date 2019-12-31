#!/bin/env python
#coding=utf-8

# 此脚本用于redis集群导入时，持续同步持久化文件（aof，rdb）

import sys
import time
import os
import stat
import pwd
import grp

def printUseage():
    print "useage:"
    print "python sync_persistence_file.python srcFile1 targetFile1 ... srcFileN targetFileN"
    print "number of arg must by even"

uid = pwd.getpwnam("redis").pw_uid
gid = grp.getgrnam("redis").gr_gid

# the class of sync
class SyncExecutor:
    # 原始路径
    src = ""
    # 目标文件路径
    target = ""
    # 是否处理完
    done = False

    def __init__(self, src, target):
        self.src = src
        self.target = target

    def sync(self):
        print "start to sync ", self.src, " to ", self.target
        # check src path is exist
        if os.path.exists(self.src) == False:
            print "src file not exists ", self.src
            self.done = True
            return

        needChmod = True

        # check target file is newer than src
        if os.path.exists(self.target) == True:
            targetModifyTime = os.path.getmtime(self.target)
            srcModifyTime = os.path.getmtime(self.src)

            # if target path is not a link
            # if target file's create time is older one 3 minutes than src file, stop sync
            if os.path.islink(self.target) == False:
                print "target path is not a link, sync stop.", self.src
                self.done = True
                return
            elif targetModifyTime - srcModifyTime > 60 * 3:
                print "target file is order than src file, sync stop.", self.src
                self.done = True
                return
        else:
            # make soft link
            os.symlink(self.src, self.target)
            os.lchown(self.target, uid, gid)

        srcAccess = oct(os.stat(self.src).st_mode)[-3:]
        if srcAccess != '757':
            # change src file and parent dir's mod to 757
            parent = self.src
            while parent != "/":
                # change mod and owner to 757
                os.chmod(parent, stat.S_IXGRP | stat.S_IRGRP | stat.S_IXOTH | stat.S_IROTH | stat.S_IWOTH | stat.S_IWUSR | stat.S_IRUSR | stat.S_IXUSR)

                parent = os.path.dirname(parent)

        print "sync done"


# check args must be even
if len(sys.argv) % 2 != 1 or len(sys.argv) == 1:
    printUseage()
    sys.exit(1)

syncExecutors = []

# handle args
for i in range(1, len(sys.argv), 2):
    syncExecutor = SyncExecutor(sys.argv[i], sys.argv[i + 1])
    syncExecutors.append(syncExecutor)

# continue do sync until list is empty
while len(syncExecutors) > 0:
    i = 0
    while i < len(syncExecutors):
        syncExecutor = syncExecutors[i]
        syncExecutor.sync()

        # finsh job when job done
        if syncExecutor.done == True:
            syncExecutors.pop(i)
            i = i - 1

        i += 1

    time.sleep(30)
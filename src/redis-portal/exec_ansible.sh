#!/bin/bash
# init redis server by call ansible playbook

#if [ $(whoami) != "redis" ] ;then
#  echo "who can run this script must be the user of redis"
#  exit 1
#fi

if [ -z $1 ] || [ -z $2 ] || [ -z $3 ] ;then
  echo "Usage: exec_ansible.sh [playbook name] [ip1] [password1] ... [ipN] [passwordN]"
  echo "playbook name:"
  echo "total_init                               Init all module on servers. Include linux user's authorized key,"
  echo "                                         full version of redis software and redis monitor module."
  echo "monitor_init                             Init monitor module on servers"
  exit 1
fi

# playbook name
playbook=$1
shift 1

# loop server,generate temp ansible inventory file
tmpfile=`uuidgen`.cfg;
echo "[redisservers]">$tmpfile
while [ $# -gt 0 ] ;do
  ip=$1
  pass=$2
  shift 2
  echo "$ip ansible_ssh_user=root ansible_ssh_pass=$pass" >>$tmpfile
done

# exec ansible-playbook
shell_path=$0
local_base_path=$(cd "$(dirname "$shell_path")" >/dev/null; pwd)
/usr/bin/ansible-playbook $local_base_path/../ansible/$playbook.yaml -i $tmpfile;

# remove temp inventory file
rm -f $tmpfile

#!/bin/bash

#  Setting up irq affinity according to /proc/interrupts
#  Assuming a device with two RX and TX queues.
#  This script will assign:
#
#	eth0-tx-0  CPU0
#	eth0-rx-1  CPU1
#	eth0-rx-2  CPU2
#	eth0-rx-3  CPU3
#

set_affinity()
{
    if [ $VEC -ge 32 ]
    then
        MASK_FILL=""
        MASK_ZERO="00000000"
        let "IDX = $VEC / 32"
        for ((i=1; i<=$IDX;i++))
        do
            MASK_FILL="${MASK_FILL},${MASK_ZERO}"
        done

        let "VEC -= 32 * $IDX"
	let "CPU_INDEX = ${VEC} % ${#CPUS[@]}"
        MASK_TMP=$((1<<CPUS[CPU_INDEX]))
        MASK=`printf "%X%s" $MASK_TMP $MASK_FILL`
    else
	let "CPU_INDEX = ${VEC} % ${#CPUS[@]}"
        MASK_TMP=$((1<<CPUS[CPU_INDEX]))
        MASK=`printf "%X" $MASK_TMP`
    fi

    printf "%s mask=%s for /proc/irq/%d/smp_affinity\n" $DEV $MASK $IRQ
    printf "%s" $MASK > /proc/irq/$IRQ/smp_affinity
}

if [ "$1" = "" ] ; then
    echo "Description:"
    echo "    This script attempts to bind each queue of a multi-queue NIC"
    echo "    to the same numbered core, ie tx0|rx0 --> cpu0, tx1|rx1 --> cpu1"
    echo "usage:"
    echo "    $0 eth0 [eth1 eth2 eth3]"
    exit 1
fi

# check for irqbalance running
IRQBALANCE_ON=`ps ax | grep -v grep | grep -q irqbalance; echo $?`
if [ "$IRQBALANCE_ON" == "0" ] ; then
    echo " WARNING: irqbalance is running and will"
    echo "          likely override this script's affinitization."
    echo "          Please stop the irqbalance service and/or execute"
    echo "          'killall irqbalance'"
fi

#
# Set up numa node
#
NUMA_NODE0_CPUS=`lscpu | grep 'NUMA node0' | cut -d ':' -f 2`
if [[ "$NUMA_NODE0_CPUS" =~ "-" ]] ; then
    CPU_SIDE=(${NUMA_NODE0_CPUS//-/ })
    for index in $(seq ${CPU_SIDE[0]} ${CPU_SIDE[1]})
    do
        CPUS[index]=$index
    done
elif [[ "$NUMA_NODE0_CPUS" =~ "," ]] ; then
    CPUS=(${NUMA_NODE0_CPUS//,/ })
else
    echo " WARNING; there is only 1 cpu, skip nic irq affinity."
    exit 0
fi
echo "NUMA node0 CPU(s): ${CPUS[*]}"

#
# Set up the desired devices.
#
for DEV in $*
do
  for DIR in tx rx TxRx fp "[0-9]"
  do
     MAX=`egrep -i $DEV-$DIR /proc/interrupts | wc -l`
     if [ "$MAX" == "0" ] ; then
       MAX=`egrep -i "$DEV:.*$DIR" /proc/interrupts | wc -l`
     fi
     if [ "$MAX" == "0" ] ; then
       echo no $DIR vectors found on $DEV
       continue
     fi
     for VEC in `seq 0 1 $MAX`
     do
        IRQ=`cat /proc/interrupts | grep -i $DEV-$DIR-$VEC"$"  | cut  -d:  -f1 | sed "s/ //g"`

        if [ -z "$IRQ" ]; then
          IRQ=`cat /proc/interrupts | egrep -i $DEV:v$VEC-$DIR"$"  | cut  -d:  -f1 | sed "s/ //g"`
        fi

        if [ -z "$IRQ" ]; then
          IRQ=`cat /proc/interrupts | egrep -i $DEV-$VEC"$"  | cut  -d:  -f1 | sed "s/ //g"`
        fi

        if [ -n  "$IRQ" ]; then
          set_affinity
        fi
     done
  done
done

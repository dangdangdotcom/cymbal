#ssh-keygen -t rsa
# Set connection timeout to 3 seconds
alias ssh='ssh -o ConnectTimeout=3'
alias scp='scp -o ConnectTimeout=3'

scp ~/.ssh/id_rsa.pub redis@10.255.209.181:/home/redis/.ssh/authorized_keys
scp ~/.ssh/id_rsa.pub redis@10.255.209.182:/home/redis/.ssh/authorized_keys
scp ~/.ssh/id_rsa.pub redis@10.255.209.183:/home/redis/.ssh/authorized_keys

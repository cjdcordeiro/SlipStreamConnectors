#!/usr/bin/env python
"""
 SlipStream Client
 =====
 Copyright (C) 2015 SixSq Sarl (sixsq.com)
 =====
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
"""

import warnings
warnings.filterwarnings("ignore", category=DeprecationWarning)

from slipstream.command.CloudClientCommand import main
from slipstream.command.DescribeInstancesCommand import DescribeInstancesCommand
from slipstream_kubernetes.KubernetesCommand import KubernetesCommand
from slipstream_kubernetes.KubernetesClientCluster import KubernetesClientCluster


class KubernetesDescribeInstances(DescribeInstancesCommand, KubernetesCommand):

    def _vm_get_state(self, cc, vm):
        return vm['status']['phase']

    def _vm_get_id(self, cc, vm):
        return vm['metadata']['uid']

    def _print_results(self, cc, vms):
        print "id, name, node name, image, state, ip, " \
                "port mappings [containerPort:hostPort], restart policy, " \
                "cpu request, ram request, instance-type, creation time, start time"
        for vm in vms:
            print ', '.join([
                self._vm_get_id(cc, vm),
                self._vm_get_name(cc, vm),
                self._vm_get_node_name(cc, vm),
                self._vm_get_image_name(cc, vm),
                self._vm_get_state(cc, vm) or 'Unknown',
                self._vm_get_ip(cc, vm),
                self._vm_get_port_mappings(cc, vm),
                self._vm_get_restart_policy(cc, vm),
                self._vm_get_cpu(cc, vm),
                self._vm_get_ram(cc, vm),
                self._vm_get_instance_type(cc, vm)]),
                self._vm_get_creation_time(cc, vm)]),
                self._vm_get_start_time(cc, vm)])

    def __init__(self):
        super(KubernetesDescribeInstances, self).__init__()


if __name__ == "__main__":
    main(KubernetesDescribeInstances)

"""
 SlipStream Client
 =====
 Copyright (C) 2014 SixSq Sarl (sixsq.com)
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

from slipstream.command.DescribeInstancesCommand import DescribeInstancesCommand
from slipstream_cloudstack.CloudStackCommand import CloudStackCommand


class CloudStackDescribeInstances(DescribeInstancesCommand, CloudStackCommand):

    STATE_MAP = {0: 'Running',
                 1: 'Rebooting',
                 2: 'Terminated',
                 3: 'Pending',
                 4: 'Unknown',
                 5: 'Stopped'}

    def _vm_get_state(self, cc, vm):
        return self.STATE_MAP[vm.state]

    def _vm_get_id(self, cc, vm):
        return vm.id

    def __init__(self):
        super(CloudStackDescribeInstances, self).__init__()
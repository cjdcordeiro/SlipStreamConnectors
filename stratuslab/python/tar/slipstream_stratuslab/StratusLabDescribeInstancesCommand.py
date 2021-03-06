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

from slipstream.command.DescribeInstancesCommand import DescribeInstancesCommand
from slipstream_stratuslab.StratusLabCommand import StratusLabCommand


# pylint: disable=abstract-method
class StratusLabDescribeInstances(DescribeInstancesCommand, StratusLabCommand):

    def __init__(self):
        super(StratusLabDescribeInstances, self).__init__()

    def _vm_get_state(self, _, vm):
        return vm.state_summary

    def _vm_get_id(self, _, vm):
        return vm.id

    def get_initialization_extra_kwargs(self):
        return {'run_instance': False}


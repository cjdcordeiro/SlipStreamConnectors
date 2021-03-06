#!/usr/bin/env python
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

from slipstream.command.RunInstancesCommand import RunInstancesCommand
from slipstream_stratuslab.StratusLabCommand import StratusLabCommand
from slipstream_stratuslab.StratusLabClientCloud import StratusLabClientCloud
from slipstream_stratuslab.StratusLabIterClientCloud import StratusLabIterClientCloud


# pylint: disable=abstract-method
class StratusLabRunInstances(RunInstancesCommand, StratusLabCommand):

    FLAVOUR_KEY = 'flavour'
    INSTANCE_TYPE_KEY = 'instance-type'
    CPU_KEY = 'cpu'
    RAM_KEY = 'ram'
    MARKETPLACE_ENDPOINT_KEY = 'markeptlace-endpoint'

    def get_connector_class(self):
        if self.get_option(self.FLAVOUR_KEY).lower() == 'stratuslabiter':
            return StratusLabIterClientCloud
        else:
            return StratusLabClientCloud

    def __init__(self):
        super(StratusLabRunInstances, self).__init__()

    def set_cloud_specific_options(self, parser):
        StratusLabCommand.set_cloud_specific_options(self, parser)

        self.parser.add_option('--' + self.FLAVOUR_KEY, dest=self.FLAVOUR_KEY,
                               help='Connector flavour.',
                               default='', metavar='FLAVOUR')

        self.parser.add_option('--' + self.INSTANCE_TYPE_KEY, dest=self.INSTANCE_TYPE_KEY,
                               help='Instance type (t-shirt).',
                               default='', metavar='INSTANCE_TYPE')

        self.parser.add_option('--' + self.CPU_KEY, dest=self.CPU_KEY,
                               help='Number of CPUs.',
                               default='', metavar='CPU')

        self.parser.add_option('--' + self.RAM_KEY, dest=self.RAM_KEY,
                               help='RAM in GB.',
                               default='', metavar='RAM')

        self.parser.add_option('--' + self.MARKETPLACE_ENDPOINT_KEY,
                               dest=self.MARKETPLACE_ENDPOINT_KEY,
                               help='Markeptlace endpoint',
                               default='', metavar='MARKETPLACE_ENDPOINT')

    def get_cloud_specific_node_inst_cloud_params(self):
        return {'instance.type': self.get_option(self.INSTANCE_TYPE_KEY),
                'cpu': self.get_option(self.CPU_KEY),
                'ram': self.get_option(self.RAM_KEY)}

    def get_cloud_specific_user_cloud_params(self):
        user_params = StratusLabCommand.get_cloud_specific_user_cloud_params(self)
        user_params['marketplace.endpoint'] = self.get_option(self.MARKETPLACE_ENDPOINT_KEY)
        return user_params

    def get_cloud_specific_mandatory_options(self):
        return StratusLabCommand.get_cloud_specific_mandatory_options(self) + \
            [self.MARKETPLACE_ENDPOINT_KEY]


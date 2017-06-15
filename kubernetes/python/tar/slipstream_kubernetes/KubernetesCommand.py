"""
 SlipStream Client
 =====
 Copyright (C) 2017 SixSq Sarl (sixsq.com)
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

from slipstream.command.CloudClientCommand import CloudClientCommand
from slipstream_kubernetes.KubernetesClientCluster import KubernetesClientCluster


class KubernetesCommand(CloudClientCommand):

    ENDPOINT_KEY = 'endpoint'

    def __init__(self):
        super(KubernetesCommand, self).__init__()

    def get_connector_class(self):
        return KubernetesClientCluster

    def set_cloud_specific_options(self, parser):
        parser.add_option('--' + self.ENDPOINT_KEY, dest=self.ENDPOINT_KEY, help='Kubernetes API endpoint (e.g. http://myk8s.com:8080/api/v1)',
                          default='', metavar='ENDPOINT')

    def get_cloud_specific_user_cloud_params(self):
        return {self.ENDPOINT_KEY: self.get_option(self.ENDPOINT_KEY)}

    def get_common_mandatory_options(self):
        return []

    def get_cloud_specific_mandatory_options(self):
        return [self.ENDPOINT_KEY]

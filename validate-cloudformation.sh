#!/bin/bash

CLOUDFORMATION_DIRECTORY="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )/cloudformation"

which aws 1>/dev/null 2>&1
if [[ $? -gt 0 ]]; then
	echo "AWS CLI not installed. Cannot validate Cloudformation Templates."
	exit 1
fi

if [[ -d "${CLOUDFORMATION_DIRECTORY}" ]]; then
	for TEMPLATE in $(find ${CLOUDFORMATION_DIRECTORY} -iname "*.json"); do
		echo "Validating CloudFormation template ${TEMPLATE}"
		aws cloudformation validate-template --template-body "file://${TEMPLATE}" 1>/dev/null
		if [[ $? -gt 0 ]]; then
			exit 1
		fi
	done
fi
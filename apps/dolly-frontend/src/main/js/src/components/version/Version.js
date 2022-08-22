import React from 'react'

import './Version.less'

export default function Version() {
	const versionStr = `v${BUILD.VERSION}`
	const branch = `Branch: ${BUILD.BRANCH} #${BUILD.COMMITHASH}`
	return (
		<div className="build-version">
			<span title={branch}>{versionStr}</span>
		</div>
	)
}

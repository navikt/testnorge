import React from 'react'

import './Version.less'

export default function Version() {
	const versionStr = `v${BUILD.VERSION} #${BUILD.COMMITHASH}`
	const branch = `Branch: ${BUILD.BRANCH}`
	return (
		<div className="build-version">
			<span title={branch}>{versionStr}</span>
		</div>
	)
}

import React, { PureComponent } from 'react'

import './Version.less'

class Version extends PureComponent {
	render() {
		const versionStr = `v${BUILD.VERSION} #${BUILD.COMMITHASH}`
		const branch = `Branch: ${BUILD.BRANCH}`
		return (
			<div className="build-version">
				<span title={branch}>{versionStr}</span>
			</div>
		)
	}
}

export default Version

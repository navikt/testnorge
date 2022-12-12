import './Version.less'

export default function Version() {
	const versionStr = `v${process.env.APP_VERSION}`
	const branch = `Branch: ${process.env.GIT_BRANCH} #${process.env.COMMIT_HASH}`
	return (
		<div className="build-version">
			<span title={branch}>{versionStr}</span>
		</div>
	)
}

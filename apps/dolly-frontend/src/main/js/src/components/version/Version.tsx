import './Version.less'

export default function Version() {
	const versionStr = `v${import.meta.env.APP_VERSION}`
	const branch = `Branch: ${import.meta.env.GIT_BRANCH} #${import.meta.env.COMMIT_HASH}`
	return (
		<div className="build-version">
			<span title={branch}>{versionStr}</span>
		</div>
	)
}

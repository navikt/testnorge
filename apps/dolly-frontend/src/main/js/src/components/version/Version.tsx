import './Version.less'

export default function Version() {
	const versionStr = `v${1}`
	const branch = `Branch: ${2} #${3}`
	return (
		<div className="build-version">
			<span title={branch}>{versionStr}</span>
		</div>
	)
}
